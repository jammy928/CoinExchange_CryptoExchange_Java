package com.bizzan.bc.wallet.component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizzan.bc.wallet.component.Watcher;
import com.bizzan.bc.wallet.entity.Deposit;
import com.bizzan.bc.wallet.util.Convert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Component
public class EctWatcher extends Watcher{
    private Logger logger = LoggerFactory.getLogger(EctWatcher.class);
    @Autowired
    private EctApi ectApi;

    @Override
    public List<Deposit> replayBlock(Long startBlockNumber, Long endBlockNumber) {
        JSONArray transactions = ectApi.getTrasactions(getCoin().getMasterAddress(),startBlockNumber,endBlockNumber,getCoin().getUnit());
        logger.info("transactions ={}",transactions);
        List<Deposit> deposits = new ArrayList<>();
        for(int index=0,size=transactions.size();index<size;index++){
            try {
                JSONObject transaction = transactions.getJSONObject(index);
                String txid = transaction.getString("hash");
                Long height = Long.parseLong(transaction.getString("ledger"));
                if (transaction.containsKey("memos")) {
                    JSONObject memo = transaction.getJSONArray("memos").getJSONObject(0);
                    logger.info("memo:{}", memo);
                    BigDecimal value = new BigDecimal(transaction.getJSONObject("amount").getString("value"));
                    Deposit deposit = new Deposit();
                    deposit.setTxid(txid);
                    deposit.setBlockHeight(height);
                    deposit.setBlockHash("");
                    deposit.setAmount(value);
                    String memoData = Convert.fromAsciiToString(memo.getString("memo_data"));
                    deposit.setAddress(getCoin().getMasterAddress() + ":" + memoData);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(transaction.getLong("date") * 1000);
                    deposit.setTime(calendar.getTime());
                    deposits.add(deposit);
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        return deposits;
    }

    @Override
    public Long getNetworkBlockHeight() {
        return ectApi.getLatestHeight();
    }
}
