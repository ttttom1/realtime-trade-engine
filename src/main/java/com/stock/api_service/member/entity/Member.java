package com.stock.api_service.member.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "members")
public class Member {
    @Id
    private String id;

    private String name;

    @Column(precision = 19, scale =2)
    private BigDecimal balance;

    //현재는 삼성전자 주식 수만 관리
    private Integer samsungStockQuantity;


    // 잔고 증가 (매도 성공시)
    public void addBalance(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }

    public void subtractBalance(BigDecimal amount) {
        if (this.balance.compareTo(amount) < 0) {
            throw new RuntimeException("잔액이 부족합니다!");
        }
        this.balance = this.balance.subtract(amount);
    }

    //주식
    public void addStock(Integer theNumberOfStocks) {
        this.samsungStockQuantity = this.samsungStockQuantity + theNumberOfStocks;
    }

    public void subtractStock(Integer theNumberOfStocks) {
        if (this.samsungStockQuantity.compareTo(theNumberOfStocks) < 0) {
            throw new RuntimeException("보유하신 주식 수량이 부족합니다.");
        }
        this.samsungStockQuantity = this.samsungStockQuantity - theNumberOfStocks;
    }
}
