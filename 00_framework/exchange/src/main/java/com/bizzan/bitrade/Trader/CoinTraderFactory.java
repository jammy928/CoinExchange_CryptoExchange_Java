package com.bizzan.bitrade.Trader;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class CoinTraderFactory {

	private ConcurrentHashMap<String, CoinTrader> traderMap;

	public CoinTraderFactory() {
		traderMap = new ConcurrentHashMap<>();
	}

	//添加，已存在的无法添加
	public void addTrader(String symbol, CoinTrader trader) {
		if(!traderMap.containsKey(symbol)) {
			traderMap.put(symbol, trader);
		}
	}

	//重置，即使已经存在也会覆盖
	public void resetTrader(String symbol, CoinTrader trader) {
		traderMap.put(symbol, trader);
	}
	
	public boolean containsTrader(String symbol) {
		return traderMap.containsKey(symbol);
	}
	
	public CoinTrader getTrader(String symbol) {
		return traderMap.get(symbol);
	}

	public ConcurrentHashMap<String, CoinTrader> getTraderMap() {
		return traderMap;
	}

}
