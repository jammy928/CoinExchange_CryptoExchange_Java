package com.bizzan.bitrade.component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizzan.bitrade.entity.Coin;
import com.bizzan.bitrade.entity.CoinThumb;
import com.bizzan.bitrade.processor.CoinProcessor;
import com.bizzan.bitrade.processor.CoinProcessorFactory;
import com.bizzan.bitrade.service.CoinService;
import com.bizzan.bitrade.service.ExchangeCoinService;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 币种汇率管理
 */
@Component
@Slf4j
@ToString
public class CoinExchangeRate {
    @Getter
    @Setter
    private BigDecimal usdCnyRate = new BigDecimal("6.90");
    
    @Getter
    @Setter
    private BigDecimal usdtCnyRate = new BigDecimal("7.00");
    
    @Getter
    @Setter
    private BigDecimal usdJpyRate = new BigDecimal("110.02");
    @Getter
    @Setter
    private BigDecimal usdHkdRate = new BigDecimal("7.8491");
    @Getter
    @Setter
    private BigDecimal sgdCnyRate = new BigDecimal("4.77");
    @Setter
    private CoinProcessorFactory coinProcessorFactory;

    @Autowired
    private CoinService coinService;
    @Autowired
    private ExchangeCoinService exCoinService;


    public BigDecimal getUsdRate(String symbol) {
        log.info("CoinExchangeRate getUsdRate unit = " + symbol);
        if ("USDT".equalsIgnoreCase(symbol)) {
            log.info("CoinExchangeRate getUsdRate unit = USDT  ,result = ONE");
            return BigDecimal.ONE;
        } else if ("CNY".equalsIgnoreCase(symbol)) {
            log.info("CoinExchangeRate getUsdRate unit = CNY  ,result : 1 divide {}", this.usdtCnyRate);
            BigDecimal bigDecimal = BigDecimal.ONE.divide(usdtCnyRate, 4,BigDecimal.ROUND_DOWN).setScale(4, BigDecimal.ROUND_DOWN);
            return bigDecimal;
        }else if ("BITCNY".equalsIgnoreCase(symbol)) {
            BigDecimal bigDecimal = BigDecimal.ONE.divide(usdCnyRate, 4,BigDecimal.ROUND_DOWN).setScale(4, BigDecimal.ROUND_DOWN);
            return bigDecimal;
        } else if ("ET".equalsIgnoreCase(symbol)) {
            BigDecimal bigDecimal = BigDecimal.ONE.divide(usdCnyRate, 4,BigDecimal.ROUND_DOWN).setScale(4, BigDecimal.ROUND_DOWN);
            return bigDecimal;
        } else if ("JPY".equalsIgnoreCase(symbol)) {
            BigDecimal bigDecimal = BigDecimal.ONE.divide(usdJpyRate, 4,BigDecimal.ROUND_DOWN).setScale(4, BigDecimal.ROUND_DOWN);
            return bigDecimal;
        }else if ("HKD".equalsIgnoreCase(symbol)) {
            BigDecimal bigDecimal = BigDecimal.ONE.divide(usdHkdRate, 4,BigDecimal.ROUND_DOWN).setScale(4, BigDecimal.ROUND_DOWN);
            return bigDecimal;
        }
        String usdtSymbol = symbol.toUpperCase() + "/USDT";
        String btcSymbol = symbol.toUpperCase() + "/BTC";
        String ethSymbol = symbol.toUpperCase() + "/ETH";

        if (coinProcessorFactory != null) {
            if (coinProcessorFactory.containsProcessor(usdtSymbol)) {
                log.info("Support exchange coin = {}", usdtSymbol);
                CoinProcessor processor = coinProcessorFactory.getProcessor(usdtSymbol);
                if(processor == null) {
                	return BigDecimal.ZERO;
                }
                CoinThumb thumb = processor.getThumb();
                if(thumb == null) {
                	log.info("Support exchange coin thumb is null", thumb);
                	return BigDecimal.ZERO;
                }
                return thumb.getUsdRate();
            } else if (coinProcessorFactory.containsProcessor(btcSymbol)) {
                log.info("Support exchange coin = {}/BTC", btcSymbol);
                CoinProcessor processor = coinProcessorFactory.getProcessor(btcSymbol);
                if(processor == null) {
                	return BigDecimal.ZERO; 
                }
                CoinThumb thumb = processor.getThumb();
                if(thumb == null) {
                	log.info("Support exchange coin thumb is null", thumb);
                	return BigDecimal.ZERO;
                }
                return thumb.getUsdRate();
            } else if (coinProcessorFactory.containsProcessor(ethSymbol)) {
                log.info("Support exchange coin = {}/ETH", ethSymbol);
                CoinProcessor processor = coinProcessorFactory.getProcessor(ethSymbol);
                if(processor == null) {
                	return BigDecimal.ZERO; 
                }
                CoinThumb thumb = processor.getThumb();
                if(thumb == null) {
                	log.info("Support exchange coin thumb is null", thumb);
                	return BigDecimal.ZERO;
                }
                return thumb.getUsdRate();
            } else {
                return getDefaultUsdRate(symbol);
            }
        } else {
            return getDefaultUsdRate(symbol);
        }
    }

    /**
     * 获取币种设置里的默认价格
     *
     * @param symbol
     * @return
     */
    public BigDecimal getDefaultUsdRate(String symbol) {
        Coin coin = coinService.findByUnit(symbol);
        if (coin != null) {
            return new BigDecimal(coin.getUsdRate());
        } else {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getCnyRate(String symbol) {
        if ("CNY".equalsIgnoreCase(symbol)) {
            return BigDecimal.ONE;
        } else if("ET".equalsIgnoreCase(symbol)){
            return BigDecimal.ONE;
        }
        return getUsdRate(symbol).multiply(usdtCnyRate).setScale(2, RoundingMode.DOWN);
    }

    public BigDecimal getJpyRate(String symbol) {
        if ("JPY".equalsIgnoreCase(symbol)) {
            return BigDecimal.ONE;
        }
        return getUsdRate(symbol).multiply(usdJpyRate).setScale(2, RoundingMode.DOWN);
    }

    public BigDecimal getHkdRate(String symbol) {
        if ("HKD".equalsIgnoreCase(symbol)) {
            return BigDecimal.ONE;
        }
        return getUsdRate(symbol).multiply(usdHkdRate).setScale(2, RoundingMode.DOWN);
    }

    /**
     * 每5分钟同步一次价格
     *
     * @throws UnirestException
     */
    
    @Scheduled(cron = "0 */5 * * * *")
    public void syncUsdtCnyPrice() throws UnirestException {
    	// 抹茶OTC接口
    	String url = "https://otc.mxc.com/api/coin/USDT/price";
        //如有报错 请自行官网申请获取汇率 或者默认写死
        HttpResponse<JsonNode> resp = Unirest.get(url)
                .asJson();
        if(resp.getStatus() == 200) { //正确返回
	        JSONObject ret = JSON.parseObject(resp.getBody().toString());
	        if(ret.getIntValue("code") == 0) {
	        	JSONObject result = ret.getJSONObject("result");
	        	setUsdtCnyRate(new BigDecimal(result.getDouble("buy")).setScale(2, RoundingMode.HALF_UP));
	        	return;
	        }
        }
        
        // Huobi Otc接口（如抹茶接口无效则走此路径）
        String url2 = "https://otc-api-sz.eiijo.cn/v1/data/trade-market?coinId=2&currency=1&tradeType=sell&currPage=1&payMethod=0&country=37&blockType=general&online=1&range=0&amount=";
        HttpResponse<JsonNode> resp2 = Unirest.get(url2)
                .asJson();
        if(resp2.getStatus() == 200) { //正确返回
        	JSONObject ret2 = JSON.parseObject(resp2.getBody().toString());
	        if(ret2.getIntValue("code") == 200) {
	        	JSONArray arr = ret2.getJSONArray("data");
	        	if(arr.size() > 0) {
	        		JSONObject obj = arr.getJSONObject(0);
	        		setUsdtCnyRate(new BigDecimal(obj.getDouble("price")).setScale(2, RoundingMode.HALF_UP));
	        		return;
	        	}
	        }
        }
        
        // Okex Otc接口
        String url3 = "https://www.okex.me/v3/c2c/tradingOrders/book?t=1566269221580&side=sell&baseCurrency=usdt&quoteCurrency=cny&userType=certified&paymentMethod=all";
        HttpResponse<JsonNode> resp3 = Unirest.get(url2)
                .asJson();
        if(resp3.getStatus() == 200) { //正确返回
        	JSONObject ret3 = JSON.parseObject(resp3.getBody().toString());
	        if(ret3.getIntValue("code") == 0) {
	        	JSONObject okObj = ret3.getJSONObject("data");
	        	JSONArray okArr = okObj.getJSONArray("sell");
	        	if(okArr.size() > 0) {
	        		JSONObject okObj2 = okArr.getJSONObject(0);
	        		setUsdtCnyRate(new BigDecimal(okObj2.getDouble("price")).setScale(2, RoundingMode.HALF_UP));
	        		return;
	        	}
	        }
        }
    }
    
    /**
     * 每30分钟同步一次价格
     *
     * @throws UnirestException
     */
    
    @Scheduled(cron = "0 */30 * * * *")
    public void syncPrice() throws UnirestException {
    	
        String url = "http://web.juhe.cn:8080/finance/exchange/frate?key=444af6d32012064c4654cf87c30ce537";
        //如有报错 请自行官网申请获取汇率 或者默认写死
        HttpResponse<JsonNode> resp = Unirest.get(url)
                .queryString("key", "444af6d32012064c4654cf87c30ce537")
                .asJson();
        log.info("forex result:{}", resp.getBody());
        JSONObject ret = JSON.parseObject(resp.getBody().toString());
        
        if(ret.getIntValue("resultcode") == 200) {
	        JSONArray result = ret.getJSONArray("result");
	        result.forEach(json -> {
	            JSONObject obj = (JSONObject) json;
	            if ("USDCNY".equals(obj.getString("code"))) {
	                setUsdCnyRate(new BigDecimal(obj.getDouble("price")).setScale(2, RoundingMode.DOWN));
	                log.info(obj.toString());
	            } else if ("USDJPY".equals(obj.getString("code"))) {
	                setUsdJpyRate(new BigDecimal(obj.getDouble("price")).setScale(2, RoundingMode.DOWN));
	                log.info(obj.toString());
	            }
	            
	            /* 
	            else if ("USDHKD".equals(obj.getString("code"))) {
	                setUsdHkdRate(new BigDecimal(obj.getDouble("price")).setScale(2, RoundingMode.DOWN));
	                log.info(obj.toString());
	            } else if("SGDCNH".equals(obj.getString("code"))){
	                setSgdCnyRate(new BigDecimal(obj.getDouble("price")).setScale(2,RoundingMode.DOWN));
	                log.info(obj.toString());
	            }
	            */
	            
	        });
        }
    }
}
