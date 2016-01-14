package com.example.idiomsandphrases;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;

public class SplashActivity extends Activity{
	
	long timeStarted = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(1);
		setContentView(R.layout.splash);
		
		timeStarted = System.currentTimeMillis();
		SetupDatabaseTask task = new SetupDatabaseTask();
		task.execute();
		
		// Cloud baðlantýsý için Parse instance'ý Parse'ýn verdiði keylerle ilklendiriliyor.
		Parse.initialize(this, "CRqjq1cIuOABmwckmaIasI0YsKZEIIViBEQAlrVi", "2gVSQutCFY0V53xGjlZLf3DYFlyQvmyJKJkNg9kz");
		
	}
	
	
	private class SetupDatabaseTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			IdiomsDataSource dataSource = new IdiomsDataSource(SplashActivity.this);
			dataSource.open();
			SQLiteDatabase database = dataSource.getDatabase();
			// Internet baðlantýsý varsa cloud'daki veriler locale çekiliyor.
			if(IdiomsNetworkManager.hasActiveInternetConnection(SplashActivity.this)){
				SyncWithCloudDatabase.setUpDatabase(database);
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			long now = System.currentTimeMillis();
			if(now - SplashActivity.this.timeStarted < 1000) {
				new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						SplashActivity.this.startNextActivity();
					}
				}, 1000);
			} else {
				SplashActivity.this.startNextActivity();
			}
		}
		
	}
	
	private void startNextActivity() {
		Intent localIntent = new Intent(this, MainPageActivity.class);
		startActivity(localIntent);
		finish();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

}
