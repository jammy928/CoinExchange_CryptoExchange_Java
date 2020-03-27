package com.bizzan.bitrade.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Column;
import java.math.BigDecimal;
import java.util.Date;

@Data
@ToString
public class ExchangeTurnoverStatisticsVO {

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    private Date date ;

    private String baseSymbol ;

    private String coinSymbol ;

    private BigDecimal amount ;

    private BigDecimal money ;
}
