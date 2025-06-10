#!/bin/bash
set -e

echo "Waiting for primary to be ready..."
until mysql -h primary-mysql -uroot -prootpass -e "SELECT 1;" &> /dev/null; do
  sleep 1
done

echo "Fetching PRIMARY status..."
STATUS=$(mysql -h primary-mysql -uroot -prootpass -e "SHOW MASTER STATUS\G")
FILE=$(echo "$STATUS" | grep File: | awk '{print $2}')
POS=$(echo "$STATUS" | grep Position: | awk '{print $2}')

echo "Configuring replication..."
mysql -uroot -prootpass -e "STOP REPLICA IO_THREAD;"

mysql -uroot -prootpass -e "CHANGE MASTER TO \
  MASTER_HOST='primary-mysql', \
  MASTER_USER='repl', \
  MASTER_PASSWORD='replpass', \
  MASTER_LOG_FILE='$FILE', \
  MASTER_LOG_POS=$POS;"

mysql -uroot -prootpass -e "START REPLICA;"
mysql -uroot -prootpass -e "SHOW REPLICA STATUS\G"

echo "Checking replication status..."

STATUS=$(mysql -uroot -prootpass -e "SHOW REPLICA STATUS\G")

IO_RUNNING=$(echo "$STATUS" | grep "Replica_IO_Running:" | awk '{print $2}')
SQL_RUNNING=$(echo "$STATUS" | grep "Replica_SQL_Running:" | awk '{print $2}')

echo "STATUS: $STATUS"
echo "Replica_IO_Running: $IO_RUNNING"
echo "Replica_SQL_Running: $SQL_RUNNING"

if [ "$IO_RUNNING" = "Yes" ] && [ "$SQL_RUNNING" = "Yes" ]; then
  echo "✅ Replication is running successfully."
else
  echo "❌ Replication failed to start properly."
  exit 1
fi