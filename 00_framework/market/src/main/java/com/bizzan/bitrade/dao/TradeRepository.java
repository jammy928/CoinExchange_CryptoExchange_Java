package com.bizzan.bitrade.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.bizzan.bitrade.entity.ExchangeTrade;

public interface TradeRepository extends MongoRepository<ExchangeTrade,Long>{
}
