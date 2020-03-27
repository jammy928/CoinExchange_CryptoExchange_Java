package com.bizzan.bitrade.entity;

import com.bizzan.bitrade.constant.LegalWalletState;
import com.bizzan.bitrade.constant.PayMode;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 合法钱包充值
 */
@Entity
@Data
@Table
public class LegalWalletRecharge {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @NotNull
    @Column(columnDefinition = "decimal(18,2) comment '充值金额'")
    private BigDecimal amount;
    /**
     * 支付凭证图片url
     */
    @NotNull
    private String paymentInstrument;
    /**
     * 充值货币
     */
    @NotNull
    @ManyToOne
    @JoinColumn(name = "coin_name")
    private Coin coin;
    /**
     * 充值状态
     */
    @NotNull
    @Enumerated
    private LegalWalletState state;

    /**
     * 支付方式
     */
    @NotNull
    @Enumerated
    private PayMode payMode;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date creationTime;

    /**
     * 处理时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date dealTime;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private Admin admin;

    @UpdateTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

}
