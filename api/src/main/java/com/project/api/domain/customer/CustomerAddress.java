package com.project.api.domain.customer;

import com.project.api.port.in.customer.ManageCustomerAddressUseCase;
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
     * 주소 정보 수정 (CustomerAddress 타입)
     */
    public void updateAddress(CustomerAddress request) {
        if (request.getAddress() != null) {
            validateAddress(request.getAddress());
            this.address = request.getAddress();
        }
        if (request.getAddressDetail() != null) {
            this.addressDetail = request.getAddressDetail();
        }
        if (request.getZipCode() != null) {
            this.zipCode = request.getZipCode();
        }
        if (request.getNickname() != null) {
            this.nickname = request.getNickname();
        }
        if (request.isDefault()) {
            this.isDefault = request.isDefault();
        }
    }

    /**
     * 주소 정보 수정 (개별 필드 업데이트)
     */
    public void updateAddressInfo(String address, String addressDetail, String zipCode, String nickname, Boolean isDefault) {
        if (address != null) {
            validateAddress(address);
            this.address = address;
        }
        if (addressDetail != null) {
            this.addressDetail = addressDetail;
        }
        if (zipCode != null) {
            this.zipCode = zipCode;
        }
        if (nickname != null) {
            this.nickname = nickname;
        }
        if (isDefault != null) {
            this.isDefault = isDefault;
        }
    }

    /**
     * 주소 정보 수정 (Command 인터페이스 사용)
     */
    public void updateFromCommand(ManageCustomerAddressUseCase.UpdateCustomerAddressCommand command) {
        if (command.address() != null) {
            validateAddress(command.address());
            this.address = command.address();
        }
        if (command.addressDetail() != null) {
            this.addressDetail = command.addressDetail();
        }
        if (command.zipCode() != null) {
            this.zipCode = command.zipCode();
        }
        if (command.nickname() != null) {
            this.nickname = command.nickname();
        }
        if (command.isDefault() != null) {
            this.isDefault = command.isDefault();
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