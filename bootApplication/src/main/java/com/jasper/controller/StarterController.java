package com.jasper.controller;

import com.cliff.JasperEnableService;
import com.cliff.JasperHelloService;
import com.jasper.config.anno.Auth;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("starter")
public class StarterController {

    @Resource
    private JasperHelloService jasperHelloService;
    @Resource
    private JasperEnableService enableService;

    @GetMapping("jasper")
    public String testStarter() {
        return jasperHelloService.hello();
    }

    @GetMapping("enable")
    @Auth
    public String enable() {
        return  enableService.isEnabled();
    }
}
