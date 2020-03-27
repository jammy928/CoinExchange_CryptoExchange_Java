package com.bizzan.bitrade.service;

import com.bizzan.bitrade.constant.CommonStatus;
import com.bizzan.bitrade.constant.SysAdvertiseLocation;
import com.bizzan.bitrade.dao.SysAdvertiseDao;
import com.bizzan.bitrade.entity.SysAdvertise;
import com.bizzan.bitrade.pagination.PageResult;
import com.bizzan.bitrade.service.Base.BaseService;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.bizzan.bitrade.entity.QSysAdvertise.sysAdvertise;

import java.util.List;

/**
 * @author GS
 * @description 系统广告service
 * @date 2018/1/6 16:45
 */
@Service
public class SysAdvertiseService extends BaseService {
    @Autowired
    SysAdvertiseDao sysAdvertiseDao;

    /**
     * 条件查询对象 pageNo pageSize 同时传时分页
     *
     * @param predicateList
     * @param pageNo
     * @param pageSize
     * @return
     */
    @Transactional(readOnly = true)
    public PageResult<SysAdvertise> query(List<Predicate> predicateList, Integer pageNo, Integer pageSize) {
        List<SysAdvertise> list;
        JPAQuery<SysAdvertise> jpaQuery = queryFactory.selectFrom(sysAdvertise);
        if (predicateList != null) {
            jpaQuery.where(predicateList.toArray(new Predicate[predicateList.size()]));
        }
        if (pageNo != null && pageSize != null) {
            list = jpaQuery.orderBy(new OrderSpecifier<>(Order.DESC, sysAdvertise.createTime))
                    .offset((pageNo - 1) * pageSize).limit(pageSize).fetch();
        } else {
            list = jpaQuery.orderBy(new OrderSpecifier<>(Order.DESC, sysAdvertise.createTime)).fetch();
        }
        return new PageResult<>(list, jpaQuery.fetchCount());
    }

    public SysAdvertise findOne(String serialNumber) {
        return sysAdvertiseDao.findOne(serialNumber);
    }

    public int getMaxSort(){
        return sysAdvertiseDao.findMaxSort();
    }

    public SysAdvertise save(SysAdvertise sysAdvertise) {
        return sysAdvertiseDao.save(sysAdvertise);
    }

    /**
     *
     * @param sysAdvertise
     */
    public void saveAll(List<SysAdvertise> sysAdvertise) {
        for (SysAdvertise sys:sysAdvertise) {
              sysAdvertiseDao.save(sys);
        }
    }

    @Override
    public List<SysAdvertise> findAll() {
        return sysAdvertiseDao.findAll();
    }

    //根据sort，sysAdvertiseLocation对数据进行筛选
    public List<SysAdvertise> querySysAdvertise(int sort,int cate) {
        return sysAdvertiseDao.querySysAdvertise(sort , cate);
    }

    public void deleteOne(String serialNumber) {
        sysAdvertiseDao.delete(serialNumber);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteBatch(String[] array) {
        for (String serialNumber : array) {
            sysAdvertiseDao.delete(serialNumber);
        }
    }

    public List<SysAdvertise> findAllNormal(SysAdvertiseLocation sysAdvertiseLocation) {
        return sysAdvertiseDao.findAllByStatusAndSysAdvertiseLocationOrderBySortAsc(CommonStatus.NORMAL, sysAdvertiseLocation);
    }

    public List<SysAdvertise> findAllNormal(SysAdvertiseLocation sysAdvertiseLocation, String lang) {
        return sysAdvertiseDao.findAllByStatusAndSysAdvertiseLocationAndLangOrderBySortAsc(CommonStatus.NORMAL, sysAdvertiseLocation, lang);
    }

    public List<SysAdvertise> findAll(List<Predicate> predicateList) {
        List<SysAdvertise> list;
        JPAQuery<SysAdvertise> jpaQuery = queryFactory.selectFrom(sysAdvertise);
        if (predicateList != null) {
            jpaQuery.where(predicateList.toArray(new Predicate[predicateList.size()]));
        }
        list = jpaQuery.orderBy(new OrderSpecifier<>(Order.DESC, sysAdvertise.createTime)).fetch();
        return list;
    }

    public Page<SysAdvertise> findAll(Predicate predicate, Pageable pageable) {
        return sysAdvertiseDao.findAll(predicate, pageable);
    }


}
