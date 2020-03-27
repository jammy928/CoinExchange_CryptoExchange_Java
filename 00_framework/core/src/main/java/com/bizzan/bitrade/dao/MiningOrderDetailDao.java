package com.bizzan.bitrade.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.bizzan.bitrade.dao.base.BaseDao;
import com.bizzan.bitrade.entity.MiningOrder;
import com.bizzan.bitrade.entity.MiningOrderDetail;

@Repository
public interface MiningOrderDetailDao  extends BaseDao<MiningOrderDetail> {
	
	List<MiningOrderDetail> findAllByMemberId(Long memberId);
	
}
