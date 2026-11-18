package com.jasper.jackson2.serial.ContextualSerializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Demo {
    public static void main(String[] args) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        UserVo userVo = new UserVo("19929998110", "2190320000@qq.com");
        String s = mapper.writeValueAsString(userVo);
        log.info(s);
    }
}
