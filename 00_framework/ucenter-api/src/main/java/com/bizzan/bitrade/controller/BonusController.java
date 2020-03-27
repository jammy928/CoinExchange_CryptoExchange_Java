package com.bizzan.bitrade.controller;

import com.alibaba.fastjson.JSONObject;
import com.bizzan.bitrade.dto.MemberBonusDTO;
import com.bizzan.bitrade.service.MemberBonusService;
import com.bizzan.bitrade.util.MessageResult;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Description: 查询个人分红数据
 * @author: GuoShuai
 * @date: create in 11:25 2018/7/3
 * @Modified:
 */
@RestController
@RequestMapping("bonus")
@Slf4j
public class BonusController {
    
    @Autowired
    private MemberBonusService memberBonusService;

    
    
    @RequestMapping(value = "user/page",method = RequestMethod.POST)
    public MessageResult getAllBonusPage(@RequestParam("memberId")long memberId, @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,@RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize){
        log.info("======根据用户ID查询分红记录=======memberID："+memberId);
        JSONObject jsonObject = new JSONObject();
        //根据用户ID查询分红记录
        Page<MemberBonusDTO> bonusDTOList = memberBonusService.getBonusByMemberIdPage(memberId,pageNo-1,pageSize);
        jsonObject.put("bonus",bonusDTOList.getContent());
        //根据用户ID查询累计分红
//        WealthInfo wealthInfo = wealthInfoService.getBonusAmountByMemberId(memberId);
        BigDecimal amount = memberBonusService.getBonusAmountByMemberId(memberId);
        jsonObject.put("amount", amount);
        log.info("======根据用户ID查询分红记录=======result："+bonusDTOList);
        MessageResult mr = MessageResult.success("success");
        mr.setData(jsonObject);
        mr.setTotalElement(bonusDTOList.getTotalElements()+"");
        mr.setTotalPage(bonusDTOList.getTotalPages()+"");
        return mr;
        
    }


    @RequestMapping(value = "user/{memberId}",method = RequestMethod.GET)
    public MessageResult getAllBonus(@PathVariable("memberId")long memberId){
        log.info("======根据用户ID查询分红记录=======memberID："+memberId);
        JSONObject jsonObject = new JSONObject();
        //根据用户ID查询分红记录
        List<MemberBonusDTO> bonusDTOList = memberBonusService.getBonusByMemberId(memberId);
        jsonObject.put("bonus",bonusDTOList);
        //根据用户ID查询累计分红
//        WealthInfo wealthInfo = wealthInfoService.getBonusAmountByMemberId(memberId);
//        BigDecimal bounsAmount =  wealthInfo==null?BigDecimal.valueOf(0):wealthInfo.getBonusAmount();
        BigDecimal amount = memberBonusService.getBonusAmountByMemberId(memberId);
        jsonObject.put("amount", amount);
        log.info("======根据用户ID查询分红记录=======result："+bonusDTOList);
        MessageResult mr = MessageResult.success("success");
        mr.setData(jsonObject);
        return mr;

    }
}
