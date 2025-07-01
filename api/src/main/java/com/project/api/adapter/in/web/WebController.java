package com.project.api.adapter.in.web;

import com.project.core.StringUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/delivery/api/test", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "테스트 API", description = "배달 서비스 테스트 API")
public class WebController {

  @GetMapping
  @Operation(summary = "테스트 API", description = "기본 테스트 API 엔드포인트")
  public ResponseEntity<Map<String, Object>> test() {
    StringUtils.print("테스트 API 호출됨");
    
    Map<String, Object> response = new HashMap<>();
    response.put("message", "테스트 API가 정상적으로 호출되었습니다!");
    response.put("timestamp", System.currentTimeMillis());
    response.put("status", "success");
    
    return ResponseEntity.ok(response);
  }

  @GetMapping("/hello/{name}")
  @Operation(summary = "인사 API", description = "이름을 받아서 인사하는 API")
  public ResponseEntity<Map<String, Object>> hello(
    @Parameter(description = "인사할 이름", example = "홍길동")
    @PathVariable String name
  ) {
    StringUtils.print("인사 API 호출됨: " + name);
    
    Map<String, Object> response = new HashMap<>();
    response.put("message", "안녕하세요, " + name + "님!");
    response.put("timestamp", System.currentTimeMillis());
    response.put("status", "success");
    
    return ResponseEntity.ok(response);
  }

  @PostMapping("/echo")
  @Operation(summary = "에코 API", description = "요청 데이터를 그대로 반환하는 API")
  public ResponseEntity<Map<String, Object>> echo(@RequestBody Map<String, Object> request) {
    StringUtils.print("에코 API 호출됨: " + request);
    
    Map<String, Object> response = new HashMap<>();
    response.put("message", "에코 성공!");
    response.put("data", request);
    response.put("timestamp", System.currentTimeMillis());
    response.put("status", "success");
    
    return ResponseEntity.ok(response);
  }

  @GetMapping("/info")
  @Operation(summary = "API 정보", description = "API 정보와 서버 정보를 반환")
  public ResponseEntity<Map<String, Object>> apiInfo() {
    Map<String, Object> response = new HashMap<>();
    response.put("application", "Delivery API");
    response.put("version", "1.0.0");
    response.put("description", "배달 서비스 API");
    response.put("swagger-ui", "http://localhost:8080/swagger-ui.html");
    response.put("timestamp", System.currentTimeMillis());
    response.put("status", "success");
    
    return ResponseEntity.ok(response);
  }
}
