package com.bizzan.bitrade.entity;

import lombok.Data;
import lombok.ToString;

/**
 * 客户端请求历史消息时的传参规范
 */

@Data
@ToString(callSuper = true)
public class HistoryChatMessage extends BaseMessage{

    private int limit = 20 ;

    private String sortFiled = "sendTime";

    private int page = 1;

}
