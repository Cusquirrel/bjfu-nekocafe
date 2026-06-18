import { vi } from 'vitest';

type MockOptions = {
  recommendationCats?: any[];
  dashboard?: any;
  failReservations?: boolean;
};

function ok(data: any, status = 200) {
  return {
    ok: status >= 200 && status < 300,
    status,
    json: async () => ({ success: status < 400, code: status < 400 ? 'OK' : 'ERROR', message: status < 400 ? 'success' : 'error', data, traceId: 'vitest' }),
  } as Response;
}

export function installMockApi(options: MockOptions = {}) {
  const stores = [{ id: 1, name: '森林猫咖·学院路店', city: '北京' }];
  const cats = [
    { id: 1, name: '年糕', breed: '布偶', interaction_status: 'AVAILABLE', health_status: 'NORMAL', age_months: 18, weight_kg: 4.8 },
    { id: 2, name: '豆沙', breed: '英短', interaction_status: 'RESTING', health_status: 'NORMAL', age_months: 30, weight_kg: 5.2 },
  ];
  const slots = [{ slot: '10:00-12:00', availableTables: 2, totalTables: 4 }];

  const fetchMock = vi.fn(async (input: RequestInfo | URL, init?: RequestInit) => {
    const url = String(input);
    const method = init?.method || 'GET';

    if (url.includes('/stores/') && url.includes('/slots')) return ok(slots);
    if (url.endsWith('/stores') || url.includes('/stores?')) return ok(stores);
    if (url.includes('/recommendations')) {
      return ok({
        reason: '推荐橘猫互动档案',
        cats: options.recommendationCats || [{ id: 3, name: '橘猫豆豆', breed: '中华田园猫', interaction_status: 'AVAILABLE' }],
      });
    }
    if (url.includes('/cats/') && url.includes('/health-records') && method === 'POST') {
      const body = JSON.parse(String(init?.body || '{}'));
      return ok({ ...cats[0], health_status: body.recordType === 'HEALTH_STATUS' ? body.value : cats[0].health_status });
    }
    if (url.includes('/cats')) return ok(cats);
    if (url.includes('/dashboard')) return ok(options.dashboard || { reservations: 8, completed: 5, cancelled: 1, watchCats: 1, revenueCents: 18800 });
    if (url.includes('/reservations') && method === 'POST') {
      if (options.failReservations) {
        return {
          ok: false,
          status: 400,
          json: async () => ({ success: false, code: 'SLOT_FULL', message: '该时段已满', data: null, traceId: 'vitest' }),
        } as Response;
      }
      return ok({ id: 101, reservation_no: 'RSV20260619-101', table_code: 'A01', status: 'CONFIRMED' });
    }
    return ok({});
  });

  vi.stubGlobal('fetch', fetchMock);
  return fetchMock;
}
