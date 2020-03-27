package com.bizzan.bitrade.controller.member;

import com.bizzan.bitrade.constant.PageModel;
import com.bizzan.bitrade.constant.WithdrawStatus;
import com.bizzan.bitrade.controller.common.BaseAdminController;
import com.bizzan.bitrade.entity.LegalWalletWithdraw;
import com.bizzan.bitrade.entity.MemberWallet;
import com.bizzan.bitrade.entity.QLegalWalletWithdraw;
import com.bizzan.bitrade.model.screen.LegalWalletWithdrawScreen;
import com.bizzan.bitrade.service.LegalWalletWithdrawService;
import com.bizzan.bitrade.service.MemberWalletService;
import com.bizzan.bitrade.util.MessageResult;
import com.bizzan.bitrade.util.PredicateUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("legal-wallet-withdraw")
public class LegalWalletWithdrawController extends BaseAdminController {
    @Autowired
    private LegalWalletWithdrawService legalWalletWithdrawService;

    @Autowired
    private MemberWalletService walletService;

    @GetMapping("page")
    public MessageResult page(
            PageModel pageModel,
            LegalWalletWithdrawScreen screen) {
        Predicate predicate = getPredicate(screen);
        Page<LegalWalletWithdraw> page = legalWalletWithdrawService.findAll(predicate, pageModel);
        return success(page);
    }

    @GetMapping("{id}")
    public MessageResult detail(@PathVariable("id") Long id) {
        LegalWalletWithdraw one = legalWalletWithdrawService.findOne(id);
        Assert.notNull(one, "validate id!");
        return success(one);
    }

    //审核通过
    @PatchMapping("{id}/pass")
    public MessageResult pass(@PathVariable("id") Long id) {
        // 校验数据
        LegalWalletWithdraw one = legalWalletWithdrawService.findOne(id);
        Assert.notNull(one, "validate id!");
        Assert.isTrue(one.getStatus() == WithdrawStatus.PROCESSING, "审核已结束!");
        //审核通过
        legalWalletWithdrawService.pass(one);
        return success();
    }

    //审核不通过
    @PatchMapping("{id}/no-pass")
    public MessageResult noPass(@PathVariable("id") Long id) {
        //校验 提现
        LegalWalletWithdraw one = legalWalletWithdrawService.findOne(id);
        Assert.notNull(one, "validate id!");
        Assert.isTrue(one.getStatus() == WithdrawStatus.PROCESSING, "审核已结束!");
        //校验钱包
        MemberWallet wallet = walletService.findByCoinAndMember(one.getCoin(), one.getMember());
        Assert.notNull(wallet, "wallet null!");
        //不通过 修改钱包 提现单状态
        legalWalletWithdrawService.noPass(wallet, one);
        return success(one);
    }

    //确认打款 即上传打款凭证
    @PatchMapping("{id}/remit")
    public MessageResult remit(
            @PathVariable("id") Long id,
            @RequestParam("paymentInstrument") String paymentInstrument) {
        //校验提现单
        LegalWalletWithdraw one = legalWalletWithdrawService.findOne(id);
        Assert.notNull(one, "validate id!");
        Assert.isTrue(one.getStatus() == WithdrawStatus.WAITING, "打款已结束!");
        //校验钱包
        MemberWallet wallet = walletService.findByCoinAndMember(one.getCoin(), one.getMember());
        Assert.notNull(wallet, "wallet null!");
        //打款操作
        legalWalletWithdrawService.remit(paymentInstrument, one, wallet);
        return success(one);
    }


    //条件
    private Predicate getPredicate(LegalWalletWithdrawScreen screen) {
        ArrayList<BooleanExpression> booleanExpressions = new ArrayList<>();
        if (StringUtils.isNotBlank(screen.getUsername())) {
            booleanExpressions.add(QLegalWalletWithdraw.legalWalletWithdraw.member.username.eq(screen.getUsername()));
        }
        if (screen.getStatus() != null) {
            booleanExpressions.add(QLegalWalletWithdraw.legalWalletWithdraw.status.eq(screen.getStatus()));
        }
        if (StringUtils.isNotBlank(screen.getCoinName())) {
            booleanExpressions.add(QLegalWalletWithdraw.legalWalletWithdraw.coin.name.eq(screen.getCoinName()));
        }
        return PredicateUtils.getPredicate(booleanExpressions);
    }
}
