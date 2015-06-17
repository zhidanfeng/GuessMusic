package com.zhi.app.util;

import android.util.Log;

/**
 * <b>自定义日志类</b><br/>
 * 		可以自由选择是否打印日志
 * @author zhidf
 *
 */
public class MyLog {
	
	// 是否打印日志开关
	private static final boolean DEBUG = true;
	
	/**
	 * 打印debug级别日志
	 * @param tag
	 * @param message
	 */
	public static void d(String tag, String message) {
		if(DEBUG) {
			Log.d(tag, message);
		}
	}
	
	/**
	 * 打印warn级别日志
	 * @param tag
	 * @param message
	 */
	public static void w(String tag, String message) {
		if(DEBUG) {
			Log.w(tag, message);
		}
	}
	
	/**
	 * 打印error级别日志
	 * @param tag
	 * @param message
	 */
	public static void e(String tag, String message) {
		if(DEBUG) {
			Log.e(tag, message);
		}
	}
	
	/**
	 * 打印info级别日志
	 * @param tag
	 * @param message
	 */
	public static void i(String tag, String message) {
		if(DEBUG) {
			Log.i(tag, message);
		}
	}
}
