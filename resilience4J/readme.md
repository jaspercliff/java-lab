# readme

- [official website](https://resilience4j.readme.io/docs/getting-started-3)
- [Chinese guide](https://github.com/lmhmhl/Resilience4j-Guides-Chinese)

Resilience4j是一个轻量级容错框架，设计灵感来源于Netflix 的Hystrix框架，为函数式编程所设计。

Resilience4j 提供了一组高阶函数（装饰器），包括断路器，限流器，重试，隔离，可以对任何的函数式接口，lambda表达式，
或方法的引用进行增强，并且这些装饰器可以进行叠加。这样做的好处是，你可以根据需要选择特定的装饰器进行组合


Retry：失败了再试几次
CircuitBreaker：失败太多直接“断路”
Bulkhead：限制同时调用数量 并发数 (Concurrency)：限制同时在处理的请求数量
RateLimiter：限制请求速率 限制单位时间内（如每秒）允许通过的请求总数
TimeLimiter：限制异步调用时间
Fallback：最终失败后返回降级结果

## use

```kotlin

dependencies {
  implementation("io.github.resilience4j:resilience4j-circuitbreaker:${property("resilience4jVersion")}")
  implementation("io.github.resilience4j:resilience4j-ratelimiter:${property("resilience4jVersion")}")
  implementation("io.github.resilience4j:resilience4j-retry:${property("resilience4jVersion")}")
  implementation("io.github.resilience4j:resilience4j-bulkhead:${property("resilience4jVersion")}")
  implementation("io.github.resilience4j:resilience4j-cache:${property("resilience4jVersion")}")
  implementation("io.github.resilience4j:resilience4j-timelimiter:${property("resilience4jVersion")}")
}

```

If you want to use everything:

```Text kotlin
dependencies {
  implementation("io.github.resilience4j:resilience4j-all:${resilience4jVersion}")
}
```



## circuitbreaker 

基于访问数量的滑动窗口是通过一个有N个元素的循环数组实现

- 先看公共接口 Registry：了解系统对外暴露的基本能力。
- 再看专用接口 CircuitBreakerRegistry：确认它增加了什么能力。
- 接着看抽象类 AbstractRegistry：找到公共实现、默认行为和扩展点。
- 最后看具体类 InMemoryCircuitBreakerRegistry：弄清楚内存存储、缓存或其他具体逻辑是怎么实现的
