package com.bizzan.bitrade.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Entity
@Data
public class PromotionCard {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    
    /**
     * 创建者ID
     */
    @NotNull
    private Long memberId;
    
    /**
     * 是否有效（0：无效，1：有效）
     */
    private int isEnabled = 1;
    
    /**
     * 卡名
     */
    @NotNull
    private String cardName;
    
    /**
     * 卡券编号
     */
    @Column(unique = true, nullable = false)
    private String cardNo;
    
    /**
     * 卡简介
     */
    private String cardDesc;
    
    /**
     * 卡对应的币
     */
    @ManyToOne
    @JoinColumn(name = "coin_id")
    private Coin coin;
    
    /**
     * 卡数量
     */
    private int count = 0;
    
    /**
     * 已兑换数量
     */
    private int exchangeCount = 0;
    
    /**
     * 单个卡券金额
     */
    @Column(columnDefinition = "decimal(18,8) comment '单个卡券金额'")
    private BigDecimal amount = BigDecimal.ZERO;
    
    /**
     * 所有卡券金额(用户付款参考）
     */
    @Column(columnDefinition = "decimal(18,8) comment '所有卡券总金额'")
    private BigDecimal totalAmount = BigDecimal.ZERO;
    
    /**
     * 是否免费领取（0：不是，1：是）
     */
    private int isFree = 0;
    
    /**
     * 是否锁定（0：未锁定，1：锁定）
     * 0：未锁定的卡券，用户兑换后直接进入余额账户
     * 1：锁定的卡券，用户兑换后进入冻结账户，直到冻结期结束
     */
    private int isLock = 0;
    
    /**
     * 自领卡时，冻结天数
     */
    private int lockDays = 0;
    
    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}
