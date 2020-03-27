package com.bizzan.bitrade.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TempWealthInfo {

    private BigDecimal fee ;

    private BigDecimal realFee ;

    private BigDecimal discountFee ;

    private BigDecimal mineAmount ;

    private BigDecimal poundageAmountEth ;

    private String orderId ;

    private Long memberId ;

    private String symbol ;
    private String direction ;
    private String type ;
    private String transactionCoin ;

}
