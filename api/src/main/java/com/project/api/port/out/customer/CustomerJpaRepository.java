package com.project.api.port.out.customer;

import com.project.api.domain.customer.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 고객 JPA Repository 구현체
 */
@Repository
public interface CustomerJpaRepository extends JpaRepository<Customer, Long>, CustomerRepository {
    
    /**
     * 이메일로 고객을 조회합니다.
     */
    Optional<Customer> findByEmail(String email);
    
    /**
     * 전화번호로 고객을 조회합니다.
     */
    Optional<Customer> findByPhoneNumber(String phoneNumber);
    
    /**
     * 이메일 존재 여부를 확인합니다.
     */
    boolean existsByEmail(String email);
    
    /**
     * 전화번호 존재 여부를 확인합니다.
     */
    boolean existsByPhoneNumber(String phoneNumber);
} 