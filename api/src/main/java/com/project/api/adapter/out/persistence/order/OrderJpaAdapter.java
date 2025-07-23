package com.project.api.adapter.out.persistence.order;

import com.project.api.domain.order.Order;
import com.project.api.port.out.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 주문 Repository JPA 어댑터
 * 
 * 헥사고날 아키텍처에서 out 포트의 구현체
 * Spring Data JPA를 사용한 주문 데이터 접근
 */
@Repository
@RequiredArgsConstructor
public class OrderJpaAdapter implements OrderRepository {

    private final SpringDataOrderRepository springDataRepository;

    @Override
    public Order save(Order order) {
        return springDataRepository.save(order);
    }

    @Override
    public Optional<Order> findById(Long orderId) {
        return springDataRepository.findById(orderId);
    }

    @Override
    public List<Order> findAll() {
        return springDataRepository.findAll();
    }

    @Override
    public Page<Order> findByCustomerId(Long customerId, Pageable pageable) {
        return springDataRepository.findByCustomerId(customerId, pageable);
    }

    @Override
    public Page<Order> findByStoreId(Long storeId, Pageable pageable) {
        return springDataRepository.findByStoreId(storeId, pageable);
    }

    @Override
    public Page<Order> findByDeliveryPersonId(Long deliveryPersonId, Pageable pageable) {
        return springDataRepository.findByDeliveryPersonId(deliveryPersonId, pageable);
    }

    @Override
    public void deleteById(Long orderId) {
        springDataRepository.deleteById(orderId);
    }

    /**
     * Spring Data JPA Repository 인터페이스
     * 내부에서만 사용하는 실제 JPA 구현체
     */
    @Repository
    interface SpringDataOrderRepository extends JpaRepository<Order, Long> {
        Page<Order> findByCustomerId(Long customerId, Pageable pageable);
        Page<Order> findByStoreId(Long storeId, Pageable pageable);
        Page<Order> findByDeliveryPersonId(Long deliveryPersonId, Pageable pageable);
    }
} 