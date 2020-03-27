package com.bizzan.bitrade.entity;


import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import com.bizzan.bitrade.constant.BooleanEnum;

import java.math.BigDecimal;

@Entity
@Data
public class ExchangeCoin {
    //交易币种名称，格式：BTC/USDT
    @NotNull(message = "交易对不能为空")
    @Id
    private String symbol;
    //交易币种符号
    private String coinSymbol;
    //结算币种符号，如USDT
    private String baseSymbol;
    //状态，1：启用，2：禁止
    private int enable;
    
    //交易手续费
    @Column(columnDefinition = "decimal(8,4) comment '交易手续费'")
    private BigDecimal fee;
    //排序，从小到大
    private int sort;
    //交易币小数精度
    private int coinScale;
    //基币小数精度
    private int baseCoinScale;
    @Column(columnDefinition = "decimal(18,8) default 0 comment '卖单最低价格'")
    private BigDecimal minSellPrice;
    
    @Column(columnDefinition = "decimal(18,8) default 0 comment '最高买单价'")
    private BigDecimal maxBuyPrice;

    @Enumerated(EnumType.ORDINAL)
    @Column(columnDefinition = "int(11) default 1 comment '是否启用市价卖'")
    private BooleanEnum  enableMarketSell = BooleanEnum.IS_TRUE;

    @Enumerated(EnumType.ORDINAL)
    @Column(columnDefinition = "int(11) default 1 comment '是否启用市价买'")
    private BooleanEnum  enableMarketBuy = BooleanEnum.IS_TRUE;

    /**
     * 最大交易时间
     */
    @Column(columnDefinition = "int(11) default 0 comment '委托超时自动下架时间，单位为秒，0表示不过期'")
    private int maxTradingTime;

    /**
     * 最大在交易中的委托数量
     */
    @Column(columnDefinition = "int(11) default 0 comment '最大允许同时交易的订单数，0表示不限制'")
    private int maxTradingOrder;

    /**
     * 机器人类型
     * 0：一般机器人，适用于有外部市场价格做参考
     * 1：平价机器人，适用于只有一个价格的机器人
     * 2：控盘机器人，适用于仅在本交易所的奇人
     */
    @Column(columnDefinition = "int(11) default 0 comment '机器人类型'")
    private int robotType;
    
    /**
     * 标签位，用于推荐，排序等,默认为0，1表示推荐，
     */
    @Column(columnDefinition = "int(11) default 0 ")
    private int flag;

    @Column(columnDefinition = "decimal(18,8) default 0 comment '最小成交额'")
    private BigDecimal minTurnover;

    /**
     * 交易区域
     */
    @Column(columnDefinition = "int(11) default 0 ")
    private int zone = 0;

    /**
     * 最小下单量，0表示不限制
     */
    @Column(columnDefinition = "decimal(18,8) default 0 comment '最小下单量'")
    private BigDecimal minVolume =BigDecimal.ZERO;
    /**
     * 最大下单量，0表示不限制
     */
    @Column(columnDefinition = "decimal(18,8) default 0 comment '最大下单量'")
    private BigDecimal maxVolume =BigDecimal.ZERO;
    
    /**
     * 发行活动类型 1：无活动，2：抢购发行，3：分摊发行
     */
    @Column(columnDefinition = "int(11) default 1  comment '发行活动类型 1:无活动,2:抢购发行,3:分摊发行'")
    private ExchangeCoinPublishType publishType = ExchangeCoinPublishType.NONE;
    
    /**
     * 活动开始时间(抢购发行与分摊发行都需要设置)
     */
    @Column(columnDefinition = "varchar(30) default '2000-01-01 01:00:00'  comment '开始时间'")
    private String startTime;//开始时间
    
    /**
     * 活动结束时间(抢购发行与分摊发行都需要设置)
     */
    @Column(columnDefinition = "varchar(30) default '2000-01-01 01:00:00'  comment '结束时间'")
    private String endTime;//开始时间
    
    /**
     * 活动清盘时间(抢购发行与分摊发行都需要设置)
     */
    @Column(columnDefinition = "varchar(30) default '2000-01-01 01:00:00'  comment '清盘时间'")
    private String clearTime;//清盘时间
    
    /**
     * 分摊发行价格(抢购发行与分摊发行都需要设置)
     */
    @Column(columnDefinition = "decimal(18,8) default 0 comment ' 分摊发行价格'")
    private BigDecimal publishPrice;
    
    /**
     * 活动发行数量(抢购发行与分摊发行都需要设置)
     */
    @Column(columnDefinition = "decimal(18,8) default 0 comment ' 活动发行数量'")
    private BigDecimal publishAmount;
    
    //前台可见状态，1：可见，2：不可见
    @Column(columnDefinition = "int(11) default 1 comment ' 前台可见状态'")
    private int visible;
    
    //是否可交易，1：可交易，2：不可交易
    @Column(columnDefinition = "int(11) default 1 comment ' 是否可交易'")
    private int exchangeable;
    // enable/visible/exchangeable三个状态的区别
    // enable:决定币种是否进入撮合交易中心与K线生成中心，所有enable=2的币种是不在撮合交易中心存在的
    // visible: 仅仅决定是否显示在前端交易面板中
    // exchangeable: 是否可交易的，也就是enable即使状态为可用，也不一定能交易
    /**
     * 服务器当前市价戳
     */
    @Transient
    private Long currentTime;
    
    /**
     * 交易引擎状态（0：不可用，1：可用
     */
    @Transient
    private int engineStatus = 0;
    
    /**
     * 行情引擎状态（0：不可用，1：可用
     */
    @Transient
    private int marketEngineStatus = 0;
    
    /**
     * 交易机器人状态（0：非运行中，1：运行中）
     */
    @Transient
    private int exEngineStatus = 0;
}
