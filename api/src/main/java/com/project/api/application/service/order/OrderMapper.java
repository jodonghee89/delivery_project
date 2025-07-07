package com.project.api.application.service.order;

import com.project.api.adapter.in.web.order.dto.CreateOrderRequest;
import com.project.api.adapter.in.web.order.dto.OrderResponse;
import com.project.api.domain.order.Order;
import com.project.api.domain.order.OrderItem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Order 도메인 객체와 DTO 간의 변환을 담당하는 매퍼 클래스
 */
@Component
public class OrderMapper {

    /**
     * 주문 생성 요청 DTO에서 총 금액을 계산합니다.
     */
    public Integer calculateTotalAmount(CreateOrderRequest request) {
        if (request.getOrderItems() == null || request.getOrderItems().isEmpty()) {
            return 0;
        }
        
        return request.getOrderItems().stream()
                .mapToInt(item -> item.getPrice() * item.getQuantity())
                .sum();
    }

    /**
     * Order 도메인 객체를 OrderResponse DTO로 변환합니다.
     */
    public OrderResponse toOrderResponse(Order order) {
        if (order == null) {
            return null;
        }

        return OrderResponse.builder()
                .orderId(order.getId())
                .customerId(order.getCustomerId())
                .storeId(order.getStoreId())
                .storeName(null) // TODO: Store 정보 조회 후 설정
                .deliveryAddress(order.getDeliveryAddress())
                .paymentMethod(order.getPaymentMethod())
                .orderStatus(order.getOrderStatus().name())
                .totalAmount(order.getTotalAmount())
                .specialRequests(order.getSpecialRequests())
                .orderDate(order.getOrderDate())
                .estimatedDeliveryTime(order.getEstimatedDeliveryTime())
                .deliveryPersonId(order.getDeliveryPersonId())
                .deliveryPersonName(null) // TODO: DeliveryPerson 정보 조회 후 설정
                .orderItems(toOrderItemResponseList(order.getOrderItems()))
                .build();
    }

    /**
     * Order 도메인 객체 리스트를 OrderResponse DTO 리스트로 변환합니다.
     */
    public List<OrderResponse> toOrderResponseList(List<Order> orders) {
        if (orders == null) {
            return null;
        }

        return orders.stream()
                .map(this::toOrderResponse)
                .collect(Collectors.toList());
    }

    /**
     * OrderItem 도메인 객체를 OrderItemResponse DTO로 변환합니다.
     */
    public OrderResponse.OrderItemResponse toOrderItemResponse(OrderItem orderItem) {
        if (orderItem == null) {
            return null;
        }

        return OrderResponse.OrderItemResponse.builder()
                .orderItemId(orderItem.getId())
                .menuId(orderItem.getMenuId())
                .menuName(null) // TODO: Menu 정보 조회 후 설정
                .quantity(orderItem.getQuantity())
                .price(orderItem.getPrice())
                .totalPrice(orderItem.getPrice() * orderItem.getQuantity())
                .specialRequests(orderItem.getSpecialRequests())
                .build();
    }

    /**
     * OrderItem 도메인 객체 리스트를 OrderItemResponse DTO 리스트로 변환합니다.
     */
    public List<OrderResponse.OrderItemResponse> toOrderItemResponseList(List<OrderItem> orderItems) {
        if (orderItems == null) {
            return null;
        }

        return orderItems.stream()
                .map(this::toOrderItemResponse)
                .collect(Collectors.toList());
    }
} 