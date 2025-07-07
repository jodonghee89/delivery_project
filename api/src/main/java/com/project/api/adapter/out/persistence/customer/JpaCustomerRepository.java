package com.project.api.adapter.out.persistence.customer;

import com.project.api.domain.customer.Customer;
import com.project.api.port.out.customer.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA Customer Repository
 * CustomerRepository 인터페이스의 구현체
 */
@Repository
@RequiredArgsConstructor
public class JpaCustomerRepository implements CustomerRepository {

    private final SpringDataCustomerRepository springDataCustomerRepository;

    @Override
    public Customer save(Customer customer) {
        return springDataCustomerRepository.save(customer);
    }

    @Override
    public Optional<Customer> findById(Long id) {
        return springDataCustomerRepository.findById(id);
    }

    @Override
    public Optional<Customer> findByEmail(String email) {
        return springDataCustomerRepository.findByEmail(email);
    }

    @Override
    public Optional<Customer> findByPhoneNumber(String phoneNumber) {
        return springDataCustomerRepository.findByPhone(phoneNumber);
    }

    @Override
    public boolean existsByEmail(String email) {
        return springDataCustomerRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        return springDataCustomerRepository.existsByPhone(phoneNumber);
    }

    @Override
    public void deleteById(Long id) {
        springDataCustomerRepository.deleteById(id);
    }
} 