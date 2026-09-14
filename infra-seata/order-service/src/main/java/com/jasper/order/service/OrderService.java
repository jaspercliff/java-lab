package com.jasper.order.service;

import com.jasper.order.entity.Order;
import com.jasper.order.feign.AccountClient;
import com.jasper.order.feign.StorageClient;
import com.jasper.order.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.seata.spring.annotation.GlobalTransactional;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderMapper orderMapper;
    private final StorageClient storageClient;
    private final AccountClient accountClient;

    @GlobalTransactional(name = "create-order", rollbackFor = Exception.class)
    public void create(String userId, String commodityCode, int count) {
        log.info("Creating order: userId={}, commodityCode={}, count={}", userId, commodityCode, count);

        // 1. Calculate money (hypothetical 100 per item)
        int money = count * 100;

        // 2. Create order locally
        Order order = new Order();
        order.setUserId(userId);
        order.setCommodityCode(commodityCode);
        order.setCount(count);
        order.setMoney(money);
        orderMapper.insert(order);
        log.info("Order created locally: {}", order.getId());

        // 3. Deduct stock via Feign
        log.info("Calling storage-service to deduct stock...");
        storageClient.deduct(commodityCode, count);

        // 4. Deduct money via Feign
        log.info("Calling account-service to deduct balance...");
        accountClient.debit(userId, money);

        log.info("Order process completed successfully.");
    }
}
