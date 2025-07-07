package com.project.api.port.in.customer;

import com.project.api.domain.customer.Customer;

/**
 * 고객 수정 Use Case
 * 고객 정보 수정 기능을 제공합니다.
 */
public interface UpdateCustomerUseCase {
    
    /**
     * 고객 정보를 수정합니다.
     * 
     * @param customerId 수정할 고객 ID
     * @param name 수정할 이름 (null이면 수정하지 않음)
     * @param email 수정할 이메일 (null이면 수정하지 않음)
     * @param phoneNumber 수정할 전화번호 (null이면 수정하지 않음)
     * @return 수정된 고객 정보
     */
    Customer updateCustomer(Long customerId, String name, String email, String phoneNumber);
} 