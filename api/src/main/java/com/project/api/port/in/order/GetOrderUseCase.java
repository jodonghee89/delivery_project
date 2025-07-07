package com.project.api.port.in.order;

import com.project.api.domain.order.Order;
import java.util.List;

/**
 * 주문 조회 Use Case
 * 주문 정보 조회 기능을 제공합니다.
 */
public interface GetOrderUseCase {
    
    /**
     * 주문 ID로 주문 정보를 조회합니다.
     * 
     * @param orderId 주문 ID
     * @return 주문 정보
     */
    Order getOrder(Long orderId);
    
    /**
     * 고객의 주문 목록을 조회합니다.
     * 
     * @param customerId 고객 ID
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 고객의 주문 목록
     */
    List<Order> getCustomerOrders(Long customerId, int page, int size);
    
    /**
     * 매장의 주문 목록을 조회합니다.
     * 
     * @param storeId 매장 ID
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 매장의 주문 목록
     */
    List<Order> getStoreOrders(Long storeId, int page, int size);
    
    /**
     * 배달원의 주문 목록을 조회합니다.
     * 
     * @param deliveryPersonId 배달원 ID
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 배달원의 주문 목록
     */
    List<Order> getDeliveryPersonOrders(Long deliveryPersonId, int page, int size);
} 