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
import android.widget.Toast;

public class AddLocationRulesActivity extends Activity {

	
	@SuppressWarnings("unused")
	private static final String TAG = "AddLocationRulesActivity";
	
    WifiManager mWifiManager;
    WifiScanReceiver mWifiScanReceiver;
	String wifiString[];
	String locationString[];
	Spinner spinner;
	Spinner picklocationspinner;
	public static final int PICK_LOCATION=0;
	public static final Uri uri=com.wingslab.intelligentwifi.LocationContract.CONTENT_URI;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.addlocationrules);

		spinner = (Spinner) findViewById(R.id.picklocationwifi);
		picklocationspinner= (Spinner) findViewById(R.id.picklocationspinner);
		Button submitlocation = (Button) findViewById(R.id.submitlocationrule);
		
		ContentResolver contentResolver = getContentResolver();

		ContentValues values = new ContentValues();

		// Create and set cursor and list adapter
		Cursor c = contentResolver.query(LocationContract.CONTENT_URI, null, null, null,
				null);

		picklocationspinner.setAdapter(new SimpleCursorAdapter(this, R.layout.viewlocations, c,
				LocationContract.ALL_COLUMNS, new int[] { R.id.idString,
				R.id.data }, 0));

		
		submitlocation.setOnClickListener(new OnClickListener() {
			public void onClick(View src) {
				
			      
				ContentResolver contentResolver = getContentResolver();

				ContentValues values = new ContentValues();
				Cursor cursor = (Cursor) picklocationspinner.getSelectedItem();
				String myColumnValue = cursor.getString(1);
				String record= "L"+myColumnValue+"-->"+spinner.getSelectedItem().toString();
				// Insert first record
				values.put(RulesContract.DATA, record);
				Uri firstRecordUri = contentResolver.insert(RulesContract.CONTENT_URI, values);

				values.clear();

				// Create and set cursor and list adapter
				Cursor c = contentResolver.query(RulesContract.CONTENT_URI, null, null, null,
						null);
				
				Toast.makeText(AddLocationRulesActivity.this,"Location Rule Added:"+record, 
						Toast.LENGTH_SHORT).show();
		
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
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK
				&& requestCode == PICK_LOCATION) {

			ContentResolver cr = getContentResolver();
			Cursor cursor = cr.query(data.getData(), null, null, null, null);

			if (null != cursor && cursor.moveToFirst()) {
				String id = cursor
						.getString(cursor.getColumnIndex("col"));
				
				/*
				String where = DATA_CONTACT_ID + " = ? AND " + DATA_MIMETYPE
						+ " = ?";
				String[] whereParameters = new String[] { id,
						STRUCTURED_POSTAL_CONTENT_ITEM_TYPE };
				Cursor addrCur = cr.query(DATA_CONTENT_URI, null, where,
						whereParameters, null);

				if (null != addrCur && addrCur.moveToFirst()) {
					String formattedAddress = addrCur
							.getString(addrCur
									.getColumnIndex(STRUCTURED_POSTAL_FORMATTED_ADDRESS));

					if (null != formattedAddress) {
						formattedAddress = formattedAddress.replace(' ', '+');
						Intent geoIntent = new Intent(
								android.content.Intent.ACTION_VIEW,
								Uri.parse("geo:0,0?q=" + formattedAddress));
						startActivity(geoIntent);
					}
				}
				if (null != addrCur)
					addrCur.close();
			}*/
			if (null != cursor)
				cursor.close();
		}
	}
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

}