package com.bizzan.bitrade.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

/**
 * 推广合伙人总榜单（每日更新）
 * 与日、周、月榜单(MemberInviteStasticRank表)的主要区别是总榜单保存返佣金额，但分类榜单不会保存
 * 同时，总榜单无历史快照，而分类榜单中可以查询历史日期的榜单
 * @author shaox
 * 
 */
@Entity
@Data
@Table(uniqueConstraints ={@UniqueConstraint(columnNames="memberId")})
public class MemberInviteStastic {
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
	
	private Long memberId;
	
	/**
	 * 用户账户标识，手机或邮箱
	 */
	private String userIdentify;
	
	/**
     * 返佣总额（BTC）
     */
	@JsonIgnore
    @Column(columnDefinition = "decimal(24,8) DEFAULT 0 ")
    private BigDecimal btcReward = BigDecimal.ZERO;
    
    /**
     * 返佣总额（ETH）
     */
	@JsonIgnore
    @Column(columnDefinition = "decimal(24,8) DEFAULT 0 ")
    private BigDecimal ethReward = BigDecimal.ZERO;
    
    /**
     * 返佣总额（USDT）
     */
	@JsonIgnore
    @Column(columnDefinition = "decimal(24,8) DEFAULT 0 ")
    private BigDecimal usdtReward = BigDecimal.ZERO;
    
    /**
     * 其他返佣（json格式{"LTC":123, "EOS":22.3})
     */
    private String otherReward;
    
    /**
     * 折合返佣总额（估算折合USDT，用户总额排名比较）
     */
    @Column(columnDefinition = "decimal(24,8) DEFAULT 0 ")
    private BigDecimal estimatedReward = BigDecimal.ZERO;
    
    /**
     * 额外奖励（折合USDT）
     */
    @Column(columnDefinition = "decimal(24,8) DEFAULT 0 ")
    private BigDecimal extraReward = BigDecimal.ZERO;
    
    /**
     * 邀请一级好友数量
     */
    private int levelOne;
    
    /**
     * 邀请二级好友数量
     */
    private int levelTwo;
    
    /**
     * 是否机器人（0：否，1：是）
     */
    @JsonIgnore
    private int isRobot = 0;

    /**
     * 统计日期
     */
    @Column(columnDefinition = "varchar(30) default '2000-01-01 01:00:00'  comment '统计日期'")
    @NotNull
    private String stasticDate;
}
