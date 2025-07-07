package com.project.api.port.in.customer;

import com.project.api.domain.customer.CustomerAddress;
import java.util.List;

/**
 * 고객 주소 관리 Use Case
 * 고객의 배달 주소 관리 기능을 제공합니다.
 */
public interface ManageCustomerAddressUseCase {
    
    /**
     * 고객의 모든 배달 주소를 조회합니다.
     * 
     * @param customerId 고객 ID
     * @return 고객의 배달 주소 목록
     */
    List<CustomerAddress> getCustomerAddresses(Long customerId);
    
    /**
     * 새로운 배달 주소를 추가합니다.
     * 
     * @param customerId 고객 ID
     * @param address 주소
     * @param addressDetail 상세 주소
     * @param zipCode 우편번호
     * @param nickname 주소 별칭
     * @param isDefault 기본 주소 여부
     * @return 추가된 주소 정보
     */
    CustomerAddress addCustomerAddress(Long customerId, String address, String addressDetail, String zipCode, String nickname, Boolean isDefault);
    
    /**
     * 배달 주소를 수정합니다.
     * 
     * @param customerId 고객 ID
     * @param addressId 주소 ID
     * @param address 수정할 주소 (null이면 수정하지 않음)
     * @param addressDetail 수정할 상세 주소 (null이면 수정하지 않음)
     * @param zipCode 수정할 우편번호 (null이면 수정하지 않음)
     * @param nickname 수정할 주소 별칭 (null이면 수정하지 않음)
     * @param isDefault 기본 주소 여부 (null이면 수정하지 않음)
     * @return 수정된 주소 정보
     */
    CustomerAddress updateCustomerAddress(Long customerId, Long addressId, String address, String addressDetail, String zipCode, String nickname, Boolean isDefault);
    
    /**
     * 배달 주소를 삭제합니다.
     * 
     * @param customerId 고객 ID
     * @param addressId 주소 ID
     */
    void deleteCustomerAddress(Long customerId, Long addressId);
} 