package com.stock.api_service.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "orders") // MySQL의 예약어 'order'와 충돌 방지
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String memberId;  // 주문자 ID
    private String stockCode; // 종목 코드 (예: AAPL, 005930)

    @Enumerated(EnumType.STRING)
    private OrderType type;   // BUY(매수), SELL(매도)

    private BigDecimal price; // 주문 가격 (BigDecimal 필수!)
    private Integer quantity; // 주문 수량

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // PENDING, COMPLETED, CANCELLED

    private LocalDateTime createdAt;

    @Builder
    public Order(String memberId, String stockCode, OrderType type, BigDecimal price, Integer quantity) {
        this.memberId = memberId;
        this.stockCode = stockCode;
        this.type = type;
        this.price = price;
        this.quantity = quantity;
        this.status = OrderStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public void reduceQuantity(int amount) {
        if (this.quantity < amount) {
            throw new IllegalArgumentException("체결 수량이 주문 수량보다 많을 수 없습니다.");
        }
        this.quantity -= amount;
    }
}