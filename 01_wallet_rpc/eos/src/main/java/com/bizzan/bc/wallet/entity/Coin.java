package com.bizzan.bc.wallet.entity;


import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;

@Data
public class Coin {
    private String name;
    private String unit;
    private String rpc;
    private String withdrawAddress;
    private String withdrawWallet;
    private String withdrawWalletPassword;
    private BigDecimal minCollectAmount;
    private BigInteger gasLimit;
    private String depositAddress;
}
