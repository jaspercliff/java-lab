package jasper.basic.collections.map;

import com.google.common.collect.ImmutableMap;

import com.jasper.pojo.entity.Person;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jasper
 * @since 2026-04-23 15:27:40
 */
@Slf4j
public class ShallowImmutableMap {
    public static void main(String[] args) {
        final HashMap<String, Person> map = new HashMap<>();
        final Person jasper = new Person(1, "jasper");
        final Person cliff = new Person(2, "cliff");
        map.put("1", jasper);
        map.put("2", cliff);

        jasper.setName("jasper1");

        final Map<String, Person> collectionsMap = Collections.unmodifiableMap(map);
        log.info("collectionMap:{}", collectionsMap);
        final ImmutableMap<String, Person> guavaMap = ImmutableMap.copyOf(map);
        log.info("guavaMap:{}", guavaMap);
        // since jdk 10
        final Map<String, Person> mapMap = Map.copyOf(map);
        log.info("mapMap:{}", mapMap);
    }
}
