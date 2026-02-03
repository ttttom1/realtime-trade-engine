package com.stock.api_service.matching.service;

import com.stock.api_service.matching.dto.TradeResponse;
import com.stock.api_service.matching.entity.Trade;
import com.stock.api_service.matching.event.TradesCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    // STOMP 메시지 브로커로 메시지를 보낼 때 사용하는 Spring의 템플릿
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * TradeCompletedEvent가 발생했을 때 호출되는 이벤트 리스너입니다.
     * 체결 데이터를 클라이언트에게 전송하기 위한 DTO로 변환하여 WebSocket 토픽으로 전송합니다.
     */
    @EventListener
    public void onTradesCompleted(TradesCompletedEvent event){
        log.info("🚚체결 완료 이벤트 수신. {} 건의 체결을 클라이언트에 전파합니다.", event.getTrades().size());

        for (Trade trade : event.getTrades()) {
            // 1. 엔티티(Trade)를 DTO(TradeResponse)로 변환함
            TradeResponse response = TradeResponse.from(trade);

            // 2. SimpMessagingTemplate을 사용하여 '/topic/trades' 토픽으로 메시지를 전송합니다.
            //    이 로직을 구독하고 있는 모든 클라이언트가 이 메시지를 수신하게 됩니다.
            messagingTemplate.convertAndSend("/topic/trades", response);

        }
    }
}
