package com.bizzan.bitrade.vo;

import com.bizzan.bitrade.constant.AdvertiseType;
import com.bizzan.bitrade.constant.AppealStatus;
import com.bizzan.bitrade.constant.BooleanEnum;
import com.bizzan.bitrade.constant.OrderStatus;
import com.bizzan.bitrade.entity.Appeal;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

@Data
public class AppealVO {

    /**
     * 申诉单id
     */
    private BigInteger appealId ;

    /**
     * 广告创建者
     */
    private String advertiseCreaterUserName ;

    private String advertiseCreaterName ;

    /**
     *
     */
    private String customerUserName ;

    private String customerName ;

    /**
     * 被投诉者真名
     */
    private String associateName ;
    /**
     * 被投诉者用户名
     */
    private String associateUsername ;
    /**
     * 申诉者用户名
     */
    private String initiatorUsername ;
    /**
     * 申诉者真名
     */
    private String initiatorName ;
    /**
     * 订单手续费
     */
    private BigDecimal fee ;

    /**
     * 订单数量
     */
    private BigDecimal number ;

    /**
     * 订单金额
     */
    private BigDecimal money ;

    /**
     * 订单编号
     */
    private String orderSn ;

    /**
     * 交易时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date transactionTime;

    /**
     * 投诉时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 处理时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date dealTime;

    /**
     * 付款方式
     */
    private String payMode;

    /**
     * 币种名称
     */
    private String coinName ;

    /**
     * 订单状态
     */
 /*   @Enumerated(EnumType.STRING)*/
    private int orderStatus ;

    /**
     * 是否胜诉
     */
    /*@Enumerated(EnumType.STRING)*/
    private int isSuccess;

    /**
     * 订单类型
     */
    /*@Enumerated(EnumType.STRING)*/
    private int advertiseType;

    private int status;

    private String remark;
}
