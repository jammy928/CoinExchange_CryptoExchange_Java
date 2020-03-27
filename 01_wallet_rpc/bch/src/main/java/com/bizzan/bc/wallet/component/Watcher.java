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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            	// 获取区块信息（交易数量）
            	int txCount = 0;
            	String retBlockStr = HttpClientUtil.doHttpsGet(blockApi + "block/" + blockHeight, null, null);
            	if(!StringUtils.isEmpty(retBlockStr)){
            		JSONObject obj = JSON.parseObject(retBlockStr);
            		if(obj.getIntValue("err_no") == 0) {
            			txCount = obj.getJSONObject("data").getIntValue("tx_count");
            		}
            	}
            	// 根据交易数量分页查询
            	int pageCount = txCount / 50;
            	pageCount = (txCount % 50 > 0) ? pageCount + 1 : pageCount; // 总数对50取余，如果不是整除则+1页
            	for(int pageNo = 1; pageNo < pageCount + 1; pageNo++) {
            		Thread.sleep(10000);//防止调用(bch.btc.com)频繁报错
            		logger.info("获取区块(" + blockHeight + ")交易列表，总交易数：" + txCount + ", 正在获取第 [" + pageNo + "] 页");
	            	Map<String,String> param = new HashMap<String, String>();
	            	param.put("pagesize", String.valueOf(50));
	            	param.put("page", String.valueOf(pageNo));
	            	String retStr = HttpClientUtil.doHttpsGet(blockApi + "block/" + blockHeight + "/tx", param, null);
	            	if(!StringUtils.isEmpty(retStr)){
	            		JSONObject obj = JSON.parseObject(retStr);
	            		if(obj.getIntValue("err_no") == 0) {
	            			JSONArray txList = obj.getJSONObject("data").getJSONArray("list");
	            			for(int i = 0; i < txList.size(); i++) {
	            				JSONObject tx = txList.getJSONObject(i);
	            				JSONArray outs = tx.getJSONArray("outputs");
	            				for(int j = 0; j < outs.size(); j++) {
	            					JSONArray addresses = outs.getJSONObject(j).getJSONArray("addresses");
	            					if(addresses.size() > 0) {
	            						String address = addresses.getString(0); // 获取outputs地址
		            					BigDecimal amount = outs.getJSONObject(j).getBigDecimal("value");
		            					if(amount.compareTo(BigDecimal.ZERO) > 0) {
		            						amount = amount.divide(BigDecimal.valueOf(100000000)); // 注意是100000000
		            					}
		            					if (accountService.isAddressExist(address)) {
		            						logger.info("发现充值地址：" + blockHeight + "-" + tx.getString("hash") + ": 输出地址(" + address + ")");
		                                    Deposit deposit = new Deposit();
		                                    deposit.setTxid(tx.getString("hash"));
		                                    deposit.setBlockHeight(blockHeight);
		                                    deposit.setBlockHash(tx.getString("block_hash"));
		                                    deposit.setAmount(amount);
		                                    deposit.setAddress(address);
		                                    deposit.setTime(new Date(tx.getLongValue("create_at") * 1000));
		                                    deposits.add(deposit);
		                                }
	            					}
	            				}
	            			}
	            		}
	            	}else {
	            		logger.info("获取区块(" + blockHeight + ")交易列表失败");
	            		break;
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
		try {
        	String retStr = HttpClientUtil.doHttpsGet(blockApi + "block/latest", null, null);
        	if(!StringUtils.isEmpty(retStr)){
        		JSONObject json = JSON.parseObject(retStr);
        		if(json.getIntValue("err_no") == 0) {
        			Long height = json.getJSONObject("data").getLong("height");
                    return height;
        		}
        	}
        	return 0L;
        }
        catch (Exception e){
            e.printStackTrace();
            return 0L;
        }
	}
}
