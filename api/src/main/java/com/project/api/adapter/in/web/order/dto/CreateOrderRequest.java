package com.project.api.adapter.in.web.order.dto;

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
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {
    
    @NotNull(message = "고객 ID는 필수입니다.")
    private Long customerId;
    
    @NotNull(message = "매장 ID는 필수입니다.")
    private Long storeId;
    
    @NotBlank(message = "배달 주소는 필수입니다.")
    private String deliveryAddress;
    
    @NotBlank(message = "결제 방법은 필수입니다.")
    private String paymentMethod;
    
    @NotNull(message = "주문 항목은 필수입니다.")
    private List<OrderItemRequest> orderItems;
    
    private String specialRequests; // 특별 요청사항
    
    /**
     * 주문 항목 요청 DTO
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemRequest {
        
        @NotNull(message = "메뉴 ID는 필수입니다.")
        private Long menuId;
        
        @NotNull(message = "수량은 필수입니다.")
        @Positive(message = "수량은 1개 이상이어야 합니다.")
        private Integer quantity;
        
        @NotNull(message = "가격은 필수입니다.")
        @Positive(message = "가격은 0원 이상이어야 합니다.")
        private Integer price;
        
        private String specialRequests; // 메뉴별 특별 요청사항
    }
} 