package com.bizzan.bitrade.constant;

import com.bizzan.bitrade.core.BaseEnum;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 广告类型
 *
 * @author GS
 * @date 2017年12月07日
 */
@AllArgsConstructor
@Getter
public enum AdvertiseType implements BaseEnum {

    /**
     * 购买
     */
    BUY("购买"),

    /**
     * 出售
     */
    SELL("出售");

    @Setter
    private String cnName;

    @Override
    @JsonValue
    public int getOrdinal(){
        return this.ordinal();
    }

}
