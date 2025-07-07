package com.project.api.adapter.in.web.customer.dto;

/**
 * 고객 주소 응답 DTO
 */
public record CustomerAddressResponse(
    Long id,
    String address,
    String addressDetail,
    String zipCode,
    String nickname,
    boolean isDefault
) {} 