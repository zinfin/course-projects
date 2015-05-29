package com.example.weatherapp.operation;

import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.example.weatherapp.activity.MainActivity;
import com.example.weatherapp.aidl.WeatherCall;
import com.example.weatherapp.aidl.WeatherRequest;
import com.example.weatherapp.util.GenericServiceConnection;

import java.lang.ref.WeakReference;
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

    @Override
    public void bindService() {

    }

    @Override
    public void unbindService() {

    }

    @Override
    public void doSync(View v) {

    }

    @Override
    public void doAsync(View v) {

    }

    @Override
    public void onConfigurationChange(MainActivity activity) {

    }

    /**
     * Initialize the View fields, which are all stored as
     * WeakReferences to enable garbage collection.
     */
    private void initializeViewFields() {

    }

    /**
     * (Re)initialize the non-view fields (e.g.,
     * GenericServiceConnection objects).
     */
    private void initializeNonViewFields() {

    }
}
