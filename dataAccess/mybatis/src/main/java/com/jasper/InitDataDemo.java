package com.jasper;

import com.jasper.mapper.SysUserRoleTestMapper;
import com.jasper.pojo.entity.SysUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class InitDataDemo {
    public static void main(String[] args) throws IOException {
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

        try (SqlSession session = sqlSessionFactory.openSession()) {
            SysUserRoleTestMapper mapper = session.getMapper(SysUserRoleTestMapper.class);

            // 1. 插入测试用户 (ID = 1)
            SysUser user = new SysUser();
            user.setId(1L);
            user.setUsername("jasper");
            user.setNickname("贾斯珀");
            user.setStatus(1);
            user.setPassword("123456");
            mapper.insertUser(user);

            // 2. 插入测试角色 (ID = 1)
            mapper.insertRole(1L, "系统管理员", "admin");

            // 3. 建立用户与角色的关联
            try {
                mapper.insertUserRole(1L, 1L);
            } catch (Exception e) {
                // 防止重复插入联合主键报错
                log.info("用户与角色关联已存在，跳过插入");
            }

            session.commit();
            log.info("--- 测试数据初始化成功！现在可以运行 CacheTestDemo 了 ---");
        } catch (Exception e) {
            log.error("初始化数据失败: {}", e.getMessage(), e);
        }
    }
}