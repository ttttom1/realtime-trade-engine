package com.stock.api_service.order.dto;

import com.stock.api_service.order.entity.OrderType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {
    private String memberId;
    private String stockCode;
    private OrderType type;     // BUY or SELL
    private BigDecimal price;
    private Integer quantity;
}