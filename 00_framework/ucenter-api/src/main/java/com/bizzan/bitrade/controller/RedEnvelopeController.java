package com.bizzan.bitrade.controller;

import static com.bizzan.bitrade.constant.SysConstant.SESSION_MEMBER;
import static com.bizzan.bitrade.util.MessageResult.error;
import static com.bizzan.bitrade.util.MessageResult.success;
import static org.springframework.util.Assert.isTrue;
import static org.springframework.util.Assert.notNull;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.shiro.util.Assert;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.bizzan.bitrade.annotation.AccessLog;
import com.bizzan.bitrade.constant.AdminModule;
import com.bizzan.bitrade.constant.CommonStatus;
import com.bizzan.bitrade.constant.MemberLevelEnum;
import com.bizzan.bitrade.constant.SysConstant;
import com.bizzan.bitrade.constant.TransactionType;
import com.bizzan.bitrade.entity.Coin;
import com.bizzan.bitrade.entity.Country;
import com.bizzan.bitrade.entity.Location;
import com.bizzan.bitrade.entity.Member;
import com.bizzan.bitrade.entity.MemberTransaction;
import com.bizzan.bitrade.entity.MemberWallet;
import com.bizzan.bitrade.entity.RedEnvelope;
import com.bizzan.bitrade.entity.RedEnvelopeDetail;
import com.bizzan.bitrade.entity.transform.AuthMember;
import com.bizzan.bitrade.event.MemberEvent;
import com.bizzan.bitrade.service.CoinService;
import com.bizzan.bitrade.service.CountryService;
import com.bizzan.bitrade.service.LocaleMessageSourceService;
import com.bizzan.bitrade.service.MemberPromotionService;
import com.bizzan.bitrade.service.MemberService;
import com.bizzan.bitrade.service.MemberTransactionService;
import com.bizzan.bitrade.service.MemberWalletService;
import com.bizzan.bitrade.service.RedEnvelopeDetailService;
import com.bizzan.bitrade.service.RedEnvelopeService;
import com.bizzan.bitrade.util.BigDecimalUtils;
import com.bizzan.bitrade.util.DateUtil;
import com.bizzan.bitrade.util.GeneratorUtil;
import com.bizzan.bitrade.util.IdWorkByTwitter;
import com.bizzan.bitrade.util.Md5;
import com.bizzan.bitrade.util.MessageResult;
import com.bizzan.bitrade.util.ValidateUtil;
import com.bizzan.bitrade.vendor.provider.SMSProvider;
import com.bizzan.bitrade.vo.MemberPromotionStasticVO;

/**
 * 红包
 *
 * @author GS
 * @date 2018年03月19日
 */
@RestController
@RequestMapping(value = "/redenvelope")
public class RedEnvelopeController extends BaseController{
	
	@Autowired
	private RedEnvelopeService redEnvelopeService;
	
	@Autowired
	private RedEnvelopeDetailService redEnvelopeDetailService;
	
	@Autowired
    private MemberPromotionService memberPromotionService;
	
    @Autowired
    private MemberService memberService;
    
    @Autowired
    private MemberWalletService memberWalletService;

    @Autowired
    private MemberTransactionService memberTransactionService;
    
    @Resource
    private LocaleMessageSourceService localeMessageSourceService;
    
    @Autowired
    private CoinService coinService;
    
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IdWorkByTwitter idWorkByTwitter;
    
    @Autowired
    private MemberEvent memberEvent;
    
    @Autowired
    private SMSProvider smsProvider;
    
    @Autowired
    private CountryService countryService;
    
    private Random rand = new Random();
    
	/**
     * 查看红包详情（根据红包编号查询，注意，不是ID）
     * @param envelopeNo
     * @return
     */
    @RequestMapping(value = "/query")
    private MessageResult envelopeDetail(@RequestParam(value = "envelopeNo", defaultValue = "") String envelopeNo,
    		@RequestParam(value = "code", defaultValue = "") String code) {
    	Assert.notNull(envelopeNo, "无效的红包！");
    	RedEnvelope redEnvelope = redEnvelopeService.findByEnvelopeNo(envelopeNo);
    	Assert.notNull(redEnvelope, "无效的红包！");
    	if(StringUtils.hasText(code)) {
    		Member member = memberService.findMemberByPromotionCode(code);
    		if(member != null) {
    			if(StringUtils.hasText(member.getMobilePhone())) {
    				redEnvelope.setInviteUser(member.getMobilePhone().substring(0, 3) + "****" + member.getMobilePhone().substring(7, member.getMobilePhone().length()));
    			}
    			redEnvelope.setInviteUserAvatar(member.getAvatar());
    		}
    	}
    	return success(redEnvelope);
    }
    
    /**
     * 红包领取详情
     * @param envelopeId
     * @param pageNo
     * @param pageSize
     * @return
     */
    @PostMapping("/query-detail")
    @AccessLog(module = AdminModule.REDENVELOPE, operation = "查看红包领取详情RedEnvelopeController")
    public MessageResult envelopeDetailList(@RequestParam(value = "envelopeId", defaultValue = "0") Long envelopeId,
    		@RequestParam(value = "pageNo", defaultValue = "0") Integer pageNo,
    		@RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
    	
    	Page<RedEnvelopeDetail> detailList = redEnvelopeDetailService.findByEnvelope(envelopeId, pageNo, pageSize);
    	for(int i = 0; i < detailList.getContent().size(); i++) {
    		detailList.getContent().get(i).setUserIdentify(detailList.getContent().get(i).getUserIdentify().substring(0, 3) 
    				+ "****"
    				+ detailList.getContent().get(i).getUserIdentify().substring(7, 11) );
    	}
        return success(detailList);
    }
    
    /**
     * 获取用户发出的所有钱包
     * @param envelopeNo
     * @return
     */
    @RequestMapping(value = "/myenvelope")
    private MessageResult envelopeList(@SessionAttribute(SESSION_MEMBER) AuthMember member,
    		@RequestParam(value = "pageNo", defaultValue = "0") Integer pageNo,
    		@RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
    	
    	Assert.notNull(member, "无效的用户！");
    	Page<RedEnvelope> redEnvelopeList = redEnvelopeService.findByMember(member.getId(), pageNo.intValue(), pageSize.intValue());
    	Assert.notNull(redEnvelopeList, "没有红包！");
    	
    	return success(redEnvelopeList);
    }
    
    /**
     * 我领取到的红包
     * @param member
     * @param envelopeNo
     * @return
     */
    @RequestMapping(value = "/myreceive")
    private MessageResult envelopeList(@SessionAttribute(SESSION_MEMBER) AuthMember member,
    		@RequestParam(value = "envelopeNo", defaultValue = "") String envelopeNo) {
    	
    	Assert.notNull(envelopeNo, "无效的红包！");
    	RedEnvelope redEnvelope = redEnvelopeService.findByEnvelopeNo(envelopeNo);
    	Assert.notNull(redEnvelope, "无效的红包！");
    	
    	// 查询用户是否存在
    	Member authMember = memberService.findOne(member.getId());
    	Assert.notNull(authMember, "非法操作!");
    	
    	List<RedEnvelopeDetail> detailList = redEnvelopeDetailService.findByEnvelopeIdAndMemberId(redEnvelope.getId(), member.getId());
    	
    	return success(detailList);
    }
    
    /**
     * 剩余领取次数（仅登录用户能查看到）
     * @param member
     * @param envelopeNo
     * @return
     */
    @RequestMapping(value = "/lefttimes")
    private MessageResult leftTimes(@SessionAttribute(SESSION_MEMBER) AuthMember member,
    		@RequestParam(value = "envelopeNo", defaultValue = "") String envelopeNo) {
    	int times = 0;
    	// 查询红包是否存在
    	Assert.notNull(envelopeNo, "无效的红包！");
    	RedEnvelope redEnvelope = redEnvelopeService.findByEnvelopeNo(envelopeNo);
    	Assert.notNull(redEnvelope, "无效的红包！");
    	
    	// 查询用户是否存在
    	Member authMember = memberService.findOne(member.getId());
    	Assert.notNull(authMember, "非法操作!");
    	
    	if(redEnvelope.getState() == 1) {
    		return error("该红包已被领完");
    	}
    	
    	if(redEnvelope.getState() == 2) {
    		return error("该红包已过期");
    	}
    	
    	if(redEnvelope.getState() == 0) {
	    	// 判断红包是否过期(过期有两种可能：1、过期了但是已经被领取完；2：过期了红包还有剩余没有被领取
	    	long currentTime = Calendar.getInstance().getTimeInMillis(); // 当前时间戳
	    	if(currentTime >= (redEnvelope.getCreateTime().getTime() + redEnvelope.getExpiredHours() * 60 * 60 * 1000)) {
	    		if(redEnvelope.getReceiveCount() < redEnvelope.getCount()) {
	    			return error("该红包已过期");
		    	}else {
		    		return error("该红包已被领完");
		    	}
	    	}
	    	
	    	// 判断红包是否已被领取完
	    	if(redEnvelope.getReceiveCount() >= redEnvelope.getCount()) {
    			return error("该红包已被领完");
	    	}
    	}
    	
    	// 判断是否领取过红包（邀请加一次领取机会的处理逻辑）
    	// 0: 邀请不增加领取机会
    	// 1: 邀请增加领取机会
    	List<RedEnvelopeDetail> detailList = redEnvelopeDetailService.findByEnvelopeIdAndMemberId(redEnvelope.getId(), member.getId());
    	if(detailList != null && detailList.size() > 0) {
    		if(redEnvelope.getInvite() == 0) {
    			return success(0);
    		}
	    	if(redEnvelope.getInvite() == 1) {
	    		// 查看邀请记录
	    		Date endDate = new Date();
	    		List<MemberPromotionStasticVO> inviteList = memberPromotionService.getDateRangeRank(0, redEnvelope.getCreateTime(), endDate, 10000); // 此处10000表示邀请人数limit
	    		if(inviteList.size() <= detailList.size()) {
	    			return success(0);
	    		}else {
	    			return success(inviteList.size() - detailList.size());
	    		}
	    	}
    	}else {
    		if(redEnvelope.getInvite() == 0) {
    			return success(1);
    		}
	    	if(redEnvelope.getInvite() == 1) {
	    		// 查看邀请记录
	    		Date endDate = new Date();
	    		List<MemberPromotionStasticVO> inviteList = memberPromotionService.getDateRangeRank(0, redEnvelope.getCreateTime(), endDate, 10000); // 此处10000表示邀请人数limit
	    		if(inviteList.size() == 0) {
	    			return success(1); // 从未领取过并且没有邀请过人
	    		}else {
	    			return success(inviteList.size() + 1); // 从未领取过并且期间邀请了人
	    		}
	    	}
    	}
    	return success(0);
    }
    /**
     * 登录用户领取（一般在红包中心、APP红包中心领取）
     * @param member
     * @param pageNo
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/receivelogin")
    private MessageResult receiveEnvelopeLogin(@SessionAttribute(SESSION_MEMBER) AuthMember member,
    		@RequestParam(value = "envelopeNo", defaultValue = "") String envelopeNo) {
    	
    	// 查询红包是否存在
    	Assert.notNull(envelopeNo, "无效的红包！");
    	RedEnvelope redEnvelope = redEnvelopeService.findByEnvelopeNo(envelopeNo);
    	Assert.notNull(redEnvelope, "无效的红包！");
    	
    	// 查询用户是否存在
    	Member authMember = memberService.findOne(member.getId());
    	Assert.notNull(authMember, "非法操作!");
    	
    	if(redEnvelope.getState() == 1) {
    		return error("该红包已被领完");
    	}
    	
    	if(redEnvelope.getState() == 2) {
    		return error("该红包已过期");
    	}
    	
    	if(redEnvelope.getState() == 0) {
	    	// 判断红包是否过期(过期有两种可能：1、过期了但是已经被领取完；2：过期了红包还有剩余没有被领取
	    	long currentTime = Calendar.getInstance().getTimeInMillis(); // 当前时间戳
	    	if(currentTime >= (redEnvelope.getCreateTime().getTime() + redEnvelope.getExpiredHours() * 60 * 60 * 1000)) {
	    		if(redEnvelope.getReceiveCount() < redEnvelope.getCount()) {
	    			return error("该红包已过期");
		    	}else {
		    		return error("该红包已被领完");
		    	}
	    	}
	    	
	    	// 判断红包是否已被领取完
	    	if(redEnvelope.getReceiveCount() >= redEnvelope.getCount()) {
    			return error("该红包已被领完");
	    	}
    	}
    	
    	// 判断是否领取过红包（邀请加一次领取机会的处理逻辑）
    	// 0: 邀请不增加领取机会
    	// 1: 邀请增加领取机会
    	List<RedEnvelopeDetail> detailList = redEnvelopeDetailService.findByEnvelopeIdAndMemberId(redEnvelope.getId(), member.getId());
    	if(detailList != null && detailList.size() > 0) {
    		if(redEnvelope.getInvite() == 0) {
    			return error("已领取过该红包");
    		}
	    	if(redEnvelope.getInvite() == 1) {
	    		// 查看邀请记录
	    		Date endDate = new Date();
	    		List<MemberPromotionStasticVO> inviteList = memberPromotionService.getDateRangeRank(0, redEnvelope.getCreateTime(), endDate, 10000); // 此处10000表示邀请人数limit
	    		if(inviteList.size() <= detailList.size()) {
	    			return error("尚无新邀请的好友，无法领取红包");
	    		}
	    	}
    	}
    	
    	// 领取红包业务执行部分
    	// 1、生成红包金额
    	BigDecimal redAmount = BigDecimal.ZERO;
    	
    	if(redEnvelope.getType() == 0) {
    		// 随机红包
    		BigDecimal leftAmount = redEnvelope.getTotalAmount().subtract(redEnvelope.getReceiveAmount());
    		if(redEnvelope.getCount() - redEnvelope.getReceiveCount() == 1) {
    			// 最后一个，则为剩余金额
    			redAmount = leftAmount;
    		}else {
	    		// 如果最大领取数量大于剩余数量，则以剩余数量作为随机数字
	    		BigDecimal randSeed = redEnvelope.getMaxRand().compareTo(leftAmount) > 0 ? leftAmount : redEnvelope.getMaxRand();
	    		redAmount = randSeed.divide( new BigDecimal(rand.nextInt(1000) + 2), 6, BigDecimal.ROUND_HALF_DOWN); // 生成随机红包
    		}
    	}else if(redEnvelope.getType() == 1){
    		// 定额红包
    		redAmount = redEnvelope.getTotalAmount().divide(new BigDecimal(redEnvelope.getCount()), 6, BigDecimal.ROUND_HALF_DOWN);
    	}
    	
    	// 超出红包总额
    	if(redAmount.add(redEnvelope.getReceiveAmount()).compareTo(redEnvelope.getTotalAmount()) > 0) {
    		return error("领取红包失败，请重试！");
    	}
    	
    	// 增加用户资产
        MemberWallet memberWallet = memberWalletService.findByCoinUnitAndMemberId(redEnvelope.getUnit(), member.getId());
        Assert.notNull(memberWallet, "钱包错误！请联系管理员！");
        memberWallet.setBalance(memberWallet.getBalance().add(redAmount));

        // 添加资产记录
        MemberTransaction memberTransaction = new MemberTransaction();
        memberTransaction.setFee(BigDecimal.ZERO);
        memberTransaction.setAmount(redAmount);
        memberTransaction.setMemberId(memberWallet.getMemberId());
        memberTransaction.setSymbol(redEnvelope.getUnit());
        memberTransaction.setType(TransactionType.RED_IN);
        memberTransaction.setCreateTime(DateUtil.getCurrentDate());
        memberTransaction.setRealFee("0");
        memberTransaction.setDiscountFee("0");
        memberTransaction= memberTransactionService.save(memberTransaction);
        
        // 添加红包领取记录
        RedEnvelopeDetail detailRecord = new RedEnvelopeDetail();
        detailRecord.setAmount(redAmount);
        detailRecord.setCate(0); // 非机器人领取，如果标记为机器人，此值请设置为大于0
        detailRecord.setMemberId(member.getId());
        detailRecord.setEnvelopeId(redEnvelope.getId());
        detailRecord.setCreateTime(DateUtil.getCurrentDate());
        detailRecord.setUserIdentify(member.getMobilePhone());
        detailRecord.setCate(0); // 非机器人
        redEnvelopeDetailService.save(detailRecord);
        
        // 更新红包主表
        redEnvelope.setReceiveCount(redEnvelope.getReceiveCount() + 1);
        redEnvelope.setReceiveAmount(redEnvelope.getReceiveAmount().add(redAmount));
        redEnvelopeService.saveAndFlush(redEnvelope);
        
    	return success(detailRecord);
    }
    
    /**
     * 领取红包(推广页面领取——输入手机号，验证码领取)
     * @param member
     * @param envelopeNo
     * @return
     * @throws Exception 
     */
    @RequestMapping(value = "/receive")
    @Transactional(rollbackFor = Exception.class)
    private MessageResult receiveEnvelope(String phone, String verifyCode, String promotionCode,
    								   @RequestParam(value = "envelopeNo", defaultValue = "") String envelopeNo) throws Exception {
    	Member member = null;
    	if(!memberService.phoneIsExist(phone)) {
	    	ValueOperations valueOperations = redisTemplate.opsForValue();
	        Object code =valueOperations.get(SysConstant.PHONE_RECEIVE_ENVELOPE_PREFIX + phone);
	        isTrue(!memberService.phoneIsExist(phone), localeMessageSourceService.getMessage("PHONE_ALREADY_EXISTS"));
	        isTrue(!memberService.usernameIsExist(phone), localeMessageSourceService.getMessage("USERNAME_ALREADY_EXISTS"));
	        if (StringUtils.hasText(promotionCode.trim())) {
	        	isTrue(memberService.userPromotionCodeIsExist(promotionCode), localeMessageSourceService.getMessage("USER_PROMOTION_CODE_EXISTS"));        	
	        }
	        // 短信验证码是否存在
	        notNull(code, localeMessageSourceService.getMessage("VERIFICATION_CODE_NOT_EXISTS"));
	        if (!code.toString().equals(verifyCode)) {
	            return error(localeMessageSourceService.getMessage("VERIFICATION_CODE_INCORRECT"));
	        } else {
	            valueOperations.getOperations().delete(SysConstant.PHONE_RECEIVE_ENVELOPE_PREFIX + phone);
	        }
	        // 不可重复随机数
	        String loginNo = String.valueOf(idWorkByTwitter.nextId());
	        // 盐
	        String credentialsSalt = ByteSource.Util.bytes(loginNo).toHex();
	        // 生成密码
	        String generatePWD = phone.substring(7, 11) + GeneratorUtil.getNonceString(4).toLowerCase(); // 手机尾号4位 + 4位随机字符串
	        smsProvider.sendCustomMessage(phone, "恭喜您注册成功！默认登录密码：" + generatePWD + ",请登录后修改!");
	        String password = Md5.md5Digest(generatePWD + credentialsSalt).toLowerCase();
	        Member member1 = new Member();
	        member1.setStatus(CommonStatus.NORMAL);
	        member1.setMemberLevel(MemberLevelEnum.GENERAL);
	        Location location = new Location();
	        location.setCountry("中国");
	        Country country = new Country();
	        country.setZhName("中国");
	        member1.setCountry(country);
	        member1.setLocation(location);
	        member1.setUsername(phone);
	        member1.setPassword(password);
	        member1.setMobilePhone(phone);
	        member1.setSalt(credentialsSalt);
	        member1.setAvatar("https://bizzan.oss-cn-hangzhou.aliyuncs.com/defaultavatar.png");
	        member = memberService.save(member1);
	        if (member != null) {
	        	// Member为@entity注解类，与数据库直接映射，因此，此处setPromotionCode会直接同步到数据库
	        	member.setPromotionCode(GeneratorUtil.getPromotionCode(member.getId()));
	            memberEvent.onRegisterSuccess(member, promotionCode.trim());
	        } else {
	            return error(localeMessageSourceService.getMessage("REGISTRATION_FAILED"));
	        }
    	}else {
    		ValueOperations valueOperations = redisTemplate.opsForValue();
	        Object code = valueOperations.get(SysConstant.PHONE_RECEIVE_ENVELOPE_PREFIX + phone);
    		// 短信验证码是否存在
	        notNull(code, localeMessageSourceService.getMessage("VERIFICATION_CODE_NOT_EXISTS"));
	        if (!code.toString().equals(verifyCode)) {
	            return error(localeMessageSourceService.getMessage("VERIFICATION_CODE_INCORRECT"));
	        } else {
	            valueOperations.getOperations().delete(SysConstant.PHONE_RECEIVE_ENVELOPE_PREFIX + phone);
	        }
    		member = memberService.findByPhone(phone);
    		if(member == null) {
    			return error("用户不存在！");
    		}
    	}
    	
    	// 查询红包是否存在
    	Assert.notNull(envelopeNo, "无效的红包！");
    	RedEnvelope redEnvelope = redEnvelopeService.findByEnvelopeNo(envelopeNo);
    	Assert.notNull(redEnvelope, "无效的红包！");
    	
    	// 查询用户是否存在
    	Member authMember = memberService.findOne(member.getId());
    	Assert.notNull(authMember, "非法操作!");
    	
    	Coin coin = coinService.findByUnit(redEnvelope.getUnit());
    	Assert.notNull(redEnvelope, "红包币种不存在！");
    	
    	if(redEnvelope.getState() == 1) {
    		return error("该红包已被领完");
    	}
    	
    	if(redEnvelope.getState() == 2) {
    		return error("该红包已过期");
    	}
    	
    	if(redEnvelope.getState() == 0) {
	    	// 判断红包是否过期(过期有两种可能：1、过期了但是已经被领取完；2：过期了红包还有剩余没有被领取
	    	long currentTime = Calendar.getInstance().getTimeInMillis(); // 当前时间戳
	    	if(currentTime >= (redEnvelope.getCreateTime().getTime() + redEnvelope.getExpiredHours() * 60 * 60 * 1000)) {
	    		if(redEnvelope.getReceiveCount() < redEnvelope.getCount()) {
	    			return error("该红包已过期");
		    	}else {
		    		return error("该红包已被领完");
		    	}
	    	}
	    	
	    	// 判断红包是否已被领取完
	    	if(redEnvelope.getReceiveCount() >= redEnvelope.getCount()) {
    			return error("该红包已被领完");
	    	}
    	}
    	
    	// 判断是否领取过红包（邀请加一次领取机会的处理逻辑）
    	// 0: 邀请不增加领取机会
    	// 1: 邀请增加领取机会
    	List<RedEnvelopeDetail> detailList = redEnvelopeDetailService.findByEnvelopeIdAndMemberId(redEnvelope.getId(), member.getId());
    	if(detailList != null && detailList.size() > 0) {
    		if(redEnvelope.getInvite() == 0) {
    			return error("已领取过该红包");
    		}
	    	if(redEnvelope.getInvite() == 1) {
	    		// 查看邀请记录
	    		Date endDate = new Date();
	    		List<MemberPromotionStasticVO> inviteList = memberPromotionService.getDateRangeRank(0, redEnvelope.getCreateTime(), endDate, 10000); // 此处10000表示邀请人数limit
	    		if(inviteList.size() <= detailList.size()) {
	    			return error("尚无新邀请的好友，无法领取红包");
	    		}
	    	}
    	}
    	
    	// 领取红包业务执行部分
    	// 1、生成红包金额
    	BigDecimal redAmount = BigDecimal.ZERO;
    	
    	if(redEnvelope.getType() == 0) {
    		// 随机红包
    		BigDecimal leftAmount = redEnvelope.getTotalAmount().subtract(redEnvelope.getReceiveAmount());
    		if(redEnvelope.getCount() - redEnvelope.getReceiveCount() == 1) {
    			// 最后一个，则为剩余金额
    			redAmount = leftAmount;
    		}else {
	    		// 如果最大领取数量大于剩余数量，则以剩余数量作为随机数字
	    		BigDecimal randSeed = redEnvelope.getMaxRand().compareTo(leftAmount) > 0 ? leftAmount : redEnvelope.getMaxRand();
	    		redAmount = randSeed.divide( new BigDecimal(rand.nextInt(1000) + 2), 6, BigDecimal.ROUND_HALF_DOWN); // 生成随机红包
    		}
    	}else if(redEnvelope.getType() == 1){
    		// 定额红包
    		redAmount = redEnvelope.getTotalAmount().divide(new BigDecimal(redEnvelope.getCount()), 6, BigDecimal.ROUND_HALF_DOWN);
    	}
    	
    	// 超出红包总额
    	if(redAmount.add(redEnvelope.getReceiveAmount()).compareTo(redEnvelope.getTotalAmount()) > 0) {
    		return error("领取红包失败，请重试！");
    	}
    	
    	// 增加用户资产
        MemberWallet memberWallet = memberWalletService.findByCoinUnitAndMemberId(redEnvelope.getUnit(), member.getId());
        Assert.notNull(memberWallet, "钱包错误！请联系管理员！");
        memberWallet.setBalance(memberWallet.getBalance().add(redAmount));

        // 添加资产记录
        MemberTransaction memberTransaction = new MemberTransaction();
        memberTransaction.setFee(BigDecimal.ZERO);
        memberTransaction.setAmount(redAmount);
        memberTransaction.setMemberId(memberWallet.getMemberId());
        memberTransaction.setSymbol(redEnvelope.getUnit());
        memberTransaction.setType(TransactionType.RED_IN);
        memberTransaction.setCreateTime(DateUtil.getCurrentDate());
        memberTransaction.setRealFee("0");
        memberTransaction.setDiscountFee("0");
        memberTransaction= memberTransactionService.save(memberTransaction);
        
        // 添加红包领取记录
        RedEnvelopeDetail detailRecord = new RedEnvelopeDetail();
        detailRecord.setAmount(redAmount);
        detailRecord.setCate(0); // 非机器人领取，如果标记为机器人，此值请设置为大于0
        detailRecord.setMemberId(member.getId());
        detailRecord.setEnvelopeId(redEnvelope.getId());
        detailRecord.setCreateTime(DateUtil.getCurrentDate());
        detailRecord.setUserIdentify(member.getMobilePhone());
        detailRecord.setCate(0); // 非机器人
        redEnvelopeDetailService.save(detailRecord);
        
        detailRecord.setPromotionCode(member.getPromotionCode()); // 设置推广码
        
        // 更新红包主表
        redEnvelope.setReceiveCount(redEnvelope.getReceiveCount() + 1);
        redEnvelope.setReceiveAmount(redEnvelope.getReceiveAmount().add(redAmount));
        redEnvelopeService.saveAndFlush(redEnvelope);
        
    	return success(detailRecord);
    }
    
    /**
     * 领取红包(模拟领取-机器人专用)
     * @param member
     * @param envelopeNo
     * @return
     * @throws Exception 
     */
    @RequestMapping(value = "/mockreceivedudalsldds")
    @Transactional(rollbackFor = Exception.class)
    private MessageResult receiveEnvelopeMock(@RequestParam(value = "sign", defaultValue = "") String sign,
    										  @RequestParam(value = "memberId", defaultValue = "") Long memberId,
    								   		  @RequestParam(value = "envelopeNo", defaultValue = "") String envelopeNo) throws Exception {
    	
    	if(!sign.equals("987654321asdf")) {
        	return error("非法请求");
        }
    	
    	// 查询红包是否存在
    	Assert.notNull(envelopeNo, "无效的红包！");
    	RedEnvelope redEnvelope = redEnvelopeService.findByEnvelopeNo(envelopeNo);
    	Assert.notNull(redEnvelope, "无效的红包！");
    	
    	// 查询用户是否存在
    	Member authMember = memberService.findOne(memberId);
    	Assert.notNull(authMember, "非法操作!");
    	
    	if(redEnvelope.getState() == 1) {
    		return error("该红包已被领完");
    	}
    	
    	if(redEnvelope.getState() == 2) {
    		return error("该红包已过期");
    	}
    	
    	if(redEnvelope.getState() == 0) {
	    	// 判断红包是否过期(过期有两种可能：1、过期了但是已经被领取完；2：过期了红包还有剩余没有被领取
	    	long currentTime = Calendar.getInstance().getTimeInMillis(); // 当前时间戳
	    	if(currentTime >= (redEnvelope.getCreateTime().getTime() + redEnvelope.getExpiredHours() * 60 * 60 * 1000)) {
	    		if(redEnvelope.getReceiveCount() < redEnvelope.getCount()) {
	    			return error("该红包已过期");
		    	}else {
		    		return error("该红包已被领完");
		    	}
	    	}
	    	
	    	// 判断红包是否已被领取完
	    	if(redEnvelope.getReceiveCount() >= redEnvelope.getCount()) {
    			return error("该红包已被领完");
	    	}
    	}
    	
    	// 判断是否领取过红包（邀请加一次领取机会的处理逻辑）
    	// 0: 邀请不增加领取机会
    	// 1: 邀请增加领取机会
    	List<RedEnvelopeDetail> detailList = redEnvelopeDetailService.findByEnvelopeIdAndMemberId(redEnvelope.getId(), memberId);
    	if(detailList != null && detailList.size() > 0) {
    		if(redEnvelope.getInvite() == 0) {
    			return error("已领取过该红包");
    		}
	    	if(redEnvelope.getInvite() == 1) {
	    		// 查看邀请记录
	    		Date endDate = new Date();
	    		List<MemberPromotionStasticVO> inviteList = memberPromotionService.getDateRangeRank(0, redEnvelope.getCreateTime(), endDate, 10000); // 此处10000表示邀请人数limit
	    		if(inviteList.size() <= detailList.size()) {
	    			return error("尚无新邀请的好友，无法领取红包");
	    		}
	    	}
    	}
    	
    	// 领取红包业务执行部分
    	// 1、生成红包金额
    	BigDecimal redAmount = BigDecimal.ZERO;
    	
    	if(redEnvelope.getType() == 0) {
    		// 随机红包
    		BigDecimal leftAmount = redEnvelope.getTotalAmount().subtract(redEnvelope.getReceiveAmount());
    		if(redEnvelope.getCount() - redEnvelope.getReceiveCount() == 1) {
    			// 最后一个，则为剩余金额
    			redAmount = leftAmount;
    		}else {
	    		// 如果最大领取数量大于剩余数量，则以剩余数量作为随机数字
	    		BigDecimal randSeed = redEnvelope.getMaxRand().compareTo(leftAmount) > 0 ? leftAmount : redEnvelope.getMaxRand();
	    		redAmount = randSeed.divide( new BigDecimal(rand.nextInt(1000) + 2), 6, BigDecimal.ROUND_HALF_DOWN); // 生成随机红包
    		}
    	}else if(redEnvelope.getType() == 1){
    		// 定额红包
    		redAmount = redEnvelope.getTotalAmount().divide(new BigDecimal(redEnvelope.getCount()), 6, BigDecimal.ROUND_HALF_DOWN);
    	}
    	
    	// 超出红包总额
    	if(redAmount.add(redEnvelope.getReceiveAmount()).compareTo(redEnvelope.getTotalAmount()) > 0) {
    		return error("领取红包失败，请重试！");
    	}
        
        // 添加红包领取记录
        RedEnvelopeDetail detailRecord = new RedEnvelopeDetail();
        detailRecord.setAmount(redAmount);
        detailRecord.setCate(0); // 非机器人领取，如果标记为机器人，此值请设置为大于0
        detailRecord.setMemberId(memberId);
        detailRecord.setEnvelopeId(redEnvelope.getId());
        detailRecord.setUserIdentify(authMember.getMobilePhone());
        detailRecord.setCreateTime(DateUtil.getCurrentDate());
        detailRecord.setCate(1); // 机器人
        redEnvelopeDetailService.save(detailRecord);
        
        // 更新红包主表
        redEnvelope.setReceiveCount(redEnvelope.getReceiveCount() + 1);
        redEnvelope.setReceiveAmount(redEnvelope.getReceiveAmount().add(redAmount));
        redEnvelopeService.saveAndFlush(redEnvelope);
        
    	return success(detailRecord);
    }
    
    /**
     * 红包验证码发送
     *
     * @return
     */
    @PostMapping("/code")
    public MessageResult envelopeCode(String phone, String country, Long envelopeId) throws Exception {
        Assert.notNull(country, localeMessageSourceService.getMessage("REQUEST_ILLEGAL"));
        Country country1 = countryService.findOne(country);
        Assert.notNull(country1, localeMessageSourceService.getMessage("REQUEST_ILLEGAL"));
        Assert.notNull(envelopeId, localeMessageSourceService.getMessage("REQUEST_ILLEGAL"));
        
        RedEnvelope redEnvelope = redEnvelopeService.findById(envelopeId);
    	Assert.notNull(redEnvelope, "红包不存在！");
    	
    	if(redEnvelope.getState() == 1) {
    		return error("该红包已被领完!");
    	}
    	
    	if(redEnvelope.getState() == 2) {
    		return error("该红包已过期!");
    	}
    	
    	if(redEnvelope.getState() == 0) {
	    	// 判断红包是否过期(过期有两种可能：1、过期了但是已经被领取完；2：过期了红包还有剩余没有被领取
	    	long currentTime = Calendar.getInstance().getTimeInMillis(); // 当前时间戳
	    	if(currentTime >= (redEnvelope.getCreateTime().getTime() + redEnvelope.getExpiredHours() * 60 * 60 * 1000)) {
	    		if(redEnvelope.getReceiveCount() < redEnvelope.getCount()) {
	    			return error("该红包已过期!");
		    	}else {
		    		return error("该红包已被领完!");
		    	}
	    	}
	    	
	    	// 判断红包是否已被领取完
	    	if(redEnvelope.getReceiveCount() >= redEnvelope.getCount()) {
    			return error("该红包已被领完!");
	    	}
    	}
    	Member member = null;
    	if(memberService.phoneIsExist(phone)) {
	    	// 判断是否领取过红包（邀请加一次领取机会的处理逻辑）
	    	// 0: 邀请不增加领取机会
	    	// 1: 邀请增加领取机会
    		member = memberService.findByPhone(phone);
	    	List<RedEnvelopeDetail> detailList = redEnvelopeDetailService.findByEnvelopeIdAndMemberId(redEnvelope.getId(), member.getId());
	    	if(detailList != null && detailList.size() > 0) {
	    		if(redEnvelope.getInvite() == 0) {
	    			return error("已领取过该红包!");
	    		}
		    	if(redEnvelope.getInvite() == 1) {
		    		// 查看邀请记录
		    		Date endDate = new Date();
		    		List<MemberPromotionStasticVO> inviteList = memberPromotionService.getDateRangeRank(0, redEnvelope.getCreateTime(), endDate, 10000); // 此处10000表示邀请人数limit
		    		if(inviteList.size() <= detailList.size()) {
		    			return error("尚无新邀请好友，无法领取红包！");
		    		}
		    	}
	    	}
    	}
    	
    	
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String key = SysConstant.PHONE_RECEIVE_ENVELOPE_PREFIX + phone;
        Object code = valueOperations.get(key);
        if (code != null) {
            //判断如果请求间隔小于一分钟则请求失败
            if (!BigDecimalUtils.compare(DateUtil.diffMinute((Date) (valueOperations.get(key + "Time"))), BigDecimal.ONE)) {
                return error(localeMessageSourceService.getMessage("FREQUENTLY_REQUEST"));
            }
        }
        String randomCode = String.valueOf(GeneratorUtil.getRandomNumber(100000, 999999));
        MessageResult result;
        if ("86".equals(country1.getAreaCode())) {
            Assert.isTrue(ValidateUtil.isMobilePhone(phone.trim()), localeMessageSourceService.getMessage("PHONE_EMPTY_OR_INCORRECT"));
            result = smsProvider.sendVerifyMessage(phone, randomCode);
        } else {
            result = smsProvider.sendInternationalMessage(randomCode, country1.getAreaCode() + phone);
        }
        if (result.getCode() == 0) {
            valueOperations.getOperations().delete(key);
            valueOperations.getOperations().delete(key + "Time");
            // 缓存验证码
            valueOperations.set(key, randomCode, 10, TimeUnit.MINUTES);
            valueOperations.set(key + "Time", new Date(), 10, TimeUnit.MINUTES);
            return success(localeMessageSourceService.getMessage("SEND_SMS_SUCCESS"));
        } else {
            return error(localeMessageSourceService.getMessage("SEND_SMS_FAILED"));
        }
    }
}
