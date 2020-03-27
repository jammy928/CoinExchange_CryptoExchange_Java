package com.bizzan.bitrade.util;

import java.util.Date;
import java.util.Random;
import java.util.UUID;

public class GeneratorUtil {
    /**
     * 得到from到to的随机数，包括to
     * @param from
     * @param to
     * @return
     */
    public static int getRandomNumber(int from, int to) {
		float a = from + (to - from) * (new Random().nextFloat());
		int b = (int) a;
		return ((a - b) > 0.5 ? 1 : 0) + b;
	}
    /**
     * 根据用户ID获取推荐码
     * @param uid
     * @return
     */
    public static String getPromotionCode(Long uid) {
    	String seed = "E5FCDG3HQA4B1NOPIJ2RSTUV67MWX89KLYZ";
    	long num = uid + 10000;
    	long mod = 0;
    	StringBuffer code = new StringBuffer();
    	while(num > 0) {
    		mod = num % 35;
    		num = (num - mod) / 35;
    		code.insert(0, seed.charAt(Integer.parseInt(String.valueOf(mod))));
    	}
    	while(code.length() < 4) {
    		code.insert(0, "0");
    	}
		return code.toString();
    }
    public static String getNonceString(int len){
    	String seed = "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
		StringBuffer tmp = new StringBuffer();
		for (int i = 0; i < len; i++) {
			tmp.append(seed.charAt(getRandomNumber(0,61)));
		}
		return tmp.toString();
    }
    
    public static String getUUID(){
    	UUID uuid = UUID.randomUUID();
    	return uuid.toString();
    }
    
    public static String getOrderId(String prefix){
    	String body = String.valueOf(System.currentTimeMillis());
    	return prefix + body + getRandomNumber(10,99);
    }
}