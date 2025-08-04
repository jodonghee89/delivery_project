package com.project.api.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

/**
 * Refresh Token 저장소
 * 
 * Redis를 사용하여 Refresh Token을 저장하고 관리합니다.
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenRepository {

    private final RedisTemplate<String, String> redisTemplate;
    
    /**
     * Refresh Token 저장 키 접두사
     */
    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";

    /**
     * Refresh Token 저장
     * 
     * @param customerId 고객 ID
     * @param refreshToken Refresh Token
     * @param expirationTimeInSeconds 만료 시간 (초)
     */
    public void saveRefreshToken(Long customerId, String refreshToken, long expirationTimeInSeconds) {
        String key = REFRESH_TOKEN_PREFIX + customerId;
        
        try {
            redisTemplate.opsForValue().set(key, refreshToken, expirationTimeInSeconds, TimeUnit.SECONDS);
            log.debug("Refresh Token 저장 완료 - customerId: {}, 만료시간: {}초", customerId, expirationTimeInSeconds);
        } catch (Exception e) {
            log.error("Refresh Token 저장 실패 - customerId: {}, error: {}", customerId, e.getMessage());
            throw new RuntimeException("Refresh Token 저장에 실패했습니다.", e);
        }
    }

    /**
     * Refresh Token 조회
     * 
     * @param customerId 고객 ID
     * @return Refresh Token (없으면 null)
     */
    public String getRefreshToken(Long customerId) {
        String key = REFRESH_TOKEN_PREFIX + customerId;
        
        try {
            String refreshToken = redisTemplate.opsForValue().get(key);
            log.debug("Refresh Token 조회 - customerId: {}, 존재여부: {}", customerId, refreshToken != null);
            return refreshToken;
        } catch (Exception e) {
            log.error("Refresh Token 조회 실패 - customerId: {}, error: {}", customerId, e.getMessage());
            return null;
        }
    }

    /**
     * Refresh Token 검증 (저장된 토큰과 비교)
     * 
     * @param customerId 고객 ID
     * @param refreshToken 검증할 Refresh Token
     * @return 유효 여부
     */
    public boolean validateRefreshToken(Long customerId, String refreshToken) {
        if (refreshToken == null) {
            return false;
        }
        
        String storedToken = getRefreshToken(customerId);
        boolean isValid = refreshToken.equals(storedToken);
        
        log.debug("Refresh Token 검증 - customerId: {}, 유효여부: {}", customerId, isValid);
        return isValid;
    }

    /**
     * Refresh Token 삭제
     * 
     * @param customerId 고객 ID
     */
    public void deleteRefreshToken(Long customerId) {
        String key = REFRESH_TOKEN_PREFIX + customerId;
        
        try {
            Boolean deleted = redisTemplate.delete(key);
            log.debug("Refresh Token 삭제 - customerId: {}, 삭제여부: {}", customerId, deleted);
        } catch (Exception e) {
            log.error("Refresh Token 삭제 실패 - customerId: {}, error: {}", customerId, e.getMessage());
        }
    }

    /**
     * 고객의 모든 Refresh Token 삭제 (로그아웃)
     * 
     * @param customerId 고객 ID
     */
    public void deleteAllRefreshTokensForCustomer(Long customerId) {
        deleteRefreshToken(customerId);
        log.info("고객의 모든 Refresh Token 삭제 완료 - customerId: {}", customerId);
    }

    /**
     * Refresh Token 만료 시간 조회
     * 
     * @param customerId 고객 ID
     * @return 남은 만료 시간 (초), 없으면 -1
     */
    public long getRefreshTokenExpiration(Long customerId) {
        String key = REFRESH_TOKEN_PREFIX + customerId;
        
        try {
            Long expiration = redisTemplate.getExpire(key, TimeUnit.SECONDS);
            return expiration != null ? expiration : -1;
        } catch (Exception e) {
            log.error("Refresh Token 만료시간 조회 실패 - customerId: {}, error: {}", customerId, e.getMessage());
            return -1;
        }
    }
} 