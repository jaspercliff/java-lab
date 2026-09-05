package com.jasper;

import com.jasper.mapper.SysUserRoleTestMapper;
import com.jasper.pojo.dto.SysUserRoleDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

/**
 * 二级缓存是 Mapper 级别（Namespace 级别）的缓存。多个 SqlSession 可以共用同一个 Mapper 的二级缓存，跨 SqlSession 生效 <br>
 * 在对应的 Mapper XML 文件中配置 <cache/> 标签，或者在 Mapper 接口上使用 @CacheNamespace 注解 <br>
 *
 * 多表关联脏读：二级缓存是基于单 Namespace 的。如果查询涉及多表关联（例如 SysUser 关联 SysRole），
 * 当修改了 SysRole 表时，SysUserMapper 的二级缓存不会被清空，从而导致查询到旧的脏数据。
 * 分布式局限：如果系统部署在多台服务器上（集群），MyBatis 默认的二级缓存是在单机内存中的，多台服务器之间无法同步缓存
 */
@Slf4j
public class CacheTestDemo {
    public static void main(String[] args) throws IOException {
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

        // --- 第一步：开启第一个会话进行查询（数据会写入二级缓存） ---
        try (SqlSession session1 = sqlSessionFactory.openSession()) {
            SysUserRoleTestMapper mapper1 = session1.getMapper(SysUserRoleTestMapper.class);
            log.info("--- 第一次查询（将数据写入二级缓存） ---");
            SysUserRoleDTO userDto1 = mapper1.selectUserWithRoles(1L);
            log.info("查询结果: {}", userDto1);

            // 必须 commit 或 close 后，数据才会真正进入二级缓存
            session1.commit();
        }

        // --- 第二步：模拟外部修改了 sys_role 表的数据 ---
        try (SqlSession sessionUpdate = sqlSessionFactory.openSession()) {
            SysUserRoleTestMapper updateMapper = sessionUpdate.getMapper(SysUserRoleTestMapper.class);
            log.info("--- 模拟修改 sys_role 表中角色名称 ---");
            updateMapper.updateRoleName(1L, "超级无敌管理员_被修改");
            sessionUpdate.commit();
        }

        // --- 第三步：开启第二个会话再次查询（验证二级缓存脏读） ---
        try (SqlSession session2 = sqlSessionFactory.openSession()) {
            SysUserRoleTestMapper mapper2 = session2.getMapper(SysUserRoleTestMapper.class);
            log.info("--- 第二次查询（期望获取修改后的名称，但由于二级缓存未清空，读到了旧缓存） ---");
            SysUserRoleDTO userDto2 = mapper2.selectUserWithRoles(1L);
            log.info("第二次查询结果: {}", userDto2);
        }
    }
}