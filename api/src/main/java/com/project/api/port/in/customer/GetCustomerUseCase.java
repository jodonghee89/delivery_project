package com.project.api.port.in.customer;

import com.project.api.domain.customer.Customer;

/**
 * 고객 조회 Use Case
 * 고객 정보 조회 기능을 제공합니다.
 */
public interface GetCustomerUseCase {
    
    /**
     * 고객 ID로 고객 정보를 조회합니다.
     * 
     * @param customerId 조회할 고객 ID
     * @return 고객 정보
     */
    Customer getCustomer(Long customerId);
} 