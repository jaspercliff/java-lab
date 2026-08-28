package population;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.jasper.pojo.entity.Person;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public class Manual {
    public static void main(String[] args) {
        int num = 10_000;
        final Cache<Object, Person> cache = Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .maximumSize(num).build();
        // 查找缓存 没有则返回null
        final Person person1 = cache.getIfPresent("1");
        System.out.println("person1 = " + person1);
        // 查找缓存 没有则创建 无法创建则返回null 推荐使用该方法
        final Person person2 = cache.get(2, k -> repositoryGetById(2));
        System.out.println("person2 = " + person2);
        // put 操作会直接写入或者更新已经存在的缓存
        cache.put(3, new Person(3, "cliff"));
        final Person person3 = cache.getIfPresent(3);
        System.out.println("person3 = " + person3);
        // 移除缓存
        cache.invalidate(3);
        System.out.println("invalidate person3 = " + person3);

        // 返回concurrentHashMap 调试 监控 批量操作
        final ConcurrentMap<Object, Person> concurrentHashMap = cache.asMap();
        concurrentHashMap.forEach((c, person) -> {
            System.out.println("key = " + c + " person = " + person);
        });

        final Person person = concurrentHashMap.get(2);
        person.setAge(18);

        System.out.println(cache.getIfPresent(2));
    }

    public static Person repositoryGetById(long key) {
        System.out.println("正在创建 person 对象：" + key);
        // 这里可以是复杂计算或数据库读取
        return new Person(2, "jasper");
    }
}
