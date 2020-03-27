package com.bizzan.bc.wallet.controller;

import com.bizzan.bc.wallet.service.AccountService;
import com.bizzan.bc.wallet.util.MessageResult;
import com.spark.blockchain.rpcclient.BitcoinException;
import com.spark.blockchain.rpcclient.BitcoinRPCClient;
import com.spark.blockchain.rpcclient.BitcoinUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/rpc")
public class WalletController {
    @Autowired
    private BitcoinRPCClient rpcClient;
    
    private Logger logger = LoggerFactory.getLogger(WalletController.class);
    @Autowired
    private AccountService accountService;
    
    @GetMapping("height")
    public MessageResult getHeight(){
        try {
            int height =rpcClient.getBlockCount();
            MessageResult result = new MessageResult(0,"success");
            result.setData(height - 1);
            return result;
        }
        catch (Exception e){
            e.printStackTrace();
            return MessageResult.error(500,"查询失败,error:"+e.getMessage());
        }
    }

    @GetMapping("address/{account}")
    public MessageResult getNewAddress(@PathVariable String account){
        logger.info("create new address :"+account);
        try {
            String address = rpcClient.getNewAddress(account);
            accountService.saveOne(account,address);
            MessageResult result = new MessageResult(0,"success");
            result.setData(address);
            return result;
        }
        catch (BitcoinException e){
            e.printStackTrace();
            return MessageResult.error(500,"rpc error:"+e.getMessage());
        }
    }

    @GetMapping({"transfer","withdraw"})
    public MessageResult withdraw(String address, BigDecimal amount,BigDecimal fee){
        logger.info("withdraw:address={},amount={},fee={}",address,amount,fee);
        if(amount.compareTo(BigDecimal.ZERO) <= 0){
            return MessageResult.error(500,"额度须大于0");
        }
        try {
            String txid = BitcoinUtil.sendTransaction(rpcClient,address,amount,fee);
            MessageResult result = new MessageResult(0,"success");
            result.setData(txid);
            return result;
        }
        catch (Exception e){
            e.printStackTrace();
            return MessageResult.error(500,"error:"+e.getMessage());
        }
    }

    @GetMapping("balance")
    public MessageResult balance(){
        try {
            BigDecimal balance = new BigDecimal(rpcClient.getBalance());

            MessageResult result = new MessageResult(0,"success");
            result.setData(balance);
            return result;
        }
        catch (Exception e){
            e.printStackTrace();
            return MessageResult.error(500,"error:"+e.getMessage());
        }
    }

    @GetMapping("balance/{address}")
    public MessageResult balance(@PathVariable String address){
        try {
            String account = rpcClient.getAccount(address);
            System.out.println("account="+account+",address="+address);
            BigDecimal balance = new BigDecimal(rpcClient.getBalance(account));
            MessageResult result = new MessageResult(0,"success");
            result.setData(balance);
            return result;
        }
        catch (Exception e){
            e.printStackTrace();
            return MessageResult.error(500,"error:"+e.getMessage());
        }
    }
}
