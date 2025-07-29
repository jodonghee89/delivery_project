package com.project.api.adapter.in.web.common;

import com.project.api.adapter.in.web.common.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;

/**
 * 컨트롤러에서 인증 상태를 체크하는 유틸리티 클래스
 * 
 * SecurityContext 기반으로 인증 상태를 확인하고
 * 적절한 에러 응답을 생성합니다.
 */
@Slf4j
public class AuthenticationUtils {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    /**
     * 현재 요청이 인증되었는지 확인
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() 
               && !"anonymousUser".equals(authentication.getPrincipal());
    }

    /**
     * 현재 인증된 고객 ID 반환
     */
    public static Long getCurrentCustomerId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Long customerId) {
            return customerId;
        }
        return null;
    }

    /**
     * 인증 실패 시 적절한 에러 응답 생성
     * 
     * @param request HTTP 요청 객체
     * @return 인증 실패 응답 (401 Unauthorized)
     */
    public static ResponseEntity<ErrorResponse> createAuthenticationErrorResponse(HttpServletRequest request) {
        String requestPath = request.getRequestURI();
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);

        ErrorResponse errorResponse;

        if (!StringUtils.hasText(authHeader)) {
            // Case 1: Authorization 헤더 없음
            log.warn("인증 토큰 누락 - Path: {}, IP: {}", requestPath, request.getRemoteAddr());
            errorResponse = ErrorResponse.jwtMissing(requestPath);
            
        } else if (!authHeader.startsWith(BEARER_PREFIX)) {
            // Case 2: 잘못된 토큰 형식
            log.warn("잘못된 토큰 형식 - Path: {}, Header: {}", requestPath, authHeader);
            errorResponse = ErrorResponse.unauthorized(
                "잘못된 토큰 형식입니다. 'Bearer <token>' 형식으로 입력해주세요.", 
                requestPath
            );
            
        } else {
            // Case 3: 토큰은 있지만 유효하지 않음 (만료, 잘못된 서명 등)
            String token = authHeader.substring(BEARER_PREFIX.length());
            String maskedToken = token.length() > 10 ? token.substring(0, 10) + "..." : token;
            log.warn("유효하지 않은 토큰 - Path: {}, Token: {}", requestPath, maskedToken);
            errorResponse = ErrorResponse.jwtInvalid(requestPath);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    /**
     * 권한 부족 에러 응답 생성
     * 
     * @param request HTTP 요청 객체
     * @param message 권한 부족 메시지
     * @return 권한 부족 응답 (403 Forbidden)
     */
    public static ResponseEntity<ErrorResponse> createForbiddenResponse(HttpServletRequest request, String message) {
        String requestPath = request.getRequestURI();
        log.warn("권한 부족 - Path: {}, Message: {}, CustomerId: {}", 
                requestPath, message, getCurrentCustomerId());
        
        ErrorResponse errorResponse = ErrorResponse.forbidden(message, requestPath);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    /**
     * 리소스 접근 권한 체크
     * 
     * @param resourceOwnerId 리소스 소유자 ID
     * @return 접근 권한 여부
     */
    public static boolean hasAccessToResource(Long resourceOwnerId) {
        Long currentCustomerId = getCurrentCustomerId();
        return currentCustomerId != null && currentCustomerId.equals(resourceOwnerId);
    }

    /**
     * 현재 고객이 리소스에 접근할 권한이 있는지 체크하고, 없으면 403 응답 반환
     * 
     * @param request HTTP 요청 객체
     * @param resourceOwnerId 리소스 소유자 ID
     * @param resourceType 리소스 타입 (예: "고객 정보", "주문")
     * @return 권한 없을 시 403 응답, 권한 있을 시 null
     */
    public static ResponseEntity<ErrorResponse> checkResourceAccess(
            HttpServletRequest request, Long resourceOwnerId, String resourceType) {
        
        if (!hasAccessToResource(resourceOwnerId)) {
            String message = String.format("다른 고객의 %s에 접근할 수 없습니다.", resourceType);
            return createForbiddenResponse(request, message);
        }
        return null; // 권한 있음
    }
} 