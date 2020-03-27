package com.bizzan.bitrade.vendor.provider.support;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.bizzan.bitrade.dto.SmsDTO;
import com.bizzan.bitrade.service.SmsService;
import com.bizzan.bitrade.util.MessageResult;
import com.bizzan.bitrade.vendor.provider.SMSProvider;

import lombok.extern.slf4j.Slf4j;

/**
 * 第一信息短信接口（http://www.1xinxi.cn）
 * @author shaox
 *
 */
@Slf4j
public class DiyiSMSProvider implements SMSProvider {
    private String username;
    private String password;
    private String sign;
    
    @Autowired
    private SmsService smsService;

    public DiyiSMSProvider(String username, String password, String sign) {
    	this.username = username;
    	this.password = password;
    	this.sign = sign;
    }
    
    public static String getName() {
        return "diyi";
    }
    
	@Override
	public MessageResult sendSingleMessage(String mobile, String content) throws Exception {
		SmsDTO smsDTO = smsService.getByStatus();
        if("diyi".equals(smsDTO.getSmsName())){
        	return sendMessage(mobile,content,smsDTO);
        }
        return null;
	}

	@Override
	public MessageResult sendMessageByTempId(String mobile, String content, String templateId) throws Exception {
		return null;
	}

	public MessageResult sendMessage(String mobile, String content,SmsDTO smsDTO) throws Exception{
        log.info("sms content={}", content);
        StringBuffer sb = new StringBuffer("http://sms.1xinxi.cn/asmx/smsservice.aspx?");

		sb.append("name="+this.username);
		sb.append("&pwd="+this.password);
		sb.append("&mobile="+mobile);
		sb.append("&content="+URLEncoder.encode("您的验证码为" + content + "，十分钟内有效，如非本人操作，请忽略。","UTF-8"));
		sb.append("&stime=");
		sb.append("&sign="+URLEncoder.encode(this.sign,"UTF-8"));
		sb.append("&type=pt&extno=");
		// 创建url对象
		//String temp = new String(sb.toString().getBytes("GBK"),"UTF-8");
		URL url = new URL(sb.toString());

		// 打开url连接
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		// 设置url请求方式 ‘post’
		connection.setRequestMethod("POST");

		// 发送
		InputStream is =url.openStream();

		//转换返回值（‘0,2019062814515653465244228,0,1,0,提交成功’）
		String returnStr = convertStreamToString(is);
		
		log.info(" mobile : " + mobile + "content : " + content);
		log.info("send result: "+returnStr);
		
		return parseResult(returnStr);
    }
	
    private MessageResult parseResult(String result) {
        //返回示例：{"code":"0","msg":"SUCCESS","smUuid":"18863_1_0_15738776414_1_XaOQ74O_1"}
        String[] resArr = result.split(",");
        MessageResult mr = new MessageResult(500, "系统错误");
        mr.setCode(Integer.parseInt(resArr[0]));
        mr.setMessage(resArr[5]);
        return mr;
    }
    
    /**
	 * 转换返回值类型为UTF-8格式.
	 * @param is
	 * @return
	 */
	public String convertStreamToString(InputStream is) {    
        StringBuilder sb1 = new StringBuilder();    
        byte[] bytes = new byte[4096];  
        int size = 0;  
        
        try {    
        	while ((size = is.read(bytes)) > 0) {  
                String str = new String(bytes, 0, size, "UTF-8");  
                sb1.append(str);  
            }  
        } catch (IOException e) {    
            e.printStackTrace();    
        } finally {    
            try {    
                is.close();    
            } catch (IOException e) {    
               e.printStackTrace();    
            }    
        }    
        return sb1.toString();    
    }

	@Override
	public MessageResult sendCustomMessage(String mobile, String content) throws Exception {
		log.info("sms content={}", content);
        StringBuffer sb = new StringBuffer("http://sms.1xinxi.cn/asmx/smsservice.aspx?");

		sb.append("name="+this.username);
		sb.append("&pwd="+this.password);
		sb.append("&mobile="+mobile);
		sb.append("&content="+URLEncoder.encode(content,"UTF-8"));
		sb.append("&stime=");
		sb.append("&sign="+URLEncoder.encode(this.sign,"UTF-8"));
		sb.append("&type=pt&extno=");
		// 创建url对象
		//String temp = new String(sb.toString().getBytes("GBK"),"UTF-8");
		URL url = new URL(sb.toString());

		// 打开url连接
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		// 设置url请求方式 ‘post’
		connection.setRequestMethod("POST");

		// 发送
		InputStream is =url.openStream();

		//转换返回值（‘0,2019062814515653465244228,0,1,0,提交成功’）
		String returnStr = convertStreamToString(is);
		
		log.info(" mobile : " + mobile + "content : " + content);
		log.info("send result: "+returnStr);
		
		return parseResult(returnStr);
	}
}
