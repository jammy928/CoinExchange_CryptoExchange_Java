package com.bizzan.bitrade.constant;

import com.bizzan.bitrade.core.BaseEnum;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 场外订单状态
 *
 * @author GS
 * @date 2017年12月11日
 */
@AllArgsConstructor
@Getter
public enum OrderStatus implements BaseEnum {

    /**
     * 已取消
     */
    CANCELLED("已取消"),
    /**
     * 未付款
     */
    NONPAYMENT("未付款"),
    /**
     * 已付款
     */
    PAID("已付款"),
    /**
     * 已完成
     */
    COMPLETED("已完成"),
    /**
     * 申诉中
     */
    APPEAL("申诉中");

    @Setter
    private String cnName;

    @Override
    @JsonValue
    public int getOrdinal(){
        return this.ordinal();
    }
}
