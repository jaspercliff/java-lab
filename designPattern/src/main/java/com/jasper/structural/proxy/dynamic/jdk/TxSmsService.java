package com.jasper.structural.proxy.dynamic.jdk;

import com.jasper.structural.proxy.SmsResponse;
import com.jasper.structural.proxy.SmsService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TxSmsService implements SmsService {
    @Override
    public SmsResponse send(String phone, String message) {
        log.info("tx phone is {} : message is {} ", phone, message);
        return new SmsResponse();
    }
}
