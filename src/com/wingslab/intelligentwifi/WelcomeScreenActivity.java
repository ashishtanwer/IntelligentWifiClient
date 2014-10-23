package com.wingslab.intelligentwifi;

import java.util.Random;

import com.wingslab.intelligentwifi.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class WelcomeScreenActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcomescreen);

		final Button viewrulesButton = (Button) findViewById(R.id.viewrules_Button);
		viewrulesButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

					Intent statsIntent = new Intent(WelcomeScreenActivity.this,
							ViewRulesActivity.class);
					startActivity(statsIntent);

			}
		});
		
		final Button addlocationrulesButton = (Button) findViewById(R.id.addlocationrules_Button);
		addlocationrulesButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

					Intent statsIntent = new Intent(WelcomeScreenActivity.this,
							AddLocationRulesActivity.class);
					startActivity(statsIntent);

			}
		});

		final Button addvelocityrulesButton = (Button) findViewById(R.id.addvelocityrules_Button);
		addvelocityrulesButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

					Intent statsIntent = new Intent(WelcomeScreenActivity.this,
							AddVelocityRulesActivity.class);
					startActivity(statsIntent);

			}
		});
		
		final Button addaccelerationrulesButton = (Button) findViewById(R.id.addaccelerationrules_Button);
		addaccelerationrulesButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

					Intent statsIntent = new Intent(WelcomeScreenActivity.this,
							AddAccelerationRulesActivity.class);
					startActivity(statsIntent);

			}
		});
		
		final Button addtimerulesButton = (Button) findViewById(R.id.addtimerules_Button);
		addtimerulesButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

					Intent statsIntent = new Intent(WelcomeScreenActivity.this,
							AddTimeRulesActivity.class);
					startActivity(statsIntent);

			}
		});
		
		final Button savelocationButton = (Button) findViewById(R.id.savelocation_Button);
		savelocationButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

					Intent statsIntent = new Intent(WelcomeScreenActivity.this,
							SaveLocationActivity.class);
					startActivity(statsIntent);

			}
		});
		
		final Button viewlocationButton = (Button) findViewById(R.id.viewlocation_Button);
		viewlocationButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

					Intent statsIntent = new Intent(WelcomeScreenActivity.this,
							ViewLocationActivity.class);
					startActivity(statsIntent);

			}
		});
		
		final Button viewstatsButton = (Button) findViewById(R.id.viewstats_Button);
		viewstatsButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

					Intent statsIntent = new Intent(WelcomeScreenActivity.this,
							ViewStatsActivity.class);
					startActivity(statsIntent);

			}
		});		
		
		// Intent used for starting the Orientation Monitoring Service
		final Intent wifiMonitoringServiceIntent = new Intent(getApplicationContext(),
				WifiMonitoringService.class);

		final Button startButton = (Button) findViewById(R.id.start_button);
		startButton.setOnClickListener(new OnClickListener() {
			public void onClick(View src) {
				
				// Start the Orientation Monitoring Service using the Intent
				startService(wifiMonitoringServiceIntent);
			}
		});

		final Button stopButton = (Button) findViewById(R.id.stop_button);
		stopButton.setOnClickListener(new OnClickListener() {
			public void onClick(View src) {

				// Stop the Orientation Monitoring Service using the Intent
				stopService(wifiMonitoringServiceIntent);
			}
		});		
	}
}


