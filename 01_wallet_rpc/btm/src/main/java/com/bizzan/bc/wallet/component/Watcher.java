package com.bizzan.bc.wallet.component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizzan.bc.wallet.entity.Coin;
import com.bizzan.bc.wallet.entity.Deposit;
import com.bizzan.bc.wallet.event.DepositEvent;
import com.bizzan.bc.wallet.service.AccountService;
import com.bizzan.bc.wallet.service.WatcherLogService;
import com.bizzan.bc.wallet.util.ClientUtils;
import com.bizzan.bc.wallet.util.HttpClientUtil;
import com.bizzan.bc.wallet.util.MessageResult;

import io.bytom.api.Block;
import io.bytom.exception.BytomException;
import io.bytom.http.Client;
import lombok.Data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Component
public class Watcher extends Thread{
	
    private Logger logger = LoggerFactory.getLogger(Watcher.class);
    private boolean stop = false;
    //默认同步间隔20秒
    private DepositEvent depositEvent;
    private Coin coin;
    private WatcherLogService watcherLogService;
    private int confirmation = 3;
    private Long currentBlockHeight = 0L;
    private int step = 5;
    private Long checkInterval = 2000L;
    
    @Autowired
    private AccountService accountService;
    

	@Value("${bytom.api.url}")
	private String coreUrl;

	@Value("${client.access.token}")
	private String accessToken;
    
    public void check(){
        try {
        	Thread.sleep(20000);
        	Long networkBlockNumber = getNetwortBlockHeight() - confirmation + 1;
        	if(currentBlockHeight < networkBlockNumber) {
        		long startBlockNumber = currentBlockHeight + 1;
        		currentBlockHeight = (networkBlockNumber - currentBlockHeight > step) ? currentBlockHeight + step : networkBlockNumber;
        		logger.info("replay block from {} to {}", startBlockNumber, networkBlockNumber);
        		List<Deposit> deposits = this.replayBlock(startBlockNumber, currentBlockHeight);
        		if(deposits != null) {
	        		deposits.forEach(deposit -> {
	                    depositEvent.onConfirmed(deposit);
	                });
	                //记录日志
	        		watcherLogService.update(coin.getName(), currentBlockHeight);
        		}else {
        			// 未扫描成功
        			currentBlockHeight = startBlockNumber - 1;
        		}
        	}else {
        		logger.info("already latest height {},nothing to do!", currentBlockHeight);
        	}
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    
    private List<Deposit> replayBlock(Long startBlockNumber, Long endBlockNumber) {
    	List<Deposit> deposits = new ArrayList<Deposit>();
        try {
        	Client client = ClientUtils.generateClient(coreUrl, accessToken);
            for (Long blockHeight = startBlockNumber; blockHeight <= endBlockNumber; blockHeight++) {
            	// 获取区块信息（交易数量）
            	Block block = new Block.QueryBuilder()
                        .setBlockHeight(blockHeight.intValue())
        	       .getBlock(client);
            	
            	for(int k = 0; k < block.transactions.size(); k++) {
        			for(int l = 0; l < block.transactions.get(k).outputs.size(); l++) {
        				
        				BigDecimal amount = BigDecimal.valueOf(block.transactions.get(k).outputs.get(l).amount);
    					if(amount.compareTo(BigDecimal.ZERO) > 0) {
    						amount = amount.divide(BigDecimal.valueOf(100000000)); // 注意是100000000
    					}
    					if(block.transactions.get(k).outputs.get(l).assetId.equalsIgnoreCase("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff")
    							&& accountService.isAddressExist(block.transactions.get(k).outputs.get(l).address)
    							&& amount.compareTo(BigDecimal.ZERO) > 0
    							&& block.transactions.get(k).outputs.get(l).type.equalsIgnoreCase("control")) {
    						logger.info("发现充值地址：" + blockHeight + "-" + block.transactions.get(k).id + ": 输出地址(" + block.transactions.get(k).outputs.get(l).address + ")");
                            Deposit deposit = new Deposit();
                            deposit.setTxid(block.transactions.get(k).id);
                            deposit.setBlockHeight(blockHeight);
                            deposit.setBlockHash(block.hash);
                            deposit.setAmount(amount);
                            deposit.setAddress(block.transactions.get(k).outputs.get(l).address);
                            deposit.setTime(new Date(block.timestamp * 1000));
                            deposits.add(deposit);
    					}
        			}
        		}
            	Thread.sleep(10000); // 块与块之间也暂停10秒
            }
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return deposits;
    }
    
	@Override
    public void run() {
        stop = false;
        long nextCheck = 0;
        while(!(Thread.interrupted() || stop)) {
            if (nextCheck <= System.currentTimeMillis()) {
                try {
                    nextCheck = System.currentTimeMillis() + checkInterval;
                    logger.info("check transactions...");
                    check();
                } catch (Exception ex) {
                    logger.info(ex.getMessage());
                }
            }
            else {
                try {
                    Thread.sleep(Math.max(nextCheck - System.currentTimeMillis(), 30000));
                } catch (InterruptedException ex) {
                    logger.info(ex.getMessage());
                }
            }
        }
    }
	
    public Long getNetwortBlockHeight() {
    	Client client;
		try {
			client = ClientUtils.generateClient(coreUrl, accessToken);
			int blockCount = Block.getBlockCount(client);
	        return (long) blockCount;
		} catch (BytomException e) {
			e.printStackTrace();
			return 0L;
		}
	}
}
