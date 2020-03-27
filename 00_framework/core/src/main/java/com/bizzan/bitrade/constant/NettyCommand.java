package com.bizzan.bitrade.constant;

public class NettyCommand {
    public static final int COMMANDS_VERSION = 1;
    public static final short SUBSCRIBE_SYMBOL_THUMB = 20001;
    public static final short UNSUBSCRIBE_SYMBOL_THUMB = 20002;
    public static final short PUSH_SYMBOL_THUMB = 20003;

    public static final short SUBSCRIBE_EXCHANGE = 20021;
    public static final short UNSUBSCRIBE_EXCHANGE = 20022;
    public static final short PUSH_EXCHANGE_TRADE = 20023;
    public static final short PUSH_EXCHANGE_PLATE = 20024;
    public static final short PUSH_EXCHANGE_KLINE = 20025;
    public static final short PUSH_EXCHANGE_ORDER_COMPLETED = 20026;
    public static final short PUSH_EXCHANGE_ORDER_CANCELED = 20027;
    public static final short PUSH_EXCHANGE_ORDER_TRADE = 20028;
    public static final short PUSH_EXCHANGE_DEPTH = 20029;

    public static final short SUBSCRIBE_CHAT = 20031;
    public static final short UNSUBSCRIBE_CHAT = 20032;
    public static final short PUSH_CHAT = 20033;

    public static final short SEND_CHAT = 20034;
    public static final short SUBSCRIBE_GROUP_CHAT = 20035;
    public static final short UNSUBSCRIBE_GROUP_CHAT = 20036;
    public static final short SUBSCRIBE_APNS = 20037;
    public static final short UNSUBSCRIBE_APNS = 20038;
    public static final short PUSH_GROUP_CHAT = 20039;
}
