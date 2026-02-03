package com.stock.api_service.matching.event;

import com.stock.api_service.matching.entity.Trade;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * 단일 주문 처리에 의해 발생한 모든 체결(Trade) 목록을 담는 이벤트입니다.
 * MatchingEngine이 발행하고, TradeService 등이 구독하여 처리합니다.
 */
@Getter
@RequiredArgsConstructor
public class TradesCompletedEvent {
    private final List<Trade> trades;
}
