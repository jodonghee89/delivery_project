package com.project.api.adapter.in.web.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * API 에러 응답 공통 DTO
 * 
 * 모든 에러 응답에서 일관된 형식을 제공합니다.
 */
@Schema(description = "에러 응답")
@Builder
public record ErrorResponse(
    
    @Schema(description = "에러 발생 시간", example = "2024-01-15T10:30:00")
    LocalDateTime timestamp,
    
    @Schema(description = "HTTP 상태 코드", example = "401")
    int status,
    
    @Schema(description = "에러 코드", example = "JWT_MISSING")
    String error,
    
    @Schema(description = "에러 메시지", example = "인증 토큰이 필요합니다.")
    String message,
    
    @Schema(description = "요청 경로", example = "/delivery/customers/1")
    String path
) {
    
    // 정적 팩토리 메서드들
    public static ErrorResponse unauthorized(String message, String path) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(401)
                .error("UNAUTHORIZED")
                .message(message)
                .path(path)
                .build();
    }
    
    public static ErrorResponse jwtMissing(String path) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(401)
                .error("JWT_MISSING")
                .message("인증 토큰이 필요합니다. Authorization 헤더에 'Bearer <token>' 형식으로 토큰을 포함해주세요.")
                .path(path)
                .build();
    }
    
    public static ErrorResponse jwtExpired(String path) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(401)
                .error("JWT_EXPIRED")
                .message("토큰이 만료되었습니다. 다시 로그인해주세요.")
                .path(path)
                .build();
    }
    
    public static ErrorResponse jwtInvalid(String path) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(401)
                .error("JWT_INVALID")
                .message("유효하지 않은 토큰입니다. 다시 로그인해주세요.")
                .path(path)
                .build();
    }
    
    public static ErrorResponse forbidden(String message, String path) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(403)
                .error("FORBIDDEN")
                .message(message)
                .path(path)
                .build();
    }
    
    public static ErrorResponse notFound(String message, String path) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(404)
                .error("NOT_FOUND")
                .message(message)
                .path(path)
                .build();
    }
    
    public static ErrorResponse badRequest(String message, String path) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(400)
                .error("BAD_REQUEST")
                .message(message)
                .path(path)
                .build();
    }
} 