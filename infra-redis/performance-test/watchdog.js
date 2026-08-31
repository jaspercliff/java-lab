import http from 'k6/http';
import { check } from 'k6';

export const options = {
  scenarios: {
    watchdog_test: {
      executor: 'per-vu-iterations',
      vus: 2,
      iterations: 1,
      maxDuration: '90s',
    },
  },
};

export default function () {

  const response = http.post(
    'http://localhost:8080/lock?name=watchdog&sleepMillis=40000',
    null,
    {
      timeout: '60s',
    }
  );

  check(response, {
    'status is 200': (r) => r.status === 200,
  });
}
