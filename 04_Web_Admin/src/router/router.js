import Main from '@/views/Main.vue';
import Cookies from "js-cookie";
import { setStore, getStore, removeStore } from "@/config/storage.js"


// 不作为Main组件的子页面展示的页面单独写，如下
export const loginRouter = {
    path: '/login',
    name: 'login',
    meta: {
        title: 'Login - 登录'
    },
    component: resolve => { require(['@/views/login.vue'], resolve); }
};

export const page404 = {
    path: '/*',
    name: 'error-404',
    meta: {
        title: '404-页面不存在'
    },
    component: resolve => { require(['@/views/error-page/404.vue'], resolve); }
};

export const page403 = {
    path: '/403',
    meta: {
        title: '403-权限不足'
    },
    name: 'error-403',
    component: resolve => { require(['@//views/error-page/403.vue'], resolve); }
};

export const page500 = {
    path: '/500',
    meta: {
        title: '500-服务端错误'
    },
    name: 'error-500',
    component: resolve => { require(['@/views/error-page/500.vue'], resolve); }
};

export const locking = {
    path: '/locking',
    name: 'locking',
    component: resolve => { require(['@/views/main-components/lockscreen/components/locking-page.vue'], resolve); }
};

// 作为Main组件的子页面展示但是不在左侧菜单显示的路由写在otherRouter里
export const otherRouter = {
    path: '/',
    name: 'otherRouter',
    redirect: '/home',
    component: Main,
    children: [
        { path: 'home', title: '首页', name: 'home_index', component: resolve => { require(['@/views/home/home.vue'], resolve); } },
        { path: 'finance/auditdetail', title: '提现审核详情', name: 'finance:auditdetail', component: resolve => { require(['@/views/finance/AuditDetail.vue'], resolve); } },
        { path: 'member/memberaudit/auditdetail', title: '会员审核详情', name: 'member:memberaudit:auditdetail', component: resolve => { require(['@/views/member/AuthenticateDetail.vue'], resolve); } },
        { path: 'member/memberdetail', title: '会员详情', name: 'member:memberdetail', component: resolve => { require(['@/views/member/MemberDetail.vue'], resolve); } },
        { path: 'content/helpManage/addhelpmanage', title: '系统帮助', name: 'content:helpManage:addhelpmanage', component: resolve => { require(['@/views/content/AddHelpManage.vue'], resolve); } },
        { path: 'content/announceManage/addAnnounce', title: '编辑公告', name: 'content:announceManage:addAnnounce', component: resolve => { require(['@/views/content/AddAnnouncement.vue'], resolve); } },
        { path: 'exchange/bborder/detail', title: '订单详情', name: 'exchange:bborder:detail', component: resolve => { require(['@/views/exchange/OrderDetail.vue'], resolve); } },
        { path: 'system/employee/auditEmployee', title: '编辑用户', name: 'system:employee:auditEmployee', component: resolve => { require(['@/views/system/AddAuditEmployee.vue'], resolve) } },
        { path: '/user/personalcenter', title: '个人中心', name: 'user:personalcenter', component: resolve => { require(['@/views/user/PersonalCenter.vue'], resolve) } },
        { path: '/system/coin/transferdetail', title: '转账明细', name: 'system:coin:transferdetail', component: resolve => { require(['@/views/system/CoinTransferDetail.vue'], resolve) } },
        { path: '/otc/adManage/advertisedetail', title: '广告详情', name: 'otc:adManage:advertisedetail', component: resolve => { require(['@/views/otc/AdvertiseDetail.vue'], resolve) } },
        { path: '/otc/appealmanage/appealdetail', title: '申诉详情', name: 'otc:appealmanage:appealdetail', component: resolve => { require(['@/views/otc/AppealDetail.vue'], resolve) } },
        { path: '/otc/adManage/exitbonddetail', title: '退保详情', name: 'otc:adManage:exitbonddetail', component: resolve => { require(['@/views/otc/ExitBondDetail.vue'], resolve) } },
        { path: '/otc/businessaudit/businessdetail', title: '商家详情', name: 'otc:businessaudit:businessdetail', component: resolve => { require(['@/views/otc/BusinessDetail.vue'], resolve) } },
        { path: 'activity/activity/addActivity', title: '编辑活动', name: 'activity:activity:add', component: resolve => { require(['@/views/activity/AddActivity.vue'], resolve); } },
        { path: 'envelope/addEnvelope', title: '编辑红包', name: 'envelope:add', component: resolve => { require(['@/views/redenvelope/AddRedEnvelope.vue'], resolve); } }
    ]
};

// 作为Main组件的子页面展示并且在左侧菜单显示的路由写在appRouter里<Icon type="briefcase"></Icon>

let findSlideArr = [
    { id: 8, path: '/system', icon: 'android-settings', component: Main },
    { id: 11, path: '/member', icon: 'person-stalker', component: Main },
    { id: 66, path: '/otc', icon: 'briefcase', component: Main },
    { id: 60, path: '/exchange', icon: 'android-options', component: Main },
    { id: 93, path: '/finance', icon: 'stats-bars', component: Main },
    { id: 18, path: '/content', icon: 'folder', component: Main },
    { id: 177, path: '/businessAuth', icon: 'cash', component: Main },
    { id: 162, path: '/activity', icon: 'cube', component: Main },
    { id: 203, path: '/ctc', icon: 'arrow-swap', component: Main },
    { id: 220, path: '/envelope', icon: 'ios-albums', component: Main },
    { id: 214, path: '/Invitation', icon: 'android-share-alt', component: Main},
    { id: 13, path: 'memberaudit', icon: 'document', component: resolve => { require(['@/views/member/Authenticate.vue'], resolve) } },
    { id: 23, path: 'coin', icon: 'document', component: resolve => { require(['@/views/system/Coin.vue'], resolve) } },
    { id: 94, path: 'userwithdrawals', icon: 'document', component: resolve => { require(['@/views/finance/WithdrawalsExamine.vue'], resolve) } },
    { id: 9, path: 'advertise', icon: 'document', component: resolve => { require(['@/views/content/AddAdvertisement.vue'], resolve) } },
    { id: 10, path: 'helpManage', icon: 'document', component: resolve => { require(['@/views/content/helpManage.vue'], resolve) } },
    { id: 26, path: 'announcemanage', icon: 'document', component: resolve => { require(['@/views/content/AnnounceManage.vue'], resolve) } },
    { id: 12, path: 'membermanage', icon: 'document', component: resolve => { require(['@/views/member/MemberManage.vue'], resolve) } },
    { id: 19, path: 'tradedetail', icon: 'document', component: resolve => { require(['@/views/finance/TradeDetail.vue'], resolve) } },
    { id: 131, path: 'changecoindetail', icon: 'document', component: resolve => { require(['@/views/finance/ChargeCoinDetail.vue'], resolve) } },
    { id: 95, path: 'feemanage', icon: 'document', component: resolve => { require(['@/views/finance/FeeManage.vue'], resolve) } },
    { id: 4, path: 'departmanage', icon: 'document', component: resolve => { require(['@/views/system/Department.vue'], resolve) } },
    { id: 150, path: 'apprevision', icon: 'document', component: resolve => { require(['@/views/system/AppReversion.vue'], resolve) } },
    { id: 2, path: 'rolemanage', icon: 'document', component: resolve => { require(['@/views/system/Role.vue'], resolve) } },
    { id: 62, path: 'bborder', icon: 'document', component: resolve => { require(['@/views/exchange/Order.vue'], resolve) } },
    { id: 103, path: 'bbsetting', icon: 'document', component: resolve => { require(['@/views/exchange/Setting.vue'], resolve) } },
    { id: 184, path: 'businessaudit', icon: 'document', component: resolve => { require(['@/views/otc/BusinessAudit.vue'], resolve) } },
    { id: 111, path: 'permission', icon: 'document', component: resolve => { require(['@/views/system/Permission.vue'], resolve) } },
    { id: 91, path: 'memberasset', icon: 'document', component: resolve => { require(['@/views/member/MemberAsset.vue'], resolve) } },
    { id: 3, path: 'employee', icon: 'document', component: resolve => { require(['@/views/system/Employee.vue'], resolve) } },
    { id: 7, path: 'accesslog', icon: 'document', component: resolve => { require(['@/views/system/AccessLog.vue'], resolve) } },
    { id: 83, path: 'coinManage', icon: 'document', component: resolve => { require(['@/views/otc/CoinManage.vue'], resolve) } },
    { id: 70, path: 'adManage', icon: 'document', component: resolve => { require(['@/views/otc/AdManage.vue'], resolve) } },
    { id: 182, path: 'cancelapply', icon: 'document', component: resolve => { require(['@/views/otc/ExitBond.vue'], resolve) } },
    { id: 77, path: 'orderManage', icon: 'document', component: resolve => { require(['@/views/otc/OrderManage.vue'], resolve) } },
    { id: 72, path: 'appealManage', icon: 'document', component: resolve => { require(['@/views/otc/AppealManage.vue'], resolve) } },
    { id: 159, path: 'otcdetail', icon: 'document', component: resolve => { require(['@/views/finance/OtcDetail.vue'], resolve) } },
    { id: 160, path: 'withdrawdetail', icon: 'document', component: resolve => { require(['@/views/finance/WithdrawDetail.vue'], resolve) } },
    { id: 147, path: 'dictionary', icon: 'document', component: resolve => { require(['@/views/system/Dictionary.vue'], resolve) } },
    { id: 171, path: 'statistic', icon: 'document', component: resolve => { require(['@/views/finance/FinanceStatistic.vue'], resolve) } },
    { id: 178, path: 'manage', icon: 'document', component: resolve => { require(['@/views/bond/BondManage.vue'], resolve) } },
    { id: 196, path: 'activity', icon: 'document', component: resolve => { require(['@/views/activity/Activity.vue'], resolve) } },
    { id: 204, path: 'ctcorderlist', icon: 'document', component: resolve => { require(['@/views/ctc/list.vue'], resolve) } },
    { id: 210, path: 'ctcacceptorlist', icon: 'document', component: resolve => { require(['@/views/ctc/acceptor.vue'], resolve) } },
    { id: 215, path: 'InvitationRecord', icon: 'document', component: resolve => { require(['@/views/invitation/InvitationRecord.vue'], resolve) }},
    { id: 216, path: 'inviteRankList', icon: 'document', component: resolve => { require(['@/views/invitation/InvitationRank.vue'], resolve) }},
    { id: 221, path: 'envelopeList', icon: 'document', component: resolve => { require(['@/views/redenvelope/RedEnvelope.vue'], resolve) }}
]
let sidebarArr = JSON.parse(getStore('leftSidebarList')) || [];

let userDefinedIDArr = [];
findSlideArr.forEach(item => {
    userDefinedIDArr.push(item.id);
})

let arr = [];
sidebarArr.forEach((item, index) => {
    if (!userDefinedIDArr.includes(item.id)) {
        delete sidebarArr[index];
    }
})

let formateUndefinedArr = [];
sidebarArr.forEach(item => {
    formateUndefinedArr.push(item);
})

if (formateUndefinedArr.length > 0) {
    formateUndefinedArr.forEach(item => {
        let index = userDefinedIDArr.indexOf(item.id);
        item.path = findSlideArr[index].path;
        item.icon = findSlideArr[index].icon;
        item.perChildren = [...item.subMenu];
        item.children = [];
        item.component = Main
        if (item.perChildren.length > 0) {

            item.perChildren.forEach((it, index) => {
                let subIndex = userDefinedIDArr.indexOf(it.id);
                if (subIndex !== -1) {
                    it.icon = findSlideArr[subIndex].icon;
                    it.path = findSlideArr[subIndex].path;
                    it.component = findSlideArr[subIndex].component;
                } else {
                    delete item.perChildren[index]
                }
            })
            item.perChildren.forEach(it => {
                item.children.push(it);
            })

        }
    })
    console.log(formateUndefinedArr);
}

export let appRouter = formateUndefinedArr;
export const clearAppRouter = () => {
    appRouter = [];
}

// 所有上面定义的路由都要写在下面的routers里
export const routers = [
    loginRouter,
    otherRouter,
    locking,
    ...appRouter,
    page500,
    page403,
    page404
];
