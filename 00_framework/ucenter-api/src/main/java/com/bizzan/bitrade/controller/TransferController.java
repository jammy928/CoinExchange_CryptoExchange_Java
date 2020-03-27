package com.bizzan.bitrade.controller;

import com.alibaba.fastjson.JSONObject;
import com.bizzan.bitrade.constant.BooleanEnum;
import com.bizzan.bitrade.constant.CommonStatus;
import com.bizzan.bitrade.constant.TransactionType;
import com.bizzan.bitrade.entity.*;
import com.bizzan.bitrade.entity.transform.AuthMember;
import com.bizzan.bitrade.es.ESUtils;
import com.bizzan.bitrade.exception.InformationExpiredException;
import com.bizzan.bitrade.service.*;
import com.bizzan.bitrade.util.BigDecimalUtils;
import com.bizzan.bitrade.util.DESUtil;
import com.bizzan.bitrade.util.Md5;
import com.bizzan.bitrade.util.MessageResult;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static com.bizzan.bitrade.constant.SysConstant.SESSION_MEMBER;
import static com.bizzan.bitrade.util.BigDecimalUtils.compare;
import static org.springframework.util.Assert.*;

/**
 * @author GS
 * @date 2018年02月27日
 */
@RestController
@Slf4j
@RequestMapping(value = "/transfer", method = RequestMethod.POST)
public class TransferController {
    @Autowired
    private LocaleMessageSourceService sourceService;
    @Autowired
    private TransferAddressService transferAddressService;
    @Autowired
    private CoinService coinService;
    @Autowired
    private MemberWalletService memberWalletService;
    @Autowired
    private TransferRecordService transferRecordService;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ESUtils esUtils;
    @Value("${transfer.url:}")
    private String url;
    @Value("${transfer.key:}")
    private String key;
    @Value("${transfer.smac:}")
    private String smac;
    @Autowired
    private MemberTransactionService memberTransactionService;
    @Autowired
    private MemberService memberService;

    /**
     * 根据币种查询转账地址等信息
     *
     * @param unit
     * @param user
     * @return
     */
    @RequestMapping("address")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult transferAddress(String unit, @SessionAttribute(SESSION_MEMBER) AuthMember user) {
        Coin coin = coinService.findByUnit(unit);
        MemberWallet memberWallet = memberWalletService.findByCoinAndMemberId(coin, user.getId());
        List<TransferAddress> list = transferAddressService.findByCoin(coin);
        MessageResult result = MessageResult.success();
        result.setData(TransferAddressInfo.builder().balance(memberWallet.getBalance())
                .info(list.stream().map(x -> {
                    HashMap<String, Object> map = new HashMap<>(3);
                    map.put("address", x.getAddress());
                    map.put("rate", x.getTransferFee());
                    map.put("minAmount", x.getMinAmount());
                    return map;
                }).collect(Collectors.toList())).build());
        return result;
    }

    /**
     * 转账申请
     *
     * @param user
     * @param unit
     * @param address
     * @param amount
     * @param fee
     * @param jyPassword
     * @param remark
     * @return
     * @throws Exception
     */
    @RequestMapping("apply")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult transfer(@SessionAttribute(SESSION_MEMBER) AuthMember user, String unit, String address,
                                  BigDecimal amount, BigDecimal fee, String jyPassword, String remark) throws Exception {
        hasText(jyPassword, sourceService.getMessage("MISSING_JYPASSWORD"));
        hasText(unit, sourceService.getMessage("MISSING_COIN_TYPE"));
        Coin coin = coinService.findByUnit(unit);
        notNull(coin, sourceService.getMessage("COIN_ILLEGAL"));
        isTrue(coin.getStatus().equals(CommonStatus.NORMAL) && coin.getCanTransfer().equals(BooleanEnum.IS_TRUE), sourceService.getMessage("COIN_NOT_SUPPORT"));
        TransferAddress transferAddress = transferAddressService.findOnlyOne(coin, address);
        isTrue(transferAddress != null, sourceService.getMessage("WRONG_ADDRESS"));
        isTrue(fee.compareTo(BigDecimalUtils.mulRound(amount, BigDecimalUtils.getRate(transferAddress.getTransferFee()))) == 0, sourceService.getMessage("FEE_ERROR"));
        Member member = memberService.findOne(user.getId());
        String mbPassword = member.getJyPassword();
        Assert.hasText(mbPassword, sourceService.getMessage("NO_SET_JYPASSWORD"));
        Assert.isTrue(Md5.md5Digest(jyPassword + member.getSalt()).toLowerCase().equals(mbPassword), sourceService.getMessage("ERROR_JYPASSWORD"));
        MemberWallet memberWallet = memberWalletService.findByCoinAndMemberId(coin, user.getId());
        isTrue(compare(memberWallet.getBalance(), BigDecimalUtils.add(amount, fee)), sourceService.getMessage("INSUFFICIENT_BALANCE"));
        int result = memberWalletService.deductBalance(memberWallet, BigDecimalUtils.add(amount, fee));
        if (result <= 0) {
            throw new InformationExpiredException("Information Expired");
        }
        TransferRecord transferRecord = new TransferRecord();
        transferRecord.setAmount(amount);
        transferRecord.setCoin(coin);
        transferRecord.setMemberId(user.getId());
        transferRecord.setFee(fee);
        transferRecord.setAddress(address);
        transferRecord.setRemark(remark);
        TransferRecord transferRecord1 = transferRecordService.save(transferRecord);

        MemberTransaction memberTransaction = new MemberTransaction();
        memberTransaction.setAddress(address);
        memberTransaction.setAmount(BigDecimalUtils.add(fee, amount));
        memberTransaction.setMemberId(user.getId());
        memberTransaction.setSymbol(coin.getUnit());
        memberTransaction.setCreateTime(transferRecord1.getCreateTime());
        memberTransaction.setType(TransactionType.TRANSFER_ACCOUNTS);
        memberTransaction.setFee(transferRecord.getFee());
        memberTransaction.setRealFee(transferRecord.getFee()+"");
        memberTransaction.setDiscountFee("0");
        memberTransaction=  memberTransactionService.save(memberTransaction);
        if (transferRecord1 == null) {
            throw new InformationExpiredException("Information Expired");
        } else {
            JSONObject json = new JSONObject();
            //会员id
            json.put("uid", user.getId());
            //转账数目
            json.put("amount", amount);
            //转账手续费
            json.put("fee", fee);
            //币种单位
            json.put("coin", coin.getUnit());
            //转账地址
            json.put("address", address);
            //转账记录ID
            json.put("recordId", transferRecord1.getId());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("data", json);
            jsonObject.put("sign", Md5.md5Digest(json.toJSONString() + smac));
            String ciphertext = DESUtil.ENCRYPTMethod(jsonObject.toJSONString(), key).toUpperCase();
            String response = restTemplate.postForEntity(url, ciphertext, String.class).getBody();
            JSONObject resJson = JSONObject.parseObject(DESUtil.decrypt(response.trim(), key));
            if (resJson != null) {
                //验证签名
                if (Md5.md5Digest(resJson.getJSONObject("data").toJSONString() + smac).equals(resJson.getString("sign"))) {
                    if (resJson.getJSONObject("data").getIntValue("returnCode") == 1) {
                        transferRecord1.setOrderSn(resJson.getJSONObject("data").getString("returnMsg"));
                        log.info("============》转账成功");
                    }
                }
            }
            return MessageResult.success();
        }
    }

    /**
     * 转账记录
     *
     * @param user
     * @param pageNo
     * @param pageSize
     * @return
     */
    @RequestMapping("record")
    public MessageResult pageWithdraw(@SessionAttribute(SESSION_MEMBER) AuthMember user, int pageNo, int pageSize) {
        MessageResult mr = new MessageResult(0, "success");
        Page<TransferRecord> records = transferRecordService.findAllByMemberId(user.getId(), pageNo, pageSize);
        mr.setData(records);
        return mr;
    }
}
