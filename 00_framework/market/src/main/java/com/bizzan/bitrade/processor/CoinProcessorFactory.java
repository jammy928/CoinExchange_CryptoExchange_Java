package com.bizzan.bitrade.processor;


import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class CoinProcessorFactory {
    private ConcurrentHashMap<String, CoinProcessor> processorMap;

    public CoinProcessorFactory() {
        processorMap = new ConcurrentHashMap<>();
    }

    public void addProcessor(String symbol, CoinProcessor processor) {
        log.info("CoinProcessorFactory addProcessor = {}" + symbol);
        processorMap.put(symbol, processor);
    }

    public boolean containsProcessor(String symbol) {
    	return processorMap != null && processorMap.containsKey(symbol);
    }
    
    public CoinProcessor getProcessor(String symbol) {
        return processorMap.get(symbol);
    }

    public ConcurrentHashMap<String, CoinProcessor> getProcessorMap() {
        return processorMap;
    }
}
