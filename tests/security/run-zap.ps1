$ErrorActionPreference = "Stop"
$target = $env:ZAP_TARGET
if (-not $target) { $target = "http://host.docker.internal:8080" }
New-Item -ItemType Directory -Force -Path "tests/reports" | Out-Null
docker run --rm `
  -v "${PWD}\tests\reports:/zap/wrk" `
  -v "${PWD}\tests\security\zap-baseline.conf:/zap/wrk/zap-baseline.conf:ro" `
  ghcr.io/zaproxy/zaproxy:stable zap-baseline.py `
  -t $target `
  -c zap-baseline.conf `
  -r zap_report.html `
  -I
