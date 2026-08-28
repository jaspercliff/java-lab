package population;

import com.github.benmanes.caffeine.cache.AsyncCache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class AsyncManual {
    public static void main(String[] args) {
        final AsyncCache<Object, Object> cache = Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .maximumSize(100).buildAsync();
        cache.put("1", CompletableFuture.completedFuture("jasper"));
        final CompletableFuture<Object> key1 = cache.getIfPresent("1");
        final CompletableFuture<Object> key2 = cache.get("2", (key) -> key + "cliff");
        System.out.println("key1 = " + key1.join());
        System.out.println("key2 = " + key2.join());
    }
}
