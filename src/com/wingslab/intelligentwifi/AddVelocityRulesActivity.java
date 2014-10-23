package com.wingslab.intelligentwifi;

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
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
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

public class AddVelocityRulesActivity extends Activity implements SensorEventListener,LocationListener{
	
	@SuppressWarnings("unused")
	private static final String TAG = "AddVelovityRulesActivity";
	
    WifiManager mWifiManager;
    WifiScanReceiver mWifiScanReceiver;
	String wifiString[];
	Spinner spinner;
	TextView velocityText;
	TextView currentvelocityText;
	SeekBar seekbar;
	Button submitvelocity;
	int progressChanged;
	LocationManager mLocationManager;
	Location location;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.addvelocityrules);

		spinner = (Spinner) findViewById(R.id.pickvelocitywifi);
		velocityText = (TextView) findViewById(R.id.velocityDisplay);
		currentvelocityText = (TextView) findViewById(R.id.currentvelocityDisplay);
		seekbar = (SeekBar) findViewById(R.id.seekBar1);
		submitvelocity = (Button) findViewById(R.id.submitvelocityrule);
		
		//wifiList = (ListView)findViewById(R.id.listView1);
		mWifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		mWifiScanReceiver = new WifiScanReceiver();
		mWifiManager.startScan();
		
		// Get reference to LocationManager
		mLocationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
		
	    String provider = LocationManager.GPS_PROVIDER;
        mLocationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 0, 0, this);
	    
	    location = mLocationManager.getLastKnownLocation(provider);
	    currentvelocityText.setText(String.valueOf(0));
		if(location!=null)
		{
			currentvelocityText.setText(String.valueOf(location.getLatitude()));
		}
		
		submitvelocity.setOnClickListener(new OnClickListener() {
			public void onClick(View src) {
				
			      
				ContentResolver contentResolver = getContentResolver();

				ContentValues values = new ContentValues();
				
				String record= "V"+velocityText.getText().toString()+"#"+spinner.getSelectedItem().toString();

				// Insert record
				values.put(RulesContract.DATA, record);
				Uri firstRecordUri = contentResolver.insert(RulesContract.CONTENT_URI, values);

				values.clear();

				// Create and set cursor and list adapter
				Cursor c = contentResolver.query(RulesContract.CONTENT_URI, null, null, null,
						null);
				
				Toast.makeText(AddVelocityRulesActivity.this,"Velocity Rule Added:"+record, 
						Toast.LENGTH_SHORT).show();
	
			}
		});

		
		seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			int progressChanged = 5;

			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
				progressChanged = progress;
				velocityText.setText(String.valueOf(progress));
			}

			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}

			public void onStopTrackingTouch(SeekBar seekBar) {
				Toast.makeText(AddVelocityRulesActivity.this,"seek bar progress:"+progressChanged, 
						Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	// Register listener
	@Override
	protected void onResume() {
		super.onResume();
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                3600000, 1000, this);
	    registerReceiver(mWifiScanReceiver, new IntentFilter(
	    	      WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

	}

	// Unregister listener
	@Override
	protected void onPause() {
		super.onPause();
		mLocationManager.removeUpdates(this);
		unregisterReceiver(mWifiScanReceiver);
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


	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLocationChanged(Location location) {
		currentvelocityText.setText(String.valueOf(location.getSpeed()));
		
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
}