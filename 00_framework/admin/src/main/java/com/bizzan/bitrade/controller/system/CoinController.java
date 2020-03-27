package com.bizzan.bitrade.controller.system;

import static org.springframework.util.Assert.notNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONObject;
import com.bizzan.bitrade.annotation.AccessLog;
import com.bizzan.bitrade.constant.AdminModule;
import com.bizzan.bitrade.constant.BooleanEnum;
import com.bizzan.bitrade.constant.PageModel;
import com.bizzan.bitrade.constant.SysConstant;
import com.bizzan.bitrade.constant.TransactionType;
import com.bizzan.bitrade.controller.common.BaseAdminController;
import com.bizzan.bitrade.dto.CoinDTO;
import com.bizzan.bitrade.entity.Admin;
import com.bizzan.bitrade.entity.Coin;
import com.bizzan.bitrade.entity.HotTransferRecord;
import com.bizzan.bitrade.entity.Member;
import com.bizzan.bitrade.entity.MemberTransaction;
import com.bizzan.bitrade.entity.MemberWallet;
import com.bizzan.bitrade.entity.QHotTransferRecord;
import com.bizzan.bitrade.es.ESUtils;
import com.bizzan.bitrade.service.CoinService;
import com.bizzan.bitrade.service.HotTransferRecordService;
import com.bizzan.bitrade.service.LocaleMessageSourceService;
import com.bizzan.bitrade.service.MemberService;
import com.bizzan.bitrade.service.MemberTransactionService;
import com.bizzan.bitrade.service.MemberWalletService;
import com.bizzan.bitrade.util.*;
import com.querydsl.core.types.dsl.BooleanExpression;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Shaoxianjun
 * @description 后台货币web
 * @date 2018/12/29 15:01
 */
@RestController
@RequestMapping("/system/coin")
@Slf4j
public class CoinController extends BaseAdminController {

    private Logger logger = LoggerFactory.getLogger(BaseAdminController.class);

    @Autowired
    private HotTransferRecordService hotTransferRecordService;

    @Autowired
    private CoinService coinService;

    @Autowired
    private MemberWalletService memberWalletService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private MemberWalletService walletService;
    @Autowired
    private MemberService memberService;

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Autowired
    private LocaleMessageSourceService messageSource;

    @Autowired
    private MemberTransactionService memberTransactionService;

    @Autowired
    private ESUtils esUtils;

    @Autowired
    JDBCUtils jdbcUtils;

    @RequiresPermissions("system:coin:create")
    @PostMapping("create")
    @AccessLog(module = AdminModule.SYSTEM, operation = "创建后台货币Coin")
    public MessageResult create(@Valid Coin coin, BindingResult bindingResult) {
        MessageResult result = BindingResultUtil.validate(bindingResult);
        if (result != null) {
            return result;
        }
        Coin one = coinService.findOne(coin.getName());
        if (one != null) {
            return error(messageSource.getMessage("COIN_NAME_EXIST"));
        }
        coinService.save(coin);
        return success();
    }

    @PostMapping("all-name")
    @AccessLog(module = AdminModule.SYSTEM, operation = "查找所有coin的name")
    public MessageResult getAllCoinName() {
        List<String> list = coinService.getAllCoinName();
        return MessageResult.getSuccessInstance(messageSource.getMessage("SUCCESS"), list);
    }

    @PostMapping("all-name-and-unit")
    @AccessLog(module = AdminModule.SYSTEM, operation = "查找所有coin的name和unit")
    public MessageResult getAllCoinNameAndUnit() {
        List<CoinDTO> list = coinService.getAllCoinNameAndUnit();
        return MessageResult.getSuccessInstance(messageSource.getMessage("SUCCESS"), list);
    }

    @PostMapping("all-name/legal")
    @AccessLog(module = AdminModule.SYSTEM, operation = "查找所有coin的name")
    public MessageResult getAllCoinNameLegal() {
        List<String> list = coinService.getAllCoinNameLegal();
        return success(list);
    }

    @RequiresPermissions("system:coin:update")
    @PostMapping("update")
    @AccessLog(module = AdminModule.SYSTEM, operation = "更新后台货币Coin")
    public MessageResult update(
            @Valid Coin coin,
            @SessionAttribute(SysConstant.SESSION_ADMIN) Admin admin,
            String code,
            BindingResult bindingResult) {

        Assert.notNull(admin, messageSource.getMessage("DATA_EXPIRED_LOGIN_AGAIN"));
        MessageResult checkCode = checkCode(code, SysConstant.ADMIN_COIN_REVISE_PHONE_PREFIX + admin.getMobilePhone());
        if (checkCode.getCode() != 0) {
            return checkCode;
        }

        notNull(coin.getName(), "validate coin.name!");
        MessageResult result = BindingResultUtil.validate(bindingResult);
        if (result != null) {
            return result;
        }
        Coin one = coinService.findOne(coin.getName());
        notNull(one, "validate coin.name!");
        coinService.save(coin);
        return success();
    }

    @RequiresPermissions("system:coin:detail")
    @PostMapping("detail")
    @AccessLog(module = AdminModule.SYSTEM, operation = "后台货币Coin详情")
    public MessageResult detail(@RequestParam("name") String name) {
        Coin coin = coinService.findOne(name);
        notNull(coin, "validate coin.name!");
        return success(coin);
    }

    @RequiresPermissions("system:coin:page-query")
    @PostMapping("page-query")
    @AccessLog(module = AdminModule.SYSTEM, operation = "分页查找后台货币Coin")
    public MessageResult pageQuery(PageModel pageModel) {
        if (pageModel.getProperty() == null) {
            List<String> list = new ArrayList<>();
            list.add("name");
            List<Sort.Direction> directions = new ArrayList<>();
            directions.add(Sort.Direction.DESC);
            pageModel.setProperty(list);
            pageModel.setDirection(directions);
        }
        Page<Coin> pageResult = coinService.findAll(null, pageModel.getPageable());
        for (Coin coin : pageResult.getContent()) {
            if (coin.getEnableRpc().getOrdinal() == 1) {
                coin.setAllBalance(memberWalletService.getAllBalance(coin.getName()));
                log.info(coin.getAllBalance() + "==============");
                if(coin.getAccountType() == 1) {
                	coin.setHotAllBalance(memberWalletService.getAllBalance(coin.getName()));
                	coin.setBlockHeight(0L);
                }else {
                	String url = "http://SERVICE-RPC-" + coin.getUnit() + "/rpc/balance";
                	coin.setHotAllBalance(getRPCWalletBalance(url, coin.getUnit()));
                	
                	String url2 = "http://SERVICE-RPC-" + coin.getUnit() + "/rpc/height";
                	coin.setBlockHeight(getRPCBlockHeight(url2, coin.getUnit()));
                }
            }
        }
        return success(pageResult);
    }

    /**
     * 测试请求
     *
     * @param url
     * @return
     */
    private BigDecimal getWalletBalance(String url) {
        try {
            //http请求
            String resultStr = getSend(url, null);
            log.info(url + ">>>result>>>" + resultStr);
            JSONObject resultJson = JSONObject.parseObject(resultStr);
            BigDecimal bigDecimal = resultJson.getBigDecimal("data");
            return bigDecimal;
        } catch (Exception e) {
            log.error("error={}", e);
            return new BigDecimal("0");
        }
    }

    public static String getSend(String url, Map<String, String> paramsMap) {
        HttpClient client = new HttpClient();
        client.setConnectionTimeout(10000);
        client.setTimeout(50000);
        try {
            GetMethod method = new GetMethod(url);
            if (paramsMap != null) {
                NameValuePair[] namePairs = new NameValuePair[paramsMap.size()];
                int i = 0;
                for (Map.Entry<String, String> param : paramsMap.entrySet()) {
                    NameValuePair pair = new NameValuePair(param.getKey(),
                            param.getValue());
                    namePairs[i++] = pair;
                }
                HttpMethodParams param = method.getParams();
                param.setContentCharset("utf-8");
            }
            client.executeMethod(method);
            return method.getResponseBodyAsString();
        } catch (Exception e) {
            log.error("getSend error!", e);
        }
        return "";
    }

    private BigDecimal getRPCWalletBalance(String url, String unit) {
        try {
            //String url = "http://" + serviceName + "/rpc/address/{account}";
            ResponseEntity<MessageResult> result = restTemplate.getForEntity(url, MessageResult.class);
            log.info("result={}", result);
            if (result.getStatusCode().value() == 200) {
                MessageResult mr = result.getBody();
                if (mr.getCode() == 0) {
                    String balance = mr.getData().toString();
                    BigDecimal bigDecimal = new BigDecimal(balance);
                    log.info(unit + messageSource.getMessage("HOT_WALLET_BALANCE"), bigDecimal);
                    return bigDecimal;
                }
            }
        } catch (IllegalStateException e) {
            log.error("error={}", e);
            return new BigDecimal("0");
        } catch (Exception e) {
            log.error("error={}", e);
            return new BigDecimal("0");
        }
        return new BigDecimal("0");
    }

    private Long getRPCBlockHeight(String url, String unit) {
    	try {
	    	ResponseEntity<MessageResult> result = restTemplate.getForEntity(url, MessageResult.class);
	        log.info("result={}", result);
	        if (result.getStatusCode().value() == 200) {
	            MessageResult mr = result.getBody();
	            if (mr.getCode() == 0) {
	                String height = mr.getData().toString();
	                Long longHeight = Long.valueOf(height);
	                return longHeight;
	            }
	        }

	    } catch (IllegalStateException e) {
	        log.error("error={}", e);
	        return Long.valueOf(0);
	    } catch (Exception e) {
	        log.error("error={}", e);
	        return Long.valueOf(0);
	    }
	    return Long.valueOf(0);
    }
    @RequiresPermissions("system:coin:out-excel")
    @GetMapping("outExcel")
    @AccessLog(module = AdminModule.SYSTEM, operation = "导出后台货币Coin Excel")
    public MessageResult outExcel(HttpServletRequest request, HttpServletResponse response) throws Exception {
        List all = coinService.findAll();
        return new FileUtil().exportExcel(request, response, all, "coin");
    }

    @RequiresPermissions("system:coin:delete-by-name")
    @PostMapping("delete/{name}")
    @AccessLog(module = AdminModule.SYSTEM, operation = "删除后台货币Coin")
    public MessageResult Delete(@PathVariable("name") String name) {
        Coin coin = coinService.findOne(name);
        notNull(coin, "validate coin.name!");
        coinService.deleteOne(name);
        return success();
    }

    @RequiresPermissions("system:coin:set-platform")
    @PostMapping("set/platform")
    @AccessLog(module = AdminModule.SYSTEM, operation = "设置平台币")
    public MessageResult setPlatformCoin(@RequestParam("name") String name) {
        Coin coin = coinService.findOne(name);
        notNull(coin, "validate coin.name!");
        coinService.setPlatformCoin(coin);
        return success();
    }


    /**
     * 转入冷钱包,扣除矿工费Coin.minerFee
     *
     * @param admin  手工操作者
     * @param amount 转账数量
     * @param unit   转账币种单位
     * @param code   验证码
     * @return
     */
    @RequiresPermissions("system:coin:transfer")
    @PostMapping("transfer")
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    @AccessLog(module = AdminModule.SYSTEM, operation = "热钱包转账至冷钱包")
    public MessageResult transfer(@SessionAttribute(SysConstant.SESSION_ADMIN) Admin admin,
                                  @RequestParam("amount") BigDecimal amount,
                                  @RequestParam("unit") String unit,
                                  @RequestParam(value = "code", defaultValue = "") String code) {
        Assert.notNull(admin, "会话已过期，请重新登录");
        String key = SysConstant.ADMIN_COIN_TRANSFER_COLD_PREFIX + admin.getMobilePhone();

        ValueOperations valueOperations = redisTemplate.opsForValue();
        Object object = valueOperations.get(key + "_PASS");

        if (object == null) {
            MessageResult checkCode = checkCode(code, key);
            if (checkCode.getCode() != 0) {
                return checkCode;
            }
        }
        Coin coin = coinService.findByUnit(unit);
        String urlBalance = "http://SERVICE-RPC-" + coin.getUnit() + "/rpc/balance";
        BigDecimal balance = getRPCWalletBalance(urlBalance, coin.getUnit());
        logger.info("balance:-------{}", balance);
        if (amount.compareTo(balance) > 0) {
            return error(messageSource.getMessage("HOT_WALLET_BALANCE_POOL"));
        }
        String url = "http://SERVICE-RPC-" + coin.getUnit() + "/rpc/transfer?address={1}&amount={2}&fee={3}";
        MessageResult result = restTemplate.getForObject(url,
                MessageResult.class, coin.getColdWalletAddress().toString(), amount, coin.getMinerFee());
        logger.info("result = {}", result);
        if (result.getCode() == 0 && result.getData() != null) {
            HotTransferRecord hotTransferRecord = new HotTransferRecord();
            hotTransferRecord.setAdminId(admin.getId());
            hotTransferRecord.setAdminName(admin.getUsername());
            hotTransferRecord.setAmount(amount);
            hotTransferRecord.setBalance(balance.subtract(amount));
            hotTransferRecord.setMinerFee(coin.getMinerFee() == null ? BigDecimal.ZERO : coin.getMinerFee());
            hotTransferRecord.setUnit(unit.toUpperCase());
            hotTransferRecord.setColdAddress(coin.getColdWalletAddress());
            hotTransferRecord.setTransactionNumber(result.getData().toString());
            hotTransferRecordService.save(hotTransferRecord);
            return success(messageSource.getMessage("SUCCESS"), hotTransferRecord);
        }
        return error(messageSource.getMessage("REQUEST_FAILED"));
    }

    @RequiresPermissions("system:coin:hot-transfer-record:page-query")
    @PostMapping("/hot-transfer-record/page-query")
    @AccessLog(module = AdminModule.SYSTEM, operation = "热钱包转账至冷钱包记录分页查询")
    public MessageResult page(PageModel pageModel, String unit) {
        List<BooleanExpression> booleanExpressions = new ArrayList<>();
        if (!StringUtils.isEmpty(unit)) {
            booleanExpressions.add(QHotTransferRecord.hotTransferRecord.unit.eq(unit));
        }
        Page<HotTransferRecord> page = hotTransferRecordService.findAll(PredicateUtils.getPredicate(booleanExpressions), pageModel);
        return success(messageSource.getMessage("SUCCESS"), page);
    }


    /**
     * 为每个人添加新币种钱包记录
     * 1.使用JDBC批量插入
     * 2.默认不获取钱包地址，用户充值时自主获取
     *
     * @param coinName
     * @return
     */
    @RequiresPermissions("system:coin:newwallet")
    @RequestMapping("create-member-wallet")
    public MessageResult createCoin(String coinName) {
        Coin coin = coinService.findOne(coinName);
        if (coin == null) {
            return MessageResult.error("币种配置不存在");
        }
        jdbcUtils.synchronization2MemberRegisterWallet(null, coin.getName());
//        List<Member> list = memberService.findAll();
//        list.forEach(member -> {
//            MemberWallet wallet = memberWalletService.findByCoinAndMember(coin, member);
//            if (wallet == null) {
//                wallet = new MemberWallet();
//                wallet.setCoin(coin);
//                wallet.setMemberId(member.getId());
//                wallet.setBalance(new BigDecimal(0));
//                wallet.setFrozenBalance(new BigDecimal(0));
//                if (coin.getEnableRpc() == BooleanEnum.IS_TRUE) {
//                    String account = "U" + member.getId();
//                    //远程RPC服务URL,后缀为币种单位
//                    String serviceName = "SERVICE-RPC-" + coin.getUnit();
//                    try {
//                        String url = "http://" + serviceName + "/rpc/address/{account}";
//                        ResponseEntity<MessageResult> result = restTemplate.getForEntity(url, MessageResult.class, account);
//                        logger.info("remote call:service={},result={}", serviceName, result);
//                        if (result.getStatusCode().value() == 200) {
//                            MessageResult mr = result.getBody();
//                            if (mr.getCode() == 0) {
//                                //返回地址成功，调用持久化
//                                String address = (String) mr.getData();
//                                wallet.setAddress(address);
//                            }
//                        }
//                        Thread.sleep(10L);
//                    } catch (Exception e) {
//                        logger.error("call {} failed,error={}", serviceName, e.getMessage());
//                        wallet.setAddress("");
//                    }
//                } else {
//                    wallet.setAddress("");
//                }
//                walletService.save(wallet);
//            }
//        });
        return MessageResult.success(messageSource.getMessage("SUCCESS"));
    }

    @RequestMapping("need-create-wallet")
    public MessageResult needCreateWallet(String coinName) {
        Coin coin = coinService.findOne(coinName);
        if (coin == null) {
            return MessageResult.error("币种配置不存在");
        }
        MessageResult result = success("", false);
        List<Member> list = memberService.findAll();
        for (Member member : list) {
            MemberWallet wallet = memberWalletService.findByCoinAndMember(coin, member);
            if (wallet == null) {
                result = success(messageSource.getMessage("SUCCESS"), true);
                return result;
            }
        }
        return result;
    }

    @GetMapping("get-no-check-key")
    public MessageResult getKey(String phone) {
        String key = SysConstant.ADMIN_COIN_TRANSFER_COLD_PREFIX + phone + "_PASS";
        ValueOperations valueOperations = redisTemplate.opsForValue();
        Object object = valueOperations.get(key);
        if (object == null) {
            return error(messageSource.getMessage("NEED_CODE"));
        }
        return success(messageSource.getMessage("NO_NEED_CODE"), object);
    }


    @RequiresPermissions("system:coin:addPartner")
    @RequestMapping(value = "add-partner", method = RequestMethod.POST)
    public MessageResult addPartner(@RequestParam("coinId") String coinId, @RequestParam("amount") long amount, @RequestParam("memberId") long memberId) {
        BigDecimal init = BigDecimal.ZERO;
        MemberTransaction memberTransaction = new MemberTransaction();
        memberTransaction.setMemberId(memberId);
        memberTransaction.setAmount(BigDecimal.valueOf(amount));
        memberTransaction.setCreateTime(new Date());
        memberTransaction.setFee(init);
        memberTransaction.setFlag(0);
        memberTransaction.setSymbol("BHB");
        memberTransaction.setType(TransactionType.RECHARGE);
        memberTransaction.setRealFee("0");
        memberTransaction.setDiscountFee("0");

        MemberTransaction result = memberTransactionService.save(memberTransaction);

        int resultWallet = walletService.updateByMemberIdAndCoinId(memberId, "BHB", BigDecimal.valueOf(amount));
        if (resultWallet == 1) {
            log.info("修改余额成功--memberID:" + memberId + "amount:" + amount);
        } else {
            log.info("修改余额成功");
        }
        if (result != null) {


            log.info("添加数据库成功--memberID:" + memberId + "amount:" + amount);
        } else {
            log.info("添加数据库失败");
        }


        return success();

    }


}
