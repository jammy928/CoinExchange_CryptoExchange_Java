package com.bizzan.bitrade.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.bizzan.bitrade.entity.ExchangeTrade;

import java.util.List;

@Service
public class ExchangeTradeService {
    @Autowired
    private MongoTemplate mongoTemplate;

    public List<ExchangeTrade> findLatest(String symbol,int size){
        Query query = new Query();
        query.with(new Sort(Sort.Direction.DESC,"time"));
        PageRequest page = new PageRequest(0,size);
        query.with(page);
        return mongoTemplate.find(query,ExchangeTrade.class,"exchange_trade_"+symbol);
    }
}
