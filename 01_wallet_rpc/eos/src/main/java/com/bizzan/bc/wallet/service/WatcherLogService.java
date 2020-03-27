package com.bizzan.bc.wallet.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.bizzan.bc.wallet.entity.WatcherLog;

import java.util.Date;

@Service
public class WatcherLogService {
    @Autowired
    private MongoTemplate mongoTemplate;

    public void update(String coinName,Long height){
        WatcherLog watcherLog = findOne(coinName);
        if(watcherLog != null){
            Query query = new Query();
            Criteria criteria = Criteria.where("coinName").is(coinName);
            query.addCriteria(criteria);
            Update update =  new Update();
            update.set("lastSyncHeight",height);
            update.set("lastSyncTime",new Date());
            mongoTemplate.updateFirst(query, update, "watcher_log");
        }
        else{
            watcherLog = new WatcherLog();
            watcherLog.setCoinName(coinName);
            watcherLog.setLastSyncHeight(height);
            watcherLog.setLastSyncTime(new Date());
            mongoTemplate.insert(watcherLog,"watcher_log");
        }
    }

    public WatcherLog findOne(String coinName){
        Query query = new Query();
        Criteria criteria = Criteria.where("coinName").is(coinName);
        query.addCriteria(criteria);
        return mongoTemplate.findOne(query, WatcherLog.class,"watcher_log");
    }

	public void updateTime(String coinName, Date endTime) {
		WatcherLog watcherLog = findOne(coinName);
        if(watcherLog != null){
            Query query = new Query();
            Criteria criteria = Criteria.where("coinName").is(coinName);
            query.addCriteria(criteria);
            Update update =  new Update();
            update.set("lastSyncHeight", 0);
            update.set("lastSyncTime", endTime);
            mongoTemplate.updateFirst(query, update, "watcher_log");
        }
        else{
            watcherLog = new WatcherLog();
            watcherLog.setCoinName(coinName);
            watcherLog.setLastSyncHeight(0L);
            watcherLog.setLastSyncTime(endTime);
            mongoTemplate.insert(watcherLog,"watcher_log");
        }
	}
}
