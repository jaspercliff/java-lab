package com.jasper.controller;
import lombok.RequiredArgsConstructor;
import com.jasper.service.ISysRoleMenuService;
import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RestController;

/**
* <p>
    * 角色和菜单关联表 前端控制器
    * </p>
*
* @author jasper
* @since 2026-09-08
*/
@RestController
@RequiredArgsConstructor
@RequestMapping("/sysRoleMenu")
public class SysRoleMenuController {
    private final ISysRoleMenuService ISysRoleMenuService;
}
