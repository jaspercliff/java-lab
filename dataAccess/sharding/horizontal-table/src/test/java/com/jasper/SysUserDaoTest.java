package com.jasper;

import com.jasper.dao.SysUserDao;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

@Slf4j
@SpringBootTest
public class SysUserDaoTest {

    @Resource
    private SysUserDao sysUserDao;
    @Test
    public void test1() {
        sysUserDao.insert(
                "tom",
                "123456",
                "Tom",
                "tom@example.com",
                "13900139000"
        );
    }

    @Test
    public void test2() {
        List<Map<String, Object>> list = sysUserDao.list();
        log.info("list={}", list);
    }

    /**
     *  test transactionalReadQueryStrategy
     */
    @Test
    public void test3() {
        Map<String, Object> andQuery = sysUserDao.createAndQuery();
        log.info("andQuery={}", andQuery);
    }


}
