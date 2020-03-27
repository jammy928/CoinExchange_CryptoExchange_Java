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
