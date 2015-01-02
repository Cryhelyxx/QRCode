package org.gditc.qrcode.utils;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

public class Util {
	
	

	public static void goTo(Context context, String url) {
		Uri uriUrl = Uri.parse(url);
		Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl); 
		context.startActivity(launchBrowser);
	}
	
	public static void goToGitHub(Context context) {
		String url = "http://github.com/jfeinstein10/slidingmenu";
		goTo(context, url);
	}
	
	/**
	 * 跳转到南网
	 * @param context
	 */
	public static void goToCSG(Context context) {
		String url = "http://www.csg.cn/gynw/";
		goTo(context, url);
	}
	
	/**
	 * 判断是否有可访问的网络
	 * @return
	 */
	public static boolean isNetworkConnected(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (null != ni) {
			return ni.isAvailable();
		} else {
			return false;
		}
	}
	
}
