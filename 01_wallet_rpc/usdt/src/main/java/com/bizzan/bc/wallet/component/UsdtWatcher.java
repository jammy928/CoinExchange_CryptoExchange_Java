package com.bizzan.bc.wallet.component;

import com.bizzan.bc.wallet.component.Watcher;
import com.bizzan.bc.wallet.config.JsonrpcClient;
import com.bizzan.bc.wallet.entity.Deposit;
import com.bizzan.bc.wallet.service.AccountService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class UsdtWatcher extends Watcher{
    private Logger logger = LoggerFactory.getLogger(UsdtWatcher.class);
    @Autowired
    private JsonrpcClient jsonrpcClient;
    @Autowired
    private AccountService accountService;
    private String usdtPropertyId = "31";

    public  List<Deposit> replayBlock(Long startBlockNumber, Long endBlockNumber) {
        List<Deposit> deposits = new ArrayList<>();
        try {
            for (Long blockHeight = startBlockNumber; blockHeight <= endBlockNumber; blockHeight++) {
                List<String> list = jsonrpcClient.omniListBlockTransactions(blockHeight);
                for (String txid : list) {
                    Map<String,Object> map = jsonrpcClient.omniGetTransactions(txid);
                    if(map.get("propertyid") == null)continue;
                    String propertyid = map.get("propertyid").toString();
                    String txId = map.get("txid").toString();
                    String address = String.valueOf(map.get("referenceaddress"));
                    Boolean valid =  Boolean.parseBoolean(map.get("valid").toString());
                    if(propertyid.equals(this.usdtPropertyId) && valid) {
                        if (accountService.isAddressExist(address)) {
                            Deposit deposit = new Deposit();
                            deposit.setTxid(txId);
                            deposit.setBlockHash(String.valueOf(map.get("blockhash")));
                            deposit.setAmount(new BigDecimal(map.get("amount").toString()));
                            deposit.setAddress(address);
                            logger.info("receive usdt {}", String.valueOf(map.get("referenceaddress")));
                            deposit.setBlockHeight(Long.valueOf(String.valueOf(map.get("block"))));
                            deposits.add(deposit);
                        }
                    }
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return deposits;
    }

    @Override
    public Long getNetworkBlockHeight() {
        try {
            return Long.valueOf(jsonrpcClient.getBlockCount());
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return 0L;
    }

}
