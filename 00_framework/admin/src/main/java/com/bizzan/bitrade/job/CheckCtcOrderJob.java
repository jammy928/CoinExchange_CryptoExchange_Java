package com.bizzan.bitrade.job;


import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.bizzan.bitrade.entity.CtcOrder;
import com.bizzan.bitrade.entity.MemberWallet;
import com.bizzan.bitrade.service.CtcAcceptorService;
import com.bizzan.bitrade.service.CtcOrderService;
import com.bizzan.bitrade.service.MemberWalletService;
import com.bizzan.bitrade.util.DateUtil;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CheckCtcOrderJob {
	@Autowired
    private CtcOrderService ctcOrderService;
	
	@Autowired
    private CtcAcceptorService ctcAcceptorService;
	
	@Autowired
    private MemberWalletService memberWalletService;
	
    /**
     * 每2分钟运行(检查是否有超时未处理订单）
     */
    @Scheduled(cron = "0 */2 * * * *")
    public void checkIfHasExpiredOrder(){
    	List<CtcOrder> orderList0 = ctcOrderService.findAllByStatus(0); // 未接单订单
    	List<CtcOrder> orderList1 = ctcOrderService.findAllByStatus(1); // 已接单订单
    	
    	Date currentDate = DateUtil.getCurrentDate();
    	for(CtcOrder order : orderList0) {
    		// 用户买入卖出，承兑商30分钟不接单
			// 超时30分钟自动取消(1000 * 60 * 35) -> 放大到35分钟
			if(currentDate.getTime() - order.getCreateTime().getTime() > 2100000) {
				// 仅卖出时才会解冻用户资产
				if(order.getDirection() == 1) {
					//解冻资产
					MemberWallet memberWallet = memberWalletService.findByCoinUnitAndMemberId(order.getUnit(), order.getMember().getId());
					if(memberWallet == null) {
						continue;
					}
					if(memberWallet.getFrozenBalance().compareTo(order.getAmount()) < 0) {
						continue;
					}
					memberWalletService.thawBalance(memberWallet, order.getAmount());
				}
				order.setStatus(4);
				order.setCancelReason("超时系统自动取消");
				order.setCancelTime(DateUtil.getCurrentDate());
				ctcOrderService.saveAndFlush(order);
			}
    	}
    	
    	for(CtcOrder order : orderList1) {
    		// 用户买入，并且承兑商已接单，但是用户一直不付款，35分钟后自动取消
    		if(order.getStatus() == 1 && order.getDirection() == 0) {
    			// 超时30分钟自动取消(1000 * 60 * 35) -> 放大到35分钟
    			if(currentDate.getTime() - order.getConfirmTime().getTime() > 2100000) {
    				order.setStatus(4);
    				order.setCancelReason("超时系统自动取消");
    				order.setCancelTime(DateUtil.getCurrentDate());
    				ctcOrderService.saveAndFlush(order);
    			}
    		}
    	}
    }
}