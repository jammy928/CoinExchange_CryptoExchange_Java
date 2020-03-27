package com.bizzan.bitrade.util;


import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

import java.util.Map;

/**
 * @author tansitao
 * @time 2018-04-05
 * http短信接口访问工具
 */
public class HttpSend {

	
	
	/**
     * 基于HttpClient 4.3的通用POST方法
     *
     * @param url       提交的URL
     * @param paramsMap 提交<参数，值>Map
     * @return 提交响应
     */

	public static String yunpianPost(String url, Map<String, String> paramsMap) {
		HttpClient client = new HttpClient();
		try {
			PostMethod method = new PostMethod(url);
			if (paramsMap != null) {
				NameValuePair[] namePairs = new NameValuePair[paramsMap.size()];
				int i = 0;
				for (Map.Entry<String, String> param : paramsMap.entrySet()) {
					NameValuePair pair = new NameValuePair(param.getKey(),
							param.getValue());
					namePairs[i++] = pair;
				}
				method.setRequestBody(namePairs);
				HttpMethodParams param = method.getParams();
				param.setContentCharset("utf-8");
			}
			client.executeMethod(method);
			return method.getResponseBodyAsString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

}
