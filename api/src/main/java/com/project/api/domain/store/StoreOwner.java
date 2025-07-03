package com.project.api.domain.store;

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
 * 매장 사장 도메인 Entity
 * 
 * 매장을 소유하고 운영하는 사장의 정보를 관리하는 도메인 엔티티
 */
@Entity
@Table(name = "store_owners")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class StoreOwner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "owner_id")
    private Long ownerId;

    @Column(name = "email", length = 100, nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Column(name = "phone", length = 20)
    private String phone;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Store> stores = new ArrayList<>();

    @Builder
    private StoreOwner(String email, String password, String name, String phone) {
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
     * 사장 정보 수정
     */
    public void updateInfo(String name, String phone) {
        validateName(name);
        this.name = name;
        this.phone = phone;
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
     * 매장 추가
     */
    public void addStore(Store store) {
        validateStore(store);
        this.stores.add(store);
    }

    /**
     * 매장 개수 조회
     */
    public int getStoreCount() {
        return stores.size();
    }

    /**
     * 운영 중인 매장 조회
     */
    public List<Store> getOperationalStores() {
        return stores.stream()
                .filter(Store::isOperational)
                .toList();
    }

    /**
     * 매장 소유 여부 확인
     */
    public boolean ownsStore(Long storeId) {
        return stores.stream()
                .anyMatch(store -> store.getStoreId().equals(storeId));
    }

    /**
     * 특정 매장 조회
     */
    public Store getStore(Long storeId) {
        return stores.stream()
                .filter(store -> store.getStoreId().equals(storeId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 매장을 찾을 수 없습니다: " + storeId));
    }

    /**
     * 매장을 가지고 있는지 확인
     */
    public boolean hasStores() {
        return !stores.isEmpty();
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

    private void validateStore(Store store) {
        if (store == null) {
            throw new IllegalArgumentException("매장 정보는 필수입니다.");
        }
    }
} 