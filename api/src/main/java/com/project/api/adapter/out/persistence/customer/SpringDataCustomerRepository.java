package com.project.api.adapter.out.persistence.customer;

import com.project.api.domain.customer.Customer;
import com.project.api.port.out.customer.CustomerRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA Customer Repository
 * CustomerRepository 포트 인터페이스를 직접 구현
 */
@Repository
public interface SpringDataCustomerRepository extends JpaRepository<Customer, Long>, CustomerRepository {
    
    Optional<Customer> findByEmail(String email);
    
    Optional<Customer> findByPhone(String phone);
    
    boolean existsByEmail(String email);
    
    boolean existsByPhone(String phone);
    
    // CustomerRepository 인터페이스의 findByPhoneNumber를 findByPhone으로 매핑
    default Optional<Customer> findByPhoneNumber(String phoneNumber) {
        return findByPhone(phoneNumber);
    }
    
    // CustomerRepository 인터페이스의 existsByPhoneNumber를 existsByPhone으로 매핑  
    default boolean existsByPhoneNumber(String phoneNumber) {
        return existsByPhone(phoneNumber);
    }
} 