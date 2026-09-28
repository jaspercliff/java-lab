package com.jasper.listener;

import com.alibaba.cloud.nacos.annotation.NacosConfigListener;
import com.jasper.pojo.entity.Person;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Properties;

@Component
@Slf4j
public class NacosConfigListenerDemo {

    /**
     * initNotify=true在启动时接收一次当前配置
     * 这里可以指定key
     */
    @NacosConfigListener(dataId = "infra-nacos.yml", group = "INFRA_NACOS_GROUP")
    public void rate(String rateConfig) {
        System.out.println("receiveRateConfig:"+rateConfig);
    }

    /**
     * scoresArray.json : [100, 90, 80, 70, 90]
     * 基础类型数组参数方法接受JSON格式配置内容
     * 将配置内容反序列化为基础类型数组对象以scores参数回调scoresChanged方法
     * 支持int, long,float,double,boolean 5种基础类型数组
     */
    @NacosConfigListener(dataId = "scoresArray.json", group = "INFRA_NACOS_GROUP")
    private void scoresChanged(int[] scores) {
        info(Arrays.toString(scores));
    }

    /**
     * Properties参数方法接受属性参数
     */
    @NacosConfigListener(dataId = "infra-nacos.properties", group = "INFRA_NACOS_GROUP")
    private void propertiesChanged(Properties properties) {
        info(properties.toString());

    }

    /**
     * 自定义Java Bean参数
     */
    @NacosConfigListener(dataId = "infra-nacos-person.json", group = "INFRA_NACOS_GROUP")
    private void myObjectChanged(Person person) {
        info(person.toString());
    }


    private static void info(String scores) {
        log.info("receive:  {}", scores);
    }
}
