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

    @Column(name = "address_detail", columnDefinition = "TEXT")
    private String addressDetail;

    @Column(name = "zip_code", length = 10)
    private String zipCode;

    @Column(name = "nickname", length = 50)
    private String nickname;

    @Column(name = "is_default", nullable = false)
    private boolean isDefault;

    // == Getter 메서드 추가 == //
    
    /**
     * ID 반환 (외부 인터페이스 일관성을 위해)
     */
    public Long getId() {
        return addressId;
    }

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Builder
    private CustomerAddress(Customer customer, String address, String addressDetail, String zipCode, String nickname, boolean isDefault) {
        validateAddress(address);
        
        this.customer = customer;
        this.address = address;
        this.addressDetail = addressDetail;
        this.zipCode = zipCode;
        this.nickname = nickname;
        this.isDefault = isDefault;
    }

    // == 비즈니스 로직 == //
    
    /**
     * 주소 정보 수정
     */
    public void updateAddress(String newAddress, String newAddressDetail, String newZipCode, String newNickname, Boolean newIsDefault) {
        if (newAddress != null) {
            validateAddress(newAddress);
            this.address = newAddress;
        }
        if (newAddressDetail != null) {
            this.addressDetail = newAddressDetail;
        }
        if (newZipCode != null) {
            this.zipCode = newZipCode;
        }
        if (newNickname != null) {
            this.nickname = newNickname;
        }
        if (newIsDefault != null) {
            this.isDefault = newIsDefault;
        }
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

    /**
     * Customer 설정 (패키지 내부 사용)
     */
    void setCustomer(Customer customer) {
        this.customer = customer;
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