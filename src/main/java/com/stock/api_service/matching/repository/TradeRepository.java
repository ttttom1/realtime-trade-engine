package com.stock.api_service.matching.repository;

import com.stock.api_service.matching.entity.Trade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradeRepository extends JpaRepository<Trade, Long> {

}
