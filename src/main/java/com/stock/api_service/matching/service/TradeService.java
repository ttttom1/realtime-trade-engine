package com.stock.api_service.matching.service;

import com.stock.api_service.matching.entity.Trade;
import com.stock.api_service.matching.repository.TradeRepository;
import com.stock.api_service.member.service.SettlementService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TradeService {
    private final TradeRepository tradeRepository;
    private final SettlementService settlementService;

    @Transactional      //OSIV문제로 트랜젝션 하나로 따로 분리.
    public void saveAndSettle(List<Trade> trades) {
        tradeRepository.saveAll(trades);
        for (Trade trade: trades) {
            settlementService.processSettlement(trade);


        }
    }
}
