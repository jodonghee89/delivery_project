package com.project.api.domain.order;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 주문 상태 이력 Entity
 * 
 * 주문 상태 변경 이력을 추적하는 도메인 엔티티
 */
@Entity
@Table(name = "order_status_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class OrderStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30, nullable = false)
    private OrderStatus status;

    @CreatedDate
    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt;

    @Builder
    private OrderStatusHistory(Order order, OrderStatus status) {
        validateOrder(order);
        validateStatus(status);
        
        this.order = order;
        this.status = status;
    }

    // == 비즈니스 로직 == //
    
    /**
     * 상태 설명 조회
     */
    public String getStatusDescription() {
        return status.getDescription();
    }

    /**
     * 특정 시간 이후에 변경되었는지 확인
     */
    public boolean isChangedAfter(LocalDateTime dateTime) {
        return changedAt.isAfter(dateTime);
    }

    /**
     * 특정 시간 이전에 변경되었는지 확인
     */
    public boolean isChangedBefore(LocalDateTime dateTime) {
        return changedAt.isBefore(dateTime);
    }

    /**
     * 완료 상태인지 확인
     */
    public boolean isCompletedStatus() {
        return status.isFinished();
    }

    // == 유효성 검증 == //
    
    private void validateOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("주문 정보는 필수입니다.");
        }
    }

    private void validateStatus(OrderStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("주문 상태는 필수입니다.");
        }
    }
} 