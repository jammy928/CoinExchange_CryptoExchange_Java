package com.bizzan.bitrade.controller.system;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bizzan.bitrade.controller.common.BaseAdminController;
import com.bizzan.bitrade.dto.MemberBonusDTO;
import com.bizzan.bitrade.entity.Member;
import com.bizzan.bitrade.service.MemberBonusService;
import com.bizzan.bitrade.service.MemberService;
import com.bizzan.bitrade.util.MessageResult;


/**
 * @author Hou ruipeng
 * @date 2018年08月29日
 */
@RestController
@RequestMapping("/system/member-bonus")
@Slf4j
public class MemberBonusController extends BaseAdminController{
    @Autowired
    private MemberBonusService memberBonusService;
    @Autowired
    private MemberService memberService;

    /***
     * 后台系统管理，分红管理查询，条件查询，memberId or phone
     * @param memberId
     * @param phone
     * @param pageNo
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/page-query",method = RequestMethod.POST)
    @RequiresPermissions("system:member-bonus:info")
    public MessageResult findAllCondition(@RequestParam(value = "memberId",required = false)Long memberId,
                                          @RequestParam(value = "phone",required = false)String phone,
                                          @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
                                          @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize){

        if (memberId!=null) {
            log.info("==根据memberId查询所有分红记录===memberId:" + memberId);
            return findByMemberId(memberId,pageNo-1,pageSize);
        }
        if (!StringUtils.isEmpty(phone)) {
            log.info("==根据phone查询所有分红记录===phone:" +phone);
            Member member=memberService.findByPhone(phone);
            return findByMemberId(member.getId(),pageNo-1,pageSize);
        }
        if (memberId==null&&StringUtils.isEmpty(phone)) {
            log.info("=====查询所有分红记录======" );
            Page<MemberBonusDTO> page=memberBonusService.getMemberBounsPage(pageNo-1,pageSize);

            return  success(page);
        }
       return success();
    }
    //根据memberId进行分红查询
    public MessageResult findByMemberId(Long memberId,Integer pageNo,Integer pageSize){
        Page<MemberBonusDTO> page=memberBonusService.getBonusByMemberIdPage(memberId,pageNo,pageSize);
        log.info("id查询结果："+page.getContent());
        return  success(page);
    }
}
