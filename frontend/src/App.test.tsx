import { render, screen } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import App from './App';
import { vi, test, expect } from 'vitest';

vi.stubGlobal('fetch', vi.fn(async (url: RequestInfo) => {
  const text = String(url);
  let data: any = [];
  if (text.includes('/stores')) data = [{id:1,name:'森林猫咖·学院路店'}];
  if (text.includes('/slots')) data = [{slot:'10:00-12:00',availableTables:2,totalTables:4}];
  if (text.includes('/recommendations')) data = {reason:'测试推荐', cats:[{id:1,name:'年糕'}]};
  return { ok:true, json: async()=>({success:true, code:'OK', message:'success', data, traceId:'test'}) } as Response;
}));

test('renders NekoCafe dashboard entry', async () => {
  render(<QueryClientProvider client={new QueryClient()}><App /></QueryClientProvider>);
  expect(await screen.findByText(/NekoCafe 智慧猫咖预约平台/)).toBeTruthy();
});
