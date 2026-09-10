package com.jasper.config;

import com.jasper.JasperEnableService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
//INFO 这里先不加该注解 因为包一样会导致 直接扫描到该类 导致enableJasper注解直接失效
public class JasperConfiguration {

    @Bean
    public JasperEnableService JasperEnableService(){
        return new JasperEnableService();
    }
}
