package com.project.api.port.in.order;

import com.project.api.domain.order.Order;

/**
 * 주문 생성 Use Case
 * 새로운 주문을 생성하는 기능을 제공합니다.
 */
public interface CreateOrderUseCase {
    
    /**
     * 새로운 주문을 생성합니다.
     * 
     * @param customerId 고객 ID
     * @param storeId 매장 ID
     * @param deliveryAddress 배달 주소
     * @param paymentMethod 결제 방법
     * @param totalAmount 총 주문 금액
     * @return 생성된 주문 정보
     */
    Order createOrder(Long customerId, Long storeId, String deliveryAddress, String paymentMethod, Integer totalAmount);
} 