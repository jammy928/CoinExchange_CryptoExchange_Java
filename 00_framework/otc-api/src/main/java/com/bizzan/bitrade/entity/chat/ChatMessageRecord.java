package com.bizzan.bitrade.entity.chat;

import lombok.Data;
import lombok.ToString;

/**
 * mogondb保存聊天消息的格式规范
 */

@Data
@ToString(callSuper = true)
public class ChatMessageRecord extends BaseMessage{

    private String content ;

    private long sendTime ;

    private String sendTimeStr ;


}
