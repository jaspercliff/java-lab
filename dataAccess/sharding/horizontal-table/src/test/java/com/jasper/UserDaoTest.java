package com.jasper;

import com.jasper.dao.UserDao;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

@Slf4j
@SpringBootTest
public class UserDaoTest {
    @Resource
    private UserDao userDao;
    @Test
    public void test() {
        userDao.create();
    }

    @Test
    public void test2() {
        Map<String, Object> stringObjectMap = userDao.get(1L);
        log.info("{}", stringObjectMap);
    }
}
