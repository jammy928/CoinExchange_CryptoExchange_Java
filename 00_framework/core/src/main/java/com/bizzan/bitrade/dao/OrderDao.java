package com.bizzan.bitrade.dao;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bizzan.bitrade.constant.OrderStatus;
import com.bizzan.bitrade.dao.base.BaseDao;
import com.bizzan.bitrade.entity.Order;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author GS
 * @date 2017年12月11日
 */
public interface OrderDao extends BaseDao<Order> {


    /**
     * 根据用户id和订单编号查询订单
     *
     * @param memberId 用户id
     * @param orderSn  订单编号
     * @return
     */
    List<Order> getOrderByMemberIdAndOrderSn(Long memberId, String orderSn);

    /**
     * 根据用户id查询订单
     *
     * @param memberId 用户id
     * @return
     */
    List<Order> getOrderByMemberId(Long memberId);

    /**
     * 根据用户id和状态查询订单
     *
     * @param memberId 用户id
     * @param status   状态
     * @return
     */
    List<Order> getOrderByMemberIdAndStatus(Long memberId, OrderStatus status);

    /**
     * 根据用户id、订单编号和状态查询订单
     *
     * @param memberId
     * @param status
     * @param orderSn
     * @return
     */
    List<Order> getOrderByMemberIdAndStatusAndOrderSn(Long memberId, OrderStatus status, String orderSn);

    List<Order> findByAdvertiseId(Long advertiseId);

    Order getOrderByOrderSn(String v2);

    /**
     * 付款
     *
     * @param date
     * @param status
     * @param orderSn
     * @return
     */
    @Modifying
    @Query("update Order a set a.payTime=:date,a.status=:status where a.status=1 and a.orderSn=:orderSn")
    int updatePayOrder(@Param("date") Date date, @Param("status") OrderStatus status, @Param("orderSn") String orderSn);

    @Modifying
    @Query("update Order a set a.cancelTime=:date,a.status=:status where (a.status=1 or a.status=2 or a.status=4) and a.orderSn=:orderSn")
    int cancelOrder(@Param("date") Date date, @Param("status") OrderStatus status, @Param("orderSn") String orderSn);

    @Modifying
    @Query("update Order a set a.releaseTime=:date,a.status=:status where  (a.status=2 or a.status=4) and a.orderSn=:orderSn")
    int releaseOrder(@Param("date") Date date, @Param("status") OrderStatus status, @Param("orderSn") String orderSn);

    @Query("select a from Order a where timestampdiff(MINUTE,a.createTime,:date)>=a.timeLimit and a.status=1")
    List<Order> findAllExpiredOrder(@Param("date") Date date);

    @Query("select a from Order a where (a.memberId=:myId or a.customerId=:myId) and (a.status=:unPay or a.status=:paid or a.status=:appeal)")
    List<Order> fingAllProcessingOrder( @Param("myId") Long myId,@Param("unPay") OrderStatus unPay,@Param("paid") OrderStatus paid,@Param("appeal") OrderStatus appeal);

    @Modifying
    @Query("update Order a set a.status=:status where a.status=2 and a.orderSn=:orderSn")
    int updateAppealOrder( @Param("status") OrderStatus status, @Param("orderSn") String orderSn);

    int countByCreateTimeBetween(Date startTime,Date endTime);
    int countByStatusAndCreateTimeBetween(OrderStatus status,Date startTime,Date endTime);
    int countByStatus(OrderStatus status);

    @Query(value = "select a.unit unit,date_format(b.release_time,'%Y-%m-%d'), sum(b.number) amount ,sum(b.commission) fee ,sum(money) from otc_order b ,otc_coin a where a.id = b.coin_id and b.status = 3 and date_format(b.release_time,'%Y-%m-%d') = :date group by a.unit",nativeQuery = true)
    List<Object[]> getOtcTurnoverAmount(@Param("date")String date);

    @Query(value = "select sum(b.commission) as fee,sum(b.money) as money from Order b  where  b.status = 3 and b.memberId = :memberId")
    Map<String,Object> getBusinessStatistics(@Param("memberId")Long memberId);
}