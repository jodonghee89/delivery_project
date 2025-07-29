package com.project.api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Delivery API 설정 정보
 * 
 * Swagger UI 접속 URL: http://localhost/swagger-ui/index.html
 */
@Configuration
public class SwaggerConfig {

    @Value("${spring.application.name:delivery-api}")
    private String applicationName;

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI deliveryOpenAPI() {
        // JWT Bearer Token 보안 스키마 정의
        String bearerAuth = "bearerAuth";
        
        return new OpenAPI()
                .info(apiInfo())
                .servers(servers())
                // JWT 인증 스키마 추가
                .components(new Components()
                    .addSecuritySchemes(bearerAuth, new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("JWT Bearer Token을 입력하세요. 'Bearer ' 접두사는 자동으로 추가됩니다.")
                    )
                )
                // 전역 보안 요구사항 추가 (Authorize 버튼 표시용)
                .addSecurityItem(new SecurityRequirement().addList(bearerAuth));
    }

    private Info apiInfo() {
        return new Info()
                .title("Delivery API")
                .description("""
                    배달 서비스 API 문서
                    
                    ## 주요 기능
                    - 사용자 관리
                    - 주문 관리  
                    - 배달 추적
                    - 결제 처리
                    
                    ## 기술 스택
                    - Spring Boot 3.4.3
                    - MySQL 8.0 (Primary/Secondary Replication)
                    - Spring Data JPA
                    - Spring Security (예정)
                    
                    ## 데이터베이스
                    - Primary DB (쓰기): localhost:3307
                    - Secondary DB (읽기): localhost:3308
                    """)
                .version("1.0.0")
                .contact(new Contact()
                        .name("Delivery Development Team")
                        .email("delivery-dev@company.com")
                        .url("https://github.com/delivery-project"))
                .license(new License()
                        .name("MIT License")
                        .url("https://opensource.org/licenses/MIT"));
    }

    private List<Server> servers() {
        return List.of(
                new Server()
                        .url("http://localhost:" + serverPort)
                        .description("로컬 개발 서버"),
                new Server()
                        .url("https://dev-api.delivery.com")
                        .description("개발 서버"),
                new Server()
                        .url("https://staging-api.delivery.com")
                        .description("스테이징 서버"),
                new Server()
                        .url("https://api.delivery.com")
                        .description("운영 서버")
        );
    }

    /**
     * API 정보 반환
     */
    public String getApiInfo() {
        return """
            Delivery API 정보
            =================
            
            애플리케이션명: %s
            서버 포트: %s
            
            ## 주요 기능
            - 사용자 관리
            - 주문 관리  
            - 배달 추적
            - 결제 처리
            
            ## 기술 스택
            - Spring Boot 3.4.3
            - MySQL 8.0 (Primary/Secondary Replication)
            - Spring Data JPA
            - Spring Security (예정)
            
            ## 데이터베이스
            - Primary DB (쓰기): localhost:3307
            - Secondary DB (읽기): localhost:3308
            
            ## API 확인 방법
            - http://localhost:%s/swagger-ui/index.html (Swagger UI)
            - http://localhost:%s/actuator/mappings (API 매핑 정보)
            - http://localhost:%s/actuator/health (서버 상태)
            - http://localhost:%s/delivery/api/test (테스트 API)
            - http://localhost:%s/delivery/api/test/info (API 정보)
            """.formatted(applicationName, serverPort, serverPort, serverPort, serverPort, serverPort, serverPort);
    }

    /**
     * 서버 정보 반환
     */
    public String getServerInfo() {
        return """
            서버 환경 정보
            ===============
            
            로컬 개발 서버: http://localhost:%s
            개발 서버: https://dev-api.delivery.com
            스테이징 서버: https://staging-api.delivery.com
            운영 서버: https://api.delivery.com
            """.formatted(serverPort);
    }
} 