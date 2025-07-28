package com.project.api.adapter.out.persistence.order;

import com.project.api.domain.order.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA Repository 인터페이스
 * 
 * Order 엔티티에 대한 데이터베이스 접근을 담당합니다.
 * Spring Data JPA가 자동으로 구현체를 생성하여 빈으로 등록합니다.
 */
@Repository
interface SpringDataOrderRepository extends JpaRepository<Order, Long> {
    
    /**
     * 고객 ID로 주문 목록 조회 (페이징)
     */
    Page<Order> findByCustomerId(Long customerId, Pageable pageable);
    
    /**
     * 매장 ID로 주문 목록 조회 (페이징)
     */
    Page<Order> findByStoreId(Long storeId, Pageable pageable);
    
    /**
     * 배달원 ID로 주문 목록 조회 (페이징)
     * JPQL 쿼리 사용
     */
    @Query("SELECT o FROM Order o WHERE o.deliveryPerson.id = :deliveryPersonId")
    Page<Order> findByDeliveryPersonId(@Param("deliveryPersonId") Long deliveryPersonId, Pageable pageable);
} 