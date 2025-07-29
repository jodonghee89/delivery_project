package com.project.api.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 토큰 관련 설정
 */
@Component
@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
public class JwtProperties {
    
    /**
     * JWT 서명을 위한 비밀키
     */
    private String secret = "delivery-project-jwt-secret-key-for-development-only-change-in-production-2024";
    
    /**
     * Access Token 만료 시간 (밀리초)
     * 기본값: 1시간 (3600000ms)
     */
    private long accessTokenValidityInMilliseconds = 3600000L;
    
    /**
     * Refresh Token 만료 시간 (밀리초)  
     * 기본값: 7일 (604800000ms)
     */
    private long refreshTokenValidityInMilliseconds = 604800000L;
    
    /**
     * 토큰 타입
     */
    private String tokenType = "Bearer";
} 