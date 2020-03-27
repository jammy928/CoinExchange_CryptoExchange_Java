package com.bizzan.bitrade.controller;

import com.bizzan.bitrade.constant.LegalWalletState;
import com.bizzan.bitrade.constant.PageModel;
import com.bizzan.bitrade.controller.BaseController;
import com.bizzan.bitrade.controller.screen.LegalWalletScreen;
import com.bizzan.bitrade.entity.*;
import com.bizzan.bitrade.entity.transform.AuthMember;
import com.bizzan.bitrade.service.CoinService;
import com.bizzan.bitrade.service.LegalWalletRechargeService;
import com.bizzan.bitrade.service.MemberService;
import com.bizzan.bitrade.util.BindingResultUtil;
import com.bizzan.bitrade.util.MessageResult;
import com.querydsl.core.types.dsl.BooleanExpression;

import static com.bizzan.bitrade.constant.SysConstant.SESSION_MEMBER;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * 会员充值
 */
@RestController
@RequestMapping("legal-wallet-recharge")
public class LegalWalletRechargeController extends BaseController {
    @Autowired
    private LegalWalletRechargeService legalWalletRechargeService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private CoinService coinService;

    /**
     * 会员充值
     *
     * @param model         参数对象
     * @param bindingResult
     * @param user          登录用户
     * @return
     */
    @PostMapping()
    public MessageResult recharge(
            LegalWalletRechargeModel model,
            BindingResult bindingResult,
            @SessionAttribute(SESSION_MEMBER) AuthMember user) {
        MessageResult result = BindingResultUtil.validate(bindingResult);
        if (result != null) {
            return result;
        }
        Coin coin = coinService.findByUnit(model.getUnit());
        Assert.notNull(coin, "validate coin name!");
        Assert.isTrue(coin.getHasLegal(), "validate coin legal!");
        //登录用户
        Member member = memberService.findOne(user.getId());
        //新建充值
        LegalWalletRecharge legalWalletRecharge = new LegalWalletRecharge();
        legalWalletRecharge.setMember(member);//所属会员
        legalWalletRecharge.setCoin(coin);//充值币种
        legalWalletRecharge.setAmount(model.getAmount());//充值金额
        legalWalletRecharge.setPaymentInstrument(model.getPaymentInstrument());//支付凭证
        legalWalletRecharge.setState(LegalWalletState.APPLYING);//状态
        legalWalletRecharge.setPayMode(model.getPayMode());//支付方式
        legalWalletRecharge.setRemark(model.getRemark());//备注
        legalWalletRechargeService.save(legalWalletRecharge);
        return success();
    }

    /**
     * 待条件分页
     *
     * @param pageModel
     * @param user
     * @return
     */
    @GetMapping()
    public MessageResult page(
            PageModel pageModel,
            LegalWalletScreen screen,
            @SessionAttribute(SESSION_MEMBER) AuthMember user) {
        Coin coin = null;
        if (StringUtils.isNotBlank(screen.getCoinName())) {
            coin = coinService.findOne(screen.getCoinName());
            Assert.notNull(coin, "validate coin name!");
            Assert.isTrue(coin.getHasLegal(), "validate coin legal!");
        }
        BooleanExpression eq = QLegalWalletRecharge.legalWalletRecharge.member.id.eq(user.getId());
        if (coin != null) {
            eq.and(QLegalWalletRecharge.legalWalletRecharge.coin.name.eq(screen.getCoinName()));
        }
        if (screen.getState() != null) {
            eq.and(QLegalWalletRecharge.legalWalletRecharge.state.eq(screen.getState()));
        }
        Page<LegalWalletRecharge> page = legalWalletRechargeService.findAll(eq, pageModel);
        return success(page);
    }

    /**
     * 详情
     *
     * @param user
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public MessageResult get(@SessionAttribute(SESSION_MEMBER) AuthMember user, @PathVariable("id") Long id) {
        LegalWalletRecharge one = legalWalletRechargeService.findOneByIdAndMemberId(id, user.getId());
        Assert.notNull(one, "validate id!");
        return success(one);
    }
}
