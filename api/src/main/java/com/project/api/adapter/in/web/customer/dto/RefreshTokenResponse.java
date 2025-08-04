package com.project.api.adapter.in.web.customer.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Refresh Token 갱신 응답 DTO
 */
@Schema(description = "Refresh Token 갱신 응답")
public record RefreshTokenResponse(
    
    @Schema(description = "새로운 Access Token", example = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxMjMiLCJ0b2tlblR5cGUiOiJhY2Nlc3MifQ.signature")
    String accessToken,
    
    @Schema(description = "새로운 Refresh Token", example = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxMjMiLCJ0b2tlblR5cGUiOiJyZWZyZXNoIn0.signature")
    String refreshToken,
    
    @Schema(description = "고객 ID", example = "123")
    Long customerId,
    
    @Schema(description = "고객 이름", example = "김배달")
    String customerName
) {}