package com.project.api.port.in.order;

import com.project.api.domain.order.Order;
import com.project.api.domain.order.OrderStatus;

/**
 * 주문 관리 Use Case
 * 주문 상태 변경, 취소, 재주문 등의 기능을 제공합니다.
 */
public interface ManageOrderUseCase {
    
    /**
     * 주문을 취소합니다.
     * 
     * @param orderId 주문 ID
     * @param cancelReason 취소 사유
     */
    void cancelOrder(Long orderId, String cancelReason);
    
    /**
     * 주문 상태를 업데이트합니다.
     * 
     * @param orderId 주문 ID
     * @param newStatus 새로운 주문 상태
     * @param updateReason 상태 변경 사유
     * @return 업데이트된 주문 정보
     */
    Order updateOrderStatus(Long orderId, OrderStatus newStatus, String updateReason);
    
    /**
     * 재주문을 생성합니다.
     * 
     * @param orderId 기존 주문 ID
     * @param deliveryAddress 새로운 배달 주소 (null이면 기존 주소 사용)
     * @return 새로 생성된 주문 정보
     */
    Order reorder(Long orderId, String deliveryAddress);
} 