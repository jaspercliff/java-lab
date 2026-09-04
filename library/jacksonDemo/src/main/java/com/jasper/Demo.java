package com.jasper;


import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

/**
 * @version 1.0
 * @Author jasper
 * @Date 2024-10-21
 */
public class Demo {
    public static void main(String[] args) {
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = "{\"userId\":\"4\",\"userName\":\"test\",\"userType\":\"TELLER_USER\"," +
                "\"branch\":\"351001\",\"accountStatus\":\"A\"}";
        JsonNode jsonNode = mapper.readTree(jsonString);
        System.out.println("jsonNode = " + jsonNode);
        ObjectNode objectNode = (ObjectNode)mapper.readTree(jsonString);
        System.out.println("objectNode = " + objectNode);
    }
}
