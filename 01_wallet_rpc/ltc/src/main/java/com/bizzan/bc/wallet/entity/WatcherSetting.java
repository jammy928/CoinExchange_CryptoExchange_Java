package com.bizzan.bc.wallet.entity;

import lombok.Data;

@Data
public class WatcherSetting {
	private String initBlockHeight = "latest";
    private Long interval = 20000L; // 间隔时间
    private int step = 5;
    private int confirmation = 3;
}
