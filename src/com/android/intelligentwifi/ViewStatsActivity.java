package com.android.intelligentwifi;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ListView;
import android.widget.Toast;
import android.os.PowerManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.*;
import android.os.Bundle;

public class ViewStatsActivity extends Activity implements
		SensorEventListener,LocationListener{

	// References to SensorManager and accelerometer

	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private LocationManager mLocationManager;
    WifiManager mWifiManager;
    WifiScanReceiver mWifiScanReceiver;
	String wifiString[];
	Spinner spinner;

	// Filtering constant

	private final float mAlpha = 0.8f;

	// Arrays for storing filtered values
	private float[] mGravity = new float[3];
	private float[] mAccel = new float[3];

	private TextView mXValueView, mYValueView, mZValueView, 
					mXGravityView, mYGravityView, mZGravityView, 
					mXAccelView, mYAccelView, mZAccelView, mLongitudeView, mLatitudeView;

	private long mLastUpdate;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.viewstats);

		mXValueView = (TextView) findViewById(R.id.x_value_view);
		mYValueView = (TextView) findViewById(R.id.y_value_view);
		mZValueView = (TextView) findViewById(R.id.z_value_view);

		mXGravityView = (TextView) findViewById(R.id.x_lowpass_view);
		mYGravityView = (TextView) findViewById(R.id.y_lowpass_view);
		mZGravityView = (TextView) findViewById(R.id.z_lowpass_view);

		mXAccelView = (TextView) findViewById(R.id.x_highpass_view);
		mYAccelView = (TextView) findViewById(R.id.y_highpass_view);
		mZAccelView = (TextView) findViewById(R.id.z_highpass_view);
		
		mLatitudeView = (TextView) findViewById(R.id.latitude_view);
		mLongitudeView = (TextView) findViewById(R.id.longitude_view);

		// Get reference to SensorManager
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		
		// Get reference to LocationManager
		mLocationManager=(LocationManager)getSystemService(LOCATION_SERVICE);

		// Get reference to Accelerometer
		if (null == (mAccelerometer = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)))
			finish();

		mLastUpdate = System.currentTimeMillis();
		
		spinner = (Spinner) findViewById(R.id.spinner);
		
		//wifiList = (ListView)findViewById(R.id.listView1);
		mWifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		mWifiScanReceiver = new WifiScanReceiver();
		mWifiManager.startScan();
	}
		


	// Register listener
	@Override
	protected void onResume() {
		super.onResume();

		mSensorManager.registerListener(this, mAccelerometer,
				SensorManager.SENSOR_DELAY_UI);
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                3600000, 1000, this);
	    registerReceiver(mWifiScanReceiver, new IntentFilter(
	    	      WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

	}

	// Unregister listener
	@Override
	protected void onPause() {
		super.onPause();

		mSensorManager.unregisterListener(this);
		mLocationManager.removeUpdates(this);
		unregisterReceiver(mWifiScanReceiver);
	}
	
	// Process new reading
	@Override
	public void onSensorChanged(SensorEvent event) {

		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

			long actualTime = System.currentTimeMillis();

			if (actualTime - mLastUpdate > 500) {

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

				mXValueView.setText(String.valueOf(rawX));
				mYValueView.setText(String.valueOf(rawY));
				mZValueView.setText(String.valueOf(rawZ));

				mXGravityView.setText(String.valueOf(mGravity[0]));
				mYGravityView.setText(String.valueOf(mGravity[1]));
				mZGravityView.setText(String.valueOf(mGravity[2]));

				mXAccelView.setText(String.valueOf(mAccel[0]));
				mYAccelView.setText(String.valueOf(mAccel[1]));
				mZAccelView.setText(String.valueOf(mAccel[2]));

			}
		}
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
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// NA
	}

	@Override
	public void onLocationChanged(Location location) {

		mLatitudeView.setText(String.valueOf(location.getLatitude()));
		mLongitudeView.setText(String.valueOf(location.getLongitude()));
		
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
	         wifiString = new String[wifiScanList.size()];
	         for(int i = 0; i < wifiScanList.size(); i++){
	            wifiString[i] = ((wifiScanList.get(i)).toString()).split(",")[0];
	         }
	         spinner.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
	         android.R.layout.simple_list_item_1,wifiString));
	         
	      }
	}
}