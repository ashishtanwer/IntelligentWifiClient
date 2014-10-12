package com.android.intelligentwifi;


import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SaveLocationActivity extends Activity implements
     LocationListener{

	private final String TAG = "MapLocation";
	private LocationManager mLocationManager;
	private TextView mLongitudeView, mLatitudeView;
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
		final Button button = (Button) findViewById(R.id.mapButton);
		mLatitudeView = (TextView) findViewById(R.id.latitude_view);
		mLongitudeView = (TextView) findViewById(R.id.longitude_view);

		// Link UI elements to actions in code		
		button.setOnClickListener(new Button.OnClickListener() {
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
