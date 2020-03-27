package com.bizzan.bitrade.controller.businessAuth;

import static com.bizzan.bitrade.constant.CertifiedBusinessStatus.CANCEL_AUTH;
import static com.bizzan.bitrade.constant.CertifiedBusinessStatus.NOT_CERTIFIED;
import static com.bizzan.bitrade.constant.CertifiedBusinessStatus.RETURN_FAILED;
import static com.bizzan.bitrade.constant.CertifiedBusinessStatus.RETURN_SUCCESS;
import static com.bizzan.bitrade.constant.CertifiedBusinessStatus.VERIFIED;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bizzan.bitrade.constant.BooleanEnum;
import com.bizzan.bitrade.constant.CertifiedBusinessStatus;
import com.bizzan.bitrade.constant.DepositStatusEnum;
import com.bizzan.bitrade.constant.MemberLevelEnum;
import com.bizzan.bitrade.constant.PageModel;
import com.bizzan.bitrade.controller.BaseController;
import com.bizzan.bitrade.entity.BusinessAuthApply;
import com.bizzan.bitrade.entity.BusinessCancelApply;
import com.bizzan.bitrade.entity.DepositRecord;
import com.bizzan.bitrade.entity.Member;
import com.bizzan.bitrade.entity.MemberWallet;
import com.bizzan.bitrade.entity.QBusinessCancelApply;
import com.bizzan.bitrade.service.BusinessAuthApplyService;
import com.bizzan.bitrade.service.BusinessCancelApplyService;
import com.bizzan.bitrade.service.DepositRecordService;
import com.bizzan.bitrade.service.LocaleMessageSourceService;
import com.bizzan.bitrade.service.MemberService;
import com.bizzan.bitrade.service.MemberWalletService;
import com.bizzan.bitrade.util.DateUtil;
import com.bizzan.bitrade.util.MessageResult;
import com.bizzan.bitrade.util.PredicateUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.querydsl.core.types.dsl.BooleanExpression;

@RestController
@RequestMapping("business/cancel-apply")
public class BusinessCancelApplyController extends BaseController {
    private static Logger logger = LoggerFactory.getLogger(BusinessCancelApplyController.class);
    @Autowired
    private BusinessCancelApplyService businessCancelApplyService;
    @Autowired
    private DepositRecordService depositRecordService;
    @Autowired
    private BusinessAuthApplyService businessAuthApplyService;
    @Autowired
    private MemberWalletService memberWalletService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private LocaleMessageSourceService msService;

    @PostMapping("page-query")
    @RequiresPermissions("business:cancel-apply:page-query")
    public MessageResult pageQuery(
            PageModel pageModel,
            @RequestParam(value = "account", required = false) String account,
            @RequestParam(value = "status", required = false) CertifiedBusinessStatus status,
            @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd") Date startDate,
            @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd") Date endDate) {
        List<BooleanExpression> predicates = new ArrayList<>();
        if (!StringUtils.isEmpty(account)) {
            predicates.add(QBusinessCancelApply.businessCancelApply.member.username.like("%" + account + "%")
                    .or(QBusinessCancelApply.businessCancelApply.member.mobilePhone.like("%" + account + "%"))
                    .or(QBusinessCancelApply.businessCancelApply.member.email.like("%" + account + "%"))
                    .or(QBusinessCancelApply.businessCancelApply.member.realName.like("%" + account + "%")));
        }
        predicates.add(QBusinessCancelApply.businessCancelApply.status.in(CANCEL_AUTH, RETURN_FAILED, RETURN_SUCCESS));
        if (status != null) {
            predicates.add(QBusinessCancelApply.businessCancelApply.status.eq(status));
        }
        if (startDate != null) {
            predicates.add(QBusinessCancelApply.businessCancelApply.cancelApplyTime.goe(startDate));
        }
        if (endDate != null) {
            predicates.add(QBusinessCancelApply.businessCancelApply.cancelApplyTime.loe(endDate));
        }

        Page<BusinessCancelApply> page = businessCancelApplyService.findAll(PredicateUtils.getPredicate(predicates), pageModel);

        for (BusinessCancelApply businessCancelApply : page.getContent()) {
            DepositRecord depositRecord = depositRecordService.findOne(businessCancelApply.getDepositRecordId());
            businessCancelApply.setDepositRecord(depositRecord);
        }
        return success(page);
    }

    /**
     * 退保审核接口
     *
     * @param id
     * @param success 通过 : IS_TRUE
     * @param reason  审核不通过的理由
     * @return
     */
    @RequiresPermissions("business:cancel-apply:check")
    @PostMapping("check")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult pass(
            @RequestParam(value = "id") Long id,
            @RequestParam(value = "success") BooleanEnum success,
            @RequestParam(value = "reason", defaultValue = "") String reason) {
        BusinessCancelApply businessCancelApply = businessCancelApplyService.findById(id);
        Member member = businessCancelApply.getMember();
        List<BusinessAuthApply> businessAuthApplyList = businessAuthApplyService.findByMemberAndCertifiedBusinessStatus(member, VERIFIED);
        if (businessAuthApplyList == null || businessAuthApplyList.size() < 1) {
            return error("data exception,businessAuthApply not exist。。。。");
        }
        BusinessAuthApply businessAuthApply = businessAuthApplyList.get(0);
        /**
         * 处理 取消申请 日志
         */
        businessCancelApply.setHandleTime(DateUtil.getCurrentDate());
        businessCancelApply.setDepositRecordId(businessAuthApply.getDepositRecordId());
        businessCancelApply.setDetail(reason);

        if (success == BooleanEnum.IS_TRUE) {

            businessCancelApply.setStatus(RETURN_SUCCESS);
            businessCancelApplyService.save(businessCancelApply);

            //取消商家认证 审核通过
            //member.setCertifiedBusinessStatus(RETURN_SUCCESS);//未认证
            member.setCertifiedBusinessStatus(NOT_CERTIFIED);
            member.setMemberLevel(MemberLevelEnum.REALNAME);
            memberService.save(member);

            List<DepositRecord> depositRecordList = depositRecordService.findByMemberAndStatus(member, DepositStatusEnum.PAY);
            if (depositRecordList != null && depositRecordList.size() > 0) {
                BigDecimal deposit = BigDecimal.ZERO;

                /**
                 * 更改保证金记录
                 */
                for (DepositRecord depositRecord : depositRecordList) {
                    depositRecord.setStatus(DepositStatusEnum.GET_BACK);
                    deposit = deposit.add(depositRecord.getAmount());
                    depositRecordService.save(depositRecord);
                }

                /**
                 * 退回保证金
                 */
                if (businessAuthApplyList != null && businessAuthApplyList.size() > 0) {
                    MemberWallet memberWallet = memberWalletService.findByCoinUnitAndMemberId(businessAuthApply.getBusinessAuthDeposit().getCoin().getUnit(), member.getId());
                    memberWallet.setBalance(memberWallet.getBalance().add(deposit));
                    // memberWallet.setFrozenBalance(memberWallet.getFrozenBalance().subtract(deposit));
                    memberWalletService.save(memberWallet);
                }
            }
            /**
             * 更改认证申请状态
             */
            return MessageResult.success(msService.getMessage("PASS_THE_AUDIT"), reason);
        } else {
            //审核不通过，商家 维持已认证状态
            //member.setCertifiedBusinessStatus(RETURN_FAILED);
            member.setCertifiedBusinessStatus(VERIFIED);
            member.setMemberLevel(MemberLevelEnum.IDENTIFICATION);
            memberService.save(member);

            businessCancelApply.setStatus(RETURN_FAILED);
            businessCancelApplyService.save(businessCancelApply);

            return MessageResult.success(msService.getMessage("AUDIT_DOES_NOT_PASS"), reason);
        }
    }

    /**
     * @param id:businessCancelApply id
     * @return
     */

    @PostMapping("detail")
    @RequiresPermissions("business:cancel-apply:detail")
    public MessageResult detail(@RequestParam(value = "id") Long id) {
        BusinessCancelApply businessCancelApply = businessCancelApplyService.findById(id);
        DepositRecord depositRecord = depositRecordService.findOne(businessCancelApply.getDepositRecordId());
        Map<String, Object> map1 = businessCancelApplyService.getBusinessOrderStatistics(businessCancelApply.getMember().getId());
        logger.info("会员订单信息:{}", map1);
        Map<String, Object> map2 = businessCancelApplyService.getBusinessAppealStatistics(businessCancelApply.getMember().getId());
        logger.info("会员申诉信息:{}", map2);
        Long advertiseNum = businessCancelApplyService.getAdvertiserNum(businessCancelApply.getMember().getId());
        logger.info("会员广告信息:{}", advertiseNum);
        Map<String, Object> map = new HashMap<>();
        map.putAll(map1);
        map.putAll(map2);
        map.put("advertiseNum", advertiseNum);
        map.put("businessCancelApply", businessCancelApply);
        map.put("depositRecord", depositRecord);
        logger.info("会员退保相关信息:{}", map);
        return success(map);
    }

    @PostMapping("get-search-status")
    public MessageResult getSearchStatus() {
        CertifiedBusinessStatus[] statuses = CertifiedBusinessStatus.values();
        List<Map> list = new ArrayList<>();
        for (CertifiedBusinessStatus status : statuses) {
            if (status.getOrdinal() < CertifiedBusinessStatus.CANCEL_AUTH.getOrdinal()) {
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
