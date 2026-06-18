# k6 Performance

主要脚本：

```powershell
k6 run tests/perf/reservation-create.js --summary-export tests/reports/k6-summary.json
```

目标接口：

- `GET /api/stores/1/slots?date=...`
- `POST /api/reservations`

`reservation-create.js` 内置 `handleSummary`，默认写出 `tests/reports/k6-summary.json`。
