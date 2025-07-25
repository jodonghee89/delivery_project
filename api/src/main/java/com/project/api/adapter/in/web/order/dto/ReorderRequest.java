package com.project.api.adapter.in.web.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 재주문 요청 DTO
 */
@Schema(description = "재주문 요청")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReorderRequest {
    
    @Schema(description = "새로운 배달 주소 (null이면 기존 주소 사용)", example = "서울특별시 강남구 테헤란로 456, 789호", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String deliveryAddress; // 새로운 배달 주소 (null이면 기존 주소 사용)
    
    @Schema(description = "특별 요청사항", example = "빨리 배달해주세요", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String specialRequests; // 특별 요청사항
} 