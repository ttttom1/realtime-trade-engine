package com.stock.api_service.controller;

import com.stock.api_service.domain.Order;
import com.stock.api_service.dto.OrderRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.BlockingQueue;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor  //QueueConfig에서 만든 orderQueue 주입
public class OrderController {

    private final BlockingQueue<Order> orderQueue;

    @PostMapping
    public ResponseEntity<String> createOrder(@RequestBody OrderRequest request) {
        //1. DTO를 Entity(Order)로 반환
        Order order = Order.builder()
                .memberId(request.getMemberId())
                .stockCode(request.getStockCode())
                .type(request.getType())
                .price(request.getPrice())
                .quantity(request.getQuantity())
                .build();

        // 2. 큐에 주문 넣기 (offer는 큐가 꽉 찼을 때 바로 false를 바놘하여 시스템을 보호함)
        boolean isAccepted = orderQueue.offer(order);

        if (isAccepted) {
            return ResponseEntity.ok("주문이 접수 되었습니다. (ID: " + order.getMemberId() + ")");
        } else {
            //큐가 가득 찼을 때 (트래픽 폭주)
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("현재 주문량이 많아 접수가 불가능합니다. 잠시 후 다시 시도해주세요.");
        }
    }
}
