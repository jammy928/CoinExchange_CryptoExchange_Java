package com.bizzan.bitrade.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bizzan.bitrade.constant.ActivityRewardType;
import com.bizzan.bitrade.constant.BooleanEnum;
import com.bizzan.bitrade.constant.RewardRecordType;
import com.bizzan.bitrade.constant.TransactionType;
import com.bizzan.bitrade.entity.*;
import com.bizzan.bitrade.es.ESUtils;
import com.bizzan.bitrade.service.*;
import com.bizzan.bitrade.util.BigDecimalUtils;
import com.bizzan.bitrade.util.GeneratorUtil;
import com.bizzan.bitrade.util.MessageResult;

import org.apache.commons.lang.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;

@Component
public class MemberConsumer {
    private Logger logger = LoggerFactory.getLogger(MemberConsumer.class);
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private CoinService coinService;
    @Autowired
    private MemberWalletService memberWalletService;
    @Autowired
    private RewardActivitySettingService rewardActivitySettingService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private RewardRecordService rewardRecordService;
    @Autowired
    private MemberTransactionService memberTransactionService;
    @Autowired
    private ESUtils esUtils ;

    /**
     * 重置用户钱包地址
     * @param record
     */
    @KafkaListener(topics = {"reset-member-address"})
    public void resetAddress(ConsumerRecord<String,String> record){
        String content = record.value();
        JSONObject json = JSON.parseObject(content);
        Coin coin = coinService.findByUnit(record.key());
        Assert.notNull(coin,"coin null");
        if(coin.getEnableRpc()==BooleanEnum.IS_TRUE){
            MemberWallet memberWallet = memberWalletService.findByCoinUnitAndMemberId(record.key(),json.getLong("uid"));
            Assert.notNull(memberWallet,"wallet null");
            String account = "U" + json.getLong("uid")+ GeneratorUtil.getNonceString(4);
            //远程RPC服务URL,后缀为币种单位
            String serviceName = "SERVICE-RPC-" + coin.getUnit();
            try{
                String url = "http://" + serviceName + "/rpc/address/{account}";
                ResponseEntity<MessageResult> result = restTemplate.getForEntity(url, MessageResult.class, account);
                logger.info("remote call:service={},result={}", serviceName, result);
                if (result.getStatusCode().value() == 200) {
                    MessageResult mr = result.getBody();
                    if (mr.getCode() == 0) {
                        String address =  mr.getData().toString();
                        memberWallet.setAddress(address);
                    }
                }
            }
            catch (Exception e){
                logger.error("call {} failed,error={}",serviceName,e.getMessage());
            }
            memberWalletService.save(memberWallet);
        }

    }


    /**
     * 客户注册消息
     * @param content
     */
    @KafkaListener(topics = {"member-register"})
    public void handle(String content) {
        logger.info("handle member-register,data={}", content);
        if (StringUtils.isEmpty(content)) {
            return;
        }
        JSONObject json = JSON.parseObject(content);
        if(json == null) {
            return ;
        }
        //获取所有支持的币种
        List<Coin> coins =  coinService.findAll();
        for(Coin coin:coins) {
            logger.info("memberId:{},unit:{}",json.getLong("uid"),coin.getUnit());
            MemberWallet wallet = new MemberWallet();
            wallet.setCoin(coin);
            wallet.setMemberId(json.getLong("uid"));
            wallet.setBalance(new BigDecimal(0));
            wallet.setFrozenBalance(new BigDecimal(0));
            wallet.setAddress("");
            
/** 此处获取地址注释掉，所有币种地址由用户主动获取才生成  **/
//            if(coin.getEnableRpc() == BooleanEnum.IS_TRUE) {
//                String account = "U" + json.getLong("uid");
//                //远程RPC服务URL,后缀为币种单位
//                String serviceName = "SERVICE-RPC-" + coin.getUnit();
//                try{
//                    String url = "http://" + serviceName + "/rpc/address/{account}";
//                    ResponseEntity<MessageResult> result = restTemplate.getForEntity(url, MessageResult.class, account);
//                    logger.info("remote call:service={},result={}", serviceName, result);
//                    if (result.getStatusCode().value() == 200) {
//                        MessageResult mr = result.getBody();
//                        logger.info("mr={}", mr);
//                        if (mr.getCode() == 0) {
//                            //返回地址成功，调用持久化
//                            String address = (String) mr.getData();
//                            wallet.setAddress(address);
//                        }
//                    }
//                }
//                catch (Exception e){
//                    logger.error("call {} failed,error={}",serviceName,e.getMessage());
//                    wallet.setAddress("");
//                }
//            }
//            else{
//                wallet.setAddress("");
//            }
            //保存
            memberWalletService.save(wallet);
        }
        //注册活动奖励
        RewardActivitySetting rewardActivitySetting = rewardActivitySettingService.findByType(ActivityRewardType.REGISTER);
        if (rewardActivitySetting!=null){
            MemberWallet memberWallet=memberWalletService.findByCoinAndMemberId(rewardActivitySetting.getCoin(),json.getLong("uid"));
            if (memberWallet==null){return;}
            // 奖励币
            BigDecimal amount3=JSONObject.parseObject(rewardActivitySetting.getInfo()).getBigDecimal("amount");
            memberWallet.setBalance(BigDecimalUtils.add(memberWallet.getBalance(),amount3));
            memberWalletService.save(memberWallet);
            // 保存奖励记录
            Member member = memberService.findOne(json.getLong("uid"));
            RewardRecord rewardRecord3 = new RewardRecord();
            rewardRecord3.setAmount(amount3);
            rewardRecord3.setCoin(rewardActivitySetting.getCoin());
            rewardRecord3.setMember(member);
            rewardRecord3.setRemark(rewardActivitySetting.getType().getCnName());
            rewardRecord3.setType(RewardRecordType.ACTIVITY);
            rewardRecordService.save(rewardRecord3);
            // 保存资产变更记录
            MemberTransaction memberTransaction = new MemberTransaction();
            memberTransaction.setFee(BigDecimal.ZERO);
            memberTransaction.setAmount(amount3);
            memberTransaction.setSymbol(rewardActivitySetting.getCoin().getUnit());
            memberTransaction.setType(TransactionType.ACTIVITY_AWARD);
            memberTransaction.setMemberId(member.getId());
            memberTransaction.setDiscountFee("0");
            memberTransaction.setRealFee("0");
            memberTransaction = memberTransactionService.save(memberTransaction);
        }

    }
}
