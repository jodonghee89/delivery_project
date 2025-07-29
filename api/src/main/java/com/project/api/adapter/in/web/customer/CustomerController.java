package com.project.api.adapter.in.web.customer;

import com.project.api.adapter.in.web.common.AuthenticationUtils;
import com.project.api.adapter.in.web.common.dto.ErrorResponse;
import com.project.api.adapter.in.web.customer.dto.*;
import com.project.api.application.service.customer.CustomerMapper;
import com.project.api.domain.customer.Customer;
import com.project.api.domain.customer.CustomerAddress;
import com.project.api.port.in.customer.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
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
    private final LoginUseCase loginUseCase;

    /**
     * 고객 회원가입
     */
    @Operation(summary = "고객 회원가입", description = "새로운 고객을 등록합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "회원가입 성공",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = CustomerResponse.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "이미 존재하는 이메일 또는 전화번호",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<?> createCustomer(
            @Valid @RequestBody CreateCustomerRequest request,
            HttpServletRequest httpRequest) {
        
        try {
            // Request DTO가 Command 인터페이스를 구현하므로 직접 전달
            Customer customer = createCustomerUseCase.createCustomer(request);
            CustomerResponse response = CustomerMapper.toResponse(customer);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (IllegalArgumentException e) {
            // 중복 이메일/전화번호 등 비즈니스 규칙 위반
            ErrorResponse errorResponse = ErrorResponse.badRequest(e.getMessage(), httpRequest.getRequestURI());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }
    }

    /**
     * 고객 로그인
     */
    @Operation(summary = "고객 로그인", description = "이메일과 패스워드로 고객 로그인을 처리합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "로그인 성공",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = LoginResponse.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "이메일 또는 비밀번호 불일치",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {
        
        try {
            // Request DTO가 LoginCommand 인터페이스를 구현하므로 직접 전달
            LoginUseCase.LoginResult result = loginUseCase.login(request);
            
            LoginResponse response = new LoginResponse(
                result.accessToken(),
                result.refreshToken(),
                result.customerId(),
                result.customerName()
            );
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            // 로그인 실패 시 구체적인 에러 메시지와 함께 401 Unauthorized 반환
            ErrorResponse errorResponse = ErrorResponse.unauthorized(
                "이메일 또는 비밀번호가 일치하지 않습니다.", 
                httpRequest.getRequestURI()
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    /**
     * 고객 정보 조회
     */
    @Operation(summary = "고객 정보 조회", description = "고객 ID로 고객 정보를 조회합니다. (본인만 조회 가능)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "고객 정보 조회 성공",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = CustomerResponse.class))),
        @ApiResponse(responseCode = "401", description = "인증 토큰 없음/유효하지 않음",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "다른 고객의 정보 접근 시도",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 고객",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{customerId}")
    public ResponseEntity<?> getCustomer(
            @Parameter(description = "고객 ID") @PathVariable Long customerId,
            HttpServletRequest httpRequest) {
        
        // 1. 인증 상태 체크
        if (!AuthenticationUtils.isAuthenticated()) {
            return AuthenticationUtils.createAuthenticationErrorResponse(httpRequest);
        }
        
        // 2. 본인 정보만 조회 가능하도록 권한 체크
        ResponseEntity<ErrorResponse> accessError = AuthenticationUtils.checkResourceAccess(
            httpRequest, customerId, "고객 정보"
        );
        if (accessError != null) {
            return accessError;
        }
        
        try {
            // 3. 비즈니스 로직 실행
            Customer customer = getCustomerUseCase.getCustomer(customerId);
            CustomerResponse response = CustomerMapper.toResponse(customer);
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            // 4. 고객이 존재하지 않는 경우
            ErrorResponse errorResponse = ErrorResponse.notFound(
                "존재하지 않는 고객입니다.", httpRequest.getRequestURI()
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    /**
     * 고객 정보 수정
     */
    @Operation(summary = "고객 정보 수정", description = "고객 정보를 수정합니다. (본인만 수정 가능)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "고객 정보 수정 성공",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = CustomerResponse.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "인증 토큰 없음/유효하지 않음",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "다른 고객의 정보 수정 시도",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 고객",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{customerId}")
    public ResponseEntity<?> updateCustomer(
            @Parameter(description = "고객 ID") @PathVariable Long customerId,
            @Valid @RequestBody UpdateCustomerRequest request,
            HttpServletRequest httpRequest) {
        
        // 1. 인증 상태 체크
        if (!AuthenticationUtils.isAuthenticated()) {
            return AuthenticationUtils.createAuthenticationErrorResponse(httpRequest);
        }
        
        // 2. 본인 정보만 수정 가능하도록 권한 체크
        ResponseEntity<ErrorResponse> accessError = AuthenticationUtils.checkResourceAccess(
            httpRequest, customerId, "고객 정보"
        );
        if (accessError != null) {
            return accessError;
        }
        
        try {
            // 3. 비즈니스 로직 실행
            Customer customer = updateCustomerUseCase.updateCustomer(request);
            CustomerResponse response = CustomerMapper.toResponse(customer);
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            // 4. 고객이 존재하지 않거나 수정 실패
            ErrorResponse errorResponse = ErrorResponse.notFound(
                "존재하지 않는 고객이거나 수정할 수 없습니다.", httpRequest.getRequestURI()
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    /**
     * 고객 탈퇴
     */
    @Operation(summary = "고객 탈퇴", description = "고객을 탈퇴시킵니다. (본인만 탈퇴 가능)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "고객 탈퇴 성공"),
        @ApiResponse(responseCode = "401", description = "인증 토큰 없음/유효하지 않음",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "다른 고객의 계정 탈퇴 시도",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 고객",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{customerId}")
    public ResponseEntity<?> deleteCustomer(
            @Parameter(description = "고객 ID") @PathVariable Long customerId,
            HttpServletRequest httpRequest) {
        
        // 1. 인증 상태 체크
        if (!AuthenticationUtils.isAuthenticated()) {
            return AuthenticationUtils.createAuthenticationErrorResponse(httpRequest);
        }
        
        // 2. 본인 계정만 탈퇴 가능하도록 권한 체크
        ResponseEntity<ErrorResponse> accessError = AuthenticationUtils.checkResourceAccess(
            httpRequest, customerId, "고객 계정"
        );
        if (accessError != null) {
            return accessError;
        }
        
        try {
            // 3. 비즈니스 로직 실행
            deleteCustomerUseCase.deleteCustomer(customerId);
            return ResponseEntity.noContent().build();
            
        } catch (RuntimeException e) {
            // 4. 고객이 존재하지 않거나 삭제 실패
            ErrorResponse errorResponse = ErrorResponse.notFound(
                "존재하지 않는 고객이거나 탈퇴할 수 없습니다.", httpRequest.getRequestURI()
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    /**
     * 고객의 배달 주소 목록 조회
     */
    @Operation(summary = "배달 주소 목록 조회", description = "고객의 모든 배달 주소를 조회합니다. (본인만 조회 가능)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "배달 주소 목록 조회 성공",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = CustomerAddressResponse.class))),
        @ApiResponse(responseCode = "401", description = "인증 토큰 없음/유효하지 않음",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "다른 고객의 주소 조회 시도",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{customerId}/addresses")
    public ResponseEntity<?> getCustomerAddresses(
            @Parameter(description = "고객 ID") @PathVariable Long customerId,
            HttpServletRequest httpRequest) {
        
        // 1. 인증 상태 체크
        if (!AuthenticationUtils.isAuthenticated()) {
            return AuthenticationUtils.createAuthenticationErrorResponse(httpRequest);
        }
        
        // 2. 본인 주소만 조회 가능하도록 권한 체크
        ResponseEntity<ErrorResponse> accessError = AuthenticationUtils.checkResourceAccess(
            httpRequest, customerId, "배달 주소"
        );
        if (accessError != null) {
            return accessError;
        }
        
        // 3. 비즈니스 로직 실행
        List<CustomerAddress> addresses = manageCustomerAddressUseCase.getCustomerAddresses(customerId);
        List<CustomerAddressResponse> responses = addresses.stream()
            .map(CustomerMapper::toAddressResponse)
            .toList();
        return ResponseEntity.ok(responses);
    }

    /**
     * 배달 주소 추가
     */
    @Operation(summary = "배달 주소 추가", description = "새로운 배달 주소를 추가합니다. (본인만 추가 가능)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "배달 주소 추가 성공",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = CustomerAddressResponse.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "인증 토큰 없음/유효하지 않음",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "다른 고객의 주소 추가 시도",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/{customerId}/addresses")
    public ResponseEntity<?> addCustomerAddress(
            @Parameter(description = "고객 ID") @PathVariable Long customerId,
            @Valid @RequestBody CreateAddressRequest request,
            HttpServletRequest httpRequest) {
        
        // 1. 인증 상태 체크
        if (!AuthenticationUtils.isAuthenticated()) {
            return AuthenticationUtils.createAuthenticationErrorResponse(httpRequest);
        }
        
        // 2. 본인 주소만 추가 가능하도록 권한 체크
        ResponseEntity<ErrorResponse> accessError = AuthenticationUtils.checkResourceAccess(
            httpRequest, customerId, "배달 주소"
        );
        if (accessError != null) {
            return accessError;
        }
        
        try {
            // 3. 비즈니스 로직 실행
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
            
        } catch (RuntimeException e) {
            // 4. 주소 추가 실패
            ErrorResponse errorResponse = ErrorResponse.badRequest(
                "배달 주소 추가에 실패했습니다: " + e.getMessage(), httpRequest.getRequestURI()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    /**
     * 배달 주소 수정
     */
    @Operation(summary = "배달 주소 수정", description = "기존 배달 주소를 수정합니다. (본인만 수정 가능)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "배달 주소 수정 성공",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = CustomerAddressResponse.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "인증 토큰 없음/유효하지 않음",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "다른 고객의 주소 수정 시도",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 주소",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{customerId}/addresses/{addressId}")
    public ResponseEntity<?> updateCustomerAddress(
            @Parameter(description = "고객 ID") @PathVariable Long customerId,
            @Parameter(description = "주소 ID") @PathVariable Long addressId,
            @Valid @RequestBody UpdateAddressRequest request,
            HttpServletRequest httpRequest) {
        
        // 1. 인증 상태 체크
        if (!AuthenticationUtils.isAuthenticated()) {
            return AuthenticationUtils.createAuthenticationErrorResponse(httpRequest);
        }
        
        // 2. 본인 주소만 수정 가능하도록 권한 체크
        ResponseEntity<ErrorResponse> accessError = AuthenticationUtils.checkResourceAccess(
            httpRequest, customerId, "배달 주소"
        );
        if (accessError != null) {
            return accessError;
        }
        
        try {
            // 3. 비즈니스 로직 실행
            CustomerAddress address = manageCustomerAddressUseCase.updateCustomerAddress(request);
            CustomerAddressResponse response = CustomerMapper.toAddressResponse(address);
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            // 4. 주소 수정 실패
            ErrorResponse errorResponse = ErrorResponse.notFound(
                "존재하지 않는 주소이거나 수정할 수 없습니다.", httpRequest.getRequestURI()
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    /**
     * 배달 주소 삭제
     */
    @Operation(summary = "배달 주소 삭제", description = "기존 배달 주소를 삭제합니다. (본인만 삭제 가능)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "배달 주소 삭제 성공"),
        @ApiResponse(responseCode = "401", description = "인증 토큰 없음/유효하지 않음",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "다른 고객의 주소 삭제 시도",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 주소",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{customerId}/addresses/{addressId}")
    public ResponseEntity<?> deleteCustomerAddress(
            @Parameter(description = "고객 ID") @PathVariable Long customerId,
            @Parameter(description = "주소 ID") @PathVariable Long addressId,
            HttpServletRequest httpRequest) {
        
        // 1. 인증 상태 체크
        if (!AuthenticationUtils.isAuthenticated()) {
            return AuthenticationUtils.createAuthenticationErrorResponse(httpRequest);
        }
        
        // 2. 본인 주소만 삭제 가능하도록 권한 체크
        ResponseEntity<ErrorResponse> accessError = AuthenticationUtils.checkResourceAccess(
            httpRequest, customerId, "배달 주소"
        );
        if (accessError != null) {
            return accessError;
        }
        
        try {
            // 3. 비즈니스 로직 실행
            manageCustomerAddressUseCase.deleteCustomerAddress(customerId, addressId);
            return ResponseEntity.noContent().build();
            
        } catch (RuntimeException e) {
            // 4. 주소 삭제 실패
            ErrorResponse errorResponse = ErrorResponse.notFound(
                "존재하지 않는 주소이거나 삭제할 수 없습니다.", httpRequest.getRequestURI()
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }
} 