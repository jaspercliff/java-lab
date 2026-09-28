package com.jasper.config;

import com.alibaba.cloud.nacos.annotation.NacosConfig;
import com.jasper.pojo.entity.Person;
import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
@Data
public class NacosConfigAnnoConfig {

    // 加载对应的配置的完整内容
    @NacosConfig(dataId = "infra-nacos.yml", group = "INFRA_NACOS_GROUP")
    private String content;
    /**
     * 加载配置中的指定key属性至基础类型字段
     * NacosConfig anno 不受其他属性源优先级影响，并且默认支持运行期动态更新
     */
    @NacosConfig(dataId = "infra-nacos.yml", group = "INFRA_NACOS_GROUP", key = "rate")
    private String rate;

    // WARN: 只支持int, long,float,double,boolean 5种基础类型数组以及其封装类型
    // 加载JSON格式配置至基础类型数组字段
    @NacosConfig(
            dataId = "infra-nacos.json",
            group = "INFRA_NACOS_GROUP",
            key = "ports"
    )
    private int[] ports;

    // 加载配置至Properties类型字段
    // 这里也支持yaml
    @NacosConfig(
            dataId = "infra-nacos.properties",
            group = "INFRA_NACOS_GROUP"
    )
    private Properties properties;

    //加载至自定义JavaBean字段
    @NacosConfig(
            dataId = "infra-nacos-person.json",
            group = "INFRA_NACOS_GROUP"
    )
    private Person person;

    @Bean
    // 加载JSON格式配置至工厂Bean
    @NacosConfig(
            dataId = "infra-nacos-person.json",
            group = "INFRA_NACOS_GROUP"
    )
    public Person personFactory() {
        return new Person();
    }
}
