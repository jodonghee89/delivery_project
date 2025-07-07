package com.project.api.adapter.in.web.customer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 고객 수정 요청 DTO
 */
public record UpdateCustomerRequest(
    
    @Size(min = 2, max = 50, message = "이름은 2자 이상 50자 이하로 입력해주세요")
    String name,
    
    @Email(message = "올바른 이메일 형식이 아닙니다")
    String email,
    
    @Pattern(regexp = "^010-\\d{4}-\\d{4}$", message = "전화번호 형식이 올바르지 않습니다 (010-XXXX-XXXX)")
    String phoneNumber
) {} 