package com.example.idiomsandphrases;

import android.app.ActionBar;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.widget.TextView;

public class ActionBarProperties {
	
	public static void setupActionBar(Activity activity, ActionBar actionBar, String title, String subtitle, boolean displayHomeAsUpEnabled) {
		actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#fff75a00")));
		actionBar.setDisplayUseLogoEnabled(true);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(title);
		actionBar.setSubtitle(subtitle);
		actionBar.setDisplayShowHomeEnabled(true);
		int actionBarTitleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
		if (actionBarTitleId > 0) {
		    TextView titleView = (TextView) activity.findViewById(actionBarTitleId);
		    if (titleView != null) {
		    	titleView.setTextColor(Color.WHITE);
		    }
		}
		int actionBarSubTitleId = Resources.getSystem().getIdentifier("action_bar_subtitle", "id", "android");
		if (actionBarSubTitleId > 0) {
			TextView subtitleView = (TextView) activity.findViewById(actionBarSubTitleId);
			if (subtitleView != null) {
				subtitleView.setTextColor(Color.WHITE);
			}
		}
		if(Build.VERSION.SDK_INT >= 14) {
			actionBar.setIcon(new ColorDrawable(Color.TRANSPARENT));
		}
		actionBar.setDisplayHomeAsUpEnabled(displayHomeAsUpEnabled);
	}

}
