package com.project.api.domain.customer;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 고객 도메인 Entity
 * 
 * 헥사고날 아키텍처에서 도메인 계층의 핵심 엔티티
 * 비즈니스 로직과 규칙을 포함
 */
@Entity
@Table(name = "customers")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "email", length = 100, nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Column(name = "phone", length = 20)
    private String phone;

    // == Getter 메서드 추가 == //
    
    /**
     * ID 반환 (외부 인터페이스 일관성을 위해)
     */
    public Long getId() {
        return customerId;
    }

    /**
     * 전화번호 반환 (외부 인터페이스 일관성을 위해)
     */
    public String getPhoneNumber() {
        return phone;
    }

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CustomerAddress> addresses = new ArrayList<>();

    @Builder
    private Customer(String email, String password, String name, String phone) {
        validateEmail(email);
        validatePassword(password);
        validateName(name);
        
        this.email = email;
        this.password = password;
        this.name = name;
        this.phone = phone;
    }

    // == 비즈니스 로직 == //
    
    /**
     * 고객 정보 수정
     */
    public void updateInfo(String name, String email, String phone) {
        if (name != null) {
            validateName(name);
            this.name = name;
        }
        if (email != null) {
            validateEmail(email);
            this.email = email;
        }
        if (phone != null) {
            this.phone = phone;
        }
    }

    /**
     * 비밀번호 변경
     */
    public void changePassword(String oldPassword, String newPassword) {
        if (!this.password.equals(oldPassword)) {
            throw new IllegalArgumentException("기존 비밀번호가 일치하지 않습니다.");
        }
        validatePassword(newPassword);
        this.password = newPassword;
    }

    /**
     * 주소 추가
     */
    public void addAddress(CustomerAddress customerAddress) {
        this.addresses.add(customerAddress);
        customerAddress.setCustomer(this);
    }

    /**
     * 주소 제거
     */
    public void removeAddress(CustomerAddress customerAddress) {
        this.addresses.remove(customerAddress);
    }

    /**
     * 기본 주소 해제
     */
    public void clearDefaultAddress() {
        addresses.forEach(addr -> addr.unsetDefault());
    }

    /**
     * 기본 주소 변경
     */
    public void changeDefaultAddress(Long addressId) {
        CustomerAddress targetAddress = addresses.stream()
                .filter(addr -> addr.getAddressId().equals(addressId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주소입니다."));

        // 모든 주소를 일반 주소로 변경
        addresses.forEach(CustomerAddress::unsetDefault);
        
        // 선택한 주소를 기본 주소로 설정
        targetAddress.setAsDefault();
    }

    /**
     * 기본 주소 조회
     */
    public CustomerAddress getDefaultAddress() {
        return addresses.stream()
                .filter(CustomerAddress::isDefault)
                .findFirst()
                .orElse(null);
    }

    // == 유효성 검증 == //
    
    private void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("이메일은 필수입니다.");
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("올바른 이메일 형식이 아닙니다.");
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("비밀번호는 필수입니다.");
        }
        if (password.length() < 8) {
            throw new IllegalArgumentException("비밀번호는 8자 이상이어야 합니다.");
        }
    }

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("이름은 필수입니다.");
        }
        if (name.length() > 50) {
            throw new IllegalArgumentException("이름은 50자를 초과할 수 없습니다.");
        }
    }
} 