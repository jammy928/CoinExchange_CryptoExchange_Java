-- noinspection SqlDialectInspectionForFile

-- noinspection SqlNoDataSourceInspectionForFile

SET NAMES utf8;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
--  Table structure for `member_wallet_history`
-- ----------------------------
DROP TABLE IF EXISTS `member_wallet_history`;
CREATE TABLE `member_wallet_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `member_id` int(11) NOT NULL,
  `coin_id` varchar(255) NOT NULL,
  `before_balance` decimal(18,8) NOT NULL,
  `after_balance` decimal(18,8) DEFAULT NULL,
  `before_frozen_balance` decimal(18,8) DEFAULT '0.00000000',
  `after_frozen_balance` decimal(18,8) DEFAULT '0.00000000',
  `op_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1338 DEFAULT CHARSET=utf8;

SET FOREIGN_KEY_CHECKS = 1;


-- 修改member_wallet 表结构，增加after update trigger

create trigger trigger_update_wallet
after update on member_wallet
for each row
begin
 INSERT INTO member_wallet_history(member_id,coin_id,before_balance,after_balance,before_frozen_balance,after_frozen_balance,op_time) VALUES (new.member_id,new.coin_id,old.balance,new.balance,old.frozen_balance,new.frozen_balance,now());
end;