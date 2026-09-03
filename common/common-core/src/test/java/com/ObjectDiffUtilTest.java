package com;

import com.jasper.result.FieldDifference;
import com.pojo.Person;

import org.junit.jupiter.api.Test;

import java.util.List;

public class ObjectDiffUtilTest {

    @Test
    public void test() throws IllegalAccessException {

        // 示例：假设有一个Person类，包含name和age字段
        Person person1 = new Person("John", 30, null);
        Person person2 = new Person("Jane", 30, null);

        List<FieldDifference> diffs = ObjectDiffUtil.compare(person1, person2);
        for (FieldDifference diff : diffs) {
            System.out.println(diff);
        }
    }
}
