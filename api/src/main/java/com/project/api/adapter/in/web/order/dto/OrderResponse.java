package com.project.api.adapter.in.web.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 주문 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {
    
    private Long orderId;
    private Long customerId;
    private Long storeId;
    private String storeName;
    private String deliveryAddress;
    private String paymentMethod;
    private String orderStatus;
    private Integer totalAmount;
    private String specialRequests;
    private LocalDateTime orderDate;
    private LocalDateTime estimatedDeliveryTime;
    private Long deliveryPersonId;
    private String deliveryPersonName;
    
    private List<OrderItemResponse> orderItems;
    
    /**
     * 주문 항목 응답 DTO
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItemResponse {
        
        private Long orderItemId;
        private Long menuId;
        private String menuName;
        private Integer quantity;
        private Integer price;
        private Integer totalPrice;
        private String specialRequests;
    }
} 