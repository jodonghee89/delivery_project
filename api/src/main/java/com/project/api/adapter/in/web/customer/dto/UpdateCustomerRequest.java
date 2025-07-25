package com.project.api.adapter.in.web.customer.dto;

import com.project.api.port.in.customer.UpdateCustomerUseCase;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 고객 수정 요청 DTO
 */
@Schema(description = "고객 정보 수정 요청")
public record UpdateCustomerRequest(
    @Schema(description = "고객 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    Long customerId,

    @Schema(description = "고객 이름", example = "김배달", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Size(min = 2, max = 50, message = "이름은 2자 이상 50자 이하로 입력해주세요")
    String name,
    
    @Schema(description = "이메일 주소", example = "kim.delivery@example.com", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Email(message = "올바른 이메일 형식이 아닙니다")
    String email,
    
    @Schema(description = "휴대폰 번호", example = "010-1234-5678", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Pattern(regexp = "^010-\\d{4}-\\d{4}$", message = "전화번호 형식이 올바르지 않습니다 (010-XXXX-XXXX)")
    String phoneNumber
) implements UpdateCustomerUseCase.UpdateCustomerCommand {}