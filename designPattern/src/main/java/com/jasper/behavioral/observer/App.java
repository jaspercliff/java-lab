package com.jasper.behavioral.observer;

public class App {
    public static void main(String[] args) {
        Weather weather = new Weather(WeatherType.SUNNY);
        try (
                AutoCloseable hobbits = weather.register(new Hobbits());
                AutoCloseable orcs = weather.register(new Orcs())
        ) {
            weather.timePasses();
            weather.timePasses();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
