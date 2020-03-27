package com.bizzan.bitrade.system;

import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class CoinExchangeFactory {

    @Data
    public class ExchangeRate{
        public BigDecimal cnyRate;
        public BigDecimal usdRate;
    }


    @Setter
    private ConcurrentHashMap<String,ExchangeRate> coins;

    public ConcurrentHashMap<String,ExchangeRate> getCoins(){
        return coins;
    }

    public CoinExchangeFactory(){
        coins = new ConcurrentHashMap<>();
    }


    /**
     * 获取币种价格
     * @param symbol
     * @return
     */
    public ExchangeRate get(String symbol){
        return coins.get(symbol);
    }

    public void set(String symbol,BigDecimal usdRate,BigDecimal cnyRate){
        ExchangeRate rate = new ExchangeRate();
        rate.setCnyRate(cnyRate);
        rate.setUsdRate(usdRate);
        coins.put(symbol,rate);
    }
}
