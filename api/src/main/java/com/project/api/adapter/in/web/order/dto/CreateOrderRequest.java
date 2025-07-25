package com.project.api.adapter.in.web.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 주문 생성 요청 DTO
 */
@Schema(description = "주문 생성 요청")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {
    
    @Schema(description = "고객 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "고객 ID는 필수입니다.")
    private Long customerId;
    
    @Schema(description = "매장 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "매장 ID는 필수입니다.")
    private Long storeId;
    
    @Schema(description = "배달 주소", example = "서울특별시 강남구 테헤란로 123, 456호", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "배달 주소는 필수입니다.")
    private String deliveryAddress;
    
    @Schema(description = "결제 방법", example = "CARD", allowableValues = {"CARD", "CASH", "ACCOUNT_TRANSFER"}, requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "결제 방법은 필수입니다.")
    private String paymentMethod;
    
    @Schema(description = "주문 항목 목록", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "주문 항목은 필수입니다.")
    private List<OrderItemRequest> orderItems;
    
    @Schema(description = "특별 요청사항", example = "빨리 배달해주세요", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String specialRequests; // 특별 요청사항
    
    /**
     * 주문 항목 요청 DTO
     */
    @Schema(description = "주문 항목")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemRequest {
        
        @Schema(description = "메뉴 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "메뉴 ID는 필수입니다.")
        private Long menuId;
        
        @Schema(description = "주문 수량", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "수량은 필수입니다.")
        @Positive(message = "수량은 1개 이상이어야 합니다.")
        private Integer quantity;
        
        @Schema(description = "개당 가격", example = "15000", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "가격은 필수입니다.")
        @Positive(message = "가격은 0원 이상이어야 합니다.")
        private Integer price;
        
        @Schema(description = "메뉴별 특별 요청사항", example = "매운맛으로 해주세요", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        private String specialRequests; // 메뉴별 특별 요청사항
    }
} 