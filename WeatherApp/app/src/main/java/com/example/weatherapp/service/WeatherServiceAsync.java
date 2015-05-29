package com.example.weatherapp.service;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.example.weatherapp.aidl.WeatherData;
import com.example.weatherapp.aidl.WeatherRequest;
import com.example.weatherapp.aidl.WeatherResults;
import com.example.weatherapp.util.WeatherUtil;

import java.util.List;

/**
 * Created by sandie on 5/28/15.
 */
public class WeatherServiceAsync extends LoggingService {
    private final static String TAG = WeatherServiceAsync.class.getCanonicalName();

    @Override
    public IBinder onBind(Intent intent){
        return mWeatherRequestImpl;
    }

    public static Intent makeIntent(Context context){
        return new Intent(context, WeatherServiceAsync.class);
    }

    WeatherRequest.Stub mWeatherRequestImpl = new WeatherRequest.Stub(){
        @Override
        public void getCurrentWeather(String location, WeatherResults callback) throws RemoteException{
            List<WeatherData> results = WeatherUtil.getResults(location);

            if (results != null){
                callback.sendResults(results);
                Log.d(TAG, "" + results.size() + " results for weather data at : " + location);
            }else{
               Log.d(TAG,"No weather results found for " + location);
            }

        }
    };
}
