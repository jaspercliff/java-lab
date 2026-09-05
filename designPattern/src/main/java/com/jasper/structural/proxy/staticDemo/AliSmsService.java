package com.jasper.structural.proxy.staticDemo;

import com.jasper.structural.proxy.SmsService;
import com.jasper.models.SmsResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AliSmsService implements SmsService {
    @Override
    public SmsResponse send(String phone, String message) {
        log.info("ali send message");
        return new SmsResponse();
    }
}
