package com.wingslab.intelligentwifi;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import com.wingslab.intelligentwifi.R;
import com.wingslab.intelligentwifi.ViewStatsActivity.WifiScanReceiver;

import android.app.Activity;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class AddAccelerationRulesActivity extends Activity implements SensorEventListener {
	
	@SuppressWarnings("unused")
	private static final String TAG = "AddAccelerationRulesActivity";
	
    WifiManager mWifiManager;
    WifiScanReceiver mWifiScanReceiver;
	String wifiString[];
	Spinner spinner;
	TextView accelerationText;
	TextView currentaccelerationText;
	SeekBar seekbar;
	Button submitacceleraion;
	int progressChanged;
	
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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.addaccelerationrules);

		spinner = (Spinner) findViewById(R.id.pickaccelerationwifi);
		accelerationText = (TextView) findViewById(R.id.acceleraionDisplay);
		currentaccelerationText = (TextView) findViewById(R.id.currentaccelerationDisplay);
		seekbar = (SeekBar) findViewById(R.id.seekBar1);
		submitacceleraion = (Button) findViewById(R.id.submitacceleraionrule);
		
		//wifiList = (ListView)findViewById(R.id.listView1);
		mWifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		mWifiScanReceiver = new WifiScanReceiver();
		mWifiManager.startScan();
		
		// Get reference to SensorManager
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

		// Get reference to Accelerometer
		mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		mLastUpdate = System.currentTimeMillis();
		
		// Register listener
		mSensorManager.registerListener(this, mAccelerometer,
				SensorManager.SENSOR_DELAY_UI);
		
		submitacceleraion.setOnClickListener(new OnClickListener() {
			public void onClick(View src) {
				
			      
				ContentResolver contentResolver = getContentResolver();

				ContentValues values = new ContentValues();
				
				String record= "A"+accelerationText.getText().toString()+"-->"+spinner.getSelectedItem().toString();

				// Insert record
				values.put(RulesContract.DATA, record);
				Uri firstRecordUri = contentResolver.insert(RulesContract.CONTENT_URI, values);

				values.clear();

				// Create and set cursor and list adapter
				Cursor c = contentResolver.query(RulesContract.CONTENT_URI, null, null, null,
						null);
				
				Toast.makeText(AddAccelerationRulesActivity.this,"Acceleration Rule Added:"+record, 
						Toast.LENGTH_SHORT).show();
	
			}
		});

		
		seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			int progressChanged = 5;

			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
				progressChanged = progress;
				accelerationText.setText(String.valueOf(progress));
			}

			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}

			public void onStopTrackingTouch(SeekBar seekBar) {
				Toast.makeText(AddAccelerationRulesActivity.this,"seek bar progress:"+progressChanged, 
						Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	// Register listener
	@Override
	protected void onResume() {
		super.onResume();

	    registerReceiver(mWifiScanReceiver, new IntentFilter(
	    	      WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

	}

	// Unregister listener
	@Override
	protected void onPause() {
		super.onPause();

		unregisterReceiver(mWifiScanReceiver);
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
		         spinner.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
		         android.R.layout.simple_list_item_1,uniquewifiString));
		         
		      }
		}


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
				currentaccelerationText.setText(String.valueOf(mAccelResult));	
			}
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
	
	// Deemphasize transient forces
	private float lowPass(float current, float gravity) {
		return gravity * mAlpha + current * (1 - mAlpha);
	}

	// Deemphasize constant forces
	private float highPass(float current, float gravity) {
		return current - gravity;

	}
}