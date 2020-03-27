package com.bizzan.bitrade.constant;

import com.bizzan.bitrade.core.BaseEnum;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 保证金状态
 * @author Shaoxianjun
 * @date 2019/5/5
 */
@AllArgsConstructor
@Getter
public enum DepositStatusEnum  implements BaseEnum {
    PAY("缴纳"),
    GET_BACK("索回");

    @Setter
    private String cnName;

    @Override
    @JsonValue
    public int getOrdinal(){
        return this.ordinal();
    }
}
