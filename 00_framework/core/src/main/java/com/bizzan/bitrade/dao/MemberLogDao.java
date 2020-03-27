package com.bizzan.bitrade.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.bizzan.bitrade.entity.MemberLog;


public interface MemberLogDao extends MongoRepository<MemberLog,Long> {
}
