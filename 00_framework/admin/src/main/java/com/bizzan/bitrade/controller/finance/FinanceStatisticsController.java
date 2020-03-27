package com.bizzan.bitrade.controller.finance;

import com.bizzan.bitrade.annotation.AccessLog;
import com.bizzan.bitrade.constant.AdminModule;
import com.bizzan.bitrade.constant.TransactionTypeEnum;
import com.bizzan.bitrade.service.LocaleMessageSourceService;
import com.bizzan.bitrade.util.DateUtil;
import com.bizzan.bitrade.util.MessageResult;
import com.bizzan.bitrade.vo.TurnoverStatisticsVO;
import com.fasterxml.jackson.annotation.JsonFormat;

import org.apache.shiro.util.Assert;
import org.bson.types.Decimal128;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;

@RestController
@RequestMapping("finance/statistics")
public class FinanceStatisticsController {

    private static final Logger logger = LoggerFactory.getLogger(FinanceStatisticsController.class);

    @Resource(name = "newMongoTemplate")
    private MongoTemplate mongoTemplate;
    @Autowired
    private LocaleMessageSourceService messageSource;

    /**
     * 法币/币币  总成交量/总成交额   区间:[startDate,endDate]
     * <p>
     * 此处仅按照币种区分 ，币币不按照交易对区分
     *
     * @param types
     * @param startDate
     * @param endDate
     * @param unit
     * @return
     */
    @PostMapping("turnover-all")
    @AccessLog(module = AdminModule.FINANCE, operation = "法币/币币  总成交量/总成交额")
    public MessageResult getResult(
            String[] types,
            @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd") Date startDate,
            @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd") Date endDate,
            String unit) {

        Assert.notNull(types, "type must not be null ");
        if (endDate == null) {
            endDate = DateUtil.getDate(new Date(), 1);
        }

        ProjectionOperation projectionOperation = Aggregation.project("date", "type", "unit", "amount").andExclude("_id");

        List<Criteria> criterias = new ArrayList<>();

        if (startDate != null) {
            criterias.add(Criteria.where("date").gte(startDate));
        }

        criterias.add(Criteria.where("date").lte(endDate));

        if (!StringUtils.isEmpty(unit)) {
            criterias.add(Criteria.where("unit").is(unit));
        }

        MatchOperation matchOperation = Aggregation.match(
                Criteria.where("type").in(types)
                        .andOperator(criterias.toArray(new Criteria[criterias.size()]))

        );

        GroupOperation groupOperation = Aggregation.group("type", "unit").sum("amount").as("amount");

        Aggregation aggregation = Aggregation.newAggregation(projectionOperation, matchOperation, groupOperation);

        AggregationResults<Map> aggregationResults = this.mongoTemplate.aggregate(aggregation, "turnover_statistics", Map.class);

        List<Map> list = aggregationResults.getMappedResults();

        Set<String> units = new HashSet<>();

        for (Map map : list) {
            /* map.put("date",date);*/
            map.put("amount", ((Decimal128) map.get("amount")).bigDecimalValue());

            units.add(map.get("unit").toString());

            logger.info("成交信息:{}", map);
        }

        return MessageResult.getSuccessInstance(messageSource.getMessage("SUCCESS"), list);
    }

    /**
     * 手续费统计 总计
     *
     * @param type      ["OTC_NUM","WITHDRAW","EXCHANGE"]
     * @param startDate
     * @param endDate
     * @param unit
     * @return
     */
    @PostMapping("fee")
    @AccessLog(module = AdminModule.FINANCE, operation = "手续费统计 总计[\"OTC_NUM\",\"WITHDRAW\",\"EXCHANGE\"]")
    public MessageResult getFee(TransactionTypeEnum type
            , @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd") Date startDate
            , @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd") Date endDate
            , String unit) {

        Assert.notNull(type, "type must not be null ");

        Assert.isTrue(type == TransactionTypeEnum.OTC_NUM ||
                type == TransactionTypeEnum.EXCHANGE ||
                type == TransactionTypeEnum.WITHDRAW, "此接口为法币统计，type只能为 0（法币） 或 2（币币）或 6（提币）手续费");

        if (endDate == null) {
            endDate = DateUtil.getDate(new Date(), 1);
        }

        Assert.notNull(startDate, "startDate must not be null ");

        ProjectionOperation projectionOperation = Aggregation.project("date", "type", "unit", "fee").andExclude("_id");

        List<Criteria> criterias = new ArrayList<>();

        criterias.add(Criteria.where("date").gte(startDate));

        criterias.add(Criteria.where("date").lte(endDate));

        if (!StringUtils.isEmpty(unit)) {
            criterias.add(Criteria.where("unit").is(unit));
        }

        MatchOperation matchOperation = Aggregation.match(
                Criteria.where("type").is(type.toString())
                        .andOperator(criterias.toArray(new Criteria[criterias.size()]))

        );

        GroupOperation groupOperation = Aggregation.group("type", "unit").sum("fee").as("fee");

        Aggregation aggregation = Aggregation.newAggregation(projectionOperation, matchOperation, groupOperation);

        AggregationResults<Map> aggregationResults = this.mongoTemplate.aggregate(aggregation, "turnover_statistics", Map.class);

        List<Map> list = aggregationResults.getMappedResults();

        for (Map map : list) {
            map.put("fee", ((Decimal128) map.get("fee")).bigDecimalValue());
            logger.info("手续费信息:{}", map);
        }

        return MessageResult.getSuccessInstance(messageSource.getMessage("SUCCESS"), list);

    }


    /**
     * 充币/提币总量统计
     *
     * @param type ["WITHDRAW" "RECHARGE"]
     * @return
     */
    @PostMapping("recharge-or-withdraw-amount")
    @AccessLog(module = AdminModule.FINANCE, operation = "充币/提币总量统计")
    public MessageResult recharge(TransactionTypeEnum type
            , @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd") Date startDate
            , @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd") Date endDate
    ) {

        Assert.isTrue(type == TransactionTypeEnum.WITHDRAW || type == TransactionTypeEnum.RECHARGE, "type只能为 5（充币） 或 6（提币）");

        if (endDate == null) {
            endDate = DateUtil.getDate(new Date(), 1);
        }

        Assert.notNull(startDate, "startDate must not be null");

        ProjectionOperation projectionOperation = Aggregation.project("date", "year", "month", "day", "type", "unit", "amount", "fee").andExclude("_id");

        MatchOperation matchOperation = Aggregation.match(
                Criteria.where("type").is(type.toString())
                        .andOperator(Criteria.where("date").gte(startDate), Criteria.where("date").lte(endDate))
        );

        GroupOperation groupOperation = Aggregation.group("type", "unit").sum("amount").as("amount").sum("fee").as("fee");

        Aggregation aggregation = Aggregation.newAggregation(projectionOperation, matchOperation, groupOperation);

        AggregationResults<TurnoverStatisticsVO> aggregationResults = this.mongoTemplate.aggregate(aggregation, "turnover_statistics", TurnoverStatisticsVO.class);

        List<TurnoverStatisticsVO> list = aggregationResults.getMappedResults();

        logger.info("{}总额:{}", type == TransactionTypeEnum.WITHDRAW ? "提币" : "充币", list);

        return MessageResult.getSuccessInstance(messageSource.getMessage("SUCCESS"), list);
    }


}
