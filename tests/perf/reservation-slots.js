import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  vus: Number(__ENV.VUS || 10),
  duration: __ENV.DURATION || '30s',
  thresholds: {
    http_req_failed: ['rate<0.01'],
    http_req_duration: ['p(95)<300'],
  },
};

export default function () {
  const baseUrl = __ENV.BASE_URL || 'http://localhost:8080';
  const date = new Date(Date.now() + 86400000 * 3).toISOString().slice(0, 10);
  const response = http.get(`${baseUrl}/api/stores/1/slots?date=${date}`);
  check(response, {
    'slot lookup succeeds': (r) => r.status === 200,
    'slot lookup returns slot list': (r) => String(r.body).includes('10:00-12:00'),
  });
  sleep(1);
}
