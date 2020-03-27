package com.bizzan.bitrade.controller;

import com.bizzan.bitrade.service.AdvertiseService;
import com.bizzan.bitrade.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import com.bizzan.bitrade.entity.*;
import com.bizzan.bitrade.handler.MessageHandler;
import com.bizzan.bitrade.handler.NettyHandler;
import com.bizzan.bitrade.utils.DateUtils;

@RestController
@Slf4j
public class WebSocketController {
    private  final Logger logger = LoggerFactory.getLogger(WebSocketController.class);
    @Autowired
    private NettyHandler nettyHandler;

    /**
     * ORDER-GENERATED:若接收到该内容，发送订单广告的自动回复（如果广告设置了自动回复的话）
     * @param message
     */
    @MessageMapping("/message/chat")
    public void chat(RealTimeChatMessage message){
        logger.info("message={}",message);
        nettyHandler.handleMessage(message);
    }
}
