package com.bizzan.bitrade.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelEntity;

import com.bizzan.bitrade.constant.*;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 广告实体类
 *
 * @author GS
 * @date 2017年12月07日
 */
@Entity
@Data
public class Advertise implements Serializable {
    @Excel(name = "编号", orderNum = "1", width = 25)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    /**
     * 交易价格(及时变动)
     */
    @Excel(name = "价格", orderNum = "1", width = 25)
    @DecimalMin(value = "0", message = "{Advertise.price.more}")
    @Column(columnDefinition = "decimal(18,2) comment '交易价格'")
    private BigDecimal price;

    /**
     * 广告类型 0:买入 1:卖出
     */
    @Excel(name = "广告类型", orderNum = "1", width = 25)
    @Enumerated(EnumType.ORDINAL)
    @NotNull(message = "{Advertise.advertiseType.null}")
    private AdvertiseType advertiseType;

    /**
     * 广告拥有者
     */
    @ExcelEntity
    @JoinColumn(name = "member_id", nullable = false)
    @ManyToOne
    private Member member;

    @Excel(name = "广告创建时间", orderNum = "1", width = 25)
    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @Excel(name = "广告最后更新时间", orderNum = "1", width = 25)
    @UpdateTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    /**
     * 币种
     */
    @ExcelEntity
    @JoinColumn(name = "coin_id")
    @NotNull(message = "{Advertise.coin.null}")
    @ManyToOne
    private OtcCoin coin;

    /**
     * 国家
     */
    @NotNull(message = "{Advertise.country.null}")
    @JoinColumn(name = "country")
    @ManyToOne
    private Country country;

    /**
     * 最低单笔交易额
     */
    @DecimalMin(value = "0", message = "{Advertise.minLimit.min}")
    @Column(columnDefinition = "decimal(18,2) comment '最低单笔交易额'")
    private BigDecimal minLimit;

    /**
     * 最高单笔交易额
     */
    @DecimalMin(value = "0", message = "{Advertise.maxLimit.min}")
    @Column(columnDefinition = "decimal(18,2) comment '最高单笔交易额'")
    private BigDecimal maxLimit;

    /**
     * 备注
     */

    private String remark;

    /**
     * 付款期限，单位分钟
     */
    @DecimalMin(value = "1", message = "{Advertise.timeLimit.min}")
    private Integer timeLimit;

    /**
     * 溢价百分比
     */
    @Column(columnDefinition = "decimal(18,6) comment '溢价百分比'")
    private BigDecimal premiseRate = BigDecimal.ZERO;

    /**
     * 广告级别
     */
    @Excel(name = "广告级别", orderNum = "1", width = 25)
    @Enumerated(EnumType.ORDINAL)
    private AdvertiseLevel level;

    /**
     * 付费方式(用英文逗号隔开)
     */
    @Excel(name = "付费方式", orderNum = "1", width = 25)
    private String payMode;

    @Version
    private Long version;

    /**
     * 广告上下架状态
     */
    @Excel(name = "广告状态", orderNum = "1", width = 25)
    @Enumerated(EnumType.ORDINAL)
    private AdvertiseControlStatus status = AdvertiseControlStatus.PUT_OFF_SHELVES;

    /**
     * 价格类型
     */
    @NotNull(message = "{Advertise.priceType.null}")
    @Enumerated(EnumType.ORDINAL)
    private PriceType priceType;

    @DecimalMin(value = "0", message = "{Advertise.number.min}")
    @Column(columnDefinition = "decimal(18,8) comment '计划数量'")
    private BigDecimal number;

    @Column(columnDefinition = "decimal(18,8) comment '交易中数量'")
    private BigDecimal dealAmount = BigDecimal.ZERO;

    @Column(columnDefinition = "decimal(18,8) comment '计划剩余数量'")
    private BigDecimal remainAmount;

    /**
     * 是否开启自动回复
     */
    @Enumerated(EnumType.ORDINAL)
    private BooleanEnum auto = BooleanEnum.IS_FALSE;

    /**
     * 市场价
     */
    @Transient
    private BigDecimal marketPrice;

    /**
     * 自动回复内容
     */
    private String autoword;
    private String limitMoney;
    private String username;
    private String coinUnit;

}
