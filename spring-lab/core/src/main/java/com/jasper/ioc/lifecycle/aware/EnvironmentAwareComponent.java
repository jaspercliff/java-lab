package com.jasper.ioc.lifecycle.aware;

import org.jspecify.annotations.NonNull;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class EnvironmentAwareComponent implements EnvironmentAware {

    private Environment environment;

    @Override
    public void setEnvironment(
            @NonNull Environment environment) {

        this.environment = environment;
    }

    public String getProperty(String key) {
        return this.environment.getProperty(key);
    }
}