package com.example.weatherapp.service;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.example.weatherapp.aidl.WeatherData;
import com.example.weatherapp.aidl.WeatherRequest;
import com.example.weatherapp.aidl.WeatherResults;
import com.example.weatherapp.util.WeatherUtil;

import java.util.List;


public class WeatherServiceAsync extends LoggingService {

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
            callback.sendResults(results);
        }
    };
}
