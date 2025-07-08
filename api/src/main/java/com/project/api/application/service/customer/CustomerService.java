package com.project.api.application.service.customer;

import com.project.api.domain.customer.Customer;
import com.project.api.domain.customer.CustomerAddress;
import com.project.api.port.in.customer.*;
import com.project.api.port.out.customer.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 고객 Application Service
 * 고객 관련 비즈니스 로직을 처리합니다.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class CustomerService implements 
    CreateCustomerUseCase,
    GetCustomerUseCase,
    UpdateCustomerUseCase,
    DeleteCustomerUseCase,
    ManageCustomerAddressUseCase {

    private final CustomerRepository customerRepository;

    @Override
    public Customer createCustomer(CreateCustomerCommand command) {
        // 1. 이메일 중복 검증
        if (customerRepository.existsByEmail(command.email())) {
            throw new RuntimeException("이미 존재하는 이메일입니다: " + command.email());
        }

        // 2. 전화번호 중복 검증
        if (customerRepository.existsByPhoneNumber(command.phoneNumber())) {
            throw new RuntimeException("이미 존재하는 전화번호입니다: " + command.phoneNumber());
        }

        // 3. 고객 도메인 객체 생성
        Customer customer = Customer.builder()
            .name(command.name())
            .email(command.email())
            .phoneNumber(command.phoneNumber())
            .password(command.password()) // TODO: 실제로는 암호화 필요
            .build();

        // 4. 첫 번째 주소가 있다면 추가
        if (command.address() != null && !command.address().isBlank()) {
            CustomerAddress firstAddress = CustomerAddress.builder()
                .address(command.address())
                .addressDetail(command.addressDetail())
                .zipCode(command.zipCode())
                .nickname("기본 주소")
                .isDefault(true)
                .build();
            customer.addAddress(firstAddress);
        }

        // 5. 저장
        return customerRepository.save(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public Customer getCustomer(Long customerId) {
        return customerRepository.findById(customerId)
            .orElseThrow(() -> new RuntimeException("고객을 찾을 수 없습니다: " + customerId));
    }

    @Override
    public Customer updateCustomer(Long customerId, String name, String email, String phoneNumber) {
        // 1. 고객 조회
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new RuntimeException("고객을 찾을 수 없습니다: " + customerId));

        // 2. 이메일 중복 검증 (자신 제외)
        if (email != null && !email.equals(customer.getEmail())) {
            if (customerRepository.existsByEmail(email)) {
                throw new RuntimeException("이미 존재하는 이메일입니다: " + email);
            }
        }

        // 3. 전화번호 중복 검증 (자신 제외)
        if (phoneNumber != null && !phoneNumber.equals(customer.getPhoneNumber())) {
            if (customerRepository.existsByPhoneNumber(phoneNumber)) {
                throw new RuntimeException("이미 존재하는 전화번호입니다: " + phoneNumber);
            }
        }

        // 4. 고객 정보 업데이트
        customer.updateInfo(name, email, phoneNumber);

        // 5. 저장 및 반환
        return customerRepository.save(customer);
    }

    @Override
    public void deleteCustomer(Long customerId) {
        // 1. 고객 존재 여부 확인
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new RuntimeException("고객을 찾을 수 없습니다: " + customerId));

        // 2. 활성 주문 검증 (TODO: OrderRepository 의존성 필요)
        // if (orderRepository.countActiveOrdersByCustomerId(customerId) > 0) {
        //     throw new RuntimeException("진행 중인 주문이 있어 탈퇴할 수 없습니다.");
        // }

        // 3. 고객 삭제
        customerRepository.deleteById(customerId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerAddress> getCustomerAddresses(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new RuntimeException("고객을 찾을 수 없습니다: " + customerId));

        return customer.getAddresses();
    }

    @Override
    public CustomerAddress addCustomerAddress(Long customerId, String address, String addressDetail, String zipCode, String nickname, Boolean isDefault) {
        // 1. 고객 조회
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new RuntimeException("고객을 찾을 수 없습니다: " + customerId));

        // 2. 주소 개수 제한 검증
        if (customer.getAddresses().size() >= 10) {
            throw new RuntimeException("주소는 최대 10개까지만 등록할 수 있습니다.");
        }

        // 3. 기본 주소 설정 검증
        boolean shouldBeDefault = isDefault != null ? isDefault : false;
        if (shouldBeDefault || customer.getAddresses().isEmpty()) {
            customer.clearDefaultAddress(); // 기존 기본 주소 해제
            shouldBeDefault = true;
        }

        // 4. 주소 생성
        CustomerAddress newAddress = CustomerAddress.builder()
            .address(address)
            .addressDetail(addressDetail)
            .zipCode(zipCode)
            .nickname(nickname)
            .isDefault(shouldBeDefault)
            .build();

        // 5. 고객에 주소 추가
        customer.addAddress(newAddress);

        // 6. 저장
        Customer savedCustomer = customerRepository.save(customer);

        // 7. 새로 추가된 주소 반환
        return savedCustomer.getAddresses()
            .stream()
            .filter(addr -> addr.getAddress().equals(address) &&
                           addr.getZipCode().equals(zipCode))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("주소 추가 실패"));
    }

    @Override
    public CustomerAddress updateCustomerAddress(Long customerId, Long addressId, String address, String addressDetail, String zipCode, String nickname, Boolean isDefault) {
        // 1. 고객 조회
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new RuntimeException("고객을 찾을 수 없습니다: " + customerId));

        // 2. 주소 조회
        CustomerAddress customerAddress = customer.getAddresses()
            .stream()
            .filter(addr -> addr.getId().equals(addressId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("주소를 찾을 수 없습니다: " + addressId));

        // 3. 기본 주소 변경 처리
        if (isDefault != null && isDefault) {
            customer.clearDefaultAddress();
        }

        // 4. 주소 정보 업데이트
        customerAddress.updateAddress(address, addressDetail, zipCode, nickname, isDefault);

        // 5. 저장 및 반환
        Customer savedCustomer = customerRepository.save(customer);

        return savedCustomer.getAddresses()
            .stream()
            .filter(addr -> addr.getId().equals(addressId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("주소 업데이트 실패"));
    }

    @Override
    public void deleteCustomerAddress(Long customerId, Long addressId) {
        // 1. 고객 조회
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new RuntimeException("고객을 찾을 수 없습니다: " + customerId));

        // 2. 주소 조회
        CustomerAddress address = customer.getAddresses()
            .stream()
            .filter(addr -> addr.getId().equals(addressId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("주소를 찾을 수 없습니다: " + addressId));

        // 3. 기본 주소 삭제 방지
        if (address.isDefault() && customer.getAddresses().size() > 1) {
            throw new RuntimeException("기본 주소는 다른 주소를 기본 주소로 설정한 후 삭제할 수 있습니다.");
        }

        // 4. 주소 삭제
        customer.removeAddress(address);

        // 5. 저장
        customerRepository.save(customer);
    }
} 