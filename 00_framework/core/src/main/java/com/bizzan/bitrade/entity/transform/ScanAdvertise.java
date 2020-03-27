package com.bizzan.bitrade.entity.transform;

import com.bizzan.bitrade.constant.AdvertiseType;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author GS
 * @date 2018年01月12日
 */
@Builder
@Data
public class ScanAdvertise {
    private String memberName;
    private String avatar;
    private long advertiseId;
    /**
     * 交易次数
     */
    private int transactions;
    /**
     * 目前价格
     */
    private BigDecimal price;
    private BigDecimal minLimit;
    private BigDecimal maxLimit;
    /**
     * 剩余币数
     */
    private BigDecimal remainAmount;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    private String payMode;
    private long coinId;
    private String unit;
    private String coinName;
    private String coinNameCn;
    /**
     * 0:未实名用户，1：实名用户，2：认证商家
     */
    private int level;

    private AdvertiseType advertiseType;

    /**
     * 查询广告类型字段
     */
    private int advType;
    private BigDecimal premiseRate;

}
