package com.bizzan.bitrade.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
public class ExchangeTradeVO {

    private String buyOrderId;

    private String sellOrderId ;

    private String symbol ;

    /**
     * 成交价
     */
    private BigDecimal realPrice;

    /**
     * 成交量
     */
    private BigDecimal amount ;

    private BigDecimal buyerFee ;

    private BigDecimal sellerFee ;

    private String buyerUsername;

    private String sellerUsername ;

    /**
     * 成交时间
     */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    private Date transactionTime ;
}
