package com.bizzan.bitrade.entity;

import com.fasterxml.jackson.annotation.JsonValue;
import com.bizzan.bitrade.core.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
public enum MessageTypeEnum implements BaseEnum{

    /**
     * 提醒对方刷新订单页面
     */
    NOTICE("确认付款"),
    /**
     * 聊天
     */
    NORMAL_CHAT("正常聊天");

    @Setter
    private String cnName;

    @Override
    @JsonValue
    public int getOrdinal(){
        return this.ordinal();
    }
}
