package com.jasper;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

public class StorageManager {
    private static final Map<String, StorageService> SERVICES = new HashMap<>();

    static {
        // 静态代码块只执行一次，扫描 classpath 下所有的存储实现
        ServiceLoader<StorageService> loader = ServiceLoader.load(StorageService.class);
        for (StorageService service : loader) {
            SERVICES.put(service.getName(), service);
        }
    }

    public static void doUpload(String type, byte[] data, String path) {
        StorageService service = SERVICES.get(type);
        if (service != null) {
            service.upload(path,data);
        } else {
            throw new RuntimeException("未找到类型为 " + type + " 的存储驱动");
        }
    }
}