import { BASEURL, fetch, post, patch, put, postConfig } from './http.js'

export const BASICURL = BASEURL;
// export const uploadPic = data => post('', data);
// export const getALL = () => post(地址);  post
// export const getALL = () => fetch(地址);  get
// post  帮助管理
export const helpDown = data => post('admin/cms/system-help/down', data);
// post 公告管理
export const manageDown = data => post('admin/system/announcement/down', data);
//post "币币管理" => "转账地址所有币种"
export const getCoinName = () => post('admin/system/coin/all-name-and-unit');
//post "币币管理" => "导出excel"
export const coinOutExcel = () => fetch('admin/otc/order/out-excel');

//GET获取 "财务管理"=>"提币管理(记录)"
export const withdrawManage = data => fetch('admin/finance/withdraw-record/page-query', data);

//Patch获取 "财务管理"=>"提币管理"   "一键通过"
export const auditPass = data => patch('admin/finance/withdraw-record/audit-pass', data);

//Patch获取 "财务管理"=>"提币管理"   "一键不通过"
export const auditNoPass = data => patch('admin/finance/withdraw-record/audit-no-pass', data);

//POST获取 "财务管理"=>"充币明细"
export const memberDeposit = data => post('admin/finance/member-deposit/page-query', data);

//POST？？？获取 "首页"=>上部面板
export const dashBoardInfo = data => post('admin/system/statistics/dashboard', data);

//GET获取 "审核详情"=>"个人交易详细信息"
export const personalTradeInfo = data => fetch(`admin/finance/withdraw-record/${data}`);

//GET获取 "审核详情"=>"个人所有交易记录"
export const allTradeInfo = () => post('admin/finance/member-transaction/all');

//GET获取 "财务管理"=>"个人记录"
export const perTradeAll = data => post('admin/finance/member-transaction/page-query', data);

//POST获取 "会员实名审核"
export const MemberRealNameList = data => post('admin/member/member-application/page-query', data);

//POST获取 "会员实名审核详情页"
export const MemberRealNameDetail = data => post('admin/member/member-application/detail', data);

//POST获取 "会员实名审核通过"
export const memberCheckPass = data => patch(`admin/member/member-application/${data}/pass`);

//POST获取 "会员实名审核不通过"
export const memberCheckNotPass = (url, data) => patch(`admin/member/member-application/${url}/no-pass`, data);

//POST获取 "内容管理" => "广告管理"
export const manageAd = data => post('admin/cms/system-advertise/page-query', data);

//POST "内容管理" => "添加广告"
export const createAd = data => post('admin/cms/system-advertise/create', data);

//POST "内容管理" => "删除广告"
export const deleteAd = data => post('admin/cms/system-advertise/deletes', data);

//Post "内容管理" => "广告详情"
export const adDetail = data => post('admin/cms/system-advertise/detail', data);

//Post "内容管理" => "更新广告"
export const updateAd = data => post('admin/cms/system-advertise/update', data);

//Post "内容管理" => "帮助管理"
export const helpManage = data => post('admin/cms/system-help/page-query', data);

//Post "内容管理" => "帮助管理"=>"置顶"
export const stickTopHelp = data => post('admin/cms/system-help/top', data);

//Post "内容管理" => "添加帮助管理"
export const addHelpManage = data => post('admin/cms/system-help/create', data);

//Post "内容管理" => "删除帮助管理"
export const delHelpManage = data => post('admin/cms/system-help/deletes', data);

//Post "内容管理" => "查看帮助管理"
export const helpManageDetail = data => post('admin/cms/system-help/detail', data);

//Post "内容管理" => "更新帮助管理"
export const updateHelpManage = data => post('admin/cms/system-help/update', data);

//Post "内容管理" => "公告管理"=>"置顶"
export const stickTopAnnounce = data => post('admin/system/announcement/top', data);

//Post "会员管理" => "会员管理"
export const memberManage = data => post('admin/member/page-query', data);

//Post "会员管理" => "会员详情"
export const memberDetail = data => post('admin/member/detail', data);

//GET "内容管理" => "公告管理"
export const announceManage = data => fetch('admin/system/announcement/page-query', data);

//Post "内容管理" => "添加公告管理"
export const addAnnounce = data => post('admin/system/announcement/create', data);

//Post "内容管理" => "删除公告"
export const delAnnounce = data => patch('admin/system/announcement/deletes', data);

// GET "内容管理" => "公告详情"
export const announceDetail = parma => fetch(`admin/system/announcement/${parma}/detail`);

//Put "内容管理" => "修改公告管理"
export const updateAnnounce = (urlID, data) => put(`admin/system/announcement/${urlID}/update`, data);

//Post "内容管理" => "广告管理"=>"置顶"
export const stickTopAdv = data => post('admin/cms/system-advertise/top', data);

// POST "系统管理" => "角色管理"
export const roleManage = () => post('admin/system/role/all');

// POST "系统管理" => "角色管理"->"查询角色权限"
export const queryRolePermission = data => post('admin/system/role/permission', data);

// POST "系统管理" => "角色管理"->"添加或修改角色信息"
export const addAuditRole = data => post('admin/system/role/merge', data);

// POST "系统管理" => "角色管理"->"删除角色"
export const deleteRole = data => post('admin/system/role/deletes', data);

// POST "系统管理" => "部门管理"=>"所有部门"
export const departmentManage = () => post('admin/system/department/all');

// POST "系统管理" => "部门管理"->"添加或修改部门信息"
export const addAuditDepart = data => post('admin/system/department/merge', data);

// POST "系统管理" => "部门管理"->"查看部门详细信息"
export const departDetail = data => post('admin/system/department/detail', data);

// POST "系统管理" => "部门管理"->"删除部门"
export const delDepart = data => post('admin/system/department/deletes', data);

// POST "获取全部权限"
export const getAllPermission = () => post('admin/system/role/permission/all');

//POST "系统管理" => "权限管理"
export const permissionManage = data => post('admin/system/permission/page-query', data);

//POST "系统管理" => "添加编辑权限"
export const addAuditPermission = data => post('admin/system/permission/merge', data);

//POST "系统管理" => "删除权限"
export const delPermission = data => post('admin/system/permission/deletes', data);

// GET "法币管理" => "商家审核"
export const businessAudit = (parma, data) => patch(`admin/member/${parma}/audit-business`, data);

// GET "法币管理" => "商家审核"=>"申请资料信息"
export const businessDetail = (url, data) => fetch(`admin/member/${url}/business-auth-detail`, data);

//POST "法币管理" => "币种管理"->"删除"
export const delOtcCoin = data => post('admin/otc/otc-coin/deletes', data);

//POST "法币管理" => "后台广告"
export const queryOtcAdv = data => post('admin/otc/advertise/page-query', data);

//POST "法币管理" => "后台广告"=>"广告上/下架"
export const upDownAdv = data => post('admin/otc/advertise/alter-status', data);

//POST "法币管理" => "后台申诉"
export const queryAppeal = data => post('admin/otc/appeal/page-query', data);

//POST "法币管理" => "后台申诉"=> "放币"
export const releaseAppealCoin = data => post('admin/otc/appeal/release-coin', data);

//POST "法币管理" => "后台申诉"=> "取消订单"
export const cancelAppealOrder = data => post('admin/otc/appeal/cancel-order', data);

//POST "法币管理" => "后台申诉"=> "订单管理"
export const queryOtcOrder = data => post('admin/otc/order/page-query', data);

//POST "法币管理" => "币种管理"
export const queryOtcCoin = data => post('admin/otc/otc-coin/page-query', data);

//POST "法币管理" => "币种管理"=>"添加币种"
export const addOtcCoin = data => post('admin/otc/otc-coin/create', data);

//POST "法币管理" => "币种管理"=>"更新币种"
export const updateOtcCoin = data => post('admin/otc/otc-coin/update', data);

//GET "法币管理" => "充值管理"
export const rechargeOtcCoin = data => fetch('admin/legal-wallet-recharge/page', data);

//GET "法币管理" => "充值管理"=>"详情"
export const otcCoinDetail = (url, data) => fetch(`admin/legal-wallet-recharge/${url}`, data);

//GET "法币管理" => "充值管理"=>"通过"
export const rechargeOtcCoinPass = (url, data) => patch(`admin/legal-wallet-recharge/${url}/pass`, data);

//GET "法币管理" => "充值管理"=>"不通过"
export const rechargeOtcCoinNoPass = (url, data) => patch(`admin/legal-wallet-recharge/${url}/no-pass`, data);

//POST "法币管理" => "充值管理"=>"合法币种"
export const legalOtcCoin = () => post('admin/system/coin/all-name/legal');

//GET "法币管理" => "提现管理"
export const withdrawOtcCoin = data => fetch('admin/legal-wallet-withdraw/page', data);

//GET "法币管理" => "提现管理"=>"详情"
export const withdrawOtcCoinDetail = (url, data) => fetch(`admin/legal-wallet-withdraw/${url}`, data);

//GET "法币管理" => "提现管理"=>"通过"
export const withdrawOtcCoinPass = (url, data) => patch(`admin/legal-wallet-withdraw/${url}/pass`, data);

//GET "法币管理" => "提现管理"=>"不通过"
export const withdrawOtcCoinNoPass = (url, data) => patch(`admin/legal-wallet-withdraw/${url}/no-pass`, data);

//GET "法币管理" => "提现管理"=>"确认打款"
export const withdrawOtcCoinRemit = (url, data) => patch(`admin/legal-wallet-withdraw/${url}/remit`, data);

//POST "币币管理" => "币币订单"->"详情"
export const exgOrderDetail = data => post('admin/exchange/exchange-order/detail', data);

//POST "币币管理" => "币币订单"->"删除"
export const delBBSetting = data => post('admin/exchange/exchange-coin/deletes', data);

//POST "币币管理" => "币币设置"
export const queryBBSetting = data => post('admin/exchange/exchange-coin/page-query', data);

//POST "币币管理" => "币币设置"=>"添加交易对"
export const addBBSetting = data => post('admin/exchange/exchange-coin/merge', data);

//POST "币币管理" => "币币设置"=>"修改"
export const fixBBSetting = data => post('admin/exchange/exchange-coin/alter-rate', data);

//POST "币币管理" => "币币设置"=>"启动交易引擎"
export const startBBTrader = data => post('admin/exchange/exchange-coin/start-trader', data);

//POST "币币管理" => "币币设置"=>"停止交易引擎"
export const stopBBTrader = data => post('admin/exchange/exchange-coin/stop-trader', data);

//POST "币币管理" => "币币设置"=>"查看机器人参数"
export const getRobotConfig = data => post('admin/exchange/exchange-coin/robot-config', data);

//POST "币币管理" => "币币设置"=>"设置机器人参数"
export const setRobotConfig = data => post('admin/exchange/exchange-coin/alter-robot-config', data);
//POST "币币管理" => "币币设置"=>"创建机器人参数"
export const createRobotConfig = data => post('admin/exchange/exchange-coin/create-robot-config', data);

//POST "币币管理" => "币币设置"=>"设置机器人参数"
export const setPriceRobotConfig = data => post('admin/exchange/exchange-coin/alter-robot-config-price', data);
//POST "币币管理" => "币币设置"=>"创建机器人参数"
export const createPriceRobotConfig = data => post('admin/exchange/exchange-coin/create-robot-config-price', data);

//POST "币币管理" => "币币设置"=>"撤销所有委托单"
export const cancelBBAllOrders = data => post('admin/exchange/exchange-coin/cancel-all-order', data);

//POST "币币管理" => "币币设置"=>"查看撮合引擎概况"
export const overviewBB = data => post('admin/exchange/exchange-coin/exchange-overview', data);

//POST "币币管理" => "币币订单"
export const queryBBOrder = data => post('admin/exchange/exchange-order/page-query', data);

//POST "会员管理" => "会员资产"
export const memberAsset = data => post('admin/member/member-wallet/balance', data);

//patch "财务管理" => "放币"
export const passCoin = data => patch('admin/finance/withdraw-record/remittance', data);

//patch "财务管理" => "单个放币"
export const passCoinByOne = data => patch('admin/finance/withdraw-record/add-transaction-number', data);

//post "用户管理" => "用户查询"
export const queryEmployee = data => post('admin/system/employee/page-query', data);

//post "用户管理" => "新增或者修改用户"
export const addAuditEmployee = data => post('admin/system/employee/merge', data);

//post "用户管理" => "用户详情"
export const employeeDetail = data => post('admin/system/employee/detail', data);

//post "用户管理" => "删除用户"
export const delEmployee = data => post('admin/system/employee/deletes', data);

//post "首页" => "待处理事务（法币管理）"
// export const queryOtc = data => post('admin/otc/order/page-query', data);

//post "会员管理" => "会员详情"=>"人工充币"
export const manualPay = data => post('admin/member/member-wallet/recharge', data);

//post "首页" => "待办事项"=>"dashboard"
export const orderNum = () => post('admin/otc/order/get-order-num');

//post "会员管理" => "会员详情"=>"锁定钱包"
export const lockWallet = data => post('admin/member/member-wallet/lock-wallet', data);

//post "会员管理" => "会员详情"=>"解锁钱包"
export const unlockWallet = data => post('admin/member/member-wallet/unlock-wallet', data);

//post "币币管理" => "委托管理"=>"撤销"
export const cancelOrder = data => post('admin/exchange/exchange-order/cancel', data);

//post "个人中心" => "修改密码"
export const fixPersonalPW = data => post('admin/system/employee/update-password', data);

//post "系统管理" => "系统日志"
export const accessLog = (url, data) => fetch(`admin/system/access-log/page-query/${url}`, data);

//post "系统管理" => "新增投票"
export const addVote = (data, config) => postConfig('admin/system/vote/merge', data, config);

//post "系统管理" => "查询投票"
export const queryVote = data => post('admin/system/vote/page-query', data);

//post "系统管理" => "查询分红"
// export const queryDividend = data => post('admin/system/dividend/page-query', data);

//post "系统管理" => "开始分红"
export const startDividend = data => post('admin/system/dividend/start', data);

//post "系统管理" => "查询手续费"
export const queryDividendFee = data => post('admin/system/dividend/fee/info', data);

//post "系统管理" => "查询币种"
export const querySysCoin = data => post('admin/system/coin/page-query', data);

//post "系统管理" => "添加币种"
export const addSysCoin = data => post('admin/system/coin/create', data);

//post "系统管理" => "更新币种"
export const updateSysCoin = data => post('admin/system/coin/update', data);

//post "系统管理" => "币种详情"
export const sysCoinDetail = data => post('admin/system/coin/detail', data);

//post "币币管理" => "转账地址"
export const queryTansAdr = () => post('admin/system/transfer-address/page-query');

//post "币币管理" => "新增/修改转账地址"
export const auditTansAdr = data => post('admin/system/transfer-address/merge', data);

//post "币币管理" => "转账详情"
export const tansAdrDetail = data => post('admin/system/transfer-address/detail', data);

//post "币币管理" => "删除转账地址"
export const delTansAdr = data => post('admin/system/transfer-address/deletes', data);

//POST获取 "首页"=>"获取验证码"
export const getLoginCode = data => post('admin/system/employee/check', data);

//POST获取 "首页"=>"获取验证码后登录"
export const signIn = data => post('admin/system/employee/sign/in', data);

//POST获取 "首页"=>"再次获取验证码后登录"
export const getCodeAgain = data => post('admin/code/sms-provider/login', data);

//POST获取 "系统管理"=>"币种管理"=>"修改币种时发送验证码"
export const coinReviseSys = data => post('admin/code/sms-provider/system/coin-revise', data);

//POST获取 "系统管理"=>"币种管理"=>"是否为平台币"
export const setPlatformCoin = data => post('admin/system/coin/set/platform', data);

//POST获取 "系统管理"=>"币种管理"=>"添加钱包"
export const createMemberWallet = data => post('admin/system/coin/create-member-wallet', data);

//POST获取 "系统管理"=>"币种管理"=>"转入冷钱包"
export const transferColdWallet = data => post('admin/system/coin/transfer', data);

//POST获取 "系统管理"=>"币种管理"=>"转入冷钱包获取验证码"
export const getColdWalletCode = data => post('admin/code/sms-provider/transfer-cold-wallet', data);

//POST获取 "系统管理"=>"币种管理"=>"转入明细"
export const coinTransferDetail = data => post('admin/system/coin/hot-transfer-record/page-query', data);

//POST获取 "系统管理"=>"新增实名认证"
export const addAuthenticationSys = data => post('admin/system/member-application-config/merge', data);

//POST获取 "系统管理"=>"查询实名认证"
export const queryAuthenticationSys = data => post('admin/system/member-application-config/detail', data);

//POST获取 "法币管理"=>"认证商家"=>"禁用/解禁会员发布广告"
export const publishAdvOtc = data => post('admin/member/alter-publish-advertisement-status	', data);

//POST获取 "法币管理"=>"认证商家"=>"禁用/解禁会员交易"
export const forbiddenMemberTrans = data => post('admin/member/alter-transaction-status', data);

//POST获取 "法币管理"=>"认证商家"=>"禁用/解禁会员"
export const forbiddenMember = data => post('admin/member/alter-status', data);

//POST获取 "会员管理"=>"会员详情"=>"重置地址"
export const resetMemberAddr = data => post('admin/member/member-wallet/reset-address', data);

//POST获取 "法币管理"=>"后台广告"=>"广告详情"
export const advDetailOtc = data => post('admin/otc/advertise/detail', data);

//POST获取 "推荐返佣"=>"推荐会员"=>"分页"
export const queryRecommend = data => post('admin/promotion/member/page-query', data);

//POST获取 "推荐返佣"=>"佣金参数"=>"分页"
export const queryRewardRecommend = data => fetch('admin/promotion/reward/page-query', data);

//POST获取 "推荐返佣"=>"佣金参数"=>"详情"
export const rewardRecommendDetail = data => post('admin/promotion/reward/detail', data);

//POST获取 "推荐返佣"=>"佣金参数"=>"新增/修改"
export const auditRewardRecommend = data => post('admin/promotion/reward/merge', data);

//POST获取 "推荐返佣"=>"推荐会员"=>"推荐详情"
export const recommendDetail = data => post('admin/promotion/member/details', data);

//POST获取 "推荐返佣"=>"推荐会员"=>"导出"
export const recommendOutExcel = data => fetch('admin/promotion/member/out-excel', data);

//POST获取 "法币管理"=>"后台申诉"=>"申诉详情"
export const appealManageDetail = data => post('admin/otc/appeal/detail', data);

//POST获取 "系统管理"=>"字典查询"=>"查询分页GET"
export const dictionaryQuery = data => fetch('admin/system/data-dictionary', data);

//POST获取 "系统管理"=>"字典查询"=>"创建POST"
export const createDictionary = data => post('admin/system/data-dictionary', data);

//POST获取 "系统管理"=>"字典查询"=>"更新数据"
export const updateDictionary = (url, data) => put(`admin/system/data-dictionary/${url}`, data);

//GET获取 "系统管理"=>"活动管理"=>"会员签到记录"
export const memberSignQuery = data => fetch('admin/activity/member-sign-record/page-query', data);

//GET获取 "系统管理"=>"活动管理"=>"签到活动查询"
export const activityQuery = data => fetch('admin/activity/sign/page-query', data);

//POST获取 "系统管理"=>"活动管理"=>"新增签到"
export const createSign = data => post('admin/activity/sign', data);

//POST获取 "活动管理"=>"活动管理"=>"活动列表"
export const activityList = data => post('admin/activity/activity/page-query', data);

//POST获取 "活动管理"=>"活动管理"=>"添加活动"
export const addActivity = data => post('admin/activity/activity/add', data);

//POST获取 "活动管理"=>"活动管理"=>"修改活动"
export const modifyActivity = data => post('admin/activity/activity/modify', data);

//POST获取 "活动管理"=>"活动管理"=>"修改进度"
export const modifyActivityProgress = data => post('admin/activity/activity/modify-progress', data);

// GET "活动管理" => "活动详情"
export const activityDetail = parma => fetch(`admin/activity/activity/${parma}/detail`);

// GET "活动管理" => "活动参与详情"
export const activityOrderList = parma => fetch(`admin/activity/activity/${parma}/orderlist`);

// POST "活动管理" => "派发活动币"
export const distributeOrder = data => post(`admin/activity/activity/distribute`, data);

//POST获取 "系统管理"=>"活动管理"=>"签到详情"
export const signDetail = (url) => fetch(`admin/activity/sign/${url}`);

//POST获取 "系统管理"=>"活动管理"=>"修改签到详情"
export const fixSignDetail = (url, data) => put(`admin/activity/sign/${url}`, data);

//POST获取 "系统管理"=>"活动管理"=>"提前结束"
export const earlyCloseSign = url => patch(`admin/activity/sign/${url}/early-closing`);

//POST获取 "系统管理"=>"活动管理"=>"查询是否存在结束"
export const queryIfEnd = () => fetch('admin/activity/sign/has-underway');

//POST获取 "红包管理"=>"红包管理"=>"红包列表"
export const envelopeList = data => post('admin/envelope/page-query', data);

//POST获取 "红包管理"=>"红包管理"=>"红包详情"
export const envelopeDetail = parma => fetch(`admin/envelope/${parma}/detail`);

//POST获取 "红包管理"=>"红包管理"=>"红包领取详情"
export const envelopeReceiveDetail = data => post('admin/envelope/receive-detail', data);

//POST获取 "红包管理"=>"红包管理"=>"新建红包"
export const envelopeAdd = data => post('admin/envelope/add', data);

//POST获取 "红包管理"=>"红包管理"=>"修改红包"
export const envelopeModify = data => post('admin/envelope/modify', data);

//POST获取 "系统管理"=>"币种管理"=>"转账是否超时"
export const tansTimeout = data => fetch('admin/system/coin/get-no-check-key', data);

//POST获取 "财务管理"=>"财务统计"=>"总成交量"
export const financeTurnover = data => post('admin/finance/statistics/turnover-all', data);

//POST获取 "财务管理"=>"财务统计"=>"手续费"
export const financeFee = data => post('admin/finance/statistics/fee', data);

//POST获取 "财务管理"=>"财务统计"=>"总充币数"
export const financeRecharge = data => post('admin/finance/statistics/recharge-or-withdraw-amount', data);

//POST获取 "首页"=>"统计"=>"会员统计表"
export const memberBoard = data => post('admin/index/statistics/member-statistics-info', data);

//POST获取 "首页"=>"统计"=>"会员统计图"
export const memberChart = data => post('admin/index/statistics/member-statistics-chart', data);

//POST获取 "首页"=>"统计"=>"法币统计图"
export const otcChart = data => post('admin/index/statistics/otc-statistics-num-chart', data);

//POST获取 "首页"=>"统计"=>"法币统计表"
export const otcBoard = data => post('admin/index/statistics/otc-statistics-turnover', data);

//POST获取 "首页"=>"统计"=>"币币成交量/成交额 统计图"
export const coinChart = data => post('admin/index/statistics/exchange-statistics-turnover-chart', data);

//POST获取 "首页"=>"统计"=>"币币成交量/成交额 统计表"
export const coinBoard = data => post('admin/index/statistics/exchange-statistics-turnover', data);

//首页统计图表 => 所有法比币种单位
export const allOtcCoin = () => post('admin/otc/otc-coin/all-otc-coin-units');

//首页统计图表 => 所有币币交易区币种单位
export const allBaseCoin = () => post('admin/exchange/exchange-coin/all-base-symbol-units');

//首页统计图表 => 所有币币交易币种
export const allExchangeUnits = data => post('admin/exchange/exchange-coin/all-coin-symbol-units', data);

//GET获取 "保证金管理" => "分页查询"
export const queryBusinessAuth = data => fetch('admin/business-auth/page', data);

//POST获取 "保证金管理" => "创建保证金策略"
export const createBusinessAuth = data => post('admin/business-auth/create', data);

//GET获取 "保证金管理" => "更新保证金策略"
export const updateBusinessAuth = data => patch('admin/business-auth/update', data);

//GET获取 "法币管理" => "退保管理" => "分页"
export const queryCancelApply = data => post('admin/business/cancel-apply/page-query', data);

//GET获取 "法币管理" => "退保管理"=>"详情"
export const cancelApplyDetail = data => post('admin/business/cancel-apply/detail', data);

//GET获取 "法币管理" => "退保管理"=>"审核通过/不通过"
export const checkApply = data => post('admin/business/cancel-apply/check', data);

//POST获取 "法币管理" => "认证商家"=>"分页查询"
export const queryBusiness = data => post('admin/business-auth/apply/page-query', data);

//POST获取 "法币管理" => "认证商家"=>"商家状态"
export const queryBusinessStatus = () => post('admin/business-auth/get-search-status');

//POST获取 "法币管理" => "退保管理"=>"状态"
export const cancelBusinessStatus = () => post('admin/business/cancel-apply/get-search-status');

//POST获取 "法币管理" => "认证商家"=>"详情"
export const authBusinessDetail = data => post('admin/business-auth/apply/detail', data);

// post获取 "系统管理" => 合伙人放币
export const parnter = data => post('admin/system/coin/add-partner', data);

// C2C管理 CTC管理

//POST获取 "C2C管理"=>"订单管理"=>"订单列表"
export const ctcOrderList = data => post('admin/ctc/order/page-query', data);

//POST获取 "C2C管理"=>"订单管理"=>"订单详情"
export const ctcOrderDetail = data => post('admin/ctc/order/detail', data);

//POST获取 "C2C管理"=>"订单管理"=>"接单"
export const ctcOrderConfirm = data => post('admin/ctc/order/confirm-order', data);

//查询邀请记录
export const inviteRecord = data => postConfig('admin/invite/management/query', data);

//根据id查询二级邀请
export const inviteSecondRecord = data => postConfig('admin/invite/management/info', data);

//查询邀请排名
export const inviteRank = data => postConfig('admin/invite/management/rank', data);

//更新邀请排名
export const alterRank = data => post('admin/invite/management/update-rank', data);

//POST获取 "C2C管理"=>"订单管理"=>"标记付款"
export const ctcOrderPay = data => post('admin/ctc/order/pay-order', data);

//POST获取 "C2C管理"=>"订单管理"=>"取消订单"
export const ctcOrderCancel = data => post('admin/ctc/order/cancel-order', data);

//POST获取 "C2C管理"=>"订单管理"=>"完成放币"
export const ctcOrderComplete = data => post('admin/ctc/order/complete-order', data);

//POST获取 "C2C管理"=>"承兑商管理"=>"列表"
export const ctcAcceptorList = data => post('admin/ctc/acceptor/page-query', data);

//POST获取 "C2C管理"=>"承兑商管理"=>"切换状态"
export const ctcAcceptorSwitch = data => post('admin/ctc/acceptor/switch', data);

//POST获取 "系统管理"=>"APP版本"=>"列表"
export const sysAppRevision = data => fetch('admin/system/app-revision/page-query', data);
