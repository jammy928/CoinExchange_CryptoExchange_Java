package com.bizzan.bc.wallet.util;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class HttpClientUtil {
    
    private static String charSet = "UTF-8";
    private static CloseableHttpClient httpClient = null;
    private static CloseableHttpResponse response = null;
    
    /**
     * https的post请求
     * @param url
     * @param jsonstr
     * @param charset
     * @return
     * @throws IOException 
     * @throws ClientProtocolException 
     * @throws KeyStoreException 
     * @throws NoSuchAlgorithmException 
     * @throws KeyManagementException 
     */
    public static String doHttpsPost(String url, String jsonStr, Map<String,String> headerPram) throws ClientProtocolException, IOException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
        try {
            httpClient = SSLClient.createSSLClientDefault();
            HttpPost httpPost = new HttpPost(url);
            if(headerPram != null && !headerPram.isEmpty()) {
            	for(Map.Entry<String, String> entry:headerPram.entrySet()){
            		httpPost.setHeader(entry.getKey(), entry.getValue());
                }
            }
            
            StringEntity se = new StringEntity(jsonStr);
            se.setContentType("text/json");
            se.setContentEncoding(new BasicHeader("Content-Type", "application/json"));
            httpPost.setEntity(se);
            
            response = httpClient.execute(httpPost);
            if (response != null) {
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    return EntityUtils.toString(resEntity, charSet);
                }
            }
        }finally {
            if(httpClient != null){
                 httpClient.close();
            }
            if(response != null){
                  response.close();
            }
        }
        return null;
    }
    /**
     * http的post请求(用于key-value格式的参数) 
     * @param url
     * @param param
     * @return
     * @throws IOException 
     * @throws ClientProtocolException 
     * @throws KeyStoreException 
     * @throws NoSuchAlgorithmException 
     * @throws KeyManagementException 
     */
    public static String doHttpsPost(String url,Map<String,String> param, Map<String,String> headerPram) throws ClientProtocolException, IOException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException{
        try {
            //请求发起客户端
            httpClient = SSLClient.createSSLClientDefault();
            //参数集合
            List<NameValuePair> postParams = new ArrayList<NameValuePair>();
            //遍历参数并添加到集合
            for(Map.Entry<String, String> entry:param.entrySet()){
                postParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            
            //通过post方式访问
            HttpPost post = new HttpPost(url);
            if(headerPram != null && !headerPram.isEmpty()) {
            	for(Map.Entry<String, String> entry:headerPram.entrySet()){
            		post.setHeader(entry.getKey(), entry.getValue());
                }
            }
            HttpEntity paramEntity = new UrlEncodedFormEntity(postParams,charSet);
            post.setEntity(paramEntity);
            response = httpClient.execute(post);
            StatusLine status = response.getStatusLine();  
            int state = status.getStatusCode();  
            if (state == HttpStatus.SC_OK) {  
                HttpEntity valueEntity = response.getEntity();
                String content = EntityUtils.toString(valueEntity);
                //jsonObject = JSONObject.fromObject(content);
                return content;
            }
        }finally{
            if(httpClient != null){
                httpClient.close();
            }
            if(response != null){
                response.close();
            }
        }
        return null;
    }
    /**
     * http的post请求(用于key-value格式的参数) 
     * @param url
     * @param param
     * @return
     * @throws IOException 
     * @throws ClientProtocolException 
     */
    public static String doHttpPost(String url,Map<String,String> param, Map<String,String> headerPram) throws ClientProtocolException, IOException{
        try {
            //请求发起客户端
            httpClient = HttpClients.createDefault();
            //参数集合
            List<NameValuePair> postParams = new ArrayList<NameValuePair>();
            //遍历参数并添加到集合
            for(Map.Entry<String, String> entry:param.entrySet()){
                postParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            
            //通过post方式访问
            HttpPost post = new HttpPost(url);
            if(headerPram != null && !headerPram.isEmpty()) {
            	for(Map.Entry<String, String> entry:headerPram.entrySet()){
            		post.setHeader(entry.getKey(), entry.getValue());
                }
            }
            HttpEntity paramEntity = new UrlEncodedFormEntity(postParams,charSet);
            post.setEntity(paramEntity);
            response = httpClient.execute(post);
            StatusLine status = response.getStatusLine();  
            int state = status.getStatusCode();  
            if (state == HttpStatus.SC_OK) {
                HttpEntity valueEntity = response.getEntity();
                String content = EntityUtils.toString(valueEntity);
                return content;
            }else {
            	return null;
            }
        }finally{
            if(httpClient != null){
                httpClient.close();
            }
            if(response != null){
                response.close();
            }
        }
    }
    
     /** 
     * http的post请求（用于请求json格式的参数） 
     * @param url 
     * @param params 
     * @return 
     * @throws IOException 
     * @throws ClientProtocolException 
     */  
    public static String doHttpPost(String url, String jsonStr, Map<String,String> headerPram) throws ClientProtocolException, IOException {  
        try {
            httpClient = HttpClients.createDefault();
          
            // 创建httpPost
            HttpPost httpPost = new HttpPost(url);     
            if(headerPram != null && !headerPram.isEmpty()) {
            	for(Map.Entry<String, String> entry:headerPram.entrySet()){
            		httpPost.setHeader(entry.getKey(), entry.getValue());
                }
            }
              
            StringEntity entity = new StringEntity(jsonStr, charSet);  
            entity.setContentType("text/json");
            entity.setContentEncoding(new BasicHeader("Content-Type", "application/json"));
            httpPost.setEntity(entity);          
            //发送post请求
            response = httpClient.execute(httpPost);  
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {  
                HttpEntity responseEntity = response.getEntity();  
                String jsonString = EntityUtils.toString(responseEntity);  
                return jsonString;  
            }
        }finally {
            if(httpClient != null){
                httpClient.close();
            }
            if(response != null){
                response.close();
            }
        }  
        return null;  
    }  
    
    /**
     * http的Get请求
     * @param url
     * @param param
     * @return
     * @throws IOException 
     * @throws ClientProtocolException 
     */
    public static String doHttpGet(String url, Map<String,String> param, Map<String,String> headerPram) throws ClientProtocolException, IOException {
        CloseableHttpClient httpclient = null;
        CloseableHttpResponse response = null;
        
        try {
            httpclient = HttpClients.createDefault();
            if(param != null && !param.isEmpty()) {
                //参数集合
                List<NameValuePair> getParams = new ArrayList<NameValuePair>();
                for(Map.Entry<String, String> entry:param.entrySet()){
                    getParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
                url +="?"+EntityUtils.toString(new UrlEncodedFormEntity(getParams), "UTF-8");
            }
            //发送gey请求
            HttpGet httpGet = new HttpGet(url);
            if(headerPram != null && !headerPram.isEmpty()) {
            	for(Map.Entry<String, String> entry:headerPram.entrySet()){
            		httpGet.setHeader(entry.getKey(), entry.getValue());
                }
            }
            response = httpclient.execute(httpGet);  
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {  
                return EntityUtils.toString(response.getEntity());  
            } 
        }finally{
            if(httpclient != null){
                httpclient.close();
            }
            if(response != null){
                response.close();
            }
        }
        return null;
    }
    /**
     * https的Get请求
     * @param url
     * @param param
     * @return
     * @throws IOException 
     * @throws ClientProtocolException 
     * @throws KeyStoreException 
     * @throws NoSuchAlgorithmException 
     * @throws KeyManagementException 
     */
    public static String doHttpsGet(String url, Map<String,String> param, Map<String,String> headerPram) throws ClientProtocolException, IOException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
        try {
            httpClient = SSLClient.createSSLClientDefault();
            if(param != null && !param.isEmpty()) {
                //参数集合
                List<NameValuePair> getParams = new ArrayList<NameValuePair>();
                for(Map.Entry<String, String> entry:param.entrySet()){
                    getParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
                url +="?"+EntityUtils.toString(new UrlEncodedFormEntity(getParams), "UTF-8");
            }
            HttpGet httpGet = new HttpGet(url);
            RequestConfig rconfig = RequestConfig.custom()
            		.setConnectionRequestTimeout(2000)
            		.setSocketTimeout(4000)
            		.setConnectTimeout(3000)
            		.build();
            httpGet.setConfig(rconfig);
            if(headerPram != null && !headerPram.isEmpty()) {
            	for(Map.Entry<String, String> entry:headerPram.entrySet()){
            		httpGet.setHeader(entry.getKey(), entry.getValue());
                }
            }
            response = httpClient.execute(httpGet);
            if (response != null) {
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    return EntityUtils.toString(resEntity, charSet);
                }
            }
        }finally {
         	if(httpClient != null){
                httpClient.close();
            }
            if(response != null){
                response.close();
            }
        }
        return null;
    }
}
