package com.jasper;


import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Demo1 {
    public static void main(String[] args) throws IOException {
//        json到    object
        String studentJson = "{\"name\":\"jasper\",\"age\":18}";
        ObjectMapper objectMapper = new ObjectMapper();
        Student student = objectMapper.readValue(studentJson, Student.class);
        System.out.println(student);
//        json字符输入流到   object
        StringReader stringReader = new StringReader(studentJson);
        Student student1 = objectMapper.readValue(stringReader, Student.class);
        System.out.println("student1 = " + student1);
//       json 文件到 object
        Student student2 = objectMapper.readValue(new File("D:\\java\\springboot\\jackson\\src\\main\\java\\com\\jasper\\Student.json"), Student.class);
        System.out.println("student2 = " + student2);
//       via url
//        Student student3 = objectMapper.readValue(new URL("file:///D:/java/springboot/jackson/src/main/java/com/jasper/Student.json"), Student.class);
//        System.out.println("student3 = " + student3);
//      via byte input stream
        Student student4 = objectMapper.readValue(new FileInputStream("D:\\java\\springboot\\jackson\\src\\main\\java\\com\\jasper\\Student.json"), Student.class);
        System.out.println("student4 = " + student4);
//        byte[]
        byte[] bytes = studentJson.getBytes("UTF-8");
        Student student5 = objectMapper.readValue(bytes, Student.class);
        System.out.println("student5 = " + student5);
//        json array
        String jsonArray = "[{\"name\":\"ford\"}, {\"name\":\"Fiat\"}]";
        Car[] cars = objectMapper.readValue(jsonArray, Car[].class);
        System.out.println("cars = " + Arrays.toString(cars));
//        json array to list
        List<Car> cars1 = objectMapper.readValue(jsonArray, new TypeReference<List<Car>>() {});
        System.out.println("cars1 = " + cars1);
//        json str to map
        ObjectMapper objectMapper1 = new ObjectMapper();
        Map<String, Object> jsonMap = objectMapper1.readValue(studentJson,
                new TypeReference<Map<String,Object>>(){});
        System.out.println("jsonMap = " + jsonMap);
//        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

//        java object --> json
        Student student6 = new Student();
        student6.setName("cliff");
        student6.setAge(21);
//        写入到指定的输出流中
        objectMapper.writeValue(new File("D:\\java\\springboot\\jackson\\src\\main\\java\\com\\jasper\\Student6.json"), student6);
//        写入到一个字符串中
        String stuJson = objectMapper.writeValueAsString(student6);
        System.out.println("stuJson = " + stuJson);
    }
}
