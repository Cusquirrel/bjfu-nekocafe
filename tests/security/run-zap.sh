#!/usr/bin/env sh
set -eu
TARGET="${ZAP_TARGET:-http://host.docker.internal:8080}"
mkdir -p tests/reports
docker run --rm \
  -v "$PWD/tests/reports:/zap/wrk" \
  -v "$PWD/tests/security/zap-baseline.conf:/zap/wrk/zap-baseline.conf:ro" \
  ghcr.io/zaproxy/zaproxy:stable zap-baseline.py \
  -t "$TARGET" \
  -c zap-baseline.conf \
  -r zap_report.html \
  -I
