package com.bizzan.bc.wallet.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bizzan.bc.wallet.component.ActClient;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

@Configuration
public class JsonrpcConfig {

    @Bean
    public ActClient setActClient(@Value("${coin.rpc}") String url) throws MalformedURLException, URISyntaxException {
        System.out.println("coin.rpc="+url);
        ActClient client = new ActClient(url);
        return client;
    }
}
