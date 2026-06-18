$ErrorActionPreference = "Stop"
$url = $env:LIGHTHOUSE_URL
if (-not $url) { $url = "http://127.0.0.1:3000" }
New-Item -ItemType Directory -Force -Path "tests/reports" | Out-Null
npx lighthouse $url --output=html --output-path=tests/reports/lighthouse-home.html --chrome-flags="--headless=new --no-sandbox"
