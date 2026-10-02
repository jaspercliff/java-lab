package com.jasper;

import com.jasper.config.CustomLoadBalancerConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;

/**
 * 从注册中心 获取全部的实例列表
 * loadbalancer 在本地 按照负载均衡算法选一个实例
 * 发送http请求
 */
@SpringBootApplication
@LoadBalancerClient(
        name = "producer-service",
        configuration = CustomLoadBalancerConfiguration.class
)
public class LoadBalancerApplication {
    public static void main(String[] args) {
        SpringApplication.run(LoadBalancerApplication.class, args);
    }
}

