package com.bizzan.bitrade.constant;

import com.bizzan.bitrade.core.BaseEnum;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TransactionType implements BaseEnum {
    RECHARGE("充值"),
    WITHDRAW("提现"),
    TRANSFER_ACCOUNTS("转账"),
    EXCHANGE("币币交易"),
    OTC_BUY("法币买入"),
    OTC_SELL("法币卖出"),
    ACTIVITY_AWARD("活动奖励"),
    PROMOTION_AWARD("推广奖励"),
    DIVIDEND("分红"),
    VOTE("投票"),
    ADMIN_RECHARGE("人工充值"),
    MATCH("配对"),
	ACTIVITY_BUY("活动兑换"),
	CTC_BUY("CTC买入"),
	CTC_SELL("CTC卖出"),
	RED_OUT("红包发出"),
	RED_IN("红包领取");

    private String cnName;
    @Override
    @JsonValue
    public int getOrdinal() {
        return this.ordinal();
    }

    public static TransactionType valueOfOrdinal(int ordinal){
        switch (ordinal){
            case 0:return RECHARGE;
            case 1:return WITHDRAW;
            case 2:return TRANSFER_ACCOUNTS;
            case 3:return EXCHANGE;
            case 4:return OTC_BUY;
            case 5:return OTC_SELL;
            case 6:return ACTIVITY_AWARD;
            case 7:return PROMOTION_AWARD;
            case 8:return DIVIDEND;
            case 9:return VOTE;
            case 10:return ADMIN_RECHARGE;
            case 11:return MATCH;
            case 12:return ACTIVITY_BUY;
            case 13:return CTC_BUY;
            case 14:return CTC_SELL;
            case 15:return RED_OUT;
            case 16:return RED_IN;
            default:return null;
        }
    }
    public static int parseOrdinal(TransactionType ordinal) {
        if (TransactionType.RECHARGE.equals(ordinal)) {
            return 0;
        } else if (TransactionType.WITHDRAW.equals(ordinal)) {
            return 1;
        } else if (TransactionType.TRANSFER_ACCOUNTS.equals(ordinal)) {
            return 2;
        } else if (TransactionType.EXCHANGE.equals(ordinal)) {
            return 3;
        } else if (TransactionType.OTC_BUY.equals(ordinal)) {
            return 4;
        } else if (TransactionType.OTC_SELL.equals(ordinal)) {
            return 5;
        } else if (TransactionType.ACTIVITY_AWARD.equals(ordinal)) {
            return 6;
        }else if (TransactionType.PROMOTION_AWARD.equals(ordinal)) {
            return 7;
        }else if (TransactionType.DIVIDEND.equals(ordinal)) {
            return 8;
        }else if (TransactionType.VOTE.equals(ordinal)) {
            return 9;
        }else if (TransactionType.ADMIN_RECHARGE.equals(ordinal)) {
            return 10;
        }else if (TransactionType.MATCH.equals(ordinal)) {
            return 11;
        }else if (TransactionType.ACTIVITY_BUY.equals(ordinal)) {
            return 12;
        }else if (TransactionType.CTC_BUY.equals(ordinal)) {
            return 13;
        }else if (TransactionType.CTC_SELL.equals(ordinal)) {
            return 14;
        }else if (TransactionType.RED_OUT.equals(ordinal)) {
            return 15;
        }else if (TransactionType.RED_IN.equals(ordinal)) {
            return 16;
        }else {
            return 20;
        }
    }

}
