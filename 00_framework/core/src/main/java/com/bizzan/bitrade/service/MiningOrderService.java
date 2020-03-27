package com.bizzan.bitrade.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.bizzan.bitrade.dao.MiningOrderDao;
import com.bizzan.bitrade.entity.Activity;
import com.bizzan.bitrade.entity.MiningOrder;
import com.bizzan.bitrade.pagination.Criteria;
import com.bizzan.bitrade.pagination.Restrictions;
import com.bizzan.bitrade.service.Base.BaseService;
import com.querydsl.core.types.Predicate;
@Service
public class MiningOrderService extends BaseService {
	@Autowired
	private MiningOrderDao miningOrderDao;
	
	public MiningOrder findOne(Long id) {
		return miningOrderDao.findOne(id);
	}
	
    public MiningOrder save(MiningOrder miningOrder) {
        return miningOrderDao.save(miningOrder);
    }

    public MiningOrder saveAndFlush(MiningOrder miningOrder) {
        return miningOrderDao.saveAndFlush(miningOrder);
    }
    
    public Page<MiningOrder> findAll(Predicate predicate, Pageable pageable){
    	return miningOrderDao.findAll(predicate, pageable);
    }
    
	public List<MiningOrder> findAllByMemberId(Long memberId){
		return miningOrderDao.findAllByMemberId(memberId);
	}
	
	public List<MiningOrder> findAllByActivityId(Long activityId){
		return miningOrderDao.findAllByActivityId(activityId);
	}
	
	public List<MiningOrder> findAllByMemberIdAndActivityId(Long memberId, Long activityId){
		return miningOrderDao.findAllByMemberIdAndActivityId(memberId, activityId);
	}
	
	public List<MiningOrder> findAllByMiningStatus(int miningStatus){
		return miningOrderDao.findAllByMiningStatus(miningStatus);
	}
	
	public Page<MiningOrder> findAllByMemberIdPage(Long memberId, int pageNo, int pageSize){
		Sort orders = Criteria.sortStatic("createTime.desc");
        //分页参数
        PageRequest pageRequest = new PageRequest(pageNo - 1, pageSize, orders);
        //查询条件
        Criteria<MiningOrder> specification = new Criteria<MiningOrder>();
        specification.add(Restrictions.eq("memberId", memberId, false));
        
        return miningOrderDao.findAll(specification, pageRequest);
	}
}
