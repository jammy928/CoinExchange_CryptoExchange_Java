package com.bizzan.bc.wallet.entity;

import lombok.Data;

@Data
public class WatcherSetting {
    private String initBlockHeight = "latest";
    private Long interval = 5000L;
    private int step = 5;
    private int confirmation = 3;
}
