package com.bizzan.bitrade.controller;

import com.alibaba.fastjson.JSONObject;
import com.bizzan.bitrade.constant.MemberLevelEnum;
import com.bizzan.bitrade.constant.PromotionLevel;
import com.bizzan.bitrade.constant.SysConstant;
import com.bizzan.bitrade.controller.BaseController;
import com.bizzan.bitrade.entity.Member;
import com.bizzan.bitrade.entity.MemberInviteStastic;
import com.bizzan.bitrade.entity.MemberInviteStasticRank;
import com.bizzan.bitrade.entity.MemberPromotion;
import com.bizzan.bitrade.entity.MemberWallet;
import com.bizzan.bitrade.entity.PromotionCard;
import com.bizzan.bitrade.entity.PromotionCardOrder;
import com.bizzan.bitrade.entity.PromotionMember;
import com.bizzan.bitrade.entity.PromotionRewardRecord;
import com.bizzan.bitrade.entity.RewardRecord;
import com.bizzan.bitrade.entity.transform.AuthMember;
import com.bizzan.bitrade.service.CoinService;
import com.bizzan.bitrade.service.MemberInviteStasticService;
import com.bizzan.bitrade.service.MemberPromotionService;
import com.bizzan.bitrade.service.MemberService;
import com.bizzan.bitrade.service.MemberWalletService;
import com.bizzan.bitrade.service.PromotionCardOrderService;
import com.bizzan.bitrade.service.PromotionCardService;
import com.bizzan.bitrade.service.RewardRecordService;
import com.bizzan.bitrade.util.DateUtil;
import com.bizzan.bitrade.util.GeneratorUtil;
import com.bizzan.bitrade.util.MessageResult;
import org.springframework.util.StringUtils;

import org.apache.shiro.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.bizzan.bitrade.constant.SysConstant.SESSION_MEMBER;
import static com.bizzan.bitrade.util.MessageResult.error;
import static com.bizzan.bitrade.util.MessageResult.success;
/**
 * 推广
 *
 * @author GS
 * @date 2018年03月19日
 */
@RestController
@RequestMapping(value = "/promotion")
public class PromotionController extends BaseController{

	@Autowired
    private RedisTemplate redisTemplate;
	
    @Autowired
    private MemberService memberService;
    
    @Autowired
    private RewardRecordService rewardRecordService;

    @Autowired
    private CoinService coinService;
    
    @Autowired
    private MemberInviteStasticService memberInviteStasticService;
    
    @Autowired
    private PromotionCardService promotionCardService;
    
    @Autowired
    private PromotionCardOrderService promotionCardOrderService;
    
    @Autowired
    private MemberWalletService memberWalletService;
    
    @Autowired
    private MemberPromotionService memberPromotionService;
    
    private Random rand = new Random();
    /**
     * 获取推广合伙人信息
     * @param member
     * @return
     */
    @RequestMapping(value = "/mypromotion")
    public MessageResult myPromotioin(@SessionAttribute(SESSION_MEMBER) AuthMember member) {
    	MemberInviteStastic result  =  memberInviteStasticService.findByMemberId(member.getId());
    	if(result != null) {
    		return success(result);
    	}else {
    		return error("no data");
    	}
    }
    
    /**
     * 推广周榜
     * @param top
     * @return
     */
    @RequestMapping(value = "/weektoprank")
    public MessageResult topRankWeek(@RequestParam(value = "top", defaultValue = "20") Integer top) {
    	ValueOperations valueOperations = redisTemplate.opsForValue();
        JSONObject result = (JSONObject) valueOperations.get(SysConstant.MEMBER_PROMOTION_TOP_RANK_WEEK + top);
        if (result != null){
            return success(result);
        } else {
        	JSONObject resultObj = new JSONObject();
        	// 周榜
        	List<MemberInviteStasticRank> topInviteWeek = memberInviteStasticService.topInviteCountByType(1, 20);
        	for(MemberInviteStasticRank item3: topInviteWeek) {
        		item3.setUserIdentify(item3.getUserIdentify().substring(0, 3) + "****" + item3.getUserIdentify().substring(item3.getUserIdentify().length() - 4, item3.getUserIdentify().length()));
        	}
        	
        	resultObj.put("topinviteweek", topInviteWeek);
        	
        	valueOperations.set(SysConstant.MEMBER_PROMOTION_TOP_RANK_WEEK+top, resultObj, SysConstant.MEMBER_PROMOTION_TOP_RANK_EXPIRE_TIME_WEEK, TimeUnit.SECONDS);
            return success(resultObj);
        }
    }
    
    /**
     * 推广月榜
     * @param top
     * @return
     */
    @RequestMapping(value = "/monthtoprank")
    public MessageResult topRankMonth(@RequestParam(value = "top", defaultValue = "20") Integer top) {
    	ValueOperations valueOperations = redisTemplate.opsForValue();
        JSONObject result = (JSONObject) valueOperations.get(SysConstant.MEMBER_PROMOTION_TOP_RANK_MONTH + top);
        if (result != null){
            return success(result);
        } else {
        	JSONObject resultObj = new JSONObject();
        	// 月榜
        	List<MemberInviteStasticRank> topInviteMonth = memberInviteStasticService.topInviteCountByType(2, 20);
        	for(MemberInviteStasticRank item4: topInviteMonth) {
        		item4.setUserIdentify(item4.getUserIdentify().substring(0, 3) + "****" + item4.getUserIdentify().substring(item4.getUserIdentify().length() - 4, item4.getUserIdentify().length()));
        	}
        	resultObj.put("topinvitemonth", topInviteMonth);
        	
        	valueOperations.set(SysConstant.MEMBER_PROMOTION_TOP_RANK_MONTH+top, resultObj, SysConstant.MEMBER_PROMOTION_TOP_RANK_EXPIRE_TIME_MONTH, TimeUnit.SECONDS);
            return success(resultObj);
        }
    }
    /**
     * 获取排名前top名返佣金额 & 前top名邀请人数
     * @param member
     * @param top
     * @return
     */
    @RequestMapping(value = "/toprank")
    public MessageResult topRank(@RequestParam(value = "top", defaultValue = "20") Integer top) {
    	ValueOperations valueOperations = redisTemplate.opsForValue();
        JSONObject result = (JSONObject) valueOperations.get(SysConstant.MEMBER_PROMOTION_TOP_RANK + top);
        if (result != null){
            return success(result);
        } else {
        	JSONObject resultObj = new JSONObject();
        	List<MemberInviteStastic> topReward = memberInviteStasticService.topRewardAmount(top);
        	
        	for(MemberInviteStastic item1 : topReward) {
        		item1.setUserIdentify(item1.getUserIdentify().substring(0, 3) + "****" + item1.getUserIdentify().substring(item1.getUserIdentify().length() - 4, item1.getUserIdentify().length()));
        		item1.setMemberId(item1.getMemberId() * (item1.getMemberId() % 100)); // 仅仅为了隐藏真实ID
        	}
        	
        	List<MemberInviteStastic> topInvite = memberInviteStasticService.topInviteCount(top);
        	for(MemberInviteStastic item2 : topInvite) {
        		item2.setUserIdentify(item2.getUserIdentify().substring(0, 3) + "****" + item2.getUserIdentify().substring(item2.getUserIdentify().length() - 4, item2.getUserIdentify().length()));
        		item2.setMemberId(item2.getMemberId() * (item2.getMemberId() % 100));
        	}
        	resultObj.put("topreward", topReward);
        	resultObj.put("topinvite", topInvite);
        	
        	// 周榜
        	List<MemberInviteStasticRank> topInviteWeek = memberInviteStasticService.topInviteCountByType(1, 20);
        	for(MemberInviteStasticRank item3: topInviteWeek) {
        		item3.setUserIdentify(item3.getUserIdentify().substring(0, 3) + "****" + item3.getUserIdentify().substring(item3.getUserIdentify().length() - 4, item3.getUserIdentify().length()));
        		item3.setMemberId(item3.getMemberId() * (item3.getMemberId() % 100));
        	}
        	
        	// 月榜
        	List<MemberInviteStasticRank> topInviteMonth = memberInviteStasticService.topInviteCountByType(2, 20);
        	for(MemberInviteStasticRank item4: topInviteMonth) {
        		item4.setUserIdentify(item4.getUserIdentify().substring(0, 3) + "****" + item4.getUserIdentify().substring(item4.getUserIdentify().length() - 4, item4.getUserIdentify().length()));
        		item4.setMemberId(item4.getMemberId() * (item4.getMemberId() % 100));
        	}
        	resultObj.put("topinviteweek", topInviteWeek);
        	resultObj.put("topinvitemonth", topInviteMonth);
        	
        	valueOperations.set(SysConstant.MEMBER_PROMOTION_TOP_RANK+top, resultObj, SysConstant.MEMBER_PROMOTION_TOP_RANK_EXPIRE_TIME, TimeUnit.SECONDS);
            return success(resultObj);
        }
    }

    /**
     * 推广记录查询
     *
     * @param member
     * @return
     */
//    @RequestMapping(value = "/record")
//    public MessageResult promotionRecord(@SessionAttribute(SESSION_MEMBER) AuthMember member) {
//        List<Member> list = memberService.findPromotionMember(member.getId());
//        List<PromotionMember> list1 = list.stream().map(x ->
//                PromotionMember.builder().createTime(x.getRegistrationTime())
//                        .level(PromotionLevel.ONE)
//                        .username(x.getUsername())
//                        .build()
//        ).collect(Collectors.toList());
//        if (list.size() > 0) {
//            list.stream().forEach(x -> {
//                if (x.getPromotionCode() != null) {
//                    list1.addAll(memberService.findPromotionMember(x.getId()).stream()
//                            .map(y ->
//                                    PromotionMember.builder().createTime(y.getRegistrationTime())
//                                            .level(PromotionLevel.TWO)
//                                            .username(y.getUsername())
//                                            .build()
//                            ).collect(Collectors.toList()));
//                }
//            });
//        }
//        MessageResult messageResult = MessageResult.success();
//        messageResult.setData(list1.stream().sorted((x, y) -> {
//            if (x.getCreateTime().after(y.getCreateTime())) {
//                return -1;
//            } else {
//                return 1;
//            }
//        }).collect(Collectors.toList()));
//        return messageResult;
//    }
    @RequestMapping(value = "/record")
    public MessageResult promotionRecord2(@SessionAttribute(SESSION_MEMBER) AuthMember member, @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo, @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {

        //只查找一级推荐人
        Page<Member> pageList = memberService.findPromotionMemberPage(pageNo-1, pageSize, member.getId());
        MessageResult messageResult = MessageResult.success();
        List<Member> list = pageList.getContent();
        List<PromotionMember> list1 = list.stream().map(x ->
                PromotionMember.builder().createTime(x.getRegistrationTime())
                        .level(PromotionLevel.ONE)
                        .username(x.getUsername().substring(0, 3) + "****" + x.getUsername().substring(x.getUsername().length() - 4, x.getUsername().length()))
                        .realNameStatus(x.getRealNameStatus())
                        .build()
        ).collect(Collectors.toList());

        messageResult.setData(list1.stream().sorted((x, y) -> {
            if (x.getCreateTime().after(y.getCreateTime())) {
                return -1;
            } else {
                return 1;
            }
        }).collect(Collectors.toList()));

        messageResult.setTotalPage(pageList.getTotalPages() + "");
        messageResult.setTotalElement(pageList.getTotalElements() + "");
        return messageResult;
    }


    /**
     * 推广奖励记录
     *
     * @param member
     * @return
     */
//    @RequestMapping(value = "/reward/record")
//    public MessageResult rewardRecord(@SessionAttribute(SESSION_MEMBER) AuthMember member) {
//        List<RewardRecord> list = rewardRecordService.queryRewardPromotionList(memberService.findOne(member.getId()));
//        MessageResult result = MessageResult.success();
//        result.setData(list.stream().map(x ->
//                PromotionRewardRecord.builder().amount(x.getAmount())
//                        .createTime(x.getCreateTime())
//                        .remark(x.getRemark())
//                        .symbol(x.getCoin().getUnit())
//                        .build()
//        ).collect(Collectors.toList()));
//        return result;
//    }


    /**
     * 只查询推荐奖励
     *
     * @param member
     * @return
     */
    @RequestMapping(value = "/reward/record")
    public MessageResult rewardRecord2(@SessionAttribute(SESSION_MEMBER) AuthMember member, @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo, @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        Page<RewardRecord> pageList = rewardRecordService.queryRewardPromotionPage(pageNo, pageSize, memberService.findOne(member.getId()));
        MessageResult result = MessageResult.success();
        List<RewardRecord> list = pageList.getContent();
        result.setData(list.stream().map(x ->
                PromotionRewardRecord.builder().amount(x.getAmount())
                        .createTime(x.getCreateTime())
                        .remark(x.getRemark())
                        .symbol(x.getCoin().getUnit())
                        .build()
        ).collect(Collectors.toList()));

        result.setTotalPage(pageList.getTotalPages() + "");
        result.setTotalElement(pageList.getTotalElements() + "");
        return result;
    }

    /**
     * 获取免费推广卡（BTC: 0.001)
     * @param member
     * @return
     */
    @RequestMapping(value = "/promotioncard/getfreecard")
    public MessageResult createFreeCard(@SessionAttribute(SESSION_MEMBER) AuthMember member) {
    	// 检查是否实名认证
    	Member authMember = memberService.findOne(member.getId());
        if(authMember.getMemberLevel()== MemberLevelEnum.GENERAL){
            return MessageResult.error(500,"请先进行实名认证");
        }
    	// 检查是否领取过一次
        List<PromotionCard> result = promotionCardService.findAllByMemberIdAndIsFree(member.getId(), 1);
        if(result != null && result.size() > 0) {
        	return MessageResult.error(500,"请不要重复领取免费推广卡");
        }
        
        PromotionCard card = new PromotionCard();
        card.setCardName("合伙人推广卡");
        card.setCardNo(authMember.getPromotionCode() + GeneratorUtil.getNonceString(5).toUpperCase());
        card.setAmount(new BigDecimal(0.001));
        card.setCardDesc("");
        card.setCoin(coinService.findByUnit("BTC"));
        card.setCount(30);
        card.setMemberId(authMember.getId());
        card.setIsFree(1);
        card.setIsEnabled(1);
        card.setExchangeCount(0);
        card.setTotalAmount(new BigDecimal(0.03));
        card.setIsLock(0);
        card.setLockDays(0);
        card.setIsEnabled(1);
        card.setCreateTime(DateUtil.getCurrentDate());
        
        PromotionCard cardResult = promotionCardService.save(card);
        
        return success(cardResult);
    }
    
    /**
     * 获取我创建的卡券列表
     * @param member
     * @return
     */
    @RequestMapping(value = "/promotioncard/mycard")
    private MessageResult getMyCardList(@SessionAttribute(SESSION_MEMBER) AuthMember member) {
    	
    	List<PromotionCard> result = promotionCardService.findAllByMemberId(member.getId());
    	
    	return success(result);
    }
    
    /**
     * 兑换卡详情
     * @param cardId
     * @return
     */
    @RequestMapping(value = "/promotioncard/detail")
    private MessageResult getCardDetail(@RequestParam(value = "cardId", defaultValue = "") Long cardId) {
    	
    	Assert.notNull(cardId, "无效的兑换卡！");
    	PromotionCard result = promotionCardService.findOne(cardId);
    	Assert.notNull(result, "无效的兑换卡！");
    	
    	return success(result);
    }
    /**
     * 兑换码兑换卡券（免费领取的卡券只能兑换一次）
     * @param member
     * @param cardNo
     * @return
     */
    @RequestMapping(value = "/promotioncard/exchangecard")
    @Transactional(rollbackFor = Exception.class)
    private MessageResult exhcangeCard(@SessionAttribute(SESSION_MEMBER) AuthMember member, 
    								   @RequestParam(value = "cardNo", defaultValue = "") String cardNo) {
    	
    	// 检查卡券是否存在
    	Assert.notNull(cardNo, "请输入兑换卡号！");
    	if(!StringUtils.hasText(cardNo)) {
    		return error("请输入兑换卡号！");
    	}
    	PromotionCard card = promotionCardService.findPromotionCardByCardNo(cardNo);
    	Assert.notNull(card, "无效的兑换卡号！");
    	
    	// 用户是否存在
    	Member authMember = memberService.findOne(member.getId());
    	Assert.notNull(authMember, "非法操作!");
    	
    	// 检查卡券是否有效
    	if(card.getIsEnabled() == 0) {
    		return error("推广卡无效！");
    	}
    	
    	// 用户钱包是否存在
    	MemberWallet memberWallet = memberWalletService.findByCoinUnitAndMemberId(card.getCoin().getUnit(), authMember.getId());
    	Assert.notNull(memberWallet, "该资产不存在!");
    	
    	// 检查卡券数量是否足够
    	if(card.getExchangeCount() >= card.getCount()) {
    		return error("该推广卡已兑完！");
    	}
    	// 检查自己是否领取过
    	List<PromotionCardOrder> order = promotionCardOrderService.findByCardIdAndMemberId(card.getId(), authMember.getId());
    	if(order != null && order.size() > 0) {
    		return error("已兑换过该卡，请勿重复兑换！");
    	}
    	
    	// 检查自己是否领取过免费卡（官方为每一个用户发放的免费卡，每个人只能兑换一次）
    	List<PromotionCardOrder> orderFree = promotionCardOrderService.findAllByMemberIdAndIsFree(authMember.getId(), 1);
    	if(orderFree != null && orderFree.size() > 0) {
    		return error("官方发行免费推广卡只能兑换一次！");
    	}
    	
    	PromotionCardOrder newOrder= new PromotionCardOrder();
    	newOrder.setMemberId(authMember.getId());
    	newOrder.setAmount(card.getAmount());
    	newOrder.setCard(card);
    	newOrder.setIsFree(card.getIsFree());
    	newOrder.setIsLock(card.getIsLock());
    	newOrder.setLockDays(card.getLockDays());
    	
    	newOrder.setState(1);
    	
    	newOrder.setCreateTime(DateUtil.getCurrentDate());
    	newOrder = promotionCardOrderService.save(newOrder);
    	
    	if(newOrder != null) {
    		// 如果用户自身没有被任何邀请，则新增邀请人
    		if(authMember.getInviterId() == null) {
    			if(authMember.getId() != card.getMemberId()) {
    				Member levelOneMember = memberService.findOne(card.getMemberId());
    				// 用户已通过实名认证再兑换卡时，需要考虑一级二级好友
    				// 用户未通过实名认证，则只需设置inviteID
    				authMember.setInviterId(card.getMemberId());
    				if(authMember.getMemberLevel() == MemberLevelEnum.REALNAME){
	    				// 一级邀请关系保存
	    				MemberPromotion one = new MemberPromotion();
	    		        one.setInviterId(card.getMemberId());
	    		        one.setInviteesId(authMember.getId());
	    		        one.setLevel(PromotionLevel.ONE);
	    		        memberPromotionService.save(one);
	    		        // 一级好友人数 + 1
	    		        levelOneMember.setFirstLevel(levelOneMember.getFirstLevel() + 1);
	    		        
	    		        if(levelOneMember.getInviterId() != null) {
	    		        	Member levelTwoMember = memberService.findOne(levelOneMember.getInviterId());
	    		        	// 二级邀请关系保存
	    		        	MemberPromotion two = new MemberPromotion();
	    		            two.setInviterId(levelTwoMember.getId());
	    		            two.setInviteesId(authMember.getId());
	    		            two.setLevel(PromotionLevel.TWO);
	    		            memberPromotionService.save(two);
	    		            
	    		            // 二级好友人数 + 1
	    		            levelTwoMember.setSecondLevel(levelTwoMember.getSecondLevel() + 1);
	    		        }
    				}
    			}
    		}
    		
            // 添加资产到用户钱包
    		memberWalletService.increaseFrozen(memberWallet.getId(), newOrder.getAmount());
    		
    		// 更新主表数据
    		card.setExchangeCount(card.getExchangeCount() + 1);
    		promotionCardService.saveAndFlush(card);
    		
    		return success("兑换成功！");
    	}else {
    		return error("兑换失败！");
    	}
    }
}
