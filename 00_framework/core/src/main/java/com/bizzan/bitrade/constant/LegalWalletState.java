package com.bizzan.bitrade.constant;

public enum LegalWalletState {
    APPLYING("申请中"), COMPLETE("完成"), DEFEATED("失败");
    String cnName;

    LegalWalletState(String cnName) {
        this.cnName = cnName;
    }
}
