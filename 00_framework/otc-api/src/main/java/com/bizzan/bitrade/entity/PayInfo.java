package com.bizzan.bitrade.entity;

import com.bizzan.bitrade.entity.Alipay;
import com.bizzan.bitrade.entity.BankInfo;
import com.bizzan.bitrade.entity.WechatPay;

import lombok.Builder;
import lombok.Data;

/**
 * @author GS
 * @date 2018年01月20日
 */
@Builder
@Data
public class PayInfo {
    private String realName;
    private Alipay alipay;
    private WechatPay wechatPay;
    private BankInfo bankInfo;
}
