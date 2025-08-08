#!/bin/bash
set -e

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

# 환경변수 설정 (기본값 포함)
MYSQL_MASTER_HOST=${MYSQL_MASTER_HOST:-primary-mysql}
MYSQL_MASTER_PORT=${MYSQL_MASTER_PORT:-3306}
MYSQL_MASTER_USER=${MYSQL_MASTER_USER:-repl}
MYSQL_MASTER_PASSWORD=${MYSQL_MASTER_PASSWORD:-repl_password}
MYSQL_ROOT_PASSWORD=${MYSQL_ROOT_PASSWORD:-rootpass}
MYSQL_DATABASE=${MYSQL_DATABASE:-delivery}

# 최대 대기 시간 (초)
MAX_WAIT_TIME=300
WAIT_INTERVAL=2

log_info "🚀 Starting MySQL Secondary (Slave) initialization..."
log_info "📋 Configuration:"
log_info "   - Master Host: ${MYSQL_MASTER_HOST}"
log_info "   - Master Port: ${MYSQL_MASTER_PORT}"
log_info "   - Master User: ${MYSQL_MASTER_USER}"
log_info "   - Database: ${MYSQL_DATABASE}"

# 함수: 프로세스 생존 확인
check_process_alive() {
    local pid=$1
    if ! kill -0 $pid 2>/dev/null; then
        log_error "MySQL process (PID: $pid) has died"
        return 1
    fi
    return 0
}

# 함수: 타임아웃과 함께 대기
wait_with_timeout() {
    local condition_func=$1
    local description=$2
    local waited=0
    
    while ! $condition_func; do
        if [ $waited -ge $MAX_WAIT_TIME ]; then
            log_error "Timeout waiting for: $description"
            return 1
        fi
        
        if [ $((waited % 30)) -eq 0 ] && [ $waited -gt 0 ]; then
            log_info "Still waiting for: $description (${waited}s elapsed)"
        fi
        
        sleep $WAIT_INTERVAL
        waited=$((waited + WAIT_INTERVAL))
    done
    
    log_success "$description is ready! (waited ${waited}s)"
    return 0
}

# MySQL 로컬 연결 확인 함수
check_local_mysql() {
    mysqladmin ping -h localhost --silent 2>/dev/null
}

# Primary MySQL 연결 확인 함수
check_primary_mysql() {
    mysqladmin ping -h "$MYSQL_MASTER_HOST" -P "$MYSQL_MASTER_PORT" -u root -p"$MYSQL_ROOT_PASSWORD" --silent 2>/dev/null
}

# Replication 상태 확인 함수
check_replication_status() {
    local result=$(mysql -h localhost -u root -p"$MYSQL_ROOT_PASSWORD" -e "SHOW SLAVE STATUS\G" 2>/dev/null | grep -E "(Slave_IO_Running|Slave_SQL_Running)" | grep -c "Yes" || echo "0")
    [ "$result" = "2" ]
}

# 신호 처리
cleanup() {
    log_warning "Received termination signal, shutting down gracefully..."
    if [ ! -z "$MYSQL_PID" ]; then
        kill $MYSQL_PID 2>/dev/null || true
        wait $MYSQL_PID 2>/dev/null || true
    fi
    exit 0
}

trap cleanup SIGTERM SIGINT

# MySQL 백그라운드 시작
log_info "🔄 Starting MySQL server..."
docker-entrypoint.sh mysqld &
MYSQL_PID=$!

# MySQL이 준비될 때까지 대기
log_info "⏳ Waiting for local MySQL to be ready..."
if ! wait_with_timeout check_local_mysql "Local MySQL"; then
    log_error "Failed to start local MySQL"
    exit 1
fi

# Primary MySQL 연결 확인
log_info "🔍 Checking Primary MySQL connectivity..."
if ! wait_with_timeout check_primary_mysql "Primary MySQL connectivity"; then
    log_error "Cannot connect to Primary MySQL at ${MYSQL_MASTER_HOST}:${MYSQL_MASTER_PORT}"
    exit 1
fi

# Replication 설정 실행
log_info "🔧 Setting up replication..."
if [ -f "/setup-replication.sh" ]; then
    if bash /setup-replication.sh; then
        log_success "Replication setup script executed successfully!"
    else
        log_error "Replication setup script failed!"
        exit 1
    fi
else
    log_error "Replication script not found at /setup-replication.sh"
    exit 1
fi

# Replication 상태 확인
log_info "🔍 Verifying replication status..."
if wait_with_timeout check_replication_status "Replication status verification"; then
    log_success "Replication is working correctly!"
else
    log_warning "Replication status verification failed, but continuing..."
fi

# 최종 상태 출력
log_success "🎯 MySQL Secondary (Slave) is ready and running!"
log_info "📊 Current status:"
mysql -h localhost -u root -p"$MYSQL_ROOT_PASSWORD" -e "SHOW SLAVE STATUS\G" 2>/dev/null | grep -E "(Slave_IO_Running|Slave_SQL_Running|Seconds_Behind_Master|Master_Host|Master_Port)" | sed 's/^/   /'

log_info "💡 Useful commands:"
log_info "   - Check status: docker exec delivery-secondary-mysql mysql -u root -p${MYSQL_ROOT_PASSWORD} -e \"SHOW SLAVE STATUS\\G\""
log_info "   - View logs: docker logs -f delivery-secondary-mysql"

# MySQL 프로세스 대기 (신호 처리와 함께)
while true; do
    if ! check_process_alive $MYSQL_PID; then
        log_error "MySQL process has terminated unexpectedly"
        exit 1
    fi
    sleep 5
done 