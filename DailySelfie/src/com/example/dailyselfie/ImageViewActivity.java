package com.example.dailyselfie;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

public class ImageViewActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		// Get the Intent used to start this Activity
		Intent intent = getIntent();
		
		// Make a new ImageView
		ImageView imageView = new ImageView(getApplicationContext());
		
		// Get the path of the image to display and set it as the URI for the image
		String path  = intent.getStringExtra(MainActivity.IMG_LOCATION);
		imageView.setImageURI(Uri.parse(path));				
		setContentView(imageView);
	}
}
