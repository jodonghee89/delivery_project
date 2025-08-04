package com.project.api.adapter.in.web.customer.dto;

import com.project.api.port.in.customer.RefreshTokenUseCase;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * Refresh Token 갱신 요청 DTO
 */
@Schema(description = "Refresh Token 갱신 요청")
public record RefreshTokenRequest(
    
    @Schema(description = "Refresh Token", example = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxMjMiLCJ0b2tlblR5cGUiOiJyZWZyZXNoIn0.signature", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Refresh Token은 필수입니다")
    String refreshToken
    
) implements RefreshTokenUseCase.RefreshTokenCommand {} 