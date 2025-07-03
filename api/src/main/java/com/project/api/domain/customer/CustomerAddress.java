package com.project.api.domain.customer;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 고객 주소 Entity
 * 
 * 고객의 배달 주소 정보를 관리하는 도메인 엔티티
 */
@Entity
@Table(name = "customer_addresses")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class CustomerAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private Long addressId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "address", columnDefinition = "TEXT", nullable = false)
    private String address;

    @Column(name = "is_default", nullable = false)
    private boolean isDefault;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Builder
    private CustomerAddress(Customer customer, String address, boolean isDefault) {
        validateAddress(address);
        validateCustomer(customer);
        
        this.customer = customer;
        this.address = address;
        this.isDefault = isDefault;
    }

    // == 비즈니스 로직 == //
    
    /**
     * 주소 정보 수정
     */
    public void updateAddress(String newAddress) {
        validateAddress(newAddress);
        this.address = newAddress;
    }

    /**
     * 기본 주소로 설정
     */
    public void setAsDefault() {
        this.isDefault = true;
    }

    /**
     * 기본 주소 해제
     */
    public void unsetDefault() {
        this.isDefault = false;
    }

    /**
     * 기본 주소 여부 확인
     */
    public boolean isDefault() {
        return this.isDefault;
    }

    // == 유효성 검증 == //
    
    private void validateAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("주소는 필수입니다.");
        }
        if (address.length() > 500) {
            throw new IllegalArgumentException("주소는 500자를 초과할 수 없습니다.");
        }
    }

    private void validateCustomer(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("고객 정보는 필수입니다.");
        }
    }
} 