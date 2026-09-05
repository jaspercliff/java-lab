package com.jasper.behavioral.observer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Orcs implements WeatherObserver{

    @Override
    public void update(WeatherType weatherType) {
        log.info("Orcs are in {}", weatherType);
    }
}
