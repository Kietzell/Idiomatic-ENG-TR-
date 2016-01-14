package com.example.idiomsandphrases;

import java.util.Arrays;
import java.util.Vector;





import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class IdiomsCategoryListActivity extends Activity implements
		AdapterView.OnItemClickListener, TextWatcher{
	private static final String PREFS_NAME = "idioms";
	private static final String PREF_KEY = "shownever";
	private static final String PREF_KEY_REFER = "shownever_refer";
	public static int adDispayCount = 0;
	public static int adSkipCount = 0;
	private ArrayAdapter<String> adaptor;
	private boolean exitProcess = false;
	private Intent pendingIntent = null;
	private long languageId = 0;
	private Vector<Long> categoryIds = new Vector<Long>();
	private String langName;
	private RelativeLayout mainLayout;
	private RelativeLayout indicatorLayout;

	private void launchPendingIntent() {
		Intent localIntent = this.pendingIntent;
		this.pendingIntent = null;
		if (localIntent != null) {
			startActivity(localIntent);
		}
	}


	public void afterTextChanged(Editable paramEditable) {
		this.adaptor.getFilter().filter(paramEditable.toString().trim());
	}

	public void beforeTextChanged(CharSequence paramCharSequence,
			int paramInt1, int paramInt2, int paramInt3) {
	}



	public void onCreate(Bundle paramBundle) {
		
		
		super.onCreate(paramBundle);
		
		requestWindowFeature(Window.FEATURE_ACTION_BAR);
		setContentView(R.layout.idiomcategorieslist);
		
		mainLayout = (RelativeLayout)findViewById(R.id.catparent);
		indicatorLayout = ActivityIndicator.getActivityIndicatorLayout(this);
		
		langName = getIntent().getExtras().getString("langname");
		
		ActionBar actionBar = getActionBar();
		ActionBarProperties.setupActionBar(this, actionBar, "Idioms & Phrases", langName, true);
		
		ListView localListView = (ListView) findViewById(R.id.idiomcategories);
		this.adaptor = new IdiomsCategoryListAdaptor(this, R.id.categoryname,
				IdiomsDataSource.getInstance(this).getIdiomCategoryNames(langName));
		localListView.setAdapter(this.adaptor);
		int[] arrayOfInt = { 0xFF000000, 0xFF000000, 0xFF000000 };
		localListView.setDivider(new GradientDrawable(
				GradientDrawable.Orientation.RIGHT_LEFT, arrayOfInt));
		localListView.setDividerHeight(1);
		localListView.setOnItemClickListener(this);
		localListView.requestFocusFromTouch();
		((EditText) findViewById(R.id.search)).addTextChangedListener(this);
		
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.clear();
		MenuItem item1 = menu.add("Add New Category");
		item1.setIcon(R.drawable.add_icon);
		item1.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		item1.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Intent localIntent = new Intent(IdiomsCategoryListActivity.this, AddCategoryActivity.class);
				localIntent.putExtra("langname", langName);
				IdiomsCategoryListActivity.this.pendingIntent = localIntent;
				IdiomsCategoryListActivity.this.launchPendingIntent();
				IdiomsCategoryListActivity.this.finish();
				return true;
			}
		});
		MenuItem item2 = menu.add("Delete "+langName);
		item2.setIcon(R.drawable.delete_icon);
		item2.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		item2.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						IdiomsCategoryListActivity.this);
				alertDialogBuilder.setTitle("Delete Language");

				// set dialog message
				alertDialogBuilder
						.setMessage("Are you sure to delete " + langName)
						.setCancelable(false)
						.setPositiveButton("Yes",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										DataTask task = new DataTask();
										task.execute();
									}
								})
						.setNegativeButton("No",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										dialog.cancel();
									}
								});
				AlertDialog alertDialog = alertDialogBuilder.create();

				alertDialog.show();

				return true;
			}
		});
		return super.onCreateOptionsMenu(menu);
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
			IdiomsDataSource.getInstance(IdiomsCategoryListActivity.this).deleteLanguage(langName);
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			mainLayout.removeView(indicatorLayout);
			IdiomsCategoryListActivity.this.goBack();
		}
		
	}

	private void goBack() {
		Intent localIntent = new Intent(IdiomsCategoryListActivity.this, MainPageActivity.class);
		pendingIntent = localIntent;
		launchPendingIntent();
		finish();
	}

	public void onItemClick(AdapterView<?> paramAdapterView, View paramView,
			int paramInt, long paramLong) {
		Intent localIntent = new Intent(this, IdiomsListActivity.class);
		localIntent.putExtra("langname", langName);
		localIntent.putExtra("categoryname", this.adaptor.getItem(paramInt));
		this.pendingIntent = localIntent;
		launchPendingIntent();
		finish();
	}


	protected void onResume() {
		super.onResume();
	}

	public void onTextChanged(CharSequence paramCharSequence, int paramInt1,
			int paramInt2, int paramInt3) {
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
