package com.bizzan.bc.wallet.config;

import com.spark.blockchain.rpcclient.BitcoinException;
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
    public BitcoinRPCClient setClient(@Value("${coin.rpc}") String uri){
        try {
            logger.info("uri={}",uri);
            BitcoinRPCClient client =  new BitcoinRPCClient(uri);
            int blockCount = client.getBlockCount();
            logger.info("blockHeight={}",blockCount);
            return client;
        } catch (MalformedURLException e) {
            logger.info("init wallet failed");
            e.printStackTrace();
            return null;
        } catch (BitcoinException e) {
            logger.info("BitcoinException");
            e.printStackTrace();
            return null;
        }
    }
}
