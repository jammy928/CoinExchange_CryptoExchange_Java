package com.bizzan.bc.wallet.entity;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class Deposit {
    private String txid;
    private String blockHash;
    private Long blockHeight;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date time;
    private BigDecimal amount;
    private String address;
    private int status = 0;
    private Long userId = 0L;
}
