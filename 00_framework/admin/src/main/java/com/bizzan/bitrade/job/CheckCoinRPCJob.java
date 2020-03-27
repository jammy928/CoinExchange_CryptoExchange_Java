package com.bizzan.bitrade.job;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.client.RestTemplate;

import com.bizzan.bitrade.entity.Coin;
import com.bizzan.bitrade.service.CoinService;
import com.bizzan.bitrade.util.MessageResult;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CheckCoinRPCJob {
	
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

	@Autowired
    private CoinService coinService;

	@Autowired
    private RestTemplate restTemplate;

	private List<Coin> lastCoinList = null;

	private boolean sendFlag = false; // 是否发送报告

	List<Coin> changeCoinList = new ArrayList<Coin>(); // 区块高度变化币种
	List<Coin> noChangeCoinList = new ArrayList<Coin>(); // 区块高度未变化币种
	List<Coin> noticeCoinList = new ArrayList<Coin>(); // 需要注意的币种
	
	@Scheduled(cron = "0 */30 * * * *")
    public void checkIfHasExpiredOrder(){
		
		changeCoinList.clear();
		noChangeCoinList.clear();
		noticeCoinList.clear();
		
		List<Coin> coinList = coinService.findAll();
		for (Coin coin : coinList) {
			String url2 = "http://SERVICE-RPC-" + coin.getUnit() + "/rpc/height";
			if (coin.getEnableRpc().getOrdinal() == 1) {
				coin.setBlockHeight(getRPCBlockHeight(url2, coin.getUnit()));
			}else{
				coin.setBlockHeight(Long.valueOf(0));
			}
		}
		
		// 首次运行
		if(lastCoinList == null) {
			this.lastCoinList = coinList;
			// 初始化状态未改变币种列表
			for (Coin coin : coinList) {
				noChangeCoinList.add(coin);
			}
		}else {
			// 检查上次高度与本次是否有变化
			for (Coin coin : coinList) {
				for (Coin coinLast : lastCoinList) {
					if(coin.getUnit().equals(coinLast.getUnit())) {
						if(coin.getBlockHeight() != coinLast.getBlockHeight()) {
							changeCoinList.add(coin);
						}else {
							noChangeCoinList.add(coin);
							// 区块高度未变化，但是RPC是开启的，说明区块长时间未同步，这是需要注意的
							if(coin.getEnableRpc().getOrdinal() == 1) {
								noticeCoinList.add(coin);
							}
						}
					}
				}
			}
			this.lastCoinList = coinList;
		}
		
		if(noticeCoinList.size() > 0) {
			// 发送邮件通知
			String[] adminList = admins.split(",");
			for(int i = 0; i < adminList.length; i++) {
				try {
					sendEmailMsg(adminList[i], changeCoinList, noChangeCoinList, noticeCoinList, "币种RPC健康检查报告");
				} catch (MessagingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (TemplateException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private Long getRPCBlockHeight(String url, String unit) {
    	try {
	    	ResponseEntity<MessageResult> result = restTemplate.getForEntity(url, MessageResult.class);
	        log.info("result={}", result);
	        if (result.getStatusCode().value() == 200) {
	            MessageResult mr = result.getBody();
	            if (mr.getCode() == 0) {
	                String height = mr.getData().toString();
	                Long longHeight = Long.valueOf(height);
	                return longHeight;
	            }
	        }
	    } catch (IllegalStateException e) {
	        log.error("error={}", e);
	        return Long.valueOf(0);
	    } catch (Exception e) {
	        log.error("error={}", e);
	        return Long.valueOf(0);
	    }
	    return Long.valueOf(0);
    }
	
	@Async
    public void sendEmailMsg(String email, List<Coin> changeList, List<Coin> nochangeList, List<Coin> noticeList, String subject) throws MessagingException, IOException, TemplateException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = null;
        helper = new MimeMessageHelper(mimeMessage, true);
        helper.setFrom(from);
        helper.setTo(email);
        helper.setSubject(company + "-" + subject);
        Map<String, Object> model = new HashMap<>(16);
        model.put("changeList", changeList);
        model.put("nochangeList", nochangeList);
        model.put("noticeList", noticeList);
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_26);
        cfg.setClassForTemplateLoading(this.getClass(), "/templates");
        Template template = cfg.getTemplate("coinCheck.ftl");
        String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
        helper.setText(html, true);

        //发送邮件
        javaMailSender.send(mimeMessage);
    }
}
