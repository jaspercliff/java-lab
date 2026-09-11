package com.jasper.ioc.lifecycle.aware;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ApplicationContextAwareService implements ApplicationContextAware {
    private ApplicationContext applicationContext;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

        this.applicationContext = applicationContext;
    }

    /**
     * 当无法使用 @Autowired（比如在某些非 Spring 管理的工具类中，
     * 或者需要根据字符串名称动态获取 Bean）时，可以通过 applicationContext.getBean("beanName") 获取
     */
    public void doSomething() {
        // 3. 现在你可以使用 applicationContext 做各种事情了
        // 例如：动态获取其他 Bean
        BeanNameAwareService bean = applicationContext.getBean(BeanNameAwareService.class);
    }
}
