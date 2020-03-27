package com.bizzan.bitrade.util;

import javax.servlet.http.HttpSession;

public class CaptchaUtil {
	private static final String prefix = "CAPTCHA_";
	
	public static boolean validate(HttpSession session, String pageId, String value){
		String captcha = (String) session.getAttribute(prefix+pageId);
		return captcha != null && captcha.equalsIgnoreCase(value);
	}
}
