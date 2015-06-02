package com.example.dailyselfie;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.AlarmManager;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends ListActivity
{
	private static final long TWO_MINS = 2 * 60 * 1000L;
	private static final int REQUEST_IMAGE_CAPTURE = 1;
	private static final int REQUEST_TAKE_PHOTO = 1;
	private static final int IMAGE_HEIGHT = 10;
	private static final int IMAGE_WIDTH = 10;
	protected static final String IMG_LOCATION = "image location";
	private static final String TAG = "Daily Selfie";

	private String mCurrentPhotoPath;
	private String mCurrentFileName;
	private String mAbsolutePath;
	private ThumbnailAdapter mAdapter;
	private AlarmManager mAlarmManager;
	private Intent mNotificationReceiverIntent;
	private PendingIntent mNotificationReceiverPendingIntent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "Starting main activity at:"
				+ DateFormat.getDateTimeInstance().format(new Date()));
		
		super.onCreate(savedInstanceState);
		
		// Load any existing photos
		final List<PhotoRecord> list = loadExistingPhotos();
		mAdapter = new ThumbnailAdapter(this, R.layout.activity_main,list);
		
		// Get the list view and attach the adapter with any saved photos
		final ListView imageList = getListView();
		imageList.setAdapter(mAdapter);
		
		// Get the alarm manager
		mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		
		// Set up a pending intent that will start the alarm notificatoin receiver
		mNotificationReceiverIntent = new Intent(MainActivity.this,
				AlarmNotificationReceiver.class);
		mNotificationReceiverPendingIntent = PendingIntent.getBroadcast(
					MainActivity.this, 0, mNotificationReceiverIntent, 0);
		
		
		mAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 
				SystemClock.elapsedRealtime() + TWO_MINS, 
				TWO_MINS,
				mNotificationReceiverPendingIntent);
	}

	/* Handle list item clicks by creating an intent for the ImageViewActivity */
	@Override
	public void onListItemClick(ListView l, View v, int position, long id){
		//Create an Intent to start the ImageViewActivity
		Intent intent = new Intent(MainActivity.this, ImageViewActivity.class);
		// Add the file path for the selected item to the extra information
		PhotoRecord record = mAdapter.getItem(position);
		intent.putExtra(IMG_LOCATION, record.getPath());
		// Start the ImageViewActivity
		startActivity(intent);
		
	}
	/* Create the option menu with the camera icon */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/* Specify what happens if the camera icon in the menu is clicked */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		final int id = item.getItemId();
		if (id == R.id.action_settings) {
			dispatchTakePictureIntent();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	/* Load any saved pictures from the Environment.DIRECTORY_PICTURES location */
	private List<PhotoRecord> loadExistingPhotos(){
		List<PhotoRecord> photoRecords = new ArrayList<PhotoRecord>();
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			final File directory=	getExternalFilesDir(Environment.DIRECTORY_PICTURES);
			final String directoryPath = directory.getAbsolutePath();
			Log.i(TAG,directoryPath);
			final String[] files = directory.list();
			String fileName= null;
			for (int i=0;i < files.length;i++){				
				fileName = directoryPath.concat(File.separator+files[i]);
				final Bitmap bitmap = setPic(IMAGE_WIDTH, IMAGE_HEIGHT,fileName);
		        final PhotoRecord record = new PhotoRecord(files[i], bitmap, fileName);
		        photoRecords.add(record);
			}
		}			
		return photoRecords;
	}
	/* Kickoff an intent that will be handled by the camera */
	private void dispatchTakePictureIntent() {
	    final Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    // Ensure that there's a camera activity to handle the intent
	    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
	        // Create the File where the photo should go
	        File photoFile = null;
	        
	        if (isExternalStorageWritable()){
	        	photoFile = createImageFile();
	        }else{
	        	Toast.makeText(getApplicationContext(), "External storage is not available", Toast.LENGTH_SHORT).show();
	        }
	    		
	        // Continue only if the File was successfully created
	        if (photoFile != null) {
	            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
	                    Uri.fromFile(photoFile));
	            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
	        }
	    }
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && mAbsolutePath!=null) {	       	
	        final Bitmap bitmap = setPic(IMAGE_WIDTH, IMAGE_HEIGHT, mAbsolutePath);
	        final PhotoRecord record = new PhotoRecord(mCurrentFileName, bitmap, mAbsolutePath);
	        mAdapter.add(record);
	    }
	}

	private File createImageFile()  {
		// Create an image file name
		final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		mCurrentFileName = timeStamp;
		File image = null;
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {

			image = new File(
					getExternalFilesDir(Environment.DIRECTORY_PICTURES),
					mCurrentFileName);
		
			mCurrentPhotoPath = "file:" + image.getAbsolutePath();
			Log.i(TAG, "Current path: " + mCurrentPhotoPath);
			mAbsolutePath = image.getAbsolutePath();
			Log.i(TAG,"Absolute path: " + mAbsolutePath);
		}
		return image;
	}
	/* Checks if external storage is available for read and write */
	public boolean isExternalStorageWritable() {
	    final String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state)) {
	        return true;
	    }
	    return false;
	}

	private Bitmap setPic(int targetW, int targetH, String fileName) {


	    // Get the dimensions of the bitmap
	    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
	    bmOptions.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(fileName, bmOptions);
	    int photoW = bmOptions.outWidth;
	    int photoH = bmOptions.outHeight;

	    // Determine how much to scale down the image
	    int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

	    // Decode the image file into a Bitmap sized to fill the View
	    bmOptions.inJustDecodeBounds = false;
	    bmOptions.inSampleSize = scaleFactor;
	    bmOptions.inPurgeable = true;

	    Bitmap bitmap = BitmapFactory.decodeFile(fileName, bmOptions);
	    return bitmap;
	}

}
