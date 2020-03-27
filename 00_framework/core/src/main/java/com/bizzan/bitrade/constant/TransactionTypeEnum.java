package com.bizzan.bitrade.constant;

import com.bizzan.bitrade.core.BaseEnum;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public enum TransactionTypeEnum implements BaseEnum {
    OTC_NUM("法币成交量"),
    OTC_MONEY("法币成交额"),
    EXCHANGE("币币交易手续费统计"),
    EXCHANGE_COIN("币币交易成交量统计"),
    EXCHANGE_BASE("币币交易成交额统计"),
    RECHARGE("充币"),
    WITHDRAW("提币");


    @Setter
    private String cnName;

    @Override
    @JsonValue
    public int getOrdinal(){
        return this.ordinal();
    }
}
