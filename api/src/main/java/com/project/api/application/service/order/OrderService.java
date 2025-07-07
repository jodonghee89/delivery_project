package com.project.api.application.service.order;

import com.project.api.domain.order.Order;
import com.project.api.domain.order.OrderItem;
import com.project.api.domain.order.OrderStatus;
import com.project.api.port.in.order.CreateOrderUseCase;
import com.project.api.port.in.order.GetOrderUseCase;
import com.project.api.port.in.order.ManageOrderUseCase;
import com.project.api.port.out.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 주문 Application Service
 * 주문 관련 비즈니스 로직을 처리합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService implements CreateOrderUseCase, GetOrderUseCase, ManageOrderUseCase {

    private final OrderRepository orderRepository;

    @Override
    @Transactional
    public Order createOrder(Long customerId, Long storeId, String deliveryAddress, String paymentMethod, Integer totalAmount) {
        log.info("주문 생성 시작 - customerId: {}, storeId: {}", customerId, storeId);
        
        // 주문 생성
        Order order = Order.builder()
                .customerId(customerId)
                .storeId(storeId)
                .deliveryAddress(deliveryAddress)
                .paymentMethodStr(paymentMethod)
                .orderStatus(OrderStatus.PENDING)
                .totalAmount(totalAmount)
                .orderDate(LocalDateTime.now())
                .estimatedDeliveryTime(LocalDateTime.now().plusMinutes(30)) // 기본 30분 후 배달 예정
                .build();
        
        Order savedOrder = orderRepository.save(order);
        log.info("주문 생성 완료 - orderId: {}", savedOrder.getId());
        
        return savedOrder;
    }

    @Override
    public Order getOrder(Long orderId) {
        log.info("주문 조회 - orderId: {}", orderId);
        
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다. orderId: " + orderId));
    }

    @Override
    public List<Order> getCustomerOrders(Long customerId, int page, int size) {
        log.info("고객 주문 목록 조회 - customerId: {}, page: {}, size: {}", customerId, page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        return orderRepository.findByCustomerId(customerId, pageable).getContent();
    }

    @Override
    public List<Order> getStoreOrders(Long storeId, int page, int size) {
        log.info("매장 주문 목록 조회 - storeId: {}, page: {}, size: {}", storeId, page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        return orderRepository.findByStoreId(storeId, pageable).getContent();
    }

    @Override
    public List<Order> getDeliveryPersonOrders(Long deliveryPersonId, int page, int size) {
        log.info("배달원 주문 목록 조회 - deliveryPersonId: {}, page: {}, size: {}", deliveryPersonId, page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        return orderRepository.findByDeliveryPersonId(deliveryPersonId, pageable).getContent();
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId, String cancelReason) {
        log.info("주문 취소 - orderId: {}, reason: {}", orderId, cancelReason);
        
        Order order = getOrder(orderId);
        
        // 취소 가능 상태 검증
        if (!order.canBeCancelled()) {
            throw new RuntimeException("취소할 수 없는 주문 상태입니다. 현재 상태: " + order.getOrderStatus());
        }
        
        order.cancel(cancelReason);
        orderRepository.save(order);
        
        log.info("주문 취소 완료 - orderId: {}", orderId);
    }

    @Override
    @Transactional
    public Order updateOrderStatus(Long orderId, OrderStatus newStatus, String updateReason) {
        log.info("주문 상태 업데이트 - orderId: {}, newStatus: {}, reason: {}", orderId, newStatus, updateReason);
        
        Order order = getOrder(orderId);
        
        // 상태 변경 검증
        if (!order.canChangeStatusTo(newStatus)) {
            throw new RuntimeException("유효하지 않은 상태 전환입니다. 현재: " + order.getOrderStatus() + " -> 변경: " + newStatus);
        }
        
        order.updateStatus(newStatus, updateReason);
        Order updatedOrder = orderRepository.save(order);
        
        log.info("주문 상태 업데이트 완료 - orderId: {}, status: {}", orderId, newStatus);
        
        return updatedOrder;
    }

    @Override
    @Transactional
    public Order reorder(Long orderId, String deliveryAddress) {
        log.info("재주문 생성 - originalOrderId: {}", orderId);
        
        Order originalOrder = getOrder(orderId);
        
        // 재주문 생성
        String newDeliveryAddress = deliveryAddress != null ? deliveryAddress : originalOrder.getDeliveryAddress();
        
        Order newOrder = Order.builder()
                .customerId(originalOrder.getCustomerId())
                .storeId(originalOrder.getStoreId())
                .deliveryAddress(newDeliveryAddress)
                .paymentMethodStr(originalOrder.getPaymentMethod())
                .orderStatus(OrderStatus.PENDING)
                .totalAmount(originalOrder.getTotalAmount())
                .orderDate(LocalDateTime.now())
                .estimatedDeliveryTime(LocalDateTime.now().plusMinutes(30))
                .build();
        
        // 기존 주문 항목들 복사
        if (originalOrder.getOrderItems() != null) {
            for (OrderItem originalItem : originalOrder.getOrderItems()) {
                OrderItem newItem = OrderItem.builder()
                        .order(newOrder)
                        .menuId(originalItem.getMenuId())
                        .quantity(originalItem.getQuantity())
                        .price(originalItem.getPrice())
                        .specialRequests(originalItem.getSpecialRequests())
                        .build();
                newOrder.addOrderItem(newItem);
            }
        }
        
        Order savedOrder = orderRepository.save(newOrder);
        log.info("재주문 생성 완료 - newOrderId: {}", savedOrder.getId());
        
        return savedOrder;
    }
} 