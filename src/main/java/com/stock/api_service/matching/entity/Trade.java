package com.stock.api_service.matching.entity;

import com.stock.api_service.order.entity.Order;
import com.stock.api_service.order.entity.OrderType;
import io.lettuce.core.dynamic.annotation.CommandNaming;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "trades")
public class Trade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String stockCode;
    private BigDecimal price;
    private Integer quantity;

    private String buyMemberId;
    private String sellMemberId;

    private OrderType takerType;    // The Taker Rule

    private LocalDateTime createdAt;

    public static Trade of(Order takerOrder, Order makerOrder, int quantity) {
        String buyer, seller;
        // Taker(공격 주문)가 매수 주문이면, Taker가 구매자, Maker가 판매자
        if (takerOrder.getType() == OrderType.BUY) {
            buyer = takerOrder.getMemberId();
            seller = makerOrder.getMemberId();
        } else {
            buyer = makerOrder.getMemberId();
            seller = takerOrder.getMemberId();
        }
        return Trade.builder()
                .stockCode(takerOrder.getStockCode())
                .price(makerOrder.getPrice())
                .quantity(quantity)
                .buyMemberId(buyer)
                .sellMemberId(seller)
                .takerType(takerOrder.getType())    //공격자의 타입 기록
                .createdAt(LocalDateTime.now())     //체결 시점으로 체결 거래 정보 업뎃 (기존 디비 저장될때였음)
                .build();
    }
}
