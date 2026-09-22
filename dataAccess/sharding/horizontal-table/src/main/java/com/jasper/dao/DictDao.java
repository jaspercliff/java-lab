package com.jasper.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DictDao {

    private final JdbcTemplate jdbcTemplate;

    /**
     * 插入字典
     */
    public int insert(Long id,
                      String dictType,
                      String dictCode,
                      String dictName,
                      int sort) {

        String sql = """
                INSERT INTO t_dict
                    (id, dict_type, dict_code, dict_name, sort)
                VALUES
                    (?, ?, ?, ?, ?)
                """;

        return jdbcTemplate.update(
                sql,
                id,
                dictType,
                dictCode,
                dictName,
                sort
        );
    }

    /**
     * 根据 ID 删除
     */
    public int deleteById(Long id) {

        String sql = """
                DELETE FROM t_dict
                WHERE id = ?
                """;

        return jdbcTemplate.update(sql, id);
    }
}