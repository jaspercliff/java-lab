package com.jasper.controller;

import com.jasper.service.BackendAService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/backendA")
@RequiredArgsConstructor
public class BackendAController {
    private final BackendAService backendAService;


    @GetMapping("success")
    public String success(){
        return backendAService.success();
    }
    @GetMapping("failure")
    public String failure(){
        return backendAService.failure();
    }


}
