package com.project.api.adapter.in.web.customer.dto;

import com.project.api.port.in.customer.ManageCustomerAddressUseCase;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

/**
 * 주소 수정 요청 DTO
 */
@Schema(description = "고객 주소 수정 요청")
public record UpdateAddressRequest(

    @Schema(description = "고객 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    Long customerId,

    @Schema(description = "주소 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    Long addressId,

    @Schema(description = "주소", example = "서울특별시 강남구 테헤란로 123", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Size(max = 255, message = "주소는 255자 이하로 입력해주세요")
    String address,
    
    @Schema(description = "상세 주소", example = "456호", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Size(max = 255, message = "상세주소는 255자 이하로 입력해주세요")
    String addressDetail,
    
    @Schema(description = "우편번호", example = "06234", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Size(min = 5, max = 6, message = "우편번호는 5-6자리로 입력해주세요")
    String zipCode,
    
    @Schema(description = "주소 별칭", example = "회사", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Size(max = 50, message = "주소 별칭은 50자 이하로 입력해주세요")
    String nickname,
    
    @Schema(description = "기본 주소 여부", example = "false", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    Boolean isDefault
) implements ManageCustomerAddressUseCase.UpdateCustomerAddressCommand {}

