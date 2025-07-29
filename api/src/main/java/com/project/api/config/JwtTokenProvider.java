package com.project.api.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 토큰 생성, 검증, 파싱을 담당하는 클래스
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;

    /**
     * Access Token 생성
     */
    public String generateAccessToken(Long customerId, String customerName) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.getAccessTokenValidityInMilliseconds());

        return Jwts.builder()
                .subject(String.valueOf(customerId))
                .claim("customerName", customerName)
                .claim("tokenType", "access")
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Refresh Token 생성
     */
    public String generateRefreshToken(Long customerId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.getRefreshTokenValidityInMilliseconds());

        return Jwts.builder()
                .subject(String.valueOf(customerId))
                .claim("tokenType", "refresh")
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 토큰에서 사용자 ID 추출
     */
    public Long getCustomerIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return Long.valueOf(claims.getSubject());
    }

    /**
     * 토큰에서 사용자 이름 추출
     */
    public String getCustomerNameFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("customerName", String.class);
    }

    /**
     * 토큰 유효성 검증
     */
    public boolean validateToken(String token) {
        try {
            getClaimsFromToken(token);
            return true;
        } catch (SecurityException e) {
            log.warn("유효하지 않은 JWT 서명: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.warn("잘못된 형식의 JWT 토큰: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.warn("만료된 JWT 토큰: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("지원하지 않는 JWT 토큰: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("JWT claims가 비어있음: {}", e.getMessage());
        } catch (Exception e) {
            log.error("JWT 토큰 검증 중 예상치 못한 오류: {}", e.getMessage());
        }
        return false;
    }

    /**
     * 토큰 유효성 검증 및 상세 오류 정보 반환
     */
    public TokenValidationResult validateTokenWithDetails(String token) {
        try {
            getClaimsFromToken(token);
            return TokenValidationResult.valid();
        } catch (SecurityException e) {
            return TokenValidationResult.invalid("유효하지 않은 서명입니다.");
        } catch (MalformedJwtException e) {
            return TokenValidationResult.invalid("잘못된 토큰 형식입니다.");
        } catch (ExpiredJwtException e) {
            return TokenValidationResult.invalid("토큰이 만료되었습니다.");
        } catch (UnsupportedJwtException e) {
            return TokenValidationResult.invalid("지원하지 않는 토큰입니다.");
        } catch (IllegalArgumentException e) {
            return TokenValidationResult.invalid("토큰이 비어있습니다.");
        } catch (Exception e) {
            return TokenValidationResult.invalid("토큰 검증 중 오류가 발생했습니다.");
        }
    }

    /**
     * 토큰 검증 결과를 담는 내부 클래스
     */
    public static class TokenValidationResult {
        private final boolean valid;
        private final String errorMessage;

        private TokenValidationResult(boolean valid, String errorMessage) {
            this.valid = valid;
            this.errorMessage = errorMessage;
        }

        public static TokenValidationResult valid() {
            return new TokenValidationResult(true, null);
        }

        public static TokenValidationResult invalid(String errorMessage) {
            return new TokenValidationResult(false, errorMessage);
        }

        public boolean isValid() { return valid; }
        public String getErrorMessage() { return errorMessage; }
    }

    /**
     * 토큰이 만료되었는지 확인
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 토큰에서 Claims 추출
     */
    private Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * JWT 서명용 키 생성
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
} 