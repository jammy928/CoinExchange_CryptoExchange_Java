package com.bizzan.bitrade.entity;

import com.bizzan.bitrade.constant.TransactionTypeEnum;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.ToString;
import org.bson.types.Decimal128;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.math.BigDecimal;
import java.util.Date;



@Data
@Document(collection = "turnover_statistics")
@ToString
public class TurnoverStatistics {
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

    @Enumerated(value = EnumType.STRING)
    private TransactionTypeEnum type ;

    /**
     * 交易区:平台指定的合法币种
     */
    private String unit;

    /**
     * 当天的交易量:币种（coinUnit）的数量
     */
    @Column(name = "Salary1", columnDefinition = "decimal(18,8)")
    private BigDecimal amount ;
    /**
     * 当天收取的币种手续费
     */
    @Column(name = "Salary1", columnDefinition = "decimal(18,8)")
    private BigDecimal fee ;

}
