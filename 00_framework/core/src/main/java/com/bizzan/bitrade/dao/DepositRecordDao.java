package com.bizzan.bitrade.dao;

import java.util.List;

import com.bizzan.bitrade.constant.DepositStatusEnum;
import com.bizzan.bitrade.dao.base.BaseDao;
import com.bizzan.bitrade.entity.DepositRecord;
import com.bizzan.bitrade.entity.Member;

/**
 * @author Shaoxianjun
 * @date 2019/5/7
 */
public interface DepositRecordDao extends BaseDao<DepositRecord> {
    public DepositRecord findById(String id);

    public List<DepositRecord> findByMemberAndStatus(Member member, DepositStatusEnum status);
}
