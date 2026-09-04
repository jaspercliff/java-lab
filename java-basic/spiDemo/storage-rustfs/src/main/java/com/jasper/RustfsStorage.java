package com.jasper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RustfsStorage implements StorageService{
    @Override
    public String getName() {
        return "rustfs";
    }

    @Override
    public void upload(String path, byte[] bytes) {
        log.info("rustfs upload");
    }
}
