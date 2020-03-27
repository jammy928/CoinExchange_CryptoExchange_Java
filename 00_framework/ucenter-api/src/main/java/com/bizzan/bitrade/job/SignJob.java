package com.bizzan.bitrade.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.bizzan.bitrade.constant.SignStatus;
import com.bizzan.bitrade.entity.Sign;
import com.bizzan.bitrade.service.MemberService;
import com.bizzan.bitrade.service.SignService;
import com.bizzan.bitrade.util.DateUtil;

import java.util.Date;

/**
 * @author GS
 * @Description: 关于签到活动的定时任务
 * @date 2018/5/410:41
 */
@Component
@Slf4j
public class SignJob {
    @Autowired
    private MemberService memberService;
    @Autowired
    private SignService signService;

    // 判断 签到活动是否到期 未到期则修改会员签到能力
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void job() {
        Sign sign = signService.fetchUnderway();
        if (sign == null){
            log.info("sign = { null }");
            return;
        }
        log.info("sign = {}" ,sign);
        memberService.resetSignIn();//赋予签到能力
        //判断今天活动是否到期
        int compare = DateUtil.compare(sign.getEndDate(), new Date());
        log.info("比较时间" );
        log.info("compare = {}" ,compare);
        if (compare != 1) {
            sign.setStatus(SignStatus.FINISH);//当前时间小于等于是结束时间 关闭签到活动
        }
    }

}
