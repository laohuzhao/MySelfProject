package com.sdk4.biz.aote.util;

import java.util.regex.Pattern;

/**
 * 常用工具类
 * 
 * @author sh
 *
 */
public class Tools {

	static String HEX4_PATTERN = "[a-f0-9A-F]{4}";

	public static boolean testHex4(String str) {
		return Pattern.matches(HEX4_PATTERN, str);
	}

}
