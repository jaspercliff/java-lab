package com.jasper.config;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

@Component
public class MemoryInfoContributor implements InfoContributor {

    @Override
    public void contribute(Info.Builder builder) {
        Runtime runtime = Runtime.getRuntime();

        long usedBytes = runtime.totalMemory() - runtime.freeMemory();
        long maxBytes = runtime.maxMemory();

        builder.withDetail("memory", java.util.Map.of(
                "usedMB", usedBytes / (1024.0 * 1024.0),
                "maxMB", maxBytes / (1024.0 * 1024.0)
        ));
    }
}