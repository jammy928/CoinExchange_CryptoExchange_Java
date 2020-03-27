package com.bizzan.bitrade.handler;

import com.alibaba.fastjson.JSON;
import com.bizzan.bitrade.entity.ChatMessageRecord;
import com.cdeer.apns.http2.core.manager.ApnsServiceManager;
import com.cdeer.apns.http2.core.model.Payload;
import com.cdeer.apns.http2.core.model.PushNotification;
import com.cdeer.apns.http2.core.service.ApnsService;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

// @Component
public class ApnsHandler {
	/*
    private Map<String,String> uidTokenMap = new HashMap<>();
    @Autowired
    private ApnsService apnsService;
    private Logger logger = LoggerFactory.getLogger(ApnsHandler.class);

    public void setToken(String uid,String token){
        uidTokenMap.put(uid,token);
    }

    public void removeToken(String uid){
        uidTokenMap.remove(uid);
    }

    public void handleMessage(String uid,ChatMessageRecord message){
        if(StringUtils.isNotEmpty(uid) && uidTokenMap.containsKey(uid)) {
            String token = uidTokenMap.get(uid);
            try {
                sendNotification(token, message);
            }
            catch (Exception e){
                e.printStackTrace();
                logger.error("发送APNS失败："+e.getMessage());
            }
        }
    }


    public void sendNotification(String token,ChatMessageRecord message) {

        Payload payload = new Payload();
        payload.setAlert(message.getNameFrom()+":"+message.getContent());
        //payload.setBadge(1);
        //payload.setSound("aaa.audio");
        payload.addParam("addition", JSON.toJSONString(message));
        PushNotification notification = new PushNotification();
        notification.setPayload(payload);
        notification.setToken(token);
        System.out.println("token="+token+",payload="+payload.toString());
        apnsService.sendNotification(notification);// 异步发送
        //boolean result = service.sendNotificationSynch(notification);// 同步发送

    }
    */
}
