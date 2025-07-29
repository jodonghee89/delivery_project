package com.project.api.port.in.customer;

/**
 * 고객 로그인 Use Case
 * 고객 인증 및 로그인 기능을 제공합니다.
 */
public interface LoginUseCase {
    
    /**
     * 고객 로그인을 처리합니다.
     * 
     * @param command 로그인 명령
     * @return 로그인 결과 (토큰 정보 포함)
     */
    LoginResult login(LoginCommand command);
    
    /**
     * 로그인 명령 인터페이스
     * Controller의 Request DTO가 이 인터페이스를 구현하여 직접 전달
     */
    interface LoginCommand {
        String email();
        String password();
    }
    
    /**
     * 로그인 결과
     */
    record LoginResult(
        String accessToken,
        String refreshToken,
        Long customerId,
        String customerName
    ) {}
} 