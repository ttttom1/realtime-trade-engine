package com.stock.api_service.member.service;

import com.stock.api_service.matching.entity.Trade;
import com.stock.api_service.member.entity.Member;
import com.stock.api_service.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SettlementService {
    private final MemberRepository memberRepository;

    @Transactional // 둘 중 하나라도 실패하면 롤백
    public void processSettlement(List<Trade> trades) {
        if (trades == null || trades.isEmpty()) {
            return;
        }

        // 최적화 : 모든 거래에서 관련된 모든 memberId를 중복 없이 추출합니다.
        Set<String> memberIds = trades.stream()
                .flatMap(trade -> Stream.of(trade.getBuyMemberId(), trade.getSellMemberId()))
                .collect(Collectors.toSet());

        // 2. [최적화] memberId로 필요한 Member 한 번에 쿼리로 조회
        Map<String, Member> membersMap = memberRepository.findAllById(memberIds).stream()
                .collect(Collectors.toMap(Member::getId, member -> member));
        //3. 각 거래에 대해 정산을 수행합니다.
        for (Trade trade : trades) {
            Member buyer = membersMap.get(trade.getBuyMemberId());
            Member seller = membersMap.get(trade.getSellMemberId());

            if (buyer == null || seller == null) {
                throw new RuntimeException("정산 중 매수자 또는 매도자 정보를 찾을 수 없습니다.");
            }

            BigDecimal totalAmount = trade.getPrice().multiply(BigDecimal.valueOf(trade.getQuantity()));
            Integer stockTotalAmount = trade.getQuantity();

            buyer.subtractBalance(totalAmount); // 주식 산 만큼 돈 깎기
            buyer.addStock(stockTotalAmount);

            seller.addBalance(totalAmount); // 주식 판 만큼 돈 벌기
            seller.subtractStock(stockTotalAmount);
        }
    }
}
