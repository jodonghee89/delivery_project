package com.project.api.adapter.in.web.customer.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 고객 응답 DTO
 */
public record CustomerResponse(
    Long id,
    String name,
    String email,
    String phoneNumber,
    LocalDateTime createdAt,
    List<CustomerAddressResponse> addresses
) {} 