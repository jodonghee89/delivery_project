package com.project.api.adapter.in.web.order.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 주문 상태 업데이트 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderStatusRequest {
    
    @NotBlank(message = "주문 상태는 필수입니다.")
    private String orderStatus;
    
    private String updateReason; // 상태 변경 사유
} 