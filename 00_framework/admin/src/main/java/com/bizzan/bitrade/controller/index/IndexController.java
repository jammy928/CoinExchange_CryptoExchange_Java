package com.bizzan.bitrade.controller.index;


import com.bizzan.bitrade.annotation.AccessLog;
import com.bizzan.bitrade.constant.AdminModule;
import com.bizzan.bitrade.constant.TransactionTypeEnum;
import com.bizzan.bitrade.entity.BusinessAuthApply;
import com.bizzan.bitrade.entity.ExchangeCoin;
import com.bizzan.bitrade.entity.MemberApplication;
import com.bizzan.bitrade.entity.MemberLog;
import com.bizzan.bitrade.service.*;
import com.bizzan.bitrade.util.DateUtil;
import com.bizzan.bitrade.util.MessageResult;
import com.bizzan.bitrade.vo.ExchangeTurnoverStatisticsVO;
import com.bizzan.bitrade.vo.TurnoverStatisticsVO;
import com.fasterxml.jackson.annotation.JsonFormat;

import org.apache.shiro.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@RestController("index")
@RequestMapping("index/statistics")
public class IndexController {

    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    private ExchangeCoinService exchangeCoinService ;

    @Autowired
    private MongoTemplate mongoTemplate ;

    @Autowired
    private MemberApplicationService memberApplicationService ;

    @Autowired
    private BusinessAuthApplyService businessAuthApplyService ;

    @Autowired
    private BusinessCancelApplyService businessCancelApplyService ;

    @Autowired
    private AppealService appealService ;

    @Autowired
    private WithdrawRecordService withdrawRecordService ;

    @PostMapping("member-statistics-info")
    @AccessLog(module = AdminModule.INDEX,operation = "首页会员信息统计")
    public MessageResult getYestodayStatisticsInfo(
            @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")Date startDate,
            @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")Date endDate){

        if(endDate == null){
            endDate = DateUtil.getDate(new Date(),1);
        }

        ProjectionOperation projectionOperation = Aggregation.project("date","registrationNum","applicationNum","bussinessNum");

        List<Criteria> criterias = new ArrayList<>();

        criterias.add(Criteria.where("date").lte(endDate));

        if(startDate!=null) {
            criterias.add(Criteria.where("date").gte(startDate));
        }

        MatchOperation matchOperation = Aggregation.match(
                Criteria.where("date").ne(null).andOperator(
                        criterias.toArray(new Criteria[criterias.size()])
                )
        );
        GroupOperation groupOperation = Aggregation.group().sum("registrationNum").as("registrationNum")
                .sum("applicationNum").as("applicationNum")
                .sum("bussinessNum").as("bussinessNum");

        Aggregation aggregation = Aggregation.newAggregation(projectionOperation, matchOperation,groupOperation);

        AggregationResults<Map> aggregationResults = this.mongoTemplate.aggregate(aggregation, "member_log", Map.class);

        List<Map> list = aggregationResults.getMappedResults() ;

        Query query = new Query(Criteria.where("date").is(DateUtil.getDate(new Date(),1)));

        List<MemberLog> list1 = mongoTemplate.find(query,MemberLog.class) ;
        MemberLog log = list1==null||list1.size()<1?new MemberLog():list1.get(0);

        Map map = list.get(0) ;
        map.put("yesterdayRegistrationNum",log.getRegistrationNum());
        map.put("yesterdayApplicationNum",log.getApplicationNum());
        map.put("yesterdayBussinessNum",log.getBussinessNum());
        map.remove("_id");
        return MessageResult.getSuccessInstance("",list);

    }

    /**
     *
     * @param startDate
     * @param endDate
     * @return
     */
    @PostMapping("member-statistics-chart")
    @AccessLog(module = AdminModule.INDEX,operation = "首页会员信息统计图")
    public MessageResult getMemberStatisticsChart(
            @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")Date startDate,
            @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")Date endDate
    ){

        Assert.notNull(startDate,"startDate must not be null");

        Assert.notNull(endDate,"endDate must not be null");

        List<Criteria> criterias = new ArrayList<>();

        criterias.add(Criteria.where("date").gte(startDate));

        criterias.add(Criteria.where("date").lte(endDate));

        Query query = new Query(Criteria.where("date").ne(null).andOperator(criterias.toArray(new Criteria[criterias.size()])));

        List<Map> list = mongoTemplate.find(query, Map.class,"member_log");

        for(Map map:list){
            map.remove("_id");
            map.remove("year");
            map.remove("month");
            map.remove("day");
        }

        return MessageResult.getSuccessInstance("",list);
    }

    @PostMapping("otc-statistics-turnover")
    @AccessLog(module = AdminModule.INDEX,operation = "法币成交信息统计")
    public MessageResult otcStatistics(
            @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")Date startDate,
            @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")Date endDate,
            String unit
    ){

        Assert.notNull(unit,"unit must not be null ......");

        if(endDate == null){
            endDate = DateUtil.getDate(new Date(),1);
        }

        ProjectionOperation projectionOperation = Aggregation.project("date","type","unit","amount","fee");

        List<Criteria> criterias = new ArrayList<>();

        criterias.add(Criteria.where("date").lte(endDate));

        if(startDate!=null) {
            criterias.add(Criteria.where("date").gte(startDate));
        }

        criterias.add(Criteria.where("unit").is(unit));

        MatchOperation matchOperation = Aggregation.match(
                Criteria.where("type").in(TransactionTypeEnum.OTC_NUM.toString()
                        ,TransactionTypeEnum.OTC_MONEY.toString()).andOperator(
                        criterias.toArray(new Criteria[criterias.size()])
                )
        );

        GroupOperation groupOperation = Aggregation.group("unit","type")
                .sum("amount").as("amount")
                .sum("fee").as("fee");

        Aggregation aggregation = Aggregation.newAggregation(projectionOperation, matchOperation,groupOperation);

        AggregationResults<Map> aggregationResults = this.mongoTemplate.aggregate(aggregation, "turnover_statistics", Map.class);

        List<Map> list = aggregationResults.getMappedResults() ;

        Map<String,Object> result = getResults(unit ,list,TransactionTypeEnum.OTC_NUM.toString(),TransactionTypeEnum.OTC_MONEY.toString());

        List<Map> yesterdayList = yerterdayQuery(unit,TransactionTypeEnum.OTC_NUM) ;

        boolean flag = yesterdayList!=null && yesterdayList.size()>0 ;

        result.put("yesterdayAmount",flag && yesterdayList.get(0).get("amount")!=null ?new BigDecimal(yesterdayList.get(0).get("amount").toString()):0);

        result.put("yesterdayFee",flag && yesterdayList.get(0).get("fee")!=null ?new BigDecimal(yesterdayList.get(0).get("fee").toString()):0);

        return MessageResult.getSuccessInstance("",result);
    }

    /**
     * 币币成交量/成交额/手续费 总计
     * @param startDate
     * @param endDate
     * @param unit
     *
     * #root.method.name ==  exchangeStatistics (方法名)
     *
     * condition : 满足条件的进行缓存 （针对方法参数的条件判断） 为true 就缓存
     *
     * unless ：满足条件的进行缓存 （针对结果集的条件判断） 为true就不缓存
     * @return
     */
    @PostMapping("exchange-statistics-turnover")
    @AccessLog(module = AdminModule.INDEX,operation = "首页币币成交量/成交额/手续费 总计")
    public MessageResult exchangeStatistics(
            @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")Date startDate,
            @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")Date endDate,
            String unit
    ) {
        Assert.notNull(unit, "unit must not be null ......");

        if (endDate == null) {
            endDate = DateUtil.getDate(new Date(), 1);
        }

        ProjectionOperation projectionOperation = Aggregation.project("date", "type", "unit", "amount", "fee");

        List<Criteria> criterias = new ArrayList<>();

        criterias.add(Criteria.where("date").lte(endDate));

        if (startDate != null) {
            criterias.add(Criteria.where("date").gte(startDate));
        }


        criterias.add(Criteria.where("unit").is(unit));

        MatchOperation matchOperation = Aggregation.match(
                Criteria.where("type").is(TransactionTypeEnum.EXCHANGE_COIN.toString()).andOperator(
                        criterias.toArray(new Criteria[criterias.size()])
                )
        );

        GroupOperation groupOperation = Aggregation.group("unit","type")
                .sum("amount").as("amount");

        Aggregation aggregation = Aggregation.newAggregation(projectionOperation, matchOperation, groupOperation);

        AggregationResults<Map> aggregationResults = this.mongoTemplate.aggregate(aggregation, "turnover_statistics", Map.class);

        List<Map> list = aggregationResults.getMappedResults();

        matchOperation = Aggregation.match(
                Criteria.where("type").is(TransactionTypeEnum.EXCHANGE.toString()).andOperator(
                        criterias.toArray(new Criteria[criterias.size()])));

        groupOperation = Aggregation.group("unit", "type").sum("fee").as("fee");

        aggregation = Aggregation.newAggregation(projectionOperation, matchOperation, groupOperation);

        aggregationResults = this.mongoTemplate.aggregate(aggregation, "turnover_statistics", Map.class);

        List<Map> list1 = aggregationResults.getMappedResults();

        List<Map> yesterdayAmounts = yerterdayQuery(unit,TransactionTypeEnum.EXCHANGE_COIN);

        List<Map> yesterdayFees = yerterdayQuery(unit,TransactionTypeEnum.EXCHANGE);

        Map map = new HashMap();

        boolean flag = list != null && list.size() > 0 && list.get(0).get("amount") !=null;

        boolean flag2 = yesterdayAmounts != null && yesterdayAmounts.size()>0 && yesterdayAmounts.get(0).get("amount")!=null;

        boolean flag3 = yesterdayFees != null && yesterdayFees.size()>0 && yesterdayFees.get(0).get("fee")!=null;

        boolean flag4 = list1 != null && list1.size() > 0 && list1.get(0).get("fee")!=null ;

        map.put("amount", flag ? new BigDecimal(list.get(0).get("amount").toString()) : 0);

        map.put("type", TransactionTypeEnum.EXCHANGE.toString());

        map.put("unit", unit);

        map.put("yesterdayAmount",flag2  ? new BigDecimal(yesterdayAmounts.get(0).get("amount").toString()) : 0);

        map.put("yesterdayFee",flag3 ? new BigDecimal(yesterdayFees.get(0).get("fee").toString()) : 0);

        if (list1 != null && list1.size() > 0) {

            map.put("fee",flag4 ? new BigDecimal(list1.get(0).get("fee").toString()) : 0);

        }

        return MessageResult.getSuccessInstance("", map);
    }


    /**
     * 处理结果集 （将法币/币币  的 成交量/成交额/手续费/币种 信息 聚合到一个map中）
     * @param list
     * @return
     */
    private Map<String,Object> getResults(String unit ,List<Map> list,String typeNum,String typeMoney) {

        Map<String,Object> map0 = new HashMap<>();
        map0.put("unit",unit);
        for(Map map:list){
            logger.info("法币原始成交信息:{}",map);
            if(map.get("type").toString().equals(typeNum)){
                map0.put("amount",map.get("amount")!=null?new BigDecimal(map.get("amount").toString()):0);
                map0.put("fee",map.get("fee")!=null?new BigDecimal(map.get("fee").toString()):0);
            }else if(map.get("type").toString().equals(typeMoney)){
                map0.put("money",map.get("amount")!=null?new BigDecimal(map.get("amount").toString()):0);
            }
        }
        map0.put("type","OTC");
        logger.info("法币成交信息:{}",map0);
        return map0 ;
    }

    /**
     * 法币成交量统计图
     * @param startDate
     * @param endDate
     * @param units  币种集合
     * @return
     */
    @PostMapping("/otc-statistics-num-chart")
    @AccessLog(module = AdminModule.INDEX,operation = "法币成交量统计图")
    public MessageResult otcNumChart(
            @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")Date startDate,
            @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")Date endDate,
            String[] units/*,
            TransactionTypeEnum type*/){

        //Assert.isTrue(type==TransactionTypeEnum.OTC_NUM || type==TransactionTypeEnum.OTC_MONEY ,"此接口为法币统计，type只能为 0（成交量） 或 1（成交额）");

        Assert.notNull(startDate,"startDate must not be null ......");

        Assert.notEmpty(units,"units must not be null");

        if(endDate == null){
            endDate = DateUtil.getDate(new Date(),1);
        }

        //Assert.notNull(type,"type must not be null");

        ProjectionOperation projectionOperation = Aggregation.project("date","type","unit", "amount");

        List<Criteria> criterias = new ArrayList<>();

        criterias.add(Criteria.where("date").gte(startDate)) ;

        criterias.add(Criteria.where("date").lte(endDate));

        criterias.add(Criteria.where("unit").in(units));

        MatchOperation matchOperation = Aggregation.match(
                Criteria.where("type").is(TransactionTypeEnum.OTC_NUM.toString())
                        .andOperator(criterias.toArray(new Criteria[criterias.size()]))

        ) ;

        Aggregation aggregation = Aggregation.newAggregation(projectionOperation, matchOperation);

        AggregationResults<TurnoverStatisticsVO> aggregationResults = this.mongoTemplate.aggregate(aggregation, "turnover_statistics", TurnoverStatisticsVO.class);

        List<TurnoverStatisticsVO> list = aggregationResults.getMappedResults() ;

        list = list.stream().sorted((x,y)->{
            if(x.getDate().after(y.getDate())){
                return -1 ;
            }else{
                return 1 ;
            }
        }).collect(Collectors.toList());

        logger.info("法币成交量统计图:{}",list);

        return MessageResult.getSuccessInstance("",list);
    }

    /**
     * 币币成交 统计图 （按照交易对区分）
     *
     * 首页按照交易对统计总成交量/成交额
     *
     * @param startDate
     * @param endDate
     * @param baseSymbol    交易区币种（合法币/支付币)
     * @param coinSymbols   平台币种
     * @return
     */
    @PostMapping("exchange-statistics-turnover-chart")
    @AccessLog(module = AdminModule.INDEX,operation = "币币成交 统计图 （按照交易对区分）")
    public MessageResult exchangeNumStatistics(
            @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")Date startDate,
            @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")Date endDate,
            String baseSymbol,
            String[] coinSymbols){

        Assert.notNull(startDate,"startDate must not be null ......");

        Assert.notNull(baseSymbol,"baseSymbol must not be null");

        if(coinSymbols==null||coinSymbols.length<1){
            List<String> list0 = exchangeCoinService.getCoinSymbol(baseSymbol) ;
            coinSymbols = list0==null?null:list0.toArray(new String[list0.size()]) ;
        }

        if(endDate == null){
            endDate = DateUtil.getDate(new Date(),1);
        }

        ProjectionOperation projectionOperation = Aggregation.project("date","baseSymbol","coinSymbol","amount","money").andExclude("_id");

        List<Criteria> criterias = new ArrayList<>();

        criterias.add(Criteria.where("date").gte(startDate));

        criterias.add(Criteria.where("date").lte(endDate));

        criterias.add(Criteria.where("coinSymbol").in(coinSymbols));

        MatchOperation matchOperation = Aggregation.match(
                Criteria.where("baseSymbol").is(baseSymbol)
                        .andOperator(criterias.toArray(new Criteria[criterias.size()]))
        );

        Aggregation aggregation = Aggregation.newAggregation(projectionOperation, matchOperation);

        AggregationResults<ExchangeTurnoverStatisticsVO> aggregationResults = this.mongoTemplate.aggregate(aggregation, "exchange_turnover_statistics", ExchangeTurnoverStatisticsVO.class);

        List<ExchangeTurnoverStatisticsVO> list = aggregationResults.getMappedResults() ;

        logger.info("币币成交 统计图:{}",list);

        return MessageResult.getSuccessInstance("",list) ;
    }

    /**
     * 待办事项
     * @return
     */
    @GetMapping("affairs")
    public MessageResult affairs(){
        //实名审核
        long applicationNum = memberApplicationService.countAuditing();
        //商家审核
        long businessAuthNum = businessAuthApplyService.countAuditing();
        //订单申诉
        long appealNum = appealService.countAuditing();
        //退保审核
        long businessCancelNum = businessCancelApplyService.countAuditing();
        //提币审核
        long withdrawRecordNum = withdrawRecordService.countAuditing();
        Map<String,Object> map = new HashMap<>();
        map.put("applicationNum",applicationNum);
        map.put("businessAuthNum",businessAuthNum);
        map.put("appealNum",appealNum);
        map.put("businessCancelNum",businessCancelNum);
        map.put("withdrawRecordNum",withdrawRecordNum);
        return MessageResult.getSuccessInstance("",map);
    }



    private List<Map> yerterdayQuery(String unit,TransactionTypeEnum type){

        Query query = new Query(Criteria.where("date").is(DateUtil.getDate(new Date(), 1))
                .and("type").is(type.toString())
                .and("unit").is(unit));

        List<Map> list = mongoTemplate.find(query,Map.class,"turnover_statistics");
        return list ;
    }


    @PostMapping("all-exchange-coin")
    public MessageResult getAllExchangeCoin(){
        List<String> list = exchangeCoinService.getAllCoin();
        return MessageResult.getSuccessInstance("",list);
    }
}
