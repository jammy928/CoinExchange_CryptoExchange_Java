package com.bizzan.bitrade.vendor.provider.support;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.bizzan.bitrade.util.MessageResult;
import com.bizzan.bitrade.vendor.provider.SMSProvider;
import com.bizzan.bitrade.vendor.provider.involve.SSLClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author GS
 * @date 2018年03月12日
 */
@Slf4j
@Data
@AllArgsConstructor
public class HuaXinSMSProvider implements SMSProvider {

    /**
     * 国内短信
     */
    private String gateway;
    private String username;
    private String password;
    /**
     * 国际短信
     */
    private String internationalGateway;
    private String internationalUsername;
    private String internationalPassword;

    private String sign;

    public static String getName() {
        return "huaxin";
    }

    @Override
    public MessageResult sendSingleMessage(String mobile, String content) throws Exception {
        org.apache.http.client.HttpClient httpclient = new SSLClient();
        HttpPost post = new HttpPost(gateway);
        post.setHeader("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        List<org.apache.http.NameValuePair> nvps = new ArrayList<org.apache.http.NameValuePair>();
        nvps.add(new BasicNameValuePair("action", "send"));
        nvps.add(new BasicNameValuePair("userid", ""));
        nvps.add(new BasicNameValuePair("account", username));
        nvps.add(new BasicNameValuePair("password", password));
        //多个手机号用逗号分隔
        nvps.add(new BasicNameValuePair("mobile", mobile));
        nvps.add(new BasicNameValuePair("content", content));
        nvps.add(new BasicNameValuePair("sendTime", ""));
        nvps.add(new BasicNameValuePair("extno", ""));
        post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
        HttpResponse response = httpclient.execute(post);
        System.out.println(response.getStatusLine());
        HttpEntity entity = response.getEntity();
        // 将字符转化为XML
        String returnString = EntityUtils.toString(entity, "UTF-8");
        Document doc = DocumentHelper.parseText(returnString);
        // 获取根节点
        Element rootElt = doc.getRootElement();
        // 获取根节点下的子节点的值
        String returnstatus = rootElt.elementText("returnstatus").trim();
        String message = rootElt.elementText("message").trim();
        String remainpoint = rootElt.elementText("remainpoint").trim();
        String taskID = rootElt.elementText("taskID").trim();
        String successCounts = rootElt.elementText("successCounts").trim();
        log.info(" mobile : " + mobile + "content : " + content);
        System.out.println(returnString);
        System.out.println("返回状态为：" + returnstatus);
        System.out.println("返回信息提示：" + message);
        System.out.println("返回余额：" + remainpoint);
        System.out.println("返回任务批次：" + taskID);
        System.out.println("返回成功条数：" + successCounts);
        EntityUtils.consume(entity);
        MessageResult messageResult = MessageResult.success(message);
        if (!"Success".equals(returnstatus)) {
            messageResult.setCode(500);
        }
        return messageResult;
    }

    @Override
    public MessageResult sendMessageByTempId(String mobile, String content, String templateId) throws Exception {
        return null;
    }

    @Override
    public MessageResult sendVerifyMessage(String mobile, String verifyCode) throws Exception {
        String content = formatVerifyCode(verifyCode);
        return sendSingleMessage(mobile, content);
    }

    @Override
    public String formatVerifyCode(String code) {
        return String.format("【%s】验证码：%s，10分钟内有效，请勿告诉他人。", sign, code);
    }

    @Override
    public MessageResult sendInternationalMessage(String content, String phone) throws IOException, DocumentException {
        content=String.format("[%s]Verification Code:%s.If you do not send it, please ignore this message.", sign,content);
        HttpClient client = new HttpClient();
        PostMethod method = new PostMethod(internationalGateway);
        String result = encodeHexStr(8, content);
        client.getParams().setContentCharset("UTF-8");
        method.setRequestHeader("ContentType",
                "application/x-www-form-urlencoded;charset=UTF-8");
        NameValuePair[] data = {new NameValuePair("action", "send"),
                new NameValuePair("userid", ""),
                new NameValuePair("account", internationalUsername),
                new NameValuePair("password", internationalPassword),
                new NameValuePair("mobile", phone),
                //发英文 用  0    其他中日韩 用  8
                new NameValuePair("code", "8"),
                new NameValuePair("content", result),
                new NameValuePair("sendTime", ""),
                new NameValuePair("extno", ""),};
        method.setRequestBody(data);
        client.executeMethod(method);
        String response = method.getResponseBodyAsString();
        Document doc = DocumentHelper.parseText(response);
        // 获取根节点
        Element rootElt = doc.getRootElement();
        // 获取根节点下的子节点的值
        String returnstatus = rootElt.elementText("returnstatus").trim();
        String message = rootElt.elementText("message").trim();
        String remainpoint = rootElt.elementText("balance").trim();
        String taskID = rootElt.elementText("taskID").trim();
        String successCounts = rootElt.elementText("successCounts").trim();

        System.out.println(response);
        System.out.println("返回状态为：" + returnstatus);
        System.out.println("返回信息提示：" + message);
        System.out.println("返回余额：" + remainpoint);
        System.out.println("返回任务批次：" + taskID);
        System.out.println("返回成功条数：" + successCounts);

        MessageResult messageResult = MessageResult.success();
        if (!"Success".equals(returnstatus)) {
            messageResult.setCode(500);
        }
        messageResult.setMessage(message);
        return messageResult;
    }

    // 字符编码成HEX
    public static String encodeHexStr(int dataCoding, String realStr) {
        String strhex = "";
        try {
            byte[] bytSource = null;
            if (dataCoding == 15) {
                bytSource = realStr.getBytes("GBK");
            } else if (dataCoding == 3) {
                bytSource = realStr.getBytes("ISO-8859-1");
            } else if (dataCoding == 8) {
                bytSource = realStr.getBytes("UTF-16BE");
            } else {
                bytSource = realStr.getBytes("ASCII");
            }
            strhex = bytesToHexString(bytSource);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return strhex;
    }

    /** */
    /**
     * 把字节数组转换成16进制字符串
     *
     * @param bArray
     * @return
     */
    public static final String bytesToHexString(byte[] bArray) {
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2) {
                sb.append("0");
            }
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }

	@Override
	public MessageResult sendCustomMessage(String mobile, String content) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}


}
