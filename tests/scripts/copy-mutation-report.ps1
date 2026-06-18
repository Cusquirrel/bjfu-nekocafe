$ErrorActionPreference = "Stop"
$source = "tests/reports/pit/index.html"
$target = "tests/reports/mutation.html"
if (Test-Path $source) {
  Copy-Item -Force $source $target
} else {
  throw "PIT report not found: $source"
}
