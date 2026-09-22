package com.jasper;

import com.jasper.dao.OrderDao;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.annotation.Resource;

import java.util.List;
import java.util.Map;

@SpringBootTest
public class OrderDaoTest {


    @Resource
    private OrderDao orderDao;


    @Test
    void testInsertOrder() {

        orderDao.insertOrder();

    }

    @Test
    void selectAll() {

        List<Map<String, Object>> maps = orderDao.selectAll();
        for (Map<String, Object> map : maps) {
            System.out.println(map);
        }
    }

    @Test
    void selectByUserId() {
        // 根据userId 分库 带分片键 只去一个库   表没有分片键 union all 俩张表
        List<Map<String, Object>> maps = orderDao.selectByUserId(1001L);
        for (Map<String, Object> map : maps) {
            System.out.println(map);
        }
    }

    @Test
    void selectById() {
        // 根据Id 分表 带分片键 去俩个库 一张表
        List<Map<String, Object>> maps = orderDao.selectById(1309108431176597505L);
        for (Map<String, Object> map : maps) {
            System.out.println(map);
        }
    }
}