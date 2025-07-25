package com.project.api.adapter.in.web.customer.dto;

import com.project.api.port.in.customer.CreateCustomerUseCase;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 고객 생성 요청 DTO
 * CreateCustomerCommand 인터페이스를 구현하여 직접 UseCase에 전달 가능
 */
@Schema(description = "고객 생성 요청")
public record CreateCustomerRequest(
    
    @Schema(description = "고객 이름", example = "김배달", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "이름은 필수입니다")
    @Size(min = 2, max = 50, message = "이름은 2자 이상 50자 이하로 입력해주세요")
    String name,
    
    @Schema(description = "이메일 주소", example = "kim.delivery@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    String email,
    
    @Schema(description = "휴대폰 번호", example = "010-1234-5678", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "전화번호는 필수입니다")
    @Pattern(regexp = "^010-\\d{4}-\\d{4}$", message = "전화번호 형식이 올바르지 않습니다 (010-XXXX-XXXX)")
    String phoneNumber,
    
    @Schema(description = "비밀번호", example = "mypassword123", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "비밀번호는 필수입니다")
    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요")
    String password,
    
    @Schema(description = "기본 배달 주소", example = "서울특별시 강남구 테헤란로 123", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    // 첫 번째 배달 주소 (선택사항)
    String address,
    
    @Schema(description = "상세 주소", example = "456호", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    String addressDetail,
    
    @Schema(description = "우편번호", example = "06234", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    String zipCode
) implements CreateCustomerUseCase.CreateCustomerCommand {} 