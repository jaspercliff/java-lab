package com.jasper.structural.proxy.staticDemo;

import com.google.common.base.Stopwatch;
import com.jasper.structural.proxy.SmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class SmsServiceProxy {
    private final SmsService realService;

    /**
     * 如果某个模块调用时需要代理，则使用该proxy类，不需要则直接使用原对象就好
     * @param phone phone
     * @param message message
     */
    public void send(String phone,String message) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        realService.send(phone,message);
        stopwatch.stop();
        log.info("cost time: {}",stopwatch.elapsed());
    }
}
