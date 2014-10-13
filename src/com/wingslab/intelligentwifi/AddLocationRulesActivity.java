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

public class AddLocationRulesActivity extends Activity {

	
	@SuppressWarnings("unused")
	private static final String TAG = "AddLocationRulesActivity";
	
    WifiManager mWifiManager;
    WifiScanReceiver mWifiScanReceiver;
	String wifiString[];
	Spinner spinner;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.addlocationrules);

		spinner = (Spinner) findViewById(R.id.picklocationwifi);
		Button submitlocation = (Button) findViewById(R.id.submitlocationrule);
		
		submitlocation.setOnClickListener(new OnClickListener() {
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

		
		//wifiList = (ListView)findViewById(R.id.listView1);
		mWifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		mWifiScanReceiver = new WifiScanReceiver();
		mWifiManager.startScan();
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