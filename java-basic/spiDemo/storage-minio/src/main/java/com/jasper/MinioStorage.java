package com.jasper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MinioStorage implements StorageService {
    @Override
    public String getName() {
        return "minio";
    }

    @Override
    public void upload(String path, byte[] bytes) {
        log.info("minio upload");
    }
}