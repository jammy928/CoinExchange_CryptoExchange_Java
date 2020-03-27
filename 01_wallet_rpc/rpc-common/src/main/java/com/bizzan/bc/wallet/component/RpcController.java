package com.bizzan.bc.wallet.component;

import java.math.BigDecimal;

import com.bizzan.bc.wallet.util.MessageResult;

public interface RpcController {
    /**
     * 获取当前区块高度
     * @return
     */
    MessageResult blockHeight();

    /**
     * 为用户获取新地址
     * @param uuid
     * @return
     */
    MessageResult getNewAddress(String uuid);

    /**
     * 提现
     * @param toAddress
     * @param amount
     * @param fee
     * @param isSync
     * @param withdrawId
     * @return
     */
    MessageResult withdraw(String toAddress, BigDecimal amount, BigDecimal fee,Boolean isSync,String withdrawId);

    /**
     * 转账
     * @return
     */
    MessageResult transfer();

    /**
     * 余额
     * @return
     */
    MessageResult balance();
}
