package com.bizzan.bitrade.job;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.bizzan.bitrade.constant.TransactionType;
import com.bizzan.bitrade.entity.MemberTransaction;
import com.bizzan.bitrade.entity.MemberWallet;
import com.bizzan.bitrade.entity.RedEnvelope;
import com.bizzan.bitrade.service.MemberTransactionService;
import com.bizzan.bitrade.service.MemberWalletService;
import com.bizzan.bitrade.service.RedEnvelopeService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CheckRedEnvelopeJob {
	@Autowired
	private RedEnvelopeService redEnvelopeService;

    @Autowired
    private MemberWalletService memberWalletService;
    @Autowired
    private MemberTransactionService transactionService;
	/**
	 * 检查过期红包（5分钟执行一次）
	 * 平台发出的红包过期后，设置状态即可，不用退回资产
	 * 用户发出的红包过期后，设置状态，并且退回用户未被领取的资产
	 */
    @Scheduled(cron = "0 */5 * * * *")
    public void checkExpired(){
    	List<RedEnvelope> list = redEnvelopeService.findAllByState(0);
    	for(int i = 0; i < list.size(); i++) {
    		RedEnvelope redEnvelope = list.get(i);
    		long currentTime = Calendar.getInstance().getTimeInMillis(); // 当前时间戳
    		// 超时订单
	    	if(currentTime >= (redEnvelope.getCreateTime().getTime() + redEnvelope.getExpiredHours() * 60 * 60 * 1000)) {
	    		if(redEnvelope.getReceiveCount() < redEnvelope.getCount()) {
	    			redEnvelope.setState(2); // 过期未领完
	    		}else {
	    			redEnvelope.setState(1); // 已正常领完
	    		}
	    		redEnvelopeService.saveAndFlush(redEnvelope);
	    		if(redEnvelope.getPlateform() == 0) {
	    			redEnvelopeService.saveAndFlush(redEnvelope);
	    			
	    			MemberWallet wallet = memberWalletService.findByCoinUnitAndMemberId(redEnvelope.getUnit(), redEnvelope.getMemberId());
	    			BigDecimal refundAmount = redEnvelope.getTotalAmount().subtract(redEnvelope.getReceiveAmount());
	    			memberWalletService.decreaseFrozen(wallet.getId(), redEnvelope.getReceiveAmount());
	    			//增加资金出记录
	    			MemberTransaction transaction2 = new MemberTransaction();
	                transaction2.setAmount(redEnvelope.getReceiveAmount());
	                transaction2.setSymbol(redEnvelope.getUnit());
	                transaction2.setAddress("");
	                transaction2.setMemberId(wallet.getMemberId());
	                transaction2.setType(TransactionType.EXCHANGE);
	                transaction2.setFee(BigDecimal.ZERO);
	                transaction2.setRealFee("0");
	                transaction2.setDiscountFee("0");
	                transactionService.save(transaction2);
	                
	                // 解冻剩余资产
	                if(refundAmount.compareTo(BigDecimal.ZERO) > 0) {
	                	memberWalletService.thawBalance(wallet, refundAmount);
	                }
	    		}
	    	}else {
	    		// 提前领完
	    		if(redEnvelope.getReceiveCount() == redEnvelope.getCount()) {
	    			redEnvelope.setState(1); // 已正常领完
	    			redEnvelopeService.saveAndFlush(redEnvelope);
	    			if(redEnvelope.getPlateform() == 0) {
		    			MemberWallet wallet = memberWalletService.findByCoinUnitAndMemberId(redEnvelope.getUnit(), redEnvelope.getMemberId());
		    			BigDecimal refundAmount = redEnvelope.getTotalAmount().subtract(redEnvelope.getReceiveAmount());
		    			memberWalletService.decreaseFrozen(wallet.getId(), redEnvelope.getReceiveAmount());
		    			//增加资金出记录
		    			MemberTransaction transaction2 = new MemberTransaction();
		                transaction2.setAmount(redEnvelope.getReceiveAmount());
		                transaction2.setSymbol(redEnvelope.getUnit());
		                transaction2.setAddress("");
		                transaction2.setMemberId(wallet.getMemberId());
		                transaction2.setType(TransactionType.EXCHANGE);
		                transaction2.setFee(BigDecimal.ZERO);
		                transaction2.setRealFee("0");
		                transaction2.setDiscountFee("0");
		                transactionService.save(transaction2);
		                
		                // 解冻剩余资产（理论上这一步不会执行，因为红包被领完，不会产生退款）
		                if(refundAmount.compareTo(BigDecimal.ZERO) > 0) {
		                	memberWalletService.thawBalance(wallet, refundAmount);
		                }
	    			}
	    		}
	    	}
    	}
    }
}
