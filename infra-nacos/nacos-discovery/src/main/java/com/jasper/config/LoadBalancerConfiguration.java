package com.jasper.config;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.RandomLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ReactorLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

/**
 * 默认使用的 ReactiveLoadBalancer 实现是 RoundRobinLoadBalancer <br>
 * 通过 @LoadBalancerClient 注释来配置，以切换到使用 RandomLoadBalancer <br>
 * *@LoadBalancerClient 指定的配置类，不要让它同时被 Spring Boot 的主组件扫描扫描到。
 */
public class LoadBalancerConfiguration {

	@Bean
    ReactorLoadBalancer<ServiceInstance> randomLoadBalancer(Environment environment,
                                                            LoadBalancerClientFactory loadBalancerClientFactory) {
		String name = environment.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);
		return new RandomLoadBalancer(loadBalancerClientFactory
				.getLazyProvider(name, ServiceInstanceListSupplier.class),
				name);
	}
}