package com.jasper.ioc.lifecycle;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean(initMethod = "init", destroyMethod = "destroyMethod")
    public UserService userService() {
        return new UserService();
    }


}