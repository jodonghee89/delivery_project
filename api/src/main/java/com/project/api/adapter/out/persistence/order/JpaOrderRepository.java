package com.project.api.adapter.out.persistence.order;

import com.project.api.domain.order.Order;
import com.project.api.port.out.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * JPA Order Repository
 * OrderRepository 인터페이스의 구현체
 */
@Repository
@RequiredArgsConstructor
public class JpaOrderRepository implements OrderRepository {

    private final SpringDataOrderRepository springDataOrderRepository;

    @Override
    public Order save(Order order) {
        return springDataOrderRepository.save(order);
    }

    @Override
    public Optional<Order> findById(Long orderId) {
        return springDataOrderRepository.findById(orderId);
    }

    @Override
    public Page<Order> findByCustomerId(Long customerId, Pageable pageable) {
        return springDataOrderRepository.findByCustomerId(customerId, pageable);
    }

    @Override
    public Page<Order> findByStoreId(Long storeId, Pageable pageable) {
        return springDataOrderRepository.findByStoreId(storeId, pageable);
    }

    @Override
    public Page<Order> findByDeliveryPersonId(Long deliveryPersonId, Pageable pageable) {
        return springDataOrderRepository.findByDeliveryPersonId(deliveryPersonId, pageable);
    }

    @Override
    public void deleteById(Long orderId) {
        springDataOrderRepository.deleteById(orderId);
    }
} 