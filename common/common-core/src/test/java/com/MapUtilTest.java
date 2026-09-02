package com;

import com.pojo.Person;
import com.jasper.lang.MapUtil;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MapUtilTest {

    @Test
    public void test() {
        List<Person> users = Arrays.asList(
                new Person("Alice", 1),
                new Person("Bob", 2),
                new Person("Charlie", 3)
        );

        // 将用户列表按 id 转换为 Map<id, User>
        Map<Integer, Person> userMap = MapUtil.listToMap(users, Person::getAge);
        System.out.println("userMap = " + userMap);
    }

}
