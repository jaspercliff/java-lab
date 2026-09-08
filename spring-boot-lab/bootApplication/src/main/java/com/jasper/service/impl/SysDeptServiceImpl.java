package com.jasper.service.impl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.jasper.pojo.entity.SysDept;
import com.jasper.mapper.SysDeptMapper;
import com.jasper.service.ISysDeptService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 部门表 服务实现类
 * </p>
 *
 * @author jasper
 * @since 2026-09-08
 */
@Service
public class SysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDept> implements ISysDeptService {

}
