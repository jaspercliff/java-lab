package com.jasper.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class OrderDao {

    private final JdbcTemplate jdbcTemplate;

    public void insertOrder() {
        // id 由 ShardingSphere snowflake 生成，勿手写固定主键
        String sql = """
                INSERT INTO t_order
                (
                    user_id,
                    order_no,
                    amount,
                    status
                )
                VALUES
                (?, ?, ?, ?)
                """;

        for (long i = 1; i <= 10; i++) {
            jdbcTemplate.update(
                    sql,
                    1000 + i,
                    "ORDER_" + UUID.randomUUID(),
                    new BigDecimal("99.99"),
                    0
            );
        }
    }

    public List<Map<String, Object>> selectAll() {
        String sql = """
                SELECT
                    id,
                    user_id,
                    order_no,
                    amount,
                    status
                FROM t_order
                ORDER BY id
                """;

        return jdbcTemplate.queryForList(sql);
    }

    public List<Map<String, Object>> selectByUserId(Long userId) {
        String sql = """
            SELECT
                id,
                user_id,
                order_no,
                amount,
                status
            FROM t_order
            WHERE user_id = ?
            """;

        return jdbcTemplate.queryForList(sql, userId);
    }
}
