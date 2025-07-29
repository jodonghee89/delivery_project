package com.project.api.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * JWT 인증 필터
 * 
 * HTTP 요청에서 JWT 토큰을 추출하고 검증하여 
 * Spring Security 컨텍스트에 인증 정보를 설정합니다.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    
    /**
     * Authorization 헤더 접두사
     */
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, 
            HttpServletResponse response, 
            FilterChain filterChain) throws ServletException, IOException {

        try {
            // 1. HTTP 요청에서 JWT 토큰 추출
            String jwt = extractJwtFromRequest(request);

            // 2. 토큰 존재 여부 및 유효성 검사
            if (StringUtils.hasText(jwt)) {
                if (jwtTokenProvider.validateToken(jwt)) {
                    // 토큰에서 사용자 정보 추출
                    Long customerId = jwtTokenProvider.getCustomerIdFromToken(jwt);
                    String customerName = jwtTokenProvider.getCustomerNameFromToken(jwt);

                    // Spring Security 인증 객체 생성
                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                        customerId,           // principal (사용자 식별자)
                        null,                // credentials (패스워드 - JWT에서는 불필요)
                        Collections.emptyList() // authorities (권한 - 추후 역할 기반 권한 추가 예정)
                    );

                    // Security Context에 인증 정보 설정
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    
                    log.debug("JWT 인증 성공: customerId={}, customerName={}", customerId, customerName);
                } else {
                    log.warn("유효하지 않은 JWT 토큰 - URI: {}, Token: {}...", 
                            request.getRequestURI(), 
                            jwt.length() > 10 ? jwt.substring(0, 10) : jwt);
                }
            } else {
                // 토큰이 없는 경우 - 공개 API가 아니라면 나중에 AuthenticationEntryPoint에서 처리됨
                log.debug("JWT 토큰 없음 - URI: {}", request.getRequestURI());
            }
        } catch (Exception e) {
            // JWT 토큰 처리 중 오류 발생 시 상세 로그 기록
            log.error("JWT 토큰 처리 중 예외 발생 - URI: {}, Error: {}", 
                     request.getRequestURI(), e.getMessage(), e);
            SecurityContextHolder.clearContext();
        }

        // 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }

    /**
     * HTTP 요청에서 JWT 토큰 추출
     * 
     * @param request HTTP 요청
     * @return JWT 토큰 (Bearer 접두사 제거된 순수 토큰)
     */
    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        
        return null;
    }
} 