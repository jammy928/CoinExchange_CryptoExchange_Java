# Crypto-Exchange / Coin-Exchange   
# 全网唯一开源核心代码的交易所，架构/代码质量看得见  
# 我想这可能是你搭建交易所，或者二次开发的最好选择

>  声明一：我已在新公司上班，一些说明性的东西我会抽空在这里更新，以方便大家编译、搭建、开发  
>  声明二：APP源码及交易机器人源码未开源（有偿提供），有需要的添加QQ：877070886  
>  声明三：请注意，本源码唯一交易渠道为QQ：877070886，没有其他QQ或微信

## 简要介绍

本项目是基于Java（SpringCloud）开发的比特币交易所 | BTC交易所 | ETH交易所 | 数字货币交易所 | 交易平台 | 撮合交易引擎。本项目基于SpringCloud微服务开发，可用来搭建和二次开发数字货币交易所，有完整的系统组成部分。 
- 撮合交易引擎 
- 后台管理（后端+前端） 
- 前台（交易页面、活动页面、个人中心等） 
- 原生安卓APP源码 
- 原生苹果APP源码 
- 币种钱包RPC源码 
 
## 系统架构概要

随便画的几个草图，凑合看吧。。。

#### 整体架构

![整体架构](https://images.gitee.com/uploads/images/2020/0407/143836_eac248e5_2182501.png "1.png")

#### 逻辑架构

![逻辑架构](https://images.gitee.com/uploads/images/2020/0407/143856_66257325_2182501.png "2.png")

#### 部署架构

![部署架构](https://images.gitee.com/uploads/images/2020/0408/141710_07923003_2182501.png "1117.png")

#### 依赖关系

![依赖关系](https://images.gitee.com/uploads/images/2020/0407/194510_89803a9d_2182501.png "QQ截图20200407194419.png")

## 系统演示视频

PC前端（用户Web端）：[https://gitee.com/cexchange/CoinExchange/attach_files](https://gitee.com/cexchange/CoinExchange/attach_files)

手机APP端：[https://gitee.com/cexchange/CoinExchange/attach_files](https://gitee.com/cexchange/CoinExchange/attach_files)

管理后台：[https://gitee.com/cexchange/CoinExchange/attach_files](https://gitee.com/cexchange/CoinExchange/attach_files)

## 开发参考

开发参考文档：[https://gitee.com/cexchange/CoinExchange/blob/master/DEVELOP.md](https://gitee.com/cexchange/CoinExchange/blob/master/DEVELOP.md)

管理后台截图：[https://gitee.com/cexchange/CoinExchange/tree/master/09_DOC/管理后台截图](https://gitee.com/cexchange/CoinExchange/tree/master/09_DOC/%E7%AE%A1%E7%90%86%E5%90%8E%E5%8F%B0%E8%BF%90%E8%A1%8C%E6%88%AA%E5%9B%BE)

## 关于服务器配置与部署

如果你想在自己的电脑或云服务器上搭建一套交易所系统，我这里准备了一些基本的部署手册，当然，在linux/unix上安装软件并不是一件简单的事，你需要有一定的linux基础和命令行功底，同时还要有遇到问题解决问题的勇气和耐心，祝你顺利！

- [服务器配置参考手册](./09_DOC/00_服务器部署/推荐服务器配置.md)  
- [安装基础环境手册](./09_DOC/00_服务器部署/安装基础环境.md)  
- [服务部署脚本](./09_DOC/00_服务器部署/install.sh)  
- [安装MySql手册](./09_DOC/00_服务器部署/安装MySql.md)  
- [安装Redis手册](./09_DOC/00_服务器部署/安装Redis.md)  
- [安装Zookeeper手册](./09_DOC/00_服务器部署/安装Zookeeper.md)  
- [安装Kafka手册](./09_DOC/00_服务器部署/安装Kafka.md)  
- [安装Mongodb手册](./09_DOC/00_服务器部署/安装Mongodb.md)  
- [搭建BTC钱包节点手册](./09_DOC/00_服务器部署/搭建BTC钱包.md)  
- [搭建ETH钱包节点手册](./09_DOC/00_服务器部署/搭建ETH钱包.md)  
- [搭建USDT钱包节点手册](./09_DOC/00_服务器部署/搭建USDT钱包.md)  

## 关于SpringCloud

Spring Cloud是一系列框架的有序集合。它利用Spring Boot的开发便利性巧妙地简化了分布式系统基础设施的开发，如服务发现注册、配置中心、消息总线、负载均衡、断路器、数据监控等，都可以用Spring Boot的开发风格做到一键启动和部署。Spring Cloud并没有重复制造轮子，它只是将各家公司开发的比较成熟、经得起实际考验的服务框架组合起来，通过Spring Boot风格进行再封装屏蔽掉了复杂的配置和实现原理，最终给开发者留出了一套简单易懂、易部署和易维护的分布式系统开发工具包。
一般而言，一个完整的SpringCloud框架应该如下图所示：

![SpringCloud框架图](https://images.gitee.com/uploads/images/2020/0408/133052_3ec984df_2182501.png "图片2.png")

如果对SpringCloud尚不熟悉，可以先简单学习一下SpringCloud的相关教程，这样再回来看本项目，会更加容易上手。
提醒一下，因为Springcloud框架图中是完整的架构，在开发的时候，我们会适当的裁剪一些内容，从而让开发和部署更加快速，所以有一些出入的地方。

## 关于撮合交易引擎

本系统对交易队列采用内存撮合的方式进行，以Kafka做撮合订单信息传输，MongoDB持久化订单成交明细，MySQL记录订单总体成交。
其中01_Framework/Exchange项目主要负责内存撮合，01_Framework/Market项目主要负责订单成交持久化、行情生成、行情推送等服务，包括：

- K线数据，间隔分别为：1分钟、5分钟、15分钟、30分钟、1小时、1天、1周、1月
- 所有交易对的市场深度（market depth）数据
- 所有交易对的最新价格
- 最近成交的交易对

 **内存撮合交易支持的模式** 

- 限价订单与限价订单撮合
- 市价订单与限价订单撮合
- 限价订单与市价订单撮合
- 市价订单与市价订单撮合

 **限价&市价订单处理逻辑** 

![限价&市价订单处理逻辑](https://images.gitee.com/uploads/images/2020/0408/144633_457c0552_2182501.png "2222.png")
注意：这个图很久以前的了，最新代码中的逻辑更复杂了

 **撮合引擎支持的其他功能** 

除了普通的限价与市价撮合交易功能外，本系统的撮合交易引擎还引入了活动成交模式，通过设置交易对（如：BTC/USDT）的开始交易时间、初始发行量、初始发行价、活动模式等参数，可以制定出丰富的撮合交易模式，从而满足不同的撮合模式。

 **举例说明** 

交易所预计在2020年8月8日 12时00分00秒上线交易对AAA/USDT，但是作为一个新上线的币种，没有活动怎么能行呢？项目方或交易所决定拿出10000个AAA以0.0001USDT（市场行情价：0.0005）的价格让大家抢购。本系统支持对这种活动的设置。

另外，如果项目方或交易所决定拿出10000个AAA以0.0001USDT的价格发行，不希望大家抢购，而是希望所有充值USDT的用户能够平均瓜分10000个AAA，本系统也支持这种活动的设置。

 **总结** 

总之，本系统支持高度自定义的撮合模式，同时你也可以开发出你自己想要的撮合交易模式，只需要通过修改Exchange项目中的撮合逻辑就可以。

## 关于技术构成

- 后端：Spring、SpringMVC、SpringData、SpringCloud、SpringBoot
- 数据库：Mysql、Mongodb
- 其他：redis、kafka、阿里云OSS、腾讯防水校验
- 前端：Vue、iView、less

## 实际演示网站

[https://www.bizzan.com](https://www.bizzan.com)

这个是给客户做的，但是后来客户不运营了，所以遗留了这个网站，因为我没有服务器权限，所以这个网站随时可能无法访问。

搭建起来一个测试站点需要购买好几台云服务器，成本较大，所以我自己没有搭建测试站，但是系统是完整的，经过了将近一年的商用及实际运营考验。


> 关于交易机器人   
> 交易机器人是自动交易的程序，可以根据外部行情，自动进行交易，让本交易所的交易对价格与外部保持一致，防止因部分用户“搬砖”导致的损失。   

> 关于搬砖  
> 例如A交易所BTC价格是10000USDT，而B交易所的BTC价格是9500USDT，搬砖就是在B交易所通过9500USDT的价格买入BTC，然后转账到A交易所，赚取差价（500USDT）。    
> 如果交易所没有交易机器人，就会导致本交易所的币种价格与其他主流交易所相比有差价，从而让用户“搬砖”，导致交易所损失。   
> 另外，交易机器人还有一个功能，就是在交易所初期运营的时候，形成一个初期的交易深度，不会让用户觉得交易所冷清，没有用户。   

我本人是Java程序员，对移动端开发不太了解，所以包调试安装实际上也是我付费请别人帮忙的。

如果你没有技术人员，我可以帮助你搭建一套完整的交易所系统，但是需要你请一到两名维护人员，因为系统的稳定运行少不了运维人员。

联系QQ：877070886

==============================================

## 系统运行环境

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

## 生产环境建议配置

![生产环境建议配置](https://images.gitee.com/uploads/images/2020/0406/204436_dfff09a6_2182501.png "QQ截图20200406204341.png")

## 文件目录说明

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

└─———otc-api   OTC交易API（如无需otc功能可不启动）

└─———otc-core  OTC核心

└─———sql    SQL脚本

└─———ucenter-api    用户个人中心API

└─———wallet      钱包资产管理，负责与RPC对接


 **01_wallet_rpc**

└─———bitcoin

└─———bsv

└─———btm

└─———eos

└─———erc-eusdt

└─———erc-token（可对接各种ERC20币种）

└─———eth

└─———ltc

└─———usdt


 **02_App_Android**

 **03_App_IOS**

 **04_Web_Admin**

 **05_Web_Front**


## 使用教程

1.  准备mysql数据库，创建名称为“xxxx”的数据库
2.  准备redis缓存数据库
3.  准备kafka流式处理环境（先配置运行zookper，接着配置运行kafka）
4.  准备mongodb数据库环境，创建用户admin、xxxx，创建bitrade数据库
5.  准备阿里云OSS（修改项目中需要配置的地方）
6.  准备nginx，修改配置文件（可选，正式上线需配置）
7.  修改framework代码中的配置文件为准备环境配置参数
8.  编译生成jar可执行文件
9.  运行cloud.jar（微服务注册中心）
10. 运行exchange.jar（撮合交易引擎）
11. 运行market.jar（行情中心，需要等待Exchange.jar完全启动）
12. 运行ucenter.jar（用户中心）
13. 运行其他模块（wallet.jar、chat.jar、otc-api.jar等）
14. 打开mysql，导入framework代码中的sql文件夹中xxxxxxx.sql文件，注意，trigger的sql如果报错，需要针对wallet表添加trigger
15. 运行前端vue项目
16. 运行后端vue项目
17. 运行钱包RPC
18. 运行自动交易机器人程序（本部分代码未上传，但不影响）
19. 运行Admin项目（该服务并不依赖其他服务，因此也可只运行此项目，直接查看后台）

## 技术支持

本数字货币交易系统系我所在公司为交易所开发的项目，该交易所因团队原因已停止运营，我司也已于2月解散。因我参与项目时，负责整体研发管理、架构设计以及客户对接，所以掌握所有代码。

本系统在功能使用上有一些需要特别注意的地方，例如新建交易对以后的其他操作，操作不当会引起数据紊乱的错误出现。

本人可提供有偿技术帮助与使用培训指导!

联系QQ：877070886

## 注意事项

当内存不足时，在linux控制台输入top可以查看java进程占用了大量内存（一个java进程占用1G以上），因为有很多jar包需要运行，所以需要控制某些jar包使用的内存，你可选择几个不怎么耗费资源的项目，如下：


```
java -jar -Xms128m -Xmx128m -Xmn200m -Xss256k  admin-api.jar

java -jar -Xms512m -Xmx512m -Xmn200m -Xss256k  cloud.jar

java -jar -Xms512m -Xmx512m -Xmn200m -Xss256k  wallet.jar
```


## 关于邮件&短信

1.  本系统支持邮件、短信发送系统运营状态
2.  系统通知/报警支持：用户注册、用户认证、用户充值/提现、币种RPC运行状态、系统资源使用监控等24种监控

## 关于数据库脚本的问题

有朋友反映没有完整的SQL文件，这是因为编译成功的Jar，首次运行后会自动将Entity映射成数据库结构，项目中的SQL只是完成一些Springcloud无法完成的数据库结构。
数据库自动生成配置位于application.properties配置文件：

> #jpa  
> spring.jpa.show-sql=true  
> spring.data.jpa.repositories.enabled=true  
> spring.jpa.hibernate.ddl-auto=update  

spring.jpa.hibernate.ddl-auto=update
这个配置会自动更新数据库结构。

## 核心功能说明（用户端）

- [x] 1.  注册/登录/实名认证/审核（目前仅支持手机，二次开发可加入邮件，很简单）
- [x] 2.  Banner/公告/帮助/定制页面（Banner支持PC与APP分开设置，帮助支持各种分类模式）
- [x] 3.  法币C2C交易/法币OTC交易（支持两种法币模式，项目早期可由平台承担C2C法币兑换，后期可开放OTC交易）
- [x] 4.  币币交易（支持限价委托、市价委托，二次开发可加入其它委托模式）
- [x] 5.  邀请注册/推广合伙人（支持对邀请推广人数、佣金进行以日、周、月的排行统计）
- [x] 6.  创新实验室（该部分支持功能较多，分项说明。另，APP暂不全部支持该功能）

>    - [x] 6-1.  首发抢购活动模式（如发行新交易对时，可对交易对设置一定数量的币种进行抢购）  
>    - [x] 6-2.  首发分摊活动模式（如发行BTC/USDT交易对之前，官方拿出5BTC做活动，根据用户充值抵押的USDT多少进行均分BTC）  
>    - [x] 6-3.  控盘抢购模式（如发行ZZZ/USDT交易对之前，ZZZ币种价格为5USDT，官方发行活动价为0.5USDT，则可使用该模式）  
>    - [x] 6-4.  控盘均摊模式（如6-3，只不过平均分配）  
>    - [x] 6-5.  矿机活动模式（支持用户抵押一定数量的币种，由官方承诺每月返还一定数量的币种）  

- [x] 7.  红包功能（支持平台及官方发放一定数量币种的红包，此功能适合用户裂变）
- [x] 8.  用户资产管理、流水管理、委托管理、实名管理等各种基础管理


## 核心功能说明（管理端）

- [x] 1.  概要（查看平台运行数据，包含交易额、注册人数、充值等）
- [x] 2.  会员管理（会员信息管理、会员实名审核、会员实名管理、会员余额管理、会员充值/冻结余额等）
- [x] 3.  邀请管理（会员邀请信息、会员邀请排行管理）
- [x] 4.  CTC管理（CTC订单管理、流水管理、承兑商管理）
- [x] 5.  内容管理（PC广告管理、APP广告管理、公告管理、帮助管理）
- [x] 6.  财务管理（充值提现管理、财务流水管理、对账管理、币种钱包余额管理）
- [x] 7.  币币管理（新建交易对、管理交易对、新建交易机器人、设置交易机器人参数、设置行情引擎/交易引擎、撤销所有委托）
- [x] 8.  活动管理（新建活动、矿机认购、抢购/瓜分管理）
- [x] 9.  红包管理（平台红包管理、用户红包管理）
- [x] 10.  系统管理（角色管理、部门管理、用户管理、权限管理、币种管理、RPC管理、版本管理）
- [x] 11.  保证金管理（此功能设计时考虑到，但实际运营期间未使用到）
- [x] 12.  OTC管理（广告管理、订单管理、OTC币种管理、退保管理等，此功能未获得实际运营检验）

## 关于区块链钱包对接

本项目提供两种钱包对接方式，一种是自建节点+区块链浏览器的方式，另一种是第三方钱包对接方式。如果你想使用自建节点或区块链浏览器，你直接使用00_framework中的代码进行编译即可。如果你想使用第三方钱包对接，你可以下载07_Uduncloud文件夹的优盾钱包的项目文件，把他们复制到00_framework中即可。

当你拿到代码以后，对本项目进行调试运行期间，可不连接区块链节点，这并不会有太大的影响；即使不连接区块链节点，你也可以部署其一个具有撮合交易功能的交易平台（只是用户无法通过钱包地址充值而已）。 

当你渐渐熟悉整套系统，同时对区块链运行原理、节点搭建、区块链浏览器有一定基础储备后，就可以开始对 **01_wallet_rpc** 文件夹下的项目进行研究。每个币种对应不同的数据访问方式，大部分区块链项目的钱包操作方式是相同的或十分相似的，比如BTC、LTC、BCH、BSV、BCD等比特币衍生币，其API操作方式几乎一样；再比如ETH，当你掌握一个合约币种的操作，其他基于ETH发行的数字货币的操作方式几乎一样。所以，基本上当你花时间弄懂了一个，就懂了一堆币种。

本项目使用的钱包操作方案也是不同的，也尽可能的为大家展示了不同用法：
- 如BTC、USDT，使用的自建全节点，现在差不多需要300G硬盘空间；
- 如ETH，使用的是自建轻节点（[参考文章](https://www.cnblogs.com/bizzan/p/11341713.html)），因为全节点需要硬盘空间太大；
- 如BCH、BSV等，使用的是第三方区块链浏览器获取数据；
- 如XRP，官方就已经提供了访问区块数据的接口（[Ripple API GitHub地址](https://github.com/ripple/ripple-lib/)）

一般而言，当交易所来往资金量不大的时候，你可以自己摸索，但是当交易所资金量大了以后，如果你对自己操作钱包不太放心，你也可以使用第三方的钱包服务，当然，这需要你与钱包服务商进行谈判，付个年费什么的。

下图是用户充值监控逻辑的简要说明图，简单看看就行：

![充值逻辑](https://images.gitee.com/uploads/images/2020/0327/162223_5d418523_2182501.png "13981024-76374161aedf70d6.png")


## 系统展示（PC前端）

![首页](https://images.gitee.com/uploads/images/2020/0327/135803_75ec9a0b_2182501.png "01_首页.png")

![币币交易](https://images.gitee.com/uploads/images/2020/0327/135834_4a5fb1c4_2182501.png "02_币币交易.png")

![法币交易](https://images.gitee.com/uploads/images/2020/0327/135902_a7286b9c_2182501.png "03_法币交易CTC.png")

![登录](https://images.gitee.com/uploads/images/2020/0322/193759_edc5dc7b_2182501.png "图片5.png")

![活动/创新实验室](https://images.gitee.com/uploads/images/2020/0327/135930_0c02d004_2182501.png "04_创新实验室.png")

![创新实验室详情](https://images.gitee.com/uploads/images/2020/0327/140037_074a81a4_2182501.png "创新实验室详情.png")

![推广合伙人](https://images.gitee.com/uploads/images/2020/0327/140003_9b962fe7_2182501.png "07_推广合伙人.png")

![公告](https://images.gitee.com/uploads/images/2020/0322/193852_3ad12a6f_2182501.png "图片8.png")

![帮助](https://images.gitee.com/uploads/images/2020/0322/193902_ef09925e_2182501.png "图片9.png")

## 系统运行展示（APP前端）

![首页](https://images.gitee.com/uploads/images/2020/0322/193927_9940ca7c_2182501.jpeg "图片10.jpg")

![行情](https://images.gitee.com/uploads/images/2020/0322/193941_ff5a16a2_2182501.jpeg "图片11.jpg")

![K线](https://images.gitee.com/uploads/images/2020/0322/193951_abf7b5b6_2182501.jpeg "图片12.jpg")

![交易](https://images.gitee.com/uploads/images/2020/0322/194003_d14a772a_2182501.jpeg "图片13.jpg")

![个人中心](https://images.gitee.com/uploads/images/2020/0322/194021_a047d3a5_2182501.jpeg "图片14.jpg")

![个人资产管理](https://images.gitee.com/uploads/images/2020/0322/194059_faeeeb4a_2182501.jpeg "图片15.jpg")

![邀请管理](https://images.gitee.com/uploads/images/2020/0322/194112_7ae11b00_2182501.jpeg "图片16.jpg")

## 手机Web端部分页面

![APP下载](https://images.gitee.com/uploads/images/2020/0325/130921_7d8dee06_2182501.jpeg "12.jpg")

![抢红包](https://images.gitee.com/uploads/images/2020/0325/130936_18809c8f_2182501.jpeg "11.jpg")

![活动/创新实验室](https://images.gitee.com/uploads/images/2020/0325/130948_e36b562d_2182501.jpeg "13.jpg")


## 系统运行展示（后端）

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

## APP下载图片示例

![输入图片说明](https://images.gitee.com/uploads/images/2020/0324/182826_f0617759_2182501.png "download1.png")
![输入图片说明](https://images.gitee.com/uploads/images/2020/0324/182834_7a82f8a8_2182501.png "download2.png")
![输入图片说明](https://images.gitee.com/uploads/images/2020/0324/182840_c3d08fb7_2182501.png "download3.png")
![输入图片说明](https://images.gitee.com/uploads/images/2020/0324/182850_8b19fe57_2182501.png "download4.png")
![输入图片说明](https://images.gitee.com/uploads/images/2020/0324/182856_9206a79a_2182501.png "download5.png")

## 特别注意

任何使用本源码从事商业活动，对别人和自己造成损失的，本人概不负责！
