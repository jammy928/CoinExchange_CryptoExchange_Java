package com.bizzan.bitrade.constant;

/**
 * 系统常量
 *
 * @author GS
 * @date 2017年12月18日
 */
public class SysConstant {
    /**
     * session常量
     */
    public static final String SESSION_ADMIN = "ADMIN_MEMBER";

    public static final String SESSION_MEMBER = "API_MEMBER";

    /**
     * 验证码
     */
    public static final String PHONE_WITHDRAW_MONEY_CODE_PREFIX = "PHONE_WITHDRAW_MONEY_CODE_PREFIX_";
    
    public static final String PHONE_CTC_TRADE_CODE_PREFIX = "PHONE_CTC_TRADE_CODE_PREFIX_";
    
    public static final String PHONE_REG_CODE_PREFIX = "PHONE_REG_CODE_";

    public static final String PHONE_RESET_TRANS_CODE_PREFIX = "PHONE_RESET_TRANS_CODE_";

    public static final String PHONE_BIND_CODE_PREFIX = "PHONE_BIND_CODE_";

    public static final String PHONE_UPDATE_PASSWORD_PREFIX = "PHONE_UPDATE_PASSWORD_";

    public static final String PHONE_ADD_ADDRESS_PREFIX = "PHONE_ADD_ADDRESS_";
    
    public static final String PHONE_RECEIVE_ENVELOPE_PREFIX = "PHONE_RECEIVE_ENVELOPE_PREFIX"; // 领取红包

    public static final String PHONE_ATTEND_ACTIVITY_PREFIX = "PHONE_ADD_ADDRESS_";
    
    public static final String EMAIL_BIND_CODE_PREFIX = "EMAIL_BIND_CODE_";

    public static final String ADD_ADDRESS_CODE_PREFIX = "ADD_ADDRESS_CODE_";
    public static final String RESET_PASSWORD_CODE_PREFIX = "RESET_PASSWORD_CODE_";
    public static final String PHONE_CHANGE_CODE_PREFIX = "PHONE_CHANGE_CODE_";

    public static final String ADMIN_LOGIN_PHONE_PREFIX = "ADMIN_LOGIN_PHONE_PREFIX_";

    public static final String ADMIN_COIN_REVISE_PHONE_PREFIX = "ADMIN_COIN_REVISE_PHONE_PREFIX_";
    public static final String ADMIN_COIN_TRANSFER_COLD_PREFIX = "ADMIN_COIN_TRANSFER_COLD_PREFIX_";
    public static final String ADMIN_EXCHANGE_COIN_SET_PREFIX = "ADMIN_EXCHANGE_COIN_SET_PREFIX_";

    /** 防攻击验证 */
    public static final String ANTI_ATTACK_ = "ANTI_ATTACK_";
    /**
     * 防止注册机器人
     */
    public static final String ANTI_ROBOT_REGISTER ="ANTI_ROBOT_REGISTER_";
    // 安全与发展基金协会
    public static final String SAFETY_AND_DEVELOPMENT_FUND_ASSOCIATION="SAFETY_AND_DEVELOPMENT_FUND_ASSOCIATION";
    //昨日挖币总数
    public static final String YESTERDAY_MINE_AMOUNT_FOR_BHB="YESTERDAY_MINE_AMOUNT_FOR_BHB";
    //昨日持币分红
    public static final String YESTERDAY_CASH_DIVIDENDS_AMOUNT_FOR_ETH="YESTERDAY_CASH_DIVIDENDS_AMOUNT_FOR_ETH";
    //安全基金 昨日挖币总数 昨日持币分红 过期时间 存为 8个小时 28800s
    public static final int SAFETH_AND_MINE_AND_DIVIDENDS_EXPIRE_TIME= 28800;
    /**
     * 60亿BHB累计(过期时间为15分钟)
     */
    public static final String BHB_AMOUNT="BHB_AMOUNT";
    public static final int BHB_AMOUNT_EXPIRE_TIME=900;


    /**
     * 公告页缓存
     */
    public static final String NOTICE_DETAIL = "notice_detail_";
    public static final int NOTICE_DETAIL_EXPIRE_TIME=300;

    /**
     * 帮助页缓存(首页)
     */
    public static final String SYS_HELP = "SYS_HELP";
    public static final int SYS_HELP_EXPIRE_TIME=300;


    /**
     * 帮助页缓存(类别页)
     */
    public static final String SYS_HELP_CATE = "SYS_HELP_CATE_";
    public static final int SYS_HELP_CATE_EXPIRE_TIME=300;

    /**
     * 帮助页缓存(详情页)
     */
    public static final String SYS_HELP_DETAIL = "SYS_HELP_DETAIL_";
    public static final int SYS_HELP_DETAIL_EXPIRE_TIME=300;

    /**
     * 帮助页缓存(该分类置顶文章)
     */
    public static final String SYS_HELP_TOP = "SYS_HELP_TOP_";
    public static final int SYS_HELP_TOP_EXPIRE_TIME=300;


    //字典表数据缓存
    public static final String DATA_DICTIONARY_BOUND_KEY= "data_dictionary_bound_key_";
    public static final int DATA_DICTIONARY_BOUND_EXPIRE_TIME= 604800;

    //盘口数据
    public static final String EXCHANGE_INIT_PLATE_SYMBOL_KEY="EXCHANGE_INIT_PLATE_SYMBOL_KEY_";
    public static final int EXCHANGE_INIT_PLATE_SYMBOL_EXPIRE_TIME= 18000;


    /**
     * 币竞猜
     */
    //已投注数缓存(期数+类型ID)
    public static final String ALREADY_ORDER = "ALREADY_ORDER_";
    public static final int ALREADY_ORDER_EXPIRE_TIME = 900;
    //币竞猜类型缓存
    public static final String QUIZ_TYPE = "QUIZ_TYPE_";
    public static final int QUIZ_TYPE_EXPIRE_TIME = 18000;
    //币竞猜前三等奖缓存
    public static final String QUIZ_WIN = "QUIZ_WIN_";
    public static final int QUIZ_WIN_EXPIRE_TIME = 18000;
    //币竞猜summary缓存
    public static final String QUIZ_SUMMARY = "QUIZ_SUMMARY_";
    public static final int QUIZ_SUMMARY_EXPIRE_TIME = 18000;
    /**
     * 用户币币交易订单时间限制
     */
    public static final String USER_ADD_EXCHANGE_ORDER_TIME_LIMIT= "USER_ADD_EXCHANGE_ORDER_TIME_LIMIT_";
    public static final int USER_ADD_EXCHANGE_ORDER_TIME_LIMIT_EXPIRE_TIME= 20;
    
    /**
     * 邀请人数排行榜
     */
    public static final String MEMBER_PROMOTION_TOP_RANK = "MEMBER_PROMOTION_TOP_RANK_";
    public static final String MEMBER_PROMOTION_TOP_RANK_DAY = "MEMBER_PROMOTION_TOP_RANK_DAY_";
    public static final String MEMBER_PROMOTION_TOP_RANK_WEEK = "MEMBER_PROMOTION_TOP_RANK_WEEK_";
    public static final String MEMBER_PROMOTION_TOP_RANK_MONTH = "MEMBER_PROMOTION_TOP_RANK_MONTH_";
    public static final int MEMBER_PROMOTION_TOP_RANK_EXPIRE_TIME = 129600; // 排行榜缓存一天半（36小时）
    public static final int MEMBER_PROMOTION_TOP_RANK_EXPIRE_TIME_DAY = 129600; // 排行榜缓存一天半（36小时）
    public static final int MEMBER_PROMOTION_TOP_RANK_EXPIRE_TIME_WEEK = 691200; // 排行榜缓存8天
    public static final int MEMBER_PROMOTION_TOP_RANK_EXPIRE_TIME_MONTH = 2764800; // 排行榜缓存32天
}
