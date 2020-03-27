package com.bizzan.bitrade.controller.otc;

import com.bizzan.bitrade.annotation.AccessLog;
import com.bizzan.bitrade.constant.*;
import com.bizzan.bitrade.controller.common.BaseAdminController;
import com.bizzan.bitrade.entity.*;
import com.bizzan.bitrade.es.ESUtils;
import com.bizzan.bitrade.event.OrderEvent;
import com.bizzan.bitrade.exception.InformationExpiredException;
import com.bizzan.bitrade.model.screen.AppealScreen;
import com.bizzan.bitrade.service.*;
import com.bizzan.bitrade.util.DateUtil;
import com.bizzan.bitrade.util.MessageResult;
import com.bizzan.bitrade.vo.AppealVO;
import com.querydsl.core.types.dsl.BooleanExpression;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.bizzan.bitrade.util.BigDecimalUtils.add;
import static org.springframework.util.Assert.isTrue;
import static org.springframework.util.Assert.notNull;

/**
 * @author Shaoxianjun
 * @description 后台申诉管理
 * @date 2019/1/23 9:26
 */
@Slf4j
@RestController
@RequestMapping("/otc/appeal")
public class AdminAppealController extends BaseAdminController {

    @Autowired
    private AppealService appealService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private AdvertiseService advertiseService;

    @Autowired
    private MemberWalletService memberWalletService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private LocaleMessageSourceService msService;

    @Autowired
    private OrderEvent orderEvent;

    @Autowired
    private MemberTransactionService memberTransactionService;
    @Autowired
    private ESUtils esUtils;


    @RequiresPermissions("otc:appeal:page-query")
    @PostMapping("page-query")
    @AccessLog(module = AdminModule.OTC, operation = "分页查找后台申诉Appeal")
    public MessageResult pageQuery(
            PageModel pageModel,
            AppealScreen screen) {

        StringBuilder headSqlBuilder = new StringBuilder("select a.id appealId,")
                .append("b.member_name advertiseCreaterUserName,b.member_real_name advertiseCreaterName,")
                .append("b.customer_name customerUserName,b.customer_real_name customerName,")
                .append("c.username initiatorUsername,c.real_name initiatorName,")
                .append("d.username associateUsername,d.real_name associateName,")
                .append("b.commission fee,b.number,b.money,b.order_sn orderSn,b.create_time transactionTime,")
                .append("a.create_time createTime,a.deal_with_time dealWithTime,b.pay_mode payMode, e.name coinName,")
                .append("b.status orderStatus,a.is_success isSuccess,b.advertise_type advertiseType,a.status,a.remark ");

        StringBuilder countHead = new StringBuilder("select count(*) ") ;

        StringBuilder endSql = new StringBuilder("from appeal a,otc_order b,member c,member d,otc_coin e")
                .append(" where a.order_id = b.id and a.initiator_id = c.id and a.associate_id = d.id ")
                .append(" and b.coin_id = e.id and b.status != 0 ");

        if(!StringUtils.isEmpty(screen.getNegotiant())) {
            endSql.append(" and (b.customer_name like '%"+screen.getNegotiant()+"%'")
                  .append(" or b.customer_real_name like '%"+screen.getNegotiant()+"%')");
        }
        if(!StringUtils.isEmpty(screen.getComplainant())) {
            endSql.append(" and (b.member_name like '%"+screen.getComplainant()+"%'")
                    .append(" or b.member_real_name like '%"+screen.getComplainant()+"%')");
        }

        if (screen.getAdvertiseType() != null) {
            endSql.append(" and b.advertise_type = "+screen.getAdvertiseType().getOrdinal()+" ");
        }

        if(screen.getSuccess() != null) {
            endSql.append(" and (a.is_success = " + screen.getSuccess().getOrdinal() + " and a.deal_with_time is not null) ");
        }else{
            if(screen.getAuditing()){
                endSql.append(" and a.is_success is null ");
            }
        }

        if(!StringUtils.isEmpty(screen.getUnit())) {
            endSql.append(" and lower(e.unit) = '"+screen.getUnit().toLowerCase()+"'");
        }

        if(screen.getStatus()!=null&&screen.getStatus().getOrdinal()!=0) {
            endSql.append(" and b.status = "+screen.getStatus().getOrdinal());
        }

        Page page = appealService.createNativePageQuery(countHead.append(endSql),headSqlBuilder.append(endSql),pageModel, Transformers.ALIAS_TO_ENTITY_MAP);

        return success("获取成功",page);
    }

    @RequiresPermissions("otc:appeal:detail")
    @PostMapping("detail")
    @AccessLog(module = AdminModule.OTC, operation = "后台申诉Appeal详情")
    public MessageResult detail(
            @RequestParam(value = "id") Long id) {
        AppealVO one = appealService.findOneAppealVO(id);
        if (one == null) {
            return error("Data is empty!You should check parameter (id)!");
        }
        return success(one);
    }

    //查询断言
    private List<BooleanExpression> getBooleanExpressionList(AppealStatus status, OrderStatus orderStatus) {
        List<BooleanExpression> booleanExpressionList = new ArrayList();
        QAppeal qAppeal = QAppeal.appeal;
        if (status != null) {
            booleanExpressionList.add(qAppeal.status.eq(status));
        }
        if (orderStatus != null) {
            booleanExpressionList.add(qAppeal.order.status.eq(orderStatus));
        }
        return booleanExpressionList;
    }

    /**
     * 申诉已处理  取消订单
     *
     * @param orderSn
     * @return
     * @throws InformationExpiredException
     */
    @RequiresPermissions("otc:appeal:cancel-order")
    @RequestMapping(value = "cancel-order")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult cancelOrder(long appealId, String orderSn, @RequestParam(value = "banned", defaultValue = "false") boolean banned) throws InformationExpiredException {
        Appeal appeal = appealService.findOne(appealId);
        Assert.notNull(appeal, "申诉单不存在");
        Long initiatorId = appeal.getInitiatorId();
        Long associateId = appeal.getAssociateId();
        Order order = orderService.findOneByOrderSn(orderSn);
        notNull(order, msService.getMessage("ORDER_NOT_EXISTS"));
        int ret = getRet(order, initiatorId, associateId);
        isTrue(ret != 0, msService.getMessage("REQUEST_ILLEGAL"));
        isTrue(order.getStatus().equals(OrderStatus.NONPAYMENT) || order.getStatus().equals(OrderStatus.PAID) || order.getStatus().equals(OrderStatus.APPEAL), msService.getMessage("ORDER_NOT_ALLOW_CANCEL"));
        //取消订单
        if (!(orderService.cancelOrder(order.getOrderSn()) > 0)) {
            throw new InformationExpiredException("Information Expired");
        }
        MessageResult result = success("");
        if (ret == 1) {
            //banned为true 禁用账户
            Member member1 = memberService.findOne(initiatorId);
            if (member1.getStatus() == CommonStatus.NORMAL && banned) {
                member1.setStatus(CommonStatus.ILLEGAL);
                memberService.save(member1);
            }

            result = cancel(order, order.getNumber(), associateId);
        } else if (ret == 2) {
            Member member1 = memberService.findOne(initiatorId);
            if (member1.getStatus() == CommonStatus.NORMAL && banned) {
                member1.setStatus(CommonStatus.ILLEGAL);
                memberService.save(member1);
            }
            result = cancel(order, add(order.getNumber(), order.getCommission()), associateId);
        } else if (ret == 3) {
            Member member1 = memberService.findOne(associateId);
            if (member1.getStatus() == CommonStatus.NORMAL && banned) {
                member1.setStatus(CommonStatus.ILLEGAL);
                memberService.save(member1);
            }
            result = cancel(order, add(order.getNumber(), order.getCommission()), initiatorId);
        } else if (ret == 4) {
            Member member1 = memberService.findOne(associateId);
            if (member1.getStatus() == CommonStatus.NORMAL && banned) {
                member1.setStatus(CommonStatus.ILLEGAL);
                memberService.save(member1);
            }
            result = cancel(order, order.getNumber(), initiatorId);
        } else {
            throw new InformationExpiredException("Information Expired");
        }
        appeal.setDealWithTime(DateUtil.getCurrentDate());
        appeal.setIsSuccess(BooleanEnum.IS_FALSE);
        appeal.setStatus(AppealStatus.PROCESSED);
        appealService.save(appeal);
        return result;
    }


    private MessageResult cancel(Order order , BigDecimal amount , Long memberId)  throws InformationExpiredException{
        MemberWallet memberWallet  ;
        //更改广告
        if (!advertiseService.updateAdvertiseAmountForCancel(order.getAdvertiseId(), amount)) {
            throw new InformationExpiredException("Information Expired");
        }
        memberWallet = memberWalletService.findByOtcCoinAndMemberId(order.getCoin(), memberId);
        log.info("======memberWallet===="+memberWallet.toString());
        MessageResult result = memberWalletService.thawBalance(memberWallet,amount);
        if (result.getCode() == 0) {
            return MessageResult.success("取消订单成功");
        } else {
            throw new InformationExpiredException("Information Expired");
        }
    }

    private int getRet(Order order, Long initiatorId, Long associateId) {
        int ret = 0;
        if (order.getAdvertiseType().equals(AdvertiseType.BUY) && order.getMemberId().equals(initiatorId)) {
            //代表该申诉者是广告发布者，并且是付款者   卖家associateId
            ret = 1;
        } else if (order.getAdvertiseType().equals(AdvertiseType.SELL) && order.getCustomerId().equals(initiatorId)) {
            //代表该申诉者不是广告发布者，但是是付款者   卖家associateId
            ret = 2;
        } else if (order.getAdvertiseType().equals(AdvertiseType.SELL) && order.getCustomerId().equals(associateId)) {
            //代表该申诉者是广告发布者，但不是付款者   卖家initiatorId
            ret = 3;
        } else if (order.getAdvertiseType().equals(AdvertiseType.BUY) && order.getMemberId().equals(associateId)) {
            //代表该申诉者不是广告发布者，但不是付款者  卖家initiatorId
            ret = 4;
        }
        return ret;
    }


    /**
     * 申诉处理 订单放行（放币）
     *
     * @param orderSn
     * @return
     */
    @RequiresPermissions("otc:appeal:release-coin")
    @RequestMapping(value = "release-coin")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult confirmRelease(long appealId, String orderSn, @RequestParam(value = "banned", defaultValue = "false") boolean banned) throws Exception {
        Appeal appeal = appealService.findOne(appealId);
        Assert.notNull(appeal, "申诉单不存在");
        Long initiatorId = appeal.getInitiatorId();
        Long associateId = appeal.getAssociateId();
        // Assert.hasText(jyPassword, msService.getMessage("MISSING_JYPASSWORD"));
        Member member = memberService.findOne(initiatorId);
       /* String mbPassword = member.getJyPassword();
        Assert.hasText(mbPassword, msService.getMessage("NO_SET_JYPASSWORD"));
        Assert.isTrue(Md5.md5Digest(jyPassword + member.getSalt()).toLowerCase().equals(mbPassword), msService.getMessage("ERROR_JYPASSWORD"));*/
        Order order = orderService.findOneByOrderSn(orderSn);
        notNull(order, msService.getMessage("ORDER_NOT_EXISTS"));
        int ret = getRet(order, initiatorId, associateId);
        isTrue(ret != 0, msService.getMessage("REQUEST_ILLEGAL"));
        isTrue(order.getStatus().equals(OrderStatus.PAID) || order.getStatus().equals(OrderStatus.APPEAL), msService.getMessage("ORDER_STATUS_EXPIRED"));
        if (ret == 1 || ret == 4) {
            //更改广告
            if (!advertiseService.updateAdvertiseAmountForRelease(order.getAdvertiseId(), order.getNumber())) {
                throw new InformationExpiredException("Information Expired");
            }
        } else if ((ret == 2 || ret == 3)) {
            //更改广告
            if (!advertiseService.updateAdvertiseAmountForRelease(order.getAdvertiseId(), add(order.getNumber(), order.getCommission()))) {
                throw new InformationExpiredException("Information Expired");
            }
        } else {
            throw new InformationExpiredException("Information Expired");
        }
        //放行订单
        if (!(orderService.releaseOrder(order.getOrderSn()) > 0)) {
            throw new InformationExpiredException("Information Expired");
        }
        //后台处理申诉结果为放行---更改买卖双方钱包
        memberWalletService.transferAdmin(order, ret);

        if (ret == 1) {

            generateMemberTransaction(order, TransactionType.OTC_SELL, associateId, BigDecimal.ZERO);

            generateMemberTransaction(order, TransactionType.OTC_BUY, initiatorId, order.getCommission());

        } else if (ret == 2) {

            generateMemberTransaction(order, TransactionType.OTC_SELL, associateId, order.getCommission());

            generateMemberTransaction(order, TransactionType.OTC_BUY, initiatorId, BigDecimal.ZERO);

        } else if (ret == 3) {

            generateMemberTransaction(order, TransactionType.OTC_BUY, associateId, BigDecimal.ZERO);

            generateMemberTransaction(order, TransactionType.OTC_SELL, initiatorId, order.getCommission());
        } else {

            generateMemberTransaction(order, TransactionType.OTC_BUY, associateId, order.getCommission());

            generateMemberTransaction(order, TransactionType.OTC_SELL, initiatorId, BigDecimal.ZERO);
        }
        orderEvent.onOrderCompleted(order);

        //banned为true 禁用账户
        if (ret == 1 || ret == 2) {
            Member member1 = memberService.findOne(associateId);
            if (member1.getStatus() == CommonStatus.NORMAL && banned) {
                member1.setStatus(CommonStatus.ILLEGAL);
                memberService.save(member1);
            }

        } else {
            Member member1 = memberService.findOne(initiatorId);
            if (member1.getStatus() == CommonStatus.NORMAL && banned) {
                member1.setStatus(CommonStatus.ILLEGAL);
                memberService.save(member1);
            }
        }
        appeal.setDealWithTime(DateUtil.getCurrentDate());
        appeal.setIsSuccess(BooleanEnum.IS_TRUE);
        appeal.setStatus(AppealStatus.PROCESSED);
        appealService.save(appeal);
        return MessageResult.success(msService.getMessage("SUCCESS"));
    }


    private void generateMemberTransaction(Order order, TransactionType type, long memberId, BigDecimal fee) {

        MemberTransaction memberTransaction = new MemberTransaction();
        memberTransaction.setSymbol(order.getCoin().getUnit());
        memberTransaction.setType(type);
        memberTransaction.setFee(fee);
        memberTransaction.setMemberId(memberId);
        memberTransaction.setAmount(order.getNumber());
        memberTransaction.setDiscountFee("0");
        memberTransaction.setRealFee(fee+"");
        memberTransaction.setCreateTime(new Date());
        memberTransaction = memberTransactionService.save(memberTransaction);

    }
}
