package com.jasper.repository;

import com.jasper.pojo.entity.SysUser;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SysUserRepository {

    private final JdbcClient jdbcClient;

    private static final String COLUMNS = """
            id, dept_id, username, password,
            nickname, avatar, email, phone,
            status, is_deleted, created_at, updated_at
            """;

    /**
     * 新增用户
     */
    public SysUser insert(SysUser user) {
        String sql = """
            INSERT INTO sys_user (
                dept_id, username, password, nickname,
                avatar, email, phone
            )
            VALUES (
                :deptId, :username, :password, :nickname,
                :avatar, :email, :phone
            )
            RETURNING %s
            """.formatted(COLUMNS);

        return jdbcClient.sql(sql)
                .paramSource(new BeanPropertySqlParameterSource(user))
                .query(SysUser.class)
                .single();
    }

    /**
     * 根据 ID 查询未删除用户
     */
    public Optional<SysUser> findById(Long id) {

        String sql = """
                SELECT %s
                FROM sys_user
                WHERE id = :id
                  AND is_deleted = 0
                """.formatted(COLUMNS);

        return jdbcClient.sql(sql)
                .param("id", id)
                .query(SysUser.class)
                .optional();
    }

    /**
     * 根据用户名查询
     */
    public Optional<SysUser> findByUsername(String username) {

        String sql = """
                SELECT %s
                FROM sys_user
                WHERE username = :username
                  AND is_deleted = 0
                """.formatted(COLUMNS);

        return jdbcClient.sql(sql)
                .param("username", username)
                .query(SysUser.class)
                .optional();
    }

    /**
     * 根据部门查询用户
     */
    public List<SysUser> findByDeptId(Long deptId) {

        String sql = """
                SELECT %s
                FROM sys_user
                WHERE dept_id = :deptId
                  AND is_deleted = 0
                ORDER BY id DESC
                """.formatted(COLUMNS);

        return jdbcClient.sql(sql)
                .param("deptId", deptId)
                .query(SysUser.class)
                .list();
    }

    /**
     * 分页查询
     */
    public List<SysUser> findPage(int page, int size) {

        String sql = """
                SELECT %s
                FROM sys_user
                WHERE is_deleted = 0
                ORDER BY id DESC
                LIMIT :size OFFSET :offset
                """.formatted(COLUMNS);

        return jdbcClient.sql(sql)
                .param("size", size)
                .param("offset", (long) (page - 1) * size)
                .query(SysUser.class)
                .list();
    }

    /**
     * 更新用户
     */
    public Optional<SysUser> update(Long id, SysUser user) {

        String sql = """
                UPDATE sys_user
                SET dept_id = :deptId,
                    nickname = :nickname,
                    avatar = :avatar,
                    email = :email,
                    phone = :phone,
                    status = :status
                WHERE id = :id
                  AND is_deleted = 0
                RETURNING
                """ + COLUMNS;

        return jdbcClient.sql(sql)
                .paramSource(new BeanPropertySqlParameterSource(user))
                .query(SysUser.class)
                .optional();
    }

    /**
     * 逻辑删除
     */
    public boolean deleteById(Long id) {

        String sql = """
                UPDATE sys_user
                SET is_deleted = 1
                WHERE id = :id
                  AND is_deleted = 0
                RETURNING id
                """;

        return jdbcClient.sql(sql)
                .param("id", id)
                .query(Long.class)
                .optional()
                .isPresent();
    }
}