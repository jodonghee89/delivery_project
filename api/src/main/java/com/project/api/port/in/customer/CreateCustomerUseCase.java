package com.project.api.port.in.customer;

import com.project.api.domain.customer.Customer;

/**
 * 고객 생성 Use Case
 * 고객 회원가입 기능을 제공합니다.
 */
public interface CreateCustomerUseCase {
    
    /**
     * 새로운 고객을 생성합니다.
     * 
     * @param name 고객 이름
     * @param email 고객 이메일
     * @param phoneNumber 고객 전화번호
     * @param password 고객 비밀번호
     * @param address 첫 번째 주소 (선택)
     * @param addressDetail 주소 상세 (선택)
     * @param zipCode 우편번호 (선택)
     * @return 생성된 고객 정보
     */
    Customer createCustomer(String name, String email, String phoneNumber, String password, String address, String addressDetail, String zipCode);
} 