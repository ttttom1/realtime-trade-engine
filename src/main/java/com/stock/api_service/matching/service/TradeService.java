package com.stock.api_service.matching.service;

import com.stock.api_service.matching.entity.Trade;
import com.stock.api_service.matching.event.TradesCompletedEvent;
import com.stock.api_service.matching.repository.TradeRepository;
import com.stock.api_service.member.service.SettlementService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TradeService {
    private final TradeRepository tradeRepository;
    private final SettlementService settlementService;

    @Transactional
    @EventListener
    public void onTradesCompleted(TradesCompletedEvent event) {
        List<Trade> trades = event.getTrades();
        // 1. saveAll의 반환 값을 받아, DB에 저장된 최종 상태의 엔티티 리스트를 확보함.
        List<Trade> savedTrades = tradeRepository.saveAll(trades);

        settlementService.processSettlement(savedTrades);
    }
}
