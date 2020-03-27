package com.bizzan.bitrade.vendor.provider.support;


import com.alibaba.fastjson.JSONObject;
import com.bizzan.bitrade.dto.SmsDTO;
import com.bizzan.bitrade.service.SmsService;
import com.bizzan.bitrade.util.MessageResult;
import com.bizzan.bitrade.vendor.provider.SMSProvider;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URLEncoder;
import java.util.List;


/**
 * 创瑞短信接口实现类
 */
@Slf4j
public class ChuangRuiSMSProvider implements SMSProvider {

    private String gateway;
    private String username;
    private String password;
    private String sign;
    private String accesskey;
    private String accessSecret;
    
    @Autowired
    private SmsService smsService;

    public ChuangRuiSMSProvider(String gateway, String username, String password, String sign, String accesskey, String accessSecret) {
        this.gateway = gateway;
        this.username = username;
        this.password = password;
        this.sign = sign;
        this.accesskey = accesskey;
        this.accessSecret = accessSecret;
    }

    public static String getName() {
        return "chuangrui";
    }

//    @Override
//    public MessageResult sendSingleMessage(String mobile, String content) throws Exception {
//        log.info("sms content={}", content);
//        HttpResponse<String> response = Unirest.post(gateway)
//                .field("name", username)
//                .field("pwd", password)
//                .field("mobile", mobile)
//                .field("content", content + "【" + sign + "】")
//                .field("time", "")
//                .field("type", "pt")
//                .field("extno", "")
//                .asString();
//        log.info(" mobile : " + mobile + "content : " + content);
//        log.info("result = {}", response.getBody());
//        return parseResult(response.getBody());
//    }


    @Override
    public MessageResult sendSingleMessage(String mobile, String content) throws Exception {
        SmsDTO smsDTO = smsService.getByStatus();
        if ("chuangrui".equals(smsDTO.getSmsName())){
            return sendMessage(mobile,content,smsDTO);
        }else if ("gongxintong".equals(smsDTO.getSmsName())){
            return send2Method(mobile,content,smsDTO);
        }
        return null;
    }

    @Override
    public MessageResult sendMessageByTempId(String mobile, String content,String templateId) throws Exception {
        SmsDTO smsDTO = smsService.getByStatus();
        if ("chuangrui".equals(smsDTO.getSmsName())){
            SmsDTO smsTemp = new SmsDTO();
            smsTemp.setTemplateId(templateId);
            smsTemp.setKeyId(smsDTO.getKeyId());
            smsTemp.setKeySecret(smsDTO.getKeySecret());
            smsTemp.setSmsName(smsDTO.getSmsName());
            smsTemp.setSignId(smsDTO.getSignId());
            smsTemp.setSmsStatus(smsDTO.getSmsStatus());
            return sendMessage(mobile,content,smsTemp);
        }else if ("gongxintong".equals(smsDTO.getSmsName())){
            return sendByOder(mobile,content,smsDTO);
        }
        return null;
    }
    
    public MessageResult sendMessage(String mobile, String content,SmsDTO smsDTO) throws Exception{
        log.info("sms content={}", content);

        HttpClient httpClient = new HttpClient();
        PostMethod postMethod = new PostMethod("http://api.1cloudsp.com/api/v2/single_send");
        postMethod.getParams().setContentCharset("UTF-8");
        postMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());


        NameValuePair[] data = {
                new NameValuePair("accesskey", smsDTO.getKeyId()),
                new NameValuePair("secret", smsDTO.getKeySecret()),
                new NameValuePair("sign", smsDTO.getSignId()),
                new NameValuePair("templateId", smsDTO.getTemplateId()),
                new NameValuePair("mobile", mobile),
                new NameValuePair("content", URLEncoder.encode(content, "utf-8"))//（发送的短信内容是模板变量内容，多个变量中间用##或者$$隔开，采用utf8编码）
        };
        postMethod.setRequestBody(data);

        int statusCode = httpClient.executeMethod(postMethod);
        System.out.println("statusCode: " + statusCode + ", body: "
                + postMethod.getResponseBodyAsString());


        log.info(" mobile : " + mobile + "content : " + content);
        log.info("statusCode: " + statusCode + ", body: "
                + postMethod.getResponseBodyAsString());
        return parseResult(postMethod.getResponseBodyAsString());
    }

    private MessageResult parseResult(String result) {
        //返回示例：{"code":"0","msg":"SUCCESS","smUuid":"18863_1_0_15738776414_1_XaOQ74O_1"}
        JSONObject parts = JSONObject.parseObject(result);
        MessageResult mr = new MessageResult(500, "系统错误");
        mr.setCode(Integer.parseInt(parts.getString("code")));
        mr.setMessage(parts.getString("msg"));
        return mr;
    }


    public MessageResult sendByOder(String mobile, String content, SmsDTO smsDTO) throws Exception {
        log.info("============sms content==========={}", content);

        HttpClient httpClient = new HttpClient();
        //组装请求参数
        JSONObject json = new JSONObject();
        json.put("id", 1);
        json.put("method", "send");
        JSONObject params = new JSONObject();
        params.put("userid", smsDTO.getKeyId());
        params.put("password", smsDTO.getKeySecret());
        JSONObject[] phoneSend = new JSONObject[1];
        JSONObject submit = new JSONObject();
        submit.put("content", "您好，" + StringUtils.substringBefore(content,"#") + "广告有新的订单，对方用户名为"+StringUtils.substringAfterLast(content,"#")+"，请登录系统及时处理。【BIZZAN】");
        submit.put("phone", mobile);

        phoneSend[0] = submit;
        params.put("submit", phoneSend);
        json.put("params", params);
        log.info("==============请求参数：==========" + json);
        String url = "http://112.74.139.4:8002/sms3_api/jsonapi/jsonrpc2.jsp?" + URLEncoder.encode(json.toJSONString(), "UTF-8");
        //发送get请求
        GetMethod getMethod = new GetMethod(url);
        log.info("==============url==========" + url);
        int code = httpClient.executeMethod(getMethod);
        log.info("==============短信请求返回体：==========" + getMethod.getResponseBodyAsString());
        return parse2Result(getMethod.getResponseBodyAsString());

    }

    public MessageResult send2Method(String mobile, String content, SmsDTO smsDTO) throws Exception {
        log.info("============sms content==========={}", content);

        HttpClient httpClient = new HttpClient();
        //组装请求参数
        JSONObject json = new JSONObject();
        json.put("id", 1);
        json.put("method", "send");
        JSONObject params = new JSONObject();
        params.put("userid", smsDTO.getKeyId());
        params.put("password", smsDTO.getKeySecret());
        JSONObject[] phoneSend = new JSONObject[1];
        JSONObject submit = new JSONObject();
        submit.put("content", "您的验证码为" + content + "，十分钟内有效，如非本人操作，请忽略。【BIZZAN】");
        submit.put("phone", mobile);

        phoneSend[0] = submit;
        params.put("submit", phoneSend);
        json.put("params", params);
        log.info("==============请求参数：==========" + json);
        String url = "http://112.74.139.4:8002/sms3_api/jsonapi/jsonrpc2.jsp?" + URLEncoder.encode(json.toJSONString(), "UTF-8");
        //发送get请求
        GetMethod getMethod = new GetMethod(url);
        log.info("==============url==========" + url);
        int code = httpClient.executeMethod(getMethod);
        log.info("==============短信请求返回体：==========" + getMethod.getResponseBodyAsString());
        return parse2Result(getMethod.getResponseBodyAsString());

    }

    private MessageResult parse2Result(String result){
        //{"result":[{"phone":"15738776414","msgid":"1806282017484877844","return":"0","info":"成功"}],"id":1}
        JSONObject jsonObject = JSONObject.parseObject(result);
        MessageResult mr = new MessageResult(500, "系统错误");
        List<JSONObject> jsonResult = (List<JSONObject>) jsonObject.get("result");
        JSONObject mess = jsonResult.get(0);
        mr.setCode(Integer.parseInt(mess.getString("return")));
        mr.setMessage(mess.getString("info"));
        return mr;
    }

	@Override
	public MessageResult sendCustomMessage(String mobile, String content) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}



//    private MessageResult parseResult(String result) {
//        //返回示例：0,2017110112134171782680251,0,1,0,提交成功
//        String[] parts = result.split(",");
//        MessageResult mr = new MessageResult(500, "系统错误");
//        mr.setCode(Integer.parseInt(parts[0]));
//        mr.setMessage(parts[1]);
//        return mr;
//    }
}
