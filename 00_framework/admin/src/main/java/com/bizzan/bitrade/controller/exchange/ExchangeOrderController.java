package com.bizzan.bitrade.controller.exchange;

import com.alibaba.fastjson.JSON;
import com.bizzan.bitrade.annotation.AccessLog;
import com.bizzan.bitrade.constant.AdminModule;
import com.bizzan.bitrade.constant.BooleanEnum;
import com.bizzan.bitrade.constant.PageModel;
import com.bizzan.bitrade.controller.common.BaseAdminController;
import com.bizzan.bitrade.entity.*;
import com.bizzan.bitrade.model.screen.ExchangeOrderScreen;
import com.bizzan.bitrade.model.screen.ExchangeTradeScreen;
import com.bizzan.bitrade.service.ExchangeOrderService;
import com.bizzan.bitrade.service.LocaleMessageSourceService;
import com.bizzan.bitrade.util.FileUtil;
import com.bizzan.bitrade.util.MessageResult;
import com.bizzan.bitrade.util.PredicateUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Shaoxianjun
 * @description
 * @date 2019/1/31 10:52
 */
@RestController
@RequestMapping("exchange/exchange-order")
public class ExchangeOrderController extends BaseAdminController {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private ExchangeOrderService exchangeOrderService;
    @Autowired
    private LocaleMessageSourceService messageSource;

    @RequiresPermissions("exchange:exchange-order:all")
    @PostMapping("all")
    @AccessLog(module = AdminModule.EXCHANGE, operation = "查找所有exchangeOrder")
    public MessageResult all() {
        List<ExchangeOrder> exchangeOrderList = exchangeOrderService.findAll();
        if (exchangeOrderList != null && exchangeOrderList.size() > 0) {
            return success(exchangeOrderList);
        }
        return error(messageSource.getMessage("NO_DATA"));
    }

    @RequiresPermissions("exchange:exchange-order:detail")
    @PostMapping("detail")
    @AccessLog(module = AdminModule.EXCHANGE, operation = "exchangeOrder详情")
    public MessageResult detail(String id) {
        List<ExchangeOrderDetail> one = exchangeOrderService.getAggregation(id);
        if (one == null) {
            return error(messageSource.getMessage("NO_DATA"));
        }
        return success(one);
    }

    @RequiresPermissions("exchange:exchange-order:page-query")
    @PostMapping("page-query")
    @AccessLog(module = AdminModule.EXCHANGE, operation = "分页查找exchangeOrder")
    public MessageResult page(
            PageModel pageModel,
            ExchangeOrderScreen screen) {
        if (pageModel.getDirection() == null && pageModel.getProperty() == null) {
            ArrayList<Sort.Direction> directions = new ArrayList<>();
            directions.add(Sort.Direction.DESC);
            pageModel.setDirection(directions);
            List<String> property = new ArrayList<>();
            property.add("time");
            pageModel.setProperty(property);
        }
        //获取查询条件
        Predicate predicate = getPredicate(screen);
        Page<ExchangeOrder> all = exchangeOrderService.findAll(predicate, pageModel.getPageable());
        return success(all);
    }

    private Predicate getPredicate(ExchangeOrderScreen screen) {
        ArrayList<BooleanExpression> booleanExpressions = new ArrayList<>();
        QExchangeOrder qExchangeOrder = QExchangeOrder.exchangeOrder;
        if (screen.getOrderDirection() != null) {
            booleanExpressions.add(qExchangeOrder.direction.eq(screen.getOrderDirection()));
        }
        if (StringUtils.isNotEmpty(screen.getOrderId())) {
            booleanExpressions.add(qExchangeOrder.orderId.eq(screen.getOrderId()));
        }
        if (screen.getMemberId() != null) {
            booleanExpressions.add(qExchangeOrder.memberId.eq(screen.getMemberId()));
        }
        if (screen.getType() != null) {
            booleanExpressions.add(qExchangeOrder.type.eq(screen.getType()));
        }
        if (StringUtils.isNotBlank(screen.getCoinSymbol())) {
            booleanExpressions.add(qExchangeOrder.coinSymbol.equalsIgnoreCase(screen.getCoinSymbol()));
        }
        if (StringUtils.isNotBlank(screen.getBaseSymbol())) {
            booleanExpressions.add(qExchangeOrder.baseSymbol.equalsIgnoreCase(screen.getBaseSymbol()));
        }
        if (screen.getStatus() != null) {
            booleanExpressions.add(qExchangeOrder.status.eq(screen.getStatus()));
        }
        if (screen.getMinPrice()!=null) {
            booleanExpressions.add(qExchangeOrder.price.goe(screen.getMinPrice()));
        }
        if (screen.getMaxPrice()!=null) {
            booleanExpressions.add(qExchangeOrder.price.loe(screen.getMaxPrice()));
        }
        if (screen.getMinTradeAmount()!=null) {
            booleanExpressions.add(qExchangeOrder.tradedAmount.goe(screen.getMinTradeAmount()));
        }
        if (screen.getMaxTradeAmount()!=null) {
            booleanExpressions.add(qExchangeOrder.tradedAmount.loe(screen.getMaxTradeAmount()));
        }
        if (screen.getMinTurnOver()!=null) {
            booleanExpressions.add(qExchangeOrder.turnover.goe(screen.getMinTurnOver()));
        }
        if (screen.getMaxTurnOver()!=null) {
            booleanExpressions.add(qExchangeOrder.turnover.loe(screen.getMaxTurnOver()));
        }
        if (screen.getRobotOrder()!=null&&screen.getRobotOrder() == 1){
            //不看机器人（不包含机器人）
            booleanExpressions.add(qExchangeOrder.memberId.notIn(1, 2, 10001));
//            booleanExpressions.add(qExchangeOrder.memberId.notIn(69296 , 52350));
        }
        if (screen.getRobotOrder()!=null&&screen.getRobotOrder() == 0){
            //查看机器人
            booleanExpressions.add(qExchangeOrder.memberId.in(1, 2, 10001));
//            booleanExpressions.add(qExchangeOrder.memberId.in(69296 , 52350));

        }
        if(screen.getCompleted()!=null)
            /**
             * 委托订单
             */ {
            if(screen.getCompleted()== BooleanEnum.IS_FALSE){
                booleanExpressions.add(qExchangeOrder.completedTime.isNull().and(qExchangeOrder.canceledTime.isNull())
                        .and(qExchangeOrder.status.eq(ExchangeOrderStatus.TRADING)));
            }else{
                /**
                 * 历史订单
                 */
                booleanExpressions.add(qExchangeOrder.completedTime.isNotNull().or(qExchangeOrder.canceledTime.isNotNull())
                .or(qExchangeOrder.status.ne(ExchangeOrderStatus.TRADING)));
            }
        }
        return PredicateUtils.getPredicate(booleanExpressions);
    }

    @RequiresPermissions("exchange:exchange-order:entrust-details")
    @PostMapping("entrust-details")
    public MessageResult entrustDetails(ExchangeTradeScreen screen,PageModel pageModel){
       /* ExchangeOrder
        StringBuilder headSql = new StringBuilder("select orderId as IF(a.direction=0,buyOrderId,sellOrderId)");

        StringBuilder headCount = new StringBuilder("select count(*) ");*/
        return  null ;
    }


    @RequiresPermissions("exchange:exchange-order:out-excel")
    @GetMapping("out-excel")
    @AccessLog(module = AdminModule.EXCHANGE, operation = "导出 exchangeOrder Excel")
    public MessageResult outExcel(
            @RequestParam(value = "memberId") Long memberId,
            @RequestParam(value = "type") ExchangeOrderType type,
            @RequestParam(value = "symbol") String symbol,
            @RequestParam(value = "status") ExchangeOrderStatus status,
            @RequestParam(value = "direction") ExchangeOrderDirection direction,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        //获取查询条件
        List<Predicate> predicates = getPredicates(memberId, type, symbol, status, direction);
        List list = exchangeOrderService.queryWhereOrPage(predicates, null, null).getContent();
        return new FileUtil().exportExcel(request, response, list, "order");
    }

    //查询条件的获取
    public List<Predicate> getPredicates(Long memberId, ExchangeOrderType type, String symbol, ExchangeOrderStatus status, ExchangeOrderDirection direction) {
        ArrayList<Predicate> predicates = new ArrayList<>();
        QExchangeOrder qExchangeOrder = QExchangeOrder.exchangeOrder;
        //predicates.add(qExchangeOrder.symbol.eq(QExchangeCoin.exchangeCoin.symbol));
        if (memberId != null) {
            predicates.add(qExchangeOrder.memberId.eq(memberId));
        }
        if (type != null) {
            predicates.add(qExchangeOrder.type.eq(type));
        }
        if (symbol != null) {
            predicates.add(qExchangeOrder.symbol.eq(symbol));
        }
        if (status != null) {
            predicates.add(qExchangeOrder.status.eq(status));
        }
        if (direction != null) {
            predicates.add(qExchangeOrder.direction.eq(direction));
        }
        return predicates;
    }

    @RequiresPermissions("exchange:exchange-order:cancel")
    @PostMapping("cancel")
    @AccessLog(module = AdminModule.EXCHANGE, operation = "取消委托")
    public MessageResult cancelOrder(String orderId) {
        ExchangeOrder order = exchangeOrderService.findOne(orderId);
        if (order.getStatus() != ExchangeOrderStatus.TRADING) {
            return MessageResult.error(500, "order not in trading");
        }
        // 发送消息至Exchange系统
        kafkaTemplate.send("exchange-order-cancel",JSON.toJSONString(order));
        return MessageResult.success(messageSource.getMessage("SUCCESS"));
    }
}
