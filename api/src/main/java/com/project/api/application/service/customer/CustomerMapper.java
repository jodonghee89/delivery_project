package com.project.api.application.service.customer;

import com.project.api.adapter.in.web.customer.dto.CustomerAddressResponse;
import com.project.api.adapter.in.web.customer.dto.CustomerResponse;
import com.project.api.domain.customer.Customer;
import com.project.api.domain.customer.CustomerAddress;

import java.util.List;

/**
 * Customer 도메인 객체와 DTO 간 변환 Mapper
 */
public class CustomerMapper {

    /**
     * Customer 도메인 객체를 CustomerResponse DTO로 변환
     */
    public static CustomerResponse toResponse(Customer customer) {
        List<CustomerAddressResponse> addresses = customer.getAddresses().stream()
            .map(CustomerMapper::toAddressResponse)
            .toList();

        return new CustomerResponse(
            customer.getId(),
            customer.getName(),
            customer.getEmail(),
            customer.getPhoneNumber(),
            customer.getCreatedAt(),
            addresses
        );
    }

    /**
     * CustomerAddress 도메인 객체를 CustomerAddressResponse DTO로 변환
     */
    public static CustomerAddressResponse toAddressResponse(CustomerAddress address) {
        return new CustomerAddressResponse(
            address.getId(),
            address.getAddress(),
            address.getAddressDetail(),
            address.getZipCode(),
            address.getNickname(),
            address.isDefault()
        );
    }
} 