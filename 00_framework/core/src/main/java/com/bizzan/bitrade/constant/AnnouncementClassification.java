package com.bizzan.bitrade.constant;

import com.bizzan.bitrade.core.BaseEnum;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author GS
 * @description
 * @date 2018/1/9 9:42
 */
@AllArgsConstructor
@Getter
public enum AnnouncementClassification implements BaseEnum {

    NORMAL("一般公告"),

    ACTIVITY("活动公告"),

    ASSETS("充提公告"),
    
    SYSTEM("系统公告"),
    
    LIST("上币公告"),
    
    APPROVE("投票公告"),
    
    OTHER("其他");

    @Setter
    private String cnName;
    @Override
    @JsonValue
    public int getOrdinal() {
        return ordinal();
    }
}
