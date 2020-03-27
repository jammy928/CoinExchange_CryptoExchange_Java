package com.bizzan.bitrade.vo;

import com.bizzan.bitrade.constant.BooleanEnum;
import com.bizzan.bitrade.constant.WithdrawStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class WithdrawRecordVO {

    private Long id ;
    private Long memberId ;
    private String memberUsername ;
    private String memberRealName ;
    private String phone ;
    private String email ;
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dealTime ;
    private String unit ;
    private BigDecimal totalAmount ;
    private BigDecimal fee ;
    private BigDecimal arrivedAmount ;
    private String transactionNumber ;
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime ;
    private String address ;
    private WithdrawStatus status ;
    private String remark ;
    private BooleanEnum isAuto ;

}
