package com.bizzan.bitrade.handler;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aqmd.netty.annotation.HawkBean;
import com.aqmd.netty.annotation.HawkMethod;
import com.aqmd.netty.common.NettyCacheUtils;
import com.aqmd.netty.push.HawkPushServiceApi;
import com.bizzan.bitrade.entity.ChatMessageRecord;
import com.bizzan.bitrade.entity.ConfirmResult;
import com.bizzan.bitrade.entity.MessageTypeEnum;
import com.bizzan.bitrade.entity.RealTimeChatMessage;
import com.bizzan.bitrade.netty.QuoteMessage;
import com.bizzan.bitrade.utils.DateUtils;

import com.bizzan.bitrade.constant.NettyCommand;
import com.bizzan.bitrade.entity.Order;
import com.bizzan.bitrade.service.OrderService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

/**
 * 处理Netty订阅与取消订阅
 */
@HawkBean
public class NettyHandler {
    @Autowired
    private HawkPushServiceApi hawkPushService;
    @Autowired
    private OrderService orderService ;
    @Autowired
    private MessageHandler chatMessageHandler ;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    /*
    @Autowired
    private ApnsHandler apnsHandler;
	*/
    public void subscribeTopic(Channel channel,String topic){
        String userKey = channel.id().asLongText();
        NettyCacheUtils.keyChannelCache.put(channel,userKey);
        NettyCacheUtils.storeChannel(topic,channel);
        if(NettyCacheUtils.userKey.containsKey(userKey)){
            NettyCacheUtils.userKey.get(userKey).add(topic);
        }
        else{
            Set<String> userkeys=new HashSet<>();
            userkeys.add(topic);
            NettyCacheUtils.userKey.put(userKey,userkeys);
        }
    }

    public void unsubscribeTopic(Channel channel,String topic){
        String userKey = channel.id().asLongText();
        if(NettyCacheUtils.userKey.containsKey(userKey)) {
            NettyCacheUtils.userKey.get(userKey).remove(topic);
        }
        NettyCacheUtils.keyChannelCache.remove(channel);
    }

    /*@HawkMethod(cmd = NettyCommand.SUBSCRIBE_CHAT,version = NettyCommand.COMMANDS_VERSION)
    public QuoteMessage.SimpleResponse subscribeChat(byte[] body, ChannelHandlerContext ctx){
        JSONObject json = JSON.parseObject(new String(body));
        System.out.println("订阅："+json.toJSONString());
        QuoteMessage.SimpleResponse.Builder response = QuoteMessage.SimpleResponse.newBuilder();
        String orderId = json.getString("orderId");
        String uid = json.getString("uid");
        if(StringUtils.isEmpty(uid) || StringUtils.isEmpty(orderId)){
            response.setCode(500).setMessage("订阅失败，参数错误");
        }
        else {
            String key = orderId + "-" + uid;
            subscribeTopic(ctx.channel(),key);
            response.setCode(0).setMessage("订阅成功");
        }
        return response.build();
    }*/

    @HawkMethod(cmd = NettyCommand.SUBSCRIBE_GROUP_CHAT)
    public QuoteMessage.SimpleResponse subscribeGroupChat(byte[] body, ChannelHandlerContext ctx){
        JSONObject json = JSON.parseObject(new String(body));
        System.out.println("订阅GroupChat："+json.toJSONString());
        QuoteMessage.SimpleResponse.Builder response = QuoteMessage.SimpleResponse.newBuilder();
        String uid = json.getString("uid");
        if(StringUtils.isEmpty(uid)){
            response.setCode(500).setMessage("订阅失败，参数错误");
        }
        else {
            String key = uid;
            subscribeTopic(ctx.channel(),key);
            response.setCode(0).setMessage("订阅成功");
        }
        return response.build();
    }

    /*@HawkMethod(cmd = NettyCommand.UNSUBSCRIBE_CHAT)
    public QuoteMessage.SimpleResponse unsubscribeChat(byte[] body, ChannelHandlerContext ctx){
        System.out.println(ctx.channel().id());
        JSONObject json = JSON.parseObject(new String(body));
        String orderId = json.getString("orderId");
        String uid = json.getString("uid");
        String key = orderId+"-"+uid;
        unsubscribeTopic(ctx.channel(),key);
        apnsHandler.removeToken(uid);
        QuoteMessage.SimpleResponse.Builder response = QuoteMessage.SimpleResponse.newBuilder();
        response.setCode(0).setMessage("取消订阅成功");
        return response.build();
    }*/

    @HawkMethod(cmd = NettyCommand.UNSUBSCRIBE_GROUP_CHAT)
    public QuoteMessage.SimpleResponse unsubscribeGroupChat(byte[] body, ChannelHandlerContext ctx){
        JSONObject json = JSON.parseObject(new String(body));
        String uid = json.getString("uid");
        String key = uid;
        unsubscribeTopic(ctx.channel(),key);
        //apnsHandler.removeToken(uid);
        QuoteMessage.SimpleResponse.Builder response = QuoteMessage.SimpleResponse.newBuilder();
        response.setCode(0).setMessage("取消订阅成功");
        return response.build();
    }

    @HawkMethod(cmd = NettyCommand.SUBSCRIBE_APNS)
    public QuoteMessage.SimpleResponse subscribeApns(byte[] body, ChannelHandlerContext ctx){
        JSONObject json = JSON.parseObject(new String(body));
        System.out.println("订阅APNS推送："+json.toJSONString());
        QuoteMessage.SimpleResponse.Builder response = QuoteMessage.SimpleResponse.newBuilder();
        String token = json.getString("token");
        String uid = json.getString("uid");
        if(StringUtils.isEmpty(uid) || StringUtils.isEmpty(token)){
            response.setCode(500).setMessage("订阅失败，参数错误");
        }
        else {
            //apnsHandler.setToken(uid,token);
            response.setCode(0).setMessage("订阅成功");
        }
        return response.build();
    }

    @HawkMethod(cmd = NettyCommand.UNSUBSCRIBE_APNS)
    public QuoteMessage.SimpleResponse unsubscribeApns(byte[] body, ChannelHandlerContext ctx){
        JSONObject json = JSON.parseObject(new String(body));
        System.out.println("取消订阅APNS推送："+json.toJSONString());
        String uid = json.getString("uid");
        //apnsHandler.removeToken(uid);
        QuoteMessage.SimpleResponse.Builder response = QuoteMessage.SimpleResponse.newBuilder();
        response.setCode(0).setMessage("取消订阅成功");
        return response.build();
    }

    @HawkMethod(cmd = NettyCommand.SEND_CHAT)
    public QuoteMessage.SimpleResponse sendMessage(byte[] body, ChannelHandlerContext ctx){
        System.out.println("发送消息："+new String(body));
        RealTimeChatMessage message = JSON.parseObject(new String(body), RealTimeChatMessage.class);
        handleMessage(message);
        QuoteMessage.SimpleResponse.Builder response = QuoteMessage.SimpleResponse.newBuilder();
        response.setCode(0).setMessage("发送成功");
        return response.build();
    }

    /**
     * 推送消息
     * @param key
     * @param result
     */
    public void push(String key, Object result,short command) {
        byte[] body = JSON.toJSONString(result).getBytes();
        Set<Channel> channels = NettyCacheUtils.getChannel(key);
        if(channels!=null && channels.size() > 0) {
            System.out.println("下发消息:key="+key+",result="+JSON.toJSONString(result)+",channel size="+channels.size());
            hawkPushService.pushMsg(channels, command, body);
        }
    }

    public void handleMessage(RealTimeChatMessage message){
        if(message.getMessageType()==MessageTypeEnum.NOTICE){
            Order order =  orderService.findOneByOrderId(message.getOrderId());
            ConfirmResult result = new ConfirmResult(message.getContent(),order.getStatus().getOrdinal());
            result.setUidFrom(message.getUidFrom());
            result.setOrderId(message.getOrderId());
            result.setNameFrom(message.getNameFrom());
            //push(message.getOrderId() + "-" + message.getUidTo(),result,NettyCommand.PUSH_CHAT);
            push(message.getUidTo(),result,NettyCommand.PUSH_GROUP_CHAT);
            messagingTemplate.convertAndSendToUser(message.getUidTo(),"/order-notice/"+message.getOrderId(),result);
        }
        else if(message.getMessageType() == MessageTypeEnum.NORMAL_CHAT) {
            ChatMessageRecord chatMessageRecord = new ChatMessageRecord();
            BeanUtils.copyProperties(message, chatMessageRecord);
            chatMessageRecord.setSendTime(DateUtils.getCurrentDate().getTime());
            chatMessageRecord.setFromAvatar(message.getAvatar());
            //聊天消息保存到mogondb
            chatMessageHandler.handleMessage(chatMessageRecord);
            chatMessageRecord.setSendTimeStr(DateUtils.getDateStr(chatMessageRecord.getSendTime()));
            //发送给指定用户（客户端订阅路径：/user/+uid+/+key）
            push(message.getUidTo(),chatMessageRecord,NettyCommand.PUSH_GROUP_CHAT);
            //push(message.getOrderId() + "-" + message.getUidTo(),chatMessageRecord,NettyCommand.PUSH_CHAT);
            //apnsHandler.handleMessage(message.getUidTo(),chatMessageRecord);
            messagingTemplate.convertAndSendToUser(message.getUidTo(), "/" + message.getOrderId(), chatMessageRecord);
        }
    }
}
