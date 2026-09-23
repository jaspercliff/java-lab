package com.jasper.dao;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class SysUserDao {

    private final JdbcTemplate jdbcTemplate;

    /**
     * 创建用户
     */
    public int insert(String username,
                      String password,
                      String nickname,
                      String email,
                      String phone) {

        String sql = """
                INSERT INTO sys_user
                    (username, password, nickname, email, phone)
                VALUES
                    (?, ?, ?, ?, ?)
                """;

        return jdbcTemplate.update(
                sql,
                username,
                password,
                nickname,
                email,
                phone
        );
    }

    /**
     * 查询用户列表
     */
    public List<Map<String, Object>> list() {

        String sql = """
                SELECT
                    id,
                    username,
                    nickname,
                    email,
                    phone,
                    status,
                    created_at
                FROM sys_user
                WHERE is_deleted = 0
                ORDER BY id
                """;

        return jdbcTemplate.queryForList(sql);
    }

    @Transactional
    public Map<String, Object> createAndQuery() {

        this.insert(
                "tom1",
                "123456",
                "Tom1",
                "tom1@example.com",
                "13900139001"
        );

        // 紧接着查询刚刚插入的数据
        return this.findById(1L);
    }

    /**
     * 根据 ID 查询用户
     */
    public Map<String, Object> findById(Long id) {

        String sql = """
                SELECT
                    id,
                    dept_id,
                    username,
                    password,
                    nickname,
                    avatar,
                    email,
                    phone,
                    status,
                    is_deleted,
                    created_at,
                    updated_at
                FROM sys_user
                WHERE id = ?
                """;

        return jdbcTemplate.queryForMap(sql, id);
    }


}