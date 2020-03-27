package com.bizzan.bitrade.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

@Data
@ToString
public class TurnoverStatisticsVO {

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    private Date date ;

    private String unit;

    private BigDecimal amount   = BigDecimal.ZERO;

    private BigDecimal money   = BigDecimal.ZERO;
}
