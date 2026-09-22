package com.jasper;

import com.jasper.dao.DictDao;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DictDaoTest {

    @Resource
    private DictDao dictDao;

    @Test
    public void test(){
        dictDao.insert(
                1L,
                "gender",
                "MALE",
                "男",
                1
        );

        dictDao.insert(
                2L,
                "gender",
                "FEMALE",
                "女",
                2
        );

        dictDao.deleteById(2L);
    }

    @Test
    void test1(){
        dictDao.insert(
                3L,
                "user_status",
                "1",
                "正常",
                1
        );

        dictDao.insert(
                4L,
                "user_status",
                "0",
                "禁用",
                2
        );
    }
}
