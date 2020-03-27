package com.bizzan.bitrade.entity;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.bizzan.bitrade.constant.PayMode;

import java.math.BigDecimal;

@Data
public class LegalWalletWithdrawModel {

    /**
     * 数量
     */
    @Min(value = 0, message = "{LegalWalletRechargeModel.amount.min}")
    private BigDecimal amount;

    /**
     * 提现货币
     */
    @NotNull(message = "{LegalWalletRechargeModel.coinName.null}")
    private String unit;

    /**
     * 收款方式
     */
    @NotNull(message = "{LegalWalletRechargeModel.payModes.null}")
    private PayMode payMode;

    /**
     * 备注
     */
    private String remark;

}
