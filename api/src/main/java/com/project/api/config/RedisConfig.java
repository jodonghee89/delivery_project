package com.project.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 설정
 * 
 * Refresh Token 저장을 위한 Redis 연결 및 템플릿 설정
 */
@Configuration
public class RedisConfig {

    /**
     * RedisTemplate 설정
     * 
     * String 타입의 Key-Value 저장을 위한 템플릿 설정
     */
    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        
        // Redis 연결 팩토리 설정
        template.setConnectionFactory(connectionFactory);
        
        // Key 직렬화: String 형태로 저장
        template.setKeySerializer(new StringRedisSerializer());
        
        // Value 직렬화: String 형태로 저장  
        template.setValueSerializer(new StringRedisSerializer());
        
        // Hash Key 직렬화
        template.setHashKeySerializer(new StringRedisSerializer());
        
        // Hash Value 직렬화
        template.setHashValueSerializer(new StringRedisSerializer());
        
        // 기본 직렬화 방식 설정
        template.setDefaultSerializer(new StringRedisSerializer());
        
        return template;
    }
} 