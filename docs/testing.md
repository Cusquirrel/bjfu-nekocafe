# 实验三测试说明

## 后端测试

- `MemberLevelPolicyTest`：会员积分等级规则单元测试。
- `ReservationFlowIntegrationTest`：预约创建、重复提交、取消预约、健康检查、运营看板接口集成测试。

## 前端测试

- `App.test.tsx`：验证 React 首页入口、接口 mock 与基础渲染。

## 性能测试

`tests/performance/reservation.k6.js` 对 `/api/reservations` 进行预约链路压测，指标关注：

- `http_req_duration p95 < 500ms`
- `http_req_failed < 1%`

## 冒烟测试

`tests/smoke/smoke-test.sh` 验证健康检查、门店列表、时段余量、创建预约、猫咪列表和运营看板。
