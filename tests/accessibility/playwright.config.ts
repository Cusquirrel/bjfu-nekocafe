import { defineConfig, devices } from '@playwright/test';
import path from 'node:path';

const reportsDir = path.resolve(process.cwd(), 'tests/reports');

export default defineConfig({
  testDir: '.',
  outputDir: path.join(reportsDir, 'accessibility-artifacts'),
  reporter: [
    ['list'],
    ['html', { outputFolder: path.join(reportsDir, 'accessibility-report'), open: 'never' }],
  ],
  use: {
    baseURL: process.env.E2E_BASE_URL || 'http://127.0.0.1:3000',
    trace: 'retain-on-failure',
  },
  projects: [{ name: 'chromium', use: { ...devices['Desktop Chrome'] } }],
});
