package com.example.dailyselfie;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ThumbnailAdapter extends ArrayAdapter<PhotoRecord> {

	private final Activity mContext;
	private final List<PhotoRecord> mRecords;
	public ThumbnailAdapter(Activity context, int resource, List<PhotoRecord> records) {
		super(context, resource, records);
		mRecords = records;
		mContext = context;
	}
	@Override
	public View getView(int position, View view, ViewGroup parent){
		LayoutInflater inflater = mContext.getLayoutInflater();
		View rowView = inflater.inflate(R.layout.main, null,true);
		TextView txtName = (TextView) rowView.findViewById(R.id.image_name);
		ImageView image = (ImageView) rowView.findViewById(R.id.image);
		PhotoRecord record = mRecords.get(position);
		txtName.setText(record.getImageName());
		image.setImageBitmap(record.getPhotoBitmap());
		return rowView;
		
	}
}
