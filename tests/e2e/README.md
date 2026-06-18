# Playwright E2E

三条用户旅程覆盖：

- 新用户注册 -> 浏览门店 -> 完成预约 -> 取消预约
- 老会员登录 -> 智能推荐 -> 下单 -> 模拟支付 -> 评价
- 店员登录 -> 到店核验 -> 完单 -> 运营看板更新

运行前启动系统：

```powershell
docker compose up -d --build
npm install
npm run test:e2e
```

HTML 报告输出到 `tests/reports/playwright-report`。
