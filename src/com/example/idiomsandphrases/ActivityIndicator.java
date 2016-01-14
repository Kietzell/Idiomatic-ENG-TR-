package com.example.idiomsandphrases;

import android.app.Activity;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class ActivityIndicator {

	public static RelativeLayout getActivityIndicatorLayout(Activity context) {
		RelativeLayout layout = (RelativeLayout)context.getLayoutInflater().inflate(R.layout.activity_indicator, null);
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		layoutParams.leftMargin = 0;
		layoutParams.topMargin = 0;
		layoutParams.bottomMargin = 0;
		layoutParams.rightMargin = 0;
		layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
		layout.setLayoutParams(layoutParams);
		return layout;
	}
}
