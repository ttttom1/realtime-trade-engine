package com.stock.api_service.common.config;

import com.stock.api_service.order.entity.Order;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Configuration
public class QueueConfig {

    /**
     * 주문을 담는 공유 버퍼입니다.
     * LinkedBlockingQueue는 Thread-safe하며,
     * 대용량 트래픽 시 메모리 고갈을 방지하기 위해 최대 크기(Capacity)를 지정합니다.
     */
    @Bean
    public BlockingQueue<Order> orderQueue() {
        // 최대 10,000개의 주문을 대기시킬 수 있는 큐 생성
        return new LinkedBlockingQueue<>(10000); //여러 사용자가 동시에 호출해도 안전하게
    }
}