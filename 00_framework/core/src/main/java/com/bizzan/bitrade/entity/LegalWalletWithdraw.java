package com.bizzan.bitrade.entity;

import com.bizzan.bitrade.constant.PayMode;
import com.bizzan.bitrade.constant.WithdrawStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 合法钱包提现
 */
@Data
@Entity
@Table
public class LegalWalletWithdraw {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    /**
     * 申请人
     */
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    /**
     * 申请数量
     */
    @Column(columnDefinition = "decimal(18,8) comment '申请总数量'")
    private BigDecimal amount;


    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 处理时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date dealTime;

    /**
     * 提现状态
     */
    @Enumerated(EnumType.ORDINAL)
    private WithdrawStatus status = WithdrawStatus.PROCESSING;

    /**
     * 审核人
     */
    @JoinColumn(name = "admin_id")
    @ManyToOne
    private Admin admin;
    /**
     * 备注
     */
    private String remark;

    /**
     * 收款方式
     */
    @NotNull
    @Enumerated
    private PayMode payMode;

    /**
     * 提现货币
     */
    @NotNull
    @ManyToOne
    @JoinColumn(name = "coin_name")
    private Coin coin;

    /**
     * 支付凭证图片url
     */
    private String paymentInstrument;

    /**
     * 打款时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date remitTime;

}
