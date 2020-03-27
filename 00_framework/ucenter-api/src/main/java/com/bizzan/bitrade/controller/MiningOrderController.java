package com.bizzan.bitrade.controller;

import static com.bizzan.bitrade.constant.SysConstant.SESSION_MEMBER;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.bizzan.bitrade.entity.MiningOrder;
import com.bizzan.bitrade.entity.MiningOrderDetail;
import com.bizzan.bitrade.entity.transform.AuthMember;
import com.bizzan.bitrade.service.MiningOrderDetailService;
import com.bizzan.bitrade.service.MiningOrderService;
import com.bizzan.bitrade.util.DateUtil;
import com.bizzan.bitrade.util.MessageResult;

@RestController
@RequestMapping("miningorder")
public class MiningOrderController extends BaseController {
	@Autowired
	private MiningOrderService miningOrderService;
	
	@Autowired
	private MiningOrderDetailService miningOrderDetailService;
	
	/**
	 * 我的矿机列表
	 * @param member
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	@RequestMapping("my-minings")
    public MessageResult page(@SessionAttribute(SESSION_MEMBER) AuthMember member, @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo, @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        Assert.notNull(member, "The login timeout!");
    	MessageResult mr = new MessageResult();
        Page<MiningOrder> all = miningOrderService.findAllByMemberIdPage(member.getId(), pageNo, pageSize);
        long currentTime = DateUtil.getCurrentDate().getTime();
        for(int i = 0; i < all.getContent().size(); i++) {
        	// 已超时
        	if(currentTime > all.getContent().get(i).getEndTime().getTime()) {
        		all.getContent().get(i).setMiningStatus(2); // 已结束
        	}
        	// 超出天数
        	if(all.getContent().get(i).getMiningedDays() >= all.getContent().get(i).getMiningDays()) {
        		all.getContent().get(i).setMiningStatus(2); // 已结束
        	}
        }
        mr.setCode(0);
        mr.setData(all);
        return mr;
    }
	
	/**
	 * 获取指定矿机详情
	 * @param member
	 * @param miningId
	 * @return
	 */
	@RequestMapping("my-mining-detail")
    public MessageResult miningDetail(@SessionAttribute(SESSION_MEMBER) AuthMember member, Long miningId) {
        Assert.notNull(member, "The login timeout!");
        Assert.notNull(miningId, "矿机不存在!");
    	MiningOrder mo = miningOrderService.findOne(miningId);
    	if(mo != null) {
    		if(mo.getMemberId().longValue() != member.getId()) {
    			return error("非法访问");
    		}
    		long currentTime = DateUtil.getCurrentDate().getTime();
    		if(currentTime > mo.getEndTime().getTime()) {
    			mo.setMiningStatus(2);
    		}
    		if(mo.getMiningedDays() >= mo.getMiningDays()) {
    			mo.setMiningStatus(2);
    		}
    		return success(mo);
    	}else {
    		return error("矿机不存在");
    	}
    }
	
	/**
	 * 矿机产出明细
	 * @param member
	 * @param miningId
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	@RequestMapping("mining-detail")
    public MessageResult miningDetail(@SessionAttribute(SESSION_MEMBER) AuthMember member, 
    								  @RequestParam(value = "miningId", defaultValue = "1") Long miningId,
    								  @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
    								  @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        Assert.notNull(member, "The login timeout!");
        Assert.notNull(miningId, "矿机不存在!");
        MiningOrder mining = miningOrderService.findOne(miningId);
        Assert.notNull(mining, "矿机不存在!");
        if(mining.getMemberId().longValue() != member.getId()) {
        	return error("非法访问");
        }
        Page<MiningOrderDetail> all = miningOrderDetailService.findAllByMiningOrderId(miningId, pageNo, pageSize);
        return success(all);
    }
}
