import http from 'k6/http';
import { check } from 'k6'; // 引入 check 模块

export const options = {
    vus: 100,         // 100 个虚拟用户同时发起请求（100 个并发）
    // duration: '30s',  // 建议加上持续时间，避免脚本瞬间执行完（根据需要调整）
};

export default function () {
    // 发起 GET 请求
    let res = http.get('http://localhost:8081/test/bulkhead/1');

    // 使用 check 对响应进行断言检查
    check(res, {
        'status is 200': (r) => r.status === 200,
        'response body contains  fallback': (r) => r.body.includes('触发并发隔离降级'),
    });
}