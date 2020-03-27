package com.bizzan.bitrade.constant;

import com.bizzan.bitrade.core.BaseEnum;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 广告级别
 *
 * @author GS
 * @date 2017年12月13日
 */
@AllArgsConstructor
@Getter
public enum AdvertiseLevel implements BaseEnum {

    /**
     * 普通
     */
    ORDINARY("普通"),
    /**
     * 优质
     */
    EXCELLENT("优质");


    @Setter
    private String cnName;

    @Override
    @JsonValue
    public int getOrdinal() {
        return ordinal();
    }
}
