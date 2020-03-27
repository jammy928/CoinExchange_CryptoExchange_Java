package com.bizzan.bitrade.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bizzan.bitrade.constant.CommonStatus;
import com.bizzan.bitrade.dao.OtcCoinDao;
import com.bizzan.bitrade.entity.OtcCoin;
import com.bizzan.bitrade.entity.QOtcCoin;
import com.bizzan.bitrade.pagination.Criteria;
import com.bizzan.bitrade.pagination.PageResult;
import com.bizzan.bitrade.pagination.Restrictions;
import com.bizzan.bitrade.service.Base.BaseService;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.sparkframework.sql.model.Model;

/**
 * @author GS
 * @description
 * @date 2018/1/11 13:45
 */
@Service
public class OtcCoinService extends BaseService {
    @Autowired
    private OtcCoinDao otcCoinDao;

    /**
     * 条件查询对象 pageNo pageSize 同时传时分页
     *
     * @param booleanExpressionList
     * @param pageNo
     * @param pageSize
     * @return
     */
    @Transactional(readOnly = true)
    public PageResult<OtcCoin> queryWhereOrPage(List<BooleanExpression> booleanExpressionList, Integer pageNo, Integer pageSize) {
        List<OtcCoin> list;
        JPAQuery<OtcCoin> jpaQuery = queryFactory.selectFrom(QOtcCoin.otcCoin);
        if (booleanExpressionList != null) {
            jpaQuery.where(booleanExpressionList.toArray(new BooleanExpression[booleanExpressionList.size()]));
        }
        if (pageNo != null && pageSize != null) {
            list = jpaQuery.offset((pageNo - 1) * pageSize).limit(pageSize).fetch();
        } else {
            list = jpaQuery.fetch();
        }
        return new PageResult<>(list, jpaQuery.fetchCount());
    }


    public OtcCoin save(OtcCoin otcCoin) {
        return otcCoinDao.save(otcCoin);
    }

    @Override
    public List<OtcCoin> findAll() {
        return otcCoinDao.findAll();
    }

    public OtcCoin findOne(Long id) {
        return otcCoinDao.findOne(id);
    }

    public OtcCoin findByUnit(String unit) {
        return otcCoinDao.findOtcCoinByUnit(unit);
    }

    public List<Map<String, String>> getAllNormalCoin() throws Exception {
        return new Model("otc_coin")
                .field("id,name,name_cn as nameCn,unit,sell_min_amount,sort,buy_min_amount")
                .where("status=?", CommonStatus.NORMAL.ordinal())
                .order("sort asc").select();
    }

    public List<OtcCoin> getNormalCoin() {
        return otcCoinDao.findAllByStatus(CommonStatus.NORMAL);
    }

    /**
     * @author GS
     * @description 分页请求
     * @date 2018/1/11 15:04
     */
    public Page<OtcCoin> pageQuery(Integer pageNo, Integer pageSize, String name, String nameCn) {
        //排序方式
        Sort orders = Criteria.sortStatic("sort");
        //分页参数
        PageRequest pageRequest = new PageRequest(pageNo, pageSize, orders);
        //查询条件
        Criteria<OtcCoin> specification = new Criteria<OtcCoin>();
        specification.add(Restrictions.like("name", name, false));
        specification.add(Restrictions.like("nameCn", nameCn, false));
        return otcCoinDao.findAll(specification, pageRequest);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletes(Long[] ids) {
        for (long id : ids) {
            otcCoinDao.delete(id);
        }
    }

    public Page<OtcCoin> findAll(Predicate predicate, Pageable pageable) {
        return otcCoinDao.findAll(predicate, pageable);
    }

    public List<String> findAllUnits(){
        List<String> list = otcCoinDao.findAllUnits();
        return  list ;
    }
}
