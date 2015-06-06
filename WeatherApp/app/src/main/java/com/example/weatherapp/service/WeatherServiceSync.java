package com.example.weatherapp.service;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.example.weatherapp.aidl.WeatherCall;
import com.example.weatherapp.aidl.WeatherData;
import com.example.weatherapp.util.WeatherUtil;

import java.util.ArrayList;
import java.util.List;


public class WeatherServiceSync extends LoggingService {

    /**
     * Logging TAG
     */
    private final static String TAG = WeatherServiceSync.class.getCanonicalName();

    @Override
    public IBinder onBind(Intent intent){
        return mWeatherRequestImpl;
    }

    public static Intent makeIntent(Context context){
        return new Intent(context, WeatherServiceSync.class);
    }

    /**
     * The concrete implementation of the AIDL Interface
     * WeatherRequest, which extends the Stub class that implements
     * WeatherRequest, thereby allowing Android to handle calls across
     * process boundaries.  This method runs in a separate Thread as
     * part of the Android Binder framework.
     *
     * This implementation plays the role of Invoker in the Broker
     * Pattern.
     */
    WeatherCall.Stub mWeatherRequestImpl = new WeatherCall.Stub( ){
        @Override
        public List<WeatherData> getCurrentWeather(String location) throws RemoteException {
            List<WeatherData> weatherResults = WeatherUtil.getResults( location);

            if (weatherResults !=null){
                Log.d(TAG,""+weatherResults.size() + " results for weather data at : " + location);
            }else{
                weatherResults = new ArrayList<>();
            }
            return weatherResults;
        }

    };
}
