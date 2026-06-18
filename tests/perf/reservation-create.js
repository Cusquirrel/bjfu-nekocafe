import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  vus: Number(__ENV.VUS || 20),
  duration: __ENV.DURATION || '1m',
  thresholds: {
    http_req_failed: ['rate<0.01'],
    http_req_duration: ['p(95)<500', 'p(99)<1000'],
    checks: ['rate>0.99'],
  },
};

const baseUrl = __ENV.BASE_URL || 'http://localhost:8080';

export default function () {
  const date = new Date(Date.now() + 86400000 * 7).toISOString().slice(0, 10);
  const slotResponse = http.get(`${baseUrl}/api/stores/1/slots?date=${date}`);
  check(slotResponse, {
    'slots status is 200': (r) => r.status === 200,
    'slots has data': (r) => String(r.body).includes('availableTables'),
  });

  const payload = JSON.stringify({
    userId: 1,
    storeId: 1,
    visitDate: date,
    slot: '18:00-20:00',
    partySize: 2,
    requestId: `k6-${__VU}-${__ITER}-${Date.now()}`,
  });
  const reservation = http.post(`${baseUrl}/api/reservations`, payload, {
    headers: { 'Content-Type': 'application/json' },
  });
  check(reservation, {
    'reservation is accepted or gracefully rejected when full': (r) => r.status === 200 || r.status === 400,
    'reservation response has trace': (r) => String(r.body).includes('traceId'),
  });
  sleep(1);
}

export function handleSummary(data) {
  return {
    'tests/reports/k6-summary.json': JSON.stringify(data, null, 2),
    stdout: JSON.stringify({
      http_req_duration: data.metrics.http_req_duration,
      http_req_failed: data.metrics.http_req_failed,
      iterations: data.metrics.iterations,
      checks: data.metrics.checks,
    }, null, 2),
  };
}
