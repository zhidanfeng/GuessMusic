package com.zhi.app.util;

import java.io.UnsupportedEncodingException;
import java.util.Random;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

public class Utils {

	/**
	 * 获取View
	 * @param context
	 * @param layoutId
	 * @return
	 */
	public static View getView(Context context, int layoutId) {
		LayoutInflater inflater = (LayoutInflater)context.
				getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		View layout = inflater.inflate(layoutId, null);
		
		return layout;
	}
	
	/**
	 * 随机生成汉字
	 * @return
	 */
	public static String generateRandomWord() {
		String word = "";
		int hightPos =0, lowPos = 0;
		Random random = new Random();
		
		// 获取高位值，高位字节的范围0xB0 - 0xF7，而0xB0就是176
		// 之所以设置在0-39之间取值是因为这个范围得出的汉字比较正常，不会出现生僻字
		hightPos = 176 + Math.abs(random.nextInt(39));
		// 获取低位值,低位字节的范围是0xA1 - 0xFE，而0xA1就是161
		lowPos = (161 + Math.abs(random.nextInt(93)));
		
		// 定义一个byte数据，用于存放汉字。PS：汉字占两个字节
		byte[] bytes = new byte[2];
		
		bytes[0] = Integer.valueOf(hightPos).byteValue();
		bytes[1] = Integer.valueOf(lowPos).byteValue();
		
		try {
			word = new String(bytes, "GBK");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return word;
	}
}
