package jasper.config;

import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import jasper.feign.ProducerServiceClient1;
import jasper.feign.ProducerServiceClient2;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    /**
     * 默认 Feign 使用自己的注解体系
     * User getUser(@Param("id") Long id);
     */
    @Bean
    public ProducerServiceClient1 userServiceFeignClient() {
        return Feign.builder()
                // 把 Java 对象转换成 JSON 的组件
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .target(ProducerServiceClient1.class, "http://127.0.0.1:8080"); // 目标地址
    }

    /**
     * 可以使用 Spring MVC 风格的注解
     * @GetMapping("/users/{id}")
     * User getUser(@PathVariable Long id);
     */
    @Bean
    public ProducerServiceClient2 userServiceFeignClient02() {
        return Feign.builder()
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .contract(new SpringMvcContract())
                .target(ProducerServiceClient2.class, "http://127.0.0.1:8080"); // 目标地址
    }
}
