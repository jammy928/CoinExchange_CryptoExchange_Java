package com.bizzan.bitrade.model.screen;

import com.bizzan.bitrade.constant.CommonStatus;
import com.bizzan.bitrade.constant.SysAdvertiseLocation;

import lombok.Data;

@Data
public class SysAdvertiseScreen {
    private String serialNumber;
    private SysAdvertiseLocation sysAdvertiseLocation;
    private CommonStatus status;
}
