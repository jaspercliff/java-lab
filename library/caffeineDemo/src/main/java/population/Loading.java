package population;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.jasper.pojo.entity.Person;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class Loading {
    public static void main(String[] args) {
        int num = 10_000;
        final LoadingCache<Object, Person> loadingCache = Caffeine.newBuilder().expireAfterWrite(10, TimeUnit.MINUTES)
                .maximumSize(num).build(key -> repositoryGetById((Integer) key));

        // 第一次加载会触发load
        final Person person = loadingCache.get(2);
        log.info(person.toString());
        // 第二次直接获取缓存
        final Person person1 = loadingCache.get(2);
        log.info(person1.toString());
    }

    public static Person repositoryGetById(Integer key) {
        System.out.println("正在创建 person 对象：" + key);
        // 这里可以是复杂计算或数据库读取
        return new Person(2, "jasper");
    }
}
