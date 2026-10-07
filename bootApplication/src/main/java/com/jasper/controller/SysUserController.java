package com.jasper.controller;
import com.jasper.pojo.entity.SysUser;
import lombok.RequiredArgsConstructor;
import com.jasper.service.ISysUserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
* <p>
    * 用户表 前端控制器
    * </p>
*
* @author jasper
* @since 2026-09-08
*/
@RestController
@RequiredArgsConstructor
@RequestMapping("/sysUser")
public class SysUserController {
    private final ISysUserService sysUserService;

    @GetMapping
    public List<SysUser> getSysUser() {
         return sysUserService.list();
    }
}
