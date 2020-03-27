package com.bizzan.bitrade.controller.screen;

import com.bizzan.bitrade.constant.LegalWalletState;

import lombok.Data;

/**
 * @author GS
 * @Title: ${file_name}
 * @Description:
 * @date 2018/4/217:44
 */
@Data
public class LegalWalletScreen {
    private LegalWalletState state;
    private String coinName;
}
