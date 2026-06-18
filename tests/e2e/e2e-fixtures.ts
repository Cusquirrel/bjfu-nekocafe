import { expect, type APIRequestContext, type Page } from '@playwright/test';

export const apiBase = process.env.E2E_API_BASE || 'http://127.0.0.1:8080/api';

export function uniqueId(prefix: string) {
  return `${prefix}-${Date.now()}-${Math.floor(Math.random() * 100000)}`;
}

export function futureDate(days: number) {
  const date = new Date();
  date.setDate(date.getDate() + days);
  return date.toISOString().slice(0, 10);
}

export async function openHome(page: Page) {
  await page.goto('/');
  await expect(page.getByRole('heading', { name: /NekoCafe 智慧猫咖预约平台/ })).toBeVisible();
}

export async function createReservation(request: APIRequestContext, prefix: string, days = 2) {
  const response = await request.post(`${apiBase}/reservations`, {
    data: {
      userId: 1,
      storeId: 1,
      visitDate: futureDate(days),
      slot: '10:00-12:00',
      partySize: 2,
      requestId: uniqueId(prefix),
    },
  });
  expect(response.ok()).toBeTruthy();
  return (await response.json()).data;
}
