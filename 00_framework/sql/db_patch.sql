# noinspection SqlNoDataSourceInspectionForFile


SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `coin`
-- ----------------------------
DROP TABLE IF EXISTS `coin`;
CREATE TABLE `coin` (
  `name` varchar(255) NOT NULL,
  `can_auto_withdraw` int(11) DEFAULT NULL,
  `can_recharge` int(11) DEFAULT NULL,
  `can_transfer` int(11) DEFAULT NULL,
  `can_withdraw` int(11) DEFAULT NULL,
  `cny_rate` double NOT NULL,
  `enable_rpc` int(11) DEFAULT NULL,
  `is_platform_coin` int(11) DEFAULT NULL,
  `max_tx_fee` double NOT NULL,
  `max_withdraw_amount` decimal(18,8) DEFAULT NULL COMMENT '最大提币数量',
  `min_tx_fee` double NOT NULL,
  `min_withdraw_amount` decimal(18,8) DEFAULT NULL COMMENT '最小提币数量',
  `name_cn` varchar(255) NOT NULL,
  `sort` int(11) NOT NULL,
  `status` int(11) DEFAULT NULL,
  `unit` varchar(255) NOT NULL,
  `usd_rate` double NOT NULL,
  `withdraw_threshold` decimal(18,8) DEFAULT NULL COMMENT '提现阈值',
  `has_legal` bit(1) NOT NULL DEFAULT b'0',
  `cold_wallet_address` varchar(255) DEFAULT NULL,
  `miner_fee` decimal(18,8) DEFAULT '0.00000000' COMMENT '矿工费',
  `withdraw_scale` int(11) DEFAULT '4' COMMENT '提币精度',
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of coin
-- ----------------------------
INSERT INTO `coin` VALUES ('Bitcoin', '0', '0', '1', '0', '0', '0', '0', '0.0002', '5.00000000', '0.0002', '0.00100000', '比特币', '1', '0', 'BTC', '0', '0.10000000', '', null, '0.00000000', '4');
INSERT INTO `coin` VALUES ('Bitcoincash', '1', '1', '1', '1', '0', '1', '0', '0.0002', '5.00000000', '0.0002', '0.00100000', '比特现金', '1', '0', 'BCH', '0', '0.10000000', '', null, '0.00000000', '4');
INSERT INTO `coin` VALUES ('DASH', '1', '1', '1', '1', '0', '1', '0', '0.0002', '5.00000000', '0.0002', '0.00100000', '达世币', '1', '0', 'DASH', '0', '0.10000000', '', null, '0.00000000', '4');
INSERT INTO `coin` VALUES ('DEV', '1', '0', '1', '0', '1', '0', '0', '0.0002', '5.00000000', '0.0002', '0.00100000', 'DEV', '1', '0', 'DEV', '0', '0.10000000', '', null, '0.00000000', '4');
INSERT INTO `coin` VALUES ('Ethereum', '1', '1', '1', '1', '0', '1', '0', '0.0002', '5.00000000', '0.0002', '0.00100000', '以太坊', '1', '0', 'ETH', '0', '0.10000000', '', null, '0.00000000', '4');
INSERT INTO `coin` VALUES ('GalaxyChain', '1', '1', '1', '1', '1', '1', '0', '0.0002', '5.00000000', '0.0002', '0.00100000', '银河链', '1', '0', 'GCC', '0', '0.10000000', '', null, '0.00000000', '4');
INSERT INTO `coin` VALUES ('Litecoin', '1', '0', '1', '1', '1', '1', '0', '0.0002', '5.00000000', '0.0002', '0.00100000', '莱特币', '1', '0', 'LTC', '0', '0.10000000', '', null, '0.00000000', '4');
INSERT INTO `coin` VALUES ('RMB', '1', '1', '1', '1', '0', '0', '0', '0.0002', '5.00000000', '0.0002', '0.00100000', '零币TEST', '1', '1', 'ZEC', '0', '0.10000000', '', null, '0.00000000', '4');
INSERT INTO `coin` VALUES ('SGD', '1', '1', '1', '1', '0', '1', '0', '0.0002', '500.00000000', '1', '1.00000000', '新币', '4', '0', 'SGD', '0', '0.10000000', '', null, '0.00000000', '4');
INSERT INTO `coin` VALUES ('USDT', '1', '1', '1', '1', '0', '1', '0', '0.0002', '5.00000000', '0.0002', '0.00100000', '泰达币T', '1', '0', 'USDT', '0', '0.10000000', '', null, '0.00000000', '4');
INSERT INTO `coin` VALUES ('ZEC', '1', '0', '1', '1', '0', '0', '1', '0.0002', '5.00000000', '0.0002', '0.00100000', '零币KT1', '1', '0', 'ZEC', '0', '0.10000000', '', null, null, '4');

-- ----------------------------
-- Table structure for `exchange_coin`
-- ----------------------------
DROP TABLE IF EXISTS `exchange_coin`;
CREATE TABLE `exchange_coin` (
  `symbol` varchar(255) NOT NULL,
  `base_coin_scale` int(11) NOT NULL,
  `base_symbol` varchar(255) DEFAULT NULL,
  `coin_scale` int(11) NOT NULL,
  `coin_symbol` varchar(255) DEFAULT NULL,
  `enable` int(11) NOT NULL,
  `fee` decimal(8,4) DEFAULT NULL COMMENT '交易手续费',
  `sort` int(11) NOT NULL,
  `enable_market_buy` int(11) DEFAULT '1' COMMENT '是否启用市价买',
  `enable_market_sell` int(11) DEFAULT '1' COMMENT '是否启用市价卖',
  `min_sell_price` decimal(18,8) DEFAULT '0.00000000' COMMENT '最低挂单卖价',
  `flag` int(11) DEFAULT '0',
  `max_trading_order` int(11) DEFAULT '0' COMMENT '最大允许同时交易的订单数，0表示不限制',
  `max_trading_time` int(11) DEFAULT '0' COMMENT '委托超时自动下架时间，单位为秒，0表示不过期',
  `instrument` varchar(20) DEFAULT NULL COMMENT '交易类型，B2C2特有',
  `min_turnover` decimal(18,8) DEFAULT '0.00000000' COMMENT '最小挂单成交额',
  PRIMARY KEY (`symbol`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of exchange_coin
-- ----------------------------
INSERT INTO `exchange_coin` VALUES ('BCH/USDT', '2', 'USDT', '4', 'BCH', '1', '0.0002', '2', '1', '1', '0.00000000', '1', '0', '0', null, '0.00000000');
INSERT INTO `exchange_coin` VALUES ('BTC/USDT', '2', 'USDT', '2', 'BTC', '1', '0.0010', '1', '1', '1', '0.00000000', '1', '0', '0', null, '0.00000000');
INSERT INTO `exchange_coin` VALUES ('DASH/USDT', '2', 'USDT', '4', 'DASH', '2', '0.0010', '6', '1', '1', '0.00000000', '1', '0', '0', null, '0.00000000');
INSERT INTO `exchange_coin` VALUES ('ETH/USDT', '2', 'USDT', '2', 'ETH', '1', '0.0010', '3', '1', '1', '0.00000000', '0', '0', '0', null, '0.00000000');
INSERT INTO `exchange_coin` VALUES ('LTC/USDT', '2', 'USDT', '4', 'LTC', '1', '0.0010', '5', '1', '1', '0.00000000', '0', '0', '0', null, '0.00000000');
INSERT INTO `exchange_coin` VALUES ('ZEC/USDT', '2', 'USDT', '4', 'ZEC', '1', '0.1200', '7', '1', '1', '0.00000000', '0', '0', '0', null, '0.00000000');

-- ----------------------------
-- Table structure for `otc_coin`
-- ----------------------------
DROP TABLE IF EXISTS `otc_coin`;
CREATE TABLE `otc_coin` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `buy_min_amount` decimal(18,8) DEFAULT NULL COMMENT '买入广告最低发布数量',
  `is_platform_coin` int(11) DEFAULT NULL,
  `jy_rate` decimal(18,6) DEFAULT NULL COMMENT '交易手续费率',
  `name` varchar(255) NOT NULL,
  `name_cn` varchar(255) NOT NULL,
  `sell_min_amount` decimal(18,8) DEFAULT NULL COMMENT '卖出广告最低发布数量',
  `sort` int(11) NOT NULL,
  `status` int(11) DEFAULT NULL,
  `unit` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of otc_coin
-- ----------------------------
INSERT INTO `otc_coin` VALUES ('1', '0.01000000', '0', '1.000000', 'Bitcoin', '比特币', '0.10000000', '1', '0', 'BTC');
INSERT INTO `otc_coin` VALUES ('2', '0.01000000', '0', '1.000000', 'USDT', '泰达币', '0.10000000', '3', '0', 'USDT');
INSERT INTO `otc_coin` VALUES ('3', '0.01000000', '0', '1.000000', 'Ethereum', '以太坊', '0.10000000', '2', '0', 'ETH');
INSERT INTO `otc_coin` VALUES ('4', '8.00000000', '0', '9.000000', 'Bitcoin Cash', '比特币现金', '8.00000000', '0', '0', 'BCH');
INSERT INTO `otc_coin` VALUES ('5', '1.00000000', '0', '1.000000', 'Litecoin', '莱特币', '1.00000000', '0', '1', 'LTC');
INSERT INTO `otc_coin` VALUES ('6', '1.00000000', '0', '1.000000', 'SGD', '新币', '1.00000000', '0', '1', 'SGD');



SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `country`
-- ----------------------------
DROP TABLE IF EXISTS `country`;
CREATE TABLE `country` (
  `zh_name` varchar(255) NOT NULL,
  `area_code` varchar(255) DEFAULT NULL,
  `en_name` varchar(255) DEFAULT NULL,
  `language` varchar(255) DEFAULT NULL,
  `local_currency` varchar(255) DEFAULT NULL,
  `sort` int(11) NOT NULL,
  PRIMARY KEY (`zh_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of country
-- ----------------------------
INSERT INTO `country` VALUES ('中国', '86', 'China', 'zh_CN', 'CNY', '0');
INSERT INTO `country` VALUES ('中国台湾', '886', 'Taiwan,China', 'zh_TW', 'TWD', '0');
INSERT INTO `country` VALUES ('新加坡', '65', 'Singapore', 'en_US', 'SGD', '0');
INSERT INTO `country` VALUES ('美国', '1', 'America', 'en_US', 'USD', '0');
INSERT INTO `country` VALUES ('香港', '852', 'HongKong', 'zh_HK', 'HKD', '0');


SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `admin`
-- ----------------------------
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `avatar` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `enable` int(11) DEFAULT NULL,
  `last_login_ip` varchar(255) DEFAULT NULL,
  `last_login_time` datetime DEFAULT NULL,
  `mobile_phone` varchar(255) NOT NULL,
  `password` varchar(255) DEFAULT NULL,
  `qq` varchar(255) DEFAULT NULL,
  `real_name` varchar(255) NOT NULL,
  `role_id` bigint(20) NOT NULL,
  `status` int(11) DEFAULT NULL,
  `username` varchar(255) NOT NULL,
  `department_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_gfn44sntic2k93auag97juyij` (`username`) USING BTREE,
  KEY `FKibjnyhe6m46qfkc6vgbir1ucq` (`department_id`) USING BTREE,
  CONSTRAINT `admin_ibfk_1` FOREIGN KEY (`department_id`) REFERENCES `department` (`id`),
  CONSTRAINT `admin_ibfk_2` FOREIGN KEY (`department_id`) REFERENCES `department` (`id`),
  CONSTRAINT `admin_ibfk_3` FOREIGN KEY (`department_id`) REFERENCES `department` (`id`),
  CONSTRAINT `FKnmmt6f2kg0oaxr11uhy7qqf3w` FOREIGN KEY (`department_id`) REFERENCES `department` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=60 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of admin
-- ----------------------------
INSERT INTO `admin` VALUES ('1', 'kjglkasdjg/sdfasdf/asdf', 'root', '0', '0:0:0:0:0:0:0:1', '2018-05-24 15:06:00', '18225520751', '47943eeeab5ed28f8a93f7fb77ec5111', '312', '人人', '1', '0', 'root', '1');


-- ----------------------------
-- Table structure for `admin_permission`
-- ----------------------------
DROP TABLE IF EXISTS `admin_permission`;
CREATE TABLE `admin_permission` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `parent_id` bigint(20) DEFAULT NULL,
  `sort` int(11) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=191 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of admin_permission
-- ----------------------------
INSERT INTO `admin_permission` (`id`, `title`, `name`, `parent_id`, `sort`, `description`) VALUES
(2, '角色管理', 'system:role:all', 8, 0, '角色管理'),
(3, '用户管理', 'system:employee:page-query', 8, 0, '用户管理'),
(4, '部门管理', 'system:department:all', 8, 0, '部门管理'),
(5, '站点管理', 'system:website-information:find-one', 8, 0, '站点管理'),
(6, '菜单管理', 'system:role:permission:all', 8, 0, '菜单管理'),
(7, '系统日志', 'system:access-log:page-query', 8, 0, '系统日志'),
(8, '系统管理', 'system-------', 0, 7, '系统管理'),
(9, '广告管理', 'cms:system-advertise:page-query', 18, 0, '广告管理'),
(10, '帮助管理', 'cms:system-help:page-query', 18, 0, '帮助管理'),
(11, '会员管理', 'member--------', 0, 1, '会员管理'),
(12, '会员管理', 'member:page-query', 11, 0, '会员管理'),
(13, '实名管理', 'member:member-application:page-query', 11, 0, '实名管理'),
(14, '会员监控', 'member--------', 11, 0, '会员监控'),
(18, '内容管理', 'cms-------', 0, 4, '内容管理'),
(19, '交易明细', 'finance:member-transaction:page-query', 93, 0, '交易记录'),
(20, '提现管理', 'finance:withdraw-record:page-query', 93, 0, '提现管理'),
(23, '币种管理', 'system:coin:page-query', 8, 0, '币币币种管理'),
(26, '公告管理', 'cms:notice', 18, 0, '公告管理'),
(27, '创建修改角色', 'system:role:merge', 8, 0, '创建修改角色'),
(28, '更改角色权限', 'system:role:permission:update', 8, 0, '更改角色权限'),
(29, '角色拥有权限', 'system:role:permission', 8, 0, '角色拥有权限'),
(30, '全部权限树', 'system:role:permission:all', 8, 0, '全部权限树'),
(31, '创建更改后台用户', 'system:employee:merge', 8, 0, '创建更改后台用户'),
(32, '后台用户详情', 'system:employee:detail', 8, 0, '后台用户详情'),
(33, '站点信息修改', 'system:website-information:alter', 8, 0, '站点信息修改'),
(34, '系统日志详情', 'system:access-log:detail', 8, 0, '系统日志详情'),
(35, '创建系统广告', 'cms:system-advertise:create', 18, 0, '创建系统广告'),
(36, '所有系统广告', 'cms:system-advertise:all', 18, 0, '所有系统广告'),
(37, '系统广告详情', 'cms:system-advertise:detail', 18, 0, '系统广告详情'),
(38, '更新系统广告', 'cms:system-advertise:update', 18, 0, '更新系统广告'),
(39, '删除系统广告', 'cms:system-advertise:deletes', 18, 0, '删除系统广告'),
(40, '导出广告excel', 'cms:system-advertise:out-excel', 18, 0, '导出广告excel'),
(41, '创建系统帮助', 'cms:system-help:create', 18, 0, '创建系统帮助'),
(42, '系统帮助详情', 'cms:system-help:detail', 18, 0, '系统帮助详情'),
(43, '更新系统帮助', 'cms:system-help:update', 18, 0, '更新系统帮助'),
(44, '删除系统帮助', 'cms:system-help:deletes', 18, 0, '删除系统帮助'),
(45, '导出系统帮助excel', 'cms:system-help:out-excel', 18, 0, '导出系统帮助excel'),
(46, '会员详情', 'member:detail', 11, 0, '会员详情'),
(47, '会员删除', 'member:delete', 11, 0, '会员删除'),
(48, '会员更新', 'member:update', 11, 0, '会员更新'),
(49, '认证商家审核', 'member:audit-business', 66, 0, '认证商家审核'),
(50, '导出会员excel', 'member:out-excel', 11, 0, '导出会员excel'),
(51, '实名信息详情', 'member:member-application:detail', 11, 0, '实名信息详情'),
(52, '实名审核通过', 'member:member-application:pass', 11, 0, '实名审核通过'),
(53, '实名审核不通过', 'member:member-application:no-pass', 11, 0, '实名审核不通过'),
(54, '交易记录详情', 'finance:member-transaction:detail', 93, 0, '交易记录详情'),
(55, '导出交易记录excel', 'finance:member-transaction:out-excel', 93, 0, '导出交易记录excel'),
(56, '提现记录详情', 'finance:withdraw-record:detail', 93, 0, '提现记录详情'),
(57, '提现记录一键审核通过', 'finance:withdraw-record:audit-pass', 93, 0, '提现记录一键审核通过'),
(58, '提现记录一键审核不通过', 'finance:withdraw-record:audit-no-pass', 93, 0, '提现记录一键审核不通过'),
(59, '批量打款', 'finance:withdraw-record:remittance', 93, 0, '批量打款'),
(60, '币币管理', 'exchange-------', 0, 5, '币币管理父菜单'),
(61, '币币交易订单详情', 'exchange:exchange-order:detail', 60, 0, '币币交易订单详情'),
(62, '订单管理', 'exchange:exchange-order:page-query', 60, 0, '币币交易订单'),
(63, '导出币币交易订单excel', 'exchange:exchange-order:out-excel', 60, 0, '导出币币交易订单excel'),
(64, '币种管理', 'exchange:exchange-coin:page-query', 60, 0, '币种管理'),
(65, '币币交易对详情', 'exchange:exchange-coin:detail', 60, 0, '币币交易对详情'),
(66, 'OTC管理', 'otc-------', 0, 10, '法币otc'),
(67, '后台广告详情', 'otc:advertise:detail', 66, 0, '后台广告详情'),
(68, '关闭后台广告', 'otc:advertise:turnoff', 66, 0, '关闭后台广告'),
(69, '后台广告状态修改', 'otc:advertise:alter-status', 66, 0, '后台广告状态修改'),
(70, '后台广告', 'otc:advertise:page-query', 66, 0, '后台广告'),
(71, '后台广告导出excel', 'otc:advertise:out-excel', 66, 0, '后台广告导出excel'),
(72, '后台申诉', 'otc:appeal:page-query', 66, 0, '后台申诉'),
(73, '申诉详情', 'otc:appeal:detail', 66, 0, '申诉详情'),
(74, '申诉处理', 'otc:appeal:audit', 66, 0, '申诉处理'),
(75, '法币交易订单详情', 'otc:order:detail', 66, 0, '法币交易订单详情'),
(76, '法币交易订单状态修改', 'otc:order:alert-status', 66, 0, '法币交易订单状态修改'),
(77, '订单管理', 'otc:order:page-query', 66, 0, '法币交易订单'),
(78, '导出法币交易订单excel', 'otc:order:out-excel', 66, 0, '导出法币交易订单excel'),
(79, '创建otc币种', 'otc:otc-coin:create', 66, 0, '创建otc币种'),
(80, 'otc币种详情', 'otc:otc-coin:detail', 66, 0, 'otc币种详情'),
(81, 'otc币种更新', 'otc:otc-coin:update', 66, 0, 'otc币种更新'),
(82, 'otc币种交易率修改', 'otc:otc-coin:alter-jy-rate', 66, 0, 'otc币种交易率修改'),
(83, '币种管理', 'otc:otc-coin:page-query', 66, 0, '法币币种管理'),
(84, '导出otc币种excel', 'otc:otc-coin:out-excel', 66, 0, '导出otc币种excel'),
(85, '创建后台货币', 'system:coin:create', 8, 0, '创建后台货币'),
(86, '部门详情', 'system:department:detail', 8, 0, '部门详情'),
(87, '查询新增用户曲线', 'system:statistics:member-statistics', 8, 0, '查询新增用户曲线'),
(88, '委托量曲线', 'system:statistics:delegation-statistics', 8, 0, '委托量曲线'),
(89, '法币交易订单量曲线', 'system:statistics:order-statistics', 8, 0, '法币交易订单量曲线'),
(90, 'otc_order统计', 'system:statistics:dashboard', 8, 0, 'otc_order统计'),
(91, '余额管理', 'member:member-wallet:balance', 11, 0, '余额管理'),
(92, '充值管理', 'finance:member-transaction:page-query:recharge', 93, 0, '充值管理'),
(93, '财务管理', 'finance-------', 0, 4, '财务管理'),
(94, '提币审核', 'finance:member-transaction:page-query:check', 93, 0, '提现客服审核'),
(95, '手续费管理', 'finance:member-transaction:page-query:fee', 93, 0, '手续费管理'),
(96, '创建公告', 'system:announcement:create', 8, 0, '创建公告'),
(97, '分页查询公告', 'system:announcement:page-query', 8, 0, '分页查询公告'),
(98, '删除公告', 'system:announcement:deletes', 8, 0, '删除公告'),
(99, '公告详情', 'system:announcement:detail', 8, 0, '公告详情'),
(100, '更新公告', 'system:announcement:update', 8, 0, '更新公告'),
(101, '关闭公告', 'system:announcement:turn-off', 8, 0, '关闭公告'),
(102, '打开公告', 'system:announcement:turn-on', 8, 0, '打开公告'),
(103, '币币设置', 'exchange:set', 60, 0, '币币设置'),
(104, '放币处理', 'otc:appeal:release-coin', 66, 0, '放币处理'),
(105, '取消订单', 'otc:appeal:cancel-order', 66, 0, '取消订单'),
(106, '创建修改部门', 'system:department:merge', 8, 0, '创建修改部门'),
(107, '删除exchangeCoin', 'exchange:exchange-coin:deletes', 60, 0, '删除exchangeCoin'),
(108, '删除otcCoin', 'otc:otc-coin:deletes', 66, 0, '删除otcCoin'),
(109, '删除部门', 'system:department:deletes', 8, 0, '删除部门'),
(110, '增加/修改权限', 'system:permission:merge', 8, 0, '增加权限'),
(111, '权限管理', 'system:permission:page-query', 8, 0, '分页查询权限'),
(112, '权限详情', 'system:permission:details', 8, 0, '权限详情'),
(113, '权限删除', 'system:permission:deletes', 8, 0, '权限删除'),
(114, '添加交易流水号', 'finance:withdraw-record:add-transaction-number', 93, 0, '财务提现转账成功添加流水号'),
(115, '人工充值', 'member:member-wallet:recharge', 11, 0, '人工充值'),
(116, '首页订单数', 'otc:order:get-order-num', 66, 0, '首页订单数'),
(117, '投票管理', 'system:vote:merge', 8, 0, '新增/修改投票'),
(118, '分页查询', 'system:vote:page-query', 8, 0, '分页查询'),
(119, 'admin更改密码', 'system:employee:update-password', 8, 0, 'admin更改密码'),
(120, '系统公告置顶', 'cms:system-help:top', 18, 0, '系统公告置顶'),
(121, '系统广告置顶', 'cms:system-advertise:top', 18, 0, '系统广告置顶'),
(122, '公告置顶', 'system:announcement:top', 8, 0, '公告置顶'),
(123, '转账地址', 'system:transfer-address:page-query', 8, 0, '转账地址管理    拍币网独有'),
(124, '新增/修改转账地址', 'system:transfer-address:merge', 8, 0, '新增/修改转账地址  拍币网独有'),
(125, '转账地址详情', 'system:transfer-address:detail', 8, 0, '转账地址详情  拍币网独有'),
(126, '批量删除转账地址', 'system:transfer-address:deletes', 8, 0, '批量删除转账地址   拍币网独有'),
(128, '分红管理', 'system:dividend:page-query', 8, 0, '分红管理分页查询'),
(129, '开始分红', 'system:dividend:start', 8, 0, '开始分红'),
(130, '分红手续费', 'system:dividend:fee-query', 8, 0, '分红手续费'),
(131, '充币记录', 'finance:member-deposit:page-query', 93, 0, '区块链钱包充币记录'),
(132, '人工转账', 'system:coin:transfer', 8, 0, '热钱包转账至冷钱包'),
(133, '转入明细', 'system:coin:hot-transfer-record:page-query', 8, 0, '热钱包转入冷钱包记录'),
(134, '实名认证配置修改', 'system:member-application-config:merge', 8, 0, '实名认证配置修改'),
(135, '实名认证配置详情', 'system:member-application-config:detail', 8, 0, '实名认证配置详情'),
(136, '禁用/解禁发布广告', 'member:alter-publish-advertisement-status', 11, 0, '禁用/解禁发布广告 1表示正常'),
(137, '禁用/解禁会员账号', 'member:alter-status', 11, 0, '禁用/解禁会员账号 0表示正常'),
(138, '推荐会员', 'promotion:member:page-query', 143, 0, '推荐会员分页'),
(139, '删除用户', 'system:employee:deletes', 8, 0, '批量删除用户'),
(140, '充值管理', 'legal-wallet-recharge', 66, 0, '充值管理分页'),
(141, '提币管理', 'legal-wallet-withdraw', 66, 0, '提币管理查询分页'),
(142, '是否禁止交易', 'member:alter-transaction-status', 11, 0, '是否禁止交易  1表示正常'),
(143, '推荐返佣', 'promotion-------', 0, 9, '推荐返佣 根'),
(144, '新增/修改推荐返佣配置', 'promotion:reward:merge', 143, 0, '新增/修改推荐返佣配置'),
(145, '重置会员钱包地址', 'member:member-wallet:reset-address', 11, 0, '重置会员钱包地址'),
(146, '佣金参数', 'promotion:reward:page-query', 143, 0, '佣金参数'),
(147, '系统信息维护', 'system:data-dictionary', 8, 0, '系统信息管理'),
(148, '数据字典添加', 'system:data-dictionary:create', 8, 0, '数据字典添加'),
(149, '数据字典修改', 'system:data-dictionary:update', 8, 0, '数据字典修改'),
(150, '版本管理', 'system:app-revision:page-query', 8, 0, '版本管理'),
(151, '添加版本信息', 'system:app-revision:create', 8, 0, '添加版本信息'),
(152, '更新版本信息', 'system:app-revision:update', 8, 0, '更新版本信息'),
(153, '版本信息详情', 'system:app-revision:details', 8, 0, '版本信息详情'),
(154, '推荐会员导出', 'promotion:member:out-excel', 143, 0, '推荐会员导出'),
(155, '推荐会员明细', 'promotion:member:details', 143, 0, '推荐会员明细'),
(156, '测试权限', '测试名称', 18, 0, '描述'),
(158, '取消委托', 'exchange:exchange-order:cancel', 18, 0, '取消委托订单'),
(159, '法币交易明细', 'finance:otc:order:page-query', 93, 0, '法币交易明细'),
(160, '提币明细', 'finance:withdraw-record:page-query:success', 93, 0, '提币明细'),
(161, '保证金管理', 'business-auth:manager', 93, 0, '保证金管理'),
(162, '活动管理', 'activity-------', 0, 6, '活动管理'),
(164, '签到新增', 'activity:sign:post', 162, 0, '签到新增'),
(165, '签到修改', 'activity:sign:put', 162, 0, '签到修改'),
(167, '签到管理', 'activity:sign:page-query', 162, 0, '签到分页'),
(168, '签到详情', 'activity:sign:id:get', 162, 0, '签到详情'),
(169, '签到提前关闭', 'activity:sign:id:early-closing', 162, 0, '签到提前关闭'),
(170, '签到记录', 'activity:member-sign-record:page-query', 162, 0, '签到记录'),
(171, '财务统计', 'finance:statistics:turnover', 93, 0, '成交量/成交额统计'),
(172, '手续费合计', 'finance:statistics:fee', 93, 0, '手续费合计'),
(173, '锁定钱包', 'member:member-wallet:lock-wallet', 11, 0, '锁定钱包'),
(174, '解锁钱包', 'member:member-wallet:unlock-wallet', 11, 0, '解锁钱包'),
(176, '角色删除', 'system:role:deletes', 8, 0, '角色删除'),
(177, '保证金管理', 'business:auth:deposit', 0, 8, '保证金管理'),
(178, '查询保证金策略', 'business:auth:deposit:page', 177, 0, '查询保证金策略'),
(179, '创建保证金策略', 'business:auth:deposit:create', 177, 0, '创建保证金策略'),
(180, '修改保证金策略', 'business:auth:deposit:update', 177, 0, '修改保证金策略'),
(181, '退保审核', 'business:cancel-apply:check', 66, 0, '退保审核'),
(182, '退保管理', 'business:cancel-apply:page-query', 66, 0, '退保申请记录'),
(183, '退保用户详情', 'business:cancel-apply:detail', 66, 0, '退保用户详情'),
(184, '认证商家', 'business-auth:apply:page-query', 66, 0, '认证商家'),
(185, '佣金参数详情', 'promotion:reward:detail', 143, 0, '佣金参数详情'),
(186, '认证商家详情', 'business-auth:apply:detail', 66, 0, '认证商家详情'),
(187, '币种详情', 'system:coin:detail', 8, 0, '系统管理下币种详情'),
(188, '添加交易对', 'exchange:exchange-coin:merge', 60, 0, '添加交易对'),
(189, '修改交易对', 'exchange:exchange-coin:alter-rate', 60, 0, '修改交易对'),
(190, '更新后台货币', 'system:coin:update', 8, 0, '系统管理下更新后台货币'),
(191, '添加用户钱包记录', 'system:coin:newwallet', 8, 0, '系统管理下添加用户钱包记录'),
(192, '启动交易引擎', 'exchange:exchange-coin:start-trader', 60, 0, '启动交易引擎'),
(193, '停止交易引擎', 'exchange:exchange-coin:stop-trader', 60, 0, '停止交易引擎'),
(194, '撤销某交易对委托', 'exchange:exchange-coin:cancel-all-order', 60, 0, '撤销某交易对所有委托单'),
(195, '查看交易对盘面', 'exchange:exchange-coin:exchange-overview', 60, 0, '查看交易对盘面'),
(196, '活动列表', 'activity:activity:page-query', 162, 0, '活动列表'),
(197, '添加活动', 'activity:activity:add', 162, 0, '添加活动'),
(198, '修改活动', 'activity:activity:modify', 162, 0, '修改活动'),
(199, '活动详情', 'activity:activity:detail', 162, 0, '查看活动详情'),
(200, '修改活动进度', 'activity:activity:modify-progress', 162, 0, '修改活动进度'),
(201, '活动参与详情', 'activity:activity:orderlist', 162, 0, '活动参与详情列表'),
(202, '派发活动币', 'activity:activity:distribute', 162, 0, ''),
(203, 'CTC管理', 'ctc---------', 0, 3, ''),
(204, '订单列表', 'ctc:order:page-query', 203, 0, '用户买入卖出列表'),
(205, '订单详情', 'ctc:order:order-detail', 203, 0, '订单详情'),
(206, '标记付款', 'ctc:order:pay-order', 203, 0, '标记付款'),
(207, '完成放币', 'ctc:order:complete-order', 203, 0, '完成放币'),
(208, '接单', 'ctc:order:confirm-order', 203, 0, '接单'),
(209, '取消订单', 'ctc:order:cancel-order', 203, 0, '取消订单'),
(210, '承兑商列表', 'ctc:acceptor:page-query', 203, 0, '承兑商列表'),
(211, '切换承兑商状态', 'ctc:acceptor:switch', 203, 0, '切换状态'),
(212, '查看机器人参数', 'exchange:exchange-coin:robot-config', 60, 0, '查看机器人参数'),
(213, '修改一般机器人参数', 'exchange:exchange-coin:alter-robot-config', 60, 0, '修改一般机器人参数'),
(214, '邀请管理', 'invite-------', 0, 2, ''),
(215, '邀请记录', 'invite:management:query', 214, 0, '邀请记录'),
(216, '邀请排名', 'invite:management:rank', 214, 0, '邀请'),
(217, '更新邀请参数', 'invite:management:update-rank', 214, 0, '更新邀请参数'),
(218, '邀请详情', 'invite:management:detail-rank', 214, 0, '邀请详情'),
(219, '创建一般机器人', 'exchange:exchange-coin:create-robot-config', 60, 0, '创建交易机器人'),
(220, '红包管理', 'envelope-----', 0, 6, '红包管理'),
(221, '红包列表', 'envelope:page-query', 220, 0, '红包列表'),
(222, '红包详情', 'envelope:detail', 220, 0, '红包详情'),
(223, '红包领取详情', 'envelope:receive-detail', 220, 0, '红包领取详情'),
(224, '添加红包', 'envelope:add', 220, 0, '添加红包'),
(225, '修改红包', 'envelope:modify', 220, 0, '修改红包'),
(226, '修改定价机器人', 'exchange:exchange-coin:alter-robot-config-price', 60, 0, '修改定价机器人'),
(227, '创建定价机器人', 'exchange:exchange-coin:create-robot-config-price', 60, 0, '创建定价机器人');

-- ----------------------------
-- Table structure for `admin_role`
-- ----------------------------
DROP TABLE IF EXISTS `admin_role`;
CREATE TABLE `admin_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `description` varchar(255) DEFAULT NULL,
  `role` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=88 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of admin_role
-- ----------------------------
INSERT INTO `admin_role` VALUES ('1', '系统管理员', '系统管理员');
INSERT INTO `admin_role` VALUES ('61', '客服', '客服');
INSERT INTO `admin_role` VALUES ('62', '技术', '技术');
INSERT INTO `admin_role` VALUES ('84', '测试', '测试');
INSERT INTO `admin_role` VALUES ('85', '', 'Cayman');
INSERT INTO `admin_role` VALUES ('86', 'aaa22', 'aaa');

-- ----------------------------
-- Table structure for `admin_role_permission`
-- ----------------------------
DROP TABLE IF EXISTS `admin_role_permission`;
CREATE TABLE `admin_role_permission` (
  `role_id` bigint(20) NOT NULL,
  `rule_id` bigint(20) NOT NULL,
  UNIQUE KEY `UKplesprlvm1sob8nl9yc5rgh3m` (`role_id`,`rule_id`),
  KEY `FK52rddd3qje4p49iubt08gplb5` (`role_id`) USING BTREE,
  KEY `FKqf3fhgl5mjqqb0jeupx7yafh0` (`rule_id`) USING BTREE,
  CONSTRAINT `admin_role_permission_ibfk_1` FOREIGN KEY (`role_id`) REFERENCES `admin_role` (`id`),
  CONSTRAINT `admin_role_permission_ibfk_2` FOREIGN KEY (`rule_id`) REFERENCES `admin_permission` (`id`),
  CONSTRAINT `admin_role_permission_ibfk_3` FOREIGN KEY (`role_id`) REFERENCES `admin_role` (`id`),
  CONSTRAINT `admin_role_permission_ibfk_4` FOREIGN KEY (`rule_id`) REFERENCES `admin_permission` (`id`),
  CONSTRAINT `FK52rddd3qje4p49iubt08gplb5` FOREIGN KEY (`role_id`) REFERENCES `admin_role` (`id`),
  CONSTRAINT `FKqf3fhgl5mjqqb0jeupx7yafh0` FOREIGN KEY (`rule_id`) REFERENCES `admin_permission` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of admin_role_permission
-- ----------------------------
INSERT INTO `admin_role_permission` (`role_id`, `rule_id`) VALUES
(1, 2),
(1, 3),
(1, 4),
(1, 5),
(1, 6),
(1, 7),
(1, 8),
(1, 9),
(1, 10),
(1, 11),
(1, 12),
(1, 13),
(1, 14),
(1, 18),
(1, 19),
(1, 20),
(1, 23),
(1, 26),
(1, 27),
(1, 28),
(1, 29),
(1, 30),
(1, 31),
(1, 32),
(1, 33),
(1, 34),
(1, 35),
(1, 36),
(1, 37),
(1, 38),
(1, 39),
(1, 40),
(1, 41),
(1, 42),
(1, 43),
(1, 44),
(1, 45),
(1, 46),
(1, 47),
(1, 48),
(1, 49),
(1, 50),
(1, 51),
(1, 52),
(1, 53),
(1, 54),
(1, 55),
(1, 56),
(1, 57),
(1, 58),
(1, 59),
(1, 60),
(1, 61),
(1, 62),
(1, 63),
(1, 64),
(1, 65),
(1, 66),
(1, 67),
(1, 68),
(1, 69),
(1, 70),
(1, 71),
(1, 72),
(1, 73),
(1, 74),
(1, 75),
(1, 76),
(1, 77),
(1, 78),
(1, 79),
(1, 80),
(1, 81),
(1, 82),
(1, 83),
(1, 84),
(1, 85),
(1, 86),
(1, 87),
(1, 88),
(1, 89),
(1, 90),
(1, 91),
(1, 92),
(1, 93),
(1, 94),
(1, 95),
(1, 96),
(1, 97),
(1, 98),
(1, 99),
(1, 100),
(1, 101),
(1, 102),
(1, 103),
(1, 104),
(1, 105),
(1, 106),
(1, 107),
(1, 108),
(1, 109),
(1, 110),
(1, 111),
(1, 112),
(1, 113),
(1, 114),
(1, 115),
(1, 116),
(1, 117),
(1, 118),
(1, 119),
(1, 120),
(1, 121),
(1, 122),
(1, 123),
(1, 124),
(1, 125),
(1, 126),
(1, 128),
(1, 129),
(1, 130),
(1, 131),
(1, 132),
(1, 133),
(1, 134),
(1, 135),
(1, 136),
(1, 137),
(1, 138),
(1, 139),
(1, 140),
(1, 141),
(1, 142),
(1, 143),
(1, 144),
(1, 145),
(1, 146),
(1, 147),
(1, 148),
(1, 149),
(1, 150),
(1, 151),
(1, 152),
(1, 153),
(1, 154),
(1, 155),
(1, 156),
(1, 158),
(1, 159),
(1, 160),
(1, 161),
(1, 162),
(1, 164),
(1, 165),
(1, 167),
(1, 168),
(1, 169),
(1, 170),
(1, 171),
(1, 172),
(1, 173),
(1, 174),
(1, 176),
(1, 177),
(1, 178),
(1, 179),
(1, 180),
(1, 181),
(1, 182),
(1, 183),
(1, 184),
(1, 185),
(1, 186),
(1, 187),
(1, 188),
(1, 189),
(1, 190),
(1, 191),
(1, 192),
(1, 193),
(1, 194),
(1, 195),
(1, 196),
(1, 197),
(1, 198),
(1, 199),
(1, 200),
(1, 201),
(1, 202),
(1, 203),
(1, 204),
(1, 205),
(1, 206),
(1, 207),
(1, 208),
(1, 209),
(1, 210),
(1, 211),
(1, 212),
(1, 213),
(1, 214),
(1, 215),
(1, 216),
(1, 217),
(1, 218),
(1, 219),
(1, 220),
(1, 221),
(1, 222),
(1, 223),
(1, 224),
(1, 225),
(1, 226),
(1, 227);

