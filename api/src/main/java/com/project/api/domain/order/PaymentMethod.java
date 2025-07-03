package com.project.api.domain.order;

/**
 * 결제 방법 Enum
 * 
 * 주문 시 사용 가능한 결제 수단
 */
public enum PaymentMethod {
    
    /**
     * 신용카드
     */
    CREDIT_CARD("신용카드"),
    
    /**
     * 체크카드
     */
    DEBIT_CARD("체크카드"),
    
    /**
     * 현금
     */
    CASH("현금"),
    
    /**
     * 카카오페이
     */
    KAKAO_PAY("카카오페이"),
    
    /**
     * 네이버페이
     */
    NAVER_PAY("네이버페이"),
    
    /**
     * 토스페이
     */
    TOSS_PAY("토스페이"),
    
    /**
     * 페이코
     */
    PAYCO("페이코"),
    
    /**
     * 삼성페이
     */
    SAMSUNG_PAY("삼성페이");

    private final String description;

    PaymentMethod(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 전자결제 방식인지 확인
     */
    public boolean isElectronicPayment() {
        return this != CASH;
    }

    /**
     * 간편결제 방식인지 확인
     */
    public boolean isSimplePayment() {
        return this == KAKAO_PAY || this == NAVER_PAY || 
               this == TOSS_PAY || this == PAYCO || this == SAMSUNG_PAY;
    }

    /**
     * 카드 결제 방식인지 확인
     */
    public boolean isCardPayment() {
        return this == CREDIT_CARD || this == DEBIT_CARD;
    }
} 