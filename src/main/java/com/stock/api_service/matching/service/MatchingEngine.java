package com.stock.api_service.matching.service;

import com.stock.api_service.order.entity.Order;
import com.stock.api_service.matching.entity.Trade;
import com.stock.api_service.matching.event.TradesCompletedEvent;
import com.stock.api_service.order.service.OrderBook;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class MatchingEngine {
    // 1. [의존성 변경] BlockingQueue 대신 RedisTemplate과 ObjectMapper를 주입 받음.
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final OrderBook orderBook;
    private final ApplicationEventPublisher eventPublisher;

    private static final String ORDER_STREAM_KEY = "order-stream";
    private static final String CONSUMER_GROUP_NAME = "matching-group";
    private static final String CONSUMER_NAME = "engine-1";

    @PostConstruct
    public void startEngine() {
        // 2. [소비자 그룹 생성] 애플리케이션 시작 시, Redis Stream에 소비자 그룹을 생성합니다.
        //이미 그룹이 존재하면 에러가 발생하므로, try-catch로 감싸줍니다.
        try {
            redisTemplate.opsForStream().createGroup(ORDER_STREAM_KEY, CONSUMER_GROUP_NAME);
        } catch (Exception e) {
            log.info("Consumer group '{}' already exists.", CONSUMER_GROUP_NAME);
        }

        //엔진을 별도의 스레드에서 실행 ( 메인 스레드가 멈추지 않게)
        Thread engineThread = new Thread(() -> {
            log.info("🚀 매칭 엔진이 가동되었습니다. (Redis stream 구독 시작");
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    // 3. [블로킹 읽기] Redis Stream에서 새로운 메시지가 들어올 때까지 대기하며 읽습니다.
                    List<MapRecord<String, Object,Object>> messages = redisTemplate.opsForStream().read(
                            Consumer.from(CONSUMER_GROUP_NAME, CONSUMER_NAME),
                            StreamReadOptions.empty().count(1).block(Duration.ofSeconds(10)),   //1개씩, 10초 블로킹
                            StreamOffset.create(ORDER_STREAM_KEY, ReadOffset.lastConsumed())
                    );

                    if (messages == null || messages.isEmpty()) {
                        continue;   //메시지가 없으면 다시 대기
                    }
                    for(MapRecord<String, Object, Object> message : messages){
                        // 4. [역직렬화] Redis에서 받은 Map 데이터를 Order 객체로 변환합니다.
                        Object data = message.getValue().get("data");
                        Order order = objectMapper.convertValue(data, Order.class);

                        log.info("🚚Redis Stream으로부터 주문 수신: MemberID {}", order.getMemberId());
                        processOrder(order);

                        // 5. [메시지 처리 완료 확인] 처리가 끝난 메시지는 Acknowledge를 보내줘야 합니다.
                        // 이걸 해줘야 다른 컨슈머가 이 메시지를 중복을 가져가지 않습니다.
                        redisTemplate.opsForStream().acknowledge(ORDER_STREAM_KEY, CONSUMER_GROUP_NAME,message.getId());
                    }
                } catch (Exception e)  {
                    log.error("엔진 가동 중 에러 발생: {}", e.getMessage(), e);
                    // 에러 발생 시 잠시 대기 후 재시도
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        });
        engineThread.setName("MatchingEngineThread");
        engineThread.start();
    }

    private void processOrder(Order order){
        //수량 0 인 주문 무시
        if (order.getQuantity() <= 0) return;

        // 1. 매칭 실행 및 체결 리스트 확보
        List<Trade> trades = orderBook.process(order);

        // 2. 체결 내역이 있다면 '서비스 직접 호출' 대신 '이벤트 발행'
        if (!trades.isEmpty()) {
            eventPublisher.publishEvent(new TradesCompletedEvent(trades));
        }
    }
}
