package com.jasper.behavioral.observer;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Weather extends Subject {
    private WeatherType currentWeather;

    public Weather(WeatherType currentWeather){
        this.currentWeather = currentWeather;

    }

    public void timePasses() {
        WeatherType[] values = WeatherType.values();
        currentWeather = values[(currentWeather.ordinal() + 1) % values.length];
        log.info("time pass weather is {}",currentWeather);
        notifyObservers(currentWeather);
    }

}
