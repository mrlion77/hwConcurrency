package com.gmail.tarkhanov.lev;

import net.aksingh.owmjapis.CurrentWeather;
import net.aksingh.owmjapis.OpenWeatherMap;
import org.json.JSONException;

import java.io.IOException;

/**
 * Created by Lev Tarkhanov on 29.09.2017.
 */
public class WeatherGetter {

    private String owmApiKey = "2fe5a1e5bc8529e2aab5973998d80b88";
    private OpenWeatherMap.Units units = OpenWeatherMap.Units.METRIC;
    private OpenWeatherMap owm = new OpenWeatherMap(units, owmApiKey);

    Float getTemperatureByCityCode(long cityId) throws IOException, JSONException {
        CurrentWeather cwd = owm.currentWeatherByCityCode(cityId);
        return cwd.getMainInstance().getTemperature();
    }

}
