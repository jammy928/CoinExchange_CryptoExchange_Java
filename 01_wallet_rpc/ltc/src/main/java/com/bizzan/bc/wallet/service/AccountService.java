package com.bizzan.bc.wallet.service;

import com.bizzan.bc.wallet.entity.Account;
import com.bizzan.bc.wallet.entity.BalanceSum;
import com.bizzan.bc.wallet.entity.Coin;
import com.mongodb.BasicDBObject;
import com.mongodb.WriteResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@Service
public class AccountService {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private Coin coin;

    /**
     * 获取集合名称
     * @return
     */
    public String getCollectionName(){
        return coin.getUnit() + "_address_book";
    }

    public String getCollectionName(String coinUnit){
        return coinUnit + "_address_book";
    }

    public void save(Account account){
        mongoTemplate.insert(account,getCollectionName());
    }

    /**
     * 根据账户名查找
     * @param coinUnit
     * @param username
     * @return
     */
    public Account findByName(String coinUnit,String username){
        Query query = new Query();
        Criteria criteria = Criteria.where("account").is(username);
        query.addCriteria(criteria);
        return mongoTemplate.findOne(query,Account.class,getCollectionName(coinUnit));
    }

    public Account findByName(String username){
        return findByName(coin.getUnit(),username);
    }

    /**
     * 根据地址查找
     * @param address
     * @return
     */
    public Account findByAddress(String address){
        Query query = new Query();
        Criteria criteria = Criteria.where("address").is(address);
        query.addCriteria(criteria);
        return mongoTemplate.findOne(query,Account.class,getCollectionName());
    }

    public void removeByName(String name){
        Query query = new Query();
        Criteria criteria = Criteria.where("account").is(name);
        query.addCriteria(criteria);
        mongoTemplate.remove(query,getCollectionName());
    }

    public boolean isAddressExist(String address){
        Query query = new Query();
        Criteria criteria = Criteria.where("address").is(address);
        query.addCriteria(criteria);
        return  mongoTemplate.exists(query,getCollectionName());
    }

    /**
     * 保存账号，并且删除老的的账号
     * @param username
     * @param fileName
     * @param address
     */
    public void saveOne(String username, String fileName, String address) {
        removeByName(username);
        Account account = new Account();
        account.setAccount(username);
        account.setAddress(address.toLowerCase());
        account.setWalletFile(fileName);
        save(account);
    }

    public void saveOne(String username, String address) {
        removeByName(username);
        Account account = new Account();
        account.setAccount(username);
        account.setAddress(address);
        save(account);
    }


    /**
     * 获取所有账户
     * @return
     */
    public List<Account> findAll() {
        return mongoTemplate.findAll(Account.class,getCollectionName());
    }

    /**
     * 获取账户数量
     * @return
     */
    public long count(){
        Query query = new Query();
        Sort.Order order = new Sort.Order(Sort.Direction.ASC, "_id");
        Sort sort = new Sort(order);
        query.with(sort);
        return mongoTemplate.count(query,getCollectionName());
    }

    /**
     * 分页获取账户
     * @param pageNo
     * @param pageSize
     * @return
     */
    public List<Account> find(int pageNo,int pageSize){
        Sort.Order order = new Sort.Order(Sort.Direction.ASC, "_id");
        Sort sort = new Sort(order);
        PageRequest page = new PageRequest(pageNo, pageSize, sort);
        Query query = new Query();
        query.with(page);
        return mongoTemplate.find(query,Account.class,getCollectionName());
    }


    /**
     * 根据余额查询
     * @param minAmount
     * @return
     */
    public List<Account> findByBalance(BigDecimal minAmount) {
        Query query = new Query();
        Criteria criteria = Criteria.where("balance").gte(minAmount);
        query.addCriteria(criteria);
        Sort sort = new Sort(new Sort.Order(Sort.Direction.DESC, "balance"));
        query.with(sort);
        return mongoTemplate.find(query, Account.class, getCollectionName());
    }

    /**
     * 根据余额和手续费查询
     * @param minAmount
     * @param gasLimit
     * @return
     */
    public List<Account> findByBalanceAndGas(BigDecimal minAmount,BigDecimal gasLimit) {
        Query query = new Query();
        Criteria criteria = Criteria.where("balance").gte(minAmount);
        criteria.andOperator(Criteria.where("gas").gte(gasLimit));
        query.addCriteria(criteria);
        Sort sort = new Sort(new Sort.Order(Sort.Direction.DESC, "balance"));
        query.with(sort);
        return mongoTemplate.find(query, Account.class, getCollectionName());
    }

    /**
     * 查询钱包总额
     *
     * @return
     */
    public BigDecimal findBalanceSum() {
        Aggregation aggregation = Aggregation.
                newAggregation(Aggregation.group("max").sum("balance").as("totalBalance"))
                .withOptions(Aggregation.newAggregationOptions().cursor(new BasicDBObject()).build());
        AggregationResults<BalanceSum> results = mongoTemplate.aggregate(aggregation, getCollectionName(), BalanceSum.class);
        List<BalanceSum> list = results.getMappedResults();
        return list.get(0).getTotalBalance().setScale(8, BigDecimal.ROUND_DOWN);
    }


    /**
     * 更新余额
     *
     * @param address
     * @param balance
     */
    public void updateBalance(String address, BigDecimal balance) {
        Query query = new Query();
        Criteria criteria = Criteria.where("address").is(address.toLowerCase());
        query.addCriteria(criteria);
        WriteResult result = mongoTemplate.updateFirst(query, Update.update("balance", balance.setScale(8, BigDecimal.ROUND_DOWN)), getCollectionName());
    }

    public void updateBalanceAndGas(String address, BigDecimal balance,BigDecimal gas) {
        Query query = new Query();
        Criteria criteria = Criteria.where("address").is(address.toLowerCase());
        query.addCriteria(criteria);
        Update update =  new Update();
        update.set("balance", balance.setScale(8, BigDecimal.ROUND_DOWN));
        update.set("gas",gas);
        WriteResult result = mongoTemplate.updateFirst(query,update, getCollectionName());
    }
}
