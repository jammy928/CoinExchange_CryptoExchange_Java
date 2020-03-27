package com.bizzan.bc.wallet.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.bizzan.bc.wallet.entity.Account;
import com.bizzan.bc.wallet.entity.Coin;
import com.bizzan.bc.wallet.entity.Deposit;

import java.util.List;

@Service
public class DepositService {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private Coin coin;

    public void save(Deposit tx){
        mongoTemplate.insert(tx,getCollectionName());
    }

    public String getCollectionName(){
        return coin.getUnit() + "_deposit";
    }

    public boolean exists(Deposit deposit){
        Criteria criteria =  Criteria.where("address").is(deposit.getAddress())
                .andOperator(Criteria.where("txid").is(deposit.getTxid()));
        Query query = new Query(criteria);
        return mongoTemplate.exists(query,getCollectionName());
    }


    public Deposit findLatest(){
        Sort.Order order = new Sort.Order(Sort.Direction.DESC,"blockHeight");
        Sort sort = new Sort(order);
        PageRequest page = new PageRequest(0, 1, sort);
        Query query = new Query();
        query.with(page);
        Deposit result = mongoTemplate.findOne(query,Deposit.class,getCollectionName());
        return result;
    }
}
