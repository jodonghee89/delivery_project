#!/bin/bash

# 배달 서비스 인프라 시작 스크립트
echo "🚀 배달 서비스 인프라를 시작합니다..."

# Docker Compose로 전체 인프라 시작
echo "📦 MySQL 이중화 + Redis 시작 중..."
docker-compose up -d

# 서비스 상태 확인
echo "⏳ 서비스 상태 확인 중..."
sleep 10

# 각 서비스 상태 체크
echo "🔍 서비스 상태:"
echo "---------------------------------------------"

# MySQL Primary 상태
if docker exec delivery-primary-mysql mysqladmin ping -h localhost --silent; then
    echo "✅ MySQL Primary (3307) - 정상"
else
    echo "❌ MySQL Primary (3307) - 오류"
fi

# MySQL Secondary 상태  
if docker exec delivery-secondary-mysql mysqladmin ping -h localhost --silent; then
    echo "✅ MySQL Secondary (3308) - 정상"
else
    echo "❌ MySQL Secondary (3308) - 오류"
fi

# Redis 상태
if docker exec delivery-redis redis-cli ping > /dev/null 2>&1; then
    echo "✅ Redis (6379) - 정상"
else
    echo "❌ Redis (6379) - 오류"
fi

echo "---------------------------------------------"
echo "🌐 접속 정보:"
echo "  - MySQL Primary:  localhost:3307"
echo "  - MySQL Secondary: localhost:3308" 
echo "  - Redis:           localhost:6379"
echo "  - Redis Web UI:    http://localhost:8081"
echo ""
echo "📊 상세 상태 확인: docker-compose ps"
echo "📝 로그 확인: docker-compose logs -f [service-name]"
echo "🛑 전체 중지: ./stop-infrastructure.sh" 