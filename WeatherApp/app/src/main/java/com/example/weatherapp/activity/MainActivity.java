package com.example.weatherapp.activity;


import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.weatherapp.R;
import com.example.weatherapp.operation.WeatherOps;
import com.example.weatherapp.operation.WeatherOpsImpl;
import com.example.weatherapp.util.RetainedFragmentManager;


public class MainActivity extends LoggingActivity {

    /**
     * Used to retain the AcronymOps state between runtime configuration
     * changes.
     */
    public final RetainedFragmentManager mRetainedFragmentManager =
            new RetainedFragmentManager(this.getFragmentManager(),
                    TAG);

    private WeatherOps mWeatherOps;
    /**
     * Hook method called when a new instance of Activity is created.
     * One time initialization code goes here, e.g., UI layout
     * initialization and runtime configuration changes.
     *
     * @param  savedInstanceState object that contains saved state information.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Handle any configuration change.
        handleConfigurationChanges();
    }

    /**
     * Hook method called after onCreate() or after onRestart() (when
     * the activity is being restarted from stopped state).
     */
    @Override
    protected void onStart(){
        // Always call super class for necessary
        // initialization/implementation.
        super.onStart();

        // Initiate the service binding protocol.
        mWeatherOps.bindService();

    }
    @Override
    protected void onResume(){
        super.onResume();
        // Show any results if they exist
        mWeatherOps.showResults();
    }
    /**
     * Hook method called by Android when this Activity becomes
     * invisible.
     */
    @Override
    protected void onStop() {
        // Unbind from the Service.
        mWeatherOps.unbindService();

        // Always call super class for necessary operations when
        // stopping.
        super.onStop();
    }
    public void doSync(View v)
    {

        mWeatherOps.doSync(v);
    }

    public void doAsync(View v){

        mWeatherOps.doAsync(v);
    }
    /**
     * Handle hardware reconfigurations, such as rotating the display.
     */
    protected void handleConfigurationChanges() {
        // If this method returns true then this is the first time the
        // Activity has been created.
        if (mRetainedFragmentManager.firstTimeIn()) {
            Log.d(TAG,
                    "First time onCreate() call");

            // Create the WeatherOps object one time.
            mWeatherOps = new WeatherOpsImpl(this);

            // Store the WeatherOps into the RetainedFragmentManager.
            mRetainedFragmentManager.put("WEATHER_OPS_STATE",
                    mWeatherOps);

        } else {
            // The RetainedFragmentManager was previously initialized,
            // which means that a runtime configuration change
            // occured.

            Log.d(TAG,
                    "Second or subsequent onCreate() call");

            // Obtain the WeatherOps object from the
            // RetainedFragmentManager.
            mWeatherOps =
                    mRetainedFragmentManager.get("WEATHER_OPS_STATE");

            // This check shouldn't be necessary under normal
            // circumtances, but it's better to lose state than to
            // crash!
            if (mWeatherOps == null) {
                // Create the AcronymOps object one time.
                mWeatherOps = new WeatherOpsImpl(this);

                // Store the WeatherOps into the
                // RetainedFragmentManager.
                mRetainedFragmentManager.put("WEATHER_OPS_STATE",
                        mWeatherOps);
            }
            else
                // Inform it that the runtime configuration change has
                // completed.
                mWeatherOps.onConfigurationChange(this);
        }
    }

}
