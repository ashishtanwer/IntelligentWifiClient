package com.android.intelligentwifi;



import java.util.Calendar;
import java.util.List;

import com.android.intelligentwifi.ViewStatsActivity.WifiScanReceiver;

import android.app.Activity;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

public class AddTimeRulesActivity extends Activity {

	
	@SuppressWarnings("unused")
	private static final String TAG = "AddRulesActivity";


	private TextView mTimeDisplay;
	private Button mPickTime;

	private int mHour;
	private int mMinute;

	static final int TIME_DIALOG_ID = 0;
	
    WifiManager mWifiManager;
    WifiScanReceiver mWifiScanReceiver;
	String wifiString[];
	Spinner spinner;
	
	// the callback received when the user "sets" the time in the dialog
	private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			mHour = hourOfDay;
			mMinute = minute;
			updateDisplay();
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.addtimerules);
		// capture our View elements
		mTimeDisplay = (TextView) findViewById(R.id.timeDisplay);
		mPickTime = (Button) findViewById(R.id.pickTime);
		Button submittime = (Button) findViewById(R.id.submittimerule);
		submittime.setOnClickListener(new OnClickListener() {
			public void onClick(View src) {
				
			      
				ContentResolver contentResolver = getContentResolver();

				ContentValues values = new ContentValues();

				// Insert first record
				values.put(RulesContract.DATA, "Record1");
				Uri firstRecordUri = contentResolver.insert(RulesContract.CONTENT_URI, values);

				values.clear();

				// Create and set cursor and list adapter
				Cursor c = contentResolver.query(RulesContract.CONTENT_URI, null, null, null,
						null);

				//setListAdapter(new SimpleCursorAdapter(this, R.layout.list_layout, c,
						//DataContract.ALL_COLUMNS, new int[] { R.id.idString,
								//R.id.data }, 0));
		
			}
		});

		// add a click listener to the button
		mPickTime.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDialog(TIME_DIALOG_ID);
			}
		});

		// get the current time
		final Calendar c = Calendar.getInstance();
		mHour = c.get(Calendar.HOUR_OF_DAY);
		mMinute = c.get(Calendar.MINUTE);

		// display the current date
		updateDisplay();

		spinner = (Spinner) findViewById(R.id.picktimewifi);
		
		//wifiList = (ListView)findViewById(R.id.listView1);
		mWifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		mWifiScanReceiver = new WifiScanReceiver();
		mWifiManager.startScan();
	}
	
	// updates the time we display in the TextView
	private void updateDisplay() {
		mTimeDisplay.setText(new StringBuilder().append(pad(mHour)).append(":")
				.append(pad(mMinute)));
	}

	private static String pad(int c) {
		if (c >= 10)
			return String.valueOf(c);
		else
			return "0" + String.valueOf(c);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case TIME_DIALOG_ID:
			return new TimePickerDialog(this, mTimeSetListener, mHour, mMinute,
					false);
		}
		return null;
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
		         wifiString = new String[wifiScanList.size()];
		         for(int i = 0; i < wifiScanList.size(); i++){
		            wifiString[i] = ((wifiScanList.get(i)).toString()).split(",")[0];
		         }
		         spinner.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
		         android.R.layout.simple_list_item_1,wifiString));
		         
		      }
		}
}