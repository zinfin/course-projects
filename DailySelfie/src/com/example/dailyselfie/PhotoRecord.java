package com.example.dailyselfie;

import android.graphics.Bitmap;

public class PhotoRecord {
	private String mImageName;
	private Bitmap mPhotoBitmap;
	private String mPath;
	
	public PhotoRecord(String imageName, Bitmap bitmap, String path) {
		super();
		mImageName = imageName;
		mPhotoBitmap = bitmap;
		mPath = path;
	}

	public String getImageName() {
		return mImageName;
	}

	public void setImageName(String imageName) {
		mImageName = imageName;
	}

	public Bitmap getPhotoBitmap() {
		return mPhotoBitmap;
	}

	public void setPhotoBitmap(Bitmap bitmap) {
		mPhotoBitmap = bitmap;
	}
	public String getPath(){
		return mPath;
	}
	public void setPath(String path){
		mPath = path;
	}

}
