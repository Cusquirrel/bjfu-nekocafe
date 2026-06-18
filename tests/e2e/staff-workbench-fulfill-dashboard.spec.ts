import { expect, test } from '@playwright/test';
import { apiBase, createReservation, openHome } from './e2e-fixtures';

test('staff checks in reservation, completes service, and sees dashboard update', async ({ page, request }) => {
  const login = await request.post(`${apiBase}/auth/login`, {
    data: { username: 'staff_01', password: 'demo123' },
  });
  expect(login.ok()).toBeTruthy();
  expect((await login.json()).data.role_code).toBe('STAFF');

  const reservation = await createReservation(request, 'e2e-staff-fulfill', 5);
  const checkIn = await request.post(`${apiBase}/reservations/${reservation.id}/check-in`);
  expect(checkIn.ok()).toBeTruthy();
  expect((await checkIn.json()).data.status).toBe('CHECKED_IN');

  const complete = await request.post(`${apiBase}/reservations/${reservation.id}/complete`);
  expect(complete.ok()).toBeTruthy();
  expect((await complete.json()).data.status).toBe('COMPLETED');

  await openHome(page);
  await page.getByRole('button', { name: /运营看板/ }).click();
  await expect(page.getByText('运营看板')).toBeVisible();

  const dashboard = await request.get(`${apiBase}/dashboard/overview`);
  expect(dashboard.ok()).toBeTruthy();
  expect((await dashboard.json()).data.completed).toBeGreaterThanOrEqual(1);
});
