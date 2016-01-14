package com.example.idiomsandphrases;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class IdiomsCategoryListAdaptor extends ArrayAdapter<String> {

	public IdiomsCategoryListAdaptor(Context paramContext, int paramInt,
			String[] paramArrayOfString) {
		super(paramContext, paramInt, paramArrayOfString);
	}

	public View getView(int paramInt, View paramView, ViewGroup paramViewGroup) {
		View localView = LayoutInflater.from(getContext()).inflate(R.layout.idiomcategoryitem,
				null);
		localView.setBackgroundColor(Color.parseColor("#ecf0f1"));
		if (paramInt % 2 == 0) {
			localView.setBackgroundColor(Color.parseColor("#9bc438"));
		}
		((TextView) localView.findViewById(R.id.categoryname)).setText((String) getItem(paramInt));
		((TextView) localView.findViewById(R.id.categoryitemcount)).setVisibility(View.GONE);
		return localView;
	}
}
