package com.jasper;

public interface StorageService {
     String getName();
     void upload(String path, byte[] bytes);
}
