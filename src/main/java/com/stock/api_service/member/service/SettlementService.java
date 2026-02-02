package com.stock.api_service.member.service;

import com.stock.api_service.matching.entity.Trade;
import com.stock.api_service.member.entity.Member;
import com.stock.api_service.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class SettlementService {
    private final MemberRepository memberRepository;

    @Transactional // 둘 중 하나라도 실패하면 롤백
    public void processSettlement(Trade trade) {
        //1. 매수자(Buy) 찾아서 돈 깎기
        Member buyer = memberRepository.findById(trade.getBuyMemberId())
                .orElseThrow(() -> new RuntimeException("매수자를 찾을 수 없습니다.: " + trade.getBuyMemberId()));

        BigDecimal totalAmount = trade.getPrice().multiply(BigDecimal.valueOf(trade.getQuantity()));
        Integer stockTotalAmount = trade.getQuantity();

        buyer.subtractBalance(totalAmount); // 주식 산 만큼 돈 깎기
        buyer.addStock(stockTotalAmount);

        // 2. 매도자(Sell) 찾아서 돈 넣어주기
        Member seller = memberRepository.findById(trade.getSellMemberId())
                .orElseThrow(() -> new RuntimeException("매도자를 찾을 수 없습니다: " + trade.getSellMemberId()));
        seller.addBalance(totalAmount); // 주식 판 만큼 돈 벌기
        seller.subtractStock(stockTotalAmount);
    }
}
