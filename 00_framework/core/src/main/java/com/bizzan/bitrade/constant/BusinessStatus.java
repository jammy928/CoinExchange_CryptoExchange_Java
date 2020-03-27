package com.bizzan.bitrade.constant;

import com.bizzan.bitrade.core.BaseEnum;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author GS
 * @description 交易状态
 * @date 2018/1/8 15:04
 */
@AllArgsConstructor
@Getter
public enum BusinessStatus implements BaseEnum {

    ZERO("为成交"),
    /**
     * 部分成交
     */
    PORTION("部分成交"),
    /**
     * 全部成交
     */
    ALL("全部成交");

    @Setter
    private String cnName;

    @Override
    @JsonValue
    public int getOrdinal(){
        return this.ordinal();
    }
}
