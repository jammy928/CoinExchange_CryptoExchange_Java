package com.bizzan.bitrade.controller.system;

import com.bizzan.bitrade.annotation.AccessLog;
import com.bizzan.bitrade.constant.AdminModule;
import com.bizzan.bitrade.constant.PageModel;
import com.bizzan.bitrade.constant.RewardRecordType;
import com.bizzan.bitrade.constant.TransactionType;
import com.bizzan.bitrade.controller.common.BaseAdminController;
import com.bizzan.bitrade.entity.*;
import com.bizzan.bitrade.es.ESUtils;
import com.bizzan.bitrade.service.*;
import com.bizzan.bitrade.util.BigDecimalUtils;
import com.bizzan.bitrade.util.MessageResult;
import com.querydsl.core.types.Predicate;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import static com.bizzan.bitrade.util.BigDecimalUtils.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 分红
 *
 * @author Shaoxianjun
 * @date 2019年03月21日
 */
@Slf4j
@RestController
@RequestMapping("/system/dividend")
public class DividendController extends BaseAdminController {
    @Autowired
    private OrderDetailAggregationService orderDetailAggregationService;
    @Autowired
    private DividendStartRecordService dividendStartRecordService;
    @Autowired
    private CoinService coinService;
    @Autowired
    private MemberWalletService memberWalletService;
    @Autowired
    private MemberTransactionService memberTransactionService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private RewardRecordService rewardRecordService;
    @Autowired
    private ESUtils esUtils;

    /**
     * 查看手续费信息
     *
     * @return
     */
    @RequestMapping(value = "/fee/info", method = RequestMethod.POST)
    @RequiresPermissions("system:dividend:fee-query")
    @AccessLog(module = AdminModule.SYSTEM, operation = "查看手续费信息")
    public MessageResult statisticsFee(@RequestParam(value = "start") Date start,
                                       @RequestParam(value = "end") Date end) {
        if (end.before(start)) {
            return error("error,end Time before start Time");
        }
        MessageResult result = success();
        result.setData(orderDetailAggregationService.queryStatistics(start.getTime(), end.getTime()));
        return result;
    }

    /**
     * 创建分红
     *
     * @return
     */
    @RequestMapping(value = "/start", method = RequestMethod.POST)
    @RequiresPermissions("system:dividend:start")
    @Transactional(rollbackFor = Exception.class)
    @AccessLog(module = AdminModule.SYSTEM, operation = "开始分红")
    public MessageResult start(@RequestParam(value = "start") Date start,
                               @RequestParam(value = "end") Date end,
                               String unit, BigDecimal amount, BigDecimal rate, HttpServletRequest request) {
        if (end.before(start)) {
            return error("error,end Time before start Time");
        }

        Assert.isTrue(rate.compareTo(BigDecimal.ZERO) > 0 && rate.compareTo(new BigDecimal("100")) <= 0, "rate illegal");
        Assert.isTrue(amount.compareTo(BigDecimal.ZERO) > 0, "amount illegal");
        Coin coin = coinService.queryPlatformCoin();
        if (coin == null) {
            return error("please set platform coin");
        }

        // TODO 获取分红金额
        /*List<OrderDetailAggregation> list = orderDetailAggregationService.queryStatisticsByUnit(start.getTime(), end.getTime(), unit);
        if (list.size() > 0) {
            if (new BigDecimal(
                    list.stream()
                            .map(x -> x.getFee()).reduce((x, y) -> x + y)
                            .orElse(0d))
                    .setScale(4, BigDecimal.ROUND_HALF_UP).compareTo(amount) != 0) {
                return error("amount error");
            }
        } else {
            return error("coin error");
        }*/



        if (dividendStartRecordService.matchRecord(start.getTime(), end.getTime(), unit).size() > 0) {
            return error("time Repeat");
        }
        Admin admin = getAdmin(request);

        DividendStartRecord record = new DividendStartRecord();
        record.setAdmin(admin);
        BigDecimal dividend = BigDecimalUtils.mulRound(amount, BigDecimalUtils.getRate(rate), 6);
        record.setAmount(dividend);
        record.setEnd(end.getTime());
        record.setEndDate(end);
        record.setStart(start.getTime());
        record.setStartDate(start);
        record.setUnit(unit);
        record.setRate(rate);
        if (dividendStartRecordService.save(record) != null) {
            try {
                startDividend(unit, dividend, coin);
            } catch (Exception e) {
                log.error("dividend error!");
            }
        }
        MessageResult result = success();
        return result;
    }

    //分红
    public synchronized void startDividend(String unit, BigDecimal dividend, Coin coin) {
        //平台币钱包
        List<MemberWallet> list = memberWalletService.findAllByCoin(coin);
        //总的平台币
        BigDecimal amount = list.stream().map(x ->
                add(x.getBalance(), x.getFrozenBalance())
        ).reduce((x, y) -> add(x, y)).orElse(BigDecimal.ZERO);
        Coin coin1 = coinService.findByUnit(unit);
        List<MemberWallet> list1 = memberWalletService.findAllByCoin(coin1);
        Map<Long, MemberWallet> map = list1.stream().collect(Collectors.toMap(x -> x.getMemberId(), x -> x));
        list.stream()
                .filter(x -> add(x.getFrozenBalance(), x.getBalance()).compareTo(BigDecimal.ZERO) > 0)
                .forEach(x -> {
                    BigDecimal va = mulDown(divDown(add(x.getBalance(), x.getFrozenBalance()), amount), dividend, 6);
                    map.get(x.getMemberId()).setBalance(add(x.getBalance(), va));
                    memberWalletService.save(map.get(x.getMemberId()));
                    MemberTransaction memberTransaction = new MemberTransaction();
                    memberTransaction.setFee(BigDecimal.ZERO);
                    memberTransaction.setSymbol(unit);
                    memberTransaction.setMemberId(x.getMemberId());
                    memberTransaction.setType(TransactionType.DIVIDEND);
                    memberTransaction.setAmount(va);memberTransaction.setRealFee("0");
                    memberTransaction.setDiscountFee("0");
                    memberTransaction.setCreateTime(new Date());
                    memberTransaction= memberTransactionService.save(memberTransaction);
                    RewardRecord rewardRecord1 = new RewardRecord();
                    rewardRecord1.setAmount(va);
                    rewardRecord1.setCoin(coin1);
                    rewardRecord1.setMember(memberService.findOne(x.getMemberId()));
                    rewardRecord1.setRemark(RewardRecordType.DIVIDEND.getCnName());
                    rewardRecord1.setType(RewardRecordType.DIVIDEND);
                    rewardRecordService.save(rewardRecord1);
                });
    }

    @RequiresPermissions("system:dividend:page-query")
    @PostMapping("page-query")
    public MessageResult pageQuery(
            PageModel pageModel,
            @RequestParam(value = "unit", required = false) String unit) {
        Predicate predicate = null;
        if (!StringUtils.isEmpty(unit)) {
            predicate = QDividendStartRecord.dividendStartRecord.unit.eq(unit);
        }
        Page<DividendStartRecord> all = dividendStartRecordService.findAll(predicate, pageModel);
        return success(all);
    }

}
