package com.project.api.domain.delivery;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 배달원 도메인 Entity
 */
@Entity
@Table(name = "delivery_persons")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class DeliveryPerson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delivery_person_id")
    private Long deliveryPersonId;

    @Column(name = "email", length = 100, nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "vehicle_type", length = 30)
    private String vehicleType;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Builder
    private DeliveryPerson(String email, String password, String name, String phone, String vehicleType) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.vehicleType = vehicleType;
    }

    /**
     * ID 반환 (외부 인터페이스 일관성을 위해)
     */
    public Long getId() {
        return deliveryPersonId;
    }

    public void updateInfo(String name, String phone, String vehicleType) {
        this.name = name;
        this.phone = phone;
        this.vehicleType = vehicleType;
    }

    public void changePassword(String oldPassword, String newPassword) {
        if (!this.password.equals(oldPassword)) {
            throw new IllegalArgumentException("기존 비밀번호가 일치하지 않습니다.");
        }
        this.password = newPassword;
    }
} 