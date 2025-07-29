package com.project.api.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * JWT 인증 관련 커스텀 예외
 * 
 * JWT 토큰 검증 실패 시 구체적인 오류 정보를 제공합니다.
 */
public class JwtAuthenticationException extends AuthenticationException {

    private final String errorCode;

    public JwtAuthenticationException(String message) {
        super(message);
        this.errorCode = "JWT_INVALID";
    }

    public JwtAuthenticationException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public JwtAuthenticationException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "JWT_ERROR";
    }

    public String getErrorCode() {
        return errorCode;
    }

    // 정적 팩토리 메서드들
    public static JwtAuthenticationException tokenMissing() {
        return new JwtAuthenticationException(
            "인증 토큰이 필요합니다. Authorization 헤더에 'Bearer <token>' 형식으로 토큰을 포함해주세요.",
            "JWT_MISSING"
        );
    }

    public static JwtAuthenticationException invalidFormat() {
        return new JwtAuthenticationException(
            "잘못된 토큰 형식입니다. 'Bearer <token>' 형식으로 입력해주세요.",
            "JWT_INVALID_FORMAT"
        );
    }

    public static JwtAuthenticationException tokenExpired() {
        return new JwtAuthenticationException(
            "토큰이 만료되었습니다. 다시 로그인해주세요.",
            "JWT_EXPIRED"
        );
    }

    public static JwtAuthenticationException invalidToken() {
        return new JwtAuthenticationException(
            "유효하지 않은 토큰입니다.",
            "JWT_INVALID"
        );
    }

    public static JwtAuthenticationException invalidSignature() {
        return new JwtAuthenticationException(
            "토큰 서명이 유효하지 않습니다.",
            "JWT_INVALID_SIGNATURE"
        );
    }
} 