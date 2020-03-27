package com.bizzan.bc.wallet.controller;


import com.bizzan.bc.wallet.component.EctApi;
import com.bizzan.bc.wallet.entity.Coin;
import com.bizzan.bc.wallet.util.MessageResult;
import com.spark.blockchain.rpcclient.BitcoinUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/rpc")
public class WalletController {
    private Logger logger = LoggerFactory.getLogger(WalletController.class);
    @Autowired
    private Coin coin;
    @Autowired
    private EctApi ectApi;

    @GetMapping("balance")
    public MessageResult walletBalance() {
        try {
            BigDecimal amt = ectApi.getBalance(coin.getMasterAddress(),coin.getUnit());
            MessageResult result = new MessageResult(0, "success");
            result.setData(amt);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return MessageResult.error(500, "查询失败，error:" + e.getMessage());
        }
    }

    @GetMapping({"transfer","withdraw"})
    public MessageResult withdraw(String address, BigDecimal amount,BigDecimal fee){
        logger.info("withdraw:address={},amount={},fee={}",address,amount,fee);
        if(amount.compareTo(BigDecimal.ZERO) <= 0){
            return MessageResult.error(500,"额度须大于0");
        }
        try {
            String txid = ectApi.sendFrom(coin.getWithdrawWallet(),coin.getWithdrawAddress(),address,amount,"withdraw");
            MessageResult result = new MessageResult(0,"success");
            result.setData(txid);
            return result;
        }
        catch (Exception e){
            e.printStackTrace();
            return MessageResult.error(500,"error:"+e.getMessage());
        }
    }
}
