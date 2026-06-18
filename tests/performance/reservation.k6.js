import http from 'k6/http';
import { check, sleep } from 'k6';
export const options = {
  vus: 20,
  duration: '1m',
  thresholds: {
    http_req_failed: ['rate<0.01'],
    http_req_duration: ['p(95)<500']
  }
};
export default function () {
  const tomorrow = new Date(Date.now() + 86400000).toISOString().slice(0, 10);
  const payload = JSON.stringify({ userId: 1, storeId: 1, visitDate: tomorrow, slot: '18:00-20:00', partySize: 2, requestId: `k6-${__VU}-${__ITER}-${Date.now()}` });
  const res = http.post(`${__ENV.BASE_URL || 'http://localhost:8080'}/api/reservations`, payload, { headers: { 'Content-Type': 'application/json' } });
  check(res, { 'status is 200 or 400 when full': r => r.status === 200 || r.status === 400 });
  sleep(1);
}
