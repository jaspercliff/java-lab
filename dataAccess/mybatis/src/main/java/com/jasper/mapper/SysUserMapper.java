package com.jasper.mapper;

import com.jasper.pojo.entity.SysUser;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface SysUserMapper {

    void insertSysUser(SysUser sysUser);

    void insertSysUserWithParam(@Param("sysUser") SysUser sysUser);

    @Select("select * from sys_user where id = #{id}")
    SysUser selectById(Long id);
}