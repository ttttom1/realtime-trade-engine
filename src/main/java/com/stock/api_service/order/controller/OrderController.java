package com.stock.api_service.order.controller;

import com.stock.api_service.order.entity.Order;
import com.stock.api_service.order.dto.OrderRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.BlockingQueue;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final BlockingQueue<Order> orderQueue;

    @PostMapping
    public ResponseEntity<String> createOrder(@RequestBody OrderRequest request) {
        Order order = Order.builder()
                .memberId(request.getMemberId())
                .stockCode(request.getStockCode())
                .type(request.getType())
                .price(request.getPrice())
                .quantity(request.getQuantity())
                .build();

        // [최종 디버깅 로그] Order 객체가 Queue에 들어가기 직전 상태 확인
        log.info(">>> [CONTROLLER-DEBUG] Order created and offered to queue. MemberID: {}", order.getMemberId());

        boolean isAccepted = orderQueue.offer(order);

        if (isAccepted) {
            return ResponseEntity.ok("주문이 접수 되었습니다. (ID: " + order.getMemberId() + ")");
        } else {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("현재 주문량이 많아 접수가 불가능합니다. 잠시 후 다시 시도해주세요.");
        }
    }
}

