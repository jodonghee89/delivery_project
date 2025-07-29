package com.project.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 패스워드 암호화 설정
 */
@Configuration
public class PasswordEncoderConfig {

    /**
     * BCrypt 패스워드 인코더 빈 등록
     * 
     * BCrypt는 강력한 해시 함수로 다음과 같은 특징이 있습니다:
     * - Salt 자동 생성 (같은 패스워드도 다른 해시값 생성)
     * - 적응형 비용 (브루트포스 공격에 대한 저항성)
     * - Spring Security 표준 권장
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
} 