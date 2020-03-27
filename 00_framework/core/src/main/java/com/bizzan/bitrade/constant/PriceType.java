package com.bizzan.bitrade.constant;

import com.bizzan.bitrade.core.BaseEnum;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author GS
 * @date 2017年12月23日
 */
@AllArgsConstructor
@Getter
public enum PriceType implements BaseEnum {
    /**
     * 固定的
     */
    REGULAR("固定的"),
    /**
     * 变化的
     */
    MUTATIVE("变化的");
    @Setter
    private String cnName;

    @Override
    @JsonValue
    public int getOrdinal() {
        return ordinal();
    }
}
