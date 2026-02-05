package com.stock.api_service.order.controller;

import com.stock.api_service.order.entity.Order;
import com.stock.api_service.order.dto.OrderRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final RedisTemplate<String, Object> redisTemplate;
    private final static String ORDER_STREAM_KEY = "order-stream";

    @PostMapping
    public ResponseEntity<String> createOrder(@RequestBody OrderRequest request) {
        Order order = Order.builder()
                .memberId(request.getMemberId())
                .stockCode(request.getStockCode())
                .price(request.getPrice())
                .quantity(request.getQuantity())
                .type(request.getType()) // BUY or SELL
                .build();

        try{
            // Redis Stream에 'order-stream'이라는 Key로 Order 객체를 추가합니다.
            redisTemplate.opsForStream().add(ORDER_STREAM_KEY, Collections.singletonMap("data", order));

            log.info(">>> [CONTROLLER] Order published to Redis Stream. MemberID: {}", order.getMemberId());

            return ResponseEntity.ok("주문이 성공적을 접수되었습니다");
        } catch (Exception e) {
            log.error("Redis Stream에 주문 발행 실해", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("주문 처리에 실패했습니다. 잠시 후 다시 시도해주세요.");
        }
    }
}

