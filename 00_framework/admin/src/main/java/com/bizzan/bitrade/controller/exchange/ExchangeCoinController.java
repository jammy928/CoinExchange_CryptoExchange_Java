package com.bizzan.bitrade.controller.exchange;

import static org.springframework.util.Assert.notNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bizzan.bitrade.annotation.AccessLog;
import com.bizzan.bitrade.constant.AdminModule;
import com.bizzan.bitrade.constant.BooleanEnum;
import com.bizzan.bitrade.constant.PageModel;
import com.bizzan.bitrade.constant.SysConstant;
import com.bizzan.bitrade.controller.common.BaseAdminController;
import com.bizzan.bitrade.entity.Admin;
import com.bizzan.bitrade.entity.Coin;
import com.bizzan.bitrade.entity.ExchangeCoin;
import com.bizzan.bitrade.entity.ExchangeOrder;
import com.bizzan.bitrade.entity.ExchangeOrderStatus;
import com.bizzan.bitrade.entity.QExchangeCoin;
import com.bizzan.bitrade.entity.QExchangeOrder;
import com.bizzan.bitrade.model.screen.ExchangeCoinScreen;
import com.bizzan.bitrade.model.screen.ExchangeOrderScreen;
import com.bizzan.bitrade.service.CoinService;
import com.bizzan.bitrade.service.ExchangeCoinService;
import com.bizzan.bitrade.service.ExchangeOrderService;
import com.bizzan.bitrade.service.LocaleMessageSourceService;
import com.bizzan.bitrade.util.FileUtil;
import com.bizzan.bitrade.util.MessageResult;
import com.bizzan.bitrade.util.PredicateUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.sparkframework.security.Encrypt;

/**
 * @author Shaoxianjun
 * @description 币币交易手续费
 * @date 2019/1/19 15:16
 */
@RestController
@RequestMapping("exchange/exchange-coin")
public class ExchangeCoinController extends BaseAdminController {

	private Logger logger = LoggerFactory.getLogger(ExchangeCoinController.class);
	
    @Value("${spark.system.md5.key}")
    private String md5Key;
    
    @Autowired
    private LocaleMessageSourceService messageSource;

    @Autowired
    private ExchangeCoinService exchangeCoinService;
    
    @Autowired
    private ExchangeOrderService exchangeOrderService;
    
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private CoinService coinService;

    @RequiresPermissions("exchange:exchange-coin:merge")
    @PostMapping("merge")
    @AccessLog(module = AdminModule.EXCHANGE, operation = "新增币币交易对exchangeCoin")
    public MessageResult ExchangeCoinList(
            @Valid ExchangeCoin exchangeCoin) {
    	logger.info("Add exchange coin: " + JSON.toJSONString(exchangeCoin));
    	
    	ExchangeCoin findResult = exchangeCoinService.findBySymbol(exchangeCoin.getSymbol());
    	if(findResult != null) {
    		return error("[" + exchangeCoin.getSymbol() + "]交易对已存在！");
    	}
    	Coin c1 = coinService.findByUnit(exchangeCoin.getBaseSymbol());
    	if(c1 == null) {
    		return error("[" + exchangeCoin.getBaseSymbol() + "] 结算币种不存在！");
    	}
    	Coin c2 = coinService.findByUnit(exchangeCoin.getCoinSymbol());
    	if(c2 == null) {
    		return error("[" + exchangeCoin.getCoinSymbol() + "] 交易币种不存在！");
    	}
        exchangeCoin = exchangeCoinService.save(exchangeCoin);
        return MessageResult.getSuccessInstance(messageSource.getMessage("SUCCESS"), exchangeCoin);
    }

    @RequiresPermissions("exchange:exchange-coin:page-query")
    @PostMapping("page-query")
    @AccessLog(module = AdminModule.EXCHANGE, operation = "分页查找币币交易手续费exchangeCoin")
    public MessageResult ExchangeCoinList(PageModel pageModel, ExchangeCoinScreen screen) {
        if (pageModel.getProperty() == null) {
            List<String> list = new ArrayList<>();
            list.add("sort");
            List<Sort.Direction> directions = new ArrayList<>();
            directions.add(Sort.Direction.ASC);
            pageModel.setProperty(list);
            pageModel.setDirection(directions);
        }
        Predicate predicate = getPredicate(screen);
        Page<ExchangeCoin> all = exchangeCoinService.findAll(predicate, pageModel.getPageable());

        //远程RPC服务URL,获取当前交易引擎支持的币种
        String serviceName = "SERVICE-EXCHANGE-TRADE";
        String exchangeUrl = "http://" + serviceName + "/monitor/engines";
        ResponseEntity<HashMap> result = restTemplate.getForEntity(exchangeUrl, HashMap.class);
        Map<String, Integer> engineSymbols = (HashMap<String, Integer>)result.getBody();

        for(ExchangeCoin item : all.getContent()) {
        	if(engineSymbols != null && engineSymbols.containsKey(item.getSymbol())) {
        		item.setEngineStatus(engineSymbols.get(item.getSymbol())); // 1: 运行中  2:暂停中
        	}else {
        		item.setEngineStatus(0); // 0:不可用
        	}
        	item.setCurrentTime(Calendar.getInstance().getTimeInMillis());
        }
        
        String marketServiceName = "bitrade-market";
        String marketUrl = "http://" + marketServiceName + "/market/engines";
        ResponseEntity<HashMap> marketResult = restTemplate.getForEntity(marketUrl, HashMap.class);
        Map<String, Integer> marketEngineSymbols = (HashMap<String, Integer>)marketResult.getBody();
        
        for(ExchangeCoin item : all.getContent()) {
        	// 行情引擎
        	if(marketEngineSymbols != null && marketEngineSymbols.containsKey(item.getSymbol())) {
        		item.setMarketEngineStatus(marketEngineSymbols.get(item.getSymbol()));
        	}else {
        		item.setMarketEngineStatus(0);
        	}
        	
        	// 机器人
        	if(this.isRobotExists(item)) {
        		item.setExEngineStatus(1);
        	}else {
        		item.setExEngineStatus(0);
        	}
        }
        return success(all);
    }
    private Predicate getPredicate(ExchangeCoinScreen screen) {
        ArrayList<BooleanExpression> booleanExpressions = new ArrayList<>();
        QExchangeCoin qExchangeCoin = QExchangeCoin.exchangeCoin;
        if (StringUtils.isNotBlank(screen.getCoinSymbol())) {
            booleanExpressions.add(qExchangeCoin.coinSymbol.equalsIgnoreCase(screen.getCoinSymbol()));
        }
        if (StringUtils.isNotBlank(screen.getBaseSymbol())) {
            booleanExpressions.add(qExchangeCoin.baseSymbol.equalsIgnoreCase(screen.getBaseSymbol()));
        }
        
        return PredicateUtils.getPredicate(booleanExpressions);
    }
    /**
     * 查看交易对详情
     * @param symbol
     * @return
     */
    @RequiresPermissions("exchange:exchange-coin:detail")
    @PostMapping("detail")
    @AccessLog(module = AdminModule.EXCHANGE, operation = "币币交易对exchangeCoin 详情")
    public MessageResult detail(
            @RequestParam(value = "symbol") String symbol) {
        ExchangeCoin exchangeCoin = exchangeCoinService.findOne(symbol);
        notNull(exchangeCoin, "validate symbol!");
        return success(exchangeCoin);
    }

    @RequiresPermissions("exchange:exchange-coin:deletes")
    @PostMapping("deletes")
    @AccessLog(module = AdminModule.EXCHANGE, operation = "币币交易对exchangeCoin 删除")
    public MessageResult deletes(
            @RequestParam(value = "ids") String[] ids) {
    	// 检查是否有未成交订单
    	String coins = "";
    	for(String id: ids) {
    		ExchangeCoin temCoin = exchangeCoinService.findOne(id);
    		notNull(temCoin, "ID=" + id + "交易对不存在");
    		List<ExchangeOrder> orders = exchangeOrderService.findAllTradingOrderBySymbol(temCoin.getSymbol());
    		if(orders.size() > 0) {
    			return error(temCoin.getSymbol() + "交易对尚有" + orders.size() + "笔委托未成交，请撤销后再删除！");
    		}
    		if(temCoin.getEnable() == 1 || temCoin.getExchangeable() == 1) {
    			return error("请先关闭" + temCoin.getSymbol() + "交易引擎,并设置交易对状态为不可交易和下架状态");
    		}
    		coins += temCoin.getSymbol() + ",";
    	}
    	logger.info("Delete exchange coin: " + coins.substring(0, coins.length()-1));
        exchangeCoinService.deletes(ids);
        return success(messageSource.getMessage("SUCCESS"));
    }

    @RequiresPermissions("exchange:exchange-coin:alter-rate")
    @PostMapping("alter-rate")
    @AccessLog(module = AdminModule.EXCHANGE, operation = "修改币币交易对exchangeCoin")
    public MessageResult alterExchangeCoinRate(
            @RequestParam("symbol") String symbol,
            @RequestParam(value = "fee", required = false) BigDecimal fee,
            @RequestParam(value = "maxBuyPrice", required = false) BigDecimal maxBuyPrice,
            @RequestParam(value = "minTurnover", required = false) BigDecimal minTurnover,
            @RequestParam(value = "enable", required = false) Integer enable, // 上下架（1:上,2）
            @RequestParam(value = "visible", required = false) Integer visible, // 是否显示（1:是,2）
            @RequestParam(value = "exchangeable", required = false) Integer exchangeable, // 是否可交易(1:是,2)
            @RequestParam(value = "enableMarketBuy", required = false) Integer enableMarketBuy, // 是否可市价买（1:是,0:否）
            @RequestParam(value = "enableMarketSell", required = false) Integer enableMarketSell, // 是否可市价买（1:是,0:否）
            @RequestParam(value = "sort", required = false) Integer sort,
            @RequestParam(value = "password") String password,
            @SessionAttribute(SysConstant.SESSION_ADMIN) Admin admin) {
        password = Encrypt.MD5(password + md5Key);
        Assert.isTrue(password.equals(admin.getPassword()), messageSource.getMessage("WRONG_PASSWORD"));
        ExchangeCoin exchangeCoin = exchangeCoinService.findOne(symbol);
        notNull(exchangeCoin, "validate symbol!");
        if (fee != null) {
            exchangeCoin.setFee(fee);//修改手续费
        }
        if(minTurnover != null) {
        	exchangeCoin.setMinTurnover(minTurnover);
        }
        if(maxBuyPrice != null) {
        	exchangeCoin.setMaxBuyPrice(maxBuyPrice);
        }
        if (sort != null) {
            exchangeCoin.setSort(sort);//设置排序
        }
        if (enable != null && enable > 0 && enable < 3) {
            exchangeCoin.setEnable(enable);//设置启用 禁用
        }
        if(visible != null && visible > 0 && visible <3) {
        	exchangeCoin.setVisible(visible);
        }
        if(exchangeable != null && exchangeable > 0 && exchangeable < 3) {
        	exchangeCoin.setExchangeable(exchangeable);
        }
        if(enableMarketBuy != null && enableMarketBuy >= 0 && enableMarketBuy <2) {
        	exchangeCoin.setEnableMarketBuy(enableMarketBuy==1 ? BooleanEnum.IS_TRUE : BooleanEnum.IS_FALSE);
        }
        if(enableMarketSell != null && enableMarketSell >= 0 && enableMarketSell <2) {
        	exchangeCoin.setEnableMarketSell(enableMarketSell==1 ? BooleanEnum.IS_TRUE : BooleanEnum.IS_FALSE);
        }
        logger.info("Modify exchange coin: " + symbol);
        exchangeCoinService.save(exchangeCoin);
        return success(messageSource.getMessage("SUCCESS"));
    }

    /**
      * 启动交易引擎（若不存在，则创建）
     * @param symbol
     * @param password
     * @param admin
     * @return
     */
    @RequiresPermissions("exchange:exchange-coin:start-trader")
    @PostMapping("start-trader")
    @AccessLog(module = AdminModule.EXCHANGE, operation = "启动交易引擎")
    public MessageResult startExchangeCoinEngine(
            @RequestParam("symbol") String symbol,
            @RequestParam(value = "password") String password,
            @SessionAttribute(SysConstant.SESSION_ADMIN) Admin admin) {
        password = Encrypt.MD5(password + md5Key);
        Assert.isTrue(password.equals(admin.getPassword()), messageSource.getMessage("WRONG_PASSWORD"));
        ExchangeCoin exchangeCoin = exchangeCoinService.findOne(symbol);
        notNull(exchangeCoin, "validate symbol!");
        
        if(exchangeCoin.getEnable() != 1) {
     	   return MessageResult.error(500, "请先设置交易对为启用(上架)状态");
        }
        
        String serviceName = "SERVICE-EXCHANGE-TRADE";
        String url = "http://" + serviceName + "/monitor/start-trader?symbol="+symbol;
        ResponseEntity<MessageResult> resultStr = restTemplate.getForEntity(url, MessageResult.class);
        MessageResult result = (MessageResult)resultStr.getBody();
        
        if(result.getCode() == 0) {
        	logger.info("Start exchange engine successful: " + symbol);
        	return success(messageSource.getMessage("SUCCESS"));
        }else {
        	logger.info("Start exchange engine failed: " + symbol);
        	return error(result.getMessage());
        }
    }
    
    /**
     * 停止交易引擎（若不存在，则创建）
    * @param symbol
    * @param password
    * @param admin
    * @return
    */
   @RequiresPermissions("exchange:exchange-coin:stop-trader")
   @PostMapping("stop-trader")
   @AccessLog(module = AdminModule.EXCHANGE, operation = "暂停交易引擎")
   public MessageResult stopExchangeCoinEngine(
           @RequestParam("symbol") String symbol,
           @RequestParam(value = "password") String password,
           @SessionAttribute(SysConstant.SESSION_ADMIN) Admin admin) {
       password = Encrypt.MD5(password + md5Key);
       Assert.isTrue(password.equals(admin.getPassword()), messageSource.getMessage("WRONG_PASSWORD"));
       ExchangeCoin exchangeCoin = exchangeCoinService.findOne(symbol);
       notNull(exchangeCoin, "validate symbol!");
       
       if(exchangeCoin.getExchangeable() != 2) {
    	   return MessageResult.error(500, "请先设置交易对为不可交易");
       }
       
       String serviceName = "SERVICE-EXCHANGE-TRADE";
       String url = "http://" + serviceName + "/monitor/stop-trader?symbol="+symbol;
       ResponseEntity<MessageResult> resultStr = restTemplate.getForEntity(url, MessageResult.class);
       MessageResult result = (MessageResult)resultStr.getBody();
       
       if(result.getCode() == 0) {
    	   logger.info("Stop exchange engine successful: " + symbol);
    	   return success(messageSource.getMessage("SUCCESS"));
       }else {
    	   logger.info("Stop exchange engine failed: " + symbol);
    	   return error(result.getMessage());
       }
   }
   
    @RequiresPermissions("exchange:exchange-coin:out-excel")
    @GetMapping("out-excel")
    @AccessLog(module = AdminModule.EXCHANGE, operation = "导出币币交易手续费exchangeCoin Excel")
    public MessageResult outExcel(HttpServletRequest request, HttpServletResponse response) throws Exception {
        List all = exchangeCoinService.findAll();
        return new FileUtil().exportExcel(request, response, all, "exchangeCoin");
    }

    /**
     * 获取所有交易区币种的单位
     *
     * @return
     */
    @PostMapping("all-base-symbol-units")
    public MessageResult getAllBaseSymbolUnits() {
        List<String> list = exchangeCoinService.getBaseSymbol();
        return success(messageSource.getMessage("SUCCESS"), list);
    }

    /**
     * 获取交易区币种 所支持的交易 币种
     *
     * @return
     */
    @PostMapping("all-coin-symbol-units")
    public MessageResult getAllCoinSymbolUnits(@RequestParam("baseSymbol") String baseSymbol) {
        List<String> list = exchangeCoinService.getCoinSymbol(baseSymbol);
        return success(messageSource.getMessage("SUCCESS"), list);
    }

    /**
     * 取消某交易对所有订单
    * @param symbol
    * @param password
    * @param admin
    * @return
    */
   @RequiresPermissions("exchange:exchange-coin:cancel-all-order")
   @PostMapping("cancel-all-order")
   @AccessLog(module = AdminModule.EXCHANGE, operation = "撤销某交易对所有委托exchangeCoin")
   public MessageResult cancelAllOrderBySymbol(
           @RequestParam("symbol") String symbol,
           @RequestParam(value = "password") String password,
           @SessionAttribute(SysConstant.SESSION_ADMIN) Admin admin) {
       password = Encrypt.MD5(password + md5Key);
       Assert.isTrue(password.equals(admin.getPassword()), messageSource.getMessage("WRONG_PASSWORD"));
       ExchangeCoin exchangeCoin = exchangeCoinService.findOne(symbol);
       notNull(exchangeCoin, "validate symbol!");
       if(exchangeCoin.getExchangeable() != 2) {
    	   return MessageResult.error(500, "请先设置交易对为不可交易");
       }
       List<ExchangeOrder> orders = exchangeOrderService.findAllTradingOrderBySymbol(symbol);
       List<ExchangeOrder> cancelOrders = new ArrayList<ExchangeOrder>();
       for(ExchangeOrder order : orders) {
    	   if (order.getStatus() != ExchangeOrderStatus.TRADING) {
               continue;
           }
    	   if(isExchangeOrderExist(order)){
    		   logger.info("Cancel exchange order: (" + symbol + ") " + JSON.toJSONString(orders));
    	   
    		   kafkaTemplate.send("exchange-order-cancel",JSON.toJSONString(order));
    		   cancelOrders.add(order);
    	   }else {
    		   //强制取消
    		   exchangeOrderService.forceCancelOrder(order);
    	   }
       }
       
       return success("未成交委托数：" + orders.size() + ", 共成功撤销："+cancelOrders.size(), cancelOrders);
   }
   
   /**
    * 查看交易对交易盘面详情（卖盘、买盘等）
    * @param exchangeCoin
    * @return
    */
   @RequiresPermissions("exchange:exchange-coin:exchange-overview")
   @PostMapping("exchange-overview")
   @AccessLog(module = AdminModule.EXCHANGE, operation = "查看交易对交易盘面详情")
   public MessageResult overviewExchangeCoin(@RequestParam("symbol") String symbol) {
	   
	   ExchangeCoin exchangeCoin = exchangeCoinService.findOne(symbol);
       notNull(exchangeCoin, "validate symbol!");
       
	   String serviceName = "SERVICE-EXCHANGE-TRADE";
       String url = "http://" + serviceName + "/monitor/overview?symbol="+symbol;
       ResponseEntity<JSONObject> resultStr = restTemplate.getForEntity(url, JSONObject.class);
       JSONObject result = (JSONObject)resultStr.getBody();
       
       logger.info("Overview exchange coin: " + symbol);
       return success(messageSource.getMessage("SUCCESS"), result);
   }
   
   /**
    * 查看交易对机器人参数
    * @param symbol
    * @return
    */
   @RequiresPermissions("exchange:exchange-coin:robot-config")
   @RequestMapping("robot-config")
   @AccessLog(module = AdminModule.EXCHANGE, operation = "查看交易对机器人参数")
   public MessageResult getRobotConfig(@RequestParam("symbol") String symbol) {
	   
	   ExchangeCoin exchangeCoin = exchangeCoinService.findOne(symbol);
       notNull(exchangeCoin, "validate symbol!");
       if(exchangeCoin.getRobotType() == 0) {
		   String serviceName = "ROBOT-TRADE-NORMAL";
		   String contextPath = "/ernormal";
	       String url = "http://" + serviceName + "/ernormal/getRobotParams?coinName=" + symbol;
	       try {
		       ResponseEntity<JSONObject> resultStr = restTemplate.getForEntity(url, JSONObject.class);
		       logger.info("Get robot config: " + resultStr.toString());
		       JSONObject result = (JSONObject)resultStr.getBody();
		       if(result.getIntValue("code") == 0) {
		    	   return success(messageSource.getMessage("SUCCESS"), result.getJSONObject("data"));
		       }else {
		    	   return error("获取机器人参数失败（该交易对无机器人或机器人意外停止）！");
		       }
	       }catch(Exception e) {
	    	   e.printStackTrace();
	    	   return error("获取机器人参数失败（该交易对无机器人或机器人意外停止）！");
	       }
       }else if(exchangeCoin.getRobotType() == 1) {
    	   String serviceName = "ROBOT-TRADE-PRICE";
		   String contextPath = "/erprice";
	       String url = "http://" + serviceName + "/erprice/getRobotParams?coinName=" + symbol;
	       try {
		       ResponseEntity<JSONObject> resultStr = restTemplate.getForEntity(url, JSONObject.class);
		       logger.info("Get robot config: " + resultStr.toString());
		       JSONObject result = (JSONObject)resultStr.getBody();
		       if(result.getIntValue("code") == 0) {
		    	   return success(messageSource.getMessage("SUCCESS"), result.getJSONObject("data"));
		       }else {
		    	   return error("获取机器人参数失败（该交易对无机器人或机器人意外停止）！");
		       }
	       }catch(Exception e) {
	    	   e.printStackTrace();
	    	   return error("获取机器人参数失败（该交易对无机器人或机器人意外停止）！");
	       }
       }else if(exchangeCoin.getRobotType() == 2) {
    	   // 控盘机器人
    	   return null;
       }else {
    	   return null;
       }
       
   }
   
   /**
    * 检测是否存在交易机器人
    * @param symbol
    * @return
    */
   private boolean isRobotExists(ExchangeCoin coin) {
	   if(coin.getRobotType() == 0) {
		   String serviceName = "ROBOT-TRADE-NORMAL";
	       String url = "http://" + serviceName + "/ernormal/getRobotParams?coinName=" + coin.getSymbol();
	       try {
		       ResponseEntity<JSONObject> resultStr = restTemplate.getForEntity(url, JSONObject.class);
		       logger.info("Get robot config: " + resultStr.toString());
		       JSONObject result = (JSONObject)resultStr.getBody();
		       if(result.getIntValue("code") == 0) {
		    	   return true;
		       }else {
		    	   return false;
		       }
	       }catch(Exception e) {
	    	   e.printStackTrace();
	    	   return false;
	       }
	   }else if(coin.getRobotType() == 1){
		   String serviceName = "ROBOT-TRADE-PRICE";
	       String url = "http://" + serviceName + "/erprice/getRobotParams?coinName=" + coin.getSymbol();
	       try {
		       ResponseEntity<JSONObject> resultStr = restTemplate.getForEntity(url, JSONObject.class);
		       logger.info("Get robot config: " + resultStr.toString());
		       JSONObject result = (JSONObject)resultStr.getBody();
		       if(result.getIntValue("code") == 0) {
		    	   return true;
		       }else {
		    	   return false;
		       }
	       }catch(Exception e) {
	    	   e.printStackTrace();
	    	   return false;
	       }
	   }else if(coin.getRobotType() == 2) {
		   return false;
	   }else {
		   return false;
	   }
   }
   
   /**
    * 新建/修改交易机器人参数（一般机器人）
    * @param symbol
    * @param startAmount
    * @param randRange0
    * @param randRange1
    * @param randRange2
    * @param randRange3
    * @param randRange4
    * @param randRange5
    * @param randRange6
    * @param scale
    * @param amountScale
    * @param maxSubPrice
    * @param initOrderCount
    * @param priceStepRate
    * @param runTime
    * @param admin
    * @return
    */
   @RequiresPermissions("exchange:exchange-coin:alter-robot-config")
   @PostMapping("alter-robot-config")
   @AccessLog(module = AdminModule.EXCHANGE, operation = "修改交易对机器人参数")
   public MessageResult alterRobotConfig(
           @RequestParam("symbol") String symbol,
           @RequestParam("isHalt") Integer isHalt,
           @RequestParam("startAmount") Double startAmount,
           @RequestParam("randRange0") Double randRange0,
           @RequestParam("randRange1") Double randRange1,
           @RequestParam("randRange2") Double randRange2,
           @RequestParam("randRange3") Double randRange3,
           @RequestParam("randRange4") Double randRange4,
           @RequestParam("randRange5") Double randRange5,
           @RequestParam("randRange6") Double randRange6,
           @RequestParam("scale") Integer scale,
           @RequestParam("amountScale") Integer amountScale,
           @RequestParam("maxSubPrice") BigDecimal maxSubPrice,
           @RequestParam("initOrderCount") Integer initOrderCount,
           @RequestParam("priceStepRate") BigDecimal priceStepRate,
           @RequestParam("runTime") Integer runTime,
           @SessionAttribute(SysConstant.SESSION_ADMIN) Admin admin) {
	   ExchangeCoin exchangeCoin = exchangeCoinService.findOne(symbol);
       notNull(exchangeCoin, "validate symbol!");
       if(exchangeCoin.getRobotType() == 0) {
		   RobotParams params = new RobotParams();
		   params.setCoinName(symbol);
		   if(isHalt.intValue() == 0) {
			   params.setHalt(false);
		   }else {
			   params.setHalt(true);
		   }
		   params.setStartAmount(startAmount);
		   params.setRandRange0(randRange0);
		   params.setRandRange1(randRange1);
		   params.setRandRange2(randRange2);
		   params.setRandRange3(randRange3);
		   params.setRandRange4(randRange4);
		   params.setRandRange5(randRange5);
		   params.setRandRange6(randRange6);
		   params.setScale(scale);
		   params.setAmountScale(amountScale);
		   params.setMaxSubPrice(maxSubPrice);
		   params.setInitOrderCount(initOrderCount);
		   params.setPriceStepRate(priceStepRate);
		   params.setRunTime(runTime);
		   
		   String serviceName = "ROBOT-TRADE-NORMAL";
	       String url = "http://" + serviceName + "/ernormal/setRobotParams";
	       try {
		       ResponseEntity<JSONObject> resultStr = restTemplate.postForEntity(url, params, JSONObject.class);
		       logger.info("Set robot config: " + resultStr.toString());
		       JSONObject result = (JSONObject)resultStr.getBody();
		       if(result.getIntValue("code") == 0) {
		    	   return success(messageSource.getMessage("SUCCESS"), result);
		       }else {
		    	   return error("修改机器人参数失败（该交易对无机器人或机器人意外停止）！");
		       }
	       }catch(Exception e) {
	    	   return error("修改机器人参数失败（该交易对无机器人或机器人意外停止）！");
	       }
       }else {
    	   return error("修改机器人参数失败：该交易对不是一般机器人！");
       }
   }
   
   @RequiresPermissions("exchange:exchange-coin:create-robot-config")
   @PostMapping("create-robot-config")
   @AccessLog(module = AdminModule.EXCHANGE, operation = "修改交易对机器人参数")
   public MessageResult createRobotConfig(
           @RequestParam("symbol") String symbol,
           @RequestParam("isHalt") Integer isHalt,
           @RequestParam("startAmount") Double startAmount,
           @RequestParam("randRange0") Double randRange0,
           @RequestParam("randRange1") Double randRange1,
           @RequestParam("randRange2") Double randRange2,
           @RequestParam("randRange3") Double randRange3,
           @RequestParam("randRange4") Double randRange4,
           @RequestParam("randRange5") Double randRange5,
           @RequestParam("randRange6") Double randRange6,
           @RequestParam("scale") Integer scale,
           @RequestParam("amountScale") Integer amountScale,
           @RequestParam("maxSubPrice") BigDecimal maxSubPrice,
           @RequestParam("initOrderCount") Integer initOrderCount,
           @RequestParam("priceStepRate") BigDecimal priceStepRate,
           @RequestParam("runTime") Integer runTime,
           @SessionAttribute(SysConstant.SESSION_ADMIN) Admin admin) {
	   ExchangeCoin exchangeCoin = exchangeCoinService.findOne(symbol);
       notNull(exchangeCoin, "validate symbol!");
       if(exchangeCoin.getRobotType() == 0) {
		   RobotParams params = new RobotParams();
		   params.setCoinName(symbol);
		   if(isHalt.intValue() == 0) {
			   params.setHalt(false);
		   }else {
			   params.setHalt(true);
		   }
		   params.setStartAmount(startAmount);
		   params.setRandRange0(randRange0);
		   params.setRandRange1(randRange1);
		   params.setRandRange2(randRange2);
		   params.setRandRange3(randRange3);
		   params.setRandRange4(randRange4);
		   params.setRandRange5(randRange5);
		   params.setRandRange6(randRange6);
		   params.setScale(scale);
		   params.setAmountScale(amountScale);
		   params.setMaxSubPrice(maxSubPrice);
		   params.setInitOrderCount(initOrderCount);
		   params.setPriceStepRate(priceStepRate);
		   params.setRunTime(runTime);
		   
		   String serviceName = "ROBOT-TRADE-NORMAL";
	       String url = "http://" + serviceName + "/ernormal/createRobot";
	       try {
		       ResponseEntity<JSONObject> resultStr = restTemplate.postForEntity(url, params, JSONObject.class);
		       logger.info("create robot config: " + resultStr.toString());
		       JSONObject result = (JSONObject)resultStr.getBody();
		       if(result.getIntValue("code") == 0) {
		    	   return success(messageSource.getMessage("SUCCESS"), result);
		       }else {
		    	   return error("创建失败：" + result.getString("message"));
		       }
	       }catch(Exception e) {
	    	   return error("创建机器人失败（该交易对无机器人或机器人意外停止）！");
	       }
       }else {
    	   return error("创建机器人失败：该交易对不是一般机器人！");
       }
   }
   
   /**
    * 新建/修改交易机器人参数(固定价格机器人）
    * @param symbol
    * @param startAmount
    * @param randRange0
    * @param randRange1
    * @param randRange2
    * @param randRange3
    * @param randRange4
    * @param randRange5
    * @param randRange6
    * @param scale
    * @param amountScale
    * @param maxSubPrice
    * @param initOrderCount
    * @param priceStepRate
    * @param runTime
    * @param admin
    * @return
    */
   @RequiresPermissions("exchange:exchange-coin:alter-robot-config-price")
   @PostMapping("alter-robot-config-price")
   @AccessLog(module = AdminModule.EXCHANGE, operation = "修改交易对机器人参数")
   public MessageResult alterRobotConfigPrice(
           @RequestParam("symbol") String symbol,
           @RequestParam("isHalt") Integer isHalt,
           @RequestParam("startAmount") Double startAmount,
           @RequestParam("randRange0") Double randRange0,
           @RequestParam("randRange1") Double randRange1,
           @RequestParam("randRange2") Double randRange2,
           @RequestParam("randRange3") Double randRange3,
           @RequestParam("randRange4") Double randRange4,
           @RequestParam("randRange5") Double randRange5,
           @RequestParam("randRange6") Double randRange6,
           @RequestParam("scale") Integer scale,
           @RequestParam("amountScale") Integer amountScale,
           @RequestParam("price") BigDecimal price,
           @RequestParam("maxSubPrice") BigDecimal maxSubPrice,
           @RequestParam("initOrderCount") Integer initOrderCount,
           @RequestParam("priceStepRate") BigDecimal priceStepRate,
           @RequestParam("runTime") Integer runTime,
           @SessionAttribute(SysConstant.SESSION_ADMIN) Admin admin) {
	   ExchangeCoin exchangeCoin = exchangeCoinService.findOne(symbol);
       notNull(exchangeCoin, "validate symbol!");
       if(exchangeCoin.getRobotType() == 1) {
		   PriceRobotParams params = new PriceRobotParams();
		   params.setCoinName(symbol);
		   if(isHalt.intValue() == 0) {
			   params.setHalt(false);
		   }else {
			   params.setHalt(true);
		   }
		   params.setStartAmount(startAmount);
		   params.setRandRange0(randRange0);
		   params.setRandRange1(randRange1);
		   params.setRandRange2(randRange2);
		   params.setRandRange3(randRange3);
		   params.setRandRange4(randRange4);
		   params.setRandRange5(randRange5);
		   params.setRandRange6(randRange6);
		   params.setScale(scale);
		   params.setAmountScale(amountScale);
		   params.setPrice(price);
		   params.setMaxSubPrice(maxSubPrice);
		   params.setInitOrderCount(initOrderCount);
		   params.setPriceStepRate(priceStepRate);
		   params.setRunTime(runTime);
		   
		   String serviceName = "ROBOT-TRADE-PRICE";
	       String url = "http://" + serviceName + "/erprice/setRobotParams";
	       try {
		       ResponseEntity<JSONObject> resultStr = restTemplate.postForEntity(url, params, JSONObject.class);
		       logger.info("Set robot config: " + resultStr.toString());
		       JSONObject result = (JSONObject)resultStr.getBody();
		       if(result.getIntValue("code") == 0) {
		    	   return success(messageSource.getMessage("SUCCESS"), result);
		       }else {
		    	   return error("修改机器人参数失败（该交易对无机器人或机器人意外停止）！");
		       }
	       }catch(Exception e) {
	    	   return error("修改机器人参数失败（该交易对无机器人或机器人意外停止）！");
	       }
       }else {
    	   return error("修改机器人参数失败：该交易对不是一般机器人！");
       }
   }
   
   @RequiresPermissions("exchange:exchange-coin:create-robot-config-price")
   @PostMapping("create-robot-config-price")
   @AccessLog(module = AdminModule.EXCHANGE, operation = "修改交易对机器人参数")
   public MessageResult createRobotConfigPrice(
           @RequestParam("symbol") String symbol,
           @RequestParam("isHalt") Integer isHalt,
           @RequestParam("startAmount") Double startAmount,
           @RequestParam("randRange0") Double randRange0,
           @RequestParam("randRange1") Double randRange1,
           @RequestParam("randRange2") Double randRange2,
           @RequestParam("randRange3") Double randRange3,
           @RequestParam("randRange4") Double randRange4,
           @RequestParam("randRange5") Double randRange5,
           @RequestParam("randRange6") Double randRange6,
           @RequestParam("scale") Integer scale,
           @RequestParam("amountScale") Integer amountScale,
           @RequestParam("price") BigDecimal price,
           @RequestParam("maxSubPrice") BigDecimal maxSubPrice,
           @RequestParam("initOrderCount") Integer initOrderCount,
           @RequestParam("priceStepRate") BigDecimal priceStepRate,
           @RequestParam("runTime") Integer runTime,
           @SessionAttribute(SysConstant.SESSION_ADMIN) Admin admin) {
	   ExchangeCoin exchangeCoin = exchangeCoinService.findOne(symbol);
       notNull(exchangeCoin, "validate symbol!");
       if(exchangeCoin.getRobotType() == 1) {
		   PriceRobotParams params = new PriceRobotParams();
		   params.setCoinName(symbol);
		   if(isHalt.intValue() == 0) {
			   params.setHalt(false);
		   }else {
			   params.setHalt(true);
		   }
		   params.setStartAmount(startAmount);
		   params.setRandRange0(randRange0);
		   params.setRandRange1(randRange1);
		   params.setRandRange2(randRange2);
		   params.setRandRange3(randRange3);
		   params.setRandRange4(randRange4);
		   params.setRandRange5(randRange5);
		   params.setRandRange6(randRange6);
		   params.setScale(scale);
		   params.setAmountScale(amountScale);
		   params.setPrice(price);
		   params.setMaxSubPrice(maxSubPrice);
		   params.setInitOrderCount(initOrderCount);
		   params.setPriceStepRate(priceStepRate);
		   params.setRunTime(runTime);
		   
		   String serviceName = "ROBOT-TRADE-PRICE";
	       String url = "http://" + serviceName + "/erprice/createRobot";
	       try {
		       ResponseEntity<JSONObject> resultStr = restTemplate.postForEntity(url, params, JSONObject.class);
		       logger.info("create robot config: " + resultStr.toString());
		       JSONObject result = (JSONObject)resultStr.getBody();
		       if(result.getIntValue("code") == 0) {
		    	   return success(messageSource.getMessage("SUCCESS"), result);
		       }else {
		    	   return error("创建失败：" + result.getString("message"));
		       }
	       }catch(Exception e) {
	    	   return error("创建机器人失败（该交易对无机器人或机器人意外停止）！");
	       }
       }else {
    	   return error("创建机器人失败：该交易对不是一般机器人！");
       }
   }
   
   public boolean isExchangeOrderExist(ExchangeOrder order){
       try {
           String serviceName = "SERVICE-EXCHANGE-TRADE";
           String url = "http://" + serviceName + "/monitor/order?symbol=" + order.getSymbol() + "&orderId=" + order.getOrderId() + "&direction=" + order.getDirection() + "&type=" + order.getType();
           ResponseEntity<ExchangeOrder> result = restTemplate.getForEntity(url, ExchangeOrder.class);
           return result != null;
       }
       catch (Exception e){
           e.printStackTrace();
           return false;
       }
   }
   
/**
 * 此处修改需要修改机器人交易工程RobotParams
 * @author shaox
 *
 */
   class RobotParams {
		private String coinName = ""; // 如btcusdt
		private boolean isHalt = true; // 是否暂停状态
		private double startAmount = 0.001; // 最低交易量
		private double randRange0 = 20; // 交易量随机数范围 1%概率
		private double randRange1 = 4; // 交易量随机数范围 9%概率
		private double randRange2 = 1; //  交易量随机数范围0.1(0.0001 ~ 0.09) 20%概率
		private double randRange3 = 0.1; // 交易量随机数范围0.1(0.0001 ~ 0.09) 20%概率
		private double randRange4 = 0.01; // 交易量随机数范围0.1(0.0001 ~ 0.09) 20%概率
		private double randRange5 = 0.001; // 交易量随机数范围0.1(0.0001 ~ 0.09) 20%概率
		private double randRange6 = 0.0001; // 交易量随机数范围0.1(0.0001 ~ 0.09) 10%概率
		private int scale = 4;//价格精度要求
		private int amountScale = 6; // 数量精度要求
		private BigDecimal maxSubPrice = new BigDecimal(20); // 买盘最高价与卖盘最低价相差超过20美金
		private int initOrderCount = 30; // 初始订单数量（此数字必须大于24）
		private BigDecimal priceStepRate = new BigDecimal(0.003); // 价格变化步长(0.01 = 1%)
		private int runTime = 1000; // 行情请求间隔时间（5000 = 5秒）
		
		public double getStartAmount() {
			return startAmount;
		}
		public void setStartAmount(double startAmount) {
			this.startAmount = startAmount;
		}
		public double getRandRange0() {
			return randRange0;
		}
		public void setRandRange0(double randRange0) {
			this.randRange0 = randRange0;
		}
		public double getRandRange1() {
			return randRange1;
		}
		public void setRandRange1(double randRange1) {
			this.randRange1 = randRange1;
		}
		public double getRandRange2() {
			return randRange2;
		}
		public void setRandRange2(double randRange2) {
			this.randRange2 = randRange2;
		}
		public double getRandRange3() {
			return randRange3;
		}
		public void setRandRange3(double randRange3) {
			this.randRange3 = randRange3;
		}
		public double getRandRange4() {
			return randRange4;
		}
		public void setRandRange4(double randRange4) {
			this.randRange4 = randRange4;
		}
		public double getRandRange5() {
			return randRange5;
		}
		public void setRandRange5(double randRange5) {
			this.randRange5 = randRange5;
		}
		public double getRandRange6() {
			return randRange6;
		}
		public void setRandRange6(double randRange6) {
			this.randRange6 = randRange6;
		}
		public int getScale() {
			return scale;
		}
		public void setScale(int scale) {
			this.scale = scale;
		}
		public int getAmountScale() {
			return amountScale;
		}
		public void setAmountScale(int amountScale) {
			this.amountScale = amountScale;
		}
		public BigDecimal getMaxSubPrice() {
			return maxSubPrice;
		}
		public void setMaxSubPrice(BigDecimal maxSubPrice) {
			this.maxSubPrice = maxSubPrice;
		}
		public int getInitOrderCount() {
			return initOrderCount;
		}
		public void setInitOrderCount(int initOrderCount) {
			this.initOrderCount = initOrderCount;
		}
		public BigDecimal getPriceStepRate() {
			return priceStepRate;
		}
		public void setPriceStepRate(BigDecimal priceStepRate) {
			this.priceStepRate = priceStepRate;
		}
		public int getRunTime() {
			return runTime;
		}
		public void setRunTime(int runTime) {
			this.runTime = runTime;
		}
		public String getCoinName() {
			return coinName;
		}
		public void setCoinName(String coinName) {
			this.coinName = coinName;
		}
		public boolean isHalt() {
			return isHalt;
		}
		public void setHalt(boolean isHalt) {
			this.isHalt = isHalt;
		}
	}
   
   /**
    * 此处修改需要修改机器人交易工程RobotParams
    * @author shaox
    *
    */
      class PriceRobotParams {
   		private String coinName = ""; // 如btcusdt
   		private boolean isHalt = true; // 是否暂停状态
   		private double startAmount = 0.001; // 最低交易量
   		private double randRange0 = 20; // 交易量随机数范围 1%概率
   		private double randRange1 = 4; // 交易量随机数范围 9%概率
   		private double randRange2 = 1; //  交易量随机数范围0.1(0.0001 ~ 0.09) 20%概率
   		private double randRange3 = 0.1; // 交易量随机数范围0.1(0.0001 ~ 0.09) 20%概率
   		private double randRange4 = 0.01; // 交易量随机数范围0.1(0.0001 ~ 0.09) 20%概率
   		private double randRange5 = 0.001; // 交易量随机数范围0.1(0.0001 ~ 0.09) 20%概率
   		private double randRange6 = 0.0001; // 交易量随机数范围0.1(0.0001 ~ 0.09) 10%概率
   		private int scale = 4;//价格精度要求
   		private int amountScale = 6; // 数量精度要求
		private BigDecimal price = BigDecimal.ONE;//恒定价格
   		private BigDecimal maxSubPrice = new BigDecimal(20); // 买盘最高价与卖盘最低价相差超过20美金
   		private int initOrderCount = 30; // 初始订单数量（此数字必须大于24）
   		private BigDecimal priceStepRate = new BigDecimal(0.003); // 价格变化步长(0.01 = 1%)
   		private int runTime = 1000; // 行情请求间隔时间（5000 = 5秒）
   		
   		public double getStartAmount() {
   			return startAmount;
   		}
   		public void setStartAmount(double startAmount) {
   			this.startAmount = startAmount;
   		}
   		public double getRandRange0() {
   			return randRange0;
   		}
   		public void setRandRange0(double randRange0) {
   			this.randRange0 = randRange0;
   		}
   		public double getRandRange1() {
   			return randRange1;
   		}
   		public void setRandRange1(double randRange1) {
   			this.randRange1 = randRange1;
   		}
   		public double getRandRange2() {
   			return randRange2;
   		}
   		public void setRandRange2(double randRange2) {
   			this.randRange2 = randRange2;
   		}
   		public double getRandRange3() {
   			return randRange3;
   		}
   		public void setRandRange3(double randRange3) {
   			this.randRange3 = randRange3;
   		}
   		public double getRandRange4() {
   			return randRange4;
   		}
   		public void setRandRange4(double randRange4) {
   			this.randRange4 = randRange4;
   		}
   		public double getRandRange5() {
   			return randRange5;
   		}
   		public void setRandRange5(double randRange5) {
   			this.randRange5 = randRange5;
   		}
   		public double getRandRange6() {
   			return randRange6;
   		}
   		public void setRandRange6(double randRange6) {
   			this.randRange6 = randRange6;
   		}
   		public int getScale() {
   			return scale;
   		}
   		public void setScale(int scale) {
   			this.scale = scale;
   		}
   		public int getAmountScale() {
   			return amountScale;
   		}
   		public void setAmountScale(int amountScale) {
   			this.amountScale = amountScale;
   		}
   		public BigDecimal getMaxSubPrice() {
   			return maxSubPrice;
   		}
   		public void setMaxSubPrice(BigDecimal maxSubPrice) {
   			this.maxSubPrice = maxSubPrice;
   		}
   		public int getInitOrderCount() {
   			return initOrderCount;
   		}
   		public void setInitOrderCount(int initOrderCount) {
   			this.initOrderCount = initOrderCount;
   		}
   		public BigDecimal getPriceStepRate() {
   			return priceStepRate;
   		}
   		public void setPriceStepRate(BigDecimal priceStepRate) {
   			this.priceStepRate = priceStepRate;
   		}
   		public int getRunTime() {
   			return runTime;
   		}
   		public void setRunTime(int runTime) {
   			this.runTime = runTime;
   		}
   		public String getCoinName() {
   			return coinName;
   		}
   		public void setCoinName(String coinName) {
   			this.coinName = coinName;
   		}
   		public boolean isHalt() {
   			return isHalt;
   		}
   		public void setHalt(boolean isHalt) {
   			this.isHalt = isHalt;
   		}
   		public BigDecimal getPrice() {
			return price;
		}
		public void setPrice(BigDecimal price) {
			this.price = price;
		}
   	}
}
