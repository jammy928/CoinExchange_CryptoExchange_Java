package com.bizzan.bitrade.controller.finance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bizzan.bitrade.annotation.AccessLog;
import com.bizzan.bitrade.constant.AdminModule;
import com.bizzan.bitrade.constant.PageModel;
import com.bizzan.bitrade.controller.BaseController;
import com.bizzan.bitrade.model.screen.ExchangeTradeScreen;
import com.bizzan.bitrade.service.ExchangeOrderService;
import com.bizzan.bitrade.util.MessageResult;

import javax.annotation.Resource;

/**
 * 币币交易明细
 */

@RestController
@RequestMapping("finance/exchange-transaction")
public class ExchangeTransactionController extends BaseController {

    @Resource(name = "newMongoTemplate")
    private MongoTemplate mongoTemplate;

    @Autowired
    private ExchangeOrderService exchangeOrderService;

    @PostMapping("page-query")
    @AccessLog(module = AdminModule.FINANCE, operation = "币币交易明细")
    public MessageResult page(PageModel pageModel, ExchangeTradeScreen screen) {
       /* ExchangeOrder
        StringBuilder headSql = new StringBuilder("select a.symbol symbol,a.price price,a.tradeAmount amount,a.turnover money , ")
                .append("FROM_UNIXTIME(a.completedTime/1000, '%Y-%m-%d %H:%i:%S') as completedTime,a.orderId buyOrderId,")
                .append("b.username buyUsername ")
                .append("b.level promotionLevel ") ;
        StringBuilder endSql = new StringBuilder(" from member a join member_promotion b on a.inviter_id = b.inviter_id and a.inviter_id = "+memberId);
        StringBuilder countHead = new StringBuilder("select count(*) ") ;*/
        return null;
    }
}
