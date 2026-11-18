package com.jasper.jackson2.serial.jsonSerializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Demo {
    public static void main(String[] args) throws JsonProcessingException {
        UserVO jasper = new UserVO(1L, "jasper", "15399996666");
        ObjectMapper objectMapper = new ObjectMapper();
        String s = objectMapper.writeValueAsString(jasper);
        log.info("s: {}",s);
    }
}
