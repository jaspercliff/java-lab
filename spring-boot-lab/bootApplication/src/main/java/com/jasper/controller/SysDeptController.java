package com.jasper.controller;
import lombok.RequiredArgsConstructor;
import com.jasper.service.ISysDeptService;
import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RestController;

/**
* <p>
    * 部门表 前端控制器
    * </p>
*
* @author jasper
* @since 2026-09-08
*/
@RestController
@RequiredArgsConstructor
@RequestMapping("/sysDept")
public class SysDeptController {
    private final ISysDeptService ISysDeptService;
}
