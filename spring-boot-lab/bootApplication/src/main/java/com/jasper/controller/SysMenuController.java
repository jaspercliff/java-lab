package com.jasper.controller;
import lombok.RequiredArgsConstructor;
import com.jasper.service.ISysMenuService;
import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RestController;

/**
* <p>
    * 菜单权限表 前端控制器
    * </p>
*
* @author jasper
* @since 2026-09-08
*/
@RestController
@RequiredArgsConstructor
@RequestMapping("/sysMenu")
public class SysMenuController {
    private final ISysMenuService ISysMenuService;
}
