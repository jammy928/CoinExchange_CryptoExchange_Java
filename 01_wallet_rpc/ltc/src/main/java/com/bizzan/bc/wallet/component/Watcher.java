package com.bizzan.bc.wallet.component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizzan.bc.wallet.entity.Coin;
import com.bizzan.bc.wallet.entity.Deposit;
import com.bizzan.bc.wallet.event.DepositEvent;
import com.bizzan.bc.wallet.service.AccountService;
import com.bizzan.bc.wallet.service.WatcherLogService;
import com.bizzan.bc.wallet.util.HttpClientUtil;

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
import java.util.List;

@Data
@Component
public class Watcher extends Thread{
	
	@Value("${bizzan.blockApi}")
	private String blockApi;
	
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
    
    public void check(){
        try {
        	Thread.sleep(20000); // 避免连续访问btc.com
        	Long networkBlockNumber = getNetwortBlockHeight() - confirmation + 1;
        	Thread.sleep(20000); // 避免连续访问btc.com
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
            for (Long blockHeight = startBlockNumber; blockHeight <= endBlockNumber; blockHeight++) {
            	// 获取区块Hash
            	
            	String blockHashStr = HttpClientUtil.doHttpsGet(blockApi + "block-index/" + blockHeight, null, null);
            	String blockHash = "";
            	if(!StringUtils.isEmpty(blockHashStr)){
            		JSONObject obj = JSON.parseObject(blockHashStr);
            		blockHash = obj.getString("blockHash");
            	}
            	logger.info("获取区块高度(" + blockHeight + ")的Hash值: " + blockHash);
            	// 根据BlockHash获取区块交易列表
            	if(!StringUtils.isEmpty(blockHash)) {
            		String retStr = HttpClientUtil.doHttpsGet(blockApi + "txs/?block=" + blockHeight + "&pageNum=0", null, null);
            		if(!StringUtils.isEmpty(retStr)){
                		JSONObject obj = JSON.parseObject(retStr);
                		int pageSize = obj.getIntValue("pagesTotal");
                		logger.info("该区块有共" + pageSize + "页交易");
            			for(int page = 0; page < pageSize; page++) {
            				JSONArray txList = null;
            				if(page > 0) {
            					String txStr = HttpClientUtil.doHttpsGet(blockApi + "txs/?block=" + blockHeight + "&pageNum="+page, null, null);
            					if(!StringUtils.isEmpty(txStr)){
            						txList = JSON.parseObject(txStr).getJSONArray("txs");
            					}
            				}else {
            					txList = obj.getJSONArray("txs");
            				}
            				// 循环交易列表
            				for(int i = 0; i < txList.size(); i++) {
            					JSONObject tx = txList.getJSONObject(i); // 获取交易本身
            					JSONArray outs = tx.getJSONArray("vout");
            					for(int j = 0; j < outs.size(); j++) {
            						JSONObject out = outs.getJSONObject(j);
            						JSONArray addresses = out.getJSONObject("scriptPubKey").getJSONArray("addresses");
            						BigDecimal amount = out.getBigDecimal("value");
            						
            						if(amount.compareTo(BigDecimal.ZERO) > 0 && addresses != null && addresses.size() > 0) {
            							String address = addresses.getString(0); // 获取地址
	            						if (accountService.isAddressExist(address)) {
		            						logger.info("发现充值地址：" + blockHeight + "-" + tx.getString("txid") + ": 输出地址(" + address + ")");
		                                    Deposit deposit = new Deposit();
		                                    deposit.setTxid(tx.getString("txid"));
		                                    deposit.setBlockHeight(blockHeight);
		                                    deposit.setBlockHash(tx.getString("blockhash"));
		                                    deposit.setAmount(amount);
		                                    deposit.setAddress(address);
		                                    deposit.setTime(new Date(tx.getLongValue("blocktime") * 1000));
		                                    deposits.add(deposit);
		                                }
            						}else {
            							logger.info("交易(" + tx.getString("txid") + ")内部vout地址为空");
            						}
            					}
            				}
            				Thread.sleep(10000);
            			}
                	}
            	}
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
		try {
        	String retStr = HttpClientUtil.doHttpsGet(blockApi + "status?q=getInfo", null, null);
        	if(!StringUtils.isEmpty(retStr)){
        		JSONObject json = JSON.parseObject(retStr);
        		JSONObject info = json.getJSONObject("info");
        		Long height = json.getJSONObject("info").getLong("blocks");
        		return height;
        	}
        	return 0L;
        }
        catch (Exception e){
            e.printStackTrace();
            return 0L;
        }
	}
}
