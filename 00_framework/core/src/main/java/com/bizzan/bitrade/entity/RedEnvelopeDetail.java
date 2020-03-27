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
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Entity
@Data
public class RedEnvelopeDetail {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    
    /**
     * 领取人ID
     */
    private Long memberId;

    /**
     * 红包ID
     */
    private Long envelopeId;
    
    /**
     * 领取数额
     */
    @Column(columnDefinition = "decimal(18,8) comment '领取数额'")
    private BigDecimal amount = BigDecimal.ZERO;
    
    /**
     * 是否机器人（0：不是，> 1：是）
     */
    private int cate = 0;
    
    /**
     * 领取时间
     */
    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    
    /**
     * 领取人的邀请码，此字段仅用于用户领取后保存推广码
     */
    @Transient
    private String promotionCode;
    
    /**
     * 用户领取标识（手机orEmail）
     */
    private String userIdentify;
}
