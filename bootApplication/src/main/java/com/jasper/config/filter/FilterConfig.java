package com.jasper.config.filter;

import jakarta.servlet.DispatcherType;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.swing.*;

/**
 * 与具体的业务逻辑无关，属于 Web 层面的基础预处理/后处理，或者需要修改请求/响应流
 * 如防 XSS 攻击（包装 Request 过滤特殊字符）、防 SQL 注入 <br>
 * 可以精确控制拦截路径（addUrlPatterns）、精确控制执行顺序（setOrder）、支持动态启用/禁用
 */
@Configuration
public class FilterConfig {
    @Bean
    public FilterRegistrationBean<TraceIdFilter> logFilter() {
        FilterRegistrationBean<TraceIdFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new TraceIdFilter());
        registration.addUrlPatterns("/*");
        registration.setOrder(1); // 越小优先级越高
        registration.setName("TraceIdFilter");
        registration.setEnabled(true);
        return registration;
    }

    @Bean
    public FilterRegistrationBean<TestFilter> TestFilter() {
        FilterRegistrationBean<TestFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new TestFilter());
        registration.addUrlPatterns("/*");
        registration.setOrder(1); // 越小优先级越高
        registration.setName("TestFilter");
        registration.setEnabled(true);
//        默认情况下，Spring Boot / Servlet 容器只会在 DispatcherType.REQUEST（即主请求刚进来时）触发一次
        registration.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.ASYNC);
        return registration;
    }
}
