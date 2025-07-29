package com.project.api.adapter.in.web.customer.dto;

import com.project.api.port.in.customer.LoginUseCase;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * 로그인 요청 DTO
 */
@Schema(description = "고객 로그인 요청")
public record LoginRequest(
    
    @Schema(description = "이메일 주소", example = "kim.delivery@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    String email,
    
    @Schema(description = "비밀번호", example = "mypassword123", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "비밀번호는 필수입니다")
    String password
) implements LoginUseCase.LoginCommand {} 