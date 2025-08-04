package com.project.api.port.in.customer;

/**
 * Refresh Token 갱신 UseCase
 * 
 * Refresh Token을 사용하여 새로운 Access Token을 발급합니다.
 */
public interface RefreshTokenUseCase {

    /**
     * Access Token 갱신
     * 
     * @param command Refresh Token 갱신 명령
     * @return 갱신된 토큰 정보
     */
    RefreshTokenResult refreshToken(RefreshTokenCommand command);

    /**
     * Refresh Token 갱신 명령
     */
    interface RefreshTokenCommand {
        String refreshToken();
    }

    /**
     * Refresh Token 갱신 결과
     */
    record RefreshTokenResult(
        String accessToken,
        String refreshToken,
        Long customerId,
        String customerName
    ) {}

    /**
     * 로그아웃 (Refresh Token 무효화)
     * 
     * @param customerId 고객 ID
     */
    void logout(Long customerId);
} 