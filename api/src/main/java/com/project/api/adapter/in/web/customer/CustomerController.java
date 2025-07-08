package com.project.api.adapter.in.web.customer;

import com.project.api.adapter.in.web.customer.dto.*;
import com.project.api.application.service.customer.CustomerMapper;
import com.project.api.domain.customer.Customer;
import com.project.api.domain.customer.CustomerAddress;
import com.project.api.port.in.customer.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 고객 관련 REST API Controller
 */
@Tag(name = "Customer", description = "고객 관리 API")
@RestController
@RequestMapping("/delivery/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CreateCustomerUseCase createCustomerUseCase;
    private final GetCustomerUseCase getCustomerUseCase;
    private final UpdateCustomerUseCase updateCustomerUseCase;
    private final DeleteCustomerUseCase deleteCustomerUseCase;
    private final ManageCustomerAddressUseCase manageCustomerAddressUseCase;

    /**
     * 고객 회원가입
     */
    @Operation(summary = "고객 회원가입", description = "새로운 고객을 등록합니다.")
    @PostMapping
    public ResponseEntity<CustomerResponse> createCustomer(
            @Valid @RequestBody CreateCustomerRequest request) {
        // DTO를 Command로 변환
        CreateCustomerUseCase.CreateCustomerCommand command = 
            new CreateCustomerUseCase.CreateCustomerCommand(
                request.name(),
                request.email(),
                request.phoneNumber(),
                request.password(),
                request.address(),
                request.addressDetail(),
                request.zipCode()
            );
        
        Customer customer = createCustomerUseCase.createCustomer(command);
        CustomerResponse response = CustomerMapper.toResponse(customer);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 로그인 (임시 구현 - 실제로는 Security를 통해 처리)
     */
    @Operation(summary = "고객 로그인", description = "고객 로그인을 처리합니다.")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {
        // TODO: 실제 로그인 로직 구현 필요 (JWT 토큰 생성 등)
        LoginResponse response = new LoginResponse(
            "temporary_access_token", 
            "temporary_refresh_token", 
            1L, 
            "임시_고객명"
        );
        return ResponseEntity.ok(response);
    }

    /**
     * 고객 정보 조회
     */
    @Operation(summary = "고객 정보 조회", description = "고객 ID로 고객 정보를 조회합니다.")
    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerResponse> getCustomer(
            @Parameter(description = "고객 ID") @PathVariable Long customerId) {
        Customer customer = getCustomerUseCase.getCustomer(customerId);
        CustomerResponse response = CustomerMapper.toResponse(customer);
        return ResponseEntity.ok(response);
    }

    /**
     * 고객 정보 수정
     */
    @Operation(summary = "고객 정보 수정", description = "고객 정보를 수정합니다.")
    @PutMapping("/{customerId}")
    public ResponseEntity<CustomerResponse> updateCustomer(
            @Parameter(description = "고객 ID") @PathVariable Long customerId,
            @Valid @RequestBody UpdateCustomerRequest request) {
        Customer customer = updateCustomerUseCase.updateCustomer(
            customerId,
            request.name(),
            request.email(),
            request.phoneNumber()
        );
        CustomerResponse response = CustomerMapper.toResponse(customer);
        return ResponseEntity.ok(response);
    }

    /**
     * 고객 탈퇴
     */
    @Operation(summary = "고객 탈퇴", description = "고객을 탈퇴시킵니다.")
    @DeleteMapping("/{customerId}")
    public ResponseEntity<Void> deleteCustomer(
            @Parameter(description = "고객 ID") @PathVariable Long customerId) {
        deleteCustomerUseCase.deleteCustomer(customerId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 고객의 배달 주소 목록 조회
     */
    @Operation(summary = "배달 주소 목록 조회", description = "고객의 모든 배달 주소를 조회합니다.")
    @GetMapping("/{customerId}/addresses")
    public ResponseEntity<List<CustomerAddressResponse>> getCustomerAddresses(
            @Parameter(description = "고객 ID") @PathVariable Long customerId) {
        List<CustomerAddress> addresses = manageCustomerAddressUseCase.getCustomerAddresses(customerId);
        List<CustomerAddressResponse> responses = addresses.stream()
            .map(CustomerMapper::toAddressResponse)
            .toList();
        return ResponseEntity.ok(responses);
    }

    /**
     * 배달 주소 추가
     */
    @Operation(summary = "배달 주소 추가", description = "새로운 배달 주소를 추가합니다.")
    @PostMapping("/{customerId}/addresses")
    public ResponseEntity<CustomerAddressResponse> addCustomerAddress(
            @Parameter(description = "고객 ID") @PathVariable Long customerId,
            @Valid @RequestBody CreateAddressRequest request) {
        CustomerAddress address = manageCustomerAddressUseCase.addCustomerAddress(
            customerId,
            request.address(),
            request.addressDetail(),
            request.zipCode(),
            request.nickname(),
            request.isDefault()
        );
        CustomerAddressResponse response = CustomerMapper.toAddressResponse(address);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 배달 주소 수정
     */
    @Operation(summary = "배달 주소 수정", description = "기존 배달 주소를 수정합니다.")
    @PutMapping("/{customerId}/addresses/{addressId}")
    public ResponseEntity<CustomerAddressResponse> updateCustomerAddress(
            @Parameter(description = "고객 ID") @PathVariable Long customerId,
            @Parameter(description = "주소 ID") @PathVariable Long addressId,
            @Valid @RequestBody UpdateAddressRequest request) {
        CustomerAddress address = manageCustomerAddressUseCase.updateCustomerAddress(
            customerId,
            addressId,
            request.address(),
            request.addressDetail(),
            request.zipCode(),
            request.nickname(),
            request.isDefault()
        );
        CustomerAddressResponse response = CustomerMapper.toAddressResponse(address);
        return ResponseEntity.ok(response);
    }

    /**
     * 배달 주소 삭제
     */
    @Operation(summary = "배달 주소 삭제", description = "기존 배달 주소를 삭제합니다.")
    @DeleteMapping("/{customerId}/addresses/{addressId}")
    public ResponseEntity<Void> deleteCustomerAddress(
            @Parameter(description = "고객 ID") @PathVariable Long customerId,
            @Parameter(description = "주소 ID") @PathVariable Long addressId) {
        manageCustomerAddressUseCase.deleteCustomerAddress(customerId, addressId);
        return ResponseEntity.noContent().build();
    }
} 