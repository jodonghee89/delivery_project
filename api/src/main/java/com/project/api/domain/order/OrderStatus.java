package com.project.api.domain.order;

/**
 * 주문 상태 Enum
 * 
 * 주문의 생명주기를 나타내는 상태값
 */
public enum OrderStatus {
    
    /**
     * 주문 접수 대기
     */
    PENDING("주문 접수 대기"),
    
    /**
     * 주문 확인됨
     */
    CONFIRMED("주문 확인"),
    
    /**
     * 조리 중
     */
    PREPARING("조리 중"),
    
    /**
     * 배달 중
     */
    DELIVERING("배달 중"),
    
    /**
     * 배달 완료
     */
    COMPLETED("배달 완료"),
    
    /**
     * 주문 취소
     */
    CANCELLED("주문 취소");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 완료된 상태인지 확인
     */
    public boolean isFinished() {
        return this == COMPLETED || this == CANCELLED;
    }

    /**
     * 진행 중인 상태인지 확인
     */
    public boolean isInProgress() {
        return this == CONFIRMED || this == PREPARING || this == DELIVERING;
    }

    /**
     * 취소 가능한 상태인지 확인
     */
    public boolean isCancellable() {
        return this == PENDING || this == CONFIRMED;
    }
} 