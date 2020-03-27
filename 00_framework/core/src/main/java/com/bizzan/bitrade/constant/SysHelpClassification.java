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
public enum SysHelpClassification implements BaseEnum {

    HELP("新手入门"),

    FAQ("常见问题"),

    EXCHANGE("交易指南"),
    
    COININFO("币种资料"),
    
    ANALYSIS("行情技术"),
    
    PROTOCOL("条款协议"),
    
    OTHER("其他"),
    
    QR_CODE("APP二维码");

    @Setter
    private String cnName;
    @Override
    @JsonValue
    public int getOrdinal() {
        return ordinal();
    }
}
