package com.example.weatherapp.operation;

import android.view.View;

import com.example.weatherapp.activity.MainActivity;

/**
 * Created by sandie on 5/28/15.
 */
public interface WeatherOps {
    /**
     * Initiate the service binding protocol.
     */
    public void bindService();

    /**
     * Initiate the service unbinding protocol.
     */
    public void unbindService();

    /**
     * Initiate the sync weather lookup when Sync
     * button is clicked
     * @param v - View
     */
    public void doSync(View v);

    /**
     * Initiate the async weather lookup when Async
     * button is clicked
     * @param v - View
     */
    public void doAsync(View v);

    /**
     * Called after a runtime configuration change occurs to finish
     * the initialization steps.
     */

    public void onConfigurationChange(MainActivity activity);
}
