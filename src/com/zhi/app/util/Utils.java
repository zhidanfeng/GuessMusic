package com.zhi.app.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

public class Utils {

	public static View getView(Context context, int layoutId) {
		LayoutInflater inflater = (LayoutInflater)context.
				getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		View layout = inflater.inflate(layoutId, null);
		
		return layout;
	}
}
