package com.bizzan.bitrade.service;

import com.bizzan.bitrade.constant.RewardRecordType;
import com.bizzan.bitrade.dao.RewardRecordDao;
import com.bizzan.bitrade.entity.Member;
import com.bizzan.bitrade.entity.RewardRecord;
import com.bizzan.bitrade.pagination.Criteria;
import com.bizzan.bitrade.pagination.Restrictions;
import com.bizzan.bitrade.service.Base.BaseService;
import com.querydsl.core.types.dsl.BooleanExpression;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author GS
 * @date 2018年03月08日
 */
@Service
public class RewardRecordService extends BaseService {
    @Autowired
    private RewardRecordDao rewardRecordDao;

    public RewardRecord save(RewardRecord rewardRecord) {
        return rewardRecordDao.save(rewardRecord);
    }

    public List<RewardRecord> queryRewardPromotionList(Member member) {
        return rewardRecordDao.findAllByMemberAndType(member, RewardRecordType.PROMOTION);
    }

    public Page<RewardRecord> queryRewardPromotionPage(int pageNo, int pageSize, Member member) {
        Sort sort = new Sort(new Sort.Order(Sort.Direction.DESC, "id"));
        Pageable pageable = new PageRequest(pageNo - 1, pageSize, sort);

        ArrayList<BooleanExpression> booleanExpressions = new ArrayList<>();

        Specification specification = new Specification<RewardRecord>() {
            List<Predicate> predicates = new ArrayList<>();

            @Override
            public Predicate toPredicate(Root<RewardRecord> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                predicates.add(criteriaBuilder.equal(root.get("member"), member));
                predicates.add(criteriaBuilder.equal(root.get("type"), 0));
                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
        return rewardRecordDao.findAll(specification,pageable);
    }


    public Map<String, BigDecimal> getAllPromotionReward(long memberId, RewardRecordType type) {
        List<Object[]> list = rewardRecordDao.getAllPromotionReward(memberId, type.getOrdinal());
        Map<String, BigDecimal> map = new HashMap<>();
        for (Object[] array : list) {
            map.put(array[0].toString(), (BigDecimal) array[1]);
        }
        return map;
    }

    /*public */

}
