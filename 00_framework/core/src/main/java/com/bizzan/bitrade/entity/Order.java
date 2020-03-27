package com.bizzan.bitrade.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;

import com.bizzan.bitrade.constant.AdvertiseType;
import com.bizzan.bitrade.constant.OrderStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 场外交易订单
 *
 * @author GS
 * @date 2017年12月11日
 */
@Entity
@Data
@Table(name = "otc_order")
public class Order {
    @Excel(name = "订单编号", orderNum = "1", width = 20)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    /**
     * 订单号
     */
    @Excel(name = "订单号", orderNum = "1", width = 20)
    @Column(unique = true, nullable = false)
    private String orderSn;

    /**
     * 广告类型 0:买入 1:卖出
     */
    @Excel(name = "订单类型", orderNum = "1", width = 20)
    @Enumerated(EnumType.ORDINAL)
    @NotNull(message = "广告类型不能为空")
    private AdvertiseType advertiseType;

    /**
     * 创建时间
     */
    @Excel(name = "创建时间", orderNum = "1", width = 20)
    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 广告拥有者id
     */
    @NotNull
    private Long memberId;
    /**
     * 广告拥有者姓名
     */
    private String memberName;
    private String memberRealName;

    /**
     * 交易对象id
     */
    @NotNull
    private Long customerId;

    /**
     * 交易对象用户名
     */
    private String customerName;
    private String customerRealName;

    /**
     * 币种
     */
    @JoinColumn(name = "coin_id", nullable = false)
    @ManyToOne
    private OtcCoin coin;

    /**
     * 价格
     */
    @DecimalMin(value = "0", message = "价格必须大于等于0")
    @Column(columnDefinition = "decimal(18,2) comment '价格'")
    private BigDecimal price;

    /**
     * 最高单笔交易额
     */
    @DecimalMin(value = "0", message = "最高交易额必须大于等于0")
    @Column(columnDefinition = "decimal(18,2) comment '最高交易额'")
    private BigDecimal maxLimit;

    /**
     * 国家
     */
    @NotBlank
    private String country;

    /**
     * 最低单笔交易额
     */
    @DecimalMin(value = "0", message = "最低交易额必须大于等于0")
    @Column(columnDefinition = "decimal(18,2) comment '最低交易额'")
    private BigDecimal minLimit;

    /**
     * 顾客需求
     */
    private String remark;

    /**
     * 付款期限，单位分钟
     */
    @DecimalMin(value = "1", message = "付款期限必须大于等于1")
    private Integer timeLimit;

    /**
     * 交易金额
     */
    @DecimalMin(value = "0.01", message = "交易金额必须大于等于0.01")
    @Column(columnDefinition = "decimal(18,2) comment '交易金额'")
    private BigDecimal money;

    /**
     * 交易数量
     */
    @Column(columnDefinition = "decimal(18,8) comment '交易数量'")
    @DecimalMin(value = "0.00000001", message = "交易数量有误")
    private BigDecimal number;

    /**
     * 手续费
     */
    @Column(columnDefinition = "decimal(18,8) comment '手续费'")
    private BigDecimal commission;

    /**
     * 状态
     */
    @Enumerated(EnumType.ORDINAL)
    private OrderStatus status;

    /**
     * 付款时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date payTime;

    /**
     * 付费方式(用英文逗号隔开)
     */
    @NotBlank(message = "付费方式不能为空")
    private String payMode;

    @NotNull
    private Long advertiseId;

    /**
     * 订单取消时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date cancelTime;
    /**
     * 放行时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date releaseTime;
    @Embedded
    private Alipay alipay;
    @Embedded
    private BankInfo bankInfo;
    @Embedded
    private WechatPay wechatPay;

    @JsonIgnore
    @Version
    private Long version;
}
