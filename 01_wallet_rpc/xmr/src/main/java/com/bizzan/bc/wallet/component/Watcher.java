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
        	Thread.sleep(5000); // 避免连续访问btc.com
        	Long networkBlockNumber = getNetwortBlockHeight() - confirmation + 1;
        	Thread.sleep(5000); // 避免连续访问btc.com
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
        	int[] addressIndex = {0}; 
        	// 获取高度
        	JSONObject param = new JSONObject();
        	param.put("jsonrpc", "2.0");
        	param.put("id", "0");
        	param.put("method", "get_transfers");
        	
        	JSONObject param2 = new JSONObject();
        	param2.put("in", true);
        	param2.put("account_index", 0);
        	param2.put("filter_by_height", true);
        	param2.put("min_height", startBlockNumber); // 开始高度
        	param2.put("max_height", endBlockNumber); // 结束高度
        	
        	Map<String, String> headerParam = new HashMap<String, String>();
        	headerParam.put("Content-Type", "application/json");
        	
        	logger.info("获取区块区间交易: 开始区块：{}， 结束区块：{}", startBlockNumber, endBlockNumber); 
			String result = HttpClientUtil.doHttpPost(this.blockApi, param.toJSONString(), headerParam);
			if(result != null) {
				JSONObject obj = JSONObject.parseObject(result);
				JSONArray incommings = obj.getJSONObject("result").getJSONArray("in");
				if(incommings != null) {
					logger.info("发现交易，共 {} 条", incommings.size()); 
					for(int i = 0; i < incommings.size(); i++) {
						JSONObject incommObj = incommings.getJSONObject(i);
						
						BigDecimal amount = incommObj.getBigDecimal("amount").divide(BigDecimal.valueOf(1000000000000L));
						String txId = incommObj.getString("txid");
						Long blockHeight = incommObj.getLong("height");
						String paymentId = incommObj.getString("payment_id");
						
						// 解析UID
						StringBuilder uidS = new StringBuilder();
						for(int j = 0; j < 8; j++) {
							uidS.append(paymentId.charAt(j*8 + 1));
	            		}
						Long uid = Long.parseLong(uidS.toString(), 16) + 345678;
						
						logger.info("发现用户充值记录: 数量：{}，用户：{}，区块：{}，paymentID：{}", amount, uid, blockHeight, paymentId); 
						Deposit deposit = new Deposit();
						deposit.setUserId(uid); //此处无需对memo处理，wallet.jar会处理
				        deposit.setTxid(txId);
				        deposit.setBlockHeight(blockHeight);
				        deposit.setBlockHash("");
				        deposit.setAmount(amount);
				        deposit.setAddress(coin.getDepositAddress());
				        deposit.setTime(new Date());
				        deposits.add(deposit);
					}
				}else {
					logger.info("没有发现交易！");
				}
			}else {
				logger.info("获取区块区间交易失败，返回空！！！（ 开始区块：{}， 结束区块：{}）", startBlockNumber, endBlockNumber); 
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
                    Thread.sleep(Math.max(nextCheck - System.currentTimeMillis(), 60000));
                } catch (InterruptedException ex) {
                    logger.info(ex.getMessage());
                }
            }
        }
    }
	
    public Long getNetwortBlockHeight() {
    	int[] addressIndex = {0}; 
    	// 获取高度
    	JSONObject param = new JSONObject();
    	param.put("jsonrpc", "2.0");
    	param.put("id", "0");
    	param.put("method", "get_height");
    	
    	Map<String, String> headerParam = new HashMap<String, String>();
    	headerParam.put("Content-Type", "application/json");
    	
        try {
			String result = HttpClientUtil.doHttpPost(this.blockApi, param.toJSONString(), headerParam);
			if(!StringUtils.isEmpty(result)) {
				JSONObject obj = JSONObject.parseObject(result);
				logger.info("获取区块高度：{}", obj.getJSONObject("result").getLong("height"));
				return obj.getJSONObject("result").getLong("height");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
        return 0L;
	}
}
