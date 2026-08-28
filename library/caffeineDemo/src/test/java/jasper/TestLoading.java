package jasper;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import com.jasper.pojo.entity.Person;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

@Slf4j
public class TestLoading {

    @Test
    public void test() throws InterruptedException {
        LoadingCache<Integer, Person> cache = Caffeine.newBuilder()
                .maximumSize(2)
                .expireAfterWrite(2, TimeUnit.SECONDS)
                // 数据收集功能 Cache.stats()
                .recordStats()
                .removalListener((Integer key, Person value, RemovalCause cause) -> {
                    log.info("key: {}, value: {}, cause: {}", key, value, cause);
                })
                .build(key -> new Person((Integer) key, "NAME" + key));

        cache.get(1);
        cache.get(2);
        cache.get(3); // lru 存 tinylru 清除

        cache.cleanUp(); // 强制让当前线程去处理那些排队中的清理任务

        cache.get(10);
        Thread.sleep(3000); // expireAfterWrite=2s 会移除所有key
        cache.get(10);

        // 输出缓存统计信息
        CacheStats stats = cache.stats();
        log.info("Cache Stats: {}", stats);
    }
}
