package com.jasper.config;

import com.alibaba.cloud.nacos.annotation.NacosConfig;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
//加载JSON格式配置至SpringBean
@NacosConfig(
        dataId = "infra-nacos-person.json",
        group = "INFRA_NACOS_GROUP"
)
public class NacosConfigAnnoConfig1 {
    private String name;
    private int age;
    private String city;
}
