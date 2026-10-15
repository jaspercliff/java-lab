package com.jasper.rateLimiter;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import io.github.resilience4j.core.RegistryStore;
import io.github.resilience4j.ratelimiter.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;

import java.time.Duration;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;

/**
 * 默认情况下，Resilience4j 的 RateLimiterRegistry 会把所有创建出来的限流器实例
 * （比如名字叫 "backend"、"orderService" 的实例）永久保存在内存的一个内部 Map 中，直到应用程序关闭
 * 但在一些特殊场景下（比如多租户系统、动态 API 网关），系统可能会动态创建成千上万个不同的限流器实例
 *  <br>
 * 通过 .withRegistryStore(...)，你可以接入自定义的存储容器（例如结合 Caffeine Cache 或 Redis）。
 * 当某个限流器在一段时间内（比如 30 分钟）没有被访问时，自动将其从内存中清除（Evict），释放资源
 */
@Slf4j
@SuppressWarnings("all")
public class CacheRateLimiterRegistryStore implements RegistryStore<RateLimiter> {

    // 使用 Caffeine 缓存：30分钟无访问自动回收，最多容纳 10000 个实例
    private final Cache<String, RateLimiter> cache = Caffeine.newBuilder()
            .expireAfterAccess(Duration.ofMinutes(30))
            .maximumSize(10000)
            .recordStats()
            .build();

    /**
     * 首次获取某个名称的限流器时触发
     */
    @Override
    @NonNull
    public RateLimiter computeIfAbsent(@NonNull String key, @NonNull Function<? super String,
            ? extends RateLimiter> mappingFunction) {
        printCacheStats();
        // 利用 Caffeine 的 asMap() 提供线程安全的原子计算与加载
        return cache.asMap().computeIfAbsent(key, mappingFunction);
    }

    @Override
    public  RateLimiter putIfAbsent(@NonNull String key, @NonNull RateLimiter value) {
        //如果 key 不存在则放入，并返回旧值（若原本存在则返回已有值）
        return cache.asMap().putIfAbsent(key, value);
    }

    @Override
    public Optional<RateLimiter> find(@NonNull String key) {
        return Optional.ofNullable(cache.getIfPresent(key));
    }

    @Override
    public Optional<RateLimiter> remove(@NonNull String name) {
        RateLimiter removed = cache.getIfPresent(name);
        if (removed != null) {
            cache.invalidate(name);
        }
        return Optional.ofNullable(removed);
    }

    @Override
    public Optional<RateLimiter> replace(@NonNull String name, @NonNull RateLimiter newEntry) {
        RateLimiter old = cache.getIfPresent(name);
        if (old != null) {
            cache.put(name, newEntry);
        }
        return Optional.ofNullable(old);
    }

    @Override
    public Collection<RateLimiter> values() {
        return cache.asMap().values();
    }
    public void printCacheStats() {
        CacheStats stats = cache.stats();
        log.info("=== Caffeine Cache Stats ===");
        log.info("缓存命中次数 (Hit Count): {}", stats.hitCount());
        log.info("缓存未命中次数 (Miss Count): {}", stats.missCount());
        log.info("缓存命中率 (Hit Rate): {}%", stats.hitRate() * 100);
        log.info("驱逐总数 (Eviction Count): {}", stats.evictionCount());
        log.info("当前缓存实例总数 (Current Size): {}", cache.estimatedSize());
    }
}