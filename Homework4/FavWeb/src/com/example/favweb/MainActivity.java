package com.example.favweb;

import java.util.ArrayList;
import java.util.Collections;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends ListActivity {

	private static final String WEBSITE = "websites";
	
	private EditText urlEdit;
	private EditText tagEdit;
	private SharedPreferences savedWebs;
	private ArrayList<String> tags;
	private ArrayAdapter<String> adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		urlEdit = (EditText) findViewById(R.id.urledit);
		tagEdit = (EditText) findViewById(R.id.tagEdit);
		
		savedWebs = getSharedPreferences(WEBSITE, MODE_PRIVATE);
		
		tags = new ArrayList<String>(savedWebs.getAll().keySet());
		Collections.sort(tags, String.CASE_INSENSITIVE_ORDER);
		
		adapter = new ArrayAdapter<String>(this,R.layout.list_item,tags);
		setListAdapter(adapter);
		
		ImageButton saveButton = (ImageButton) findViewById(R.id.saveButton);
		saveButton.setOnClickListener(saveButtonListener);
		
		getListView().setOnItemClickListener(itemClickListener);  
	    getListView().setOnItemLongClickListener(itemLongClickListener);  
	}
	
	public OnClickListener saveButtonListener = new OnClickListener(){
		@Override
		public void onClick(View v){
			if (urlEdit.getText().length()>0 && tagEdit.getText().length()>0){
				addWebsite(urlEdit.getText().toString(),tagEdit.getText().toString());
				urlEdit.setText("");
				tagEdit.setText("");
				
				((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(tagEdit.getWindowToken(), 0);
			}
			else{
				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
				
				builder.setMessage(R.string.missingMessage);
				builder.setPositiveButton(R.string.OK, null);
				
				AlertDialog errorDialog = builder.create();
				errorDialog.show();
			}
		}
	};

	private void addWebsite(String url, String tag){
		SharedPreferences.Editor preferencesEditor = savedWebs.edit();
		preferencesEditor.putString(tag, url);
		preferencesEditor.apply();

		if(!tags.contains(tag)){
			tags.add(tag);
			Collections.sort(tags, String.CASE_INSENSITIVE_ORDER);
			adapter.notifyDataSetChanged();
		}
	}
	
	OnItemClickListener itemClickListener = new OnItemClickListener()
	{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
			String tag = ((TextView) view).getText().toString();
			String urlString = Uri.encode(savedWebs.getString(tag, ""));
			String messageShowing = tag+":\n\n"+urlString;
			AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
			
			builder.setMessage(messageShowing);
			builder.setPositiveButton(R.string.OK, null);
			
			AlertDialog errorDialog = builder.create();
			errorDialog.show();	
		}
	};
	
	OnItemLongClickListener itemLongClickListener = new OnItemLongClickListener(){

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			final String tag = ((TextView)view).getText().toString();
			
			AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
			
			builder.setTitle(getString(R.string.editDeleteTitle, tag));
			builder.setItems(R.array.dialog_items, new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which){
					switch (which){
					case 0:
						tagEdit.setText(tag);
						urlEdit.setText(savedWebs.getString(tag,""));
						break;
					case 1:
						deleteWeb(tag);
						break;
					}
				}
			}	
		);
		
			builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
					
				}
			}
		);
			builder.create().show();
			return true;
		}
		
	};
	
	private void deleteWeb(final String tag){
		AlertDialog.Builder confirmBuilder = new AlertDialog.Builder(this);
		
		confirmBuilder.setMessage(getString(R.string.confirmMessage, tag));
		
		confirmBuilder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			
				}
			}
		);
		
		confirmBuilder.setPositiveButton(getString(R.string.delete), new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int id){
				tags.remove(tag);
				
				SharedPreferences.Editor preferencesEditor = savedWebs.edit();
				preferencesEditor.remove(tag);
				preferencesEditor.apply();
				
				adapter.notifyDataSetChanged();
				}
			}
		);
		confirmBuilder.create().show();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
