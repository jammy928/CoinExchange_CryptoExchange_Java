package com.bizzan.bc.wallet.util;

public class Convert {

    public static String fromAsciiToString(String data){
        if(data.length()%2 != 0)return "";
        StringBuffer chars = new StringBuffer();
        for(int i= 0;i<data.length();i+=2){
            char digit = (char) Integer.parseInt(data.substring(i,i+2),16);
            chars.append(digit);
        }
        return chars.toString();
    }

    public static void main(String[] args){
        String data = "31323334353637383930";
        System.out.println(fromAsciiToString(data));
    }
}
