package population;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.jasper.pojo.entity.Person;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class AsyncLoading {
    public static void main(String[] args) {
        final AsyncLoadingCache<Object, Person> loadingCache = Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .maximumSize(100)
                .buildAsync(((key) -> repositoryGetById((long) key)));
        // 第一次加载会触发load
        final CompletableFuture<Person> person = loadingCache.get(2L);
        // 第二次直接获取缓存
        final CompletableFuture<Person> person1 = loadingCache.get(2L);
        System.out.println("person = " + person.join());
        System.out.println("person2 = " + person1.join());
    }

    public static Person repositoryGetById(long key) {
        System.out.println("正在创建 person 对象：" + key);
        // 这里可以是复杂计算或数据库读取
        return new Person(2, "jasper");
    }
}
