package com.project.api.port.in.customer;

import com.project.api.domain.customer.Customer;

/**
 * 고객 수정 Use Case
 * 고객 정보 수정 기능을 제공합니다.
 */
public interface UpdateCustomerUseCase {

    Customer updateCustomer(UpdateCustomerCommand command);

    interface UpdateCustomerCommand {

        Long customerId();

        String name();

        String email();

        String phoneNumber();
    }
}