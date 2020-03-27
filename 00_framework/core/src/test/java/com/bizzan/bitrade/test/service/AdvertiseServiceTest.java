package com.bizzan.bitrade.test.service;

import com.alibaba.fastjson.JSON;
import com.bizzan.bitrade.constant.AdvertiseType;
import com.bizzan.bitrade.entity.Advertise;
import com.bizzan.bitrade.entity.Member;
import com.bizzan.bitrade.service.AdvertiseService;
import com.bizzan.bitrade.service.MemberService;
import com.bizzan.bitrade.test.BaseTest;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * @author Shaoxianjun
 * @create 2018年12月07日
 * @desc
 */
public class AdvertiseServiceTest extends BaseTest {
    @Autowired
    private AdvertiseService advertiseService;
    @Autowired
    private MemberService memberService;

    @Test
    public void test(){
        Member member = new Member();
        member.setRealName("张金伟");
        member.setUsername("哔啵哔啵");
        Member member1=memberService.save(member);
        Advertise advertise=new Advertise();
        advertise.setMember(member);
        advertise.setAdvertiseType(AdvertiseType.BUY);
        advertise.setCreateTime(new Date());
        Advertise advertise1 = advertiseService.saveAdvertise(advertise);
        System.out.println(JSON.toJSONString(advertise1));
    }
}
