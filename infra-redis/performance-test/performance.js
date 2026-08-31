import http from 'k6/http';
import { check } from 'k6';

// 10 秒从 0 → 10 VU，
// 接着 30 秒保持 50 VU，
// 最后 10 秒降到 0，
// 期间持续请求lock 接口，并要求 P95 < 2s、P99 < 3s、失败率 < 1%
export const options = {

  stages: [

    {
      duration: '10s',
      target: 10,
    },

    {
      duration: '30s',
      target: 50,
    },

    {
      duration: '10s',
      target: 0,
    },
  ],

  thresholds: {

    http_req_duration: [
      'p(95)<2000',
      'p(99)<3000',
    ],

    http_req_failed: [
      'rate<0.01',
    ],
  },
};

export default function () {

  const response = http.post(
    'http://localhost:8080/lock?name=performance&sleepMillis=10'
  );

  check(response, {
    'status is 200': (r) => r.status === 200,
  });
}
