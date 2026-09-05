package com.jasper.behavioral.observer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Hobbits implements WeatherObserver{

    @Override
    public void update(WeatherType weatherType) {
        log.info("hobbits are face {} weather", weatherType);
    }
}
