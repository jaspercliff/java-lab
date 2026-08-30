# index

Jedis：同步、阻塞、API 直接，适合普通 Java 应用
Lettuce：异步、非阻塞、基于 Netty，适合 Spring / 高并发 / Reactive

jedis 线程不安全 所以得配合线程池使用 新版本自动引入了 commons-pool 不需要手动引入了 

spring-boot-starter-data-redis 默认使用lettuce 
如果需要使用jedis 则单独导入jedis 依赖 但是版本不需要手动指定 starter指定了已经
