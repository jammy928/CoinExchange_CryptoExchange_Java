package com.bizzan.bitrade.model.screen;

import com.bizzan.bitrade.constant.WithdrawStatus;

import lombok.Data;

@Data
public class LegalWalletWithdrawScreen {
    WithdrawStatus status;
    String username;
    String coinName;

}
