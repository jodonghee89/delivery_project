package com.project.api.domain.order;

import com.project.api.domain.customer.Customer;
import com.project.api.domain.store.Store;
import com.project.api.domain.delivery.DeliveryPerson;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 주문 도메인 Entity
 * 
 * 배달 주문의 핵심 비즈니스 로직을 포함하는 Aggregate Root
 */
@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "store_id", nullable = false)
    private Long storeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_person_id")
    private DeliveryPerson deliveryPerson;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30, nullable = false)
    private OrderStatus status;

    @CreatedDate
    @Column(name = "order_time", nullable = false)
    private LocalDateTime orderTime;

    @Column(name = "total_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", length = 30, nullable = false)
    private PaymentMethod paymentMethod;

    @Column(name = "delivery_address", columnDefinition = "TEXT", nullable = false)
    private String deliveryAddress;

    @Column(name = "memo", columnDefinition = "TEXT")
    private String memo;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderStatusHistory> statusHistories = new ArrayList<>();

    @Builder
    private Order(Long customerId, Long storeId, String deliveryAddress, String memo,
                  String paymentMethodStr, OrderStatus orderStatus, Integer totalAmount,
                  LocalDateTime orderDate, LocalDateTime estimatedDeliveryTime) {
        
        validateCustomerId(customerId);
        validateStoreId(storeId);
        validateDeliveryAddress(deliveryAddress);
        
        this.customerId = customerId;
        this.storeId = storeId;
        this.deliveryAddress = deliveryAddress;
        this.memo = memo;
        
        // PaymentMethod 설정
        if (paymentMethodStr != null) {
            this.paymentMethod = PaymentMethod.valueOf(paymentMethodStr);
        }
        
        this.status = orderStatus != null ? orderStatus : OrderStatus.PENDING;
        this.totalPrice = totalAmount != null ? BigDecimal.valueOf(totalAmount) : BigDecimal.ZERO;
        this.orderTime = orderDate != null ? orderDate : LocalDateTime.now();
    }

    // == 비즈니스 로직 == //
    
    /**
     * 주문 아이템 추가
     */
    public void addOrderItem(OrderItem orderItem) {
        validateOrderItem(orderItem);
        this.orderItems.add(orderItem);
        calculateTotalPrice();
    }

    /**
     * 주문 아이템 제거
     */
    public void removeOrderItem(Long orderItemId) {
        orderItems.removeIf(item -> item.getOrderItemId().equals(orderItemId));
        calculateTotalPrice();
    }

    /**
     * 주문 확인
     */
    public void confirm() {
        validateStatusTransition(OrderStatus.CONFIRMED);
        validateOrderItems();
        changeStatus(OrderStatus.CONFIRMED);
    }

    /**
     * 조리 시작
     */
    public void startPreparing() {
        validateStatusTransition(OrderStatus.PREPARING);
        changeStatus(OrderStatus.PREPARING);
    }

    /**
     * 배달원 배정
     */
    public void assignDeliveryPerson(DeliveryPerson deliveryPerson) {
        validateDeliveryPerson(deliveryPerson);
        if (this.status != OrderStatus.PREPARING) {
            throw new IllegalStateException("조리 중인 주문에만 배달원을 배정할 수 있습니다.");
        }
        
        this.deliveryPerson = deliveryPerson;
        changeStatus(OrderStatus.DELIVERING);
    }

    /**
     * 배달 완료
     */
    public void complete() {
        validateStatusTransition(OrderStatus.COMPLETED);
        if (deliveryPerson == null) {
            throw new IllegalStateException("배달원이 배정되지 않은 주문은 완료할 수 없습니다.");
        }
        changeStatus(OrderStatus.COMPLETED);
    }

    /**
     * 주문 취소
     */
    public void cancel(String reason) {
        if (status == OrderStatus.COMPLETED) {
            throw new IllegalStateException("완료된 주문은 취소할 수 없습니다.");
        }
        if (status == OrderStatus.DELIVERING) {
            throw new IllegalStateException("배달 중인 주문은 취소할 수 없습니다.");
        }
        changeStatus(OrderStatus.CANCELLED);
    }

    /**
     * 상태 변경
     */
    private void changeStatus(OrderStatus newStatus) {
        this.status = newStatus;
        addStatusHistory(newStatus);
    }

    /**
     * 상태 이력 추가
     */
    private void addStatusHistory(OrderStatus status) {
        OrderStatusHistory history = OrderStatusHistory.builder()
                .order(this)
                .status(status)
                .build();
        this.statusHistories.add(history);
    }

    /**
     * 총 금액 계산
     */
    private void calculateTotalPrice() {
        this.totalPrice = orderItems.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 주문 취소 가능 여부 확인
     */
    public boolean canBeCancelled() {
        return status == OrderStatus.PENDING || status == OrderStatus.CONFIRMED;
    }

    /**
     * 상태 변경 가능 여부 확인
     */
    public boolean canChangeStatusTo(OrderStatus newStatus) {
        return isValidStatusTransition(this.status, newStatus);
    }

    /**
     * 주문 상태 업데이트
     */
    public void updateStatus(OrderStatus newStatus, String reason) {
        validateStatusTransition(newStatus);
        changeStatus(newStatus);
    }

    /**
     * 특별 요청사항 업데이트
     */
    public void updateSpecialRequests(String specialRequests) {
        this.memo = specialRequests;
    }

    /**
     * ID 반환 (Service에서 사용)
     */
    public Long getId() {
        return this.orderId;
    }

    /**
     * 고객 ID 반환
     */
    public Long getCustomerId() {
        return this.customerId;
    }

    /**
     * 매장 ID 반환
     */
    public Long getStoreId() {
        return this.storeId;
    }

    /**
     * 배달원 ID 반환
     */
    public Long getDeliveryPersonId() {
        return this.deliveryPerson != null ? this.deliveryPerson.getId() : null;
    }

    /**
     * 주문 상태 반환
     */
    public OrderStatus getOrderStatus() {
        return this.status;
    }

    /**
     * 총 금액 반환 (Integer 타입)
     */
    public Integer getTotalAmount() {
        return this.totalPrice != null ? this.totalPrice.intValue() : 0;
    }

    /**
     * 결제 방법 반환 (String 타입)
     */
    public String getPaymentMethod() {
        return this.paymentMethod != null ? this.paymentMethod.name() : null;
    }

    /**
     * 주문 날짜 반환
     */
    public LocalDateTime getOrderDate() {
        return this.orderTime;
    }

    /**
     * 예상 배달 시간 반환 (임시로 주문 시간 + 30분)
     */
    public LocalDateTime getEstimatedDeliveryTime() {
        return this.orderTime != null ? this.orderTime.plusMinutes(30) : null;
    }

    /**
     * 특별 요청사항 반환
     */
    public String getSpecialRequests() {
        return this.memo;
    }

    /**
     * 배달 중 여부 확인
     */
    public boolean isDelivering() {
        return status == OrderStatus.DELIVERING;
    }

    /**
     * 완료 여부 확인
     */
    public boolean isCompleted() {
        return status == OrderStatus.COMPLETED;
    }

    // == 유효성 검증 == //
    
    private void validateCustomerId(Long customerId) {
        if (customerId == null) {
            throw new IllegalArgumentException("고객 ID는 필수입니다.");
        }
    }

    private void validateStoreId(Long storeId) {
        if (storeId == null) {
            throw new IllegalArgumentException("매장 ID는 필수입니다.");
        }
    }

    private void validatePaymentMethod(PaymentMethod paymentMethod) {
        if (paymentMethod == null) {
            throw new IllegalArgumentException("결제 방법은 필수입니다.");
        }
    }

    private void validateDeliveryAddress(String deliveryAddress) {
        if (deliveryAddress == null || deliveryAddress.trim().isEmpty()) {
            throw new IllegalArgumentException("배달 주소는 필수입니다.");
        }
    }

    private void validateOrderItem(OrderItem orderItem) {
        if (orderItem == null) {
            throw new IllegalArgumentException("주문 아이템은 필수입니다.");
        }
    }

    private void validateDeliveryPerson(DeliveryPerson deliveryPerson) {
        if (deliveryPerson == null) {
            throw new IllegalArgumentException("배달원 정보는 필수입니다.");
        }
    }

    private void validateStatusTransition(OrderStatus newStatus) {
        if (!isValidStatusTransition(this.status, newStatus)) {
            throw new IllegalStateException(
                String.format("주문 상태를 %s에서 %s로 변경할 수 없습니다.", 
                    this.status, newStatus));
        }
    }

    private boolean isValidStatusTransition(OrderStatus current, OrderStatus target) {
        return switch (current) {
            case PENDING -> target == OrderStatus.CONFIRMED || target == OrderStatus.CANCELLED;
            case CONFIRMED -> target == OrderStatus.PREPARING || target == OrderStatus.CANCELLED;
            case PREPARING -> target == OrderStatus.DELIVERING || target == OrderStatus.CANCELLED;
            case DELIVERING -> target == OrderStatus.COMPLETED;
            case COMPLETED, CANCELLED -> false;
        };
    }

    private void validateOrderItems() {
        if (orderItems.isEmpty()) {
            throw new IllegalStateException("주문 아이템이 없으면 주문을 확인할 수 없습니다.");
        }
        if (totalPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("주문 금액이 0보다 커야 합니다.");
        }
    }
} 