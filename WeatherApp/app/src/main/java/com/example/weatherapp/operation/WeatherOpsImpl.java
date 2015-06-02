package com.example.weatherapp.operation;

import android.content.Context;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.example.weatherapp.R;
import com.example.weatherapp.activity.MainActivity;
import com.example.weatherapp.aidl.WeatherCall;
import com.example.weatherapp.aidl.WeatherData;
import com.example.weatherapp.aidl.WeatherRequest;
import com.example.weatherapp.aidl.WeatherResults;
import com.example.weatherapp.service.WeatherServiceAsync;
import com.example.weatherapp.service.WeatherServiceSync;
import com.example.weatherapp.util.GenericServiceConnection;
import com.example.weatherapp.util.WeatherUtil;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * This class implements all the weather service-related operations defined in
 * the WeatherOps interface.
 */
public class WeatherOpsImpl implements WeatherOps {
    /**
     * Debugging tag used by the Android logger.
     */
    protected final String TAG = getClass().getSimpleName();


    /**
     * Used to enable garbage collection.
     */
    protected WeakReference<MainActivity> mActivity;
    /**
     * The ListView that will display the results to the user.
     */
    protected WeakReference<ListView> mListView;

    /**
     * Acronym entered by the user.
     */
    protected WeakReference<EditText> mEditText;
    /**
     * This GenericServiceConnection is used to receive results after
     * binding to the AcronymServiceSync Service using bindService().
     */
    private GenericServiceConnection<WeatherCall> mServiceConnectionSync;

    /**
     * This GenericServiceConnection is used to receive results after
     * binding to the AcronymServiceAsync Service using bindService().
     */
    private GenericServiceConnection<WeatherRequest> mServiceConnectionAsync;

    /**
     * Constructor initializes the fields.
     */
    public WeatherOpsImpl(MainActivity activity) {
        // Initialize the WeakReference.
        mActivity = new WeakReference<>(activity);

        // Finish the initialization steps.
        initializeViewFields();
        initializeNonViewFields();
    }

    /*
     * Initiate the service binding protocol
     */
    @Override
    public void bindService() {
        Log.d(TAG, "calling bindService()");
        // Start the Weather Bound service if it isn't already
        // running.
        if(mServiceConnectionSync.getInterface()==null){
            mActivity.get().bindService(WeatherServiceSync.makeIntent(mActivity.get()),
                    mServiceConnectionSync,
                    Context.BIND_AUTO_CREATE);
        }
        if (mServiceConnectionAsync.getInterface() == null){
            mActivity.get().bindService(WeatherServiceAsync.makeIntent(mActivity.get()),
                    mServiceConnectionAsync,
                    Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    public void unbindService() {
        if(mServiceConnectionAsync.getInterface()!=null){
            mActivity.get().unbindService(mServiceConnectionAsync);
        }
        if(mServiceConnectionSync.getInterface()!=null){
            mActivity.get().unbindService(mServiceConnectionSync);
        }
    }

    @Override
    public void doSync(View v) {
        final WeatherCall weatherCall= mServiceConnectionSync.getInterface();
        if (weatherCall !=null){
            final String location = mEditText.get().getText().toString();
            WeatherUtil.hideKeyboard(mActivity.get(), mEditText.get().getWindowToken());

            new AsyncTask<String,Void,List<WeatherData>>(){
                private String mLocation;

                protected List<WeatherData> doInBackground(String... locations){
                    try{
                        mLocation = locations[0];
                        return weatherCall.getCurrentWeather(mLocation);
                    }catch (RemoteException re){
                        Log.d("Async Task", re.getMessage());
                    }
                    return null;
                }
                protected void onPostExecute(List<WeatherData> weatherData){
                    if (weatherData.size()>0){
                        displayResults(weatherData);
                    }else{
                        WeatherUtil.showToast(mActivity.get(),
                                "no weather data found for "
                                + mLocation);
                    }
                }
            }.execute(location);
        }else{
            Log.d(TAG, "weather call was null");
        }
    }

    @Override
    public void doAsync(View v) {
        WeatherRequest request = mServiceConnectionAsync.getInterface();
        if (request !=null){
            final String location = mEditText.get().getText().toString();
            WeatherUtil.hideKeyboard(mActivity.get(), mEditText.get().getWindowToken());

            try{
                // Invoke the one-way, non blocking  AIDL call
                request.getCurrentWeather(location,mWeatherResults);
            }catch (RemoteException e){
                Log.d(TAG,"Remote exception in doAsync " + e.getMessage());
            }
        }else{
            Log.d(TAG, "Weather request was null");
        }
    }

    @Override
    public void onConfigurationChange(MainActivity activity) {
        mActivity = new WeakReference<MainActivity>(activity);
        // Do any re-initializing
        initializeViewFields();
        initializeNonViewFields();

    }

    /**
     * Initialize the View fields, which are all stored as
     * WeakReferences to enable garbage collection.
     */
    private void initializeViewFields() {
        mActivity.get().setContentView(R.layout.activity_main);
        mEditText = new WeakReference<> ((EditText)mActivity.get().findViewById(R.id.editText));
    }

    /**
     * (Re)initialize the non-view fields (e.g.,
     * GenericServiceConnection objects).
     */
    private void initializeNonViewFields() {
        mServiceConnectionAsync =
                new GenericServiceConnection<WeatherRequest>(WeatherRequest.class);
        mServiceConnectionSync =
                new GenericServiceConnection<WeatherCall>(WeatherCall.class);
    }
    private void displayResults(List<WeatherData> weatherResults){
        for (WeatherData w: weatherResults){
            Log.d(TAG, w.toString());
        }


    }
    /**
     * The implementation of the WeatherResults AIDL interface which
     * is passed to the Weather service using the WeatherRequest.getCurrentWeather()
     * method.
     */
    private WeatherResults.Stub mWeatherResults = new WeatherResults.Stub(){

        @Override
        public void sendResults(final List<WeatherData> results) throws RemoteException {
            mActivity.get().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    displayResults(results);
                }
            });
        }
    };
}
