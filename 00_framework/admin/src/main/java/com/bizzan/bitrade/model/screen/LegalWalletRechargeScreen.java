package com.bizzan.bitrade.model.screen;

import com.bizzan.bitrade.constant.LegalWalletState;

import lombok.Data;

@Data
public class LegalWalletRechargeScreen {
    LegalWalletState status;
    String username;
    String coinName;

}
