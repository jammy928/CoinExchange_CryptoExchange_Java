package com.bizzan.bitrade.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Entity
@Data
@Table
public class ActivityOrder {
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
	
	/**
	 * 活动ID
	 */
	private Long activityId;
	
	@Transient
	private String activityName;
	
	private Long memberId;
	
	/**
     * 状态：0：临时  1：未成交  2：已成交  3：撤销
     */
    @NotNull
    private int state = 1;
    
    /**
     * 活动类型(0:未知，1：首发抢购，2：首发分摊，3：持仓瓜分，4：自由认购）
     */
    @NotNull
    private int type = 0;
    
    /**
     * 当type=3时有效，代表冻结资产数量
     */
    @Column(columnDefinition = "decimal(24,8) DEFAULT 0 ")
	private BigDecimal freezeAmount = BigDecimal.ZERO;
	
    /**
     * 获得币种数量
     */
    @Column(columnDefinition = "decimal(24,8) DEFAULT 0 ")
	private BigDecimal amount = BigDecimal.ZERO;
	
	/**
	 * 获得币种价格（type=3时该字段无效）
	 */
    @Column(columnDefinition = "decimal(24,8) DEFAULT 0 ")
	private BigDecimal price = BigDecimal.ZERO;
	
    //成交额，type=3时无效
    @Column(columnDefinition = "decimal(26,16) DEFAULT 0 ")
    private BigDecimal turnover = BigDecimal.ZERO;
    
    //币单位
    private String coinSymbol;
    
    //结算单位(type=3时，此为冻结资产符号）
    private String baseSymbol;
    
    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    
    
}
