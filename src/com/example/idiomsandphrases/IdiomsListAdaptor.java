package com.example.idiomsandphrases;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

public class IdiomsListAdaptor extends ArrayAdapter<IdiomPreview> {

	public IdiomsListAdaptor(Context paramContext, int paramInt,
			List<IdiomPreview> paramList) {
		super(paramContext, paramInt, paramList);
	}

	public View getView(int paramInt, View paramView, ViewGroup paramViewGroup) {
		View localView = LayoutInflater.from(getContext()).inflate(
				R.layout.idiomlistitem, null);
		((TextView) localView.findViewById(R.id.idiomname)).setText("\""
				+ ((IdiomPreview) getItem(paramInt)).getIdiomName() + "\"");
		((TextView) localView.findViewById(R.id.idiommeaning))
				.setText(((IdiomPreview) getItem(paramInt)).getMeaning());
		return localView;

	}
}
