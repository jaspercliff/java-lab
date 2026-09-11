package com.jasper.ioc.lifecycle;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope
public class UserService
        implements BeanNameAware,
        ApplicationContextAware,
        InitializingBean,
        DisposableBean {

    @Autowired
    private UserRepository userRepository;

    @Value("${app.name}")
    private String appName;

    public UserService() {
        System.out.println("1beanDefinition 2实例化：UserService()");
    }

    @Override
    public void setBeanName(String name) {
        System.out.println("3 属性赋值 4 aware BeanNameAware：" + name);
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) {
        System.out.println("4 aware ApplicationContextAware");
    }


    @PostConstruct
    public void postConstruct() {
        System.out.println(" 6 初始化 @PostConstruct");
    }

    @Override
    public void afterPropertiesSet() {
        System.out.println("6 初始化  InitializingBean.afterPropertiesSet()");
    }

    public void init() {
        System.out.println("6 初始化 init-method");
    }



    @PreDestroy
    public void preDestroy() {
        System.out.println("9 bean 销毁 @PreDestroy");
    }

    @Override
    public void destroy() {
        System.out.println(" 9 bean 销毁 DisposableBean.destroy()");
    }

    public void destroyMethod() {
        System.out.println("9 bean 销毁 destroy-method");
    }

    public void test() {
        System.out.println("Bean 正常使用");
    }
}