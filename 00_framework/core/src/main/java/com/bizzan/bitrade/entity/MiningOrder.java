package com.bizzan.bitrade.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Entity
@Data
public class MiningOrder {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    /**
     * 矿机类型（0：一般矿机，1：邀请燃料矿机）
     */
    private int type = 0;
    /**
     * 矿机图片
     */
    private String image;
    
    /**
     * 矿机名称
     */
    private String title;
    
    /**
     * 领取人ID
     */
    @NotNull
    private Long memberId;
    
    /**
     * 活动ID
     */
    private Long activityId;

    /**
     * 挖矿产出周期（0：日，1：周，2：月，3：年
     */
    private int period = 0;
    
    /**
     * 挖矿状态（0：待启动，1：挖矿中，2：已结束）
     */
    private int miningStatus = 0;
    
    /**
     * 持续天数
     */
    private int miningDays;

    /**
     * 已挖矿天数
     */
    private int miningedDays;
    
    /**
     * 矿机产出币种
     */
    private String miningUnit;
    
    /**
     * 原始日产出
     */
    @Column(columnDefinition = "decimal(18,8) comment '矿机原始日产出'")
    private BigDecimal miningDaysprofit = BigDecimal.ZERO;
    
    /**
     * 当前日产出（因邀请好友购买矿机会增加产出）
     */
    @Column(columnDefinition = "decimal(18,8) comment '矿机当前日产出'")
    private BigDecimal currentDaysprofit = BigDecimal.ZERO;
    
    /**
     * 挖矿总产出
     */
    @Column(columnDefinition = "decimal(18,8) comment '矿机总产出'")
    private BigDecimal totalProfit = BigDecimal.ZERO;


    /**
     * 邀请好友（且购买矿机）产能增加百分比（为0则不增加）
     */
    @Column(columnDefinition = "decimal(24,8) DEFAULT 0 ")
    private BigDecimal miningInvite = BigDecimal.ZERO;
    
    /**
     * 产能增加上限百分比（为0则无上限）
     */
    @Column(columnDefinition = "decimal(24,8) DEFAULT 0 ")
    private BigDecimal miningInvitelimit = BigDecimal.ZERO;
    
    /**
     * 结束日期
     */
    @Column(columnDefinition = "varchar(30) default '2000-01-01 01:00:00'  comment '结束时间'")
    @NotNull
    private Date endTime;
    
    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}
