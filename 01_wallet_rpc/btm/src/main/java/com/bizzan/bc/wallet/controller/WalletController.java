package com.bizzan.bc.wallet.controller;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.wallet.UnreadableWalletException;
import org.bitcoinj.wallet.Wallet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bizzan.bc.wallet.service.AccountService;
import com.bizzan.bc.wallet.util.ClientUtils;
import com.bizzan.bc.wallet.util.HttpClientUtil;
import com.bizzan.bc.wallet.util.MessageResult;

import io.bytom.api.Account;
import io.bytom.api.Balance;
import io.bytom.api.Block;
import io.bytom.api.Receiver;
import io.bytom.exception.BytomException;
import io.bytom.http.Client;

@RestController
@RequestMapping("/rpc")
public class WalletController {

	@Value("${bytom.api.url}")
	private String coreUrl;

	@Value("${client.access.token}")
	private String accessToken;
	
	@Value("${bytom.alias}")
	private String bytomAlias;
	
	@Value("${bytom.password}")
	private String bytomPassword;
	
	@Autowired
    private AccountService accountService;
	
	private Logger logger = LoggerFactory.getLogger(WalletController.class);
	
	@GetMapping("address/{account}")
    public MessageResult getNewAddress(@PathVariable String account){
        logger.info("create new address: "+account);
        try {
			Client client = ClientUtils.generateClient(coreUrl, accessToken);
			Account bytomAccount = null;
			Account.Items accounts = new Account.QueryBuilder().list(client);
			if(accounts.data.size() > 0) {
				bytomAccount = accounts.data.get(0);
			}
			
			// 创建地址
			Receiver receiver = new Account.ReceiverBuilder()
			   .setAccountId(bytomAccount.id)
			   .create(client);
			
			accountService.saveOne(account, receiver.address);
			MessageResult result = new MessageResult(0,"success");
	        result.setData(receiver.address);
	        return result;
		} catch (BytomException e) {
			e.printStackTrace();
			return MessageResult.error(500,"error:" + e.getMessage());
		}
    }
	
	/**
	 * 钱包总余额（非节点服务，直接返回0，后面如果有空闲可以追加获取总体余额）
	 * @return
	 */
	@GetMapping("balance")
    public MessageResult balance(){
		Client client = null;
		try {
			client = ClientUtils.generateClient(coreUrl, accessToken);
			Balance.Items bItems = new Balance.QueryBuilder().list(client);
			BigDecimal total = BigDecimal.ZERO;
			for(int m = 0; m < bItems.data.size(); m++) {
				if(bItems.data.get(m).assetId.equalsIgnoreCase("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff")) {
					total = total.add(BigDecimal.valueOf(bItems.data.get(m).amount));
				}
			}
			MessageResult result = new MessageResult(0,"success");
	        result.setData(total);
	        return result;
		} catch (BytomException e) {
			e.printStackTrace();
			MessageResult result = new MessageResult(0,"success");
	        result.setData(0);
	        return result;
		}
    }
	
	/**
	 * 获取地址余额
	 * @param address
	 * @return
	 */
	@GetMapping("balance/{address}")
    public MessageResult balance(@PathVariable String address){
		return MessageResult.error(500,"Bytom不支持单个地址查询余额");
    }
	
	@GetMapping("height")
    public MessageResult getHeight(){
		Client client;
		try {
			client = ClientUtils.generateClient(coreUrl, accessToken);
			int blockCount = Block.getBlockCount(client);
			MessageResult result = new MessageResult(0,"success");
	        result.setData(blockCount);
	        return result;
		} catch (BytomException e) {
			e.printStackTrace();
			return MessageResult.error(500,"error:" + e.getMessage());
		}
    }
	
	/**
	 * TODO 增加转账功能
	 * @param address
	 * @param amount
	 * @param fee
	 * @return
	 */
	@GetMapping({"transfer","withdraw"})
    public MessageResult withdraw(String address, BigDecimal amount,BigDecimal fee){
		return MessageResult.error(500, "暂未实现该功能");
    }
}
