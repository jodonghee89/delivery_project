#!/bin/bash

# 배달 서비스 인프라 중지 스크립트
echo "🛑 배달 서비스 인프라를 중지합니다..."

# Docker Compose로 전체 인프라 중지
echo "📦 MySQL 이중화 + Redis 중지 중..."
docker-compose down

echo "✅ 모든 서비스가 중지되었습니다."

# 옵션: 데이터 볼륨도 함께 삭제 (주의!)
read -p "🗑️  데이터 볼륨도 함께 삭제하시겠습니까? (y/N): " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    echo "⚠️  데이터 볼륨 삭제 중... (복구 불가능!)"
    docker-compose down -v
    echo "🗑️  모든 데이터가 삭제되었습니다."
else
    echo "💾 데이터 볼륨은 유지됩니다."
fi

echo ""
echo "🔄 다시 시작: ./start-infrastructure.sh" 