package com.project.api.adapter.in.web.order;

import com.project.api.adapter.in.web.order.dto.CreateOrderRequest;
import com.project.api.adapter.in.web.order.dto.OrderResponse;
import com.project.api.adapter.in.web.order.dto.ReorderRequest;
import com.project.api.adapter.in.web.order.dto.UpdateOrderStatusRequest;
import com.project.api.application.service.order.OrderMapper;
import com.project.api.domain.order.Order;
import com.project.api.domain.order.OrderItem;
import com.project.api.domain.order.OrderStatus;
import com.project.api.port.in.order.CreateOrderUseCase;
import com.project.api.port.in.order.GetOrderUseCase;
import com.project.api.port.in.order.ManageOrderUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 주문 관련 REST API Controller
 */
@Slf4j
@RestController
@RequestMapping("/delivery/orders")
@RequiredArgsConstructor
@Tag(name = "Order API", description = "주문 관련 API")
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;
    private final GetOrderUseCase getOrderUseCase;
    private final ManageOrderUseCase manageOrderUseCase;
    private final OrderMapper orderMapper;

    /**
     * 새로운 주문을 생성합니다.
     */
    @Operation(summary = "주문 생성", description = "새로운 주문을 생성합니다.")
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody CreateOrderRequest request) {
        
        log.info("주문 생성 요청 - customerId: {}, storeId: {}", request.getCustomerId(), request.getStoreId());
        
        // 총 금액 계산
        Integer totalAmount = orderMapper.calculateTotalAmount(request);
        
        // 주문 생성
        Order order = createOrderUseCase.createOrder(
                request.getCustomerId(),
                request.getStoreId(),
                request.getDeliveryAddress(),
                request.getPaymentMethod(),
                totalAmount
        );
        
        // 주문 항목 추가
        if (request.getOrderItems() != null) {
            for (CreateOrderRequest.OrderItemRequest itemRequest : request.getOrderItems()) {
                OrderItem orderItem = OrderItem.builder()
                        .order(order)
                        .menuId(itemRequest.getMenuId())
                        .quantity(itemRequest.getQuantity())
                        .price(itemRequest.getPrice())
                        .specialRequests(itemRequest.getSpecialRequests())
                        .build();
                order.addOrderItem(orderItem);
            }
        }
        
        // 특별 요청사항 설정
        if (request.getSpecialRequests() != null) {
            order.updateSpecialRequests(request.getSpecialRequests());
        }
        
        OrderResponse response = orderMapper.toOrderResponse(order);
        
        log.info("주문 생성 완료 - orderId: {}", order.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 주문 정보를 조회합니다.
     */
    @Operation(summary = "주문 조회", description = "주문 ID로 주문 정보를 조회합니다.")
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(
            @Parameter(description = "주문 ID") @PathVariable Long orderId) {
        
        log.info("주문 조회 요청 - orderId: {}", orderId);
        
        Order order = getOrderUseCase.getOrder(orderId);
        OrderResponse response = orderMapper.toOrderResponse(order);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 고객의 주문 목록을 조회합니다.
     */
    @Operation(summary = "고객 주문 목록 조회", description = "고객 ID로 주문 목록을 조회합니다.")
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderResponse>> getCustomerOrders(
            @Parameter(description = "고객 ID") @PathVariable Long customerId,
            @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "10") int size) {
        
        log.info("고객 주문 목록 조회 요청 - customerId: {}, page: {}, size: {}", customerId, page, size);
        
        List<Order> orders = getOrderUseCase.getCustomerOrders(customerId, page, size);
        List<OrderResponse> responses = orderMapper.toOrderResponseList(orders);
        
        return ResponseEntity.ok(responses);
    }

    /**
     * 매장의 주문 목록을 조회합니다.
     */
    @Operation(summary = "매장 주문 목록 조회", description = "매장 ID로 주문 목록을 조회합니다.")
    @GetMapping("/store/{storeId}")
    public ResponseEntity<List<OrderResponse>> getStoreOrders(
            @Parameter(description = "매장 ID") @PathVariable Long storeId,
            @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "10") int size) {
        
        log.info("매장 주문 목록 조회 요청 - storeId: {}, page: {}, size: {}", storeId, page, size);
        
        List<Order> orders = getOrderUseCase.getStoreOrders(storeId, page, size);
        List<OrderResponse> responses = orderMapper.toOrderResponseList(orders);
        
        return ResponseEntity.ok(responses);
    }

    /**
     * 배달원의 주문 목록을 조회합니다.
     */
    @Operation(summary = "배달원 주문 목록 조회", description = "배달원 ID로 주문 목록을 조회합니다.")
    @GetMapping("/delivery-person/{deliveryPersonId}")
    public ResponseEntity<List<OrderResponse>> getDeliveryPersonOrders(
            @Parameter(description = "배달원 ID") @PathVariable Long deliveryPersonId,
            @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "10") int size) {
        
        log.info("배달원 주문 목록 조회 요청 - deliveryPersonId: {}, page: {}, size: {}", deliveryPersonId, page, size);
        
        List<Order> orders = getOrderUseCase.getDeliveryPersonOrders(deliveryPersonId, page, size);
        List<OrderResponse> responses = orderMapper.toOrderResponseList(orders);
        
        return ResponseEntity.ok(responses);
    }

    /**
     * 주문을 취소합니다.
     */
    @Operation(summary = "주문 취소", description = "주문을 취소합니다.")
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> cancelOrder(
            @Parameter(description = "주문 ID") @PathVariable Long orderId,
            @Parameter(description = "취소 사유") @RequestParam(required = false) String cancelReason) {
        
        log.info("주문 취소 요청 - orderId: {}, reason: {}", orderId, cancelReason);
        
        manageOrderUseCase.cancelOrder(orderId, cancelReason);
        
        return ResponseEntity.noContent().build();
    }

    /**
     * 주문 상태를 업데이트합니다.
     */
    @Operation(summary = "주문 상태 업데이트", description = "주문 상태를 업데이트합니다.")
    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @Parameter(description = "주문 ID") @PathVariable Long orderId,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        
        log.info("주문 상태 업데이트 요청 - orderId: {}, status: {}", orderId, request.getOrderStatus());
        
        OrderStatus newStatus = OrderStatus.valueOf(request.getOrderStatus());
        Order updatedOrder = manageOrderUseCase.updateOrderStatus(orderId, newStatus, request.getUpdateReason());
        OrderResponse response = orderMapper.toOrderResponse(updatedOrder);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 재주문을 생성합니다.
     */
    @Operation(summary = "재주문", description = "기존 주문을 기반으로 재주문을 생성합니다.")
    @PostMapping("/{orderId}/reorder")
    public ResponseEntity<OrderResponse> reorder(
            @Parameter(description = "기존 주문 ID") @PathVariable Long orderId,
            @RequestBody(required = false) ReorderRequest request) {
        
        log.info("재주문 요청 - orderId: {}", orderId);
        
        String deliveryAddress = request != null ? request.getDeliveryAddress() : null;
        Order newOrder = manageOrderUseCase.reorder(orderId, deliveryAddress);
        
        // 특별 요청사항 설정
        if (request != null && request.getSpecialRequests() != null) {
            newOrder.updateSpecialRequests(request.getSpecialRequests());
        }
        
        OrderResponse response = orderMapper.toOrderResponse(newOrder);
        
        log.info("재주문 생성 완료 - newOrderId: {}", newOrder.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
} 