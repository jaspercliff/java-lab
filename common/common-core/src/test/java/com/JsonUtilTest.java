package com;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pojo.Person;
import com.jasper.utils.JacksonUtils;
import org.junit.jupiter.api.Test;

public class JsonUtilTest {

    @Test
    public void test1(){
        Person jasper = new Person("jasper", 2, null);
        String jsonString = JacksonUtils.toJsonString(jasper);
        System.out.println("jsonString = " + jsonString);

        String jsonString1 = JacksonUtils.toJsonString(jasper, JsonInclude.Include.NON_NULL);
        System.out.println("jsonString1 = " + jsonString1);

        Person person = new Person("", 2, null);
        String jsonString2 = JacksonUtils.toJsonString(person, JsonInclude.Include.NON_EMPTY);
        System.out.println("jsonString2 = " + jsonString2);
        Person person1 = JacksonUtils.ParseJson(jsonString, Person.class);
        System.out.println("person1 = " + person1);
        System.out.println("person1 = " + person1.getName());
    }
}
