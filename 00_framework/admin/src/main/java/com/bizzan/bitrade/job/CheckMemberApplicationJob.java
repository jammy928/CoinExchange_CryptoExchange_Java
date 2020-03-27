package com.bizzan.bitrade.job;


import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.bizzan.bitrade.service.MemberApplicationService;
import com.bizzan.bitrade.service.MemberService;
import com.bizzan.bitrade.util.MessageResult;
import com.bizzan.bitrade.vendor.provider.SMSProvider;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;

/**
 * 检查实名认证申请用户
 * @author shaox
 *
 */
@Component
@Slf4j
public class CheckMemberApplicationJob {
	@Autowired
	private MemberService memberService;
	
	@Autowired
    private MemberApplicationService memberApplicationService;
	
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
    
    private Long maxUserId = Long.valueOf(0);
    
	/**
	 * 每小时检查一次
	 */
	@Scheduled(cron = "0 0 * * * *")
    public void checkNewMemberApplication(){
		if(isRestTime()) {
			return;
		}
		// 查询待审核数量
		Long count = memberApplicationService.countAuditing();
		if(count > 0) {
			try {
				String[] adminList = admins.split(",");
				for(int i = 0; i < adminList.length; i++) {
					sendEmailMsg(adminList[i], "有新实名认证申请( 共" + count+ "条 )", "新实名认证审核通知");
				}
			} catch (Exception e) {
				MessageResult result;
				try {
					String[] phones = adminPhones.split(",");
					if(phones.length > 0) {
						result = smsProvider.sendSingleMessage(phones[0], "==新实名申请==");
						if(result.getCode() != 0) {
							if(phones.length > 1) {
								smsProvider.sendSingleMessage(phones[1], "==新实名申请==");
							}
						}
					}
					
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				e.printStackTrace();
			}
		}
	}
	
	@Async
    public void sendEmailMsg(String email, String msg, String subject) throws MessagingException, IOException, TemplateException {
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
        log.info("send email for {},content:{}", email, html);
    }
	
	/**
	 * 每小时检查一次(是否有新用户注册）
	 */
	@Scheduled(cron = "0 0 * * * *")
    public void checkHasNewUser(){
		if(isRestTime()) {
			return;
		}
		// 查询待审核数量
		Long currentMaxId = memberService.getMaxId();
		if(currentMaxId == null) return;
		
		if(this.maxUserId.compareTo(Long.valueOf(0)) != 0) {
			if(currentMaxId.compareTo(this.maxUserId) > 0) {
				// 有新用户
				Long userCount = currentMaxId - this.maxUserId;
				this.maxUserId = currentMaxId;
				
				try {
					String[] adminList = admins.split(",");
					for(int i = 0; i < adminList.length; i++) {
						sendEmailMsg(adminList[i], "有新用户注册( 共" + userCount+ "人 )", "新用户注册通知");
					}
				} catch (Exception e) {
					MessageResult result;
					try {
						String[] phones = adminPhones.split(",");
						if(phones.length > 0) {
							result = smsProvider.sendSingleMessage(phones[0], "==新用户注册("+userCount+")==");
							if(result.getCode() != 0) {
								if(phones.length > 1) {
									smsProvider.sendSingleMessage(phones[1], "==新用户注册("+userCount+")==");
								}
							}
						}
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					e.printStackTrace();
				}
			}
		}else {
			this.maxUserId = currentMaxId;
		}
	}
	
	private boolean isRestTime() {
		Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int hour = calendar.get(Calendar.HOUR_OF_DAY); // 每日不同时间段需要不同成交量体现
        
        if(hour >= 0 && hour <= 6) {
        	return true;
        }
        return false;
	}
}
