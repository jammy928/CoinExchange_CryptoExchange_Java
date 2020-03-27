package com.bizzan.bitrade.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.bizzan.bitrade.entity.ExchangeOrderDetail;
import com.bizzan.bitrade.entity.ExchangeTrade;

public interface ExchangeTradeRepository extends MongoRepository<ExchangeTrade,String> {
}
