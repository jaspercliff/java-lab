package com.jasper.service;

public interface Service {

    String success();

    String successException();

    String failure();

    String failureWithFallback();


}
