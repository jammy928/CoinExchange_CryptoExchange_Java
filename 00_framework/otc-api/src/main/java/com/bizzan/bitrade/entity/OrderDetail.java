package com.bizzan.bitrade.entity;

import com.bizzan.bitrade.constant.AdvertiseType;
import com.bizzan.bitrade.constant.OrderStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author GS
 * @date 2018年01月20日
 */
@Builder
@Data
public class OrderDetail {
    private String orderSn;
    private AdvertiseType type;
    private String unit;
    private OrderStatus status;
    private BigDecimal price;
    private BigDecimal money;
    private BigDecimal amount;
    private BigDecimal commission;
    private PayInfo payInfo;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    private Date payTime;
    private int timeLimit;
    private String otherSide;
    private long myId;
    private long hisId;
    private String memberMobile;
}
