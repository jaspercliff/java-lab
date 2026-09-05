package com.jasper.structural.proxy.dynamic.jdk;

import com.jasper.structural.proxy.SmsService;

public class MainDemo {
    static void main() {
        TxSmsService txSmsService = new TxSmsService();
        SmsService proxyInstance = (SmsService) ProxyFactory.getProxy(txSmsService);
        proxyInstance.send("110","text message");
        proxyInstance.send("120","text message");
    }
}
