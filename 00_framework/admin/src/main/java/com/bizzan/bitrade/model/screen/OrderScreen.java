package com.bizzan.bitrade.model.screen;

import lombok.Data;

import java.math.BigDecimal;

import com.bizzan.bitrade.constant.AdvertiseType;

@Data
public class OrderScreen extends OtcOrderTopScreen{
    private String orderSn;
    private BigDecimal minNumber ;
    private BigDecimal maxNumber ;
    private String memberName;//用户名和真名的关键字即可
    private String customerName;//用户名和真名的关键字即可
    private BigDecimal minMoney;
    private BigDecimal maxMoney;
    private AdvertiseType advertiseType ;
}
