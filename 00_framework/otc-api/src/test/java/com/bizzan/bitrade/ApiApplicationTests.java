package com.bizzan.bitrade;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.bizzan.bitrade.dao.AdvertiseDao;
import com.bizzan.bitrade.event.MemberEvent;
import com.bizzan.bitrade.service.AdvertiseService;
import com.bizzan.bitrade.service.OtcCoinService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApiApplicationTests {
    @Autowired
    private OtcCoinService otcCoinService;
    @Autowired
    private AdvertiseService advertiseService;
    @Autowired
    private AdvertiseDao advertiseDao;
    //@Autowired
    //private JavaMailSender javaMailSender;
    @Autowired
    private MemberEvent memberEvent;

    @Test
    public void testConfig() {
        System.out.print(advertiseService.findOne(1L).getCreateTime());
    }

    /**
     * 测试锁
     */
   /* @Test
    public void testLock() throws InterruptedException {
        Long id = 1L;
        Advertise advertise = advertiseService.findOne(id);
        advertise.setCountry("美国");
        new Thread(
                () -> {
                    Advertise advertise1 = advertiseService.findOne(id);
                    advertise1.setCountry("伊拉克");
                    advertise1 = advertiseDao.saveAndFlush(advertise1);
                    System.out.println("ad1 " + advertise1);
                }).start();
        Thread.sleep(5000);
        advertise = advertiseDao.saveAndFlush(advertise);
        System.out.println(advertise);
        Thread.sleep(20000);
    }*/

    /**
     * 测试发送邮件
     */
   /* @Test
    public void testSendEmail() throws Exception {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        //基本设置.
        helper.setFrom("308672374@qq.com");//发送者.
        helper.setTo("546302121@qq.com");//接收者.
        helper.setSubject("会员注册成功（某某网站平台）");//邮件主题.
        Map<String, Object> model = new HashMap<>();
        model.put("username", "张金伟");
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_26);
        cfg.setClassForTemplateLoading(this.getClass(), "/templates");
        Template template   = cfg.getTemplate("activateEmail.ftl");
        String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
        helper.setText(html, true);
        javaMailSender.send(mimeMessage);}*/



    /*@Test
    public void testRegisterEvent(){
        Member member = new Member();
        member.setId(1L);
        memberEvent.onRegisterSuccess(member);
    }*/

    /*@Test
    public void testFindAd() throws SQLException, DataException {
        String unit = "GCX";
        OtcCoin otcCoin = otcCoinService.findByUnit(unit);
        double marketPrice = 1000.00;
        int pageNo = 1;
        int pageSize = 10;
        AdvertiseType advertiseType = AdvertiseType.SELL;
        SpecialPage<ScanAdvertise> page = advertiseService.paginationAdvertise(pageNo, pageSize, otcCoin, advertiseType, marketPrice);
        System.out.println(page);
    }*/
}
