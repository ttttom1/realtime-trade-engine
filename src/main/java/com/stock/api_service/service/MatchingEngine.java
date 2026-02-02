package com.stock.api_service.service;

import com.stock.api_service.domain.Order;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.BlockingQueue;

@Service
@Slf4j
@RequiredArgsConstructor
public class MatchingEngine {

    private final BlockingQueue<Order> orderQueue;
    private final OrderBook orderBook;

    @PostConstruct
    public void startEngine() {
        //엔진을 별도의 스레드에서 실행 ( 메인 스레드가 멈추지 않게)
        Thread engineThread = new Thread(() -> {
            log.info("🚀 매칭 엔진이 가동되었습니다.");
            while (true) {
                try {
                    //1. 큐에서 주문이 들어올 때까지 대기하면 하나를 꺼냄
                    Order order = orderQueue.take();

                    // 2. 체결 로직 수행 (지금은 로그로 대체)
                    processOrder(order);
                } catch (InterruptedException e)  {
                    log.error("엔진 가동 중 에러 발생: {}", e.getMessage());
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        engineThread.setName("MatchingEngineThread");
        engineThread.start();
    }
    private void processOrder(Order order){
        log.info("🔔 [체결 시도] 사용자: {}, 종목: {}, 가격: {}. 수량: {}",
                order.getMemberId(), order.getStockCode(), order.getPrice(), order.getQuantity());

        //로그 대신 실질적인 매칭 프로세스 시작
        orderBook.process(order);
    }
}
