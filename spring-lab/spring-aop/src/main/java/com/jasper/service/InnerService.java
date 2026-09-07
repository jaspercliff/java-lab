package com.jasper.service;

import com.jasper.anno.Loggable;
import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;

@Service
public class InnerService {

    public void methodA() {
        // 问题：这里直接调用 methodB，AOP 拦截器（如 @Transactional 或 @Loggable）会失效！
        // 因为这是“自己调自己”，没有经过代理对象（经纪人），直接调用了目标对象（明星本人）。
//        this.methodB();
            // 从 ThreadLocal 中手动获取“经纪人”（代理对象），然后通过经纪人去调用 methodB
            InnerService proxy = (InnerService) AopContext.currentProxy();
            proxy.methodB(); // 此时 AOP 拦截生效

    }

    @Loggable("方法B")
    public void methodB() {
        System.out.println("执行方法B");
    }
}
