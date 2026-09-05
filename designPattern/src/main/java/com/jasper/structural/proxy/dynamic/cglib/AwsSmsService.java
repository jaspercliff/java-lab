package com.jasper.structural.proxy.dynamic.cglib;

import com.jasper.structural.proxy.SmsResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AwsSmsService {
    public SmsResponse send(String phone,String message){
        log.info("aws send message phone is {}, message is {}",phone,message);
        return new SmsResponse("success");
    }
}