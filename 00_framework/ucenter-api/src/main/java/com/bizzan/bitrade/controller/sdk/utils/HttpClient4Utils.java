/*
 * @(#) HttpClientUtils.java 2016年2月3日
 * 
 * Copyright 2010 NetEase.com, Inc. All rights reserved.
 */
package com.bizzan.bitrade.controller.sdk.utils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.StandardHttpRequestRetryHandler;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

/**
 * HttpClient工具类
 *
 * @author captcha_dev
 * @version 2016年2月3日
 */
public class HttpClient4Utils {
    private static HttpClient defaultClient = createHttpClient(20, 20, 5000, 5000, 3000);

    /**
     * 实例化HttpClient
     *
     * @param maxTotal
     * @param maxPerRoute
     * @param socketTimeout
     * @param connectTimeout
     * @param connectionRequestTimeout
     * @return
     */
    public static HttpClient createHttpClient(int maxTotal, int maxPerRoute, int socketTimeout, int connectTimeout,
                                              int connectionRequestTimeout) {
        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setSocketTimeout(socketTimeout)
                .setConnectTimeout(connectTimeout)
                .setConnectionRequestTimeout(connectionRequestTimeout).build();

        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(maxTotal);
        cm.setDefaultMaxPerRoute(maxPerRoute);
        cm.setValidateAfterInactivity(200); // 一个连接idle超过200ms,再次被使用之前,需要先做validation
        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(cm)
                .setConnectionTimeToLive(30, TimeUnit.SECONDS)
                .setRetryHandler(new StandardHttpRequestRetryHandler(3, true)) // 配置出错重试
                .setDefaultRequestConfig(defaultRequestConfig).build();

        startMonitorThread(cm);

        return httpClient;
    }

	/**
	 * 增加定时任务, 每隔一段时间清理连接
	 *
	 * @param cm
	 */
	private static void startMonitorThread(final PoolingHttpClientConnectionManager cm) {
		// 参数是线程的数量
		ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("start-monitor-pool-%d").build();
		ScheduledExecutorService singleThreadPool = (ScheduledExecutorService) new ThreadPoolExecutor(1, 1, 0L,
				TimeUnit.MILLISECONDS, new SynchronousQueue<Runnable>(),namedThreadFactory,new ThreadPoolExecutor.AbortPolicy());
		/**
		 * 第二个参数，是首次执行该线程的延迟时间，之后失效 第三个参数是，首次执行完之后，再过该段时间再次执行该线程，具有周期性
		 */
		singleThreadPool.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						cm.closeExpiredConnections();
						cm.closeIdleConnections(30, TimeUnit.SECONDS);

                       // log.info("closing expired & idle connections, stat={}", cm.getTotalStats());
                       TimeUnit.SECONDS.sleep(10);
                   } catch (Exception e) {
                       // ignore exceptoin
                   }
               }
           }
        }, 10, 3, TimeUnit.SECONDS);


//        Thread t = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (true) {
//                    try {
//                        cm.closeExpiredConnections();
//                        cm.closeIdleConnections(30, TimeUnit.SECONDS);
//
//                        // log.info("closing expired & idle connections, stat={}", cm.getTotalStats());
//                        TimeUnit.SECONDS.sleep(10);
//                    } catch (Exception e) {
//                        // ignore exceptoin
//                    }
//                }
//            }
////        });
//        t.setDaemon(true);
//        t.start();
    }

    /**
     * 发送post请求
     *
     * @param httpClient
     * @param url        请求地址
     * @param params     请求参数
     * @param encoding   编码
     * @return
     */
    public static String sendPost(HttpClient httpClient, String url, Map<String, String> params, Charset encoding) {
        String resp = "";
        HttpPost httpPost = new HttpPost(url);
        if (params != null && params.size() > 0) {
            List<NameValuePair> formParams = new ArrayList<NameValuePair>();
            Iterator<Map.Entry<String, String>> itr = params.entrySet().iterator();
            while (itr.hasNext()) {
                Map.Entry<String, String> entry = itr.next();
                formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            UrlEncodedFormEntity postEntity = new UrlEncodedFormEntity(formParams, encoding);
            httpPost.setEntity(postEntity);
        }
        CloseableHttpResponse response = null;
        try {
            response = (CloseableHttpResponse) httpClient.execute(httpPost);
            resp = EntityUtils.toString(response.getEntity(), encoding);
        } catch (Exception e) {
            // log
            e.printStackTrace();
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    // log
                    e.printStackTrace();
                }
            }
        }
        return resp;
    }

    /**
     * 发送post请求
     *
     * @param url    请求地址
     * @param params 请求参数
     * @return
     */
    public static String sendPost(String url, Map<String, String> params) {
        Charset encoding = Charset.forName("utf8");
        return sendPost(defaultClient, url, params, encoding);
    }
}
