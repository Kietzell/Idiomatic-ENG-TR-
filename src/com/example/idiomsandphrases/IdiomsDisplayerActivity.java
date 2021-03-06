package com.example.idiomsandphrases;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.PrintStream;
import java.util.List;
import java.util.Random;


public class IdiomsDisplayerActivity extends Activity {
	public static final String KEY_CATEGORY_NAME = "catname";
	public static final String KEY_INDEX = "index";
	private SherPagerAdapter idiomAdapter;
	private List<IdiomPreview> idiomList = null;
	private ViewPager idiomPager;
	private boolean intentStarted = false;
	private TextView page = null;
	private int pageChanges = 0;
	private long languageId = 0;
	private long categoryId = 0;
	private long idiomId = 0;
	private String language;
	private String category;
	private Intent pendingIntent;

	public void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		requestWindowFeature(Window.FEATURE_ACTION_BAR);
		setContentView(R.layout.detaillayout);
		this.language = getIntent().getExtras().getString("langname");
		this.category = getIntent().getExtras().getString("categoryname");
		
		ActionBar actionBar = getActionBar();
		ActionBarProperties.setupActionBar(this, actionBar, "Idiom, Meaning & Ex.", category, true);
		
		this.idiomList = IdiomsDataSource.getInstance(this).getIdiomsForCategory(language, category);
		this.page = ((TextView) findViewById(R.id.page));
		this.idiomAdapter = new SherPagerAdapter();
		this.idiomPager = ((ViewPager) findViewById(R.id.awesomepager));
		this.idiomPager.setAdapter(this.idiomAdapter);
		this.page.setText("1/" + this.idiomAdapter.getCount());
		this.idiomPager
				.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
					public void onPageScrollStateChanged(int paramAnonymousInt) {
					}

					public void onPageScrolled(int paramAnonymousInt1,
							float paramAnonymousFloat, int paramAnonymousInt2) {
					}

					public void onPageSelected(int paramAnonymousInt) {
						IdiomsDisplayerActivity.this.page
								.setText(paramAnonymousInt
										+ 1
										+ "/"
										+ IdiomsDisplayerActivity.this.idiomAdapter
												.getCount());
						IdiomsDisplayerActivity localIdiomsDisplayerActivity = IdiomsDisplayerActivity.this;
						localIdiomsDisplayerActivity.pageChanges = (1 + localIdiomsDisplayerActivity.pageChanges);
					}
				});
		final Random localRandom = new Random();
		int i = getIntent().getExtras().getInt("index");
		this.idiomPager.setCurrentItem(i, true);
		((ImageView) findViewById(R.id.previcon))
				.setOnClickListener(new View.OnClickListener() {
					public void onClick(View paramAnonymousView) {
						if (IdiomsDisplayerActivity.this.idiomPager
								.getCurrentItem() > 0) {
							IdiomsDisplayerActivity.this.idiomPager
									.setCurrentItem(
											-1
													+ IdiomsDisplayerActivity.this.idiomPager
															.getCurrentItem(),
											true);
						}
					}
				});
		((ImageView) findViewById(R.id.nexticon))
				.setOnClickListener(new View.OnClickListener() {
					public void onClick(View paramAnonymousView) {
						if (IdiomsDisplayerActivity.this.idiomPager
								.getCurrentItem() < -1
								+ IdiomsDisplayerActivity.this.idiomAdapter
										.getCount()) {
							IdiomsDisplayerActivity.this.idiomPager
									.setCurrentItem(
											1 + IdiomsDisplayerActivity.this.idiomPager
													.getCurrentItem(), true);
						}
					}
				});
		((ImageView) findViewById(R.id.randicon))
				.setOnClickListener(new View.OnClickListener() {
					public void onClick(View paramAnonymousView) {
						int i = localRandom
								.nextInt(IdiomsDisplayerActivity.this.idiomAdapter
										.getCount());
						IdiomsDisplayerActivity.this.idiomPager.setCurrentItem(
								i, true);
					}
				});
	}
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.clear();
		MenuItem item1 = menu.add("Edit This Idiom");
		item1.setIcon(R.drawable.edit_icon);
		item1.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		item1.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Intent localIntent = new Intent(IdiomsDisplayerActivity.this, EditIdiomActivity.class);
				localIntent.putExtra("langname", language);
				localIntent.putExtra("categoryname", category);
				int currentIndex = idiomPager.getCurrentItem();
				IdiomPreview idiomPreview = idiomList.get(currentIndex);
				localIntent.putExtra("index", currentIndex);
				localIntent.putExtra("idiomname", idiomPreview.getIdiomName());
				localIntent.putExtra("idiommeaning", idiomPreview.getMeaning());
				localIntent.putExtra("idiomexample", idiomPreview.getSample());
				IdiomsDisplayerActivity.this.pendingIntent = localIntent;
				IdiomsDisplayerActivity.this.launchPendingIntent();
				IdiomsDisplayerActivity.this.finish();
				return true;
			}
		});
		MenuItem item2 = menu.add("Delete This Idiom");
		item2.setIcon(R.drawable.delete_icon);
		item2.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		item2.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						IdiomsDisplayerActivity.this);
				alertDialogBuilder.setTitle("Delete Idiom");

				int currentIndex = idiomPager.getCurrentItem();
				// set dialog message
				alertDialogBuilder
						.setMessage("Are you sure to delete " + idiomList.get(currentIndex).getIdiomName())
						.setCancelable(false)
						.setPositiveButton("Yes",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										DataTask task = new DataTask();
										task.execute();
										IdiomsDisplayerActivity.this.goBack();
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
	private class DataTask extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... params) {
			int currentIndex = idiomPager.getCurrentItem();
			IdiomsDataSource.getInstance(IdiomsDisplayerActivity.this).deleteIdiom(language, category, idiomList.get(currentIndex).getIdiomName());
			return null;
		}
		
	}
	
	private void goBack() {
		Intent localIntent = new Intent(IdiomsDisplayerActivity.this, IdiomsListActivity.class);
		localIntent.putExtra("langname", language);
		localIntent.putExtra("categoryname", category);
		pendingIntent = localIntent;
		launchPendingIntent();
		finish();
	}
	
	private void launchPendingIntent() {
		Intent localIntent = this.pendingIntent;
		this.pendingIntent = null;
		if (localIntent != null) {
			startActivity(localIntent);
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
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private class SherPagerAdapter extends PagerAdapter {
		private SherPagerAdapter() {
		}

		private String getExample(int paramInt) {
			return ((IdiomPreview) IdiomsDisplayerActivity.this.idiomList
					.get(paramInt)).getSample();
		}

		private String getIdiom(int paramInt) {
			return ((IdiomPreview) IdiomsDisplayerActivity.this.idiomList
					.get(paramInt)).getIdiomName();
		}

		private String getMeaning(int paramInt) {
			return ((IdiomPreview) IdiomsDisplayerActivity.this.idiomList
					.get(paramInt)).getMeaning();
		}

		public void destroyItem(View paramView, int paramInt, Object paramObject) {
			((ViewPager) paramView).removeView((LinearLayout) paramObject);
		}

		public void finishUpdate(View paramView) {
		}

		public int getCount() {
			return IdiomsDisplayerActivity.this.idiomList.size();
		}

		public Object instantiateItem(View paramView, int paramInt) {
			LinearLayout localLinearLayout = (LinearLayout) LayoutInflater
					.from(IdiomsDisplayerActivity.this).inflate(2130903045,
							null);
			((TextView) localLinearLayout.findViewById(R.id.idiomname))
					.setText(getIdiom(paramInt));
			((TextView) localLinearLayout.findViewById(R.id.idiommeaning))
					.setText(getMeaning(paramInt));
			((TextView) localLinearLayout.findViewById(R.id.idiomexample))
					.setText(getExample(paramInt));
			((ViewPager) paramView).addView(localLinearLayout, 0);
			return localLinearLayout;
		}

		public boolean isViewFromObject(View paramView, Object paramObject) {
			return paramView == (LinearLayout) paramObject;
		}

		public void restoreState(Parcelable paramParcelable,
				ClassLoader paramClassLoader) {
		}

		public Parcelable saveState() {
			return null;
		}

		public void startUpdate(View paramView) {
		}
	}
}
