package com.bizzan.bitrade.service;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bizzan.bitrade.constant.OrderStatus;
import com.bizzan.bitrade.dao.MemberDao;
import com.bizzan.bitrade.dao.OrderDao;
import com.bizzan.bitrade.entity.QMember;
import com.bizzan.bitrade.entity.Statistics;
import com.bizzan.bitrade.service.Base.BaseService;
import com.bizzan.bitrade.util.DateUtil;
import com.bizzan.bitrade.util.EnumHelperUtil;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author GS
 * @description 统计service
 * @date 2018/1/8 16:21
 */
@Service
public class StatisticsService extends BaseService {
    @PersistenceContext
    private EntityManager em;

    @Autowired
    private MemberDao memberDao;
    @Autowired
    private OrderDao orderDao;
    /**
     * @param sql 需要包含startTime endTime 两个占位符
     * @author GS
     * @description 获取统计数据
     * @date 2018/1/9 15:25
     */
    public List getStatistics(String startTime, String endTime, String sql) {
        Query query = em.createNativeQuery(sql);
        query.setParameter("startTime", startTime);
        query.setParameter("endTime", endTime);
        List resultList = query.getResultList();
        return resultList;
    }

    /**
     * 统计最近几日注册人数
     * @param day
     * @return
     */
    public  int getLatelyRegMember(int day){
        Date startTime = DateUtil.strToDate(DateUtil.getPastDate(day)+" 00:00:00");
        Date endTime = DateUtil.strToDate(DateUtil.getDate()+" 23:59:59");
        return memberDao.countByRegistrationTimeBetween(startTime,endTime);
    }

    /**
     * 查询时间段内订单数量 status为-1代表所有的状态
     * @param startTime
     * @param endTime
     * @param status
     * @return
     */
    public  int getLatelyOrder(String startTime,String endTime,int status){
        Date startTimeDate = DateUtil.strToDate(startTime +" 00:00:00");
        Date endTimeDate = DateUtil.strToDate(endTime +" 23:59:59");
        if(status<0){
            return orderDao.countByCreateTimeBetween(startTimeDate,endTimeDate);
        }
        OrderStatus orderStatus = EnumHelperUtil.indexOf(OrderStatus.class,status);
        return orderDao.countByStatusAndCreateTimeBetween(orderStatus,startTimeDate,endTimeDate);
    }
    //根据状态统计订单
    public  int getLatelyOrder(OrderStatus status){
        return orderDao.countByStatus(status);
    }
    public  int getLatelyAdvertise(int i){

        return 0;
    }
}
