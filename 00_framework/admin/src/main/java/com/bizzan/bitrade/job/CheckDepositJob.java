package com.bizzan.bitrade.job;

import java.io.IOException;
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

import com.bizzan.bitrade.service.MemberDepositService;
import com.bizzan.bitrade.util.MessageResult;
import com.bizzan.bitrade.vendor.provider.SMSProvider;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CheckDepositJob {

	@Autowired
    private MemberDepositService memberDepositService;
	
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
    
    private Long maxDepositId = Long.valueOf(0);
    
	/**
	 * 每小时检查一次
	 */
	@Scheduled(cron = "0 0 * * * *")
    public void checkNewWithdrawApplication(){
		
		// 获取充值记录
		Long currentDepositId = memberDepositService.getMaxId();
		if(currentDepositId == null) return;
		
		if(this.maxDepositId.compareTo(Long.valueOf(0)) != 0) {
			if(currentDepositId.compareTo(this.maxDepositId) > 0) {
				Long count = currentDepositId - this.maxDepositId;
				this.maxDepositId = currentDepositId;
				
				try {
					String[] adminList = admins.split(",");
					for(int i = 0; i < adminList.length; i++) {
						sendEmailMsg(adminList[i], "有新充值( 共" + count+ "条 )", "用户充值通知");
					}
				} catch (Exception e) {
					MessageResult result;
					try {
						String[] phones = adminPhones.split(",");
						if(phones.length > 0) {
							result = smsProvider.sendSingleMessage(phones[0], "==用户充值==");
							if(result.getCode() != 0) {
								if(phones.length > 1) {
									smsProvider.sendSingleMessage(phones[1], "==用户充值==");
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
			this.maxDepositId = currentDepositId;
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
}
