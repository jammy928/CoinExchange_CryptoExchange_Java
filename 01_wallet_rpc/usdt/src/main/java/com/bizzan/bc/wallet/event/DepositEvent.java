package com.bizzan.bc.wallet.event;

import com.alibaba.fastjson.JSON;
import com.bizzan.bc.wallet.entity.Deposit;
import com.bizzan.bc.wallet.event.DepositEvent;
import com.bizzan.bc.wallet.service.DepositService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class DepositEvent {
    private Logger logger = LoggerFactory.getLogger(DepositEvent.class);
    @Autowired
    private DepositService depositService;
    @Autowired
    private KafkaTemplate<String,String> kafkaTemplate;
    @Value("${coin.name}")
    private String coinName;

    public synchronized void onConfirmed(Deposit deposit){
        logger.info("confirmed deposit {}",JSON.toJSON(deposit));
        if(!depositService.exists(deposit)) {
            depositService.save(deposit);
            kafkaTemplate.send("deposit",coinName, JSON.toJSONString(deposit));
        }
    }
}
