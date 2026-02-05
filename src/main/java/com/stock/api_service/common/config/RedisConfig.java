package com.stock.api_service.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    /**
     * Spring Boot가 자동으로 구성해주는 전역 ObjectMapper를 주입받아
     * 일관성 있는 JSON 직렬화 설정을 적용한 RedisTemplate을 생성합니다.
     * @param connectionFactory Redis 연결을 관리하는 팩토리
     * @param objectMapper    Spring Boot가 자동 구성한 ObjectMapper (JavaTimeModule 포함)
     * @return JSON 직렬화가 적용된 RedisTemplate
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(
            RedisConnectionFactory connectionFactory,
            ObjectMapper objectMapper // Spring의 표준 ObjectMapper를 주입받습니다.
    ) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Key는 항상 문자열을 사용합니다.
        template.setKeySerializer(new StringRedisSerializer());

        // Value는 Spring Boot의 표준 ObjectMapper를 사용하는 JSON 직렬화기를 사용합니다.
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));

        return template;
    }
}
