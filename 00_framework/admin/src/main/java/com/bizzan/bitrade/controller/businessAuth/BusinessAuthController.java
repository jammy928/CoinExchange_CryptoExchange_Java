package com.bizzan.bitrade.controller.businessAuth;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.bizzan.bitrade.constant.CertifiedBusinessStatus;
import com.bizzan.bitrade.constant.CommonStatus;
import com.bizzan.bitrade.constant.PageModel;
import com.bizzan.bitrade.constant.SysConstant;
import com.bizzan.bitrade.controller.common.BaseAdminController;
import com.bizzan.bitrade.entity.Admin;
import com.bizzan.bitrade.entity.BusinessAuthApply;
import com.bizzan.bitrade.entity.BusinessAuthDeposit;
import com.bizzan.bitrade.entity.Coin;
import com.bizzan.bitrade.entity.QBusinessAuthApply;
import com.bizzan.bitrade.entity.QBusinessAuthDeposit;
import com.bizzan.bitrade.service.BusinessAuthApplyService;
import com.bizzan.bitrade.service.BusinessAuthDepositService;
import com.bizzan.bitrade.service.CoinService;
import com.bizzan.bitrade.util.MessageResult;
import com.bizzan.bitrade.util.PredicateUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;

import lombok.extern.slf4j.Slf4j;

/**
 * 商家认证可用保证金类型
 *
 * @author Shaoxianjun
 * @date 2019/5/5
 */
@RestController
@RequestMapping("business-auth")
@Slf4j
public class BusinessAuthController extends BaseAdminController {
    @Autowired
    private BusinessAuthDepositService businessAuthDepositService;
    @Autowired
    private CoinService coinService;
    @Autowired
    private BusinessAuthApplyService businessAuthApplyService;

    @RequiresPermissions("business:auth:deposit:page")
    @GetMapping("page")
    public MessageResult getAll(PageModel pageModel, CommonStatus status) {
        ArrayList<BooleanExpression> booleanExpressions = new ArrayList<>();
        QBusinessAuthDeposit businessAuthDeposit = QBusinessAuthDeposit.businessAuthDeposit;
        if (status != null) {
            booleanExpressions.add(businessAuthDeposit.status.eq(status));
        }
        Predicate predicate = PredicateUtils.getPredicate(booleanExpressions);
        Page<BusinessAuthDeposit> depositPage = businessAuthDepositService.findAll(predicate, pageModel);
        MessageResult result = MessageResult.success();
        result.setData(depositPage);
        return result;
    }

    @RequiresPermissions("business:auth:deposit:create")
    @PostMapping("create")
    public MessageResult create(@SessionAttribute(SysConstant.SESSION_ADMIN) Admin admin,
                                @RequestParam("amount") Double amount,
                                @RequestParam("coinUnit") String coinUnit) {
        Coin coin = coinService.findByUnit(coinUnit);
        if (coin == null) {
            return error("validate coinUnit");
        }
        BusinessAuthDeposit businessAuthDeposit = new BusinessAuthDeposit();
        businessAuthDeposit.setAmount(new BigDecimal(amount));
        businessAuthDeposit.setCoin(coin);
        businessAuthDeposit.setCreateTime(new Date());
        businessAuthDeposit.setAdmin(admin);
        businessAuthDeposit.setStatus(CommonStatus.NORMAL);
        businessAuthDepositService.save(businessAuthDeposit);
        return success();
    }

    @RequiresPermissions("business-auth:apply:detail")
    @PostMapping("apply/detail")
    public MessageResult detail(@RequestParam("id") Long id) {
        MessageResult result = businessAuthApplyService.detail(id);
        return result;
    }

    @RequiresPermissions("business:auth:deposit:update")
    @PatchMapping("update")
    public MessageResult update(
            @RequestParam("id") Long id,
            @RequestParam("amount") Double amount,
            @RequestParam("status") CommonStatus status) {
        BusinessAuthDeposit oldData = businessAuthDepositService.findById(id);
        if (amount != null) {
            /*if(businessAuthDeposit.getAmount().compareTo(oldData.getAmount())>0){
                //如果上调了保证金，所有使用当前类型保证金的已认证商家的认证状态都改为保证金不足
                ArrayList<BooleanExpression> booleanExpressions = new ArrayList<>();
                booleanExpressions.add(QDepositRecord.depositRecord.coin.eq(oldData.getCoin()));
                booleanExpressions.add(QDepositRecord.depositRecord.status.eq(DepositStatusEnum.PAY));
                Predicate predicate=PredicateUtils.getPredicate(booleanExpressions);
                List<DepositRecord> depositRecordList=depositRecordService.findAll(predicate);
                if(depositRecordList!=null){
                    List<Long> idList=new ArrayList<>();
                    for(DepositRecord depositRecord:depositRecordList){
                        idList.add(depositRecord.getMember().getId());
                    }
                    memberService.updateCertifiedBusinessStatusByIdList(idList);
                }
            }*/
            oldData.setAmount(new BigDecimal(amount));
        }
        if (status != null) {
            oldData.setStatus(status);
        }
        businessAuthDepositService.save(oldData);
        return success();
    }

    @PostMapping("apply/page-query")
    @RequiresPermissions("business-auth:apply:page-query")
    public MessageResult page(
            PageModel pageModel,
            @RequestParam(value = "status", required = false) CertifiedBusinessStatus status,
            @RequestParam(value = "account", defaultValue = "") String account) {
        List<BooleanExpression> lists = new ArrayList<>();
        lists.add(QBusinessAuthApply.businessAuthApply.member.certifiedBusinessStatus.ne(CertifiedBusinessStatus.NOT_CERTIFIED));
        if (!"".equals(account)) {
            lists.add(QBusinessAuthApply.businessAuthApply.member.username.like("%" + account + "%")
                    .or(QBusinessAuthApply.businessAuthApply.member.mobilePhone.like(account + "%"))
                    .or(QBusinessAuthApply.businessAuthApply.member.email.like(account + "%"))
                    .or(QBusinessAuthApply.businessAuthApply.member.realName.like("%" + account + "%")));
        }
        if (status != null) {
            lists.add(QBusinessAuthApply.businessAuthApply.certifiedBusinessStatus.eq(status));
        }
        Page<BusinessAuthApply> page = businessAuthApplyService.page(PredicateUtils.getPredicate(lists), pageModel.getPageable());
        return success(page);

    }

    @PostMapping("get-search-status")
    public MessageResult getSearchStatus() {
        CertifiedBusinessStatus[] statuses = CertifiedBusinessStatus.values();
        List<Map> list = new ArrayList<>();
        for (CertifiedBusinessStatus status : statuses) {
            if (status == CertifiedBusinessStatus.NOT_CERTIFIED
                    || status.getOrdinal() >= CertifiedBusinessStatus.DEPOSIT_LESS.getOrdinal()) {
                continue;
            }
            Map map = new HashMap();
            map.put("name", status.getCnName());
            map.put("value", status.getOrdinal());
            list.add(map);
        }
        return success(list);
    }
}
