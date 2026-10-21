import http from 'k6/http';
import { check } from 'k6';

export const options = {
    vus: 10,
    iterations: 100,
};

export default function () {
    const res = http.get(
        'http://localhost:8081/random/absolute?rate=50'
    );

    check(res, {
        'HTTP status is 200': (r) => r.status === 200,
        'contains success message': (r) =>
            r.body.includes('Hello from Producer! Success'),
        'contains CircuitBreaker fallback': (r) =>
            r.body.toLowerCase().includes('circuitbreaker fallback'),

        'contains RateLimiter fallback': (r) =>
            r.body.toLowerCase().includes('ratelimiter fallback'),

        'contains Normal fallback': (r) =>
            r.body.toLowerCase().includes('normal fallback'),
    });
}