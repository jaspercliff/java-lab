package com.jasper.controller;
import lombok.RequiredArgsConstructor;
import com.jasper.service.ISysRoleService;
import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RestController;

/**
* <p>
    * 角色表 前端控制器
    * </p>
*
* @author jasper
* @since 2026-09-08
*/
@RestController
@RequiredArgsConstructor
@RequestMapping("/sysRole")
public class SysRoleController {
    private final ISysRoleService ISysRoleService;
}
