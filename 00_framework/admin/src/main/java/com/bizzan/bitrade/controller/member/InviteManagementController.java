package com.bizzan.bitrade.controller.member;

import com.bizzan.bitrade.annotation.AccessLog;
import com.bizzan.bitrade.constant.AdminModule;
import com.bizzan.bitrade.controller.BaseController;
import com.bizzan.bitrade.entity.Member;
import com.bizzan.bitrade.entity.MemberInviteStastic;
import com.bizzan.bitrade.service.InviteManagementService;
import com.bizzan.bitrade.service.MemberInviteStasticService;
import com.bizzan.bitrade.util.MessageResult;
import com.bizzan.bitrade.vo.InviteManagementVO;
import com.bizzan.bitrade.vo.MemberInviteStasticVO;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.List;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("invite/management")
public class InviteManagementController extends BaseController {

    @Autowired
    private InviteManagementService inviteManagementService;
    
    @Autowired
    private MemberInviteStasticService memberInviteStasticService;

    /**
     * 邀请管理默认查询所有的用户
     *
     * @return
     */
    @RequiresPermissions("invite:management:query")
    @AccessLog(module = AdminModule.CMS, operation = "邀请管理默认查询所有的用户")
    @RequestMapping(value = "look", method = RequestMethod.POST)
    public MessageResult lookAll(@RequestBody InviteManagementVO inviteManagementVO) {
        log.info("默认查询所有的用户 lookAll ={}", inviteManagementVO);
        Page<Member> page = inviteManagementService.lookAll(inviteManagementVO);
        return success(page);
    }

    /**
     * 条件查询
     */
    @AccessLog(module = AdminModule.CMS, operation = "邀请管理多条件查询")
    @RequestMapping(value = "query", method = RequestMethod.POST)
    public MessageResult queryCondition(@RequestBody InviteManagementVO inviteManagementVO) {
        log.info("默认查询所有的用户 QueryCondition ={}", inviteManagementVO);
        Page<Member> page = inviteManagementService.queryCondition(inviteManagementVO);
        return success(page);
    }

    /**
     * 根据id查询一级二级用户
     */
    @AccessLog(module = AdminModule.CMS, operation = "根据id查询一级二级用户")
    @RequestMapping(value = "info", method = RequestMethod.POST)
    public MessageResult queryId(@RequestBody InviteManagementVO inviteManagementVO) {
        log.info("根据id查询一级二级用户 queryById={}", inviteManagementVO);
        Page<Member> page = inviteManagementService.queryId(inviteManagementVO);
        return success(page);
    }
    @RequiresPermissions("invite:management:rank")
    @AccessLog(module = AdminModule.CMS, operation = "邀请排名条件查询")
    @RequestMapping(value = "rank", method = RequestMethod.POST)
    public MessageResult queryRankList(@RequestBody MemberInviteStasticVO memberInviteStasticVO) {
    	// type: 0 = 人数排名   // type: 1 = 佣金排名
    	Page<MemberInviteStastic> page = memberInviteStasticService.queryCondition(memberInviteStasticVO);
    	List<MemberInviteStastic> list= page.getContent();
    	for(MemberInviteStastic item : list) {
    		item.setUserIdentify(item.getIsRobot() + "-" + item.getUserIdentify());
    	}
    	return success(page);
    }
    @RequiresPermissions("invite:management:update-rank")
    @AccessLog(module = AdminModule.CMS, operation = "更新邀请信息")
    @PostMapping("update-rank")
    public MessageResult updateRank(@RequestParam("id") Long id,
    								@RequestParam("estimatedReward") BigDecimal estimatedReward,
    								@RequestParam("extraReward") BigDecimal extraReward,
    								@RequestParam("levelOne") Integer levelOne,
    								@RequestParam("levelTwo") Integer levelTwo) {
    	log.info(id.toString());
    	MemberInviteStastic detail = memberInviteStasticService.findById(id);
    	if(detail == null) {
    		return error("该排名用户不存在");
    	}
    	if(estimatedReward != null) {
    		detail.setEstimatedReward(estimatedReward);
    	}
    	if(extraReward != null) {
    		detail.setExtraReward(extraReward);
    	}
    	if(levelOne != null) {
    		detail.setLevelOne(levelOne);
    	}
    	if(levelTwo != null) {
    		detail.setLevelTwo(levelTwo);
    	}
    	
    	memberInviteStasticService.save(detail);
    	
    	return success(detail);
    }
    @RequiresPermissions("invite:management:detail-rank")
    @AccessLog(module = AdminModule.CMS, operation = "邀请信息详情")
    @RequestMapping(value = "detail-rank", method = RequestMethod.POST)
    public MessageResult updateRank(@RequestParam(value = "id", defaultValue="0") Long id) {
    	
    	MemberInviteStastic detail = memberInviteStasticService.findById(id);
    	
    	return success(detail);
    }
}
