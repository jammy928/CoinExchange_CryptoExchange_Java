package com.bizzan.bitrade.controller.sdk;

import com.alibaba.fastjson.JSONObject;
import com.bizzan.bitrade.controller.sdk.utils.HttpClient4Utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 二次验证
 * Created by captcha_dev on 16-9-29.
 */
public class NECaptchaVerifier {
    public static final String VERIFY_API = "http://c.dun.163yun.com/api/v2/verify"; // verify接口地址
//    public static final String REQ_VALIDATE = "NECaptchaValidate"; // 二次验证带过来的validate

    private static final String VERSION = "v2";
    private String captchaId = ""; // 验证码id
    private NESecretPair secretPair = null; // 密钥对

    public NECaptchaVerifier(String captchaId, NESecretPair secretPair) {
        Validate.notBlank(captchaId, "captchaId为空");
        Validate.notNull(secretPair, "secret为null");
        Validate.notBlank(secretPair.secretId, "secretId为空");
        Validate.notBlank(secretPair.secretKey, "secretKey为空");
        this.captchaId = captchaId;
        this.secretPair = secretPair;
    }

    /**
     * 二次验证
     *
     * @param validate 验证码组件提交上来的NECaptchaValidate值
     * @param user     用户
     * @return
     */
    public boolean verify(String validate, String user) {
        if (StringUtils.isEmpty(validate) || StringUtils.equals(validate, "null")) {
            return false;
        }
        user = (user == null) ? "" : user; // bugfix:如果user为null会出现签名错误的问题
        Map<String, String> params = new HashMap<String, String>();
        params.put("captchaId", captchaId);
        params.put("validate", validate);
        params.put("user", user);
        // 公共参数
        params.put("secretId", secretPair.secretId);
        params.put("version", VERSION);
        params.put("timestamp", String.valueOf(System.currentTimeMillis()));
        params.put("nonce", String.valueOf(ThreadLocalRandom.current().nextInt()));
        // 计算请求参数签名信息
        String signature = sign(secretPair.secretKey, params);
        params.put("signature", signature);

        String resp = HttpClient4Utils.sendPost(VERIFY_API, params);
        System.out.println("resp = " + resp);
        return verifyRet(resp);
    }

    /**
     * 生成签名信息
     *
     * @param secretKey 验证码私钥
     * @param params    接口请求参数名和参数值map，不包括signature参数名
     * @return
     */
    public static String sign(String secretKey, Map<String, String> params) {
        String[] keys = params.keySet().toArray(new String[0]);
        Arrays.sort(keys);
        StringBuffer sb = new StringBuffer();
        for (String key : keys) {
            sb.append(key).append(params.get(key));
        }
        sb.append(secretKey);
        try {
            return DigestUtils.md5Hex(sb.toString().getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();// 一般编码都支持的。。
        }
        return null;
    }

    /**
     * 验证返回结果
     *
     * @param resp
     * @return
     */
    private boolean verifyRet(String resp) {
        if (StringUtils.isEmpty(resp)) {
            return false;
        }
        try {
            JSONObject j = JSONObject.parseObject(resp);
            return j.getBoolean("result");
        } catch (Exception e) {
            return false;
        }
    }
}
