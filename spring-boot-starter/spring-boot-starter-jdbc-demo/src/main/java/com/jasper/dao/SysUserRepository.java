package com.jasper.dao;

import com.jasper.entity.SysUser;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SysUserRepository {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public void save(SysUser user) {
        String sql = """
                INSERT INTO sys_user (
                    username,
                    password,
                    nickname,
                    email,
                    phone
                )
                VALUES (?, ?, ?, ?, ?)
                """;

        jdbcTemplate.update(
                sql,
                user.getUsername(),
                user.getPassword(),
                user.getNickname(),
                user.getEmail(),
                user.getPhone()
        );
    }

    public SysUser findById(Long id) {
        String sql = """
                SELECT
                    id,
                    username,
                    password,
                    nickname,
                    email,
                    phone,
                    status,
                    created_at,
                    updated_at
                FROM sys_user
                WHERE id = ?
                """;

        return jdbcTemplate.queryForObject(
                sql,
                (rs, rowNum) -> {
                    SysUser user = new SysUser();

                    user.setId(rs.getLong("id"));
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password"));
                    user.setNickname(rs.getString("nickname"));
                    user.setEmail(rs.getString("email"));
                    user.setPhone(rs.getString("phone"));
                    user.setStatus(rs.getInt("status"));
                    user.setCreatedAt(
                            rs.getTimestamp("created_at").toLocalDateTime()
                    );
                    user.setUpdatedAt(
                            rs.getTimestamp("updated_at").toLocalDateTime()
                    );

                    return user;
                },
                id
        );
    }

    public SysUser findByUsername(String username) {
        String sql = """
                select * from sys_user where username = :username
                """;
        MapSqlParameterSource param = new MapSqlParameterSource().addValue("username", username);
        return namedParameterJdbcTemplate.queryForObject(sql, param, new BeanPropertyRowMapper<>(SysUser.class));

    }

    public List<SysUser> findAll() {
        String sql = """
                SELECT
                    id,
                    username,
                    password,
                    nickname,
                    email,
                    phone,
                    status,
                    created_at,
                    updated_at
                FROM sys_user
                """;

        return jdbcTemplate.query(
                sql,
                new BeanPropertyRowMapper<>(SysUser.class)
        );
    }

    public int deleteById(Long id) {
        String sql = """
                DELETE FROM sys_user
                WHERE id = ?
                """;

        return jdbcTemplate.update(sql, id);
    }
}