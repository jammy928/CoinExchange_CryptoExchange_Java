package com.bizzan.bc.wallet.component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizzan.bc.wallet.component.Watcher;
import com.bizzan.bc.wallet.entity.Deposit;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class ActWatcher extends Watcher{
    private Logger logger = LoggerFactory.getLogger(ActWatcher.class);
    @Autowired
    private ActClient actClient;

    @Override
    public  List<Deposit> replayBlock(Long startBlockNumber,Long endBlockNumber) {
        List<Deposit> deposits = new ArrayList<>();
        for(Long index=startBlockNumber;index<=endBlockNumber;index++){
            try {
                JSONObject block = actClient.getBlock(index);
                JSONArray txs = block.getJSONArray("user_transaction_ids");
                txs.forEach(txid->{
                    System.out.println(txid);
                });
            } catch (UnirestException e) {
                e.printStackTrace();
            }
        }
        return deposits;
    }

    @Override
    public Long getNetworkBlockHeight() {
        try {
            return actClient.getBlockCount();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return 0L;
    }
}
