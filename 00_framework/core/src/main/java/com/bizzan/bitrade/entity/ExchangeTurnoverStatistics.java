package com.bizzan.bitrade.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Column;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.math.BigDecimal;
import java.util.Date;

@Document(collection="exchange_turnover_statistics")
@Data
@ToString
public class ExchangeTurnoverStatistics {

    /**
     * 成交日期:以天为最小单位统计
     */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    private Date date ;

    private int year ;

    @Max(value = 12)
    @Min(value = 1)
    private int month ;

    @Max(value = 31)
    @Min(value = 1)
    private int day ;

    private String baseSymbol ;

    private String coinSymbol ;

    @Column(name = "Salary1", columnDefinition = "decimal(18,8)")
    private BigDecimal amount ;

    @Column(name = "Salary1", columnDefinition = "decimal(18,8)")
    private BigDecimal money ;

}
