package com.jasper.controller;

import com.jasper.JasperEnableService;
import com.jasper.JasperHelloService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("demo")
public class DemoController {

    private final JasperHelloService helloService;
    private final JasperEnableService enableService;

    @GetMapping("jasper")
    public String testStarter() {
        return helloService.hello();
    }

    @GetMapping("enable")
    public String enable() {
        return  enableService.isEnabled();
    }
}
