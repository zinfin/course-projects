package com.example.dailyselfie;

import java.text.DateFormat;
import java.util.Date;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

public class AlarmNotificationReceiver extends BroadcastReceiver  {

	// Notification ID to allow for future updates
	private static final int MY_NOTIFICATION_ID = 1;
	private static final String TAG = "DailySelfieAlarmReceiver";

	// Notification Text Elements
	private final CharSequence tickerText = "Daily Selfie Reminder";
	private final CharSequence contentTitle = "Daily Selfie";
	private final CharSequence contentText = "Time for another selfie";

	// Notification Action Elements
	private Intent mNotificationIntent;
	private PendingIntent mContentIntent;

	// Notification Sound and Vibration on Arrival
	private final Uri soundURI = Uri
			.parse("android.resource://com.example.dailyselfie/"
					+ R.raw.alarm_rooster);
	private final long[] mVibratePattern = { 0, 200, 200, 300 };

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(TAG, "Received broadcast at:"
				+ DateFormat.getDateTimeInstance().format(new Date()));
		// The Intent to be used when the user clicks on the Notification View
		mNotificationIntent = new Intent(context, MainActivity.class);

		// The PendingIntent that wraps the underlying Intent
		mContentIntent = PendingIntent.getActivity(context, 0,
				mNotificationIntent, Intent.FLAG_ACTIVITY_NEW_TASK);

		// Build the Notification
		Notification.Builder notificationBuilder = new Notification.Builder(
				context).setTicker(tickerText)
				.setSmallIcon(R.drawable.ic_action_camera)
				.setAutoCancel(true).setContentTitle(contentTitle)
				.setContentText(contentText).setContentIntent(mContentIntent)
				.setSound(soundURI).setVibrate(mVibratePattern);

		// Get the NotificationManager
		NotificationManager mNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		// Pass the Notification to the NotificationManager:
		mNotificationManager.notify(MY_NOTIFICATION_ID,
				notificationBuilder.build());

		// Log occurence of notify() call
		Log.i(TAG, "Sending notification at:"
				+ DateFormat.getDateTimeInstance().format(new Date()));

	}

}
