# 实验三部署说明

## 本地 Docker Compose

```bash
cp .env.example .env
docker compose up -d --build
curl http://localhost:8080/api/health
```

## 镜像构建要求

后端使用 Maven 多阶段构建，运行阶段使用 JRE Alpine 镜像并以非 root 用户运行。前端使用 Node 构建、Nginx 承载静态资源并反向代理 `/api`。

## 配置与密钥

数据库密码、JWT 示例密钥等配置通过 `.env` 或 Kubernetes Secret 注入，不写死在代码中。仓库只保留 `.env.example`。

## 回滚策略

本实验代码提供 Helm 渲染和 CI 构建逻辑，真实环境可使用：

```bash
helm rollback nekocafe 1
```

如果 Docker Compose 环境失败，可以回退上一版镜像标签并重新执行：

```bash
docker compose pull && docker compose up -d
```
