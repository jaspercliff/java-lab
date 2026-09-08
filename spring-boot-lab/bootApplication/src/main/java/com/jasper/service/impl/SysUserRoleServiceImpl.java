package com.jasper.service.impl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.jasper.pojo.entity.SysUserRole;
import com.jasper.mapper.SysUserRoleMapper;
import com.jasper.service.ISysUserRoleService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户和角色关联表 服务实现类
 * </p>
 *
 * @author jasper
 * @since 2026-09-08
 */
@Service
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRole> implements ISysUserRoleService {

}
