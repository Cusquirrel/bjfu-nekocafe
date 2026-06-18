#!/usr/bin/env bash
set -euo pipefail
BASE_URL=${BASE_URL:-http://localhost:8080}
echo "[1/6] health"
curl -fsS "$BASE_URL/api/health" | grep -q 'UP'
echo "[2/6] stores"
curl -fsS "$BASE_URL/api/stores" | grep -q '森林猫咖'
DATE=$(python -c "from datetime import date,timedelta; print(date.today()+timedelta(days=1))")
echo "[3/6] slots"
curl -fsS "$BASE_URL/api/stores/1/slots?date=$DATE" | grep -q '10:00-12:00'
echo "[4/6] create reservation"
curl -fsS -H 'Content-Type: application/json' -d "{\"userId\":1,\"storeId\":1,\"visitDate\":\"$DATE\",\"slot\":\"16:00-18:00\",\"partySize\":2,\"requestId\":\"smoke-$(date +%s)\"}" "$BASE_URL/api/reservations" | grep -q 'CONFIRMED'
echo "[5/6] cats"
curl -fsS "$BASE_URL/api/cats?storeId=1" | grep -q '年糕'
echo "[6/6] dashboard"
curl -fsS "$BASE_URL/api/dashboard/overview" | grep -q 'reservations'
echo "smoke test passed"
