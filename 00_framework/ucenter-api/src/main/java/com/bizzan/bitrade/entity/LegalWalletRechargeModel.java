package com.bizzan.bitrade.entity;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import com.bizzan.bitrade.constant.PayMode;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 接受对象模型
 */
@Data
public class LegalWalletRechargeModel {
    /**
     * 数量
     */
    @Min(value = 0, message = "{LegalWalletRechargeModel.amount.min}")
    private BigDecimal amount;

    /**
     * 支付凭证图片url
     */
    @NotBlank(message = "{LegalWalletRechargeModel.paymentInstrument.null}")
    private String paymentInstrument;

    /**
     * 充值货币
     */
    @NotNull(message = "{LegalWalletRechargeModel.coinName.null}")
    private String unit;

    /**
     * 支付方式
     */
    @NotNull(message = "{LegalWalletRechargeModel.payModes.null}")
    private PayMode payMode;

    /**
     * 备注
     */
    private String remark;
}
