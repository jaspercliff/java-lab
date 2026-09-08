package com.jasper.service.impl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.jasper.pojo.entity.SysRoleMenu;
import com.jasper.mapper.SysRoleMenuMapper;
import com.jasper.service.ISysRoleMenuService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 角色和菜单关联表 服务实现类
 * </p>
 *
 * @author jasper
 * @since 2026-09-08
 */
@Service
public class SysRoleMenuServiceImpl extends ServiceImpl<SysRoleMenuMapper, SysRoleMenu> implements ISysRoleMenuService {

}
