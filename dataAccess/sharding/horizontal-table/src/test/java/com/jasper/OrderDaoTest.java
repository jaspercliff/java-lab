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
        List<Map<String, Object>> maps = orderDao.selectByUserId(1001L);
        for (Map<String, Object> map : maps) {
            System.out.println(map);
        }
    }
}