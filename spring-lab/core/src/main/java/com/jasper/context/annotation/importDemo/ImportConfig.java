package com.jasper.context.annotation.importDemo;

import com.jasper.service.ThirdPartyService;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author jasper
 * @since 2026-09-07 21:28:53 <br>
 */
@Configuration
// 引入第三方库中的类，且无法修改其源码添加 @Component 注解时
@Import({ThirdPartyService.class, MyImportSelector.class, MyBeanRegistrar.class})
public class ImportConfig {}
