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

export async function createReservation(request: APIRequestContext, prefix: string, days = 2, userId = 1) {
  const slots = ['10:00-12:00', '12:00-14:00', '14:00-16:00', '16:00-18:00', '18:00-20:00'];
  let lastResponse = null;
  for (let offset = days; offset < days + 20; offset += 1) {
    for (const slot of slots) {
      const response = await request.post(`${apiBase}/reservations`, {
        data: {
          userId,
          storeId: 1,
          visitDate: futureDate(offset),
          slot,
          partySize: 2,
          requestId: uniqueId(prefix),
        },
      });
      if (response.ok()) {
        return (await response.json()).data;
      }
      lastResponse = response;
    }
  }
  expect(lastResponse?.ok()).toBeTruthy();
  return (await lastResponse!.json()).data;
}
