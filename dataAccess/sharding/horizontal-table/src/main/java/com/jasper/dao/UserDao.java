package com.jasper.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
@RequiredArgsConstructor
public class UserDao {
    private final JdbcTemplate jdbcTemplate;

    public String create() {

        String sql = """
                INSERT INTO t_user
                    (dept_id, username, password, nickname, email, phone)
                VALUES
                    (?, ?, ?, ?, ?, ?)
                """;

        jdbcTemplate.update(
                sql,
                1L,
                "jasper",
                "123456",
                "Jasper",
                "jasper@example.com",
                "13800138000"
        );

        return "success";
    }

    public Map<String, Object> get(Long id) {

        return jdbcTemplate.queryForMap(
                "SELECT * FROM t_user WHERE id = ?",
                id
        );
    }
}
