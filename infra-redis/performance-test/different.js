import http from 'k6/http';
import { check } from 'k6';

export const options = {
  scenarios: {
    different_lock: {
      executor: 'shared-iterations',
      vus: 10,
      iterations: 10,
      maxDuration: '10s',
    },
  },
};

export default function () {

  const lockName = `lock-${__VU}`;

  const response = http.post(
    `http://localhost:8080/lock?name=${lockName}&sleepMillis=2000`
  );

  check(response, {
    'status is 200': (r) => r.status === 200,
  });
}
