package com.jasper.service.impl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.jasper.mapper.SysUserMapper;
import com.jasper.pojo.entity.SysUser;
import com.jasper.service.ISysUserService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author jasper
 * @since 2026-09-08
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {

}
