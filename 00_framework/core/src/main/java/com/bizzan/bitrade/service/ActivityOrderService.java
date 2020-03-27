package com.bizzan.bitrade.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bizzan.bitrade.constant.BooleanEnum;
import com.bizzan.bitrade.dao.ActivityOrderDao;
import com.bizzan.bitrade.entity.Activity;
import com.bizzan.bitrade.entity.ActivityOrder;
import com.bizzan.bitrade.entity.MemberWallet;
import com.bizzan.bitrade.entity.MiningOrder;
import com.bizzan.bitrade.pagination.Criteria;
import com.bizzan.bitrade.pagination.Restrictions;
import com.bizzan.bitrade.service.Base.BaseService;
import com.bizzan.bitrade.util.MessageResult;
import com.querydsl.core.types.Predicate;

@Service
public class ActivityOrderService extends BaseService {
	@Autowired
    private ActivityOrderDao activityOrderDao;
	
	@Autowired
	private ActivityService activityService;
	
	@Autowired
    private MemberWalletService walletService;
	
	public ActivityOrder findOne(Long id) {
		return activityOrderDao.findOne(id);
	}
	
    public ActivityOrder save(ActivityOrder activity) {
        return activityOrderDao.save(activity);
    }

    public ActivityOrder saveAndFlush(ActivityOrder activity) {
        return activityOrderDao.saveAndFlush(activity);
    }
    
    public ActivityOrder findById(Long id) {
        return activityOrderDao.findOne(id);
    }
    
    public Page<ActivityOrder> findAll(Predicate predicate, Pageable pageable){
    	return activityOrderDao.findAll(predicate, pageable);
    }
    
    public Page<ActivityOrder> finaAllByMemberId(Long memberId, int pageNo, int pageSize){
    	Sort orders = Criteria.sortStatic("createTime.desc");
        //分页参数
        PageRequest pageRequest = new PageRequest(pageNo - 1, pageSize, orders);
        //查询条件
        Criteria<ActivityOrder> specification = new Criteria<ActivityOrder>();
        specification.add(Restrictions.eq("memberId", memberId, false));
        
        return activityOrderDao.findAll(specification, pageRequest);
    }

    /**
     * 根据活动ID获取参与详情
     * @param activityId
     * @return
     */
	public List<ActivityOrder> findAllByActivityId(Long activityId) {
		return activityOrderDao.getAllByActivityIdEquals(activityId);
	}
	
	/**
	 * 根据活动ID和用户ID获取用户参与详情
	 * @param memberId
	 * @param activityId
	 * @return
	 */
	public List<ActivityOrder> findAllByActivityIdAndMemberId(Long memberId, Long activityId) {
		return activityOrderDao.getAllByMemberIdAndActivityIdEquals(memberId, activityId);
	}
	@Transactional(rollbackFor = Exception.class)
	public MessageResult saveActivityOrder(Long memberId, ActivityOrder activityOrder) {
		MemberWallet wallet = walletService.findByCoinUnitAndMemberId(activityOrder.getBaseSymbol(), memberId);
		if(wallet.getIsLock().equals(BooleanEnum.IS_TRUE)){
            return MessageResult.error("钱包已锁定");
        }
		// 冻结资产
        MessageResult result = walletService.freezeBalance(wallet, activityOrder.getTurnover());
        if (result.getCode() != 0) {
            return MessageResult.error("无法锁定资产");
        }
        // 更新Activity参与信息
        Activity activity = activityService.findOne(activityOrder.getActivityId());
        if (activity == null) {
        	return MessageResult.error(500, "非法的活动！");
        }
        if(activity.getType() == 3) { // 持仓瓜分，更新冻结资产数量
        	activity.setFreezeAmount(activity.getFreezeAmount().add(activityOrder.getFreezeAmount()));
        }else if(activity.getType() == 4){ // 自由认购，更新交易数量
        	activity.setTradedAmount(activity.getTradedAmount().add(activityOrder.getAmount()));
        }else if(activity.getType() == 5){ // 矿机认购，更新交易数量
        	activity.setTradedAmount(activity.getTradedAmount().add(activityOrder.getAmount()));
        }
        
        // 更新进度
        if(activity.getType() == 4 || activity.getType() == 5) {
        	// 新进度不能小于旧进度
        	int newProgress = activity.getTradedAmount().divide(activity.getTotalSupply()).multiply(new BigDecimal(100)).intValue();
        	activity.setProgress(newProgress >= activity.getProgress() ? newProgress : activity.getProgress());
        }
        // 更新Activity表
        Activity saveResult = activityService.saveAndFlush(activity);
        if(saveResult == null) {
        	return MessageResult.error(500, "更新活动失败！");
        }
        
        ActivityOrder order = activityOrderDao.saveAndFlush(activityOrder);

        if (order != null) {
            return MessageResult.success("success");
        } else {
            return MessageResult.error(500, "error");
        }
	}
}
