import http from 'k6/http';
import { check } from 'k6'; // 引入 check 模块

export const options = {
    vus: 10,
    iterations: 10,
};

export default function () {
    // 发起 GET 请求
    // let res = http.get('http://localhost:8081/testDeco/bulkhead');
    let res = http.get('http://localhost:8081/testDeco/threadPoolBulkhead');

    // 使用 check 对响应进行断言检查
    check(res, {
        'status is 200': (r) => r.status === 200,
        'response body contains fallback': (r) => r.body.includes('===fallback：下游调用失败，异常类型'),
        'response body contains bulkhead fallback': (r) => r.body.includes('===fallback：bulkhead已满，拒绝调用'),
    });
}