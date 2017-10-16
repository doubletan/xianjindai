package com.xinhe.cashloan.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class DeviceUtil {
	/**
	 * @brief 判断手机当前网络开关状态 wifi gprs
	 * @param context
	 *            上下文
	 * @return boolean
	 */
	public static boolean IsNetWork(Context context) {
		// 判断手机当前网络开关状态 wifi gprs
		boolean isnetwork = false;
		try {
			ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (cm == null) {
				isnetwork = false;
			} else {
				NetworkInfo[] info = cm.getAllNetworkInfo();
				if (info != null) {
					for (int i = 0; i < info.length; i++) {
						if (info[i].getState() == NetworkInfo.State.CONNECTED) {
							return true;
						}
					}
				}
			}
		} catch (Exception e) {
			ExceptionUtil.handleException(e);
		}
		return isnetwork;
	}

	/**
	 * 判断wifi连接状态
	 *
	 * @param ctx
	 * @return
	 */
	public static boolean isWifiAvailable(Context ctx) {
		ConnectivityManager conMan = (ConnectivityManager) ctx
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo.State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.getState();
		if (NetworkInfo.State.CONNECTED == wifi) {
			return true;
		} else {
			return false;
		}
	}
}
