package com.bizzan.bitrade.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.bizzan.bitrade.dao.base.BaseDao;
import com.bizzan.bitrade.entity.ActivityOrder;


@Repository
public interface ActivityOrderDao extends  BaseDao<ActivityOrder> {
	
	List<ActivityOrder> getAllByActivityIdEquals(Long activityId);
	List<ActivityOrder> getAllByMemberIdAndActivityIdEquals(Long memberId, Long activityId);
	
}
