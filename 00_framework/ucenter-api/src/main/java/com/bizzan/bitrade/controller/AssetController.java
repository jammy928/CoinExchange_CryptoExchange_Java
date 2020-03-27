package com.bizzan.bitrade.controller;


import static com.bizzan.bitrade.constant.SysConstant.SESSION_MEMBER;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.alibaba.fastjson.JSONObject;
import com.bizzan.bitrade.constant.TransactionType;
import com.bizzan.bitrade.entity.MemberWallet;
import com.bizzan.bitrade.entity.transform.AuthMember;
import com.bizzan.bitrade.es.ESUtils;
import com.bizzan.bitrade.service.MemberTransactionService;
import com.bizzan.bitrade.service.MemberWalletService;
import com.bizzan.bitrade.system.CoinExchangeFactory;
import com.bizzan.bitrade.util.DateUtil;
import com.bizzan.bitrade.util.MessageResult;
import com.sparkframework.lang.Convert;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/asset")
@Slf4j
public class AssetController {
    @Autowired
    private MemberWalletService walletService;
    @Autowired
    private MemberTransactionService transactionService;
    @Autowired
    private CoinExchangeFactory coinExchangeFactory;
    @Value("${gcx.match.max-limit:1000}")
    private double gcxMatchMaxLimit;
    @Value("${gcx.match.each-limit:5}")
    private double gcxMatchEachLimit;
    @Autowired
    private KafkaTemplate kafkaTemplate;
    @Autowired
    private ESUtils esUtils;

    /**
     * 用户钱包信息
     *
     * @param member
     * @return
     */
    @RequestMapping("wallet")
    public MessageResult findWallet(@SessionAttribute(SESSION_MEMBER) AuthMember member) {
        List<MemberWallet> wallets = walletService.findAllByMemberId(member.getId());
        wallets.forEach(wallet -> {
            CoinExchangeFactory.ExchangeRate rate = coinExchangeFactory.get(wallet.getCoin().getUnit());
            if (rate != null) {
                wallet.getCoin().setUsdRate(rate.getUsdRate().doubleValue());
                wallet.getCoin().setCnyRate(rate.getCnyRate().doubleValue());
            } else {
                log.info("unit = {} , rate = null ", wallet.getCoin().getUnit());
            }
            // 生成Memo
            if(wallet.getCoin().getAccountType() == 1) {
            	// 门罗币特殊处理（Memo是64位的paymentId）
            	if(wallet.getCoin().getUnit().equals("XMR")) {
            		// 生成64位Hash，然后将其中1,9,17,25,33,41,49,57位替换
            		StringBuilder hash = new StringBuilder(DigestUtils.md5Hex(String.valueOf(wallet.getMemberId())) + DigestUtils.md5Hex(String.valueOf(wallet.getMemberId()+1)));
            		String hexId = String.format("%08x", wallet.getMemberId());
            		for(int i = 0; i < 8; i++) {
            			hash.setCharAt((i*8 + 1), hexId.charAt(i));
            		}
            		wallet.setMemo(hash.toString());
            	}else {
            		wallet.setMemo(String.valueOf(wallet.getMemberId() + 345678)); // 用户ID + 345678
            	}
            }
            wallet.getCoin().setColdWalletAddress(""); // 对前端隐藏冷钱包地址
        });
        MessageResult mr = MessageResult.success("success");
        mr.setData(wallets);
        return mr;
    }

    /**
     * 查询特定类型的记录
     *
     * @param member
     * @param pageNo
     * @param pageSize
     * @param type
     * @return
     */
    @RequestMapping("transaction")
    public MessageResult findTransaction(@SessionAttribute(SESSION_MEMBER) AuthMember member, int pageNo, int pageSize, TransactionType type) {
        MessageResult mr = new MessageResult();
        mr.setData(transactionService.queryByMember(member.getId(), pageNo, pageSize, type));
        mr.setCode(0);
        mr.setMessage("success");
        return mr;
    }

    /**
     * 查询所有记录
     *
     * @param member
     * @param pageNo
     * @param pageSize
     * @return
     */
    @RequestMapping("transaction/all")
    public MessageResult findTransaction(@SessionAttribute(SESSION_MEMBER) AuthMember member, HttpServletRequest request, int pageNo, int pageSize,
                                         @RequestParam(value = "startTime",required = false)  String startTime,
                                         @RequestParam(value = "endTime",required = false)  String endTime,
                                         @RequestParam(value = "symbol",required = false)  String symbol,
                                         @RequestParam(value = "type",required = false)  String type) throws ParseException {
        MessageResult mr = new MessageResult();
        TransactionType transactionType = null;
        if (StringUtils.isNotEmpty(type)) {
            transactionType = TransactionType.valueOfOrdinal(Convert.strToInt(type, 0));
        }
        mr.setCode(0);
        mr.setMessage("success");
        mr.setData(transactionService.queryByMember(member.getId(), pageNo, pageSize, transactionType, startTime, endTime,symbol));
        return mr;
    }

    @RequestMapping("wallet/{symbol}")
    public MessageResult findWalletBySymbol(@SessionAttribute(SESSION_MEMBER) AuthMember member, @PathVariable String symbol) {
        MessageResult mr = MessageResult.success("success");
        mr.setData(walletService.findByCoinUnitAndMemberId(symbol, member.getId()));
        return mr;
    }

    /**
     * 币种转化(GCC配对GCX,特殊用途，其他项目可以不管)
     *
     * @return
     */
    @RequestMapping("wallet/match-check")
    public MessageResult transformCheck(@SessionAttribute(SESSION_MEMBER) AuthMember member) throws Exception {
        String symbol = "GCX";
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour < 9) {
            return new MessageResult(500, "每日9:00开放配对");
        }
        if (transactionService.isOverMatchLimit(DateUtil.YYYY_MM_DD.format(new Date()), gcxMatchMaxLimit)) {
            return new MessageResult(500, "今日配对额度已售罄");
        }
        BigDecimal matchedAmount = transactionService.findMemberDailyMatch(member.getId(),DateUtil.YYYY_MM_DD.format(new Date()));
        log.error("matchedAmount = {}",matchedAmount);
        if(matchedAmount.compareTo(new BigDecimal(gcxMatchEachLimit)) >= 0){
            return new MessageResult(500,"您今日配对已达上限" + gcxMatchEachLimit);
        }

        BigDecimal amount = transactionService.findMatchTransactionSum(member.getId(),symbol);
        log.error("sum amount = {}",amount);
        MemberWallet gccWallet = walletService.findByCoinUnitAndMemberId("GCC",member.getId());
        if(amount.compareTo(gccWallet.getBalance()) > 0){
            amount = gccWallet.getBalance();
        }
        MemberWallet gcxWallet = walletService.findByCoinUnitAndMemberId("GCX", member.getId());
        if (amount.compareTo(gcxWallet.getBalance()) > 0) {
            amount = gcxWallet.getBalance();
        }
        BigDecimal maxAmount = new BigDecimal(gcxMatchEachLimit).subtract(matchedAmount);
        if (maxAmount.compareTo(BigDecimal.ZERO) < 0) {
            maxAmount = BigDecimal.ZERO;
        }
        if (amount.compareTo(maxAmount) > 0) {
            amount = maxAmount;
        }
        MessageResult mr = new MessageResult(0, "success");
        mr.setData(amount.setScale(4, BigDecimal.ROUND_DOWN));
        return mr;
    }

    @RequestMapping("wallet/match")
    public MessageResult transform(@SessionAttribute(SESSION_MEMBER) AuthMember member, BigDecimal amount) throws Exception {
        String symbol = "GCX";
        if(amount.compareTo(BigDecimal.ZERO) <= 0){
            return new MessageResult(500,"配对数额必须大于0");
        }
        if(amount.doubleValue() > gcxMatchEachLimit){
            return new MessageResult(500,"单人配对不能超过" + gcxMatchEachLimit);
        }
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if(member.getId() != 7 && hour < 9){
            return new MessageResult(500,"每日9:00开放配对");
        }
        if(transactionService.isOverMatchLimit(DateUtil.YYYY_MM_DD.format(new Date()), gcxMatchMaxLimit)){
            return new MessageResult(500,"今日配对额度已售罄");
        }
        BigDecimal matchedAmount = transactionService.findMemberDailyMatch(member.getId(),DateUtil.YYYY_MM_DD.format(new Date()));
        if(matchedAmount.compareTo(new BigDecimal(gcxMatchEachLimit)) >= 0){
            return new MessageResult(500,"您今日配对已达上限" + gcxMatchEachLimit);
        }
        BigDecimal maxAmount = transactionService.findMatchTransactionSum(member.getId(),symbol);
        if(amount.compareTo(maxAmount) > 0){
            return new MessageResult(500,"可配对的GCX余额不足");
        }
        MemberWallet gccWallet = walletService.findByCoinUnitAndMemberId("GCC", member.getId());
        if (gccWallet.getBalance().compareTo(amount) < 0) {
            return new MessageResult(500, "GCC可用余额不足");
        }
        MemberWallet gcxWallet = walletService.findByCoinUnitAndMemberId("GCX", member.getId());
        if (gcxWallet.getBalance().compareTo(amount) < 0) {
            return new MessageResult(500, "GCX可用余额不足");
        }
        transactionService.matchWallet(member.getId(),"GCX",amount);
        return new MessageResult(0,"success");
    }

    @RequestMapping("wallet/reset-address")
    public MessageResult resetWalletAddress(@SessionAttribute(SESSION_MEMBER) AuthMember member, String unit) {
        try {
        	MemberWallet mw = walletService.findByCoinUnitAndMemberId(unit, member.getId());
        	if(mw.getCoin().getAccountType() == 0 && StringUtils.isEmpty(mw.getAddress())) {
	            JSONObject json = new JSONObject();
	            json.put("uid", member.getId());
	            kafkaTemplate.send("reset-member-address", unit, json.toJSONString());
	            return MessageResult.success("提交成功");
        	}else {
        		return MessageResult.error("地址获取成功，请退出资产管理页面再重新进入！");
        	}
        } catch (Exception e) {
            return MessageResult.error("未知异常");
        }
    }

    @RequestMapping("transaction_es")
    public MessageResult findTransactionByES(@RequestParam(value = "memberId",required = true) long memberId,
                                             @RequestParam(value = "page",required = true) int pageNum,
                                             @RequestParam(value = "limit",required = true)  int pageSize,
                                             @RequestParam(value = "startTime",required = false)  String startTime,
                                             @RequestParam(value = "endTime",required = false)  String endTime,
                                             @RequestParam(value = "symbol",required = false)  String symbol,
                                             @RequestParam(value = "type",required = false)  String type)  {
        log.info(">>>>>>查询交易明细开始>>>>>>>>>");
        MessageResult messageResult = new MessageResult();
        try {
            String query="{\"from\":"+(pageNum-1)*pageSize+",\"size\":"+ pageSize+",\"sort\":[{\"create_time\":{\"order\":\"desc\"}}]," +
                    "\"query\":{\"bool\":{\"must\":[{\"match\":{\"member_id\":\""+memberId+"\"}}";

            if(StringUtils.isNotEmpty(symbol)){
                query=query+",{\"match\":{\"symbol\":\""+symbol+"\"}}";
            }
            if(StringUtils.isNotEmpty(type)){
                query=query+",{\"match\":{\"type\":\""+type+"\"}}";
            }
            if(StringUtils.isNotEmpty(startTime)&&StringUtils.isNotEmpty(endTime)){
                query =query+",{\"range\":{\"create_time\":{\"gte\":\""+startTime+"\",\"lte\":\""+endTime+"\"}}}";
            }
            query=query+"]}}}";
            JSONObject resultJson = esUtils.queryForAnyOne(JSONObject.parseObject(query),"member_transaction","mem_transaction");
            messageResult.setCode(0);
            messageResult.setData(resultJson);
            messageResult.setMessage("success");
        } catch (Exception e) {
            log.info(">>>>>>查询es错误>>>>>>"+e);
            e.printStackTrace();
            return MessageResult.error(500,"查询异常，请稍后再试");
        }
        return messageResult;
    }

}
