package com.mahkey.lc.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

public class UiUtils {

	/**
	 * 打开浏览器
	 * 
	 * @param activity
	 * @param url
	 */
	public static void startBrowse(Activity activity, String url) {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		Uri content_url = Uri.parse(url);
		intent.setData(content_url);
		activity.startActivity(intent);
	}
}
