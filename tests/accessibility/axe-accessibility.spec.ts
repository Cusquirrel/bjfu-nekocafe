import { test, expect } from '@playwright/test';
import { AxeBuilder } from '@axe-core/playwright';
import { mkdir, writeFile } from 'node:fs/promises';

test('home page has no serious or critical axe violations', async ({ page }) => {
  await page.goto('/');
  await page.getByText('NekoCafe 智慧猫咖预约平台').waitFor();

  const results = await new AxeBuilder({ page }).analyze();
  const blocking = results.violations.filter((violation) =>
    violation.impact === 'serious' || violation.impact === 'critical'
  );

  await mkdir('tests/reports', { recursive: true });
  await writeFile('tests/reports/axe-results.json', JSON.stringify(results, null, 2), 'utf8');

  expect(blocking).toEqual([]);
});
