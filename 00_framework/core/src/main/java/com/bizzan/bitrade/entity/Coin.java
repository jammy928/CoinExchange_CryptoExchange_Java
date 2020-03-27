package com.bizzan.bitrade.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;

import com.bizzan.bitrade.constant.BooleanEnum;
import com.bizzan.bitrade.constant.CommonStatus;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * @author GS
 * @description
 * @date 2017/12/29 14:14
 */
@Entity
@Data
@Table(name = "coin")
@ToString
public class Coin {
    @Id
    @NotBlank(message = "币名称不得为空")
    @Excel(name = "货币", orderNum = "1", width = 20)
    private String name;
    /**
     * 中文
     */
    @Excel(name = "中文名称", orderNum = "1", width = 20)
    @NotBlank(message = "中文名称不得为空")
    private String nameCn;
    /**
     * 缩写
     */
    @Excel(name = "单位", orderNum = "1", width = 20)
    @NotBlank(message = "单位不得为空")
    private String unit;
    /**
     * 状态
     */
    @Enumerated(EnumType.ORDINAL)
    private CommonStatus status = CommonStatus.NORMAL;

    /**
     * 最小提币手续费
     */
    @Excel(name = "最小提币手续费", orderNum = "1", width = 20)
    private double minTxFee;
    /**
     * 对人民币汇率
     */
    @Excel(name = "对人民币汇率", orderNum = "1", width = 20)
    @Column(columnDefinition = "decimal(18,2) default 0.00 comment '人民币汇率'")
    private double cnyRate;
    /**
     * 最大提币手续费
     */
    @Excel(name = "最大提币手续费", orderNum = "1", width = 20)
    private double maxTxFee;
    /**
     * 对美元汇率
     */
    @Excel(name = "对美元汇率", orderNum = "1", width = 20)
    @Column(columnDefinition = "decimal(18,2) default 0.00 comment '美元汇率'")
    private double usdRate;
    /**
     * 是否支持rpc接口
     */
    @Enumerated(EnumType.ORDINAL)
    private BooleanEnum enableRpc = BooleanEnum.IS_FALSE;

    /**
     * 排序
     */
    private int sort;

    /**
     * 是否能提币
     */
    @Enumerated(EnumType.ORDINAL)
    private BooleanEnum canWithdraw;

    /**
     * 是否能充币
     */
    @Enumerated(EnumType.ORDINAL)
    private BooleanEnum canRecharge;


    /**
     * 是否能转账
     */
    @Enumerated(EnumType.ORDINAL)
    private BooleanEnum canTransfer = BooleanEnum.IS_TRUE;

    /**
     * 是否能自动提币
     */
    @Enumerated(EnumType.ORDINAL)
    private BooleanEnum canAutoWithdraw;

    /**
     * 提币阈值
     */
    @Column(columnDefinition = "decimal(18,8) comment '提现阈值'")
    private BigDecimal withdrawThreshold;
    @Column(columnDefinition = "decimal(18,8) comment '最小提币数量'")
    private BigDecimal minWithdrawAmount;
    @Column(columnDefinition = "decimal(18,8) comment '最大提币数量'")
    private BigDecimal maxWithdrawAmount;
    /**
     * 充币阈值
     */
    @Column(columnDefinition = "decimal(18,8) comment '最小充值数量'")
    private BigDecimal minRechargeAmount;

    /**
     * 是否是平台币
     */
    @Enumerated(EnumType.ORDINAL)
    private BooleanEnum isPlatformCoin = BooleanEnum.IS_FALSE;

    /**
     * 是否是合法币种
     */
    @Column(name = "has_legal", columnDefinition = "bit default 0", nullable = false)
    private Boolean hasLegal = false;

    @Transient
    private BigDecimal allBalance ;

    /**
     * 冷钱包地址
     */
    private String coldWalletAddress ;

    @Transient
    private BigDecimal hotAllBalance ;
    
    @Transient
    private Long blockHeight ;

    /**
     * 转账时付给矿工的手续费
     */
    @Column(columnDefinition = "decimal(18,8) default 0 comment '矿工费'")
    private BigDecimal minerFee;

    @Column(columnDefinition = "int default 4 comment '提币精度'")
    private int withdrawScale;
    
    /**
     * 币种资料链接
     */
    private String infolink;
    
    /**
     * 币种简介
     */
    private String information ;
    
    /**
     * 账户类型：0：默认  1：EOS类型
     */
    @Column(columnDefinition = "int default 0 comment '币种账户类型'")
    private int accountType;
    
    /**
     * 充值地址（仅账户类型为EOS类型有效）
     */
    private String depositAddress;
}
