package com.bizzan.bitrade.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BusinessStatisticsVO {

    private Long advertisementNum ;

    private BigDecimal money ;

    private BigDecimal amount ;

    /**
     * 申诉次数
     */
    private Long complainantNum  ;

    /**
     * 被申诉次数
     */
    private Long defendantNum ;
}
