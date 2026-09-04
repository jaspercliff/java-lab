package com.jasper.include;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jasper.JacksonUtils;

import java.util.Optional;

public class Demo {
    public static void main(String[] args) {
        final Person person1 = new Person();
        person1.setId(null); // always
        person1.setName(null); //nonNull
        person1.setAddress(""); //nonempty
        person1.setAge(0);//nondefault
        person1.setPhone("123");//useDefault
        person1.setDescription("special");//custom
        person1.setRole(null);//nonabsent
        final String jsonString = JacksonUtils.toJsonString(person1);
        System.out.println(jsonString);

        final Person person2 = new Person();
        person2.setId(1); // always
        person2.setName("jasper"); //nonNull
        person2.setAddress("123"); //nonempty
        person2.setAge(0);//nondefault
        person2.setPhone("");//useDefault
        person2.setDescription("special");//custom
        person2.setRole(Optional.ofNullable(new Role()).get());//nonabsent
        final String jsonString1 = JacksonUtils.toJsonString(person2, JsonInclude.Include.NON_NULL);
        System.out.println(jsonString1);
    }
}
