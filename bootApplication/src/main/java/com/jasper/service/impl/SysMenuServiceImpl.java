package com.jasper.service.impl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.jasper.pojo.entity.SysMenu;
import com.jasper.mapper.SysMenuMapper;
import com.jasper.service.ISysMenuService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 菜单权限表 服务实现类
 * </p>
 *
 * @author jasper
 * @since 2026-09-08
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements ISysMenuService {

}
