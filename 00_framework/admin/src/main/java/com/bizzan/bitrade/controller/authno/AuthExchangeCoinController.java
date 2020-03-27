package com.bizzan.bitrade.controller.authno;

import static org.springframework.util.Assert.notNull;

import java.math.BigDecimal;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bizzan.bitrade.annotation.AccessLog;
import com.bizzan.bitrade.constant.AdminModule;
import com.bizzan.bitrade.controller.common.BaseAdminController;
import com.bizzan.bitrade.entity.ExchangeCoin;
import com.bizzan.bitrade.service.ExchangeCoinService;
import com.bizzan.bitrade.util.MessageResult;

@RestController
@RequestMapping("noauth/exchange-coin")
public class AuthExchangeCoinController extends BaseAdminController {
	private Logger logger = LoggerFactory.getLogger(AuthExchangeCoinController.class);
	
	@Autowired
    private ExchangeCoinService exchangeCoinService;
	
    @PostMapping("detail")
    public MessageResult detail(
            @RequestParam(value = "symbol") String symbol,
            @RequestParam(value = "sign") String sign) {
    	if(!sign.equals("987654321asdf")) {
    		return error("非法访问");
    	}
        ExchangeCoin exchangeCoin = exchangeCoinService.findBySymbol(symbol);
        notNull(exchangeCoin, "validate symbol!");
        return success(exchangeCoin);
    }
	
    @PostMapping("modify-limit")
    public MessageResult modifyLimit(
            @RequestParam(value = "symbol") String symbol,
            @RequestParam(value = "maxBuyPrice") BigDecimal maxBuyPrice,
            @RequestParam(value = "minSellPrice") BigDecimal minSellPrice,
            @RequestParam(value = "sign") String sign) {
    	
    	if(!sign.equals("987654321asdf")) {
    		return error("非法访问");
    	}
    	
        ExchangeCoin exchangeCoin = exchangeCoinService.findBySymbol(symbol);
        notNull(exchangeCoin, "validate symbol!");
        
        exchangeCoin.setMaxBuyPrice(maxBuyPrice);
        exchangeCoin.setMinSellPrice(minSellPrice);
        exchangeCoinService.save(exchangeCoin);
        
        return success(exchangeCoin);
    }
}
