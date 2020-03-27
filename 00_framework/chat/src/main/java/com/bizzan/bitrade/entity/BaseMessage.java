package com.bizzan.bitrade.entity;

import lombok.Data;
import lombok.ToString;

/**
 * 聊天消息的统一父类规范格式
 */

@Data
@ToString
public class BaseMessage {

    private String orderId ;

    private String uidTo ;

    private String uidFrom ;

    private String nameTo ;

    private String nameFrom ;

    private MessageTypeEnum messageType = MessageTypeEnum.NORMAL_CHAT;
}
