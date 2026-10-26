package com.jasper;

public class OrderService {

    private final StorageService storageService;

    public OrderService(StorageService storageService) {
        this.storageService = storageService;
    }

    public String createOrder(String commodityCode, int count) {
        int stock = storageService.getStock(commodityCode);

        if (stock < count) {
            throw new IllegalStateException("库存不足");
        }

        return "订单创建成功";
    }
}