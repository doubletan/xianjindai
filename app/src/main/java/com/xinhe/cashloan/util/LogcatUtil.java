package com.xinhe.cashloan.util;


import android.util.Log;

import com.xinhe.cashloan.myapp.MyApplication;


public class LogcatUtil {

	public static void printLogcat(String log) {
		if (MyApplication.isRelease) {
			return;
		} else {
			Log.i("手机借款", log);
		}
	}
}
