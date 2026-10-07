package com.jasper.controller;
import lombok.RequiredArgsConstructor;
import com.jasper.service.ISysUserRoleService;
import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RestController;

/**
* <p>
    * 用户和角色关联表 前端控制器
    * </p>
*
* @author jasper
* @since 2026-09-08
*/
@RestController
@RequiredArgsConstructor
@RequestMapping("/sysUserRole")
public class SysUserRoleController {
    private final ISysUserRoleService ISysUserRoleService;
}
