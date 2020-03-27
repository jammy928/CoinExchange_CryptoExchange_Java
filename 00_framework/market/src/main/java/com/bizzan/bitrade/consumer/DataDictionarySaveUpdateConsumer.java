package com.bizzan.bitrade.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.bizzan.bitrade.constant.SysConstant;

import java.util.List;

@Slf4j
@Component
public class DataDictionarySaveUpdateConsumer {
    @Autowired
    private RedisTemplate redisTemplate;

    @KafkaListener(topics = "data-dictionary-save-update", group = "group-handle")
    public void handelDataDictionarySaveUpdate(List<ConsumerRecord<String,String>> records){
        for (int i = 0; i < records.size(); i++) {
            ConsumerRecord<String, String> record = records.get(i);
            log.info("topic={},key={},value={},size={}", record.topic(), record.key(), record.value(),records.size());
            String bond = record.key();
            String value = record.value();
            log.info(">>>>字典表数据已修改>>>修改缓存中的数据>>>>>bond>"+bond+">>>>>value>>"+value);
            String key = SysConstant.DATA_DICTIONARY_BOUND_KEY+bond;
            ValueOperations valueOperations = redisTemplate.opsForValue();
            Object bondvalue =valueOperations.get(key );
            if(bondvalue==null){
                log.info(">>>>>>缓存中无数据>>>>>");
                valueOperations.set(key,value);
            }else{
                log.info(">>>>缓存中有数据>>>");
                valueOperations.getOperations().delete(key);
                valueOperations.set(key,value);
            }
        }
    }
}
