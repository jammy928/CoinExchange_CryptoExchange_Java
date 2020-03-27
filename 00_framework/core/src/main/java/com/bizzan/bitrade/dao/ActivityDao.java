package com.bizzan.bitrade.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.stereotype.Repository;

import com.bizzan.bitrade.constant.CommonStatus;
import com.bizzan.bitrade.dao.base.BaseDao;
import com.bizzan.bitrade.entity.Activity;
import com.bizzan.bitrade.entity.Coin;
import com.bizzan.bitrade.entity.MemberTransaction;

@Repository
public interface ActivityDao extends  BaseDao<Activity> {
	
    List<Activity> findAllByStep(int step);

}
