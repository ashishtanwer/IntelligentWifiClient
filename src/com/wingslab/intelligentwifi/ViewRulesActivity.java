package com.wingslab.intelligentwifi;



import com.wingslab.intelligentwifi.R;

import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.SimpleCursorAdapter;

public class ViewRulesActivity extends ListActivity {

	
	@SuppressWarnings("unused")
	private static final String TAG = "AddAccelerationRulesActivity";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
      
		ContentResolver contentResolver = getContentResolver();

		ContentValues values = new ContentValues();

		// Create and set cursor and list adapter
		Cursor c = contentResolver.query(RulesContract.CONTENT_URI, null, null, null,
				null);

		setListAdapter(new SimpleCursorAdapter(this, R.layout.viewrules, c,
				RulesContract.ALL_COLUMNS, new int[] { R.id.idString,
						R.id.data }, 0));

	}
}