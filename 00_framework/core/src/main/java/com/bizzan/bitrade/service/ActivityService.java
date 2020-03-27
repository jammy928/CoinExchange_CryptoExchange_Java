package com.bizzan.bitrade.service;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.bizzan.bitrade.dao.ActivityDao;
import com.bizzan.bitrade.entity.Activity;
import com.bizzan.bitrade.pagination.Criteria;
import com.bizzan.bitrade.pagination.Restrictions;
import com.bizzan.bitrade.service.Base.BaseService;
import com.querydsl.core.types.Predicate;

@Service
public class ActivityService extends BaseService {
	
	@Autowired
    private ActivityDao activityDao;
	
	public Activity findOne(Long id) {
		return activityDao.findOne(id);
	}
	
    public Activity save(Activity activity) {
        return activityDao.save(activity);
    }

    public Activity saveAndFlush(Activity activity) {
        return activityDao.saveAndFlush(activity);
    }
    
    public Activity findById(Long id) {
        return activityDao.findOne(id);
    }
    
    public Page<Activity> findAll(Predicate predicate, Pageable pageable){
    	return activityDao.findAll(predicate, pageable);
    }

	public Page<Activity> queryByStep(int pageNo, int pageSize, int step) {
        Sort orders = Criteria.sortStatic("createTime.desc");
        //分页参数
        PageRequest pageRequest = new PageRequest(pageNo - 1, pageSize, orders);
        //查询条件
        Criteria<Activity> specification = new Criteria<Activity>();
        if(step != -1) {
        	specification.add(Restrictions.eq("step", step, false));
        }
        specification.add(Restrictions.eq("status", 1, false));
        return activityDao.findAll(specification, pageRequest);
	}
}
