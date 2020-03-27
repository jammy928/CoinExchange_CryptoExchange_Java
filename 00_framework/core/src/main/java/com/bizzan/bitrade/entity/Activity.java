package com.bizzan.bitrade.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.CreationTimestamp;

import com.bizzan.bitrade.constant.BooleanEnum;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Entity
@Data
@Table
public class Activity {
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    
    /**
     * 启用禁用（决定是否显示在前端）
     */
    @Enumerated(EnumType.ORDINAL)
    @NotNull
    private BooleanEnum status = BooleanEnum.IS_FALSE;
    
    /**
     * 进度：0：未开始  1：进行中  2：派发中  3：已结束
     */
    @NotNull
    private int step = 0;
    
    /**
     * 进度值（例98 = 98%）,type=4（认购活动）时有效
     */
    private int progress = 0;
    
    /**
     * 活动开始时间(抢购发行与分摊发行都需要设置)
     */
    @Column(columnDefinition = "varchar(30) default '2000-01-01 01:00:00'  comment '开始时间'")
    @NotNull
    private String startTime;//开始时间
    
    /**
     * 活动结束时间(抢购发行与分摊发行都需要设置)
     */
    @Column(columnDefinition = "varchar(30) default '2000-01-01 01:00:00'  comment '结束时间'")
    @NotNull
    private String endTime;//开始时间
    
    /**
     * 活动类型(0:未知，1：首发抢购，2：首发分摊，3：持仓瓜分，4：自由认购，5：云矿机）
     */
    @NotNull
    private int type;
    

    /**
     * 要求一级好友不能低于该数（0：不限制，>0:限制）
     */
    @NotNull
    private int leveloneCount=0;
    
    /**
     * 发行总量
     */
    @NotNull
    @Column(columnDefinition = "decimal(24,8) DEFAULT 0 ")
    private BigDecimal totalSupply = BigDecimal.ZERO;
    
    /**
     * 已售数量
     */
    @Column(columnDefinition = "decimal(24,8) DEFAULT 0 ")
    private BigDecimal tradedAmount = BigDecimal.ZERO;
    
    /**
     * 冻结资产数量
     */
    @Column(columnDefinition = "decimal(26,8) DEFAULT 0 ")
    private BigDecimal freezeAmount = BigDecimal.ZERO;
    
    /**
     * 发行价格
     */
    @Column(columnDefinition = "decimal(18,8) DEFAULT 0 ")
    private BigDecimal price = BigDecimal.ZERO;
    
    /**
     * 价格精度
     */
    private int priceScale = 0;
    
    /**
     * 发售单位（如BTC、ETH等）
     */
    @NotNull
    private String unit;
    
    /**
     * 接受币种（如BTC、ETH等
     */
    @NotNull
    private String acceptUnit;
    
    /**
     * 数量精度
     */
    private int amountScale = 0;
    
    /**
     * 单人限购总量上限（0：不限制）
     */
    @Column(columnDefinition = "decimal(24,8) DEFAULT 0 ")
    private BigDecimal maxLimitAmout = BigDecimal.ZERO;
    
    /**
     * 单人限购总量下限（0：不限制）
     */
    @Column(columnDefinition = "decimal(24,8) DEFAULT 0 ")
    private BigDecimal minLimitAmout = BigDecimal.ZERO;
    
    /**
     * 购买条件（如持有XXX币需大于ZZ才可以参与购买）
     */
    @Column(columnDefinition = "decimal(24,8) DEFAULT 0 ")
    private BigDecimal holdLimit = BigDecimal.ZERO;
    
    /**
     * 持有XXX币种单位
     */
    private String holdUnit;
    
    /**
     * 限制购买次数（0：不限制）
     */
    private int limitTimes = 0;
    

    /**
     * 挖矿产出周期（0：日，1：周，2：月，3：年）
     */
    private int miningPeriod = 0;
    
    /**
     * 云矿机：挖矿持续周期数（仅type = 5时设置有效，如30天/周/月/年、60天等）
     * 参数命名与实际意义不同，请注意
     */
    private int miningDays = 0;
    
    /**
     * 云矿机：每天/周/月产出多少
     */
    @Column(columnDefinition = "decimal(24,8) DEFAULT 0 ")
    private BigDecimal miningDaysprofit = BigDecimal.ZERO;

    /**
     * 云矿机：云矿机所挖币种
     */
    @NotNull
    private String miningUnit;

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
     * 其他活动配置信息（JSON）
     */
    private String settings;

    /**
     * 活动标题
     */
    @NotNull
    private String title;
    
    /**
     * 活动标题(英文，可选)
     */
    private String titleEN;
    
    /**
     * 活动简介
     */
    @NotNull
    private String detail;
    
    /**
     * 活动简介（英文，可选)
     */
    private String detailEN;
    
    /**
     * 活动详情
     */
    @Column(columnDefinition="TEXT")
    @Basic(fetch=FetchType.LAZY)
    private String content = "";
    
    /**
     * 活动详情（英文，可选）
     */
    @Column(columnDefinition="TEXT")
    @Basic(fetch=FetchType.LAZY)
    private String contentEN = "";
    
    /**
     * 列表展示图片（Small）
     */
    private String smallImageUrl;
    
    /**
     * Banner活动图片（Banner）
     */
    private String bannerImageUrl;
    
    /**
     * 公告链接（活动详情链接）
     */
    private String noticeLink;

    /**
     * 活动链接（实际参与页面链接）
     */
    private String activityLink;
    
    /**
     * 创建时间（用于排序使用）
     */
    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}
