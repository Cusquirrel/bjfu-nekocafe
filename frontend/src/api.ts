const API_BASE = import.meta.env.VITE_API_BASE || '/api';
export type ApiResponse<T> = { success: boolean; code: string; message: string; data: T; traceId: string };
async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const res = await fetch(`${API_BASE}${path}`, { headers: { 'Content-Type': 'application/json', ...(init?.headers || {}) }, ...init });
  const json = await res.json() as ApiResponse<T>;
  if (!res.ok || !json.success) throw new Error(json.message || json.code);
  return json.data;
}
export const api = {
  health: () => request<{status:string}>('/health'),
  stores: () => request<any[]>('/stores'),
  slots: (storeId:number, date:string) => request<any[]>(`/stores/${storeId}/slots?date=${date}`),
  cats: (storeId?:number) => request<any[]>(`/cats${storeId ? `?storeId=${storeId}` : ''}`),
  dashboard: () => request<any>('/dashboard/overview'),
  recommend: (userId:number, storeId:number) => request<any>(`/recommendations/visit?userId=${userId}&storeId=${storeId}`),
  createReservation: (body:any) => request<any>('/reservations', { method:'POST', body: JSON.stringify(body) }),
  cancelReservation: (id:number) => request<any>(`/reservations/${id}/cancel`, { method:'POST' }),
  addCatHealth: (catId:number, body:any) => request<any>(`/cats/${catId}/health-records`, { method:'POST', body: JSON.stringify(body) })
};
export function tomorrow(): string { const d = new Date(); d.setDate(d.getDate()+1); return d.toISOString().slice(0,10); }
