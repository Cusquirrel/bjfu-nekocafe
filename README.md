# NekoCafé 实验三代码实现包

本代码包在实验一需求基线和实验二架构设计基础上实现实验三所需的可运行 PoC 系统、容器化部署、CI/CD、Helm、可观测性与基础测试资产。

## 技术口径

- 后端：Spring Boot 2.7.18 + Java 8 + Spring JDBC + PostgreSQL/Redis + Actuator + Prometheus 指标
- 前端：React + TypeScript + Vite，页面重新设计，不直接复用参考项目页面
- 数据库：PostgreSQL，兼容 H2 测试环境
- 部署：Docker Compose、本地 Nginx 前端代理、Helm/Kubernetes 清单
- 测试：JUnit/MockMvc/集成测试、前端 Vitest、k6 性能脚本、冒烟测试脚本

## 已覆盖的实验一/二/三功能主线

1. 账号与会员：注册、登录、会员积分、会员等级。
2. 门店与预约：门店列表、时段余量、创建预约、取消预约、到店核验。
3. 猫咪管理：猫咪档案、互动状态、健康打卡、NekoGuard 约束。
4. 点单支付：创建订单、模拟支付、退款。
5. 智能推荐：按用户偏好、猫咪状态、门店热度生成推荐。
6. 运营看板：预约数、完成数、取消数、健康预警、收入概览。
7. DevOps：Dockerfile、docker-compose、CI、Helm、Prometheus/Grafana、DORA 数据采集口径。

## 推荐运行步骤

```bash
cd EXP3_NekoCafe_CODE_v1_0
cp .env.example .env
docker compose up -d --build
```

访问入口：

- 前端：http://localhost:3000
- 后端健康检查：http://localhost:8080/api/health
- Actuator：http://localhost:8080/actuator/health
- Prometheus 指标：http://localhost:8080/actuator/prometheus

## 后端本地测试

```bash
cd backend
./mvnw test
```

如没有 Maven Wrapper，可使用本机 Maven：

```bash
mvn test
```

## 前端本地测试

```bash
cd frontend
npm install
npm run test
npm run build
```

## 目录说明

```text
backend/                  Spring Boot 后端工程
frontend/                 React 前端工程
db/init/                  PostgreSQL 建表和初始化数据
infra/docker/             Docker 相关配置
infra/observability/      Prometheus、Grafana、告警规则
helm/neko-cafe/           Kubernetes Helm Chart
.github/workflows/        CI/CD 流水线配置
tests/performance/        k6 性能测试脚本
tests/smoke/              冒烟测试脚本
docs/                     接口、部署、测试说明
```
