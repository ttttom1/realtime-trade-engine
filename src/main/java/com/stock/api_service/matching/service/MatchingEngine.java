package com.stock.api_service.matching.service;

import com.stock.api_service.order.entity.Order;
import com.stock.api_service.matching.entity.Trade;
import com.stock.api_service.matching.repository.TradeRepository;
import com.stock.api_service.order.service.OrderBook;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.BlockingQueue;

@Service
@Slf4j
@RequiredArgsConstructor
public class MatchingEngine {

    private final TradeRepository tradeRepository;
    private final BlockingQueue<Order> orderQueue;
    private final OrderBook orderBook;
    private final TradeService tradeService;

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
        //수량 0 인 주문 무시
        if (order.getQuantity() <= 0) return;

        // 1. 매칭 실행 및 체결 리스트 확보
        List<Trade> trades = orderBook.process(order);

        // 2. 체결 내역이 있다면 일괄 저장 (Bulk Insert)
        if (!trades.isEmpty()) {
            tradeService.saveAndSettle(trades);
        }
    }
}
