package com.project.api.adapter.in.web.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 주문 상태 업데이트 요청 DTO
 */
@Schema(description = "주문 상태 업데이트 요청")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderStatusRequest {
    
    @Schema(description = "주문 상태", example = "ACCEPTED", allowableValues = {"PENDING", "ACCEPTED", "PREPARING", "READY", "DELIVERING", "DELIVERED", "CANCELLED"}, requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "주문 상태는 필수입니다.")
    private String orderStatus;
    
    @Schema(description = "상태 변경 사유", example = "재료 부족으로 인한 취소", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String updateReason; // 상태 변경 사유
} 