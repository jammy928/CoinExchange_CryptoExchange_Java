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
public class PromotionCardOrder {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    /**
     * 记录对应的卡
     */
    @ManyToOne
    @JoinColumn(name = "card_id")
    private PromotionCard card;
    
    /**
     * 领取人ID
     */
    @NotNull
    private Long memberId;
    
    /**
     * 是否免费领取（0：不是，1：是）
     */
    private int isFree = 0;
    
    private int isLock = 0;
    
    private int lockDays = 0;
    
    /**
     * 领取状态（0：未知，1：已领取未到账（冻结状态），2：已领取且解冻（已到账），3：无效）
     */
    private int state = 0;
    
    @Column(columnDefinition = "decimal(18,8) comment '兑换金额'")
    private BigDecimal amount = BigDecimal.ZERO;
    
    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}
