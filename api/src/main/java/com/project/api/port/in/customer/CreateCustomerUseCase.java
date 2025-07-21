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
     * @param command 고객 생성 명령
     * @return 생성된 고객 정보
     */
    Customer createCustomer(CreateCustomerCommand command);
    
    /**
     * 고객 생성 명령 인터페이스
     * Controller의 Request DTO가 이 인터페이스를 구현하여 직접 전달
     */
    interface CreateCustomerCommand {
        String name();
        String email();
        String phoneNumber();
        String password();
        String address();
        String addressDetail();
        String zipCode();
    }
} 