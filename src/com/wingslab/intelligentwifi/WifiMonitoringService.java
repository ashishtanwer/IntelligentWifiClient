package com.wingslab.intelligentwifi;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

import com.wingslab.intelligentwifi.ViewStatsActivity.WifiScanReceiver;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import android.net.wifi.*;

public class WifiMonitoringService extends Service implements SensorEventListener,LocationListener {

	@SuppressWarnings("unused")
	private final String TAG = "WifiMonitoringService";

	private static final int NOTIFICATION_ID = 1;
	  
	// References to SensorManager and accelerometer
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	
	// Filtering constant

	private final float mAlpha = 0.8f;

	// Arrays for storing filtered values
	private float[] mGravity = new float[3];
	private float[] mAccel = new float[3];
	private double mAccelResult=0.0; 

	private int mStartID;
	private long mLastUpdate;
	private LocationManager mLocationManager;
    WifiManager mWifiManager;
    WifiScanReceiver mWifiScanReceiver;
	String wifiString[];
	private Location location;

	@Override
	public void onCreate() {
		super.onCreate();
		
		//Welcome Service
		Toast.makeText(getApplicationContext(), "Wifi Monitoring Service Started", Toast.LENGTH_LONG).show();	
		
		// Get reference to SensorManager
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		
		// Get reference to LocationManager
		mLocationManager=(LocationManager)getSystemService(LOCATION_SERVICE);
		
	    String provider = LocationManager.GPS_PROVIDER;
        mLocationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 0, 0, this);
	    
	    location = mLocationManager.getLastKnownLocation(provider);

		// Get reference to Accelerometer
		mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		mLastUpdate = System.currentTimeMillis();
		
		// Register listener
		mSensorManager.registerListener(this, mAccelerometer,
				SensorManager.SENSOR_DELAY_UI);
		
		mWifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		mWifiScanReceiver = new WifiScanReceiver();
		mWifiManager.startScan();
	

		// Create a notification area notification so the user 
		// can get back to the Wifi Monitoring Client
		
		final Intent notificationIntent = new Intent(getApplicationContext(),
				WelcomeScreenActivity.class);
		final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, 0);

		final Notification notification = new Notification.Builder(
				getApplicationContext())
				.setSmallIcon(android.R.drawable.ic_menu_always_landscape_portrait)
				.setOngoing(true).setContentTitle("Wifi Monitor")
				.setContentText("Wifi Monitoring Service Running")
				.setContentIntent(pendingIntent).build();

		// Put this Service in a foreground state, so it won't 
		// readily be killed by the system  
		startForeground(NOTIFICATION_ID, notification);
		
		ContentResolver contentResolver = getContentResolver();

		ContentValues values = new ContentValues();

		// Create and set cursor and list adapter
		Cursor c = contentResolver.query(RulesContract.CONTENT_URI, null, null, null,
				null);
		while(c.moveToNext()){
		    String rule = c.getString(c.getColumnIndex(RulesContract.DATA));
		    Toast.makeText(getApplicationContext(), " Applying Rule:"+rule, Toast.LENGTH_LONG).show();	
		}

	}

	
	@Override
	public int onStartCommand(Intent intent, int flags, int startid) {

		// Don't automatically restart this Service if it is killed
		return START_NOT_STICKY;
	}

	@Override
	public void onDestroy() {		
		
		// Destroy Service
		Toast.makeText(getApplicationContext(), "Wifi Monitoring Service Stopped", Toast.LENGTH_LONG).show();	
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
				
				// Apply low-pass filter
				mGravity[0] = lowPass(rawX, mGravity[0]);
				mGravity[1] = lowPass(rawY, mGravity[1]);
				mGravity[2] = lowPass(rawZ, mGravity[2]);

				// Apply high-pass filter
				mAccel[0] = highPass(rawX, mGravity[0]);
				mAccel[1] = highPass(rawY, mGravity[1]);
				mAccel[2] = highPass(rawZ, mGravity[2]);
				
				mAccelResult=Math.sqrt(Math.pow(mAccel[0],2)+Math.pow(mAccel[1],2)+Math.pow(mAccel[2],2));
				
				Log.d(TAG,  String.valueOf(mAccelResult));
				if(mAccelResult > 10)
				{
					try {
						Toast.makeText(getApplicationContext(), "Acceleration More than 10!!!!", Toast.LENGTH_LONG).show();	
						Log.d(TAG,  "Changing Wifi");
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
	
	// Deemphasize transient forces
	private float lowPass(float current, float gravity) {
		return gravity * mAlpha + current * (1 - mAlpha);
	}

	// Deemphasize constant forces
	private float highPass(float current, float gravity) {
		return current - gravity;

	}


	@Override
	public void onLocationChanged(Location location) {
		if(location.getSpeed() > 0.0)
		{
			try {
				Toast.makeText(getApplicationContext(), "Speed > 0.0!!!!", Toast.LENGTH_LONG).show();	
				Log.d(TAG,  "Changing Wifi");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}			
	}


	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}
	
   class WifiScanReceiver extends BroadcastReceiver {
	      public void onReceive(Context c, Intent intent) {
			List<ScanResult> wifiScanList = mWifiManager.getScanResults();
	         wifiString = new String[wifiScanList.size()+1];
	         wifiString[0] = "MobileData";
	         for(int i = 0; i < wifiScanList.size(); i++){
	            wifiString[i+1] = ((wifiScanList.get(i)).toString()).split(",")[0];
	         }
	         
	         String[] uniquewifiString = new HashSet<String>(Arrays.asList(wifiString)).toArray(new String[0]);
	         //String[] wifiStringunique =uniquewifiString;
	         //spinner.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
	         //android.R.layout.simple_list_item_1,uniquewifiString));
	         
	      }
	}

}
