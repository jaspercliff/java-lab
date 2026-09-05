package com.jasper.mapper;

import com.jasper.pojo.dto.SysUserRoleDTO;
import com.jasper.pojo.entity.SysUser;
//import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface SysUserRoleTestMapper {

    SysUserRoleDTO selectUserWithRoles(@Param("userId") Long userId);

    @Update("UPDATE sys_role SET role_name = #{roleName} WHERE id = #{roleId}")
    void updateRoleName(@Param("roleId") Long roleId, @Param("roleName") String roleName);

    int insertUser(SysUser user);

    @Insert("INSERT INTO sys_role (id, role_name, role_key) VALUES (#{id}, #{roleName}, #{roleKey}) ON DUPLICATE KEY UPDATE role_name=#{roleName}")
    void insertRole(@Param("id") Long id, @Param("roleName") String roleName, @Param("roleKey") String roleKey);

    @Insert("INSERT INTO sys_user_role (user_id, role_id) VALUES (#{userId}, #{roleId})")
    void insertUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);
}