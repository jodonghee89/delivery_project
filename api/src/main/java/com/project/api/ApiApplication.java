package com.project.api;

import com.project.core.config.DataSourceConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
@EntityScan(basePackages = "com.project.api.domain")
public class ApiApplication {

  public static void main(String[] args) {
    SpringApplication.run(ApiApplication.class, args);
  }

  /**
   * 운영 환경용 설정 (core 모듈 포함)
   */
  @Profile("!test")
  @ComponentScan(basePackages = {"com.project.api", "com.project.core"})
  static class ProductionConfig {
  }

  /**
   * 테스트 환경용 설정 
   * - core 모듈의 DataSourceConfig 완전 제외
   * - H2 인메모리 DB만 사용
   */
  @Profile("test")
  @ComponentScan(
    basePackages = {"com.project.api"},
    excludeFilters = {
      @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = DataSourceConfig.class),
      @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com\\.project\\.core\\.config\\..*")
    }
  )
  static class TestConfig {
  }
}
