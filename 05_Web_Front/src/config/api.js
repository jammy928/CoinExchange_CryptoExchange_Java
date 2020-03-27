export default {
    common: {
        area: '/uc/support/country'
    },
    uc: { //会员中心接口
        login: '/uc/login',
        register: '/uc/register',
        wallet: '/uc/asset/wallet/',
        captcha: '/uc/start/captcha',
        identification: '/uc/approve/certified/business/status', //商家认证
        apply: '/uc/approve/certified/business/apply', //商家认证申请
        announcement: '/uc/announcement/page', //公告列表
        paydividends: "/uc/bonus/user/page", //持币分红
        mylistrecord: "/uc/mine/detail/", //交易挖矿详情
        activitylist: "/uc/activity/page-query", // 活动列表
        memberactivity: "/uc/activity/getmemberrecords", // 用户参与活动列表
        attendActivity: "/uc/activity/attend", // 参与活动
        mypromotion: "/uc/promotion/mypromotion", // 我的推广佣金
        toppromotion: "/uc/promotion/toprank", // 推广合伙人排行
        getfreecard: "/uc/promotion/promotioncard/getfreecard", // 获取免费推广卡
        exchangecard: "/uc/promotion/promotioncard/exchangecard", // 兑换推广卡
        mycardlist: "/uc/promotion/promotioncard/mycard", // 兑换推广卡
        toppromotionmonth: "/uc/promotion/monthtoprank", // 推广合伙人排行
        toppromotionweek: "/uc/promotion/weektoprank", // 推广合伙人排行
        memberInfo: "/uc/member/my-info", // 获取用户信息
        mypromotionrecord: "/uc/promotion/record", //获取推广邀请人记录
        myInnovationOrderList: "/uc/activity/getmyorders", // 创新实验区参与订单列表
        myInnovationMinings: "/uc/miningorder/my-minings" // 获取我的矿机列表
    },
    market: { //币币交易行情接口
        ws: '/market/market-ws',
        thumb: '/market/symbol-thumb',
        thumbTrend: '/market/symbol-thumb-trend',
        plate: '/market/exchange-plate', //主动查询的盘口信息
        platemini: '/market/exchange-plate-mini', //获取10条数据
        platefull: '/market/exchange-plate-full', //获取所有数据
        trade: '/market/latest-trade', //主动查询的实时成交信息
        symbolInfo: '/market/symbol-info',
        coinInfo: '/market/coin-info',
        indexData: "/market/index_info"
    },
    exchange: { //币币交易委托提交与查询接口
        orderAdd: '/exchange/order/add', //提交订单接口
        current: '/exchange/order/current', //当前委托接口
        history: '/exchange/order/history', //历史委托接口
        detail: '/exchange/order/detail/', //详细订单接口
        favorFind: '/exchange/favor/find', //查询自选
        favorAdd: '/exchange/favor/add', //添加自选
        favorDelete: '/exchange/favor/delete', //删除自选
        orderCancel: '/exchange/order/cancel' //取消委托
    },
    otc: {
        coin: '/otc/coin/all', //查询支持的币种
        advertise: '/otc/advertise/page-by-unit', //获取广告
        createAd: '/uc/ad/create', //创建广告
        adDetail: '/otc/advertise/detail'
    },
    activity: {
        activity: "/activity/page-query"
    }
}
