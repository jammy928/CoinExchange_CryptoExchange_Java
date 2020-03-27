package com.bizzan.bitrade.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.Transient;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Entity
@Data
public class RedEnvelope {
	
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    
    /**
     * 红包编号
     */
    @Column(unique = true, nullable = false)
    private String envelopeNo;
    
    /**
     * 发起人ID
     * 
     */
    private Long memberId;
    
    /**
     * 红包类型（0：随机，1：定额）
     */
    private int type = 0;
    
    /**
     * 是否邀请拆分红包（0：否，1：是）
     * 此类红包当用户邀请一名一级好友注册，可额外拥有一次拆分机会
     */
    private int invite = 0;
    
    /**
     * 是否平台发出（0：否，1：是）
     * 由平台发出（一般用于上币红包、节日红包等）
     */
    private int plateform = 0;
    
    /**
     * 随机红包类型，最大可领取额度
     */
    @Column(columnDefinition = "decimal(18,8) comment '最大随机领取额'")
    private BigDecimal maxRand = BigDecimal.ZERO;
    
    /**
     * 红包总额
     */
    @Column(columnDefinition = "decimal(18,8) comment '红包总额'")
    private BigDecimal totalAmount = BigDecimal.ZERO;
    
    /**
     * 领取总额
     */
    @Column(columnDefinition = "decimal(18,8) comment '领取总额'")
    private BigDecimal receiveAmount = BigDecimal.ZERO;
    
    /**
     * 红包数量
     */
    private int count = 0;
    
    /**
     * 红包Logo图片
     * 用户发出的红包默认为头像
     * 平台发出的红包可自定义
     */
    private String logoImage;

    /**
     * 红包背景图
     * 用户发出的红包为默认图片
     * 平台发出的红包可自定义
     */
    private String bgImage;
    
    /**
     * 红包名(默认：来自133****3829的红包)
     * 平台发出的红包可自定义
     */
    private String name;
    
    /**
     * 红包描述
     */
    @Column(columnDefinition="TEXT")
    @Basic(fetch=FetchType.LAZY)
    private String detail;

    /**
     * 红包币种单位
     */
    private String unit;
    
    /**
     * 已领取数量
     */
    private int receiveCount = 0;
    
    /**
     * 领取状态（0：领取中，1：已领完，2：过期结束）
     */
    private int state = 0;
    
    /**
     * 过期时间（默认24H）
     * 平台发出可自定义
     */
    private int expiredHours = 24;
    
    /**
     * 创建时间
     */
    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    
    /**
     * 邀请人（电话/邮箱）
     */
    @Transient
    private String inviteUser;
    
    /**
     * 邀请人（电话/邮箱）
     */
    @Transient
    private String inviteUserAvatar;
}
