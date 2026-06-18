# 实验四质量工程测试说明

## 后端测试

- JUnit5 单元测试：`backend/src/test/java/com/bjfu/nekocafe/unit` 覆盖预约、会员、猫咪照护、推荐、订单支付、权限审计策略。
- jqwik PBT：`backend/src/test/java/com/bjfu/nekocafe/property` 验证预约人数、积分等级、猫咪状态、订单金额、推荐关键词等性质。
- 契约测试：`backend/src/test/java/com/bjfu/nekocafe/contract` 通过 MockMvc 固化预约、会员、猫咪、订单支付 API 响应契约，并输出 `tests/reports/pact.html`。
- 集成测试：`backend/src/test/java/com/bjfu/nekocafe/integration` 覆盖预约、会员、猫咪照护、推荐、订单支付、运营看板、权限审计主链路。
- Testcontainers：`PostgresReservationTestcontainersIT` 使用 PostgreSQL 容器验证真实数据库初始化与预约写入。
- 回归测试：`RecommendationServiceTest` 和 `RecommendationKeywordPropertyTest` 明确验证“橘猫不能被识别为橘子套餐”。

常用命令：

```bash
cd backend
mvn test
mvn -Pcoverage verify -DskipITs=true
mvn -Ptestcontainers verify
mvn org.pitest:pitest-maven:mutationCoverage
```

`mvn -Pcoverage verify` 会生成 JaCoCo 报告并复制 `tests/reports/coverage.html`。PIT 运行后可执行 `tests/scripts/copy-mutation-report.sh` 或 PowerShell 版本生成 `tests/reports/mutation.html`。

## 前端测试

- React/Vitest：`frontend/src/__tests__` 覆盖预约、猫咪照护、推荐面板、运营看板和 API 客户端。
- 前端回归：`RecommendationPanel.test.tsx` 验证推荐橘猫时页面不出现“橘子套餐”。

```bash
cd frontend
npm install
npm run test
npm run test:coverage
npm run build
```

Vitest 覆盖率输出到 `tests/reports/frontend-coverage`。

## E2E 与可访问性

- Playwright 三条用户旅程位于 `tests/e2e`：
  1. 用户注册、预约、取消。
  2. 会员推荐、下单、支付、评价。
  3. 员工工作台、核验完成、运营看板。
- axe 可访问性测试位于 `tests/accessibility/axe-accessibility.spec.ts`。
- Lighthouse 首页审计通过根目录 `npm run lighthouse:home` 生成。

```bash
docker compose up -d --build
npm install
npm run test:e2e
npm run test:a11y
npm run lighthouse:home
```

报告输出：`tests/reports/playwright-report`、`tests/reports/axe-results.json`、`tests/reports/lighthouse-home.html`。

## 性能测试

`tests/perf/reservation-create.js` 对 `/api/reservations` 进行预约链路压测，指标关注：

- `http_req_duration p95 < 500ms`
- `http_req_failed < 1%`
- `checks > 99%`

```bash
k6 run tests/perf/reservation-create.js
```

报告输出：`tests/reports/k6-summary.json`。

## 安全测试

OWASP ZAP Baseline 配置位于 `tests/security/zap-baseline.conf`。

```bash
docker compose up -d --build
./tests/security/run-zap.sh
```

报告输出：`tests/reports/zap_report.html`。

## 质量门禁

- JaCoCo：`backend/pom.xml` 配置 `coverage` profile，`verify` 阶段检查后端行覆盖率和分支覆盖率。
- PIT：`backend/pom.xml` 配置 60% 变异分数阈值，HTML/XML 输出到 `tests/reports/pit`。
- SonarQube：`sonar-project.properties` 汇总后端 JaCoCo XML、前端 lcov、测试源码路径。
- GitHub Actions：`.github/workflows/ci.yml` 自动运行后端 JUnit/jqwik/契约/覆盖率、Testcontainers、前端 Vitest/构建、Playwright E2E、axe/Lighthouse、k6、ZAP、Docker 构建，并上传 `tests/reports` 产物。

## 冒烟测试

`tests/smoke/smoke-test.sh` 验证健康检查、门店列表、时段余量、创建预约、猫咪列表和运营看板。
