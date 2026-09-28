package com.jasper.controller;

import com.jasper.config.ConfigurationPropertiesConfig;
import com.jasper.config.NacosConfigAnnoConfig;
import com.jasper.config.NacosConfigAnnoConfig1;
import com.jasper.config.ValueAnnoConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Slf4j
@RestController
@RequestMapping("nacos")
@RequiredArgsConstructor
public class ConfigController {
    private final ConfigurationPropertiesConfig config;
    private final ValueAnnoConfig valueAnnoConfig;
    private final NacosConfigAnnoConfig nacosConfigAnnoConfig;
    private final NacosConfigAnnoConfig1 nacosConfigAnnoConfig1;

    @GetMapping("configurationProperties")
    public Map<String, String> nacos() {
        HashMap<String, String> map = new HashMap<>();
        map.put("name", config.getName());
        map.put("age", config.getAge());
        map.put("sex", config.getSex());
        return map;
    }

    @GetMapping("valueAnnotation")
    public Map<String, String> valueAnnotation() {
        HashMap<String, String> map = new HashMap<>();
        map.put("name", valueAnnoConfig.getName());
        map.put("age", valueAnnoConfig.getAge());
        map.put("hobby", valueAnnoConfig.getHobby());
        return map;
    }

    @GetMapping("nacosConfigAnno")
    public Map<String, String> nacosConfigAnno() {
        HashMap<String, String> map = new HashMap<>();
        // 加载对应的配置的完整内容
        map.put("content", nacosConfigAnnoConfig.getContent());
        // 加载配置中的指定key属性至基础类型字段
        map.put("rate", nacosConfigAnnoConfig.getRate());
        // WARN: 只支持int, long,float,double,boolean 5种基础类型数组以及其封装类型
        // 加载JSON格式配置至基础类型数组字段
        map.put("ports", Arrays.toString(nacosConfigAnnoConfig.getPorts()));
        //    加载配置至Properties类型字段
        //    这里也支持yaml
        Properties properties = nacosConfigAnnoConfig.getProperties();
        properties.forEach((k, v) -> {
            map.put(k.toString(), v.toString());
        });
        //加载至自定义JavaBean字段
        map.put("person-java bean field", nacosConfigAnnoConfig.getPerson().toString());
        //加载JSON格式配置至SpringBean
        map.put("person1-spring bean",nacosConfigAnnoConfig1.toString());
        // 加载JSON格式配置至工厂Bean
        map.put("person2-factory bean",nacosConfigAnnoConfig.personFactory().toString());
        return map;
    }
}
