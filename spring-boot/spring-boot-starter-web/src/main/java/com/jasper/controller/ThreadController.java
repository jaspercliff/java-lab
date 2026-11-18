package com.jasper.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/thread")
public class ThreadController {

    @GetMapping("/info")
    public Map<String, Object> info() {
        Thread thread = Thread.currentThread();

        return Map.of(
                "threadName", thread.getName(),
                "isVirtual", thread.isVirtual(),
                "thread", thread.toString()
        );
    }
}