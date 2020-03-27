package com.bizzan.bitrade.controller;

import static com.bizzan.bitrade.constant.SysConstant.SESSION_MEMBER;
import static com.bizzan.bitrade.util.BigDecimalUtils.compare;
import static org.springframework.util.Assert.hasText;
import static org.springframework.util.Assert.isTrue;
import static org.springframework.util.Assert.notNull;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONObject;
import com.bizzan.bitrade.constant.BooleanEnum;
import com.bizzan.bitrade.constant.MemberLevelEnum;
import com.bizzan.bitrade.constant.SysConstant;
import com.bizzan.bitrade.entity.CtcAcceptor;
import com.bizzan.bitrade.entity.CtcOrder;
import com.bizzan.bitrade.entity.Member;
import com.bizzan.bitrade.entity.MemberWallet;
import com.bizzan.bitrade.entity.QCtcOrder;
import com.bizzan.bitrade.entity.transform.AuthMember;
import com.bizzan.bitrade.service.CtcAcceptorService;
import com.bizzan.bitrade.service.CtcOrderService;
import com.bizzan.bitrade.service.LocaleMessageSourceService;
import com.bizzan.bitrade.service.MemberService;
import com.bizzan.bitrade.service.MemberWalletService;
import com.bizzan.bitrade.util.DateUtil;
import com.bizzan.bitrade.util.GeneratorUtil;
import com.bizzan.bitrade.util.Md5;
import com.bizzan.bitrade.util.MessageResult;
import com.bizzan.bitrade.vendor.provider.SMSProvider;
import com.querydsl.core.types.Predicate;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

@RestController
@RequestMapping("ctc")
public class CtcController  extends BaseController{
    
	@Autowired
    private CtcOrderService ctcOrderService;
	
	@Autowired
    private CtcAcceptorService ctcAcceptorService;
	
	@Autowired
    private MemberWalletService walletService;
	
	@Autowired
    private MemberService memberService;
	
	@Autowired
    private LocaleMessageSourceService sourceService;
	
	@Autowired
    private MemberWalletService memberWalletService;
	
	@Autowired
    private RedisTemplate redisTemplate;
	@Autowired
    private RestTemplate restTemplate;
	@Autowired
    private SMSProvider smsProvider;
	

	@Autowired
    private JavaMailSender javaMailSender;
	
	@Value("${spring.mail.username}")
    private String from;
    @Value("${spark.system.host}")
    private String host;
    @Value("${spark.system.name}")
    private String company;
    
	@Value("${spark.system.admins}")
    private String admins;
	@Value("${spark.system.admin-phones}")
    private String adminPhones;
	
	/**
	 * 查询用户的订单
	 * @param user
	 * @return
	 */
	@RequestMapping("page-query")
    public MessageResult list(@SessionAttribute(SESSION_MEMBER) AuthMember member, int pageNo, int pageSize) {
		Member m = memberService.findOne(member.getId());
        Page<CtcOrder> orders = ctcOrderService.queryByMember(m, pageNo, pageSize);
        
        return success(orders);
    }
	
	/**
	 * 获取订单详情
	 * @param authMember
	 * @param oid
	 * @return
	 */
	@RequestMapping("detail")
	public MessageResult detailOrder(@SessionAttribute(SESSION_MEMBER) AuthMember authMember, Long oid) {
		if(oid == null || oid == 0) {
			return error("撤销失败，非法的订单号！");
		}
		Member member = memberService.findOne(authMember.getId());
		if(member == null) {
			return error("用户不存在！");
		}
		CtcOrder order = ctcOrderService.findById(oid);
		
		if(order == null) {
			return error("订单不存在！");
		}
		
		if(order.getMember().getId() != member.getId()) {
			return error("非法访问订单！");
		}
		order.setCurrentTime(DateUtil.getCurrentDate());
		return success(order);
	}
	
	@RequestMapping("new-ctc-order")
	@Transactional(rollbackFor = Exception.class)
	public MessageResult add(@SessionAttribute(SESSION_MEMBER) AuthMember authMember,
							 BigDecimal price,
							 BigDecimal amount,
							 String payType,
							 Integer direction,
							 String unit,
							 String fundpwd,
							 @RequestParam("code") String code) throws Exception {
		
		if(price == null || amount == null || payType == null || payType.equals("")) {
			return error("请输入合法参数");
		}
		if(!payType.equals("alipay") && !payType.equals("bank") && !payType.equals("wechatpay")) {
			return error("请选择正确的付款/收款方式");
		}
		
		hasText(unit, sourceService.getMessage("MISSING_COIN_TYPE"));
		
		if(price.compareTo(BigDecimal.ZERO) <= 0) {
			return error("买入/卖出价格错误");
		}
		
		if(amount.compareTo(new BigDecimal(50)) < 0) {
			return error("买入/卖出数量不能低于50");
		}
		
		if(amount.compareTo(new BigDecimal(50000)) > 0) {
			return error("买入/卖出数量不能高于50000");
		}
		
		if(direction.intValue() != 0 && direction.intValue() != 1) {
			return error("非法参数");
		}
		
		// 检查用户账户是否可交易
		Member member=memberService.findOne(authMember.getId());
        if(member.getMemberLevel()== MemberLevelEnum.GENERAL){
        	return error("请先进行实名认证");
        }
        
        //是否被禁止交易
        if(member.getTransactionStatus().equals(BooleanEnum.IS_FALSE)){
        	return error("您的账户无法进行交易");
        }
        // 实名认证检查
        hasText(member.getIdNumber(), sourceService.getMessage("NO_REAL_NAME"));
        // 资产密码检查
        hasText(member.getJyPassword(), sourceService.getMessage("NO_JY_PASSWORD"));
        
        // 用户卖出，如未绑定银行卡，则不通过
        if(direction.intValue() == 1) {
        	if(member.getBankInfo() == null) {
        		return error("请先绑定银行卡！");
        	}
        }
        
        // 资金密码验证
        String mbPassword = member.getJyPassword();
        Assert.hasText(mbPassword, sourceService.getMessage("NO_SET_JYPASSWORD"));
        Assert.isTrue(Md5.md5Digest(fundpwd + member.getSalt()).toLowerCase().equals(mbPassword), sourceService.getMessage("ERROR_JYPASSWORD"));
        
        // 短信验证码验证
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String phone= member.getMobilePhone();
        Object codeRedis =valueOperations.get(SysConstant.PHONE_CTC_TRADE_CODE_PREFIX + phone);
        notNull(codeRedis, sourceService.getMessage("VERIFICATION_CODE_NOT_EXISTS"));
        if (!codeRedis.toString().equals(code)) {
            return error(sourceService.getMessage("VERIFICATION_CODE_INCORRECT"));
        } else {
            valueOperations.getOperations().delete(SysConstant.PHONE_CTC_TRADE_CODE_PREFIX + phone);
        }
        
        if(!unit.equals("USDT")) {
        	return error("非法交易币种");
        }
        
        MemberWallet memberWallet = memberWalletService.findByCoinUnitAndMemberId(unit, authMember.getId());

		// 卖出
		if(direction.intValue() == 1) {
			// 检查余额是否足够
			isTrue(compare(memberWallet.getBalance(), amount), sourceService.getMessage("INSUFFICIENT_BALANCE"));
		}

		// 检查是否有正在进行中的订单
		List<Predicate> conditions = new ArrayList<>() ;
        QCtcOrder qCtcOrder = QCtcOrder.ctcOrder ;
        conditions.add(qCtcOrder.status.lt(3)) ;
        List<CtcOrder> listStatus = ctcOrderService.findAll(conditions);

		if(listStatus.size() > 10) {
			return error("请等待进行中订单完成再继续！");
		}
		
		// 获取承兑商信息
		List<CtcAcceptor> acceptors = ctcAcceptorService.findByStatus(1);
		if(acceptors.size() <= 0) {
			return error("未匹配到合适的承兑商");
		}
		
		Date date = DateUtil.dateAddDay(DateUtil.getCurrentDate(),-1);
        List<Predicate> conditions1 = new ArrayList<>() ;
        QCtcOrder qCtcOrder1 = QCtcOrder.ctcOrder ;
        conditions.add(qCtcOrder1.createTime.gt(date)) ;
        conditions.add(qCtcOrder1.member.eq(member));
        List<CtcOrder> listDay = ctcOrderService.findAll(conditions1);
        
        // 超过20个下单报错
        if(listDay.size() > 20) {
        	//return error("您24H小时内下单数量过多！");
        }
		
		// =====================卖出冻结资产==================== //
		if(direction.intValue() == 1) {
			memberWalletService.freezeBalance(memberWallet, amount);
		}
		// =====================新建订单==================== //
		CtcOrder order = new CtcOrder();
		// 获取USDT价格,设置买入/卖出价格
		String url = "http://bitrade-market/market/exchange-rate/usdtcny";
        ResponseEntity<MessageResult> result = restTemplate.getForEntity(url, MessageResult.class);
        if (result.getStatusCode().value() == 200 && result.getBody().getCode() == 0) {
        	BigDecimal buyPrice = new BigDecimal((String) result.getBody().getData());
        	BigDecimal sellPrice = buyPrice.subtract(buyPrice.multiply(new BigDecimal(0.011)).setScale(2, BigDecimal.ROUND_DOWN));
        	if(direction.intValue() == 0) {
        		// 买入
        		order.setPrice(buyPrice);
        	}else {
        		// 卖出
        		order.setPrice(sellPrice);
        	}
        } else {
        	return error("CTC交易错误，请联系客服或管理员！");
        }
		
		order.setAcceptor(acceptors.get(0).getMember());
		order.setMember(member);
		order.setAmount(amount);
		order.setMoney(amount.multiply(order.getPrice()).setScale(2, BigDecimal.ROUND_DOWN));
		order.setDirection(direction);
		order.setOrderSn(GeneratorUtil.getOrderId("C2C"));
		order.setPayMode(payType);
		order.setUnit(unit);
		
		// 设置付款/收款账户信息
		if(direction.intValue() == 0) {
			// 买入，设置为承兑商账户信息
			if(payType.equals("alipay")) {
				if(acceptors.get(0).getMember().getAlipay() == null) {
					return error("承兑商暂不支持支付宝");
				}
				order.setAlipay(acceptors.get(0).getMember().getAlipay());
			}else if(payType.equals("bank")) {
				if(acceptors.get(0).getMember().getBankInfo() == null) {
					return error("承兑商暂不支持银行卡");
				}
				order.setBankInfo(acceptors.get(0).getMember().getBankInfo());
			}else if(payType.equals("wechatpay")) {
				if(acceptors.get(0).getMember().getWechatPay() == null) {
					return error("承兑商暂不支持微信支付");
				}
				order.setWechatPay(acceptors.get(0).getMember().getWechatPay());
			}
			order.setRealName(acceptors.get(0).getMember().getRealName());
			order.setStatus(1); // 自动接单
			order.setConfirmTime(DateUtil.getCurrentDate());
		}
		if(direction.intValue() == 1) {
			// 卖出，设置为用户账户信息
			if(payType.equals("alipay")) {
				if(member.getAlipay() == null) {
					return error("您尚未绑定支付宝账户信息！");
				}
				order.setAlipay(member.getAlipay());
			}else if(payType.equals("bank")) {
				if(member.getBankInfo() == null) {
					return error("您尚未绑定银行卡信息！");
				}
				order.setBankInfo(member.getBankInfo());
			}else if(payType.equals("wechatpay")) {
				if(member.getWechatPay() == null) {
					return error("您尚未绑定微信支付信息！");
				}
				order.setWechatPay(member.getWechatPay());
			}
			order.setRealName(member.getRealName());
			order.setStatus(0); // 卖出，设置为手动接单
		}
		// 设置时间
		order.setCreateTime(DateUtil.getCurrentDate());
		
		CtcOrder saveResult = ctcOrderService.save(order);
		
		if(saveResult.getId() != null && saveResult.getId() > 0) {
			saveResult.setCurrentTime(DateUtil.getCurrentDate());
			return success(saveResult);
		}else {
			return error("未知错误，下单失败！");
		}
	}
	
	@RequestMapping("cancel-ctc-order")
	@Transactional(rollbackFor = Exception.class)
	public MessageResult cancelOrder(@SessionAttribute(SESSION_MEMBER) AuthMember authMember, Long oid) {
		if(oid == null || oid == 0) {
			return error("撤销失败，非法的订单号！");
		}
		Member member = memberService.findOne(authMember.getId());
		if(member == null) {
			return error("用户不存在！");
		}
		CtcOrder order = ctcOrderService.findById(oid);
		
		if(order == null) {
			return error("订单不存在！");
		}
		
		if(order.getMember().getId() != member.getId()) {
			return error("非法访问订单！");
		}
		
		if(order.getStatus() != 0) {
			// 买入且状态为已接单（用户有权撤销订单）
			if(order.getDirection() == 0 && order.getStatus() == 1) {
				order.setStatus(4);// 撤销状态
				order.setCancelReason("用户自主取消");
				order.setCancelTime(DateUtil.getCurrentDate());
				CtcOrder resultOrder = ctcOrderService.saveAndFlush(order);
				return success(resultOrder);
			}
			// 订单超时情况处理
			return error("无法撤消，订单已被接单并在处理中！");
		}

		if(order.getDirection() == 1) {
			//解冻资产
			MemberWallet memberWallet = memberWalletService.findByCoinUnitAndMemberId(order.getUnit(), member.getId());
			if(memberWallet == null) {
				return error("无法撤销，无法解冻资产误！");
			}
			if(memberWallet.getFrozenBalance().compareTo(order.getAmount()) < 0) {
				return error("无法撤销，无法解冻资产误！");
			}
			memberWalletService.thawBalance(memberWallet, order.getAmount());
		}
		order.setStatus(4);// 撤销状态
		order.setCancelReason("用户自主取消");
		order.setCancelTime(DateUtil.getCurrentDate());
		CtcOrder resultOrder = ctcOrderService.saveAndFlush(order);
		
		resultOrder.setCurrentTime(DateUtil.getCurrentDate());
		
		return success(resultOrder);
	}
	
	/**
	 * 标记已付款
	 * @param authMember
	 * @param oid
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping("pay-ctc-order")
	@Transactional(rollbackFor = Exception.class)
	public MessageResult payOrder(@SessionAttribute(SESSION_MEMBER) AuthMember authMember, Long oid) throws Exception {
		if(oid == null || oid == 0) {
			return error("非法的订单号！");
		}
		Member member = memberService.findOne(authMember.getId());
		if(member == null) {
			return error("用户不存在！");
		}
		CtcOrder order = ctcOrderService.findById(oid);
		
		if(order == null) {
			return error("订单不存在！");
		}
		
		if(order.getMember().getId() != member.getId()) {
			return error("非法访问订单！");
		}
		if(order.getDirection() != 0) {
			return error("无法标记售出订单！");
		}
		if(order.getStatus() != 1) {
			return error("订单状态错误(仅已接单订单可标记付款)！");
		}
		
		order.setStatus(2);//已付款
		order.setPayTime(DateUtil.getCurrentDate());
		CtcOrder orderResult = ctcOrderService.saveAndFlush(order);
		orderResult.setCurrentTime(DateUtil.getCurrentDate());
		
		sendNotification();
		
		return success(orderResult);
	}
	
	/**
	 * 发送用户付款通知
	 */
	@Async
	private void sendNotification() {
		try {
			String[] adminList = admins.split(",");
			for(int i = 0; i < adminList.length; i++) {
				sendEmailMsg(adminList[i], "收到用户付款标记", "用户付款通知");
			}
		} catch (Exception e) {
			MessageResult result;
			try {
				String[] phones = adminPhones.split(",");
				if(phones.length > 0) {
					result = smsProvider.sendSingleMessage(phones[0], "收到用户付款标记");
					if(result.getCode() != 0) {
						if(phones.length > 1) {
							smsProvider.sendSingleMessage(phones[1], "收到用户付款标记");
						}
					}
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
	}
	@Async
    private void sendEmailMsg(String email, String msg, String subject) throws MessagingException, IOException, TemplateException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = null;
        helper = new MimeMessageHelper(mimeMessage, true);
        helper.setFrom(from);
        helper.setTo(email);
        helper.setSubject(company + "-" + subject);
        Map<String, Object> model = new HashMap<>(16);
        model.put("msg", msg);
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_26);
        cfg.setClassForTemplateLoading(this.getClass(), "/templates");
        Template template = cfg.getTemplate("simpleMessage.ftl");
        String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
        helper.setText(html, true);

        //发送邮件
        javaMailSender.send(mimeMessage);
    }
}
