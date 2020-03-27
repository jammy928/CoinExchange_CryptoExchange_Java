package com.bizzan.bitrade;

import org.junit.Before;
import org.junit.Test;

import com.bizzan.bitrade.Trader.CoinTrader;
import com.bizzan.bitrade.entity.*;

import java.math.BigDecimal;
import java.util.Calendar;

public class CoinTraderTest {
    private CoinTrader trader;

    @Before
    public void setup(){
        trader = new CoinTrader("BTCUSDT");
    }

    public void genSellOrder(){
        BigDecimal basePrice = new BigDecimal(10000);
        for(int i=0;i<10;i ++) {
            ExchangeOrder exchangeOrder = new ExchangeOrder();
            exchangeOrder.setAmount(new BigDecimal("10"));
            exchangeOrder.setBaseSymbol("USDT");
            exchangeOrder.setCoinSymbol("BTC");
            exchangeOrder.setDirection(ExchangeOrderDirection.BUY);
            double rand = Math.random()*100;
            exchangeOrder.setPrice(basePrice.add(new BigDecimal(rand).setScale(4,BigDecimal.ROUND_HALF_UP)));
            long millis = Calendar.getInstance().getTimeInMillis();
            exchangeOrder.setOrderId("T"+millis+""+i);
            exchangeOrder.setTime(millis);
            exchangeOrder.setStatus(ExchangeOrderStatus.TRADING);
            exchangeOrder.setType(ExchangeOrderType.LIMIT_PRICE);
            trader.addLimitPriceOrder(exchangeOrder);
        }
    }

    public ExchangeOrder createSellOrder(String price, String amount, ExchangeOrderType type){
        ExchangeOrder exchangeOrder = new ExchangeOrder();
        exchangeOrder.setAmount(new BigDecimal(amount));
        exchangeOrder.setBaseSymbol("USDT");
        exchangeOrder.setCoinSymbol("BTC");
        exchangeOrder.setDirection(ExchangeOrderDirection.SELL);
        long millis = Calendar.getInstance().getTimeInMillis();
        exchangeOrder.setOrderId("T"+millis);
        exchangeOrder.setTime(millis);
        exchangeOrder.setStatus(ExchangeOrderStatus.TRADING);
        exchangeOrder.setPrice(new BigDecimal(price));
        exchangeOrder.setType(type);
        return exchangeOrder;
    }

    public ExchangeOrder createBuyOrder(String price, String amount, ExchangeOrderType type){
        ExchangeOrder exchangeOrder = new ExchangeOrder();
        exchangeOrder.setAmount(new BigDecimal(amount));
        exchangeOrder.setBaseSymbol("USDT");
        exchangeOrder.setCoinSymbol("BTC");
        exchangeOrder.setDirection(ExchangeOrderDirection.BUY);
        long millis = Calendar.getInstance().getTimeInMillis();
        exchangeOrder.setOrderId("T"+millis);
        exchangeOrder.setTime(millis);
        exchangeOrder.setStatus(ExchangeOrderStatus.TRADING);
        exchangeOrder.setPrice(new BigDecimal(price));
        exchangeOrder.setType(type);
        return exchangeOrder;
    }

}
