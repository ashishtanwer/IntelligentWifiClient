package com.android.intelligentwifi;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

public class OrientationMonitoringService extends Service implements SensorEventListener {

	@SuppressWarnings("unused")
	private final String TAG = "OrientationMonitoringService";

	private static final int NOTIFICATION_ID = 1;
	  
	// References to SensorManager and accelerometer
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	
	// Filtering constant

	private final float mAlpha = 0.8f;

	// Arrays for storing filtered values
	private float[] mGravity = new float[3];
	private float[] mAccel = new float[3];

	private int mStartID;
	private long mLastUpdate;

	@Override
	public void onCreate() {
		super.onCreate();
		
		//Welcome Service
		Toast.makeText(getApplicationContext(), "Orientation Monitoring Service Started", Toast.LENGTH_LONG).show();	
		
		// Get reference to SensorManager
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

		// Get reference to Accelerometer
		mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		mLastUpdate = System.currentTimeMillis();
		
		// Register listener
		mSensorManager.registerListener(this, mAccelerometer,
				SensorManager.SENSOR_DELAY_UI);

		// Create a notification area notification so the user 
		// can get back to the Orientation Monitoring Client
		/*
		final Intent notificationIntent = new Intent(getApplicationContext(),
				ServiceAccelerometerClient.class);
		final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, 0);

		final Notification notification = new Notification.Builder(
				getApplicationContext())
				.setSmallIcon(android.R.drawable.ic_menu_always_landscape_portrait)
				.setOngoing(true).setContentTitle("Orientation Monitor")
				.setContentText("Orientation Monitoring Service Running")
				.setContentIntent(pendingIntent).build();

		// Put this Service in a foreground state, so it won't 
		// readily be killed by the system  
		startForeground(NOTIFICATION_ID, notification);*/

	}

	
	@Override
	public int onStartCommand(Intent intent, int flags, int startid) {

		// Don't automatically restart this Service if it is killed
		return START_NOT_STICKY;
	}

	@Override
	public void onDestroy() {		
		
		// Destroy Service
		Toast.makeText(getApplicationContext(), "Orientation Monitoring Service Stopped", Toast.LENGTH_LONG).show();	
		// Unregister listener
		mSensorManager.unregisterListener(this);

	}
	
	// Process new reading
	@Override
	public void onSensorChanged(SensorEvent event) {

		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

			long actualTime = System.currentTimeMillis();

			if (actualTime - mLastUpdate > 5000) {

				mLastUpdate = actualTime;

				float rawX = event.values[0];
				float rawY = event.values[1];
				float rawZ = event.values[2];
				
				PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
				Log.d(TAG,  String.valueOf(rawZ));
				if(rawZ <= -7 && pm.isScreenOn())
				{
					try {
						Toast.makeText(getApplicationContext(), "Device is going to Sleep!!!!", Toast.LENGTH_LONG).show();	
						Log.d(TAG,  "Calling Sleep");
						pm.goToSleep(0);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}		
				else 
				if(rawZ > -7 && !pm.isScreenOn())
				{
				    try {
						Toast.makeText(getApplicationContext(), "Device is Waking UP!!!!", Toast.LENGTH_LONG).show();	
						Log.d(TAG,  "Calling Wakeup");
						pm.wakeUp(0);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	
				}
			}
		}
	}

	// Can't bind to this Service
	@Override
	public IBinder onBind(Intent intent) {

		return null;

	}
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// NA
	}

}
