package com.project.api.port.in.customer;

/**
 * 고객 삭제 Use Case
 * 고객 탈퇴 기능을 제공합니다.
 */
public interface DeleteCustomerUseCase {
    
    /**
     * 고객을 삭제(탈퇴)합니다.
     * 
     * @param customerId 삭제할 고객 ID
     * @throws CustomerNotFoundException 고객을 찾을 수 없는 경우
     * @throws CustomerHasActiveOrdersException 진행 중인 주문이 있는 경우
     */
    void deleteCustomer(Long customerId);
} 