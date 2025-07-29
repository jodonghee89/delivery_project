package com.project.api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 인증 실패 시 처리하는 EntryPoint
 * 
 * 인증이 필요한 API에 토큰 없이 접근하거나 
 * 유효하지 않은 토큰으로 접근할 때 호출됩니다.
 */
@Component
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, 
                        HttpServletResponse response,
                        AuthenticationException authException) throws IOException {
        
        log.warn("인증되지 않은 요청 - IP: {}, URI: {}, Message: {}", 
                request.getRemoteAddr(), 
                request.getRequestURI(), 
                authException.getMessage());

        // 응답 헤더 설정
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // 에러 응답 생성
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        errorResponse.put("status", 401);
        errorResponse.put("error", "Unauthorized");
        errorResponse.put("message", determineErrorMessage(request));
        errorResponse.put("path", request.getRequestURI());

        // JSON 응답 전송
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

    /**
     * 요청 상황에 따른 적절한 에러 메시지 결정
     */
    private String determineErrorMessage(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null) {
            return "인증 토큰이 필요합니다. Authorization 헤더에 'Bearer <token>' 형식으로 토큰을 포함해주세요.";
        } else if (!authHeader.startsWith("Bearer ")) {
            return "잘못된 토큰 형식입니다. 'Bearer <token>' 형식으로 입력해주세요.";
        } else {
            return "유효하지 않거나 만료된 토큰입니다. 다시 로그인해주세요.";
        }
    }
} 