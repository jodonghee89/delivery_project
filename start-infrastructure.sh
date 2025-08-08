#!/bin/bash

# 색상 정의
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 로그 함수
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 배달 서비스 인프라 시작 스크립트
log_info "🚀 배달 서비스 인프라를 시작합니다..."

# Docker Compose로 전체 인프라 시작
log_info "📦 MySQL 이중화 + Redis 시작 중..."
if docker-compose up -d; then
    log_success "모든 서비스가 시작되었습니다!"
else
    log_error "서비스 시작 중 오류가 발생했습니다!"
    exit 1
fi

# 서비스 상태 확인
log_info "⏳ 서비스 상태 확인 중... (30초 대기)"
sleep 30

# 각 서비스 상태 체크
log_info "🔍 서비스 상태:"
echo "---------------------------------------------"

# MySQL Primary 상태
if docker exec delivery-primary-mysql mysqladmin ping -h localhost --silent 2>/dev/null; then
    log_success "MySQL Primary (3307) - 정상"
else
    log_error "MySQL Primary (3307) - 오류"
fi

# MySQL Secondary 상태  
if docker exec delivery-secondary-mysql mysqladmin ping -h localhost --silent 2>/dev/null; then
    # Replication 상태도 확인
    REPL_STATUS=$(docker exec delivery-secondary-mysql mysql -h localhost -u root -prootpass -e "SHOW SLAVE STATUS\G" 2>/dev/null | grep -E "(Slave_IO_Running|Slave_SQL_Running)" | grep -c "Yes" || echo "0")
    if [ "$REPL_STATUS" = "2" ]; then
        log_success "MySQL Secondary (3308) - 정상 (Replication 활성)"
    else
        log_warning "MySQL Secondary (3308) - 실행 중이지만 Replication 확인 필요"
    fi
else
    log_error "MySQL Secondary (3308) - 오류"
fi

# Redis 상태
if docker exec delivery-redis redis-cli ping > /dev/null 2>&1; then
    log_success "Redis (6379) - 정상"
else
    log_error "Redis (6379) - 오류"
fi

# Redis UI 상태
if curl -s http://localhost:8082 > /dev/null 2>&1; then
    log_success "Redis Web UI (8082) - 정상"
else
    log_warning "Redis Web UI (8082) - 접근 불가 (시작 중일 수 있음)"
fi

echo "---------------------------------------------"
log_info "🌐 접속 정보:"
echo "  - MySQL Primary:  localhost:3307 (쓰기 전용)"
echo "  - MySQL Secondary: localhost:3308 (읽기 전용)" 
echo "  - Redis:           localhost:6379 (Refresh Token)"
echo "  - Redis Web UI:    http://localhost:8082"
echo ""
log_info "📊 상세 상태 확인: docker-compose ps"
log_info "📝 로그 확인: docker-compose logs -f [service-name]"
log_info "🛑 전체 중지: ./stop-infrastructure.sh"
echo ""

# 최종 상태 요약
HEALTHY_SERVICES=$(docker-compose ps --format "table {{.Service}}\t{{.State}}" | grep -c "running" || echo "0")
TOTAL_SERVICES=$(docker-compose ps --format "table {{.Service}}" | wc -l || echo "0")
TOTAL_SERVICES=$((TOTAL_SERVICES - 1)) # 헤더 제외

if [ "$HEALTHY_SERVICES" -eq "$TOTAL_SERVICES" ]; then
    log_success "✅ 모든 서비스가 정상 실행 중입니다! ($HEALTHY_SERVICES/$TOTAL_SERVICES)"
else
    log_warning "⚠️  일부 서비스에 문제가 있을 수 있습니다. ($HEALTHY_SERVICES/$TOTAL_SERVICES)"
    log_info "자세한 상태는 'docker-compose ps'로 확인하세요."
fi 