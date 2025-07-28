package com.project.api.adapter.out.persistence.customer;

import com.project.api.domain.customer.Customer;
import com.project.api.port.out.customer.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 고객 Repository JPA 어댑터
 * 
 * 헥사고날 아키텍처에서 out 포트의 구현체
 * Spring Data JPA를 사용한 고객 데이터 접근
 */
@Repository
@RequiredArgsConstructor
public class CustomerJpaAdapter implements CustomerRepository {

    private final SpringDataCustomerRepository springDataRepository;

    @Override
    public Customer save(Customer customer) {
        return springDataRepository.save(customer);
    }

    @Override
    public Optional<Customer> findById(Long customerId) {
        return springDataRepository.findById(customerId);
    }

    @Override
    public List<Customer> findAll() {
        return springDataRepository.findAll();
    }

    @Override
    public Optional<Customer> findByEmail(String email) {
        return springDataRepository.findByEmail(email);
    }

    @Override
    public Optional<Customer> findByPhoneNumber(String phoneNumber) {
        // 실제 Repository 메서드명은 엔티티 필드명(phone)을 기준으로 함
        return springDataRepository.findByPhone(phoneNumber);
    }

    @Override
    public boolean existsByEmail(String email) {
        return springDataRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        // 실제 Repository 메서드명은 엔티티 필드명(phone)을 기준으로 함
        return springDataRepository.existsByPhone(phoneNumber);
    }

    @Override
    public void deleteById(Long customerId) {
        springDataRepository.deleteById(customerId);
    }
} 