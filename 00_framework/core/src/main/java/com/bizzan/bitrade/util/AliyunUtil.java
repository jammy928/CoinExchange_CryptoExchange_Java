package com.bizzan.bitrade.util;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONObject;
import sun.misc.BASE64Encoder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.util.Date;

public class AliyunUtil {


    /*
    * 计算MD5+BASE64
    */
    public static String MD5Base64(String s) {
        if (s == null) {
            return null;
        }
        String encodeStr = "";
        byte[] utfBytes = s.getBytes();
        MessageDigest mdTemp;
        try {
            mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(utfBytes);
            byte[] md5Bytes = mdTemp.digest();
            BASE64Encoder b64Encoder = new BASE64Encoder();
            encodeStr = b64Encoder.encode(md5Bytes);
        } catch (Exception e) {
            throw new Error("Failed to generate MD5 : " + e.getMessage());
        }
        return encodeStr;
    }


    /*
     * 计算 HMAC-SHA1
     */
    public static String HMACSha1(String data, String key) {
        String result;
        try {
            SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);
            byte[] rawHmac = mac.doFinal(data.getBytes());
            result = (new BASE64Encoder()).encode(rawHmac);
        } catch (Exception e) {
            throw new Error("Failed to generate HMAC : " + e.getMessage());
        }
        return result;
    }

    public static JSONObject doPost(String url,String body,String accessId,String accessKey) throws MalformedURLException, UnirestException {
        String method = "POST";
        String accept = "application/json";
        String content_type = "application/json";
        String path = new URL(url).getFile();
        String date = DateUtil.toGMTString(new Date());
        // 1.对body做MD5+BASE64加密
        String bodyMd5 = MD5Base64(body);
        String stringToSign = method + "\n" + accept + "\n" + bodyMd5 + "\n" + content_type + "\n" + date + "\n"
                + path;
        // 2.计算 HMAC-SHA1
        String signature = HMACSha1(stringToSign, accessKey);
        // 3.得到 authorization header
        String authHeader = "Dataplus " + accessId + ":" + signature;

        HttpResponse<JsonNode> resp =  Unirest.post(url)
                .header("accept",accept)
                .header("content-type",content_type)
                .header("date",date)
                .header("Authorization",authHeader)
                .body(body)
                .asJson();
        JSONObject json = resp.getBody().getObject();
        return json;
    }
}
