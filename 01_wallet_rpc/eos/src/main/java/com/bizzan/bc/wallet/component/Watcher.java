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

import org.apache.http.client.ClientProtocolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
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
	
	@Value("${bizzan.apikey}")
	private String apikey;
	
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
        	Long networkBlockNumber = getNetwortBlockHeight() - confirmation + 1;
        	Thread.sleep(10000); // 避免连续访问
        	if(currentBlockHeight < networkBlockNumber) {
        		long startBlockNumber = currentBlockHeight + 1;
        		currentBlockHeight = (networkBlockNumber - currentBlockHeight > step) ? currentBlockHeight + step : networkBlockNumber;
        		
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
    	
    	JSONObject param = new JSONObject();
    	param.put("account_name", coin.getDepositAddress());
    	param.put("code", "eosio.token");
    	param.put("symbol", "EOS");
    	param.put("to", coin.getDepositAddress());
    	param.put("start_block_num", startBlockNumber);
    	param.put("end_block_num", endBlockNumber);
    	param.put("size", 100);
    	logger.info("replay block from {} to {}", startBlockNumber, endBlockNumber);
    	try {
    		logger.info("Transactions params: {}", param.toJSONString());
			String retStr = this.eosHttpsPost(this.blockApi + "v2/third/get_account_transfer", param.toJSONString());
			logger.info("Transactions result: {}", retStr);
			
			if(!StringUtils.isEmpty(retStr)){
				JSONObject obj = JSON.parseObject(retStr);
				int total = obj.getIntValue("total");
				
				logger.info("解析区块{} - {}的交易信息：{}条记录", startBlockNumber, endBlockNumber, total);
				
				if(total > 0) {
					JSONArray list = obj.getJSONArray("list");
					if(list.size() > 0) {
						for(int i = 0; i < list.size(); i++) {
							JSONObject transObj = list.getJSONObject(i);
							logger.info("交易信息：{}", transObj.toJSONString()); 
							if(transObj.getString("status").equals("executed")) {
								// 区块号
								Long blockNum = transObj.getLong("block_num");
								JSONObject transData = transObj.getJSONObject("data");
								String quantity = transData.getString("quantity");
								// 转账数量
								BigDecimal amount = new BigDecimal(quantity.split(" ")[0]);
								// Memo
								String memo = transData.getString("memo");
								// Memo为数字
								Boolean isNumric = memo.matches("^\\d+$");
								if(isNumric) {
									if(amount.compareTo(BigDecimal.ZERO) > 0) {
										logger.info("发现用户充值记录: 数量：{}， Memo：{}", amount, memo); 
										Deposit deposit = new Deposit();
										deposit.setUserId(Long.parseLong(memo)); //此处无需对memo处理，wallet.jar会处理
								        deposit.setTxid(transObj.getString("id"));
								        deposit.setBlockHeight(blockNum);
								        deposit.setBlockHash(transObj.getString("id"));
								        deposit.setAmount(amount);
								        deposit.setAddress(coin.getDepositAddress());
								        deposit.setTime(new Date());
								        deposits.add(deposit);
									}
								}
							}
						}
					}
				}
			}
		}catch (Exception e1) {
			e1.printStackTrace();
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
                    Thread.sleep(Math.max(nextCheck - System.currentTimeMillis(), 20000));
                } catch (InterruptedException ex) {
                    logger.info(ex.getMessage());
                }
            }
        }
    }
	
    public Long getNetwortBlockHeight() {
		try {
			String retStr = this.eosHttpsPost(this.blockApi + "v1/chain/get_info");
			if(!StringUtils.isEmpty(retStr)){
        		JSONObject json = JSON.parseObject(retStr);
        		Long height = json.getLong("head_block_num");
        		return height;
        	}
        	return 0L;
        }
        catch (Exception e){
            e.printStackTrace();
            return 0L;
        }
	}
    
    private String eosHttpsPost(String url, String json) throws KeyManagementException, ClientProtocolException, NoSuchAlgorithmException, KeyStoreException, IOException {
    	Map<String,String> headerParam = new HashMap<String, String>();
    	headerParam.put("apikey", this.apikey);
    	return HttpClientUtil.doHttpsPost(url, json, headerParam);
    }
    
    private String eosHttpsPost(String url) throws KeyManagementException, ClientProtocolException, NoSuchAlgorithmException, KeyStoreException, IOException {
    	Map<String,String> headerParam = new HashMap<String, String>();
    	Map<String,String> postParam = new HashMap<String, String>();
    	headerParam.put("apikey", this.apikey);
    	return HttpClientUtil.doHttpsPost(url, postParam, headerParam);
    }
}
