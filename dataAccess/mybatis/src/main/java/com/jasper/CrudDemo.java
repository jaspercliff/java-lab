package com.jasper;

import com.jasper.mapper.SysUserMapper;
import com.jasper.pojo.entity.SysUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class CrudDemo {
    public static void main(String[] args) throws IOException {
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        SqlSession session = sqlSessionFactory.openSession();
        try {
            SysUserMapper mapper = session.getMapper(SysUserMapper.class);

            // 插入数据（适配更新后的 sys_user 结构，增加了 deptId、avatar 等字段）
            SysUser user = new SysUser();
            user.setDeptId(1L);
            user.setUsername("jasper");
            user.setPassword("123456");
            user.setNickname("贾斯珀");
            user.setAvatar("https://example.com/avatar.jpg");
            user.setEmail("jasper@example.com");
            user.setPhone("13800138000");
            user.setStatus(1);
            user.setIsDeleted(0);

            SysUser user1 = new SysUser();
            user1.setDeptId(1L);
            user1.setUsername("jasper_param");
            user1.setPassword("123456");
            user1.setNickname("贾斯珀参数");
            user1.setAvatar("https://example.com/avatar1.jpg");
            user1.setEmail("jasper_param@example.com");
            user1.setPhone("13800138001");
            user1.setStatus(1);
            user1.setIsDeleted(0);

            mapper.insertSysUser(user);
            mapper.insertSysUserWithParam(user1);

            session.commit();

            // 测试根据 ID 查询
            if (user.getId() != null) {
                SysUser foundUser = mapper.selectById(user.getId());
                log.info("查询到的用户信息: {}", foundUser);
            }
        } catch (Exception e) {
            log.error("操作失败: {}", e.getMessage(), e);
            session.rollback();
        } finally {
            session.close();
        }
    }
}