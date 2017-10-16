package com.xinhe.cashloan.util;


import com.xinhe.cashloan.myapp.MyApplication;

/**
 * 异常统一处理
 *
 * @author tarena
 *
 */
public class ExceptionUtil {
	public static void handleException(Exception e) {
		if (MyApplication.isRelease) {
			return;
		} else {
			e.printStackTrace();
		}
	}

}
