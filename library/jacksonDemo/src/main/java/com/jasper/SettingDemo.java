package com.jasper;

import com.jasper.pojo.entity.Cup;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectWriter;
import tools.jackson.databind.SerializationFeature;

@Slf4j
public class SettingDemo {
    public static void main(String[] args) {
        ObjectMapper objectMapper = new ObjectMapper();
        //将 Java 对象序列化为 JSON，可以针对某次序列化设置格式化、视图等选项
        ObjectWriter writer = objectMapper.writer().with(SerializationFeature.INDENT_OUTPUT);
        Cup haha = new Cup("haha", 10);
        String s = writer.writeValueAsString(haha);
        log.info(s);
    }
}
