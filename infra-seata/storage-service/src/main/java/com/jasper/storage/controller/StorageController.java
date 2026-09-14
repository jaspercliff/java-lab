package com.jasper.storage.controller;

import com.jasper.storage.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/storage")
@RequiredArgsConstructor
public class StorageController {

    private final StorageService storageService;

    @PostMapping("/deduct")
    public void deduct(@RequestParam String commodityCode, @RequestParam int count) {
        storageService.deduct(commodityCode, count);
    }
}
