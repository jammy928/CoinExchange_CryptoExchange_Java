package com.bizzan.bitrade.model.screen;

import lombok.Data;

@Data
public class ExchangeTradeScreen {

    private String buyerUsername ;

    private String sellerUsername ;

    private String buyOrderId ;

    private String sellOrderId ;

    private String symbol ;

}
