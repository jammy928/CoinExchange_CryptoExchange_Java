package com.bizzan.bitrade.test.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.bizzan.bitrade.entity.Member;
import com.bizzan.bitrade.service.MemberService;
import com.bizzan.bitrade.test.BaseTest;


public class MemberServiceTest extends BaseTest {

	@Autowired
	private MemberService memberService;
	
	@Test
	public void test() {
        Member member=memberService.findOne(25L);
        System.out.println(">>>>>>>>>>>>>>"+member);
        
	}

}
