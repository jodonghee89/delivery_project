package com.project.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 관리자 대시보드 웹 애플리케이션
 * 
 * ⚠️ 아키텍처 참고사항:
 * - 현재는 MSA 환경에서 임시 관리자 도구로 사용
 * - 프로덕션에서는 별도 Admin Frontend (React/Vue) 권장
 * - 사용자용 UI는 SPA로 분리 필요
 * 
 * 담당 기능:
 * - 시스템 관리자 대시보드
 * - 데이터 조회/관리 도구
 * - API 테스트 인터페이스
 * 
 * 포트: 8081 (API 서버 8080과 분리)
 */
@SpringBootApplication(scanBasePackages = "com.project.web")
public class WebApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }
} 