package com.stock.api_service.order.controller;

import com.stock.api_service.order.service.OrderBook;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/debug")
@RequiredArgsConstructor
public class DebugController {
    private final OrderBook orderBook;

    @PostMapping("/clear-orderbook")
    public ResponseEntity<String> clearOrderBook() {
        orderBook.clear();
        return ResponseEntity.ok("OrderBOok이 초기화 되었습니다.");
    }
}
