package com.jasper.structural.proxy;

import com.jasper.models.SmsResponse;

public interface SmsService {
    SmsResponse send(String phone, String message);
}
