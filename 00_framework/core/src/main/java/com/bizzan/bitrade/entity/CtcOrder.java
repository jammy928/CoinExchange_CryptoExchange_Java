package com.bizzan.bitrade.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Entity
@Data
@Table
public class CtcOrder {
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
	
	@Column(unique = true, nullable = false)
    private String orderSn;
	
	/**
	 * 订单发起用户
	 */
	@ManyToOne
    @JoinColumn(name = "member_id")
	private Member member;
	
	/**
	 * 用户买入时，此为承兑商实名
	 * 用户卖出时，此为用户实名
	 */
	private String realName;
	
	/**
	 * 承兑商用户ID
	 */
	@JsonIgnore
	@ManyToOne
    @JoinColumn(name = "acceptor_id")
	private Member acceptor;
	
	/**
	 * 交易价格
	 */
	@Column(columnDefinition = "decimal(18,2) comment '价格'")
    private BigDecimal price;
	
	/**
	 * 交易金额
	 */
	@Column(columnDefinition = "decimal(18,2) comment '交易金额'")
	private BigDecimal money;
	
	/**
	 * 买入卖出数量
	 */
	@DecimalMin(value = "0.00000001", message = "交易数量")
    private BigDecimal amount;
	
	/**
	 * 币种单位
	 */
	@NotBlank(message = "单位不得为空")
    private String unit;
	
	/**
     * 状态(0:未接单  1：已接单  2：已付款  3：已完成  4：已取消）
     * 买入时，自动变成已接单，用户打款后，自己设置状态为已付款，承兑商审核，通过则放币且状态为已完成，否则为已取消
     * 卖出时，自动变成未接单，承兑商接单，设置状态为已接单，承兑商打款，设置为已付款&已完成
     * 订单状态为未接单，且距离创建时间超过30分钟，自动取消订单
     * 订单状态为已接单状态，且距离创建时间超过30分钟，自动取消订单
     * 只有未接单状态的订单，用户有权利取消订单，其他状态则仅有平台或承兑商有权执行取消操作
     */
    private int status;
    
    /**
     * 接单/确认时间（用户买入时，确认时间与创建时间一致）
     */
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date confirmTime;
    
    /**
     * 取消原因
     */
    private String cancelReason;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 取消时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date cancelTime;
    
    /**
     * 完成时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date payTime;
    
    /**
     * 完成时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date completeTime;
    
    /**
     * 订单类型 0:买入 1:卖出
     */
    private int direction = 0;
    
    /**
     * 付费方式(只能选择一个：alipay,bank,wechatpay)，默认bank
     */
    @NotBlank(message = "付费方式不能为空")
    private String payMode = "bank";
    
    /**
     * 支付宝
     * 买入时：保存的是承兑商收款方式
     * 卖出时：保存的是用户收款方式
     */
    @Embedded
    private Alipay alipay;
    
    /**
     * 支付宝
     * 买入时：保存的是承兑商收款方式
     * 卖出时：保存的是用户收款方式
     */
    @Embedded
    private BankInfo bankInfo;
    
    /**
     * 支付宝
     * 买入时：保存的是承兑商收款方式
     * 卖出时：保存的是用户收款方式
     */
    @Embedded
    private WechatPay wechatPay;
    
    /**
     * 支付宝
     * 买入时：保存的是承兑商收款方式
     * 卖出时：保存的是用户收款方式
     */
	@CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
	
	/**
	 * 系统当前时间
	 */
	@Transient
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date currentTime;
}
