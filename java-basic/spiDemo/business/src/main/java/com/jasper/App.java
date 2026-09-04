package com.jasper;

public class App {
    public static void main(String[] args) {
        StorageManager.doUpload("minio", new byte[0], "");
        StorageManager.doUpload("rustfs", new byte[0], "");
    }
}
