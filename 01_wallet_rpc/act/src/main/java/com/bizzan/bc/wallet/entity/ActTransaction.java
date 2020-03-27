package com.bizzan.bc.wallet.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ActTransaction {
    private String txid;
    private String from;
    private String to;
    private BigDecimal amount;
    private int type;
}
