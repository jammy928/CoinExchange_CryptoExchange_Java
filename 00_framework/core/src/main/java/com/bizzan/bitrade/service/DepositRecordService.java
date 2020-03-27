package com.bizzan.bitrade.service;

import com.bizzan.bitrade.constant.DepositStatusEnum;
import com.bizzan.bitrade.constant.PageModel;
import com.bizzan.bitrade.dao.DepositRecordDao;
import com.bizzan.bitrade.entity.DepositRecord;
import com.bizzan.bitrade.entity.Member;
import com.bizzan.bitrade.service.Base.BaseService;
import com.querydsl.core.types.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Shaoxianjun
 * @date 2019/5/7
 */
@Service
public class DepositRecordService extends BaseService {
    @Autowired
    private DepositRecordDao depositRecordDao;

    public List<DepositRecord> getAll(){
        return depositRecordDao.findAll();
    }

    public Page<DepositRecord> list(Predicate predicate, PageModel pageModel){
        return depositRecordDao.findAll(predicate,pageModel.getPageable());
    }

    public List<DepositRecord> findAll(Predicate predicate){
        return (List<DepositRecord>) depositRecordDao.findAll(predicate);
    }

    public DepositRecord findOne(String id){
        return depositRecordDao.findById(id);
    }

    public void update(DepositRecord depositRecord){
        depositRecordDao.save(depositRecord);
    }

    public void create(DepositRecord depositRecord){
        depositRecordDao.save(depositRecord);
    }

    public List<DepositRecord> findByMemberAndStatus(Member member, DepositStatusEnum status){
        return depositRecordDao.findByMemberAndStatus(member,status);
    }

    public DepositRecord save(DepositRecord depositRecord){
        return depositRecordDao.save(depositRecord);
    }
}
