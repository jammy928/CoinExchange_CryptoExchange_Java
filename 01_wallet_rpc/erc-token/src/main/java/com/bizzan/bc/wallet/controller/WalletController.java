package com.bizzan.bc.wallet.controller;


import com.bizzan.bc.wallet.component.TokenWatcher;
import com.bizzan.bc.wallet.entity.Account;
import com.bizzan.bc.wallet.entity.Coin;
import com.bizzan.bc.wallet.entity.Contract;
import com.bizzan.bc.wallet.service.AccountService;
import com.bizzan.bc.wallet.service.EthService;
import com.bizzan.bc.wallet.util.MessageResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.web3j.protocol.core.methods.response.EthBlockNumber;

import java.math.BigDecimal;
import java.util.List;

@RequestMapping("/rpc")
@RestController
public class WalletController {
    @Autowired
    private EthService service;
    @Autowired
    private AccountService accountService;
    @Autowired
    private Coin coin;
    @Autowired
    private Contract contract;
    @Autowired
    private TokenWatcher watcher;
    private Logger logger = LoggerFactory.getLogger(WalletController.class);

    @GetMapping("balance")
    public MessageResult walletBalance() {
        try {
            BigDecimal amt = accountService.findBalanceSum();
            MessageResult result = new MessageResult(0, "success");
            result.setData(amt);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return MessageResult.error(500, "查询失败，error:" + e.getMessage());
        }
    }

    @GetMapping("height")
    public MessageResult getHeight() {
        try {
            Long blockNumber = watcher.getCurrentBlockHeight();
            long rpcBlockNumber = blockNumber.longValue();
            MessageResult result = new MessageResult(0, "success");
            result.setData(rpcBlockNumber);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return MessageResult.error(500, "查询失败,error:" + e.getMessage());
        }
    }
    
    @GetMapping("balance/{address}")
    public MessageResult addressBalance(@PathVariable String address) {
        try {
            BigDecimal amt = service.getTokenBalance(address);
            MessageResult result = new MessageResult(0, "success");
            result.setData(amt);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return MessageResult.error(500, "查询失败，error:" + e.getMessage());
        }
    }

    @GetMapping("address/{account}")
    public MessageResult getNewAddress(@PathVariable String account, @RequestParam(required = false, defaultValue = "") String password) {
        logger.info("create new account={},password={}", account, password);
        try {
            String address;
            Account acct = accountService.findByName("ETH",account);
            if(acct != null){
                address = acct.getAddress();
                accountService.save(acct);
            }
            else {
                address = service.createNewWallet(account, password);
            }
            MessageResult result = new MessageResult(0, "success");
            result.setData(address);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return MessageResult.error(500, "rpc error:" + e.getMessage());
        }
    }

    @GetMapping("transfer-from-address")
    public MessageResult transferFromAddress(String fromAddress,String address, BigDecimal amount, BigDecimal fee) {
        logger.info("transferFromAddress:from={},to={},amount={},fee={}",fromAddress,address, amount, fee);
        try {
            if (fee == null || fee.compareTo(BigDecimal.ZERO) <= 0) {
                fee = service.getMinerFee(contract.getGasLimit());
            }
            if(service.getBalance(fromAddress).compareTo(fee) < 0){
                logger.info("地址{}手续费不足，最低为{}ETH",fromAddress,fee);
                return MessageResult.error(500,"矿工费不足");
            }
            MessageResult result = service.transferToken(fromAddress,address, amount, true);
            logger.info("返回结果 : " + result.toString());
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return MessageResult.error(500, "error:" + e.getMessage());
        }
    }

    @GetMapping("transfer")
    public MessageResult transfer(String address, BigDecimal amount, BigDecimal fee) {
        logger.info("transfer:address={},amount={},fee={}", address, amount, fee);
        BigDecimal transferredAmount = BigDecimal.ZERO;
        try {
            if (fee == null || fee.compareTo(BigDecimal.ZERO) <= 0) {
                fee = service.getMinerFee(contract.getGasLimit());
            }
            List<Account> accountList = accountService.findByBalanceAndGas(coin.getMinCollectAmount(),fee);
            for(Account account:accountList) {
                if(service.getBalance(account.getAddress()).compareTo(fee) < 0){
                    logger.info("地址{}手续费不足，最低为{}",account.getAddress(),fee);
                    continue;
                }
                BigDecimal availAmt = service.getTokenBalance(account.getAddress());
                if(availAmt.compareTo(coin.getMinCollectAmount()) < 0){
                    logger.info("地址{}余额不足，最低为{}",account.getAddress(),coin.getMinCollectAmount());
                    continue;
                }
                logger.info("from={},amount={},fee={}",account.getAddress(),availAmt,fee);
                MessageResult result = service.transferToken(account.getAddress(), address, availAmt, true);
                if(result.getCode() == 0) {
                    transferredAmount = transferredAmount.add(availAmt);
                }
                if(transferredAmount.compareTo(amount) >= 0) break;
            }
            logger.info("累计转出:{}",transferredAmount);
            MessageResult mr = new MessageResult(0,"转账成功");
            mr.setData(transferredAmount);
            return mr;
        } catch (Exception e) {
            e.printStackTrace();
            return MessageResult.error(500, "error:" + e.getMessage());
        }
    }

    @GetMapping("withdraw")
    public MessageResult withdraw(String address, BigDecimal amount,
                                  @RequestParam(name = "sync",required = false,defaultValue = "true") Boolean sync,
                                  @RequestParam(name = "withdrawId",required = false,defaultValue = "") String withdrawId) {
        logger.info("withdraw:to={},amount={},sync={},withdrawId={}", address, amount, sync,withdrawId);
        try {
            MessageResult result = service.transferTokenFromWithdrawWallet(address, amount, sync,withdrawId);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return MessageResult.error(500, "error:" + e.getMessage());
        }
    }

    @GetMapping("sync-block")
    public MessageResult manualSync(Long startBlock, Long endBlock) {
        try {
            watcher.replayBlockInit(startBlock,endBlock);
            return MessageResult.success();
        } catch (Exception e) {
            e.printStackTrace();
            return MessageResult.error(500, "同步失败：" + e.getMessage());
        }
    }

    @GetMapping("sync-height")
    public MessageResult getCurrentSyncHeight(){
        MessageResult result = MessageResult.success();
        result.setData(watcher.getCurrentBlockHeight());
        return result;
    }
}
