#!/usr/bin/env sh
set -eu
URL="${LIGHTHOUSE_URL:-http://127.0.0.1:3000}"
mkdir -p tests/reports
npx lighthouse "$URL" --output=html --output-path=tests/reports/lighthouse-home.html --chrome-flags="--headless=new --no-sandbox"
