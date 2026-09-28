package com.jasper.listener;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Trigger specific actions when variables are updated
 * 某个属性发生变化时触动程序执行一个业务动作或者在变更后的配置基础上在代码中做二次处理时 <br>
 * 动态调整线程池参数：监听到配置变更后，手动调用 ThreadPoolExecutor.setCorePoolSize() 等方法生效
 * 清理缓存 重新加载某些规则
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ConfigListenerDemo {

    /**
     * Nacos dataId.
     */
    public static final String DATA_ID = "infra-nacos.yml";

    /**
     * Nacos group.
     */
    public static final String GROUP = "INFRA_NACOS_GROUP";

    private final NacosConfigManager nacosConfigManager;

    @PostConstruct
    public void init() throws NacosException {
        ConfigService configService = nacosConfigManager.getConfigService();

        configService.addListener(DATA_ID, GROUP, new Listener() {
            @Override
            public Executor getExecutor() {
                return Executors.newSingleThreadExecutor();
            }

            @Override
            public void receiveConfigInfo(String configInfo) {
                log.info("[dataId]:[" + DATA_ID + "],Configuration changed to:{}", configInfo);
            }
        });
    }
}
