# Quality Reports

生成文件约定：

- `coverage.html`: 后端 JaCoCo 首页，由 `mvn -Pcoverage test` 复制生成。
- `mutation.html`: PIT 变异测试首页，可由 CI 或 `tests/scripts/copy-mutation-report.ps1` 从 `tests/reports/pit/index.html` 复制。
- `pact.html`: 后端契约测试运行时生成。
- `playwright-report/`: Playwright E2E HTML 报告。
- `frontend-coverage/`: React/Vitest 覆盖率报告。
- `k6-summary.json`: k6 预约接口压测摘要。
- `zap_report.html`: OWASP ZAP Baseline 报告。
- `lighthouse-home.html`: Lighthouse 首页可访问性报告。
- `axe-results.json`: axe 可访问性扫描原始结果。
