package com.example.idiomsandphrases;


import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

public class AddLanguageActivity extends Activity{
	private RelativeLayout mainLayout;
	private RelativeLayout indicatorLayout;
	private EditText edit;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_ACTION_BAR);
		setContentView(R.layout.add_language);
		mainLayout = (RelativeLayout)findViewById(R.id.addlangparent);
		indicatorLayout = ActivityIndicator.getActivityIndicatorLayout(this);
		ActionBar actionBar = getActionBar();
		ActionBarProperties.setupActionBar(this, actionBar, "Add New Language", null, true);
		
		Button saveButton = (Button)findViewById(R.id.addlangsavebutton);
		Button cancelButton = (Button)findViewById(R.id.addlangcancelbutton);
		edit = (EditText)findViewById(R.id.addlangedit);
		
		saveButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mainLayout.addView(indicatorLayout);
				DataTask task = new DataTask();
				task.execute();
			}
		});
		
		cancelButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AddLanguageActivity.this.goBack();
			}
		});
	}
	
	private class DataTask extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... params) {
			IdiomsDataSource.getInstance(AddLanguageActivity.this).addLanguage(edit.getText().toString());
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			mainLayout.removeView(indicatorLayout);
			AddLanguageActivity.this.goBack();
		}
		
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {

	    int itemId = item.getItemId();
	    switch (itemId) {
	    case android.R.id.home:
	    	goBack();
	        break;

	    }
	    return true;
	}
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		return super.onTouchEvent(event);
	}
	
	
	private void goBack() {
		Intent localIntent = new Intent(this, MainPageActivity.class);
		startActivity(localIntent);
		finish();
	}
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
