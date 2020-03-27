package com.bizzan.bitrade.controller;


import com.bizzan.bitrade.constant.*;
import com.bizzan.bitrade.entity.*;
import com.bizzan.bitrade.entity.transform.AuthMember;
import com.bizzan.bitrade.pagination.PageResult;
import com.bizzan.bitrade.service.*;
import com.bizzan.bitrade.util.*;
import com.querydsl.core.types.Predicate;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.bizzan.bitrade.constant.BooleanEnum.IS_FALSE;
import static com.bizzan.bitrade.constant.BooleanEnum.IS_TRUE;
import static com.bizzan.bitrade.constant.CertifiedBusinessStatus.*;
import static com.bizzan.bitrade.constant.SysConstant.SESSION_MEMBER;
import static org.springframework.util.Assert.*;


/**
 * 用户中心认证
 *
 * @author GS
 * @date 2018年01月09日
 */
@RestController
@RequestMapping("/approve")
@Slf4j
public class ApproveController {

    private static Logger logger = LoggerFactory.getLogger(ApproveController.class);

    @Autowired
    private MemberService memberService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private LocaleMessageSourceService msService;
    @Autowired
    private MemberApplicationService memberApplicationService;
    @Autowired
    private BusinessAuthDepositService businessAuthDepositService;
    @Autowired
    private BusinessCancelApplyService businessCancelApplyService ;
    @Autowired
    private MemberWalletService memberWalletService;
    @Autowired
    private BusinessAuthApplyService businessAuthApplyService;
    @Autowired
    private DepositRecordService depositRecordService;

    /**
     * 设置或更改用户头像
     *
     * @param user
     * @param url
     * @return
     */
    @RequestMapping("/change/avatar")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult update(@SessionAttribute(SESSION_MEMBER) AuthMember user, String url) {
        Member member = memberService.findOne(user.getId());
        member.setAvatar(url);
        return MessageResult.success();
    }

    /**
     * 安全设置
     *
     * @param user
     * @return
     */
    @RequestMapping("/security/setting")
    public MessageResult securitySetting(@SessionAttribute(SESSION_MEMBER) AuthMember user) {
        Member member = memberService.findOne(user.getId());
        String idNumber = member.getIdNumber();
        MemberSecurity memberSecurity = MemberSecurity.builder().username(member.getUsername())
                .createTime(member.getRegistrationTime())
                .id(member.getId())
                .emailVerified(StringUtils.isEmpty(member.getEmail()) ? IS_FALSE : IS_TRUE)
                .email(member.getEmail())
                .mobilePhone(member.getMobilePhone())
                .fundsVerified(StringUtils.isEmpty(member.getJyPassword()) ? IS_FALSE : IS_TRUE)
                .loginVerified(IS_TRUE)
                .phoneVerified(StringUtils.isEmpty(member.getMobilePhone()) ? IS_FALSE : IS_TRUE)
                .realName(member.getRealName())
                .idCard(StringUtils.isEmpty(idNumber) ? null : idNumber.substring(0, 2) + "**********" + idNumber.substring(idNumber.length() - 2))
                .realVerified(StringUtils.isEmpty(member.getRealName()) ? IS_FALSE : IS_TRUE)
                .realAuditing(member.getRealNameStatus().equals(RealNameStatus.AUDITING) ? IS_TRUE : IS_FALSE)
                .avatar(member.getAvatar())
                .accountVerified((member.getBankInfo() == null && member.getAlipay() == null && member.getWechatPay() == null) ? IS_FALSE : IS_TRUE)
                .googleStatus(member.getGoogleState())
                .build();
        if (memberSecurity.getRealAuditing().equals(IS_FALSE) && memberSecurity.getRealVerified().equals(IS_FALSE)) {
            List<MemberApplication> memberApplication = memberApplicationService.findLatelyReject(member);
            memberSecurity.setRealNameRejectReason(memberApplication == null||memberApplication.size()==0 ? null : memberApplication.get(0).getRejectReason());
        }
        MessageResult result = MessageResult.success("success");
        result.setData(memberSecurity);
        return result;
    }

    /**
     * 设置资金密码
     *
     * @param jyPassword
     * @param user
     * @return
     */
    @RequestMapping("/transaction/password")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult approveTransaction(String jyPassword, @SessionAttribute(SESSION_MEMBER) AuthMember user) throws Exception {
        hasText(jyPassword, msService.getMessage("MISSING_JY_PASSWORD"));
        isTrue(jyPassword.length() >= 6 && jyPassword.length() <= 20, msService.getMessage("JY_PASSWORD_LENGTH_ILLEGAL"));
        Member member = memberService.findOne(user.getId());
        Assert.isNull(member.getJyPassword(), msService.getMessage("REPEAT_SETTING"));
        //生成密码
        String jyPass = Md5.md5Digest(jyPassword + member.getSalt()).toLowerCase();
        member.setJyPassword(jyPass);
        return MessageResult.success(msService.getMessage("SETTING_JY_PASSWORD"));
    }

    /**
     * 修改资金密码
     *
     * @param oldPassword
     * @param newPassword
     * @param user
     * @return
     */
    @RequestMapping("/update/transaction/password")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult updateTransaction(String oldPassword, String newPassword, @SessionAttribute(SESSION_MEMBER) AuthMember user) throws Exception {
        hasText(oldPassword, msService.getMessage("MISSING_OLD_JY_PASSWORD"));
        hasText(newPassword, msService.getMessage("MISSING_NEW_JY_PASSWORD"));
        isTrue(newPassword.length() >= 6 && newPassword.length() <= 20, msService.getMessage("JY_PASSWORD_LENGTH_ILLEGAL"));
        Member member = memberService.findOne(user.getId());
        isTrue(Md5.md5Digest(oldPassword + member.getSalt()).toLowerCase().equals(member.getJyPassword()), msService.getMessage("ERROR_JYPASSWORD"));
        member.setJyPassword(Md5.md5Digest(newPassword + member.getSalt()).toLowerCase());
        return MessageResult.success(msService.getMessage("SETTING_JY_PASSWORD"));
    }

    /**
     * 重置资金密码
     *
     * @param newPassword
     * @param code
     * @param user
     * @return
     * @throws Exception
     */
    @RequestMapping("/reset/transaction/password")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult resetTransaction(String newPassword, String code, @SessionAttribute(SESSION_MEMBER) AuthMember user) throws Exception {
        hasText(newPassword, msService.getMessage("MISSING_NEW_JY_PASSWORD"));
        isTrue(newPassword.length() >= 6 && newPassword.length() <= 20, msService.getMessage("JY_PASSWORD_LENGTH_ILLEGAL"));
        ValueOperations valueOperations = redisTemplate.opsForValue();
        Object cache = valueOperations.get(SysConstant.PHONE_RESET_TRANS_CODE_PREFIX + user.getMobilePhone());
        notNull(cache, msService.getMessage("NO_GET_VERIFICATION_CODE"));
        hasText(code, msService.getMessage("MISSING_VERIFICATION_CODE"));
        if (!code.equals(cache.toString())) {
            return MessageResult.error(msService.getMessage("VERIFICATION_CODE_INCORRECT"));
        } else {
            valueOperations.getOperations().delete(SysConstant.PHONE_RESET_TRANS_CODE_PREFIX + user.getMobilePhone());
        }
        Member member = memberService.findOne(user.getId());
        member.setJyPassword(Md5.md5Digest(newPassword + member.getSalt()).toLowerCase());
        return MessageResult.success(msService.getMessage("SETTING_JY_PASSWORD"));
    }

    /**
     * 绑定手机号
     *
     * @param password
     * @param phone
     * @param code
     * @param user
     * @return
     */
    @RequestMapping("/bind/phone")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult bindPhone(HttpServletRequest request, String password, String phone, String code, @SessionAttribute(SESSION_MEMBER) AuthMember user) throws Exception {
        hasText(password, msService.getMessage("MISSING_LOGIN_PASSWORD"));
        hasText(phone, msService.getMessage("MISSING_PHONE"));
        hasText(code, msService.getMessage("MISSING_VERIFICATION_CODE"));
        if ("中国".equals(user.getLocation().getCountry())) {
            if (!ValidateUtil.isMobilePhone(phone.trim())) {
                return MessageResult.error(msService.getMessage("PHONE_FORMAT_ERROR"));
            }
        }
        ValueOperations valueOperations = redisTemplate.opsForValue();
        Object cache = valueOperations.get(SysConstant.PHONE_BIND_CODE_PREFIX + phone);
        notNull(cache, msService.getMessage("NO_GET_VERIFICATION_CODE"));
        Member member1 = memberService.findByPhone(phone);
        isTrue(member1 == null, msService.getMessage("PHONE_ALREADY_BOUND"));
        if (!code.equals(cache.toString())) {
            return MessageResult.error(msService.getMessage("VERIFICATION_CODE_INCORRECT"));
        } else {
            valueOperations.getOperations().delete(SysConstant.PHONE_BIND_CODE_PREFIX + phone);
        }
        Member member = memberService.findOne(user.getId());
        isTrue(member.getMobilePhone() == null, msService.getMessage("REPEAT_PHONE_REQUEST"));
        if (member.getPassword().equals(Md5.md5Digest(password + member.getSalt()).toLowerCase())) {
            member.setMobilePhone(phone);
            return MessageResult.success(msService.getMessage("SETTING_SUCCESS"));
        } else {
            request.removeAttribute(SysConstant.SESSION_MEMBER);
            return MessageResult.error(msService.getMessage("PASSWORD_ERROR"));
        }
    }


    /**
     *
     * 更改登录密码
     *
     * @param request
     * @param oldPassword
     * @param newPassword
     * @param code
     * @param user
     * @return
     * @throws Exception
     */
    @RequestMapping("/update/password")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult updateLoginPassword(HttpServletRequest request, String oldPassword, String newPassword, String code, @SessionAttribute(SESSION_MEMBER) AuthMember user) throws Exception {
        hasText(oldPassword, msService.getMessage("MISSING_OLD_PASSWORD"));
        hasText(newPassword, msService.getMessage("MISSING_NEW_PASSWORD"));
        isTrue(newPassword.length() >= 6 && newPassword.length() <= 20, msService.getMessage("PASSWORD_LENGTH_ILLEGAL"));
        ValueOperations valueOperations = redisTemplate.opsForValue();
        Object cache = valueOperations.get(SysConstant.PHONE_UPDATE_PASSWORD_PREFIX + user.getMobilePhone());
        notNull(cache, msService.getMessage("NO_GET_VERIFICATION_CODE"));
        hasText(code, msService.getMessage("MISSING_VERIFICATION_CODE"));
        if (!code.equals(cache.toString())) {
            return MessageResult.error(msService.getMessage("VERIFICATION_CODE_INCORRECT"));
        } else {
            valueOperations.getOperations().delete(SysConstant.PHONE_UPDATE_PASSWORD_PREFIX + user.getMobilePhone());
        }
        Member member = memberService.findOne(user.getId());
        request.removeAttribute(SysConstant.SESSION_MEMBER);
        isTrue(Md5.md5Digest(oldPassword + member.getSalt()).toLowerCase().equals(member.getPassword()), msService.getMessage("PASSWORD_ERROR"));
        member.setPassword(Md5.md5Digest(newPassword + member.getSalt()).toLowerCase());
        return MessageResult.success(msService.getMessage("SETTING_SUCCESS"));
    }

    /**
     * 绑定邮箱
     *
     * @param request
     * @param password
     * @param code
     * @param email
     * @param user
     * @return
     */
    @RequestMapping("/bind/email")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult bindEmail(HttpServletRequest request, String password, String code, String email, @SessionAttribute(SESSION_MEMBER) AuthMember user) throws Exception {
        hasText(password, msService.getMessage("MISSING_LOGIN_PASSWORD"));
        hasText(code, msService.getMessage("MISSING_VERIFICATION_CODE"));
        hasText(email, msService.getMessage("MISSING_EMAIL"));
        isTrue(ValidateUtil.isEmail(email), msService.getMessage("EMAIL_FORMAT_ERROR"));
        ValueOperations valueOperations = redisTemplate.opsForValue();
        Object cache = valueOperations.get(SysConstant.EMAIL_BIND_CODE_PREFIX + email);
        notNull(cache, msService.getMessage("NO_GET_VERIFICATION_CODE"));
        isTrue(code.equals(cache.toString()), msService.getMessage("VERIFICATION_CODE_INCORRECT"));
        Member member = memberService.findOne(user.getId());
        isTrue(member.getEmail() == null, msService.getMessage("REPEAT_EMAIL_REQUEST"));
        if (!Md5.md5Digest(password + member.getSalt()).toLowerCase().equals(member.getPassword())) {
            request.removeAttribute(SysConstant.SESSION_MEMBER);
            return MessageResult.error(msService.getMessage("PASSWORD_ERROR"));
        } else {
            member.setEmail(email);
            return MessageResult.success(msService.getMessage("SETTING_SUCCESS"));
        }
    }

    /**
     * 实名认证
     *
     * @param realName
     * @param idCard
     * @param user
     * @return
     */
    @RequestMapping("/real/name")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult realApprove(String realName, String idCard, String idCardFront,
                                     String idCardBack, String handHeldIdCard,
                                     @SessionAttribute(SESSION_MEMBER) AuthMember user) {
        hasText(realName, msService.getMessage("MISSING_REAL_NAME"));
        hasText(idCard, msService.getMessage("MISSING_ID_CARD"));
        hasText(idCardFront, msService.getMessage("MISSING_ID_CARD_FRONT"));
        hasText(idCardBack, msService.getMessage("MISSING_ID_CARD_BACK"));
        hasText(handHeldIdCard, msService.getMessage("MISSING_ID_CARD_HAND"));
        Member member = memberService.findOne(user.getId());
        if ("China".equals(member.getCountry().getEnName())) {
            isTrue(ValidateUtil.isChineseName(realName), msService.getMessage("REAL_NAME_ILLEGAL"));
            isTrue(IdcardValidator.isValidate18Idcard(idCard), msService.getMessage("ID_CARD_ILLEGAL"));
        }
        isTrue(member.getRealNameStatus() == RealNameStatus.NOT_CERTIFIED, msService.getMessage("REPEAT_REAL_NAME_REQUEST"));
        int count = memberApplicationService.queryByIdCard(idCard);
        if(count>0){
            return MessageResult.error("同一个身份证号只能认证一次");
        }
        MemberApplication memberApplication = new MemberApplication();
        memberApplication.setAuditStatus(AuditStatus.AUDIT_ING);
        memberApplication.setRealName(realName);
        memberApplication.setIdCard(idCard);
        memberApplication.setMember(member);
        memberApplication.setIdentityCardImgFront(idCardFront);
        memberApplication.setIdentityCardImgInHand(handHeldIdCard);
        memberApplication.setIdentityCardImgReverse(idCardBack);
        memberApplication.setCreateTime(new Date());
        memberApplicationService.save(memberApplication);
        member.setRealNameStatus(RealNameStatus.AUDITING);
        return MessageResult.success(msService.getMessage("REAL_APPLY_SUCCESS"));
    }


    /**
     * 查询实名认证情况
     *
     * @param user
     * @return
     */
    @PostMapping("/real/detail")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult realNameApproveDetail(@SessionAttribute(SESSION_MEMBER) AuthMember user) {
        Member member = memberService.findOne(user.getId());
        List<Predicate> predicateList = new ArrayList<>();
        predicateList.add(QMemberApplication.memberApplication.member.eq(member));
        PageResult<MemberApplication> memberApplicationPageResult = memberApplicationService.query(predicateList, null, null);
        MemberApplication memberApplication = new MemberApplication();
        if (memberApplicationPageResult != null && memberApplicationPageResult.getContent() != null
                && memberApplicationPageResult.getContent().size() > 0) {
            memberApplication = memberApplicationPageResult.getContent().get(0);
        }
        MessageResult result = MessageResult.success();
        result.setData(memberApplication);
        return result;
    }

    /**
     * 账户设置
     *
     * @param user
     * @return
     */
    @RequestMapping("/account/setting")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult accountSetting(@SessionAttribute(SESSION_MEMBER) AuthMember user) {
        Member member = memberService.findOne(user.getId());
        hasText(member.getIdNumber(), msService.getMessage("NO_REAL_NAME"));
        hasText(member.getJyPassword(), msService.getMessage("NO_JY_PASSWORD"));
        MemberAccount memberAccount = MemberAccount.builder().alipay(member.getAlipay())
                .aliVerified(member.getAlipay() == null ? IS_FALSE : IS_TRUE)
                .bankInfo(member.getBankInfo())
                .bankVerified(member.getBankInfo() == null ? IS_FALSE : IS_TRUE)
                .wechatPay(member.getWechatPay())
                .wechatVerified(member.getWechatPay() == null ? IS_FALSE : IS_TRUE)
                .realName(member.getRealName())
                .build();
        MessageResult result = MessageResult.success();
        result.setData(memberAccount);
        return result;
    }


    /**
     * 设置银行卡
     *
     * @param bindBank
     * @param bindingResult
     * @param user
     * @return
     * @throws Exception
     */
    @RequestMapping("/bind/bank")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult bindBank(@Valid BindBank bindBank, BindingResult bindingResult, @SessionAttribute(SESSION_MEMBER) AuthMember user) throws Exception {
        Member member = memberService.findOne(user.getId());
        isTrue(member.getBankInfo() == null, msService.getMessage("REPEAT_SETTING"));
        return doBank(bindBank, bindingResult, user);
    }

    private MessageResult doBank(BindBank bindBank, BindingResult bindingResult, AuthMember user) throws Exception {
        MessageResult result = BindingResultUtil.validate(bindingResult);
        if (result != null) {
            return result;
        }
        Member member = memberService.findOne(user.getId());
        isTrue(Md5.md5Digest(bindBank.getJyPassword() + member.getSalt()).toLowerCase().equals(member.getJyPassword()), msService.getMessage("ERROR_JYPASSWORD"));
        BankInfo bankInfo = new BankInfo();
        bankInfo.setBank(bindBank.getBank());
        bankInfo.setBranch(bindBank.getBranch());
        bankInfo.setCardNo(bindBank.getCardNo());
        member.setBankInfo(bankInfo);
        return MessageResult.success(msService.getMessage("SETTING_SUCCESS"));
    }

    /**
     * 更改银行卡
     *
     * @param bindBank
     * @param bindingResult
     * @param user
     * @return
     * @throws Exception
     */
    @RequestMapping("/update/bank")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult updateBank(@Valid BindBank bindBank, BindingResult bindingResult, @SessionAttribute(SESSION_MEMBER) AuthMember user) throws Exception {
        return doBank(bindBank, bindingResult, user);
    }

    /**
     * 绑定阿里
     *
     * @param bindAli
     * @param bindingResult
     * @param user
     * @return
     * @throws Exception
     */
    @RequestMapping("/bind/ali")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult bindAli(@Valid BindAli bindAli, BindingResult bindingResult, @SessionAttribute(SESSION_MEMBER) AuthMember user) throws Exception {
        Member member = memberService.findOne(user.getId());
        isTrue(member.getAlipay() == null, msService.getMessage("REPEAT_SETTING"));
        return doAli(bindAli, bindingResult, user);
    }

    private MessageResult doAli(BindAli bindAli, BindingResult bindingResult, AuthMember user) throws Exception {
        MessageResult result = BindingResultUtil.validate(bindingResult);
        if (result != null) {
            return result;
        }
        Member member = memberService.findOne(user.getId());
        isTrue(Md5.md5Digest(bindAli.getJyPassword() + member.getSalt()).toLowerCase().equals(member.getJyPassword()), msService.getMessage("ERROR_JYPASSWORD"));
        Alipay alipay = new Alipay();
        alipay.setAliNo(bindAli.getAli());
        alipay.setQrCodeUrl(bindAli.getQrCodeUrl());
        member.setAlipay(alipay);
        return MessageResult.success(msService.getMessage("SETTING_SUCCESS"));
    }

    /**
     * 修改支付宝
     *
     * @param bindAli
     * @param bindingResult
     * @param user
     * @return
     */
    @RequestMapping("/update/ali")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult updateAli(@Valid BindAli bindAli, BindingResult bindingResult, @SessionAttribute(SESSION_MEMBER) AuthMember user) throws Exception {
        return doAli(bindAli, bindingResult, user);
    }

    /**
     * 绑定微信
     *
     * @param bindWechat
     * @param bindingResult
     * @param user
     * @return
     * @throws Exception
     */
    @RequestMapping("/bind/wechat")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult bindWechat(@Valid BindWechat bindWechat, BindingResult bindingResult, @SessionAttribute(SESSION_MEMBER) AuthMember user) throws Exception {
        Member member = memberService.findOne(user.getId());
        isTrue(member.getWechatPay() == null, msService.getMessage("REPEAT_SETTING"));
        return doWechat(bindWechat, bindingResult, user);
    }

    private MessageResult doWechat(BindWechat bindWechat, BindingResult bindingResult, AuthMember user) throws Exception {
        MessageResult result = BindingResultUtil.validate(bindingResult);
        if (result != null) {
            return result;
        }
        Member member = memberService.findOne(user.getId());
        isTrue(Md5.md5Digest(bindWechat.getJyPassword() + member.getSalt()).toLowerCase().equals(member.getJyPassword()), msService.getMessage("ERROR_JYPASSWORD"));
        WechatPay wechatPay = new WechatPay();
        wechatPay.setWechat(bindWechat.getWechat());
        wechatPay.setQrWeCodeUrl(bindWechat.getQrCodeUrl());
        member.setWechatPay(wechatPay);
        return MessageResult.success(msService.getMessage("SETTING_SUCCESS"));
    }

    /**
     * 修改微信
     *
     * @param bindWechat
     * @param bindingResult
     * @param user
     * @return
     */
    @RequestMapping("/update/wechat")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult updateWechat(@Valid BindWechat bindWechat, BindingResult bindingResult, @SessionAttribute(SESSION_MEMBER) AuthMember user) throws Exception {
        return doWechat(bindWechat, bindingResult, user);
    }

    /**
     * 认证商家申请状态
     *
     * @param user
     * @return
     */
    @RequestMapping("/certified/business/status")
    public MessageResult certifiedBusinessStatus(@SessionAttribute(SESSION_MEMBER) AuthMember user) {
        Member member = memberService.findOne(user.getId());
        CertifiedBusinessInfo certifiedBusinessInfo = new CertifiedBusinessInfo();
        certifiedBusinessInfo.setCertifiedBusinessStatus(member.getCertifiedBusinessStatus());
        certifiedBusinessInfo.setEmail(member.getEmail());
        certifiedBusinessInfo.setMemberLevel(member.getMemberLevel());
        logger.info("会员状态信息:{}",certifiedBusinessInfo);
        if(member.getCertifiedBusinessStatus().equals(CertifiedBusinessStatus.FAILED)){
            List<BusinessAuthApply> businessAuthApplyList=businessAuthApplyService.findByMemberAndCertifiedBusinessStatus(member,member.getCertifiedBusinessStatus());
            logger.info("会员申请商家认证信息:{}",businessAuthApplyList);
            if(businessAuthApplyList!=null&&businessAuthApplyList.size()>0){
                certifiedBusinessInfo.setCertifiedBusinessStatus(businessAuthApplyList.get(0).getCertifiedBusinessStatus());
                logger.info("会员申请商家认证最新信息:{}",businessAuthApplyList.get(0));
                certifiedBusinessInfo.setDetail(businessAuthApplyList.get(0).getDetail());
            }
        }

        List<BusinessCancelApply> businessCancelApplies = businessCancelApplyService.findByMember(member);
        if(businessCancelApplies!=null&&businessCancelApplies.size()>0){
            if(businessCancelApplies.get(0).getStatus()==RETURN_SUCCESS) {
                if(member.getCertifiedBusinessStatus()!=VERIFIED) {
                    certifiedBusinessInfo.setCertifiedBusinessStatus(RETURN_SUCCESS);
                }
            }else if(businessCancelApplies.get(0).getStatus()==RETURN_FAILED){
                certifiedBusinessInfo.setCertifiedBusinessStatus(RETURN_FAILED);
            }else{
                certifiedBusinessInfo.setCertifiedBusinessStatus(CANCEL_AUTH);
            }
        }


        MessageResult result = MessageResult.success();
        result.setData(certifiedBusinessInfo);
        return result;
    }

    /**
     * 认证商家申请
     *
     * @param user
     * @return
     */
    @RequestMapping("/certified/business/apply")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult certifiedBusiness(@SessionAttribute(SESSION_MEMBER) AuthMember user, String json,
                                           @RequestParam Long businessAuthDepositId) {
        Member member = memberService.findOne(user.getId());
        //只有未认证和认证失败的用户，可以发起认证申请
        isTrue(member.getCertifiedBusinessStatus().equals(CertifiedBusinessStatus.NOT_CERTIFIED)
                ||member.getCertifiedBusinessStatus().equals(CertifiedBusinessStatus.FAILED), msService.getMessage("REPEAT_APPLICATION"));
        isTrue(member.getMemberLevel().equals(MemberLevelEnum.REALNAME), msService.getMessage("NO_REAL_NAME"));
        //hasText(member.getEmail(), msService.getMessage("NOT_BIND_EMAIL"));
        List<BusinessAuthDeposit> depositList=businessAuthDepositService.findAllByStatus(CommonStatus.NORMAL);
        //如果当前有启用的保证金类型，必须选择一种保证金才可以申请商家认证
        BusinessAuthDeposit businessAuthDeposit=null;
        if(depositList!=null&&depositList.size()>0){
            if(businessAuthDepositId==null){
                return MessageResult.error("must select a kind of business auth deposit");
            }
            boolean flag=false;
            for(BusinessAuthDeposit deposit:depositList){
                if(deposit.getId().equals(businessAuthDepositId)){
                    businessAuthDeposit=deposit;
                    flag=true;
                }
            }
            if(!flag){
                return MessageResult.error("business auth deposit is not found");
            }
            MemberWallet memberWallet=memberWalletService.findByCoinUnitAndMemberId(businessAuthDeposit.getCoin().getUnit(),member.getId());
            if(memberWallet.getBalance().compareTo(businessAuthDeposit.getAmount())<0){
                return MessageResult.error("您的余额不足");
            }
            //冻结保证金需要的金额
            memberWallet.setBalance(memberWallet.getBalance().subtract(businessAuthDeposit.getAmount()));
            memberWallet.setFrozenBalance(memberWallet.getFrozenBalance().add(businessAuthDeposit.getAmount()));
        }
        //申请记录
        BusinessAuthApply businessAuthApply=new BusinessAuthApply();
        businessAuthApply.setCreateTime(new Date());
        businessAuthApply.setAuthInfo(json);
        businessAuthApply.setCertifiedBusinessStatus(CertifiedBusinessStatus.AUDITING);
        businessAuthApply.setMember(member);
        //不一定会有保证金策略
        if(businessAuthDeposit!=null){
            businessAuthApply.setBusinessAuthDeposit(businessAuthDeposit);
            businessAuthApply.setAmount(businessAuthDeposit.getAmount());
        }
        businessAuthApplyService.create(businessAuthApply);

        member.setCertifiedBusinessApplyTime(new Date());
        member.setCertifiedBusinessStatus(CertifiedBusinessStatus.AUDITING);
        CertifiedBusinessInfo certifiedBusinessInfo = new CertifiedBusinessInfo();
        certifiedBusinessInfo.setCertifiedBusinessStatus(member.getCertifiedBusinessStatus());
        certifiedBusinessInfo.setEmail(member.getEmail());
        certifiedBusinessInfo.setMemberLevel(member.getMemberLevel());
        MessageResult result = MessageResult.success();
        result.setData(certifiedBusinessInfo);
        return result;
    }

    @RequestMapping("/business-auth-deposit/list")
    public MessageResult listBusinessAuthDepositList(){
        List<BusinessAuthDeposit> depositList=businessAuthDepositService.findAllByStatus(CommonStatus.NORMAL);
        depositList.forEach(deposit->{
            deposit.setAdmin(null);
        });
        MessageResult result=MessageResult.success();
        result.setData(depositList);
        return result;
    }

    /**
     * 原来的手机还能用的情况下更换手机
     *
     * @param request
     * @param password
     * @param phone
     * @param code
     * @param user
     * @return
     * @throws Exception
     */
    @RequestMapping("/change/phone")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult changePhone(HttpServletRequest request, String password, String phone, String code, @SessionAttribute(SESSION_MEMBER) AuthMember user) throws Exception {
        Member member = memberService.findOne(user.getId());
        hasText(password, msService.getMessage("MISSING_LOGIN_PASSWORD"));
        hasText(phone, msService.getMessage("MISSING_PHONE"));
        hasText(code, msService.getMessage("MISSING_VERIFICATION_CODE"));
        Member member1 = memberService.findByPhone(phone);
        isTrue(member1 == null, msService.getMessage("PHONE_ALREADY_BOUND"));
        ValueOperations valueOperations = redisTemplate.opsForValue();
        Object cache = valueOperations.get(SysConstant.PHONE_CHANGE_CODE_PREFIX + member.getMobilePhone());
        notNull(cache, msService.getMessage("NO_GET_VERIFICATION_CODE"));
        if ("86".equals(member.getCountry().getAreaCode())) {
            if (!ValidateUtil.isMobilePhone(phone.trim())) {
                return MessageResult.error(msService.getMessage("PHONE_FORMAT_ERROR"));
            }
        }
        if (member.getPassword().equals(Md5.md5Digest(password + member.getSalt()).toLowerCase())) {
            if (!code.equals(cache.toString())) {
                return MessageResult.error(msService.getMessage("VERIFICATION_CODE_INCORRECT"));
            } else {
                valueOperations.getOperations().delete(SysConstant.PHONE_CHANGE_CODE_PREFIX + member.getMobilePhone());
            }
            member.setMobilePhone(phone);
            return MessageResult.success(msService.getMessage("SETTING_SUCCESS"));
        } else {
            request.removeAttribute(SysConstant.SESSION_MEMBER);
            return MessageResult.error(msService.getMessage("PASSWORD_ERROR"));
        }
    }

    /**
     * 申请取消认证商家
     * @return
     */
    @PostMapping("/cancel/business")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult cancelBusiness(@SessionAttribute(SESSION_MEMBER) AuthMember user,
                                        @RequestParam(value = "detail",defaultValue = "")String detail) {
        log.info("user:{}",user);
        Member member=memberService.findOne(user.getId());
        if(member.getCertifiedBusinessStatus()==CANCEL_AUTH){
            return MessageResult.error("退保审核中，请勿重复提交......");
        }
        if(!member.getCertifiedBusinessStatus().equals(CertifiedBusinessStatus.VERIFIED)/*&&
                !member.getCertifiedBusinessStatus().equals(CertifiedBusinessStatus.RETURN_FAILED)*/){
            return MessageResult.error("you are not verified business");
        }

        List<BusinessAuthApply> businessAuthApplyList=businessAuthApplyService.findByMemberAndCertifiedBusinessStatus(member,CertifiedBusinessStatus.VERIFIED);
        if(businessAuthApplyList==null||businessAuthApplyList.size()<1){
            return MessageResult.error("you are not verified business,business auth apply not exist......");
        }

        if(businessAuthApplyList.get(0).getCertifiedBusinessStatus()!=CertifiedBusinessStatus.VERIFIED){
            return MessageResult.error("data exception, state inconsistency(CertifiedBusinessStatus in BusinessAuthApply and Member)");
        }

        member.setCertifiedBusinessStatus(CANCEL_AUTH);
        log.info("会员状态:{}",member.getCertifiedBusinessStatus());
        memberService.save(member);
        log.info("会员状态:{}",member.getCertifiedBusinessStatus());

        BusinessCancelApply cancelApply = new BusinessCancelApply();
        cancelApply.setDepositRecordId(businessAuthApplyList.get(0).getDepositRecordId());
        cancelApply.setMember(businessAuthApplyList.get(0).getMember());
        cancelApply.setStatus(CANCEL_AUTH);
        cancelApply.setReason(detail);
        log.info("退保申请状态:{}",cancelApply.getStatus());
        businessCancelApplyService.save(cancelApply);
        log.info("退保申请状态:{}",cancelApply.getStatus());

        return MessageResult.success();
    }

}
