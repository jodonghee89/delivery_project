package com.project.api.adapter.out.persistence.customer;

import com.project.api.domain.customer.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA Repository 인터페이스
 * 
 * Customer 엔티티에 대한 데이터베이스 접근을 담당합니다.
 * Spring Data JPA가 자동으로 구현체를 생성하여 빈으로 등록합니다.
 */
@Repository
interface SpringDataCustomerRepository extends JpaRepository<Customer, Long> {
    
    /**
     * 이메일로 고객 조회
     */
    Optional<Customer> findByEmail(String email);
    
    /**
     * 전화번호로 고객 조회 (엔티티 필드명 기준: phone)
     */
    Optional<Customer> findByPhone(String phone);
    
    /**
     * 이메일 존재 여부 확인
     */
    boolean existsByEmail(String email);
    
    /**
     * 전화번호 존재 여부 확인 (엔티티 필드명 기준: phone)
     */
    boolean existsByPhone(String phone);
} 