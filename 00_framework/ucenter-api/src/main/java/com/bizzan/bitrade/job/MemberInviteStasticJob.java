package com.bizzan.bitrade.job;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONObject;
import com.bizzan.bitrade.constant.SysConstant;
import com.bizzan.bitrade.constant.TransactionType;
import com.bizzan.bitrade.entity.Coin;
import com.bizzan.bitrade.entity.Member;
import com.bizzan.bitrade.entity.MemberInviteStastic;
import com.bizzan.bitrade.entity.MemberInviteStasticRank;
import com.bizzan.bitrade.entity.MemberTransaction;
import com.bizzan.bitrade.service.MemberInviteStasticService;
import com.bizzan.bitrade.service.MemberPromotionService;
import com.bizzan.bitrade.service.MemberService;
import com.bizzan.bitrade.service.MemberTransactionService;
import com.bizzan.bitrade.system.CoinExchangeFactory;
import com.bizzan.bitrade.vo.MemberPromotionStasticVO;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MemberInviteStasticJob {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private CoinExchangeFactory coinExchangeFactory;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private MemberService memberService;
    
    @Autowired
    private MemberTransactionService memberTransactionService;
    
    @Autowired
    private MemberPromotionService memberPromotionService;
    
    @Autowired
    private MemberInviteStasticService memberInviteStatsticService;
    
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
	
    private String serviceName = "bitrade-market";
    private Random random = new Random();
    
    /**
     * 每日2点处理，统计用户推广币币手续费返佣结果(0 0 2 * * *)，总榜
     */
    @Scheduled(cron = "0 0 2 * * *")
    public void stasticMemberInviteAll() {
    	//获取当前时间
    	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	String dateNow = df.format(new Date());
    	
    	int pageNo = 0;
    	int pageSize = 100;
    	while(true) {
    		Page<Member> members = memberService.findByPage(pageNo, pageSize);
    		List<Member> all = members.getContent();

	        if (all != null && all.size() > 0) {
	        	for(Member item : all) {
	        		if(item.getId() > 10000) {
	        			List<MemberTransaction> transactions = memberTransactionService.queryByMember(item.getId(), TransactionType.PROMOTION_AWARD);

        				BigDecimal btcTotal = BigDecimal.ZERO;
        				BigDecimal ethTotal = BigDecimal.ZERO;
        				BigDecimal usdtTotal = BigDecimal.ZERO;
        				BigDecimal estimatedTotal = BigDecimal.ZERO;
        				
	        			if(transactions != null && transactions.size() > 0) {
	        				for(MemberTransaction tItem : transactions) {
	        					if(tItem.getSymbol().equals("BTC")) {
	        						btcTotal = btcTotal.add(tItem.getAmount());
	        					}
	        					if(tItem.getSymbol().equals("ETH")) {
	        						ethTotal = ethTotal.add(tItem.getAmount());
	        					}
	        					if(tItem.getSymbol().equals("USDT")) {
	        						usdtTotal = usdtTotal.add(tItem.getAmount());
	        					}
	        				}
	        				// 计算估算总额
	        				CoinExchangeFactory.ExchangeRate rateBTC = coinExchangeFactory.get("BTC");
	        				estimatedTotal = estimatedTotal.add(btcTotal.multiply(rateBTC.usdRate));
	        				
	        				CoinExchangeFactory.ExchangeRate rateETH = coinExchangeFactory.get("ETH");
	        				estimatedTotal = estimatedTotal.add(ethTotal.multiply(rateETH.usdRate));
	        				
	        				estimatedTotal = estimatedTotal.add(usdtTotal).setScale(2);
	        			}
        				// 更新 or 保存记录
        				MemberInviteStastic mis = memberInviteStatsticService.findByMemberId(item.getId());
        				if(mis != null) {
        					mis.setUsdtReward(usdtTotal);
        					mis.setBtcReward(btcTotal);
        					mis.setEthReward(ethTotal);
        					mis.setLevelOne(item.getFirstLevel());
        					mis.setLevelTwo(item.getSecondLevel());
        					mis.setEstimatedReward(estimatedTotal);
        					mis.setStasticDate(dateNow);
        					
        					memberInviteStatsticService.save(mis);
        				}else {
        					mis = new MemberInviteStastic();
        					
        					mis.setMemberId(item.getId());
        					mis.setUserIdentify(item.getMobilePhone());
        					mis.setIsRobot(0);
        					mis.setUsdtReward(usdtTotal);
        					mis.setBtcReward(btcTotal);
        					mis.setEthReward(ethTotal);
        					mis.setLevelOne(item.getFirstLevel());
        					mis.setLevelTwo(item.getSecondLevel());
        					mis.setEstimatedReward(estimatedTotal);
        					mis.setExtraReward(BigDecimal.ZERO);
        					mis.setStasticDate(dateNow);
        					
        					memberInviteStatsticService.save(mis);
        				}
	        		}else {
	        			// 更新 or 保存记录
	    				MemberInviteStastic mis = memberInviteStatsticService.findByMemberId(item.getId());
	    				if(mis != null) {
	    					int rand1 = random.nextInt(100);
	    					mis.setUsdtReward(BigDecimal.ZERO);
	    					mis.setBtcReward(BigDecimal.ZERO);
	    					mis.setEthReward(BigDecimal.ZERO);
	    					mis.setLevelOne(mis.getLevelOne() + (rand1 % 5)); // 一级好友人数每天自动增加0-5个
	    					mis.setLevelTwo(mis.getLevelTwo() + (rand1 % 10)); // 二级好友人数每天自动增加0-10个
	    					if(mis.getLevelOne() > 100) {
	    						mis.setEstimatedReward(mis.getEstimatedReward().add(BigDecimal.valueOf(80 + (rand1 % 80)).add(BigDecimal.valueOf(random.nextDouble()).setScale(6, BigDecimal.ROUND_DOWN)))); // 80 - 160
	    					}else if(mis.getLevelOne() > 80) {
	    						mis.setEstimatedReward(mis.getEstimatedReward().add(BigDecimal.valueOf(70 + (rand1 % 70)).add(BigDecimal.valueOf(random.nextDouble()).setScale(6, BigDecimal.ROUND_DOWN)))); // 70 - 140
	    					}else if(mis.getLevelOne() > 60) {
	    						mis.setEstimatedReward(mis.getEstimatedReward().add(BigDecimal.valueOf(60 + (rand1 % 60)).add(BigDecimal.valueOf(random.nextDouble()).setScale(6, BigDecimal.ROUND_DOWN)))); // 60 - 120
	    					}else if(mis.getLevelOne() > 50) {
	    						mis.setEstimatedReward(mis.getEstimatedReward().add(BigDecimal.valueOf(50 + (rand1 % 50)).add(BigDecimal.valueOf(random.nextDouble()).setScale(6, BigDecimal.ROUND_DOWN)))); // 50 - 100
	    					}else if(mis.getLevelOne() > 40) {
	    						mis.setEstimatedReward(mis.getEstimatedReward().add(BigDecimal.valueOf(40 + (rand1 % 40)).add(BigDecimal.valueOf(random.nextDouble()).setScale(6, BigDecimal.ROUND_DOWN)))); // 40 - 80
	    					}else if(mis.getLevelOne() > 30) {
	    						mis.setEstimatedReward(mis.getEstimatedReward().add(BigDecimal.valueOf(30 + (rand1 % 30)).add(BigDecimal.valueOf(random.nextDouble()).setScale(6, BigDecimal.ROUND_DOWN)))); // 30 - 60
	    					}else if(mis.getLevelOne() > 20) {
	    						mis.setEstimatedReward(mis.getEstimatedReward().add(BigDecimal.valueOf(20 + (rand1 % 20)).add(BigDecimal.valueOf(random.nextDouble()).setScale(6, BigDecimal.ROUND_DOWN)))); // 20 - 40
	    					}else if(mis.getLevelOne() > 10) {
	    						mis.setEstimatedReward(mis.getEstimatedReward().add(BigDecimal.valueOf(10 + (rand1 % 10)).add(BigDecimal.valueOf(random.nextDouble()).setScale(6, BigDecimal.ROUND_DOWN)))); // 10-20
	    					}else if(mis.getLevelOne() > 0) {
	    						mis.setEstimatedReward(mis.getEstimatedReward().add(BigDecimal.valueOf(rand1 % 10).add(BigDecimal.valueOf(random.nextDouble()).setScale(6, BigDecimal.ROUND_DOWN)))); // 折合USDT奖励每天自动增加0 - 10刀
	    					}else {
	    						
	    					}
	    					mis.setStasticDate(dateNow);
	    					
	    					memberInviteStatsticService.save(mis);
	    				}else {
	    					mis = new MemberInviteStastic();
	    					
	    					mis.setMemberId(item.getId());
	    					mis.setUserIdentify(item.getMobilePhone());
	    					mis.setIsRobot(1);
	    					mis.setUsdtReward(BigDecimal.ZERO);
	    					mis.setBtcReward(BigDecimal.ZERO);
	    					mis.setEthReward(BigDecimal.ZERO);
	    					mis.setLevelOne(0);
	    					mis.setLevelTwo(0);
	    					mis.setEstimatedReward(BigDecimal.ZERO);
	    					mis.setExtraReward(BigDecimal.ZERO);
	    					mis.setStasticDate(dateNow);
	    					
	    					memberInviteStatsticService.save(mis);
	    				}
	        		}
	        	}
	        	
	        	pageNo++;
	        }else {
	        	break;
	        }
    	}

        int top = 20;
        List<MemberInviteStastic> topRewardList = memberInviteStatsticService.topRewardAmount(top);
    	List<MemberInviteStastic> topInviteList = memberInviteStatsticService.topInviteCount(top);
    	
    	// 发送邮件通知
		String[] adminList = admins.split(",");
		for(int i = 0; i < adminList.length; i++) {
			try {
				sendEmailMsg(adminList[i], topRewardList, topInviteList, "推广合伙人排名(总榜)");
			} catch (MessagingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (TemplateException e) {
				e.printStackTrace();
			}
		}
    }
    
    /**
     * 日榜
     */
    //@Scheduled(cron="0 0 2 * * *")
    public void stasticMemberInviteDay() {
    	// SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	
    	Date cTime = new Date();
    	Calendar calendar = Calendar.getInstance();
        calendar.setTime(cTime);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date endDate = calendar.getTime();

        Calendar calendar2 = Calendar.getInstance();
        Date yd = new Date(cTime.getTime() - 24 * 3600 * 1000);
        calendar2.setTime(yd);
        calendar2.set(Calendar.HOUR_OF_DAY, 0);
        calendar2.set(Calendar.MINUTE, 0);
        calendar2.set(Calendar.SECOND, 0);
        
        Date startDate = calendar2.getTime();

    	List<MemberPromotionStasticVO> result = memberPromotionService.getDateRangeRank(0, startDate, endDate, 20);
    	List<MemberInviteStasticRank> allList = new ArrayList<MemberInviteStasticRank>();
    	
    	for(MemberPromotionStasticVO vo : result) {
    		MemberInviteStasticRank misr = new MemberInviteStasticRank();
    		misr.setLevelOne(vo.getCount());
    		misr.setLevelTwo(0);
    		misr.setMemberId(vo.getInviterId());
    		misr.setStasticDate(endDate);
    		misr.setType(0);// 0标识DAY
    		Member m = memberService.findOne(vo.getInviterId());
    		misr.setUserIdentify(m.getMobilePhone());
    		// ID超过10000则不是机器人
    		if(m.getId().compareTo(Long.valueOf(10000)) >= 0) {
    			misr.setIsRobot(0);
    		}else {
    			misr.setIsRobot(1);
    		}
    		misr = memberInviteStatsticService.saveRank(misr);
    		
    		allList.add(misr);
    	}
    	
    	// 发送邮件通知
		String[] adminList = admins.split(",");
		for(int i = 0; i < adminList.length; i++) {
			try {
				sendEmailMsg(adminList[i], allList, "推广合伙人排名(日榜)");
			} catch (MessagingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (TemplateException e) {
				e.printStackTrace();
			}
		}
    }
    
    /**
     * 周榜（每周一2点30统计）
     */
    @Scheduled(cron="0 30 2 ? * MON")
    public void stasticMemberInviteWeek() {
    	Date cTime = new Date();
    	Calendar calendar = Calendar.getInstance();
        calendar.setTime(cTime);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date endDate = calendar.getTime();

        Calendar calendar2 = Calendar.getInstance();
        Date yd = new Date(cTime.getTime() - 7 * 24 * 3600 * 1000);
        calendar2.setTime(yd);
        calendar2.set(Calendar.HOUR, 0);
        calendar2.set(Calendar.MINUTE, 0);
        calendar2.set(Calendar.SECOND, 0);
        
        Date startDate = calendar2.getTime();
        
        List<MemberPromotionStasticVO> result = memberPromotionService.getDateRangeRank(0, startDate, endDate, 20);
    	
        List<MemberInviteStasticRank> allList = new ArrayList<MemberInviteStasticRank>();
    	for(MemberPromotionStasticVO vo : result) {
    		MemberInviteStasticRank misr = new MemberInviteStasticRank();
    		misr.setLevelOne(vo.getCount());
    		misr.setLevelTwo(0);
    		misr.setMemberId(vo.getInviterId());
    		misr.setStasticDate(endDate);
    		misr.setType(1);// 1标识WEEK
    		Member m = memberService.findOne(vo.getInviterId());
    		misr.setUserIdentify(m.getMobilePhone());
    		// ID超过10000则不是机器人
    		if(m.getId().compareTo(Long.valueOf(10000)) >= 0) {
    			misr.setIsRobot(0);
    		}else {
    			misr.setIsRobot(1);
    		}
    		misr = memberInviteStatsticService.saveRank(misr);
    		
    		allList.add(misr);
    	}
    	
    	// 发送邮件通知
		String[] adminList = admins.split(",");
		for(int i = 0; i < adminList.length; i++) {
			try {
				sendEmailMsg(adminList[i], allList, "推广合伙人排名(周榜)");
			} catch (MessagingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (TemplateException e) {
				e.printStackTrace();
			}
		}
    }
    
    /**
     * 月榜(每月1号3点统计）
     */
    @Scheduled(cron="0 0 3 1 * ?")
    public void stasticMemberInviteMonth() {
    	Date cTime = new Date();
    	
    	Calendar calendar = Calendar.getInstance();
    	calendar.add(Calendar.MONTH, 0);
    	calendar.set(Calendar.DAY_OF_MONTH,1);//1:本月第一天
    	calendar.set(Calendar.HOUR_OF_DAY, 0);
    	calendar.set(Calendar.MINUTE, 0);
    	calendar.set(Calendar.SECOND, 0);
    	Date endDate = calendar.getTime();

        Calendar calendar2 = Calendar.getInstance();
        calendar2.add(Calendar.MONTH, -1); // 上月第一天
        calendar2.set(Calendar.DAY_OF_MONTH, 1);
        calendar2.set(Calendar.HOUR_OF_DAY, 0);
        calendar2.set(Calendar.MINUTE, 0);
        calendar2.set(Calendar.SECOND, 0);
        
        Date startDate = calendar2.getTime();
        
        List<MemberPromotionStasticVO> result = memberPromotionService.getDateRangeRank(0, startDate, endDate, 20);
    	
        List<MemberInviteStasticRank> allList = new ArrayList<MemberInviteStasticRank>();
    	for(MemberPromotionStasticVO vo : result) {
    		MemberInviteStasticRank misr = new MemberInviteStasticRank();
    		misr.setLevelOne(vo.getCount());
    		misr.setLevelTwo(0);
    		misr.setMemberId(vo.getInviterId());
    		misr.setStasticDate(endDate);
    		misr.setType(2);// 1标识Month
    		Member m = memberService.findOne(vo.getInviterId());
    		misr.setUserIdentify(m.getMobilePhone());
    		// ID超过10000则不是机器人
    		if(m.getId().compareTo(Long.valueOf(10000)) >= 0) {
    			misr.setIsRobot(0);
    		}else {
    			misr.setIsRobot(1);
    		}
    		misr = memberInviteStatsticService.saveRank(misr);
    		
    		allList.add(misr);
    	}
    	
    	// 发送邮件通知
		String[] adminList = admins.split(",");
		for(int i = 0; i < adminList.length; i++) {
			try {
				sendEmailMsg(adminList[i], allList, "推广合伙人排名(周榜)");
			} catch (MessagingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (TemplateException e) {
				e.printStackTrace();
			}
		}
    }
    
    /**
     * 每天8点将排名同步到Redis
     */
    @Scheduled(cron="0 0 8 * * *") 
    public void staticSync(){
    	// 总榜单同步Redis
    	ValueOperations valueOperations = redisTemplate.opsForValue();
    	int top = 20;
    	JSONObject resultObj = new JSONObject();
    	List<MemberInviteStastic> topReward = memberInviteStatsticService.topRewardAmount(top);
    	List<MemberInviteStastic> topInvite = memberInviteStatsticService.topInviteCount(top);
    	for(MemberInviteStastic item1 : topReward) {
    		item1.setUserIdentify(item1.getUserIdentify().substring(0, 3) + "****" + item1.getUserIdentify().substring(item1.getUserIdentify().length() - 4,  item1.getUserIdentify().length()));
    	}
    	
    	for(MemberInviteStastic item2 : topInvite) {
    		item2.setUserIdentify(item2.getUserIdentify().substring(0, 3) + "****" + item2.getUserIdentify().substring(item2.getUserIdentify().length() - 4,  item2.getUserIdentify().length()));
    	}
    	resultObj.put("topreward", topReward);
    	resultObj.put("topinvite", topInvite);
    	valueOperations.set(SysConstant.MEMBER_PROMOTION_TOP_RANK+top, resultObj, SysConstant.MEMBER_PROMOTION_TOP_RANK_EXPIRE_TIME, TimeUnit.SECONDS);
    }
    
    @Async
    public void sendEmailMsg(String email, 
    						 List<MemberInviteStastic> topRewardList,
    						 List<MemberInviteStastic> topInviteList,
    						 String subject) throws MessagingException, IOException, TemplateException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = null;
        helper = new MimeMessageHelper(mimeMessage, true);
        helper.setFrom(from);
        helper.setTo(email);
        helper.setSubject(company + "-" + subject);
        Map<String, Object> model = new HashMap<>(16);
        model.put("topRewardList", topRewardList);
        model.put("topInviteList", topInviteList);
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_26);
        cfg.setClassForTemplateLoading(this.getClass(), "/templates");
        Template template = cfg.getTemplate("promotionStastic.ftl");
        String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
        helper.setText(html, true);

        //发送邮件
        javaMailSender.send(mimeMessage);
    }

    @Async
    public void sendEmailMsg(String email, 
    						 List<MemberInviteStasticRank> topInviteList,
    						 String subject) throws MessagingException, IOException, TemplateException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = null;
        helper = new MimeMessageHelper(mimeMessage, true);
        helper.setFrom(from);
        helper.setTo(email);
        helper.setSubject(company + "-" + subject);
        Map<String, Object> model = new HashMap<>(16);
        model.put("topInviteList", topInviteList);
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_26);
        cfg.setClassForTemplateLoading(this.getClass(), "/templates");
        Template template = cfg.getTemplate("promotionStasticRank.ftl");
        String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
        helper.setText(html, true);

        //发送邮件
        javaMailSender.send(mimeMessage);
    }

}
