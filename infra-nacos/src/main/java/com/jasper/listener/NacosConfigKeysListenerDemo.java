package com.jasper.listener;

import com.alibaba.cloud.nacos.annotation.NacosConfigKeysListener;
import com.alibaba.nacos.api.config.ConfigChangeEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NacosConfigKeysListenerDemo {

    /**
     * 支持properties及yaml格式配置中指定keys发生变更时，通过ConfigChangeEvent参数接受指定keys变更前后的内容
     * 通过interestedKeys指定监听的keys集合，通过interestedKeyPrefixes指定需要监听的key前缀集合，如果符合任意任一条件的key发生变化时都会触发回调
     */
    @NacosConfigKeysListener(dataId = "infra-nacos-keylistener.properties", group = "INFRA_NACOS_GROUP",
            interestedKeys = {"name"}, interestedKeyPrefixes = {"prefix."})
    private void onKeysChangeSingle(ConfigChangeEvent changeEvent) {
        log.info("interestedKeyPrefixes:nacos.{}", changeEvent.getChangeItems());
    }
}
