package com.wingslab.intelligentwifi;



import com.wingslab.intelligentwifi.R;

import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class ViewLocationActivity extends ListActivity implements OnClickListener{

	
	@SuppressWarnings("unused")
	private static final String TAG = "ViewRulesActivity";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
      
		ContentResolver contentResolver = getContentResolver();

		ContentValues values = new ContentValues();

		// Create and set cursor and list adapter
		Cursor c = contentResolver.query(LocationContract.CONTENT_URI, null, null, null,
				null);

		setListAdapter(new SimpleCursorAdapter(this, R.layout.viewlocations, c,
				LocationContract.ALL_COLUMNS, new int[] { R.id.idString,
						R.id.data }, 0));

	}

	@Override
	public void onClick(View v) {
		Intent i = getIntent();
        i.putExtra("col", "data");
        setResult(RESULT_OK, i);
        finish();
		
	}
	protected void onListItemClick(ViewLocationActivity l, View v, int position, long id)
	{
		
	}
}