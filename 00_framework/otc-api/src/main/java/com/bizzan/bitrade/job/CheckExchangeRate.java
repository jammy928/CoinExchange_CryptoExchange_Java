package com.bizzan.bitrade.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.bizzan.bitrade.coin.CoinExchangeFactory;
import com.bizzan.bitrade.util.MessageResult;

import java.math.BigDecimal;

@Component
@Slf4j
public class CheckExchangeRate {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private CoinExchangeFactory factory;

    @Scheduled(fixedRate = 5 * 60 * 1000)
    public void syncRate() {
        log.info("CheckExchangeRate syncRate start");
        factory.getCoins().forEach(
                (symbol, value) -> {
                    String serviceName = "bitrade-market";
                    String url = "http://" + serviceName + "/market/exchange-rate/cny/{coin}";
                    ResponseEntity<MessageResult> result = restTemplate.getForEntity(url, MessageResult.class, symbol);
                    log.info("remote call:url={},result={},unit={}", url, result, symbol);
                    if (result.getStatusCode().value() == 200 && result.getBody().getCode() == 0) {
                        BigDecimal rate = new BigDecimal((String) result.getBody().getData());
                        log.info("unit = {} ,get rate success ! value = {} !", symbol, rate);
                        factory.set(symbol, rate);
                    } else {
                        log.info("unit = {} ,get rate error ! default value zero!", symbol);
                        factory.set(symbol, BigDecimal.ZERO);
                    }
                });
        log.info("CheckExchangeRate syncRate end");
    }

}
