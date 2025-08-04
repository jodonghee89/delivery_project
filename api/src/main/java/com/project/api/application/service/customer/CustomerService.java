package com.project.api.application.service.customer;

import com.project.api.domain.customer.Customer;
import com.project.api.domain.customer.CustomerAddress;
import com.project.api.config.JwtTokenProvider;
import com.project.api.config.RefreshTokenRepository;
import com.project.api.port.in.customer.*;
import com.project.api.port.out.customer.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    ManageCustomerAddressUseCase,
    LoginUseCase,
    RefreshTokenUseCase {

    private final CustomerRepository customerRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;

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

        // 3. 패스워드 암호화
        String encodedPassword = passwordEncoder.encode(command.password());

        // 4. 고객 도메인 객체 생성 (암호화된 패스워드 사용)
        Customer customer = Customer.builder()
            .name(command.name())
            .email(command.email())
            .phoneNumber(command.phoneNumber())
            .password(encodedPassword)
            .build();

        // 5. 첫 번째 주소가 있다면 추가
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

        // 6. 저장
        return customerRepository.save(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public Customer getCustomer(Long customerId) {
        return customerRepository.findById(customerId)
            .orElseThrow(() -> new RuntimeException("고객을 찾을 수 없습니다: " + customerId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Customer getCustomerById(Long customerId) {
        return getCustomer(customerId); // 기존 메서드 재사용
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResult login(LoginCommand command) {
        // 1. 이메일로 고객 조회
        Customer customer = customerRepository.findByEmail(command.email())
                .orElseThrow(() -> new RuntimeException("이메일 또는 비밀번호가 일치하지 않습니다."));

        // 2. 패스워드 검증 (BCrypt 사용)
        if (!customer.isPasswordMatch(command.password(), passwordEncoder)) {
            throw new RuntimeException("이메일 또는 비밀번호가 일치하지 않습니다.");
        }

        // 3. JWT 토큰 생성
        String accessToken = jwtTokenProvider.generateAccessToken(customer.getCustomerId(), customer.getName());
        String refreshToken = jwtTokenProvider.generateRefreshToken(customer.getCustomerId());

        // 4. Refresh Token을 Redis에 저장 (7일 만료)
        long refreshTokenValidityInSeconds = 604800; // 7일을 초로 변환
        refreshTokenRepository.saveRefreshToken(customer.getCustomerId(), refreshToken, refreshTokenValidityInSeconds);

        // 5. 로그인 결과 반환
        return new LoginResult(accessToken, refreshToken, customer.getCustomerId(), customer.getName());
    }

    @Override
    public Customer updateCustomer(UpdateCustomerCommand command) {
        // 1. 고객 조회
        Customer customer = customerRepository.findById(command.customerId())
            .orElseThrow(() -> new RuntimeException("고객을 찾을 수 없습니다: " + command.customerId()));

        // 2. 이메일 중복 검증 (자신 제외)
        if (command.email() != null && !command.email().equals(customer.getEmail())) {
            if (customerRepository.existsByEmail(command.email())) {
                throw new RuntimeException("이미 존재하는 이메일입니다: " + command.email());
            }
        }

        // 3. 전화번호 중복 검증 (자신 제외)
        if (command.phoneNumber() != null && !command.phoneNumber().equals(customer.getPhoneNumber())) {
            if (customerRepository.existsByPhoneNumber(command.phoneNumber())) {
                throw new RuntimeException("이미 존재하는 전화번호입니다: " + command.phoneNumber());
            }
        }

        // 4. 고객 정보 업데이트
        customer.updateInfo(command.name(), command.email(), command.phoneNumber());

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
    public CustomerAddress updateCustomerAddress(UpdateCustomerAddressCommand request) {
        // 1. 고객 조회
        Customer customer = customerRepository.findById(request.customerId())
            .orElseThrow(() -> new RuntimeException("고객을 찾을 수 없습니다: " + request.customerId()));

        // 2. 주소 조회
        CustomerAddress customerAddress = customer.getAddresses()
            .stream()
            .filter(addr -> addr.getId().equals(request.addressId()))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("주소를 찾을 수 없습니다: " + request.addressId()));

        // 3. 기본 주소 변경 처리
        if (request.isDefault() != null && request.isDefault()) {
            customer.clearDefaultAddress();
        }

        // 4. 주소 정보 업데이트
        customerAddress.updateFromCommand(request);

        // 5. 저장 및 반환
        Customer savedCustomer = customerRepository.save(customer);

        return savedCustomer.getAddresses()
            .stream()
            .filter(addr -> addr.getId().equals(request.addressId()))
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

    // ===== RefreshTokenUseCase 구현 =====

    @Override
    @Transactional(readOnly = true)
    public RefreshTokenResult refreshToken(RefreshTokenCommand command) {
        // 1. Refresh Token에서 고객 ID 추출
        Long customerId;
        try {
            customerId = jwtTokenProvider.getCustomerIdFromToken(command.refreshToken());
        } catch (Exception e) {
            throw new RuntimeException("유효하지 않은 Refresh Token입니다.");
        }

        // 2. Refresh Token 유효성 검증 (JWT 자체 + Redis 저장 토큰 비교)
        if (!jwtTokenProvider.validateToken(command.refreshToken())) {
            throw new RuntimeException("만료되거나 유효하지 않은 Refresh Token입니다.");
        }

        if (!refreshTokenRepository.validateRefreshToken(customerId, command.refreshToken())) {
            throw new RuntimeException("저장된 Refresh Token과 일치하지 않습니다.");
        }

        // 3. 고객 정보 조회
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("고객을 찾을 수 없습니다: " + customerId));

        // 4. 새로운 토큰들 생성
        String newAccessToken = jwtTokenProvider.generateAccessToken(customer.getCustomerId(), customer.getName());
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(customer.getCustomerId());

        // 5. 새로운 Refresh Token을 Redis에 저장 (기존 토큰 덮어쓰기)
        long refreshTokenValidityInSeconds = 604800; // 7일
        refreshTokenRepository.saveRefreshToken(customer.getCustomerId(), newRefreshToken, refreshTokenValidityInSeconds);

        // 6. 갱신 결과 반환
        return new RefreshTokenResult(newAccessToken, newRefreshToken, customer.getCustomerId(), customer.getName());
    }

    @Override
    public void logout(Long customerId) {
        // Refresh Token 삭제로 로그아웃 처리
        refreshTokenRepository.deleteAllRefreshTokensForCustomer(customerId);
    }
}