package com.stock.api_service.service;

import com.stock.api_service.domain.Order;
import com.stock.api_service.domain.OrderType;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Component
public class OrderBook {
    //매수 호가: 가격(key) 기준 내림차순 (가장 높은 가격이 먼저)
    private final TreeMap<BigDecimal, List<Order>> buyOrders = new TreeMap<>(Collections.reverseOrder());
    //매도 호가: 가격(Key) 기준 오름차순 (가장 낮은 가격이 먼저)
    private final TreeMap<BigDecimal, List<Order>> sellOrders = new TreeMap<>();

    //주문 추가 및 매칭 로직이 여기 들어갈 예정입니다.
    public void process(Order newOrder) {
        if(newOrder.getType() == OrderType.BUY) {
            matchOrder(newOrder,sellOrders, buyOrders);
        } else {
            matchOrder(newOrder, buyOrders, sellOrders);
        }
    }

    private void matchOrder(Order newOrder, TreeMap<BigDecimal, List<Order>> oppositeSide, TreeMap<BigDecimal, List<Order>> sameSide) {
        log.info("🔍 매칭 시작: {} 주문 {}원", newOrder.getType(), newOrder.getPrice());

        while (newOrder.getQuantity() > 0 && !oppositeSide.isEmpty()) {
            BigDecimal bestOppositePrice = oppositeSide.firstKey();

            //매수라면: 내가 사려는 가격 >= 남이 파는 가격 일때 체결
            //매도라면: 내가 파려는 가격 <= 남이 사는 가격 일때 체결
            boolean canMatch = (newOrder.getType() == OrderType.BUY)
                ? newOrder.getPrice().compareTo(bestOppositePrice) >= 0
                : newOrder.getPrice().compareTo(bestOppositePrice) <= 0;

            if(!canMatch) break;

            List<Order> matchingPriceOrders = oppositeSide.get(bestOppositePrice);
            Iterator<Order> iterator = matchingPriceOrders.iterator();

            while (iterator.hasNext() && newOrder.getQuantity() > 0) {
                Order targetOrder = iterator.next();
                int tradeQuantity = Math.min(newOrder.getQuantity(),targetOrder.getQuantity());

                //체결 실행
                newOrder.reduceQuantity(tradeQuantity);
                targetOrder.reduceQuantity(tradeQuantity);

                log.info("✅ [체결 완료] 가격: {}, 수량: {}, 매수자: {}, 매도자: {}",
                        bestOppositePrice, tradeQuantity,
                        (newOrder.getType() == OrderType.BUY ? newOrder.getMemberId() : targetOrder.getMemberId()),
                        (newOrder.getType() == OrderType.SELL ? newOrder.getMemberId() : targetOrder.getMemberId()));

                if (targetOrder.getQuantity() == 0) {
                    iterator.remove();
                }
            }//해당 가격 솔드아웃이니 트리맵에서 빼줌
            if (matchingPriceOrders.isEmpty()) {
                oppositeSide.remove(bestOppositePrice);
            }
        }
        //체결되고 남은 수량이 있다면 호가창에 등록
        if (newOrder.getQuantity() > 0) {
            sameSide.computeIfAbsent(newOrder.getPrice(), k -> new ArrayList<>()).add(newOrder);
            log.info("📌[호가 등록] {}원 잔여 수량 {}주", newOrder.getPrice(), newOrder.getQuantity());
        }


    }
}
