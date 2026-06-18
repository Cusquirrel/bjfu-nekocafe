# Contract Tests

后端契约测试位于 `backend/src/test/java/com/bjfu/nekocafe/contract`，并在运行 `mvn test` 时生成：

- `tests/reports/pact.html`

静态 pact JSON 位于 `tests/contract/pact`，用于实验四文档追溯：

- `reservation-member.pact.json`
- `reservation-cat.pact.json`
- `order-payment.pact.json`
