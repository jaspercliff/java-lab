package com.jasper.dao.jdbcTemplate;

import com.jasper.pojo.dto.SysUserDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;

@SpringBootTest
@Slf4j
public class QueryTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void query(){
        Integer i = jdbcTemplate.queryForObject("select count(*) from sys_user", Integer.class);
        log.info("count={}", i);
        Integer i1 = jdbcTemplate.queryForObject("select count(*) from sys_user where username='jasper'", Integer.class);
        log.info("count={}", i1);
        String username = jdbcTemplate.queryForObject("select username from sys_user where username='jasper'", String.class);
        log.info("username={}", username);


        SysUserDTO sysUserDTO1 = jdbcTemplate.queryForObject("select id,username,password from sys_user where username = ?",
                (rs, rowNum) -> {
            SysUserDTO sysUserDTO = new SysUserDTO();
            sysUserDTO.setId(rs.getLong("id"));
            sysUserDTO.setUsername(rs.getString("username"));
            sysUserDTO.setPassword(rs.getString("password"));
            return sysUserDTO;
        }, "jasper");
        log.info("user: {}", sysUserDTO1);

        RowMapper<SysUserDTO> rowMapper = (rs,rowNum)->{
            SysUserDTO sysUserDTO = new SysUserDTO();
            sysUserDTO.setId(rs.getLong("id"));
            sysUserDTO.setUsername(rs.getString("username"));
            sysUserDTO.setPassword(rs.getString("password"));
            return sysUserDTO;
        };

        List<SysUserDTO> list = jdbcTemplate.query("select id,username,password from sys_user", rowMapper);
        log.info("list={}", list);

    }
}
