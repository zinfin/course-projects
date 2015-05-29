package com.example.weatherapp.util;

import com.example.weatherapp.aidl.WeatherData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sandie on 5/28/15.
 */
public class WeatherUtil {
    /**
     * Logging tag used by the debugger.
     */
    private final static String TAG = WeatherUtil.class.getCanonicalName();

    /**
     * URL to the Acronym web service.
     */
    private final static String sWeather_Web_Service_URL =
            "http://api.openweathermap.org/data/2.5/weather?q=";

    /**
     * Obtain the weather information for a given location
     */
    public static List<WeatherData> getResults(final String location){
        final List<WeatherData> returnList = new ArrayList<>();
        return returnList;
    }
}
