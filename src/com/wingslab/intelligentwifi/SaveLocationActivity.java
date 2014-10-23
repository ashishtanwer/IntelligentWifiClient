package com.wingslab.intelligentwifi;


import java.util.List;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;
import com.wingslab.intelligentwifi.R;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender.SendIntentException;
import android.database.Cursor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SaveLocationActivity extends Activity implements
     LocationListener{

	private final String TAG = "MapLocation";
	private LocationManager mLocationManager;
	private TextView mLongitudeView, mLatitudeView;
	private Location location;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		// Restore any saved state 
		super.onCreate(savedInstanceState);
		
		// Set content view
		setContentView(R.layout.savelocation);
		
		// Get reference to LocationManager
		mLocationManager=(LocationManager)getSystemService(LOCATION_SERVICE);

		// Initialize UI elements
		final EditText addrText = (EditText) findViewById(R.id.location);
		final Button mapbutton = (Button) findViewById(R.id.mapButton);
		final Button savelocationbutton = (Button) findViewById(R.id.savelocationButton);
		mLatitudeView = (TextView) findViewById(R.id.latitude_view);
		mLongitudeView = (TextView) findViewById(R.id.longitude_view);
		final EditText locationname = (EditText) findViewById(R.id.locationname);
		
	    String provider = LocationManager.GPS_PROVIDER;
        mLocationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 0, 0, this);
	    
	    location = mLocationManager.getLastKnownLocation(provider);
		if(location!=null)
		{
			mLatitudeView.setText(String.valueOf(location.getLatitude()));
			mLongitudeView.setText(String.valueOf(location.getLongitude()));
		}

		// Link UI elements to actions in code		
		mapbutton.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					String address = addrText.getText().toString();
					address = address.replace(' ', '+');
					Intent geoIntent = new Intent(
							android.content.Intent.ACTION_VIEW, Uri
									.parse("geo:0,0?q=" + address));
					startActivity(geoIntent);
				} catch (Exception e) {
					Log.e(TAG, e.toString());
				}
			}
		});
		
		savelocationbutton.setOnClickListener(new OnClickListener() {
			public void onClick(View src) {
				
			      
				ContentResolver contentResolver = getContentResolver();

				ContentValues values = new ContentValues();
				
				String record= locationname.getText()+"{"+mLatitudeView.getText().toString()+","+mLongitudeView.getText().toString()+"}";

				// Insert record
				values.put(LocationContract.DATA, record);
				Uri firstRecordUri = contentResolver.insert(LocationContract.CONTENT_URI, values);

				values.clear();

				// Create and set cursor and list adapter
				Cursor c = contentResolver.query(LocationContract.CONTENT_URI, null, null, null,
						null);
				
				Toast.makeText(SaveLocationActivity.this,"New Location Added:"+record, 
						Toast.LENGTH_SHORT).show();
	
			}
		});
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		Log.i(TAG, "The activity is visible and about to be started.");
	}

	
	@Override
	protected void onRestart() {
		super.onRestart();
		Log.i(TAG, "The activity is visible and about to be restarted.");
	}

	@Override
	protected void onResume() {
		super.onResume();	
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                3600000, 1000, this);
		Log.i(TAG, "The activity is and has focus (it is now \"resumed\")");
	}

	@Override
	protected void onPause() {
		super.onPause();
		mLocationManager.removeUpdates(this);
		Log.i(TAG,
				"Another activity is taking focus (this activity is about to be \"paused\")");
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.i(TAG, "The activity is no longer visible (it is now \"stopped\")");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "The activity is about to be destroyed.");
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
}
