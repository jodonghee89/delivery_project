package com.project.api.adapter.in.web.customer.dto;

import jakarta.validation.constraints.Size;

/**
 * 주소 수정 요청 DTO
 */
public record UpdateAddressRequest(
    
    @Size(max = 255, message = "주소는 255자 이하로 입력해주세요")
    String address,
    
    @Size(max = 255, message = "상세주소는 255자 이하로 입력해주세요")
    String addressDetail,
    
    @Size(min = 5, max = 6, message = "우편번호는 5-6자리로 입력해주세요")
    String zipCode,
    
    @Size(max = 50, message = "주소 별칭은 50자 이하로 입력해주세요")
    String nickname,
    
    Boolean isDefault
) {} 