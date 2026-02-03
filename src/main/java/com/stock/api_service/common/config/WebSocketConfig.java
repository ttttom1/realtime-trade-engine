package com.stock.api_service.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker //STOMP 사용하는 broker 활성화
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 1. 인메모리 Simple Broker를 활성화하고, '/topic' 접두사를 가진 목적지로 메시지를 라우팅 합니다.
        // 클라이언트는 '/topic/**' 주소를 구독하여 실시간 데이터를 수신하게 됩니다.
        registry.enableSimpleBroker("/topic");

        // 2. 클라이언트가 서버로 메시지를 보낼 때 사용할 주소의 접두사를 설정합니다. (에시 : /app/send-order)
        //    지금 당장은 사용하지 않지만, 향후 클라이언트 -> 서버 통신을 위해 설정함
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 3. 클라이언트가 WebSocket 연결을 시작할 STOMP 엔드 포인트를 등록합니다.
        //    예 : JavaScript에서 'new SockJS('/ws') 와 같이 접속합니다.
        //    'withStockJS()' 는 WebSocket을 지원하지 않는 브라우저를 위한 풀백 옵션입니다.
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();
    }
}
