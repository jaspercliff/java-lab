# readme

- [official website](https://resilience4j.readme.io/docs/getting-started-3)

Resilience4j是一个轻量级容错框架，设计灵感来源于Netflix 的Hystrix框架，为函数式编程所设计。

Resilience4j 提供了一组高阶函数（装饰器），包括断路器，限流器，重试，隔离，可以对任何的函数式接口，lambda表达式，
或方法的引用进行增强，并且这些装饰器可以进行叠加。这样做的好处是，你可以根据需要选择特定的装饰器进行组合


Retry：失败了再试几次
CircuitBreaker：失败太多直接“断路”
Bulkhead：限制同时调用数量
RateLimiter：限制请求速率
TimeLimiter：限制异步调用时间
Fallback：最终失败后返回降级结果