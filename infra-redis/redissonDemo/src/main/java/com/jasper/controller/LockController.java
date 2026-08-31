package com.jasper.controller;

import com.jasper.service.LockService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jasper
 * @since 2026-08-31 <br>
 */
@RestController
@RequestMapping("/lock")
@RequiredArgsConstructor
public class LockController {

    private final LockService lockService;

    @PostMapping
    public String test(
            @RequestParam("name") String name,
            @RequestParam(value = "sleepMillis", defaultValue = "2000") long sleepMillis) {

        lockService.execute(name, sleepMillis);

        return "success";
    }
}
