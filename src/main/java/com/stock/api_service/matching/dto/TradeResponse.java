package com.stock.api_service.matching.dto;

import com.stock.api_service.matching.entity.Trade;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

/**
 * 실시간 시세 전파를 위한 가벼운 객체 (WebSocket/STOMP용)
 */
public record TradeResponse(
        String stockCode,   // 종목 코드
        BigDecimal price,   // 체결 가격
        Integer quantity,   // 체결 수량
        String takerType,   // 매수/매도 구분 (BUY/SELL)
        String tradeAt      // 체결 시간 (HH:mm:ss)
) {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static TradeResponse from(Trade trade) {
        return new TradeResponse(
                trade.getStockCode(),
                trade.getPrice(),
                trade.getQuantity(),
                trade.getTakerType().name(),
                trade.getCreatedAt().format(TIME_FORMATTER)
        );
    }
}
