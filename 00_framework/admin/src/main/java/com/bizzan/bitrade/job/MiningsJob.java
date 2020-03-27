package com.bizzan.bitrade.job;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.bizzan.bitrade.constant.TransactionType;
import com.bizzan.bitrade.entity.Member;
import com.bizzan.bitrade.entity.MemberTransaction;
import com.bizzan.bitrade.entity.MemberWallet;
import com.bizzan.bitrade.entity.MiningOrder;
import com.bizzan.bitrade.entity.MiningOrderDetail;
import com.bizzan.bitrade.service.MemberService;
import com.bizzan.bitrade.service.MemberTransactionService;
import com.bizzan.bitrade.service.MemberWalletService;
import com.bizzan.bitrade.service.MiningOrderDetailService;
import com.bizzan.bitrade.service.MiningOrderService;
import com.bizzan.bitrade.util.DateUtil;
import com.bizzan.bitrade.vendor.provider.SMSProvider;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MiningsJob {
	
	@Autowired
    private SMSProvider smsProvider;

	@Autowired
	private MiningOrderDetailService miningOrderDetailService;
	
	@Autowired
	private MiningOrderService miningOrderService;

	@Autowired
	private MemberWalletService memberWalletService;
	
	@Autowired
	private MemberTransactionService memberTransactionService;
	
	@Autowired
	private MemberService memberService;
	
	/**
	 * 每天晚上10点30发放矿工收益
	 */
	@Scheduled(cron = "0 30 22 * * *")
	public void minings() {
		List<MiningOrder> list = miningOrderService.findAllByMiningStatus(1);
		
		Date currentDate = DateUtil.getCurrentDate();
		for(MiningOrder item : list) {
			if(currentDate.compareTo(item.getEndTime()) < 0) {
				Member member = memberService.findOne(item.getMemberId());
				// 生成收益
				MemberWallet userWallet = memberWalletService.findByCoinUnitAndMemberId(item.getMiningUnit(), item.getMemberId());
				if(userWallet != null) {
					// 资金记录
					MemberTransaction memberTransaction1 = new MemberTransaction();
					memberTransaction1.setFee(BigDecimal.ZERO);
					memberTransaction1.setAmount(item.getCurrentDaysprofit());
					memberTransaction1.setMemberId(item.getMemberId());
			        memberTransaction1.setSymbol(item.getMiningUnit());
			        memberTransaction1.setType(TransactionType.ACTIVITY_BUY);
			        memberTransaction1.setCreateTime(DateUtil.getCurrentDate());
			        memberTransaction1.setRealFee("0");
			        memberTransaction1.setDiscountFee("0");
			        memberTransaction1 = memberTransactionService.save(memberTransaction1);
			        // 更新余额
			        userWallet.setBalance(userWallet.getBalance().add(item.getCurrentDaysprofit()));
			        memberWalletService.save(userWallet);
			        
			        // 更新矿机数据
			        item.setTotalProfit(item.getTotalProfit().add(item.getCurrentDaysprofit()));
			        item.setMiningedDays(item.getMiningedDays() + 1);
			        miningOrderService.saveAndFlush(item);
			        
			        // 添加矿机产出数据
			        MiningOrderDetail detail = new MiningOrderDetail();
			        detail.setMemberId(item.getMemberId());
			        detail.setCreateTime(DateUtil.getCurrentDate());
			        detail.setMiningOrderId(item.getId());
			        detail.setMiningUnit(item.getMiningUnit());
			        detail.setOutput(item.getCurrentDaysprofit());
			        
			        miningOrderDetailService.saveAndFlush(detail);
			        
			        // 发送短信通知用户
			        try {
		 				smsProvider.sendCustomMessage(member.getMobilePhone(), "尊敬的用户，您的矿机【"+ item.getTitle() + "】今日挖矿产出：" + item.getCurrentDaysprofit() + " "+item.getMiningUnit() + "，请查收！");
		 			} catch (Exception e) {
		 				e.printStackTrace();
		 			}
				}
			}
		}
	}
}
