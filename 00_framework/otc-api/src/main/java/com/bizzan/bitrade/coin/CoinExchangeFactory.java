package com.bizzan.bitrade.coin;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.bizzan.bitrade.constant.Symbol;
import com.bizzan.bitrade.util.MessageResult;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;

@Slf4j
public class CoinExchangeFactory {
    @Setter
    private HashMap<String,BigDecimal> coins;

    public HashMap<String,BigDecimal> getCoins(){
        return coins;
    }

    public CoinExchangeFactory(){
        coins = new HashMap<>();
    }


    /**
     * 获取币种价格
     * @param symbol
     * @return
     */
    public BigDecimal get(String symbol){
        return coins.get(symbol);
    }

    public void  set(String symbol,BigDecimal rate){
        coins.put(symbol,rate);
    }
}
