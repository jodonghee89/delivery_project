package com.project.api.adapter.in.web.customer.dto;

/**
 * 로그인 응답 DTO
 */
public record LoginResponse(
    String accessToken,
    String refreshToken,
    Long customerId,
    String customerName
) {} 