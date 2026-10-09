package com.jasper.dao;

import com.jasper.pojo.entity.SysUser;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
@Slf4j
public class SysUserRepositoryTest {

    @Autowired
    private SysUserRepository sysUserRepository;

    private static void info(SysUser sysUser) {
        log.info("sysUser:{}", sysUser);
    }

    @Test
    public void save() {
        SysUser sysUser = new SysUser();
        sysUser.setUsername("jasper");
        sysUser.setPassword("123456");
        sysUserRepository.save(sysUser);
    }

    @Test
    public void findById() {
        SysUser sysUser = sysUserRepository.findById(2L);
        info(sysUser);
    }

    @Test
    public void findByUsername() {
        SysUser sysUser = sysUserRepository.findByUsername("jasper");
        info(sysUser);
    }


    @Test
    public void findAll() {
        List<SysUser> list = sysUserRepository.findAll();
        log.info("list:{}", list);
    }

    @Test
    public void deleteById() {
        sysUserRepository.deleteById(1L);
    }
}
