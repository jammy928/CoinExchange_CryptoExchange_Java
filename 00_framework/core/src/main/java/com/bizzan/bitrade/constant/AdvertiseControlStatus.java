package com.bizzan.bitrade.constant;

import com.bizzan.bitrade.core.BaseEnum;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author GS
 * @date 2018年01月10日
 */
@AllArgsConstructor
@Getter
public enum AdvertiseControlStatus implements BaseEnum {
    /**
     * 上架
     */
    PUT_ON_SHELVES("上架"),
    /**
     * 下架
     */
    PUT_OFF_SHELVES("下架"),
    /**
     * 已关闭（删除）
     */
    TURNOFF("已关闭");

    @Setter
    private String cnName;

    @Override
    @JsonValue
    public int getOrdinal() {
        return this.ordinal();
    }
}
