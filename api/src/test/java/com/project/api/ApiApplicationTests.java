package com.project.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("local")
class ApiApplicationTests {

  @Autowired
  private DataSource dataSource;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Test
  void contextLoads() {
    assertNotNull(dataSource, "DataSource should be loaded");
    assertNotNull(jdbcTemplate, "JdbcTemplate should be loaded");
  }

  @Test
  void testDatabaseConnection() {
    // 데이터베이스 연결 테스트
    String result = jdbcTemplate.queryForObject("SELECT 1", String.class);
    assertEquals("1", result, "Database connection should work");
  }

  @Test
  void testReadWriteSeparation() {
    // 읽기 작업 테스트 (Secondary DB 사용)
    String readResult = jdbcTemplate.queryForObject(
        "SELECT COUNT(*) FROM users", String.class);
    assertNotNull(readResult, "Read operation should work");
    
    // 쓰기 작업 테스트 (Primary DB 사용)
    int insertResult = jdbcTemplate.update(
        "INSERT INTO users (email, name, phone_number) VALUES (?, ?, ?)",
        "test@example.com", "테스트 사용자", "010-9999-9999");
    assertTrue(insertResult > 0, "Write operation should work");
    
    // 테스트 데이터 정리
    jdbcTemplate.update("DELETE FROM users WHERE email = ?", "test@example.com");
  }
}
