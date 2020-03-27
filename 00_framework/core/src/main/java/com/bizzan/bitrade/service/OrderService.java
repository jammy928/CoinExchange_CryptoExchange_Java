package com.bizzan.bitrade.service;


import com.bizzan.bitrade.constant.AdvertiseType;
import com.bizzan.bitrade.constant.OrderStatus;
import com.bizzan.bitrade.constant.PageModel;
import com.bizzan.bitrade.constant.TransactionTypeEnum;
import com.bizzan.bitrade.dao.OrderDao;
import com.bizzan.bitrade.entity.MemberWallet;
import com.bizzan.bitrade.entity.Order;
import com.bizzan.bitrade.entity.QAppeal;
import com.bizzan.bitrade.entity.QOrder;
import com.bizzan.bitrade.exception.InformationExpiredException;
import com.bizzan.bitrade.pagination.Criteria;
import com.bizzan.bitrade.pagination.PageResult;
import com.bizzan.bitrade.pagination.Restrictions;
import com.bizzan.bitrade.service.Base.BaseService;
import com.bizzan.bitrade.util.IdWorkByTwitter;
import com.bizzan.bitrade.util.MessageResult;
import com.bizzan.bitrade.vo.OtcOrderVO;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import static com.bizzan.bitrade.util.BigDecimalUtils.add;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author GS
 * @date 2017年12月11日
 */
@Service
public class OrderService extends BaseService {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private OrderDao orderDao;


    @Autowired
    private IdWorkByTwitter idWorkByTwitter;
    @Autowired
    private AdvertiseService advertiseService;
    @Autowired
    private MemberWalletService memberWalletService;

    @Transactional(rollbackFor = Exception.class)
    public void cancelOrderTask(Order order) throws InformationExpiredException {
        if (order.getAdvertiseType().equals(AdvertiseType.BUY)) {
            //更改广告
            if (!advertiseService.updateAdvertiseAmountForCancel(order.getAdvertiseId(), order.getNumber())) {
                throw new InformationExpiredException("Information Expired");
            }
            //更改钱包
            MemberWallet memberWallet = memberWalletService.findByOtcCoinAndMemberId(order.getCoin(), order.getCustomerId());
            MessageResult result = memberWalletService.thawBalance(memberWallet, order.getNumber());
            if (result.getCode() != 0) {
                throw new InformationExpiredException("Information Expired");
            }
        } else {
            //更改广告
            if (!advertiseService.updateAdvertiseAmountForCancel(order.getAdvertiseId(), add(order.getNumber(), order.getCommission()))) {
                throw new InformationExpiredException("Information Expired");
            }
            //更改钱包
            MemberWallet memberWallet = memberWalletService.findByOtcCoinAndMemberId(order.getCoin(), order.getMemberId());
            MessageResult result = memberWalletService.thawBalance(memberWallet, add(order.getNumber(), order.getCommission()));
            if (result.getCode() != 0) {
                throw new InformationExpiredException("Information Expired");
            }
        }
        //取消订单
        if (!(this.cancelOrder(order.getOrderSn()) > 0)) {
            throw new InformationExpiredException("Information Expired");
        }
    }

    /**
     * 条件查询对象 pageNo pageSize 同时传时分页
     *
     * @param predicateList
     * @param pageNo
     * @param pageSize
     * @return
     */
    @Transactional(readOnly = true)
    public PageResult<Order> query(List<Predicate> predicateList, Integer pageNo, Integer pageSize) {
        List<Order> list;
        JPAQuery<Order> jpaQuery = queryFactory.selectFrom(QOrder.order);
        if (predicateList != null) {
            jpaQuery.where(predicateList.toArray(new Predicate[predicateList.size()]));
        }
        if (pageNo != null && pageSize != null) {
            list = jpaQuery.offset((pageNo - 1) * pageSize).limit(pageSize).fetch();
        } else {
            list = jpaQuery.fetch();
        }
        return new PageResult<>(list, jpaQuery.fetchCount());
    }

    public Order findOne(Long id) {
        return orderDao.findOne(id);
    }

    public Order findOneByOrderSn(String orderSn) {
        return orderDao.getOrderByOrderSn(orderSn);
    }

    public int updateOrderAppeal(String orderSn) {
        return orderDao.updateAppealOrder(OrderStatus.APPEAL, orderSn);
    }

    public int payForOrder(String orderSn) {
        return orderDao.updatePayOrder(new Date(), OrderStatus.PAID, orderSn);
    }

    /**
     * 取消订单
     *
     * @param orderSn
     * @return
     */
    public int cancelOrder(String orderSn) {
        return orderDao.cancelOrder(new Date(), OrderStatus.CANCELLED, orderSn);
    }

    /**
     * 订单放行
     *
     * @param orderSn
     * @return
     */
    public int releaseOrder(String orderSn) {
        return orderDao.releaseOrder(new Date(), OrderStatus.COMPLETED, orderSn);
    }

    /**
     * 生成订单
     *
     * @param order
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Order saveOrder(Order order) {
        order.setOrderSn(String.valueOf(idWorkByTwitter.nextId()));
        return orderDao.save(order);
    }

    public Page<Order> pageQuery(int pageNo, Integer pageSize, OrderStatus status, long id, String orderSn) {
        Sort orders = Criteria.sortStatic("id.desc");
        PageRequest pageRequest = new PageRequest(pageNo, pageSize, orders);
        Criteria<Order> specification = new Criteria<Order>();
        specification.add(Restrictions.or(Restrictions.eq("memberId", id, false), Restrictions.eq("customerId", id, false)));
        specification.add(Restrictions.eq("status", status, false));
        if (StringUtils.isNotBlank(orderSn)) {
            specification.add(Restrictions.like("orderSn", orderSn, false));
        }
        return orderDao.findAll(specification, pageRequest);
    }


    public Map getOrderBySn(Long memberId, String orderSn) {
        String sql = "select o.*,m.real_name from otc_order o  join member m on o.customer_id=m.id and o.member_id=:memberId and o.order_sn =:orderSn ";
        Query query = em.createNativeQuery(sql);
        //设置结果转成Map类型
        query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        Object object = query.setParameter("memberId", memberId).setParameter("orderSn", orderSn).getSingleResult();
        Map map = (HashMap) object;
        return map;
    }

    public List<Order> checkExpiredOrder() {
        return orderDao.findAllExpiredOrder(new Date());
    }


    @Override
    public List<Order> findAll() {
        return orderDao.findAll();
    }

    public Order save(Order order) {
        return orderDao.save(order);
    }

    public MessageResult getOrderNum() {
        Predicate predicate = QOrder.order.status.eq(OrderStatus.NONPAYMENT);
        Long noPayNum = orderDao.count(predicate);
        Long paidNum = orderDao.count(QOrder.order.status.eq(OrderStatus.PAID));
        Long finishedNum = orderDao.count(QOrder.order.status.eq(OrderStatus.COMPLETED));
        Long cancelNum = orderDao.count(QOrder.order.status.eq(OrderStatus.CANCELLED));
        Long appealNum = orderDao.count(QOrder.order.status.eq(OrderStatus.APPEAL));
        Map<String, Long> map = new HashMap<>();
        map.put("noPayNum", noPayNum);
        map.put("paidNum", paidNum);
        map.put("finishedNum", finishedNum);
        map.put("cancelNum", cancelNum);
        map.put("appealNum", appealNum);
        return MessageResult.getSuccessInstance("获取成功", map);
    }

    public List<Order> getAllOrdering(Long id) {
        return orderDao.fingAllProcessingOrder(id, OrderStatus.APPEAL, OrderStatus.PAID, OrderStatus.NONPAYMENT);
    }

    public Order findOneByOrderId(String orderId) {
        return orderDao.getOrderByOrderSn(orderId);
    }
    public Page<Order> findAll(com.querydsl.core.types.Predicate predicate, Pageable pageable) {
        return orderDao.findAll(predicate, pageable);
    }

    public Page<OtcOrderVO> outExcel(List<Predicate> predicates , PageModel pageModel){
        List<OrderSpecifier> orderSpecifiers = pageModel.getOrderSpecifiers();
        JPAQuery<OtcOrderVO> query = queryFactory.select(
                Projections.fields(OtcOrderVO.class,
                        QOrder.order.id.as("id"),
                        QOrder.order.orderSn.as("orderSn"),
                        QOrder.order.advertiseType.as("advertiseType"),
                        QOrder.order.createTime.as("createTime"),
                        QOrder.order.memberName.as("memberName"),
                        QOrder.order.customerName.as("customerName"),
                        QOrder.order.coin.unit,
                        QOrder.order.money,
                        QOrder.order.number,
                        QOrder.order.commission.as("fee"),
                        QOrder.order.payMode.as("payMode"),
                        QOrder.order.releaseTime.as("releaseTime"),
                        QOrder.order.cancelTime.as("cancelTime"),
                        QOrder.order.payTime.as("payTime"),
                        QOrder.order.status.as("status"))
        ).from(QOrder.order).where(predicates.toArray(new BooleanExpression[predicates.size()]));
        query.orderBy(orderSpecifiers.toArray(new OrderSpecifier[orderSpecifiers.size()]));
        List<OtcOrderVO> list = query.offset((pageModel.getPageNo()-1)*pageModel.getPageSize()).limit(pageModel.getPageSize()).fetch();
        long total = query.fetchCount() ;
        return new PageImpl<>(list,pageModel.getPageable(),total);
    }

    public List<Object[]> getOtcOrderStatistics(String date){
        return orderDao.getOtcTurnoverAmount(date);
    }


}
