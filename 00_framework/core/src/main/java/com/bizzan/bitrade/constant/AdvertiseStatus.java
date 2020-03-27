package com.bizzan.bitrade.constant;

import com.bizzan.bitrade.core.BaseEnum;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author GS
 * @description
 * @date 2018/1/8 14:51
 */
@AllArgsConstructor
@Getter
public enum AdvertiseStatus implements BaseEnum {

    /**
     * 已挂单
     */
    HANG("已挂单"),
    /**
     * 待付款
     */
    PAYMENT("待付款"),
    /**
     * 已关闭
     */
    TURNOFF("已关闭");

    @Setter
    private String cnName;

    @Override
    @JsonValue
    public int getOrdinal(){
        return this.ordinal();
    }
}
