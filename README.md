# Crypto-Exchange / Coin-Exchange

#### 简要介绍

本项目是基于Java开发的比特币交易所 | BTC交易所 | ETH交易所 | 数字货币交易所 | 交易平台 | 撮合交易引擎。本项目基于SpringCloud微服务开发，可用来搭建和二次开发数字货币交易所，有完整的撮合交易引擎源码、后台管理（后端+前端）、前台（交易页面、活动页面、个人中心等）、安卓APP源码(有偿提供）、苹果APP源码(有偿提供）、币种钱包RPC源码。

#### 关于区块链钱包对接

当你拿到代码以后，对本项目进行调试运行期间，可不连接区块链节点，这并不会有太大的影响；即使不连接区块链节点，你也可以部署其一个具有撮合交易功能的交易平台（只是用户无法通过钱包地址充值而已）。 

当你渐渐熟悉整套系统，同时对区块链运行原理、节点搭建、区块链浏览器有一定基础储备后，就可以开始对 **01_wallet_rpc** 文件夹下的项目进行研究。每个币种对应不同的数据访问方式，大部分区块链项目的钱包操作方式是相同的或十分相似的，比如BTC、LTC、BCH、BSV、BCD等比特币衍生币，其API操作方式几乎一样；再比如ETH，当你掌握一个合约币种的操作，其他基于ETH发行的数字货币的操作方式几乎一样。所以，基本上当你花时间弄懂了一个，就懂了一堆币种。

本项目使用的钱包操作方案也是不同的，也尽可能的为大家展示了不同用法：
- 如BTC、USDT，使用的自建全节点，现在差不多需要300G硬盘空间；
- 如ETH，使用的是自建轻节点（[参考文章](https://www.cnblogs.com/bizzan/p/11341713.html)），因为全节点需要硬盘空间太大；
- 如BCH、BSV等，使用的是第三方区块链浏览器获取数据；
- 如XRP，官方就已经提供了访问区块数据的接口（[Ripple API GitHub地址](https://github.com/ripple/ripple-lib/)）

一般而言，当交易所来往资金量不大的时候，你可以自己摸索，但是当交易所资金量大了以后，如果你对自己操作钱包不太放心，你也可以使用第三方的钱包服务，当然，这需要你与钱包服务商进行谈判，付个年费什么的。

#### 系统架构概要

随便画的一个草图，凑合看吧。。。
![Framework整体说明](https://images.gitee.com/uploads/images/2020/0322/192334_8601c30c_2182501.png "微信截图_20200322192233.png")

#### 开发参考

开发参考文档：[https://gitee.com/cexchange/CoinExchange/blob/master/DEVELOP.md](https://gitee.com/cexchange/CoinExchange/blob/master/DEVELOP.md)

管理后台截图：[https://gitee.com/cexchange/CoinExchange/tree/master/09_DOC/%E7%AE%A1%E7%90%86%E5%90%8E%E5%8F%B0%E8%BF%90%E8%A1%8C%E6%88%AA%E5%9B%BE](https://gitee.com/cexchange/CoinExchange/tree/master/09_DOC/%E7%AE%A1%E7%90%86%E5%90%8E%E5%8F%B0%E8%BF%90%E8%A1%8C%E6%88%AA%E5%9B%BE)

#### 系统运行环境

1.  Centos 6.8
2.  MySQL 5.5.16
3.  Redis-x64-3.2.100
4.  Mongodb 3.6.13
5.  kafka_2.11-2.2.1
6.  nginx-1.16.0
7.  JRE 8u241
8.  JDK 1.8
9.  Vue
10. Zookeeper

#### 文件目录说明

 **00_framework**

└─———admin   后台管理API

└─———bitrade-job     任务管理

└─———chat   OTC聊天

└─———cloud  SpringCloud微服务管理

└─———core   核心

└─———exchange   撮合交易引擎

└─———exchange-api   撮合交易API

└─———exchange-core   撮合交易核心

└─———jar   第三方类库

└─———market    市场行情API、K线生成

└─———otc-api   OTC交易API

└─———otc-core  OTC核心

└─———sql    SQL脚本

└─———ucenter-api    用户个人中心API

└─———wallet      钱包资产管理，负责与RPC对接


 **01_wallet_rpc**

└─———act

└─———bch

└─———bitcoin

└─———bsv

└─———btm

└─———ect

└─———eos

└─———erc-eusdt

└─———erc-token

└─———eth

└─———eth-support

└─———lib

└─———ltc

└─———rpc-common

└─———usdt

└─———xmr

 **02_App_Android**

 **03_App_IOS**

 **04_Web_Admin**

 **05_Web_Front**


#### 使用教程

1.  准备mysql数据库，创建名称为“xxxx”的数据库
2.  准备redis缓存数据库
3.  准备kafka流式处理环境（先配置运行zookper，接着配置运行kafka）
4.  准备mongodb数据库环境，创建用户admin、xxxx，创建bitrade数据库
5.  准备阿里云OSS（修改项目中需要配置的地方）
6.  准备nginx，修改配置文件（可选，正式上线需配置）
7.  修改framework代码中的配置文件为准备环境配置参数
8.  编译生成jar可执行文件
9.  运行cloud.jar（微服务注册中心）
10.  运行market.jar（行情中心）
11. 运行exchange.jar（交易中心）
12. 运行ucenter.jar（用户中心）
13. 运行其他模块
14. 打开mysql，导入framework代码中的sql文件夹中xxxxxxx.sql文件，注意，trigger的sql如果报错，需要针对wallet表添加trigger
15. 运行前端vue项目
16. 运行后端vue项目
17. 运行钱包RPC
18. 运行自动交易机器人程序（本部分代码未上传，但不影响）

#### 技术支持

本数字货币交易系统系我所在公司为交易所开发的项目，该交易所因团队原因已停止运营，我司也已于2月解散。因我参与项目时，负责整体研发管理、架构设计以及客户对接，所以掌握所有代码。

本系统在功能使用上有一些需要特别注意的地方，例如新建交易对以后的其他操作，操作不当会引起数据紊乱的错误出现。

本人可提供有偿技术帮助与使用培训指导!

联系QQ：877070886

#### 关于移动端源码与交易机器人源码

Android源码、IOS源码、交易机器人源码有偿提供，需要的加QQ：877070886

Android源码：5000（不包调试安装）、50000（包调试安装）

IOS源码：8000（不包调试安装）、80000（包调试安装）

交易机器人源码：5000（不包调试安装）、30000（包调试安装）

移动端源码基本上高手很容易调试安装成功，如果不太会的，可以选择让我帮助。

#### 注意事项

当内存不足时，在linux控制台输入top可以查看java进程占用了大量内存（一个java进程占用1G以上），因为有很多jar包需要运行，所以需要控制某些jar包使用的内存，你可选择几个不怎么耗费资源的项目，如下：

java -jar -Xms128m -Xmx128m -Xmn200m -Xss256k  admin-api.jar

java -jar -Xms512m -Xmx512m -Xmn200m -Xss256k  cloud.jar

java -jar -Xms512m -Xmx512m -Xmn200m -Xss256k  wallet.jar

#### 关于邮件&短信

1.  本系统支持邮件、短信发送系统运营状态
2.  系统通知/报警支持：用户注册、用户认证、用户充值/提现、币种RPC运行状态、系统资源使用监控等24种监控

#### 关于数据库脚本的问题

有朋友反映没有完整的SQL文件，这是因为编译成功的Jar，首次运行后会自动将Entity映射成数据库结构，项目中的SQL只是完成一些Springcloud无法完成的数据库结构。
数据库自动生成配置位于application.properties配置文件：

> #jpa

> spring.jpa.show-sql=true

> spring.data.jpa.repositories.enabled=true

> spring.jpa.hibernate.ddl-auto=update

spring.jpa.hibernate.ddl-auto=update
这个配置会自动更新数据库结构。

#### 核心功能说明（用户端）

1.  注册/登录/实名认证/审核（目前仅支持手机，二次开发可加入邮件，很简单）

2.  Banner/公告/帮助/定制页面（Banner支持PC与APP分开设置，帮助支持各种分类模式）

3.  法币C2C交易/法币OTC交易（支持两种法币模式，项目早期可由平台承担C2C法币兑换，后期可开放OTC交易）

4.  币币交易（支持限价委托、市价委托，二次开发可加入其它委托模式）

5.  邀请注册/推广合伙人（支持对邀请推广人数、佣金进行以日、周、月的排行统计）

6.  创新实验室（该部分支持功能较多，分项说明。另，APP暂不全部支持该功能）

>    6-1.  首发抢购活动模式（如发行新交易对时，可对交易对设置一定数量的币种进行抢购）
>
>    6-2.  首发分摊活动模式（如发行BTC/USDT交易对之前，官方拿出5BTC做活动，根据用户充值抵押的USDT多少进行均分BTC）
>
>    6-3.  控盘抢购模式（如发行ZZZ/USDT交易对之前，ZZZ币种价格为5USDT，官方发行活动价为0.5USDT，则可使用该模式）
>
>    6-4.  控盘均摊模式（如6-3，只不过平均分配）
>
>    6-5.  矿机活动模式（支持用户抵押一定数量的币种，由官方承诺每月返还一定数量的币种）

7.  红包功能（支持平台及官方发放一定数量币种的红包，此功能适合用户裂变）

8.  用户资产管理、流水管理、委托管理、实名管理等各种基础管理


#### 核心功能说明（管理端）

1.  概要（查看平台运行数据，包含交易额、注册人数、充值等）

2.  会员管理（会员信息管理、会员实名审核、会员实名管理、会员余额管理、会员充值/冻结余额等）

3.  邀请管理（会员邀请信息、会员邀请排行管理）

4.  CTC管理（CTC订单管理、流水管理、承兑商管理）

5.  内容管理（PC广告管理、APP广告管理、公告管理、帮助管理）

6.  财务管理（充值提现管理、财务流水管理、对账管理、币种钱包余额管理）

7.  币币管理（新建交易对、管理交易对、新建交易机器人、设置交易机器人参数、设置行情引擎/交易引擎、撤销所有委托）

8.  活动管理（新建活动、矿机认购、抢购/瓜分管理）

9.  红包管理（平台红包管理、用户红包管理）

10.  系统管理（角色管理、部门管理、用户管理、权限管理、币种管理、RPC管理、版本管理）

11.  保证金管理（此功能设计时考虑到，但实际运营期间未使用到）

12.  OTC管理（广告管理、订单管理、OTC币种管理、退保管理等，此功能未获得实际运营检验）


#### 系统展示（PC前端）

![首页](https://images.gitee.com/uploads/images/2020/0322/193650_8e53953d_2182501.png "图片1.png")

![交易界面](https://images.gitee.com/uploads/images/2020/0322/193732_047f27a2_2182501.png "图片2.png")

![法币交易](https://images.gitee.com/uploads/images/2020/0322/193746_ad1d16e6_2182501.png "图片3.png")

![登录](https://images.gitee.com/uploads/images/2020/0322/193759_edc5dc7b_2182501.png "图片5.png")

![活动/创新实验室](https://images.gitee.com/uploads/images/2020/0322/193839_6f18a03d_2182501.png "图片7.png")

![推广合伙人](https://images.gitee.com/uploads/images/2020/0325/130843_4ff70c13_2182501.png "推广.png")

![公告](https://images.gitee.com/uploads/images/2020/0322/193852_3ad12a6f_2182501.png "图片8.png")

![帮助](https://images.gitee.com/uploads/images/2020/0322/193902_ef09925e_2182501.png "图片9.png")

#### 系统运行展示（APP前端）

![首页](https://images.gitee.com/uploads/images/2020/0322/193927_9940ca7c_2182501.jpeg "图片10.jpg")

![行情](https://images.gitee.com/uploads/images/2020/0322/193941_ff5a16a2_2182501.jpeg "图片11.jpg")

![K线](https://images.gitee.com/uploads/images/2020/0322/193951_abf7b5b6_2182501.jpeg "图片12.jpg")

![交易](https://images.gitee.com/uploads/images/2020/0322/194003_d14a772a_2182501.jpeg "图片13.jpg")

![个人中心](https://images.gitee.com/uploads/images/2020/0322/194021_a047d3a5_2182501.jpeg "图片14.jpg")

![个人资产管理](https://images.gitee.com/uploads/images/2020/0322/194059_faeeeb4a_2182501.jpeg "图片15.jpg")

![邀请管理](https://images.gitee.com/uploads/images/2020/0322/194112_7ae11b00_2182501.jpeg "图片16.jpg")

#### 手机Web端部分页面

![APP下载](https://images.gitee.com/uploads/images/2020/0325/130921_7d8dee06_2182501.jpeg "12.jpg")

![抢红包](https://images.gitee.com/uploads/images/2020/0325/130936_18809c8f_2182501.jpeg "11.jpg")

![活动/创新实验室](https://images.gitee.com/uploads/images/2020/0325/130948_e36b562d_2182501.jpeg "13.jpg")


#### 系统运行展示（后端）

![登录](https://images.gitee.com/uploads/images/2020/0322/194251_9b5293ff_2182501.png "图片17.png")

![首页](https://images.gitee.com/uploads/images/2020/0322/194305_f83e4f90_2182501.png "图片18.png")

![用户管理](https://images.gitee.com/uploads/images/2020/0322/194321_73eb8f58_2182501.png "图片19.png")

![邀请管理](https://images.gitee.com/uploads/images/2020/0322/194337_fd257186_2182501.png "图片20.png")

![法币交易订单管理](https://images.gitee.com/uploads/images/2020/0322/194406_ebe7328d_2182501.png "图片21.png")

![首页Banner管理](https://images.gitee.com/uploads/images/2020/0322/194433_4fb39b0a_2182501.png "图片22.png")

![交易对管理](https://images.gitee.com/uploads/images/2020/0322/194450_1eb7bb6f_2182501.png "图片23.png")

![活动管理](https://images.gitee.com/uploads/images/2020/0322/194505_204d23ce_2182501.png "图片24.png")

![红包管理](https://images.gitee.com/uploads/images/2020/0322/194531_e12eb93a_2182501.png "图片25.png")

![币种管理](https://images.gitee.com/uploads/images/2020/0322/194618_fe17409a_2182501.png "图片26.png")

![OTC管理，后端开发完成，前端未对接](https://images.gitee.com/uploads/images/2020/0322/194654_bd0acbe7_2182501.png "图片27.png")

#### APP下载图片示例

![输入图片说明](https://images.gitee.com/uploads/images/2020/0324/182826_f0617759_2182501.png "download1.png")
![输入图片说明](https://images.gitee.com/uploads/images/2020/0324/182834_7a82f8a8_2182501.png "download2.png")
![输入图片说明](https://images.gitee.com/uploads/images/2020/0324/182840_c3d08fb7_2182501.png "download3.png")
![输入图片说明](https://images.gitee.com/uploads/images/2020/0324/182850_8b19fe57_2182501.png "download4.png")
![输入图片说明](https://images.gitee.com/uploads/images/2020/0324/182856_9206a79a_2182501.png "download5.png")

#### 特别注意

任何使用本源码从事商业活动，对别人和自己造成损失的，本人概不负责！
