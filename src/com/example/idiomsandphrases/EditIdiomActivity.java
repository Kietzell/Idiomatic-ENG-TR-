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

public class EditIdiomActivity extends Activity{
	
	private String categoryName;
	private String language;
	private String idiom;
	private String meaning;
	private String example;
	private int index;
	private EditText editIdiom;
	private EditText editMeaning;
	private EditText editExample;
	private RelativeLayout mainLayout;
	private RelativeLayout indicatorLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_ACTION_BAR);
		setContentView(R.layout.edit_idiom);
		
		mainLayout = (RelativeLayout)findViewById(R.id.editidiomparent);
		indicatorLayout = ActivityIndicator.getActivityIndicatorLayout(this);

		language = getIntent().getExtras().getString("langname");
		categoryName = getIntent().getExtras().getString("categoryname");
		idiom = getIntent().getExtras().getString("idiomname");
		meaning = getIntent().getExtras().getString("idiommeaning");
		example = getIntent().getExtras().getString("idiomexample");
		index = getIntent().getExtras().getInt("index");
		
		ActionBar actionBar = getActionBar();
		ActionBarProperties.setupActionBar(this, actionBar, "Edit Idiom", idiom, true);
		
		Button saveButton = (Button)findViewById(R.id.editidiomsavebutton);
		Button cancelButton = (Button)findViewById(R.id.editidiomcancelbutton);
		editIdiom = (EditText)findViewById(R.id.editidiomedit);
		editMeaning = (EditText)findViewById(R.id.editidiommeaningedit);
		editExample = (EditText)findViewById(R.id.editidiomexampleedit);
		meaning = meaning.substring(2);
		editIdiom.setText(idiom);
		editMeaning.setText(meaning);
		editExample.setText(example);
		
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
				EditIdiomActivity.this.goBack();
			}
		});
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
	
	private class DataTask extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... params) {
			String idiomName = editIdiom.getText().toString();
			String idiomMeaning = "- "+editMeaning.getText().toString();
			String idiomExample = editExample.getText().toString();
			index = IdiomsDataSource.getInstance(EditIdiomActivity.this).editIdiom(idiom, language, categoryName, idiomName, idiomMeaning, idiomExample);
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			mainLayout.removeView(indicatorLayout);
			EditIdiomActivity.this.goBack();
		}
		
	}
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event);
	}
	
	
	private void goBack() {
		Intent localIntent = new Intent(this, IdiomsDisplayerActivity.class);
		localIntent.putExtra("langname", language);
		localIntent.putExtra("categoryname", categoryName);
		localIntent.putExtra("index", index);
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