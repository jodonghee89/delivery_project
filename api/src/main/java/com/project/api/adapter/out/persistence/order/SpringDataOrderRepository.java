package com.project.api.adapter.out.persistence.order;

import com.project.api.domain.order.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA Order Repository
 */
@Repository
public interface SpringDataOrderRepository extends JpaRepository<Order, Long> {
    
    Page<Order> findByCustomerId(Long customerId, Pageable pageable);
    
    Page<Order> findByStoreId(Long storeId, Pageable pageable);
    
    Page<Order> findByDeliveryPersonId(Long deliveryPersonId, Pageable pageable);
} 