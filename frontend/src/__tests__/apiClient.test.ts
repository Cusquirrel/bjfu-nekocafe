import { beforeEach, describe, expect, test, vi } from 'vitest';
import { api } from '../api';

describe('api client', () => {
  beforeEach(() => {
    vi.unstubAllGlobals();
  });

  test('posts reservation payload to backend contract', async () => {
    const fetchMock = vi.fn(async () => ({
      ok: true,
      json: async () => ({ success: true, code: 'OK', message: 'success', data: { id: 1 }, traceId: 'api-test' }),
    } as Response));
    vi.stubGlobal('fetch', fetchMock);

    await api.createReservation({ userId: 1, storeId: 1, partySize: 2 });

    expect(fetchMock).toHaveBeenCalledWith('/api/reservations', expect.objectContaining({
      method: 'POST',
      body: JSON.stringify({ userId: 1, storeId: 1, partySize: 2 }),
    }));
  });

  test('surfaces backend business errors', async () => {
    vi.stubGlobal('fetch', vi.fn(async () => ({
      ok: false,
      json: async () => ({ success: false, code: 'SLOT_FULL', message: '该时段已满', data: null, traceId: 'api-test' }),
    } as Response)));

    await expect(api.createReservation({})).rejects.toThrow('该时段已满');
  });
});
