package com.example.weatherapp.operation;

import android.content.Context;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
     * Acronym entered by the user.
     */
    protected WeakReference<EditText> mEditText;

    protected WeakReference<Button> mAsyncButton;
    protected WeakReference<Button> mSyncButton;
    protected WeakReference<TextView> mLocation;
    protected WeakReference<TextView> mWindSpeed;
    protected WeakReference<TextView> mWindDir;
    protected WeakReference<TextView> mTemperature;
    protected WeakReference<TextView> mHumidity;
    protected WeakReference<TextView> mSunrise;
    protected WeakReference<TextView> mSunset;
    protected WeakReference<LinearLayout> mResultTable;
    private boolean mResultsReturned = false;
    private List<WeatherData> mData;
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
                    displayResults(weatherData);

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
        mActivity = new WeakReference<>(activity);
        // Do any re-initializing
        initializeViewFields();
        initializeNonViewFields();
    }
    @Override
    public void showResults(){
        if (mResultsReturned){
            mResultTable.get().setVisibility(View.VISIBLE);
            loadData();
        }

    }
    /**
     * Initialize the View fields, which are all stored as
     * WeakReferences to enable garbage collection.
     */
    private void initializeViewFields() {
        mActivity.get().setContentView(R.layout.activity_main);
        mEditText = new WeakReference<> ((EditText)mActivity.get().findViewById(R.id.editText));
        mSyncButton = new WeakReference<>((Button) mActivity.get().findViewById(R.id.button));
        mAsyncButton =new WeakReference<>((Button) mActivity.get().findViewById(R.id.button2));
        mResultTable = new WeakReference<>((LinearLayout) mActivity.get().findViewById(R.id.resullTable));
        mLocation = new WeakReference<> ((TextView) mActivity.get().findViewById(R.id.location));
        mWindSpeed = new WeakReference<> ((TextView) mActivity.get().findViewById(R.id.windspeed));
        mWindDir = new WeakReference<> ((TextView) mActivity.get().findViewById(R.id.winddirection));
        mTemperature = new WeakReference<> ((TextView) mActivity.get().findViewById(R.id.temp));
        mHumidity = new WeakReference<> ((TextView) mActivity.get().findViewById(R.id.humidity));
        mSunrise = new WeakReference<> ((TextView) mActivity.get().findViewById(R.id.sunrise));
        mSunset = new WeakReference<> ((TextView) mActivity.get().findViewById(R.id.sunset));
        mSyncButton.get().setEnabled(false);
        mAsyncButton.get().setEnabled(false);
        mResultTable.get().setVisibility(View.INVISIBLE);
        mLocation.get().setText("");
        mWindSpeed.get().setText("");
        mWindDir.get().setText("");
        mTemperature.get().setText("");
        mHumidity.get().setText("");
        mSunrise.get().setText("");
        mSunset.get().setText("");


        EditText input = (EditText) mActivity.get().findViewById(R.id.editText);
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //no op
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //no op
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Hide results
                mResultTable.get().setVisibility(View.INVISIBLE);
                mSyncButton.get().setEnabled(true);
                mAsyncButton.get().setEnabled(true);
            }
        });
    }

    /**
     * (Re)initialize the non-view fields (e.g.,
     * GenericServiceConnection objects).
     */
    private void initializeNonViewFields() {
        mServiceConnectionAsync =
                new GenericServiceConnection<>(WeatherRequest.class);
        mServiceConnectionSync =
                new GenericServiceConnection<>(WeatherCall.class);
    }

    private void displayResults(List<WeatherData> weatherResults) {
        // Show the result table
        mData = weatherResults;
        if (mData != null && mData.size()>0){
            loadData();
        }else{
            // Clear results and show text message
            Toast.makeText(mActivity.get(),"No results returned for location", Toast.LENGTH_LONG).show();
            mResultsReturned = false;
        }
    }
    private void loadData(){
        WeatherData data = mData.get(0);
        mResultTable.get().setVisibility(View.VISIBLE);
        mLocation.get().setText(data.getmName());
        mWindSpeed.get().setText(""+data.getmSpeed());
        mWindDir.get().setText(""+data.getmDeg());
        mTemperature.get().setText(""+data.getmTemp());
        mHumidity.get().setText(""+data.getmHumidity());
        mSunrise.get().setText(getStringTime(data.getmSunrise()));
        mSunset.get().setText(getStringTime(data.getmSunset()));
        mResultsReturned =true;


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
    /**
     * Get time given milliseconds
     */
    private String getStringTime(long timeInSeconds){
        Date date = new Date(timeInSeconds * 1000L);

        DateFormat formatter = new SimpleDateFormat("HH:mm:ss z");
        return formatter.format(date);
    }
}
