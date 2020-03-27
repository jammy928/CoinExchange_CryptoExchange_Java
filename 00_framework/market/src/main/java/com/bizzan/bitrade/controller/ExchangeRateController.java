package com.bizzan.bitrade.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bizzan.bitrade.component.CoinExchangeRate;
import com.bizzan.bitrade.util.MessageResult;

import java.math.BigDecimal;

@RestController
@RequestMapping("/exchange-rate")
public class ExchangeRateController {
    @Autowired
    private CoinExchangeRate coinExchangeRate;

    @RequestMapping("usd/{coin}")
    public MessageResult getUsdExchangeRate(@PathVariable String coin){
        MessageResult mr = new MessageResult(0,"success");
        BigDecimal latestPrice = coinExchangeRate.getUsdRate(coin);
        mr.setData(latestPrice.toString());
        return mr;
    }
    
    @RequestMapping("usdtcny")
    public MessageResult getUsdtExchangeRate(){
        MessageResult mr = new MessageResult(0,"success");
        BigDecimal latestPrice = coinExchangeRate.getUsdtCnyRate();
        mr.setData(latestPrice.toString());
        return mr;
    }

    @RequestMapping("cny/{coin}")
    public MessageResult getCnyExchangeRate(@PathVariable String coin){
        MessageResult mr = new MessageResult(0,"success");
        BigDecimal latestPrice = coinExchangeRate.getCnyRate(coin);
        mr.setData(latestPrice.toString());
        return mr;
    }

    @RequestMapping("jpy/{coin}")
    public MessageResult getJpyExchangeRate(@PathVariable String coin){
        MessageResult mr = new MessageResult(0,"success");
        BigDecimal latestPrice = coinExchangeRate.getJpyRate(coin);
        mr.setData(latestPrice.toString());
        return mr;
    }

    @RequestMapping("hkd/{coin}")
    public MessageResult getHkdExchangeRate(@PathVariable String coin){
        MessageResult mr = new MessageResult(0,"success");
        BigDecimal latestPrice = coinExchangeRate.getHkdRate(coin);
        mr.setData(latestPrice.toString());
        return mr;
    }

    @RequestMapping("usd-{unit}")
    public MessageResult getUsdCnyRate(@PathVariable String unit){
        MessageResult mr = new MessageResult(0,"success");
        if("CNY".equalsIgnoreCase(unit)) {
            mr.setData(coinExchangeRate.getUsdtCnyRate());
        }
        else if("JPY".equalsIgnoreCase(unit)) {
            mr.setData(coinExchangeRate.getUsdJpyRate());
        }
        else if("HKD".equalsIgnoreCase(unit)) {
            mr.setData(coinExchangeRate.getUsdHkdRate());
        }
        else {
            mr.setData(BigDecimal.ZERO);
        }
        return mr;
    }
}
