package com.jasper.structural.proxy;


public interface SmsService {
    SmsResponse send(String phone, String message);
}
