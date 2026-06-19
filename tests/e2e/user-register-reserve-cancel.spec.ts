import { expect, test } from '@playwright/test';
import { apiBase, createReservation, openHome, uniqueId } from './e2e-fixtures';

test('new user registers, browses stores, reserves, then cancels', async ({ page, request }) => {
  await openHome(page);
  await expect(page.locator('option', { hasText: '森林猫咖·学院路店' })).toHaveCount(1);

  const username = uniqueId('e2e_user');
  const register = await request.post(`${apiBase}/auth/register`, {
    data: { username, phone: '13900007777', password: 'demo123' },
  });
  expect(register.ok()).toBeTruthy();
  const user = (await register.json()).data;

  const stores = await request.get(`${apiBase}/stores`);
  expect(stores.ok()).toBeTruthy();
  expect((await stores.json()).data.length).toBeGreaterThan(0);

  const reservationData = await createReservation(request, 'e2e-new-reserve', 3, user.id);
  expect(reservationData.status).toBe('CONFIRMED');

  const cancel = await request.post(`${apiBase}/reservations/${reservationData.id}/cancel`);
  expect(cancel.ok()).toBeTruthy();
  expect((await cancel.json()).data.status).toBe('CANCELLED');
});
