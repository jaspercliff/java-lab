import http from 'k6/http';
import { check } from 'k6';

// 创建 10 个虚拟用户（VU），总共发送 10 次 HTTP POST 请求，并让它们竞争同一个 name=same 的锁
export const options = {
  scenarios: {
    same_lock: {
      executor: 'shared-iterations',// 所有 VU 共同完成指定数量的 iteration
      vus: 10, // VU = Virtual User
      iterations: 10, // 执行10次 default function
      maxDuration: '30s',// 这个 scenario 最多运行 30 秒
    },
  },
};

export default function () {

  const response = http.post(
    'http://localhost:8080/lock?name=same&sleepMillis=1000'
  );

  check(response, {
    'status is 200': (r) => r.status === 200,
  });
}
