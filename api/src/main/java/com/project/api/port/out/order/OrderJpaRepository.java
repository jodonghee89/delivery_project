package com.project.api.port.out.order;

import com.project.api.domain.order.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 주문 JPA Repository 구현체
 */
@Repository
public interface OrderJpaRepository extends JpaRepository<Order, Long>, OrderRepository {
    
    /**
     * 고객 ID로 주문 목록을 조회합니다.
     */
    Page<Order> findByCustomerId(Long customerId, Pageable pageable);
    
    /**
     * 매장 ID로 주문 목록을 조회합니다.
     */
    Page<Order> findByStoreId(Long storeId, Pageable pageable);
    
    /**
     * 배달원 ID로 주문 목록을 조회합니다.
     */
    Page<Order> findByDeliveryPersonId(Long deliveryPersonId, Pageable pageable);
} 