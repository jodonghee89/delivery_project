package com.project.api.adapter.out.persistence.order;

import com.project.api.domain.order.Order;
import com.project.api.port.out.order.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA Order Repository
 * OrderRepository 포트 인터페이스를 직접 구현
 */
@Repository
public interface SpringDataOrderRepository extends JpaRepository<Order, Long>, OrderRepository {
    
    Page<Order> findByCustomerId(Long customerId, Pageable pageable);
    
    Page<Order> findByStoreId(Long storeId, Pageable pageable);
    
    Page<Order> findByDeliveryPersonId(Long deliveryPersonId, Pageable pageable);
} 