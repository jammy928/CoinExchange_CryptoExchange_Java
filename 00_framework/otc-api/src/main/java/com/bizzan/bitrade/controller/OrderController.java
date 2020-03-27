package com.bizzan.bitrade.controller;

import com.bizzan.bitrade.coin.CoinExchangeFactory;
import com.bizzan.bitrade.constant.*;
import com.bizzan.bitrade.entity.*;
import com.bizzan.bitrade.entity.chat.ChatMessageRecord;
import com.bizzan.bitrade.entity.transform.AuthMember;
import com.bizzan.bitrade.es.ESUtils;
import com.bizzan.bitrade.event.OrderEvent;
import com.bizzan.bitrade.exception.InformationExpiredException;
import com.bizzan.bitrade.pagination.PageResult;
import com.bizzan.bitrade.service.*;
import com.bizzan.bitrade.util.BindingResultUtil;
import com.bizzan.bitrade.util.DateUtil;
import com.bizzan.bitrade.util.Md5;
import com.bizzan.bitrade.util.MessageResult;
import com.bizzan.bitrade.vendor.provider.SMSProvider;
import com.querydsl.core.types.dsl.BooleanExpression;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.*;

import static com.bizzan.bitrade.constant.BooleanEnum.IS_FALSE;
import static com.bizzan.bitrade.constant.BooleanEnum.IS_TRUE;
import static com.bizzan.bitrade.constant.PayMode.*;
import static com.bizzan.bitrade.constant.SysConstant.SESSION_MEMBER;
import static com.bizzan.bitrade.util.BigDecimalUtils.*;
import static org.springframework.util.Assert.isTrue;
import static org.springframework.util.Assert.notNull;

/**
 * @author GS
 * @date 2017年12月11日
 */
@RestController
@RequestMapping(value = "/order", method = RequestMethod.POST)
@Slf4j
public class OrderController {

   /* private static Logger logger = LoggerFactory.getLogger(OrderController.class);*/

    @Autowired
    private OrderService orderService;

    @Autowired
    private AdvertiseService advertiseService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberWalletService memberWalletService;

    @Autowired
    private CoinExchangeFactory coins;

    @Autowired
    private OrderEvent orderEvent;

    @Autowired
    private AppealService appealService;

    @Autowired
    private LocaleMessageSourceService msService;

    @Autowired
    private OrderDetailAggregationService orderDetailAggregationService;

    @Autowired
    private MemberTransactionService memberTransactionService;


    @Value("${spark.system.order.sms:1}")
    private int notice;

    @Autowired
    private SMSProvider smsProvider;

    @Autowired
    private MongoTemplate mongoTemplate ;
    @Autowired
    private ESUtils esUtils;


    /**
     * 买入，卖出详细信息
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "pre", method = RequestMethod.POST)
    @Transactional(rollbackFor = Exception.class)
    public MessageResult preOrderInfo(long id) {
        Advertise advertise = advertiseService.findOne(id);
        notNull(advertise, msService.getMessage("PARAMETER_ERROR"));
        isTrue(advertise.getStatus().equals(AdvertiseControlStatus.PUT_ON_SHELVES), msService.getMessage("PARAMETER_ERROR"));
        Member member = advertise.getMember();
        OtcCoin otcCoin = advertise.getCoin();
        PreOrderInfo preOrderInfo = PreOrderInfo.builder()
                .advertiseType(advertise.getAdvertiseType())
                .country(advertise.getCountry().getZhName())
                .emailVerified(member.getEmail() == null ? IS_FALSE : IS_TRUE)
                .idCardVerified(member.getIdNumber() == null ? IS_FALSE : IS_TRUE)
                .maxLimit(advertise.getMaxLimit())
                .minLimit(advertise.getMinLimit())
                .number(advertise.getRemainAmount())
                .otcCoinId(otcCoin.getId())
                .payMode(advertise.getPayMode())
                .phoneVerified(member.getMobilePhone() == null ? IS_FALSE : IS_TRUE)
                .timeLimit(advertise.getTimeLimit())
                .transactions(member.getTransactions())
                .unit(otcCoin.getUnit())
                .username(member.getUsername())
                .remark(advertise.getRemark())
                .build();
        //处理可交易的最大数量
        if (advertise.getAdvertiseType().equals(AdvertiseType.SELL)){
            BigDecimal maxTransactions=divDown(advertise.getRemainAmount(),add(BigDecimal.ONE,getRate(otcCoin.getJyRate())));
            preOrderInfo.setMaxTradableAmount(maxTransactions);
        }else {
            preOrderInfo.setMaxTradableAmount(advertise.getRemainAmount());
        }
        if (advertise.getPriceType().equals(PriceType.REGULAR)) {
            preOrderInfo.setPrice(advertise.getPrice());
        } else {
            BigDecimal marketPrice = coins.get(otcCoin.getUnit());
            preOrderInfo.setPrice(mulRound(marketPrice, rate(advertise.getPremiseRate()), 2));
        }
        MessageResult result = MessageResult.success();
        result.setData(preOrderInfo);
        return result;
    }

    /**
     * 买币
     *
     * @param id
     * @param coinId
     * @param price
     * @param money
     * @param amount
     * @param remark
     * @param user
     * @return
     * @throws InformationExpiredException
     */
    @RequestMapping(value = "buy", method = RequestMethod.POST)
    @Transactional(rollbackFor = Exception.class)
    public MessageResult buy(long id, long coinId, BigDecimal price, BigDecimal money,
                             BigDecimal amount, String remark,
                             @RequestParam(value = "mode", defaultValue = "0") Integer mode,
                             @SessionAttribute(SESSION_MEMBER) AuthMember user) throws InformationExpiredException {
        Advertise advertise = advertiseService.findOne(id);
        if (advertise == null || !advertise.getAdvertiseType().equals(AdvertiseType.SELL)) {
            return MessageResult.error(msService.getMessage("PARAMETER_ERROR"));
        }
        isTrue(!user.getName().equals(advertise.getMember().getUsername()), msService.getMessage("NOT_ALLOW_BUY_BY_SELF"));
        isTrue(advertise.getStatus().equals(AdvertiseControlStatus.PUT_ON_SHELVES), msService.getMessage("ALREADY_PUT_OFF"));
        OtcCoin otcCoin = advertise.getCoin();
        if (otcCoin.getId() != coinId) {
            return MessageResult.error(msService.getMessage("PARAMETER_ERROR"));
        }
        if (advertise.getPriceType().equals(PriceType.REGULAR)) {
            isTrue(isEqual(price, advertise.getPrice()), msService.getMessage("PRICE_EXPIRED"));
        } else {
            BigDecimal marketPrice = coins.get(otcCoin.getUnit());
            isTrue(isEqual(price, mulRound(rate(advertise.getPremiseRate()), marketPrice, 2)), msService.getMessage("PRICE_EXPIRED"));
        }
        if (mode == 0) {
            isTrue(isEqual(div(money, price), amount), msService.getMessage("NUMBER_ERROR"));
        } else {
            isTrue(isEqual(mulRound(amount, price,2), money), msService.getMessage("NUMBER_ERROR"));
        }
        isTrue(compare(money, advertise.getMinLimit()), msService.getMessage("MONEY_MIN") + advertise.getMinLimit().toString() + " CNY");
        isTrue(compare(advertise.getMaxLimit(), money), msService.getMessage("MONEY_MAX") + advertise.getMaxLimit().toString() + " CNY");
        String[] pay = advertise.getPayMode().split(",");
        //计算手续费
        //if(advertise.getMember().getCertifiedBusinessStatus()==)
        BigDecimal commission = mulRound(amount, getRate(otcCoin.getJyRate()));

        //认证商家法币交易免手续费
        Member member = memberService.findOne(user.getId());
        log.info("会员等级************************************:{},********,{}",member.getCertifiedBusinessStatus(),member.getMemberLevel());
        if(member.getCertifiedBusinessStatus().equals(CertifiedBusinessStatus.VERIFIED)
                && member.getMemberLevel().equals(MemberLevelEnum.IDENTIFICATION)) {
            commission = BigDecimal.ZERO ;
        }
        isTrue(compare(advertise.getRemainAmount(), amount), msService.getMessage("AMOUNT_NOT_ENOUGH"));
        Order order = new Order();
        order.setStatus(OrderStatus.NONPAYMENT);
        order.setAdvertiseId(advertise.getId());
        order.setAdvertiseType(advertise.getAdvertiseType());
        order.setCoin(otcCoin);
        order.setCommission(commission);
        order.setCountry(advertise.getCountry().getZhName());
        order.setCustomerId(user.getId());
        order.setCustomerName(user.getName());
        order.setCustomerRealName(member.getRealName());
        order.setMemberId(advertise.getMember().getId());
        order.setMemberName(advertise.getMember().getUsername());
        order.setMemberRealName(advertise.getMember().getRealName());
        order.setMaxLimit(advertise.getMaxLimit());
        order.setMinLimit(advertise.getMinLimit());
        order.setMoney(money);
        order.setNumber(sub(amount,commission));
        order.setPayMode(advertise.getPayMode());
        order.setPrice(price);
        order.setRemark(remark);
        order.setTimeLimit(advertise.getTimeLimit());
        Arrays.stream(pay).forEach(x -> {
            if (ALI.getCnName().equals(x)) {
                order.setAlipay(advertise.getMember().getAlipay());
            } else if (WECHAT.getCnName().equals(x)) {
                order.setWechatPay(advertise.getMember().getWechatPay());
            } else if (BANK.getCnName().equals(x)) {
                order.setBankInfo(advertise.getMember().getBankInfo());
            }
        });
        if (!advertiseService.updateAdvertiseAmountForBuy(advertise.getId(), amount)) {
            throw new InformationExpiredException("Information Expired");
        }
        Order order1 = orderService.saveOrder(order);
        if (order1 != null) {
            if (notice==1){
                try {
                    smsProvider.sendMessageByTempId(advertise.getMember().getMobilePhone(), advertise.getCoin().getUnit()+"##"+user.getName(),"9499");
                } catch (Exception e) {
                    log.error("sms 发送失败");
                    e.printStackTrace();
                }
            }
            /**
             * 下单后，将自动回复记录添加到mongodb
             */
           if(advertise.getAuto()==BooleanEnum.IS_TRUE){
               ChatMessageRecord chatMessageRecord = new ChatMessageRecord();
               chatMessageRecord.setOrderId(order1.getOrderSn());
               chatMessageRecord.setUidFrom(order1.getMemberId().toString());
               chatMessageRecord.setUidTo(order1.getCustomerId().toString());
               chatMessageRecord.setNameFrom(order1.getMemberName());
               chatMessageRecord.setNameTo(order1.getCustomerName());
               chatMessageRecord.setContent(advertise.getAutoword());
               chatMessageRecord.setSendTime(Calendar.getInstance().getTimeInMillis());
               chatMessageRecord.setSendTimeStr(DateUtil.getDateTime());
               //自动回复消息保存到mogondb
               mongoTemplate.insert(chatMessageRecord,"chat_message");
           }
            MessageResult result = MessageResult.success(msService.getMessage("CREATE_ORDER_SUCCESS"));
            result.setData(order1.getOrderSn().toString());
            return result;
        } else {
            throw new InformationExpiredException("Information Expired");
        }
    }

    /**
     * 卖币
     *
     * @param id
     * @param coinId
     * @param price
     * @param money
     * @param amount
     * @param remark
     * @param user
     * @return
     * @throws InformationExpiredException
     */
    @RequestMapping(value = "sell")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult sell(long id, long coinId, BigDecimal price, BigDecimal money,
                              BigDecimal amount, String remark,
                              @RequestParam(value = "mode", defaultValue = "0") Integer mode,
                              @SessionAttribute(SESSION_MEMBER) AuthMember user) throws InformationExpiredException {
        Advertise advertise = advertiseService.findOne(id);
        if (advertise == null || !advertise.getAdvertiseType().equals(AdvertiseType.BUY)) {
            return MessageResult.error(msService.getMessage("PARAMETER_ERROR"));
        }
        isTrue(!user.getName().equals(advertise.getMember().getUsername()), msService.getMessage("NOT_ALLOW_SELL_BY_SELF"));
        isTrue(advertise.getStatus().equals(AdvertiseControlStatus.PUT_ON_SHELVES), msService.getMessage("ALREADY_PUT_OFF"));
        OtcCoin otcCoin = advertise.getCoin();
        if (otcCoin.getId() != coinId) {
            return MessageResult.error(msService.getMessage("PARAMETER_ERROR"));
        }
        if (advertise.getPriceType().equals(PriceType.REGULAR)) {
            isTrue(isEqual(price, advertise.getPrice()), msService.getMessage("PRICE_EXPIRED"));
        } else {
            BigDecimal marketPrice = coins.get(otcCoin.getUnit());
            isTrue(isEqual(price, mulRound(rate(advertise.getPremiseRate()), marketPrice, 2)), msService.getMessage("PRICE_EXPIRED"));
        }
        if (mode == 0) {
            isTrue(isEqual(div(money, price), amount), msService.getMessage("NUMBER_ERROR"));
        } else {
            isTrue(isEqual(mulRound(amount, price,2), money), msService.getMessage("NUMBER_ERROR"));
        }
        isTrue(compare(money, advertise.getMinLimit()), msService.getMessage("MONEY_MIN") + advertise.getMinLimit().toString() + " CNY");
        isTrue(compare(advertise.getMaxLimit(), money), msService.getMessage("MONEY_MAX") + advertise.getMaxLimit().toString() + " CNY");
        //计算手续费
        BigDecimal commission = mulRound(amount, getRate(otcCoin.getJyRate()));
        log.info("会员等级************************************:{},********,{}",advertise.getMember().getCertifiedBusinessStatus(),advertise.getMember().getMemberLevel());
        if(advertise.getMember().getCertifiedBusinessStatus()==CertifiedBusinessStatus.VERIFIED
                && advertise.getMember().getMemberLevel()==MemberLevelEnum.IDENTIFICATION) {
            commission = BigDecimal.ZERO ;
        }

        isTrue(compare(advertise.getRemainAmount(), amount), msService.getMessage("AMOUNT_NOT_ENOUGH"));
        MemberWallet wallet = memberWalletService.findByOtcCoinAndMemberId(otcCoin, user.getId());
        isTrue(compare(wallet.getBalance(), amount), msService.getMessage("INSUFFICIENT_BALANCE"));
        Member member = memberService.findOne(user.getId());
        Order order = new Order();
        order.setStatus(OrderStatus.NONPAYMENT);
        order.setAdvertiseId(advertise.getId());
        order.setAdvertiseType(advertise.getAdvertiseType());
        order.setCoin(otcCoin);
        order.setCommission(commission);
        order.setCountry(advertise.getCountry().getZhName());
        order.setCustomerId(user.getId());
        order.setCustomerName(user.getName());
        order.setCustomerRealName(member.getRealName());
        order.setMemberId(advertise.getMember().getId());
        order.setMemberName(advertise.getMember().getUsername());
        order.setMemberRealName(advertise.getMember().getRealName());
        order.setMaxLimit(advertise.getMaxLimit());
        order.setMinLimit(advertise.getMinLimit());
        order.setMoney(money);
        order.setNumber(amount);
        order.setPayMode(advertise.getPayMode());
        order.setPrice(price);
        order.setRemark(remark);
        order.setTimeLimit(advertise.getTimeLimit());
        String[] pay = advertise.getPayMode().split(",");
        MessageResult result = MessageResult.error(msService.getMessage("CREATE_ORDER_SUCCESS"));
        Arrays.stream(pay).forEach(x -> {
            if (ALI.getCnName().equals(x)) {
                if (member.getAlipay() != null) {
                    result.setCode(0);
                    order.setAlipay(member.getAlipay());
                }
            } else if (WECHAT.getCnName().equals(x)) {
                if (member.getWechatPay() != null) {
                    result.setCode(0);
                    order.setWechatPay(member.getWechatPay());
                }
            } else if (BANK.getCnName().equals(x)) {
                if (member.getBankInfo() != null) {
                    result.setCode(0);
                    order.setBankInfo(member.getBankInfo());
                }
            }
        });
        isTrue(result.getCode() == 0, msService.getMessage("AT_LEAST_SUPPORT_PAY"));
        if (!advertiseService.updateAdvertiseAmountForBuy(advertise.getId(), amount)) {
            throw new InformationExpiredException("Information Expired");
        }
        if (!(memberWalletService.freezeBalance(wallet, amount).getCode() == 0)) {
            throw new InformationExpiredException("Information Expired");
        }
        Order order1 = orderService.saveOrder(order);
        if (order1 != null) {
            if (notice==1){
                try {
                    smsProvider.sendMessageByTempId(advertise.getMember().getMobilePhone(), advertise.getCoin().getUnit()+"##"+user.getName(),"9499");
                } catch (Exception e) {
                    log.error("sms 发送失败");
                    e.printStackTrace();
                }
            }
            result.setData(order1.getOrderSn().toString());
            return result;
        } else {
            throw new InformationExpiredException("Information Expired");
        }
    }


    /**
     * 我的订单
     *
     * @param user
     * @param status
     * @param pageNo
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "self")
    public MessageResult myOrder(@SessionAttribute(SESSION_MEMBER) AuthMember user, OrderStatus status, int pageNo, int pageSize, String orderSn) {
        Page<Order> page = orderService.pageQuery(pageNo, pageSize, status, user.getId(), orderSn);
        List<Long> memberIdList=new ArrayList<>();
        page.forEach(order->{
            if(!memberIdList.contains(order.getMemberId())){
                memberIdList.add(order.getMemberId());
            }
            if(!memberIdList.contains(order.getCustomerId())){
                memberIdList.add(order.getCustomerId());
            }
        });
        List<BooleanExpression> booleanExpressionList = new ArrayList();
        booleanExpressionList.add(QMember.member.id.in(memberIdList));
        PageResult<Member> memberPage= memberService.queryWhereOrPage(booleanExpressionList,null,null);
        Page<ScanOrder> scanOrders = page.map(x -> ScanOrder.toScanOrder(x, user.getId()));
        for(ScanOrder scanOrder:scanOrders){
            for(Member member:memberPage.getContent()){
                if(scanOrder.getMemberId().equals(member.getId())){
                    scanOrder.setAvatar(member.getAvatar());
                }
            }
        }
        MessageResult result = MessageResult.success();
        result.setData(scanOrders);
        return result;
    }

    /**
     * 订单详情
     *
     * @param orderSn
     * @param user
     * @return
     */
    @RequestMapping(value = "detail")
    public MessageResult queryOrder(String orderSn, @SessionAttribute(SESSION_MEMBER) AuthMember user) {
        Order order = orderService.findOneByOrderSn(orderSn);
        notNull(order, msService.getMessage("ORDER_NOT_EXISTS"));
        MessageResult result = MessageResult.success();
        Member member = memberService.findOne(order.getMemberId());
        OrderDetail info = OrderDetail.builder().orderSn(orderSn)
                .unit(order.getCoin().getUnit())
                .status(order.getStatus())
                .amount(order.getNumber())
                .price(order.getPrice())
                .money(order.getMoney())
                .payTime(order.getPayTime())
                .createTime(order.getCreateTime())
                .timeLimit(order.getTimeLimit())
                .myId(user.getId()).memberMobile(member.getMobilePhone())
                .build();
        /*if (!order.getStatus().equals(OrderStatus.CANCELLED)) {*/
            PayInfo payInfo = PayInfo.builder()
                    .bankInfo(order.getBankInfo())
                    .alipay(order.getAlipay())
                    .wechatPay(order.getWechatPay())
                    .build();
            info.setPayInfo(payInfo);
       /* }*/
        if (order.getMemberId().equals(user.getId())) {
            info.setHisId(order.getCustomerId());
            info.setOtherSide(order.getCustomerName());
            info.setCommission(order.getCommission());
            Member memberCustomer = memberService.findOne(order.getCustomerId());
            info.setMemberMobile(memberCustomer.getMobilePhone());
            if (order.getAdvertiseType().equals(AdvertiseType.BUY)) {
                info.setType(AdvertiseType.BUY);
                if (info.getPayInfo() != null) {
                    info.getPayInfo().setRealName(order.getCustomerRealName());
                }
            } else {
                info.setType(AdvertiseType.SELL);
                if (info.getPayInfo() != null) {
                    info.getPayInfo().setRealName(order.getMemberRealName());
                }
            }
        } else if (order.getCustomerId().equals(user.getId())) {
            info.setHisId(order.getMemberId());
            info.setOtherSide(order.getMemberName());
            info.setCommission(BigDecimal.ZERO);
            Member memberOrder = memberService.findOne(order.getMemberId());
            info.setMemberMobile(memberOrder.getMobilePhone());
            if (order.getAdvertiseType().equals(AdvertiseType.BUY)) {
                if (info.getPayInfo() != null) {
                    info.getPayInfo().setRealName(order.getCustomerRealName());
                }
                info.setType(AdvertiseType.SELL);
            } else {
                if (info.getPayInfo() != null) {
                    info.getPayInfo().setRealName(order.getMemberRealName());
                }
                info.setType(AdvertiseType.BUY);
            }
        } else {
            return MessageResult.error(msService.getMessage("ORDER_NOT_EXISTS"));
        }
        result.setData(info);
        return result;
    }

    /**
     * 取消订单
     *
     * @param orderSn
     * @param user
     * @return
     */
    @RequestMapping(value = "cancel")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult cancelOrder(String orderSn, @SessionAttribute(SESSION_MEMBER) AuthMember user) throws InformationExpiredException {
        Order order = orderService.findOneByOrderSn(orderSn);
        notNull(order, msService.getMessage("ORDER_NOT_EXISTS"));
        int ret = 0;
        if (order.getAdvertiseType().equals(AdvertiseType.BUY) && order.getMemberId().equals(user.getId())) {
            //代表该会员是广告发布者，购买类型的广告，并且是付款者
            ret = 1;
        } else if (order.getAdvertiseType().equals(AdvertiseType.SELL) && order.getCustomerId().equals(user.getId())) {
            //代表该会员不是广告发布者，并且是付款者
            ret = 2;
        }
        isTrue(ret != 0, msService.getMessage("REQUEST_ILLEGAL"));
        isTrue(order.getStatus().equals(OrderStatus.NONPAYMENT) || order.getStatus().equals(OrderStatus.PAID), msService.getMessage("ORDER_NOT_ALLOW_CANCEL"));
        MemberWallet memberWallet;
        //取消订单
        if (!(orderService.cancelOrder(order.getOrderSn()) > 0)) {
            throw new InformationExpiredException("Information Expired");
        }
        if (ret == 1) {
            //更改广告
            //创建订单的时候减少了realAmount，增加了dealAmount，撤销时只减少了dealAmount的金额，没有增加realAmount的金额
            if (!advertiseService.updateAdvertiseAmountForCancel(order.getAdvertiseId(), order.getNumber())) {
                throw new InformationExpiredException("Information Expired");
            }
            memberWallet = memberWalletService.findByOtcCoinAndMemberId(order.getCoin(), order.getCustomerId());
            MessageResult result = memberWalletService.thawBalance(memberWallet, order.getNumber());
            if (result.getCode() == 0) {
                return MessageResult.success(msService.getMessage("CANCEL_SUCCESS"));
            } else {
                throw new InformationExpiredException("Information Expired");
            }
        } else {
            //更改广告
            if (!advertiseService.updateAdvertiseAmountForCancel(order.getAdvertiseId(), add(order.getNumber(), order.getCommission()))) {
                throw new InformationExpiredException("Information Expired");
            }
            memberWallet = memberWalletService.findByOtcCoinAndMemberId(order.getCoin(), order.getMemberId());
            MessageResult result = memberWalletService.thawBalance(memberWallet, add(order.getNumber(), order.getCommission()));
            if (result.getCode() == 0) {
                return MessageResult.success(msService.getMessage("CANCEL_SUCCESS"));
            } else {
                throw new InformationExpiredException("Information Expired");
            }
        }
    }

    /**
     * 确认付款
     *
     * @param orderSn
     * @param user
     * @return
     */
    @RequestMapping(value = "pay")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult payOrder(String orderSn, @SessionAttribute(SESSION_MEMBER) AuthMember user) throws InformationExpiredException {
        Order order = orderService.findOneByOrderSn(orderSn);
        notNull(order, msService.getMessage("ORDER_NOT_EXISTS"));
        int ret = 0;
        if (order.getAdvertiseType().equals(AdvertiseType.BUY) && order.getMemberId().equals(user.getId())) {
            //代表该会员是广告发布者，并且是付款者
            ret = 1;
        } else if (order.getAdvertiseType().equals(AdvertiseType.SELL) && order.getCustomerId().equals(user.getId())) {
            //代表该会员不是广告发布者
            ret = 2;
        }
        isTrue(ret != 0, msService.getMessage("REQUEST_ILLEGAL"));
        isTrue(order.getStatus().equals(OrderStatus.NONPAYMENT), msService.getMessage("ORDER_STATUS_EXPIRED"));
        isTrue(compare(new BigDecimal(order.getTimeLimit()), DateUtil.diffMinute(order.getCreateTime())), msService.getMessage("ORDER_ALREADY_AUTO_CANCEL"));
        int is = orderService.payForOrder(orderSn);
        if (is > 0) {
            /**
             * 聚合otc订单手续费等明细存入mongodb
             */
            OrderDetailAggregation aggregation = new OrderDetailAggregation();
            BeanUtils.copyProperties(order,aggregation);
            aggregation.setUnit(order.getCoin().getUnit());
            aggregation.setOrderId(order.getOrderSn());
            aggregation.setFee(order.getCommission().doubleValue());
            aggregation.setAmount(order.getNumber().doubleValue());
            aggregation.setType(OrderTypeEnum.OTC);
            aggregation.setTime(Calendar.getInstance().getTimeInMillis());
            orderDetailAggregationService.save(aggregation);

            MessageResult result = MessageResult.success(msService.getMessage("PAY_SUCCESS"));
            result.setData(order);
            return result;
        } else {
            throw new InformationExpiredException("Information Expired");
        }

    }

    /**
     * 订单放行
     *
     * @param orderSn
     * @param user
     * @return
     */
    @RequestMapping(value = "release")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult confirmRelease(String orderSn, String jyPassword, @SessionAttribute(SESSION_MEMBER) AuthMember user) throws Exception {
        Assert.hasText(jyPassword, msService.getMessage("MISSING_JYPASSWORD"));
        Member member = memberService.findOne(user.getId());
        String mbPassword = member.getJyPassword();
        Assert.hasText(mbPassword, msService.getMessage("NO_SET_JYPASSWORD"));
        Assert.isTrue(Md5.md5Digest(jyPassword + member.getSalt()).toLowerCase().equals(mbPassword), msService.getMessage("ERROR_JYPASSWORD"));
        Order order = orderService.findOneByOrderSn(orderSn);
        notNull(order, msService.getMessage("ORDER_NOT_EXISTS"));
        int ret = 0;
        if (order.getAdvertiseType().equals(AdvertiseType.BUY) && order.getCustomerId().equals(user.getId())) {
            //代表该会员不是广告发布者，并且是放行者
            ret = 1;
        } else if (order.getAdvertiseType().equals(AdvertiseType.SELL) && order.getMemberId().equals(user.getId())) {
            //代表该会员是广告发布者，并且是放行者
            ret = 2;
        }
        isTrue(ret != 0, msService.getMessage("REQUEST_ILLEGAL"));
        isTrue(order.getStatus().equals(OrderStatus.PAID), msService.getMessage("ORDER_STATUS_EXPIRED"));
        if (ret == 1) {
            //更改广告
            if (!advertiseService.updateAdvertiseAmountForRelease(order.getAdvertiseId(), order.getNumber())) {
                throw new InformationExpiredException("Information Expired");
            }
        } else {
            //更改广告
            if (!advertiseService.updateAdvertiseAmountForRelease(order.getAdvertiseId(), add(order.getNumber(), order.getCommission()))) {
                throw new InformationExpiredException("Information Expired");
            }
        }
        //放行订单
        if (!(orderService.releaseOrder(order.getOrderSn()) > 0)) {
            throw new InformationExpiredException("Information Expired");
        }
        //更改钱包
        memberWalletService.transfer(order, ret);
        MemberTransaction memberTransaction = new MemberTransaction();
        MemberTransaction memberTransaction1 = new MemberTransaction();
        if (ret==1){
            memberTransaction.setSymbol(order.getCoin().getUnit());
            memberTransaction.setType(TransactionType.OTC_SELL);
            memberTransaction.setFee(BigDecimal.ZERO);
            memberTransaction.setMemberId(user.getId());
            memberTransaction.setAmount(order.getNumber());
            memberTransaction.setDiscountFee("0");
            memberTransaction.setRealFee("0");
            memberTransaction.setCreateTime(new Date());
            memberTransaction = memberTransactionService.save(memberTransaction);


            memberTransaction1.setAmount(order.getNumber());
            memberTransaction1.setType(TransactionType.OTC_BUY);
            memberTransaction1.setMemberId(order.getMemberId());
            memberTransaction1.setSymbol(order.getCoin().getUnit());
            memberTransaction1.setFee(order.getCommission());
            memberTransaction1.setDiscountFee("0");
            memberTransaction1.setRealFee(order.getCommission()+"");
            memberTransaction1.setCreateTime(new Date());
            memberTransaction1 = memberTransactionService.save(memberTransaction1);
        }else{
            memberTransaction.setSymbol(order.getCoin().getUnit());
            memberTransaction.setType(TransactionType.OTC_SELL);
            memberTransaction.setFee(order.getCommission());
            memberTransaction.setMemberId(user.getId());
            memberTransaction.setAmount(order.getNumber());
            memberTransaction.setDiscountFee("0");
            memberTransaction.setRealFee(order.getCommission()+"");
            memberTransaction.setCreateTime(new Date());
            memberTransaction = memberTransactionService.save(memberTransaction);

            memberTransaction1.setAmount(order.getNumber());
            memberTransaction1.setType(TransactionType.OTC_BUY);
            memberTransaction1.setMemberId(order.getCustomerId());
            memberTransaction1.setSymbol(order.getCoin().getUnit());
            memberTransaction1.setFee(BigDecimal.ZERO);

            memberTransaction1.setDiscountFee("0");
            memberTransaction1.setRealFee(order.getCommission()+"");
            memberTransaction1.setCreateTime(new Date());
            memberTransaction1 = memberTransactionService.save(memberTransaction1);
        }
        orderEvent.onOrderCompleted(order);
        return MessageResult.success(msService.getMessage("RELEASE_SUCCESS"));
    }

    /**
     * 申诉
     *
     * @param appealApply
     * @param bindingResult
     * @param user
     * @return
     * @throws InformationExpiredException
     */
    @RequestMapping(value = "appeal")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult appeal(@Valid AppealApply appealApply, BindingResult bindingResult, @SessionAttribute(SESSION_MEMBER) AuthMember user) throws InformationExpiredException {
        MessageResult result = BindingResultUtil.validate(bindingResult);
        if (result != null) {
            return result;
        }
        Order order = orderService.findOneByOrderSn(appealApply.getOrderSn());
        int ret = 0;
        if (order.getMemberId().equals(user.getId())) {
            ret = 1;
        } else if (order.getCustomerId().equals(user.getId())) {
            ret = 2;
        }
        isTrue(ret != 0, msService.getMessage("REQUEST_ILLEGAL"));
        isTrue(order.getStatus().equals(OrderStatus.PAID), msService.getMessage("NO_APPEAL"));
        if (!(orderService.updateOrderAppeal(order.getOrderSn()) > 0)) {
            throw new InformationExpiredException("Information Expired");
        }
        Appeal appeal = new Appeal();
        appeal.setInitiatorId(user.getId());
        if (ret == 1) {
            appeal.setAssociateId(order.getCustomerId());
        } else {
            appeal.setAssociateId(order.getMemberId());
        }
        appeal.setOrder(order);
        appeal.setRemark(appealApply.getRemark());
        Appeal appeal1 = appealService.save(appeal);
        if (appeal1 != null) {
            return MessageResult.success(msService.getMessage("APPEAL_SUCCESS"));
        } else {
            throw new InformationExpiredException("Information Expired");
        }
    }
}
