package com.bizzan.bitrade.constant;

import com.bizzan.bitrade.core.BaseEnum;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author GS
 * @description 用户来源枚举
 * @date 2017/12/26 10:18
 */
@AllArgsConstructor
@Getter
public enum MemberRegisterType implements BaseEnum {
    BACKSTAGE("后台"),
    AUTONOMOUSLY("自主"),
    AUTONOMOUSLY_RECOMMEND("自主(推荐)");

    @Setter
    private String cnName;

    @Override
    @JsonValue
    public int getOrdinal(){
        return this.ordinal();
    }
}
