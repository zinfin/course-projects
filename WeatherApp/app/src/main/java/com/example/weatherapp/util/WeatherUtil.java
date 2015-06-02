package com.example.weatherapp.util;

import android.app.Activity;
import android.content.Context;
import android.os.IBinder;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.weatherapp.aidl.WeatherData;

import java.io.BufferedInputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import vandy.mooc.jsonweather.JsonWeather;
import vandy.mooc.jsonweather.WeatherJSONParser;

/**
 * Created by sandie on 5/28/15.
 */
public class WeatherUtil {
    /**
     * Logging tag used by the debugger.
     */
    private final static String TAG = WeatherUtil.class.getCanonicalName();
    /**
     * Cache time limit
     */
    private final static long CACHE_TIME_LIMIT = 10 * 1000L;
    /**
     * URL to the Acronym web service.
     */
    private final static String sWeather_Web_Service_URL =
            "http://api.openweathermap.org/data/2.5/weather?q=";

    /**
     * Set the format for the results
     */
    private final static String units = "&units=imperial";
    /**
     * Hash maps that are used to cache data for 10 seconds
     */
    private static Map<String,List<WeatherData>> weatherCache;
    private static Map<String,Long> timeCache;
    /**
     * Obtain the weather information for a given location
     */
    public static List<WeatherData> getResults(final String location){

        List<JsonWeather> jsonWeatherList=null;
        // Check the cache
        List<WeatherData> weatherList = checkCache(location.toUpperCase(), System.currentTimeMillis());
        if (weatherList == null){
            // No cached data, so go get it from the service
            try{
                final URL url = new URL( sWeather_Web_Service_URL+location+units);
                // Opens a connection to the Acronym Service.
                HttpURLConnection urlConnection =
                        (HttpURLConnection) url.openConnection();
                // Sends the GET request and reads the Json results.
                try (InputStream in =
                             new BufferedInputStream(urlConnection.getInputStream())) {
                    // Create the parser.
                    final WeatherJSONParser parser = new WeatherJSONParser();
                    jsonWeatherList= parser.parseJsonStream(in);
                }finally {
                    urlConnection.disconnect();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
            if (jsonWeatherList !=null && jsonWeatherList.size()>0){
                weatherList = new ArrayList<>();
                for (JsonWeather jweather : jsonWeatherList){
                    weatherList.add( new WeatherData(jweather.getName(),
                            jweather.getWind().getSpeed(),
                            jweather.getWind().getDeg(),
                            jweather.getMain().getTemp(),
                            jweather.getMain().getHumidity(),
                            jweather.getSys().getSunrise(),
                            jweather.getSys().getSunset()));
                }
                addToCache(location.toUpperCase(), weatherList);
                return weatherList;
            }else{
                return null;
            }
        }else{
            return weatherList;
        }




    }
    /**
     * This method is used to hide a keyboard after a user has
     * finished typing the url.
     */
    public static void hideKeyboard(Activity activity,
                                    IBinder windowToken) {
        InputMethodManager mgr =
                (InputMethodManager) activity.getSystemService
                        (Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(windowToken,
                0);
    }
    /**
     * Show a toast message.
     */
    public static void showToast(Context context,
                                 String message) {
        Toast.makeText(context,
                message,
                Toast.LENGTH_SHORT).show();
    }
    /**
     * Check the cache for the location
     */
    private static List<WeatherData> checkCache(String location, long currentTime){
        List<WeatherData> cachedResults = null;
        if (weatherCache !=null){
            if (weatherCache.containsKey(location) && timeCache.containsKey(location)){
                long lastTimestamp = timeCache.get(location);
                Log.d(TAG,"Cached time " + lastTimestamp);
                long elapsedTime = (System.currentTimeMillis()) - lastTimestamp;
                Log.d(TAG, "Elapsed time : " + elapsedTime);
                if (elapsedTime <= CACHE_TIME_LIMIT){
                    cachedResults = weatherCache.get(location);
                    Log.d("WeatherUtil", "Retrieving data from cache");
                }
            }
        }
        return cachedResults;
    }
    /**
     * Insert into cache
     */
    private static void addToCache(String location, List<WeatherData> data){
        if (weatherCache == null){
            weatherCache = new HashMap<>();
            timeCache = new HashMap<>();
        }
        timeCache.put(location,System.currentTimeMillis());
        weatherCache.put(location,data);
        Log.d("WeatherUtil", "Inserting data into cache");
    }
}
