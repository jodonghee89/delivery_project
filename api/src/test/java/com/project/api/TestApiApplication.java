package com.project.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

/**
 * 테스트용 API 애플리케이션
 * core 모듈의 DataSourceConfig를 제외하고 H2 사용
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.project.api"}) // core 제외
@EntityScan(basePackages = "com.project.api.domain")
public class TestApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestApiApplication.class, args);
    }
} 