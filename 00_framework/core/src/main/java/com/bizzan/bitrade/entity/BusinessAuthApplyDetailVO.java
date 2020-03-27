package com.bizzan.bitrade.entity;

import com.alibaba.fastjson.JSONObject;
import com.bizzan.bitrade.constant.CertifiedBusinessStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class BusinessAuthApplyDetailVO {

    private Long id ;

    /**
     * 认证信息
     */
    private JSONObject info ;

    /**
     * 认证状态
     */
    @Enumerated(value = EnumType.ORDINAL)
    private CertifiedBusinessStatus status ;

    /**
     * 认证时间
     */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    private Date checkTime ;

    /**
     * 真名
     */
    private String realName ;

    /**
     * 认证失败理由
     */
    private String detail ;

    private BigDecimal amount ;

    @JsonIgnore
    private String authInfo ;
}
