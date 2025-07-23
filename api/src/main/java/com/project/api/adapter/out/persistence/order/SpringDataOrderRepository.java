package com.project.api.adapter.out.persistence.order;

import com.project.api.domain.order.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Order용 Spring Data JPA Repository
 */
@Repository
public interface SpringDataOrderRepository extends JpaRepository<Order, Long> {
    
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
    @Query("SELECT o FROM Order o WHERE o.deliveryPerson.id = :deliveryPersonId")
    Page<Order> findByDeliveryPersonId(@Param("deliveryPersonId") Long deliveryPersonId, Pageable pageable);
} 