package com.gmail.tarkhanov.lev;

import java.io.IOException;

/**
 * Created by Lev Tarkhanov on 29.09.2017.
 */
public class WeatherGetterThread extends Thread {

    private Float temperature = 0.0F;
    private City city;

    WeatherGetterThread(City city) {
        this.city = city;
    }

    @Override
    public void run() {
        WeatherGetter wg = new WeatherGetter();
        Float temperature = 0.0F;
        try {
            temperature = wg.getTemperatureByCityCode(this.city.getId());
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.temperature = temperature;
        synchronized (this) {
            notifyAll();
        }
    }

    public Float getTemperature() {
        return temperature;
    }

    public City getCity() {
        return city;
    }

}
