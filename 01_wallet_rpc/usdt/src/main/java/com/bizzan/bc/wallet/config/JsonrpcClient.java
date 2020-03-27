package com.bizzan.bc.wallet.config;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.spark.blockchain.rpcclient.BitcoinException;
import com.spark.blockchain.rpcclient.BitcoinRPCClient;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 基于比特币rpc接口开发新的功能
 * <p>
 * TODO
 * </p>
 * 
 * @author: shangxl
 * @Date : 2017年11月16日 下午6:10:02
 */
public class JsonrpcClient extends BitcoinRPCClient {

	private Logger logger = LoggerFactory.getLogger(JsonrpcClient.class);
	/**
	 * <p>
	 * TODO
	 * </p>
	 * 
	 * @author: shangxl
	 * @param: @param
	 *             rpcUrl
	 * @param: @throws
	 *             MalformedURLException
	 */
	public JsonrpcClient(String rpcUrl) throws MalformedURLException {
		super(rpcUrl);
	}

	@Override
	public String getNewAddress(String accountName) {
		try {
			if (StringUtils.isNotEmpty(accountName)) {
					return super.getNewAddress(accountName);
			} else {
				return super.getNewAddress();
			}
		} catch (BitcoinException e) {
			logger.info("创建新币账户出错：  accountName=" + accountName);
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 获取 btc余额
	 * @param address
	 * @return
	 */
	public BigDecimal getAddressBalance(String address) {
		BigDecimal balance = BigDecimal.ZERO;
		try {
			List<Unspent> unspents = this.listUnspent(3, 99999999, address);
			for(Unspent unspent:unspents){
				balance = balance.add(unspent.amount());
			}
			return balance;
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return new BigDecimal("-1");
	}



	/**
	 * 根据地址获取usdt的账户余额
	 * <p>
	 * TODO
	 * </p>
	 *
	 * @author: shangxl
	 * @param: @param
	 *             address
	 * @param: @return
	 * @return: String
	 * @Date : 2017年12月18日 下午5:39:04
	 * @throws:
	 */
	public BigDecimal omniGetBalance(String address) {
		String balance="0";
		try {
			Integer propertyid = Integer.valueOf(Constant.PROPERTYID_USDT);
			Map<String, Object> map = (Map<String, Object>) query("omni_getbalance", new Object[] { address, propertyid });
			if (map != null) {
				balance= map.get("balance").toString();
			}
		} catch (BitcoinException e) {
			e.printStackTrace();
		}
		return new BigDecimal(balance);
	}

	public Map<String,Object> omniGetTransactions(String txid){
		try {
			return (Map<String, Object>) query("omni_gettransaction", new Object[]{txid});
		} catch (BitcoinException e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<String> omniListBlockTransactions(Long blockHeight){
		try {
			return (List<String>) query("omni_listblocktransactions", new Object[]{blockHeight});
		} catch (BitcoinException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * usdt 转币
	 * <p> TODO</p>
	 * @author:         shangxl
	 * @param:    @return
	 * @return: String 
	 * @Date :          2017年12月19日 下午3:38:31   
	 * @throws:
	 */
	public String omniSend(String fromaddress,String toaddress,BigDecimal amount){
		try {
			return query("omni_send", new Object[]{fromaddress,toaddress,Integer.valueOf(Constant.PROPERTYID_USDT),amount.toPlainString()}).toString();
		} catch (BitcoinException e) {
			logger.info("fromaddress="+fromaddress+" toaddress="+toaddress+" propertyid="+Constant.PROPERTYID_USDT+" amount="+amount);
			e.printStackTrace();
			return null;
		}
	}

	public String omniSend(String fromaddress, String toaddress, BigDecimal amount, BigDecimal bitcoinFee){
		try {
			return query("omni_send", new Object[]{fromaddress,toaddress,Integer.valueOf(Constant.PROPERTYID_USDT),amount.toPlainString(),fromaddress,bitcoinFee.toPlainString()}).toString();
		} catch (BitcoinException e) {
			logger.info("fromaddress="+fromaddress+" toaddress="+toaddress+" propertyid="+Constant.PROPERTYID_USDT+" amount="+amount);
			e.printStackTrace();
			return null;
		}
	}

	public static  void main(String[] args) throws MalformedURLException {
		JsonrpcClient client = new JsonrpcClient("http://bitcoin:bitcoin@127.0.0.1:8888/");
		System.out.println(client.getAddressBalance("3Dxrsd1DzqcvchAa3yNFAvXkNsQUZGdnS8"));
	}
}
