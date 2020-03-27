package com.bizzan.bitrade.job;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.bizzan.bitrade.constant.TransactionTypeEnum;
import com.bizzan.bitrade.dao.*;
import com.bizzan.bitrade.entity.ExchangeTurnoverStatistics;
import com.bizzan.bitrade.entity.MemberLog;
import com.bizzan.bitrade.entity.TurnoverStatistics;
import com.bizzan.bitrade.service.OrderService;
import com.bizzan.bitrade.util.DateUtil;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;

@Component
@Slf4j
public class MemberStatisticsJob {

    private static Logger logger = LoggerFactory.getLogger(MemberStatisticsJob.class);

    @Autowired
    private MemberDao memberDao ;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ExchangeOrderRepository exchangeOrderRepository ;

    @Autowired
    private MemberDepositDao memberDepositDao ;

    @Autowired
    private MongoTemplate mongoTemplate ;

    @Autowired
    private WithdrawRecordDao withdrawRecordDao ;

    @Autowired
    private MemberLogDao memberLogDao ;

    /**
     * 会员注册/实名/认证商家 统计
     */
    @Scheduled(cron = "0 34 3 * * ?")
    public void statisticsMember() {
        try {
            if(!mongoTemplate.collectionExists("member_log")){
                List<Date> list = getDateList();
                String dateStr = "";
                for(Date date :list){
                    dateStr = DateUtil.YYYY_MM_DD.format(date) ;
                    statisticsMember(dateStr,date);
                }
            }else{
                Date date = DateUtil.dateAddDay(DateUtil.getCurrentDate(),-1);
                String dateStr = DateUtil.getFormatTime(DateUtil.YYYY_MM_DD,date);
                statisticsMember(dateStr,date);
            }
        } catch (ParseException e) {
            logger.error("日期解析异常",e);
        }

    }

    /**
     * 法币/充币/提币 手续费
     * 币币交易手续费 统计
     * 法币成交量/成交额 统计
     */
    @Scheduled(cron = "0 24 3 * * ?")
    public void turnoverStatistics() {
        try {
            if(!mongoTemplate.collectionExists("turnover_statistics")){
                List<Date> list = getDateList();
                String dateStr = "";
                for(Date date :list){
                    dateStr = DateUtil.YYYY_MM_DD.format(date) ;
                    statisticsFee(dateStr,date);
                }
            }else{
                Date date = DateUtil.dateAddDay(DateUtil.getCurrentDate(),-1);
                String dateStr = DateUtil.getFormatTime(DateUtil.YYYY_MM_DD,date);
                statisticsFee(dateStr,date);
            }

        } catch (ParseException e) {
            logger.error("日期解析异常",e);
        }

    }

    /**
     * 币币交易成交量/成交额 统计
     */
    @Scheduled(cron = "0 14 3 * * ?")
    public void exchangeStatistics(){
        try {
            if(!mongoTemplate.collectionExists("exchange_turnover_statistics")){
                List<Date> list = getDateList();
                String dateStr = "";
                for(Date date :list){
                    dateStr = DateUtil.YYYY_MM_DD.format(date) ;
                    exchangeStatistics(dateStr,date);
                }
            }else{
                Date date = DateUtil.dateAddDay(DateUtil.getCurrentDate(),-1);
                String dateStr = DateUtil.getFormatTime(DateUtil.YYYY_MM_DD,date);
                exchangeStatistics(dateStr,date);
            }

        } catch (ParseException e) {
            logger.error("日期解析异常",e);
        }
    }

    private void statisticsMember(String dateStr,Date date) throws ParseException {
        logger.info("开始统计会员信息{}",dateStr);
        int registrationNum = memberDao.getRegistrationNum(dateStr);
        int bussinessNum = memberDao.getBussinessNum(dateStr);
        int applicationNum = memberDao.getApplicationNum(dateStr);
        MemberLog memberLog = new MemberLog();
        memberLog.setApplicationNum(applicationNum);
        memberLog.setBussinessNum(bussinessNum);
        memberLog.setRegistrationNum(registrationNum);
        memberLog.setDate(DateUtil.YYYY_MM_DD.parse(dateStr));
        memberLog.setYear(DateUtil.getDatePart(date,Calendar.YEAR));
        //Calendar month 默认从0开始，方便起见 保存月份从1开始
        memberLog.setMonth(DateUtil.getDatePart(date,Calendar.MONTH)+1);
        memberLog.setDay(DateUtil.getDatePart(date,Calendar.DAY_OF_MONTH));
        logger.info("{}会员信息{}",dateStr,memberLog);
        memberLogDao.save(memberLog);
        logger.info("结束统计会员信息{}",dateStr);
    }

    private List<Date> getDateList() throws ParseException {
        List<Date> list = new ArrayList<>() ;

        Date date = memberDao.getStartRegistrationDate();
        String dateStr = DateUtil.YYYY_MM_DD.format(date) ;
        date = DateUtil.YYYY_MM_DD.parse(dateStr);

        Calendar calendar = Calendar.getInstance() ;
        calendar.setTime(date);
        Date endDate = DateUtil.dateAddDay(new Date(),-1) ;
        while(date.before(endDate)){
            list.add(date);
            calendar.add(Calendar.DAY_OF_MONTH,1);
            date = calendar.getTime() ;
        }
        return list;
    }

    private void statisticsFee(String dateStr,Date date) throws ParseException {
        /**
         * 法币成交
         *
         */
        logger.info("开始统计法币成交信息{}",dateStr);
        List<Object[]> list1 = orderService.getOtcOrderStatistics(dateStr);
        TurnoverStatistics turnoverStatistics = new TurnoverStatistics();
        turnoverStatistics.setDate(DateUtil.YYYY_MM_DD.parse(dateStr));
        turnoverStatistics.setYear(DateUtil.getDatePart(date,Calendar.YEAR));
        //Calendar month 默认从0开始，方便起见 保存月份从1开始
        turnoverStatistics.setMonth(DateUtil.getDatePart(date,Calendar.MONTH)+1);
        turnoverStatistics.setDay(DateUtil.getDatePart(date,Calendar.DAY_OF_MONTH));
        for(Object[] objects:list1){
            /**
             * 法币成交量/手续费
             */
            turnoverStatistics.setUnit(objects[0].toString());
            turnoverStatistics.setAmount((BigDecimal) objects[2]);
            turnoverStatistics.setFee((BigDecimal) objects[3]);
            turnoverStatistics.setType(TransactionTypeEnum.OTC_NUM);
            logger.info("{}法币成交信息{}",dateStr,turnoverStatistics);
            mongoTemplate.insert(turnoverStatistics,"turnover_statistics");

            /**
             * 法币成交额
             */
            turnoverStatistics.setAmount((BigDecimal) objects[4]);
            turnoverStatistics.setType(TransactionTypeEnum.OTC_MONEY);
            turnoverStatistics.setFee(null);
            mongoTemplate.insert(turnoverStatistics,"turnover_statistics");
        }
        logger.info("结束统计法币成交信息{}",dateStr);

        /**
         * 币币成交额
         */
        logger.info("开始统计币币成交额信息{}",dateStr);
        turnoverStatistics.setFee(null);
        List<Object[]> list2 = exchangeOrderRepository.getExchangeTurnoverBase(dateStr);
        for(Object[] objects:list2){
            turnoverStatistics.setUnit(objects[0].toString());
            turnoverStatistics.setAmount((BigDecimal) objects[2]);
            turnoverStatistics.setType(TransactionTypeEnum.EXCHANGE_BASE);
            logger.info("{}币币成交额信息{}",dateStr,turnoverStatistics);
            mongoTemplate.insert(turnoverStatistics,"turnover_statistics");
        }
        logger.info("结束统计币币成交额信息{}",dateStr);

        /**
         * 币币成交量
         */
        logger.info("开始统计币币成交量信息{}",dateStr);
        List<Object[]> list3 = exchangeOrderRepository.getExchangeTurnoverCoin(dateStr);
        for(Object[] objects:list3){
            turnoverStatistics.setUnit(objects[0].toString());
            turnoverStatistics.setAmount((BigDecimal) objects[2]);
            turnoverStatistics.setType(TransactionTypeEnum.EXCHANGE_COIN);
            logger.info("{}币币成交量信息{}",dateStr,turnoverStatistics);
            mongoTemplate.insert(turnoverStatistics,"turnover_statistics");
        }
        logger.info("结束统计币币成交量信息{}",dateStr);

        /**
         * 充币
         */
        logger.info("开始统计充币信息{}",dateStr);
        List<Object[]> list4 = memberDepositDao.getDepositStatistics(dateStr);
        for(Object[] objects:list4){
            turnoverStatistics.setAmount(new BigDecimal(objects[1].toString()));
            turnoverStatistics.setUnit(objects[0].toString());
            turnoverStatistics.setType(TransactionTypeEnum.RECHARGE);
            logger.info("{}充币信息{}",dateStr,turnoverStatistics);
            mongoTemplate.insert(turnoverStatistics,"turnover_statistics");
        }
        logger.info("结束统计充币信息{}",dateStr);

        /**
         * 币币交易手续费
         */
        logger.info("开始统计币币交易手续费信息{}",dateStr);
        ProjectionOperation projectionOperation = Aggregation.project("time","type","unit","fee");

        Criteria operator = Criteria.where("coinName").ne("").andOperator(
                Criteria.where("time").gte(DateUtil.YYYY_MM_DD_MM_HH_SS.parse(dateStr+" 00:00:00").getTime()),
                Criteria.where("time").lte(DateUtil.YYYY_MM_DD_MM_HH_SS.parse(dateStr+" 23:59:59").getTime()),
                Criteria.where("type").is("EXCHANGE")
        );

        MatchOperation matchOperation = Aggregation.match(operator);

        GroupOperation groupOperation = Aggregation.group("unit","type").sum("fee").as("feeSum") ;

        Aggregation aggregation = Aggregation.newAggregation(projectionOperation, matchOperation, groupOperation);
        // 执行操作
        AggregationResults<Map> aggregationResults = this.mongoTemplate.aggregate(aggregation, "order_detail_aggregation", Map.class);
        List<Map> list = aggregationResults.getMappedResults();
        for(Map map:list){
            logger.info("*********{}币币交易手续费{}************",dateStr,map);
            turnoverStatistics.setFee(new BigDecimal(map.get("feeSum").toString()));
            turnoverStatistics.setAmount(null);
            turnoverStatistics.setUnit(map.get("unit").toString());
            turnoverStatistics.setType(TransactionTypeEnum.EXCHANGE);
            logger.info("{}币币交易手续费信息{}",dateStr,turnoverStatistics);
            mongoTemplate.insert(turnoverStatistics,"turnover_statistics");
        }
        logger.info("结束统计币币交易手续费信息{}",dateStr);

        /**
         * 提币
         */
        logger.info("开始统计提币信息{}",dateStr);
        List<Object[]> list5 = withdrawRecordDao.getWithdrawStatistics(dateStr);
        for(Object[] objects:list5){
            turnoverStatistics.setFee(new BigDecimal(objects[2].toString()));
            turnoverStatistics.setAmount(new BigDecimal(objects[1].toString()));
            turnoverStatistics.setUnit(objects[0].toString());
            turnoverStatistics.setType(TransactionTypeEnum.WITHDRAW);
            logger.info("{}提币信息{}",dateStr,turnoverStatistics);
            mongoTemplate.insert(turnoverStatistics,"turnover_statistics");
        }
        logger.info("结束统计提币信息{}",dateStr);
    }

    private void exchangeStatistics(String dateStr,Date date) throws ParseException {
        /**
         * 币币成交(按照交易对统计)
         */
        logger.info("开始统计币币成交(按照交易对统计)信息{}",dateStr);
        List<Object[]> list = exchangeOrderRepository.getExchangeTurnoverSymbol(dateStr);
        ExchangeTurnoverStatistics exchangeTurnoverStatistics = new ExchangeTurnoverStatistics() ;
        for(Object[] objects:list){
            exchangeTurnoverStatistics.setDate(DateUtil.YYYY_MM_DD.parse(dateStr));
            exchangeTurnoverStatistics.setAmount((BigDecimal)objects[3]);
            exchangeTurnoverStatistics.setBaseSymbol((String)objects[0]);
            exchangeTurnoverStatistics.setCoinSymbol((String)objects[1]);
            exchangeTurnoverStatistics.setMoney((BigDecimal)objects[4]);
            exchangeTurnoverStatistics.setYear(DateUtil.getDatePart(date,Calendar.YEAR));
            //Calendar month 默认从0开始，方便起见 保存月份从1开始
            exchangeTurnoverStatistics.setMonth(DateUtil.getDatePart(date,Calendar.MONTH)+1);
            exchangeTurnoverStatistics.setDay(DateUtil.getDatePart(date,Calendar.DAY_OF_MONTH));
            logger.info("{}币币成交(按照交易对统计)信息{}",dateStr,exchangeTurnoverStatistics);
            mongoTemplate.insert(exchangeTurnoverStatistics,"exchange_turnover_statistics");
        }
        logger.info("结束统计币币成交(按照交易对统计)信息{}",dateStr);
    }

}
