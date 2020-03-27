package com.bizzan.bitrade.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bizzan.bitrade.coin.CoinExchangeFactory;
import com.bizzan.bitrade.entity.OtcCoin;
import com.bizzan.bitrade.service.OtcCoinService;

import java.math.BigDecimal;
import java.util.List;

@Configuration
@Slf4j
public class CoinExchangeFactoryConfig {

    @Bean
    public CoinExchangeFactory getCoinExchangeFactory(OtcCoinService coinService) {
        log.info("init CoinExchangeFactory start");
        List<OtcCoin> coins = coinService.findAll();
        CoinExchangeFactory factory = new CoinExchangeFactory();
        coins.forEach(coin -> {
            factory.set(coin.getUnit(), new BigDecimal(0));
            log.info("factory add unit = {} , rate  = 0 !", coin.getUnit());
        });
        factory.set("USDT", new BigDecimal(0));
        log.info("factory add unit = USDT , rate  = 0!");
        log.info("init CoinExchangeFactory end");
        return factory;
    }
}
