package com.project.api.port.out.order;

import com.project.api.domain.order.Order;
import com.project.api.domain.order.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

/**
 * 주문 Repository 인터페이스
 * 주문 데이터 영속성 계층의 추상화입니다.
 */
public interface OrderRepository {
    
    /**
     * 주문을 저장합니다.
     * 
     * @param order 저장할 주문 정보
     * @return 저장된 주문 정보
     */
    Order save(Order order);
    
    /**
     * 주문 ID로 주문을 조회합니다.
     * 
     * @param orderId 주문 ID
     * @return 주문 정보 (Optional)
     */
    Optional<Order> findById(Long orderId);
    
    /**
     * 고객 ID로 주문 목록을 조회합니다.
     * 
     * @param customerId 고객 ID
     * @param pageable 페이징 정보
     * @return 고객의 주문 목록
     */
    Page<Order> findByCustomerId(Long customerId, Pageable pageable);
    
    /**
     * 매장 ID로 주문 목록을 조회합니다.
     * 
     * @param storeId 매장 ID
     * @param pageable 페이징 정보
     * @return 매장의 주문 목록
     */
    Page<Order> findByStoreId(Long storeId, Pageable pageable);
    
    /**
     * 배달원 ID로 주문 목록을 조회합니다.
     * 
     * @param deliveryPersonId 배달원 ID
     * @param pageable 페이징 정보
     * @return 배달원의 주문 목록
     */
    Page<Order> findByDeliveryPersonId(Long deliveryPersonId, Pageable pageable);
    
    /**
     * 주문을 삭제합니다.
     * 
     * @param orderId 삭제할 주문 ID
     */
    void deleteById(Long orderId);
} 