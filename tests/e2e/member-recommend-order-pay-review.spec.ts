import { expect, test } from '@playwright/test';
import { apiBase, createReservation, openHome } from './e2e-fixtures';

test('member sees recommendation, orders, pays, and reviews', async ({ page, request }) => {
  await openHome(page);
  await expect(page.getByText('智能推荐')).toBeVisible();
  await expect(page.getByText('橘子套餐')).toHaveCount(0);

  const login = await request.post(`${apiBase}/auth/login`, {
    data: { username: 'demo_member', password: 'demo123' },
  });
  expect(login.ok()).toBeTruthy();

  const rec = await request.get(`${apiBase}/recommendations/visit?userId=1&storeId=1`);
  expect(rec.ok()).toBeTruthy();
  expect((await rec.json()).data.reason).toContain('猫咪');

  const reservation = await createReservation(request, 'e2e-member-order', 4);
  const order = await request.post(`${apiBase}/orders`, {
    data: { userId: 1, reservationId: reservation.id, amountCents: 8800 },
  });
  expect(order.ok()).toBeTruthy();
  const orderData = (await order.json()).data;

  const pay = await request.post(`${apiBase}/orders/${orderData.id}/pay`, {
    data: { channel: 'MOCK_PAY' },
  });
  expect(pay.ok()).toBeTruthy();
  expect((await pay.json()).data.status).toBe('PAID');

  const review = await request.post(`${apiBase}/reviews`, {
    data: { userId: 1, storeId: 1, rating: 5, content: 'E2E 模拟支付后评价' },
  });
  expect(review.ok()).toBeTruthy();
  expect((await review.json()).data.rating).toBe(5);
});
