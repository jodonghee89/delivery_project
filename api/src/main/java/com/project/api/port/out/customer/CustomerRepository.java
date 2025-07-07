package com.project.api.port.out.customer;

import com.project.api.domain.customer.Customer;
import java.util.Optional;

/**
 * 고객 Repository 인터페이스
 * 고객 데이터 영속성 계층의 추상화입니다.
 */
public interface CustomerRepository {
    
    /**
     * 고객을 저장합니다.
     * 
     * @param customer 저장할 고객 정보
     * @return 저장된 고객 정보
     */
    Customer save(Customer customer);
    
    /**
     * 고객 ID로 고객을 조회합니다.
     * 
     * @param customerId 고객 ID
     * @return 고객 정보 (Optional)
     */
    Optional<Customer> findById(Long customerId);
    
    /**
     * 이메일로 고객을 조회합니다.
     * 
     * @param email 이메일
     * @return 고객 정보 (Optional)
     */
    Optional<Customer> findByEmail(String email);
    
    /**
     * 전화번호로 고객을 조회합니다.
     * 
     * @param phoneNumber 전화번호
     * @return 고객 정보 (Optional)
     */
    Optional<Customer> findByPhoneNumber(String phoneNumber);
    
    /**
     * 이메일 존재 여부를 확인합니다.
     * 
     * @param email 확인할 이메일
     * @return 존재 여부
     */
    boolean existsByEmail(String email);
    
    /**
     * 전화번호 존재 여부를 확인합니다.
     * 
     * @param phoneNumber 확인할 전화번호
     * @return 존재 여부
     */
    boolean existsByPhoneNumber(String phoneNumber);
    
    /**
     * 고객을 삭제합니다.
     * 
     * @param customerId 삭제할 고객 ID
     */
    void deleteById(Long customerId);
} 