package com.project.api.adapter.in.web.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 재주문 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReorderRequest {
    
    private String deliveryAddress; // 새로운 배달 주소 (null이면 기존 주소 사용)
    
    private String specialRequests; // 특별 요청사항
} 