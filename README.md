# 🚚 Delivery Project

배달 서비스를 위한 Spring Boot 기반 멀티 모듈 프로젝트입니다.

## 📋 프로젝트 개요

- **프로젝트명**: Delivery Project
- **설명**: 배달 서비스 백엔드 API
- **기술 스택**: Spring Boot 3.4.3, Java 17, MySQL 8.0, Gradle
- **아키텍처**: 멀티 모듈, 읽기/쓰기 분리, MySQL 복제

## 🏗️ 프로젝트 구조

```
delivery_project/
├── 📁 api/                    # 웹 API 모듈 (메인 애플리케이션)
├── 📁 core/                   # 핵심 비즈니스 로직 모듈
├── 📁 logging/                # 로깅 설정 모듈
├── 📁 notification/           # 알림 서비스 모듈
└── 📄 README.md              # 프로젝트 문서
```

## 🛠️ 기술 스택

### Backend
- **Java 17**
- **Spring Boot 3.4.3**
- **Spring Data JPA**
- **Spring Cloud 2024.0.1**
- **MySQL 8.0** (Primary/Secondary 복제)
- **HikariCP** (커넥션 풀)
- **Resilience4j** (서킷 브레이커)

### Database
- **MySQL 8.0** Primary/Secondary 복제
- **읽기/쓰기 분리** 자동 라우팅
- **HikariCP** 고성능 커넥션 풀

### Monitoring & Logging
- **Spring Cloud Sleuth** (분산 추적)
- **Logback** (로깅)
- **SLF4J** (로깅 파사드)

## 🚀 실행 방법

### 1. 사전 요구사항
- Java 17 이상
- Docker & Docker Compose
- Gradle 8.x

### 2. 데이터베이스 실행
```bash
cd api/mysql-replication
docker-compose up -d
```

### 3. 애플리케이션 실행
```bash
./gradlew :api:bootRun
```

### 4. API 테스트
```bash
curl http://localhost:8080/delivery/api/test
```

## 📊 API 엔드포인트

| Method | URL | Description |
|--------|-----|-------------|
| GET | `/delivery/api/test` | 테스트 API |

## 🔧 설정

### 데이터베이스 설정
- **Primary DB**: localhost:3307
- **Secondary DB**: localhost:3308
- **자동 라우팅**: 읽기 전용 → Secondary, 쓰기 → Primary

### 로깅 설정
- **로그 레벨**: DEBUG (로컬 환경)
- **추적 ID**: 128비트
- **시간대**: Asia/Seoul

## 🧪 테스트

```bash
# 전체 테스트 실행
./gradlew test

# 특정 모듈 테스트
./gradlew :api:test
./gradlew :core:test
```

## 📝 개발 가이드

### 코드 컨벤션
- **Java**: Google Java Style Guide
- **Gradle**: Gradle Best Practices
- **Git**: Conventional Commits

### 브랜치 전략
- `main`: 프로덕션 브랜치
- `develop`: 개발 브랜치
- `feature/*`: 기능 개발 브랜치
- `hotfix/*`: 긴급 수정 브랜치

## 🔒 보안

- **데이터베이스**: 읽기/쓰기 분리로 성능 최적화
- **로깅**: 민감 정보 마스킹
- **API**: 향후 Spring Security 추가 예정

## 📈 모니터링

- **헬스체크**: `/actuator/health`
- **메트릭**: `/actuator/metrics`
- **로깅**: 구조화된 로깅 (JSON 형식)

## 🤝 기여하기

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다.

## 📞 문의

- **개발자**: [개발자명]
- **이메일**: [이메일]
- **프로젝트 링크**: [GitHub 링크]
