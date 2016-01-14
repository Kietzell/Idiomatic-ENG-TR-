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

public class AddIdiomActivity extends Activity{
	
	private String categoryName;
	private String language;
	private RelativeLayout mainLayout;
	private RelativeLayout indicatorLayout;
	private EditText editIdiom;
	private EditText editMeaning;
	private EditText editExample;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_ACTION_BAR);
		setContentView(R.layout.add_idiom);
		
		mainLayout = (RelativeLayout)findViewById(R.id.addidiomparent);
		indicatorLayout = ActivityIndicator.getActivityIndicatorLayout(this);

		this.language = getIntent().getExtras().getString("langname");
		categoryName = getIntent().getExtras().getString("categoryname");
		
		ActionBar actionBar = getActionBar();
		ActionBarProperties.setupActionBar(this, actionBar, "Add New Idiom", categoryName, true);
		
		Button saveButton = (Button)findViewById(R.id.addidiomsavebutton);
		Button cancelButton = (Button)findViewById(R.id.addidiomcancelbutton);
		editIdiom = (EditText)findViewById(R.id.addidiomedit);
		editMeaning = (EditText)findViewById(R.id.addidiommeaningedit);
		editExample = (EditText)findViewById(R.id.addidiomexampleedit);
		
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
				AddIdiomActivity.this.goBack();
			}
		});
	}
	
	private class DataTask extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... params) {
			String idiom = editIdiom.getText().toString();
			String meaning = "- "+editMeaning.getText().toString();
			String example = editExample.getText().toString();
			IdiomsDataSource.getInstance(AddIdiomActivity.this).addIdiom(language, categoryName, idiom, meaning, example);
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			mainLayout.removeView(indicatorLayout);
			AddIdiomActivity.this.goBack();
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
		return super.onTouchEvent(event);
	}
	
	
	private void goBack() {
		Intent localIntent = new Intent(this, IdiomsListActivity.class);
		localIntent.putExtra("langname", language);
		localIntent.putExtra("categoryname", categoryName);
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
