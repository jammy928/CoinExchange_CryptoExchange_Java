package com.bizzan.bitrade.config;

import com.cdeer.apns.http2.core.manager.ApnsServiceManager;
import com.cdeer.apns.http2.core.service.ApnsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

// @Configuration
public class ApnsConfig {
/*
    @Bean
    public ApnsService apnsServiceConfig(@Value("${apns.cert-file-path}") String certFile,
                                         @Value("${apns.cert-file-password}") String password,
                                         @Value("${apns.bundle-id}") String bundleId,@Value("${apns.dev-env:true}") Boolean isDevEnv) throws FileNotFoundException {
    	
        InputStream is = new FileInputStream(certFile);
        System.out.println("passowrd:"+password);
        com.cdeer.apns.http2.core.model.ApnsConfig config = new com.cdeer.apns.http2.core.model.ApnsConfig();
        config.setName("bitrade");// 推送服务名称
        config.setDevEnv(isDevEnv);// 是否是开发环境
        config.setKeyStore(is);// 证书
        config.setPassword(password);// 证书密码
        config.setPoolSize(5);// 线程池大小
        config.setTimeout(3000);// TCP连接超时时间
        config.setTopic(bundleId);// 标题,即证书的bundleID
        return ApnsServiceManager.createService(config);
    }
    */
}
