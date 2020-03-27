package com.bizzan.bitrade.controller.ctc;

import static org.springframework.util.Assert.notNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.bizzan.bitrade.annotation.AccessLog;
import com.bizzan.bitrade.constant.AdminModule;
import com.bizzan.bitrade.constant.PageModel;
import com.bizzan.bitrade.constant.SysConstant;
import com.bizzan.bitrade.constant.TransactionType;
import com.bizzan.bitrade.controller.BaseController;
import com.bizzan.bitrade.entity.Admin;
import com.bizzan.bitrade.entity.CtcAcceptor;
import com.bizzan.bitrade.entity.CtcOrder;
import com.bizzan.bitrade.entity.Member;
import com.bizzan.bitrade.entity.MemberTransaction;
import com.bizzan.bitrade.entity.MemberWallet;
import com.bizzan.bitrade.service.CtcAcceptorService;
import com.bizzan.bitrade.service.CtcOrderService;
import com.bizzan.bitrade.service.LocaleMessageSourceService;
import com.bizzan.bitrade.service.MemberService;
import com.bizzan.bitrade.service.MemberTransactionService;
import com.bizzan.bitrade.service.MemberWalletService;
import com.bizzan.bitrade.util.DateUtil;
import com.bizzan.bitrade.util.MessageResult;
import com.bizzan.bitrade.vendor.provider.SMSProvider;
import com.sparkframework.security.Encrypt;
/**
 * @author Shaoxianjun
 * @description otc币种
 * @date 2019/1/11 13:35
 */
@RestController
@RequestMapping("/ctc/order")
public class AdminCtcOrderController  extends BaseController {
	
	@Autowired
    private CtcOrderService ctcOrderService;
	
	@Autowired
    private MemberWalletService memberWalletService;

    @Autowired
    private MemberTransactionService memberTransactionService;
    
    @Autowired
    private LocaleMessageSourceService messageSource;
    
    @Autowired
	private CtcAcceptorService acceptorService;
    
    @Value("${spark.system.md5.key}")
    private String md5Key;
    
    @Autowired
    private SMSProvider smsProvider;
    
    @Autowired
    private MemberService memberService;
    
	/**
	 * 分页查询
	 * @param pageModel
	 * @return
	 */
	@RequiresPermissions("ctc:order:page-query")
    @PostMapping("page-query")
    @AccessLog(module = AdminModule.CTC, operation = "分页查看CTC订单列表AdminCtcOrderController")
    public MessageResult orderList(PageModel pageModel) {
		if (pageModel.getProperty() == null) {
            List<String> list = new ArrayList<>();
            list.add("createTime");
            List<Sort.Direction> directions = new ArrayList<>();
            directions.add(Sort.Direction.DESC);
            pageModel.setProperty(list);
            pageModel.setDirection(directions);
        }
        Page<CtcOrder> all = ctcOrderService.findAll(null, pageModel.getPageable());
        return success(all);
    }
	
	/**
	 * 订单详情
	 * @param id
	 * @return
	 */
	@RequiresPermissions("ctc:order:order-detail")
    @PostMapping("order-detail")
    @AccessLog(module = AdminModule.CTC, operation = "分页查看CTC订单列表AdminCtcOrderController")
    public MessageResult orderDetail(@RequestParam("id") Long id) {
		if(id == null || id == 0) {
			return error("订单不存在");
		}
		CtcOrder order = ctcOrderService.findOne(id);
		if(order == null) {
			return error("订单不存在");
		}
		return success(order);
	}
	
	/**
	 * 标记付款
	 * @param id
	 * @param status
	 * @return
	 */
	@RequiresPermissions("ctc:order:pay-order")
    @AccessLog(module = AdminModule.CTC, operation = "标记已付款并完成CTC订单")
	@PostMapping("pay-order")
	@Transactional(rollbackFor = Exception.class)
    public MessageResult payOrder(@RequestParam("id") Long id,
    		@RequestParam(value = "password") String password,
            @SessionAttribute(SysConstant.SESSION_ADMIN) Admin admin) {
		
		password = Encrypt.MD5(password + md5Key);
        Assert.isTrue(password.equals(admin.getPassword()), messageSource.getMessage("WRONG_PASSWORD"));
        
        CtcOrder order = ctcOrderService.findOne(id);
        notNull(order, "validate order.id!");
        if(order.getStatus() != 1) {
        	return error("无法对已接单状态之外的状态订单标记付款");
        }
        if(order.getDirection() != 1) {
        	return error("该订单为用户买入，无法标记付款");
        }
        order.setStatus(2);
        order.setPayTime(DateUtil.getCurrentDate());
        ctcOrderService.save(order);
        return success();
    }
	
	/**
	 * 放币(已完成)
	 * @param id
	 * @param status
	 * @return
	 */
	@RequiresPermissions("ctc:order:complete-order")
    @AccessLog(module = AdminModule.CTC, operation = "标记已付款并完成CTC订单")
	@PostMapping("complete-order")
	@Transactional(rollbackFor = Exception.class)
    public MessageResult completeOrder(@RequestParam("id") Long id,
    		@RequestParam(value = "password") String password,
            @SessionAttribute(SysConstant.SESSION_ADMIN) Admin admin) {

		password = Encrypt.MD5(password + md5Key);
        Assert.isTrue(password.equals(admin.getPassword()), messageSource.getMessage("WRONG_PASSWORD"));
        
        CtcOrder order = ctcOrderService.findOne(id);
        notNull(order, "validate order.id!");
        
        if(order.getStatus() != 2) {
        	return error("请确认订单已付款！");
        }
        List<CtcAcceptor> acceptors = acceptorService.findByMember(order.getAcceptor());//findOne(order.getAcceptor().getId());
        if(acceptors.size() != 1) {
        	return error("承兑商映射关系错误！");
        }
        CtcAcceptor acceptor = acceptors.get(0);
        // 买入场景=>用户钱包余额增加
        if(order.getDirection() == 0) {
        	MemberWallet mw = memberWalletService.findByCoinUnitAndMemberId(order.getUnit(), order.getMember().getId());
        	if(mw == null) {
        		return error("用户钱包不存在");
        	}
        	memberWalletService.increaseBalance(mw.getId(), order.getAmount());

			MemberTransaction memberTransaction = new MemberTransaction();
	        memberTransaction.setFee(BigDecimal.ZERO);
	        memberTransaction.setAmount(order.getAmount());
	        memberTransaction.setMemberId(mw.getMemberId());
	        memberTransaction.setSymbol(order.getUnit());
	        memberTransaction.setType(TransactionType.CTC_BUY);
	        memberTransaction.setCreateTime(DateUtil.getCurrentDate());
	        memberTransaction.setRealFee("0");
	        memberTransaction.setDiscountFee("0");
	        memberTransaction= memberTransactionService.save(memberTransaction);
	        
	        // 承兑商账户变更（USDT，CNY）
	        acceptor.setUsdtOut(acceptor.getUsdtOut().add(order.getAmount())); // 售出USDT增加
	        acceptor.setCnyIn(acceptor.getCnyIn().add(order.getMoney())); // 人民币收入增加
	        acceptorService.saveAndFlush(acceptor);
	        
	        Member member = memberService.findOne(order.getMember().getId());
	        try {
				smsProvider.sendCustomMessage(member.getMobilePhone(), "尊敬的用户，您订单号为"
																		+ order.getOrderSn()
																		+ ",买入单价为" + order.getPrice() + "CNY"
																		+ ",数量为"+ order.getAmount() + " USDT"
																		+ ",总价为" + order.getMoney()
																		+ "的订单已由承兑商确认并放币，请您及时查收。");
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
        
        // 卖出场景=>用户钱包冻结资产扣除
        if(order.getDirection() == 1) {
        	MemberWallet mw = memberWalletService.findByCoinUnitAndMemberId(order.getUnit(), order.getMember().getId());
        	if(mw == null) {
        		return error("用户钱包不存在");
        	}
        	if(mw.getFrozenBalance().compareTo(order.getAmount()) < 0) {
        		return error("用户冻结余额不足");
        	}
        	memberWalletService.decreaseFrozen(mw.getId(), order.getAmount());
        	

			MemberTransaction memberTransaction = new MemberTransaction();
	        memberTransaction.setFee(BigDecimal.ZERO);
	        memberTransaction.setAmount(order.getAmount().negate());
	        memberTransaction.setMemberId(mw.getMemberId());
	        memberTransaction.setSymbol(order.getUnit());
	        memberTransaction.setType(TransactionType.CTC_SELL);
	        memberTransaction.setCreateTime(DateUtil.getCurrentDate());
	        memberTransaction.setRealFee("0");
	        memberTransaction.setDiscountFee("0");
	        memberTransaction= memberTransactionService.save(memberTransaction);
	        
	        acceptor.setUsdtIn(acceptor.getUsdtIn().add(order.getAmount())); // 入账USDT增加
	        acceptor.setCnyOut(acceptor.getCnyOut().add(order.getMoney())); // 人民币付出增加
	        acceptorService.saveAndFlush(acceptor);
	        
	        Member member = memberService.findOne(order.getMember().getId());
	        try {
				smsProvider.sendCustomMessage(member.getMobilePhone(), "尊敬的用户，您订单号为"
																		+ order.getOrderSn()
																	    + ",卖出单价为" + order.getPrice() + "CNY"
																		+ ",数量为"+ order.getAmount() + "USDT"
																		+ ",总价为" + order.getMoney()
																		+ "的订单已由承兑商确认并付款，请您及时查收。");
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
        order.setStatus(3);
        order.setCompleteTime(DateUtil.getCurrentDate());
        ctcOrderService.save(order);
        return success();
    }
	
	/**
	 * 接单处理
	 * @param id
	 * @param status
	 * @return
	 */
	@RequiresPermissions("ctc:order:confirm-order")
    @AccessLog(module = AdminModule.CTC, operation = "确认接单")
	@PostMapping("confirm-order")
	@Transactional(rollbackFor = Exception.class)
    public MessageResult confirmOrder(@RequestParam("id") Long id,
    		@RequestParam(value = "password") String password,
            @SessionAttribute(SysConstant.SESSION_ADMIN) Admin admin) {

		password = Encrypt.MD5(password + md5Key);
        Assert.isTrue(password.equals(admin.getPassword()), messageSource.getMessage("WRONG_PASSWORD"));
        
        CtcOrder order = ctcOrderService.findOne(id);
        notNull(order, "validate order.id!");
        if(order.getStatus() != 0) {
        	return error("无法对未接单状态之外的状态订单进行接单");
        }
        order.setStatus(1);
        order.setConfirmTime(DateUtil.getCurrentDate());
        ctcOrderService.save(order);
        
        Member member = memberService.findOne(order.getMember().getId());
        try {
			smsProvider.sendCustomMessage(member.getMobilePhone(), "尊敬的用户，您订单号为"
																	+ order.getOrderSn()
																    + ",单价为" + order.getPrice() + "CNY"
																	+ ",数量为"+ order.getAmount() + "USDT"
																	+ ",总价为" + order.getMoney()
																	+ "的订单已由承兑商接单，请您耐心等待对方付款。");
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        return success();
    }
	
	/**
	 * 强制取消订单
	 * @param id
	 * @param status
	 * @return
	 */
	@RequiresPermissions("ctc:order:cancel-order")
	@PostMapping("cancel-order")
    @AccessLog(module = AdminModule.CTC, operation = "标记已付款并完成CTC订单")
	@Transactional(rollbackFor = Exception.class)
    public MessageResult cancelOrder( @RequestParam("id") Long id,
    		@RequestParam(value = "password") String password,
            @SessionAttribute(SysConstant.SESSION_ADMIN) Admin admin) {

		password = Encrypt.MD5(password + md5Key);
        Assert.isTrue(password.equals(admin.getPassword()), messageSource.getMessage("WRONG_PASSWORD"));
        
        CtcOrder order = ctcOrderService.findOne(id);
        notNull(order, "validate order.id!");

        if(order.getStatus() == 3 || order.getStatus() == 4) {
        	return error("订单状态为已完成或已取消");
        }
        
        if(order.getDirection() == 1) {
        	// 售出订单，需要解冻资产
        	MemberWallet memberWallet = memberWalletService.findByCoinUnitAndMemberId(order.getUnit(), order.getMember().getId());
        	if(memberWallet.getFrozenBalance().compareTo(order.getAmount()) >= 0) {
        		memberWalletService.thawBalance(memberWallet, order.getAmount());
        	}else {
        		return error("用户余额不足以解冻");
        	}
        }
        order.setStatus(4);
        order.setCancelReason("管理员强制取消");
        order.setCancelTime(DateUtil.getCurrentDate());
        ctcOrderService.save(order);
        
        Member member = memberService.findOne(order.getMember().getId());
        try {
			smsProvider.sendCustomMessage(member.getMobilePhone(), "尊敬的用户，您订单号为"
																	+ order.getOrderSn()
																	+ "的订单已被平台强制取消。");
		} catch (Exception e) {
			e.printStackTrace();
		}
        return success("强制取消成功");
    }
}
