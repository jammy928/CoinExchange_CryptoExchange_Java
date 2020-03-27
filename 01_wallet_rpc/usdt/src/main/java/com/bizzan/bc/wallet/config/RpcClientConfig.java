package com.bizzan.bc.wallet.config;

import com.alibaba.fastjson.JSONObject;
import com.spark.blockchain.rpcclient.BitcoinRPCClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.MalformedURLException;

/**
 * 初始化RPC客户端
 */
@Configuration
public class RpcClientConfig {
    private Logger logger = LoggerFactory.getLogger(RpcClientConfig.class);

    @Bean
    public JsonrpcClient setClient(@Value("${coin.rpc}") String uri){
        try {
            logger.info("uri={}",uri);
            JsonrpcClient client =  new JsonrpcClient(uri);
            logger.info("=============================");
            logger.info("client={}",client);
            logger.info("=============================");
            return client;
        } catch (MalformedURLException e) {
            logger.info("init wallet failed");
            e.printStackTrace();
            return null;
        }
    }
}
