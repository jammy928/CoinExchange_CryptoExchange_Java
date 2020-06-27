# Crypto-Exchange / Coin-Exchange   
# Maybe The best open source core code exchange in the entire net, the architecture/code quality is visible.
# I think this may be the best choice for you to build an exchange or secondary development

>  Statement 1: I have been working in the new company. I will take the time to update some descriptive things here so that everyone can compile, build, and develop  
>  Statement 2: The APP source code and the trading robot source code are not open source (provided for a fee), if necessary, email: 877070886@qq.com  

# I am a chinese，so chinese is my mother language, But I can also use both English and Japanese to communicate with you. 

## Introduction

This project is a bitcoin exchange based on Java (SpringCloud) | BTC exchange | ETH exchange | digital currency exchange | trading platform | matching trading engine. This project is based on the development of Spring Cloud microservices, which can be used to build and secondary development digital currency exchanges, and has a complete system component.
- Match trading engine. 
- Background management (back-end + front-end)  
- Frontend (transaction page, event page, personal center, etc.)） 
- Native Android APP source code 
- Native Apple APP source code 
- Currency wallet RPC source code 
- Multi Language Support（Simple Chinese、English）

![PC](https://images.gitee.com/uploads/images/2020/0422/182754_a150e134_2182501.png "QQ截图20200422182717.png")

![APP](https://images.gitee.com/uploads/images/2020/0422/182544_05863aa2_2182501.png "幻灯片1.PNG")

![APP](https://images.gitee.com/uploads/images/2020/0422/182559_860d3c60_2182501.png "幻灯片2.PNG")

## System architecture overview

Just draw a few sketches, just look at it。。。

#### Overall structure

![整体架构](https://images.gitee.com/uploads/images/2020/0407/143836_eac248e5_2182501.png "1.png")

#### Logical architecture

![逻辑架构](https://images.gitee.com/uploads/images/2020/0407/143856_66257325_2182501.png "2.png")

#### Deployment architecture

![部署架构](https://images.gitee.com/uploads/images/2020/0408/141710_07923003_2182501.png "1117.png")

#### Dependencies

![依赖关系](https://images.gitee.com/uploads/images/2020/0407/194510_89803a9d_2182501.png "QQ截图20200407194419.png")

## System demonstration video

PC front end (user web end)：[https://gitee.com/cexchange/CoinExchange/attach_files](https://gitee.com/cexchange/CoinExchange/attach_files)

Mobile APP：[https://gitee.com/cexchange/CoinExchange/attach_files](https://gitee.com/cexchange/CoinExchange/attach_files)

Management background：[https://gitee.com/cexchange/CoinExchange/attach_files](https://gitee.com/cexchange/CoinExchange/attach_files)

## Development Reference

Development Reference Document：[https://gitee.com/cexchange/CoinExchange/blob/master/DEVELOP.md](https://gitee.com/cexchange/CoinExchange/blob/master/DEVELOP.md)

Manage background screenshots：[https://gitee.com/cexchange/CoinExchange/tree/master/09_DOC/管理后台截图](https://gitee.com/cexchange/CoinExchange/tree/master/09_DOC/%E7%AE%A1%E7%90%86%E5%90%8E%E5%8F%B0%E8%BF%90%E8%A1%8C%E6%88%AA%E5%9B%BE)

## About server configuration and deployment

If you want to build an exchange system on your computer or cloud server, I have prepared some basic deployment manuals. Of course, installing software on linux/unix is not a simple matter. You need to have a certain Linux basics and command line skills, but also the courage and patience to solve problems, I wish you success!

- [Server Configuration Reference Manual](./09_DOC/00_服务器部署/推荐服务器配置.md)  
- [Installation Basic Environment Manual](./09_DOC/00_服务器部署/安装基础环境.md)  
- [Service deployment script](./09_DOC/00_服务器部署/install.sh)  
- [Install MySql Manual](./09_DOC/00_服务器部署/安装MySql.md)  
- [Install Redis Manual](./09_DOC/00_服务器部署/安装Redis.md)  
- [Install Zookeeper Manual](./09_DOC/00_服务器部署/安装Zookeeper.md)  
- [Install Kafka Manual](./09_DOC/00_服务器部署/安装Kafka.md)  
- [nstall Mongodb manual](./09_DOC/00_服务器部署/安装Mongodb.md)  
- [Building a BTC wallet node manual](./09_DOC/00_服务器部署/搭建BTC钱包.md)  
- [Building ETH Wallet Node Manual](./09_DOC/00_服务器部署/搭建ETH钱包.md)  
- [Manual of Building USDT Wallet Node](./09_DOC/00_服务器部署/搭建USDT钱包.md)  

## About SpringCloud

Spring Cloud is an ordered collection of frameworks. It uses Spring Boot's development convenience to subtly simplify the development of distributed system infrastructure, such as service discovery registration, configuration center, message bus, load balancing, circuit breaker, data monitoring, etc., can be done using Spring Boot's development style One click to start and deploy. Spring Cloud does not repeat the manufacturing of wheels. It just combines the mature and practical service frameworks developed by various companies. The re-encapsulation through the Spring Boot style shields the complex configuration and implementation principles, and finally gives the development The author has left a set of distributed system development kits that are simple to understand, easy to deploy, and easy to maintain.
In general, a complete Spring Cloud framework should be as shown in the following figure:

![SpringCloud框架图](https://images.gitee.com/uploads/images/2020/0408/133052_3ec984df_2182501.png "图片2.png")

If you are unfamiliar with Spring Cloud, you can simply learn about Spring Cloud related tutorials first, so that you will come back to this project and it will be easier to get started.
As a reminder, because the Springcloud framework diagram is a complete architecture, we will tailor some content appropriately during development to make development and deployment faster, so there are some discrepancies.

## About Matchmaking Trading Engine

The system uses memory matching for the transaction queue, Kafka is used for matching order information transmission, MongoDB persists the order transaction details, and MySQL records the overall order transaction.
Among them, the 01_Framework/Exchange project is mainly responsible for memory matching, and the 01_Framework/Market project is mainly responsible for order transaction persistence, market generation, market push and other services, including:

- K-line data, the intervals are: 1 minute, 5 minutes, 15 minutes, 30 minutes, 1 hour, 1 day, 1 week, 1 month
- Market depth data for all trading pairs
- The latest prices of all trading pairs
- Recently traded pairs

 **Modes supported by memory matching transactions** 

- Matching of limit order and limit order
- Matching market orders and limit orders
- Matching limit orders with market orders
- Matching market orders with market orders

 **Limit & Market Order Processing Logic** 

![限价&市价订单处理逻辑](https://images.gitee.com/uploads/images/2020/0408/144633_457c0552_2182501.png "2222.png")
Note: This picture is a long time ago, the logic in the latest code is more complicated

 **Other features supported by match engine** 

In addition to the ordinary matching functions of limit and market prices, the matching trading engine of this system also introduces an active transaction mode. By setting the trading start time, initial issuance volume, initial issuance price, and activity of trading pairs (such as BTC/USDT) Modes and other parameters can formulate a wealth of matching transaction modes to meet different matching modes.

 **for example** 

The exchange is expected to launch the trading pair AAA/USDT at 12:00:00 on August 8, 2020, but as a newly launched currency, how can it work without activity? The project party or the exchange decided to come up with 10,000 AAA at a price of 0.0001USDT (market price: 0.0005) for everyone to snap up. The system supports the setting of such activities.

In addition, if the project party or the exchange decides to take out 10,000 AAAs to issue at the price of 0.0001USDT, I don’t want everyone to snap up, but hope that all users who recharge USDT can divide 10,000 AAAs on average. This system also supports the setting of this activity .

 **to sum up** 

n short, this system supports a highly customized matching mode. At the same time, you can also develop your own matching transaction mode, just by modifying the matching logic in the Exchange project.

## About the technical composition

- Backend development：Spring、SpringMVC、SpringData、SpringCloud、SpringBoot
- DataBase：Mysql、Mongodb
- Other：redis、kafka、Ali OSS、Tencent Captcha
- Frontend：Vue、iView、less

## Demo website

[https://www.bizzan.com](https://www.bizzan.com)

This was done for the customer, but later the customer stopped operating, so this website was left, because I don’t have server permissions, so this website may not be accessible at any time.

Building a test site requires purchasing several cloud servers, which cost a lot, so I did not set up a test station myself, but the system is complete and has passed the commercial and practical operation test for nearly a year.

> About trading robots   
> Trading robots are automatic trading programs that can automatically trade based on external market conditions, so that the exchange's trading pair prices are consistent with the external, preventing losses caused by some users "moving bricks".   

==============================================

## System operating environment

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

## Recommended configuration for production environment

![生产环境建议配置](https://images.gitee.com/uploads/images/2020/0406/204436_dfff09a6_2182501.png "QQ截图20200406204341.png")

## File directory description

 **00_framework**

└─———admin   Background management API

└─———bitrade-job     Task management（Empty）

└─———chat   OTC Chat

└─———cloud  SpringCloud Eureka

└─———core   Core

└─———exchange   match trading

└─———exchange-api   order API

└─———exchange-core   order core

└─———jar   Other lib

└─———market    market API、K-line service

└─———otc-api   OTC trade API  

└─———otc-core  OTC core

└─———sql    SQL script

└─———ucenter-api    user API

└─———wallet      wallet


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


## Use tutorial

1.  Prepare the mysql database and create a database named "xxxx"  
2.  Prepare redis cache database  
3.  Prepare kafka streaming environment (first configure to run zookper, then configure to run kafka)  
4.  Prepare mongodb database environment, create users admin, xxxx, create bitrade database  
5.  Prepare Alibaba Cloud OSS (modify the place to be configured in the project)  
6.  Prepare nginx and modify the configuration file (optional, need to be configured for official launch)  
7.  Modify the configuration file in the framework code to prepare the environment configuration parameters  
8.  Compile to generate jar executable file  
9.  Run cloud.jar (microservices registration center)  
10. Run exchange.jar (match trading engine)  
11. Run market.jar (Quote Center, need to wait for Exchange.jar to fully start)  
12. Run ucenter.jar (User Center)  
13. Run other modules (wallet.jar, chat.jar, otc-api.jar, etc.)  
14. Open mysql and import the xxxxxxx.sql file in the sql folder in the framework code. Note that if the trigger sql reports an error, you need to add a trigger for the wallet table  
15. Run the front-end vue project  
16. Run the back-end vue project  
17. Run wallet RPC  
18. Run the automated trading robot program (the code in this part is not uploaded, but it does not affect)  
19. Run the Admin project (the service does not depend on other services, so you can just run this project and directly view the background)  

## Technical Support
This digital currency trading system is a project developed by my company for an exchange. The exchange has ceased operations due to team reasons, and our company was disbanded in February. Since I participated in the project, I was responsible for overall R&D management, architecture design and customer docking, so I mastered all the codes.

There are some places that need special attention in the use of the function of this system, such as other operations after the new transaction pair, improper operation will cause data disorder errors.

I can provide paid technical assistance and use training guidance!

Email：877070886@qq.com

## Precautions

When the memory is insufficient, enter top in the Linux console to view the java process occupies a lot of memory (a java process occupies more than 1G), because there are many jar packages to run, so you need to control the memory used by certain jar packages, you can choose A few less resource intensive projects are as follows:


```
java -jar -Xms128m -Xmx128m -Xmn200m -Xss256k  admin-api.jar

java -jar -Xms512m -Xmx512m -Xmn200m -Xss256k  cloud.jar

java -jar -Xms512m -Xmx512m -Xmn200m -Xss256k  wallet.jar
```


## About Mail & SMS

1.  This system supports the operation status of the system for sending emails and short messages  
2.  System notification/alarm support: user registration, user authentication, user recharge/withdrawal, currency RPC operation status, system resource usage monitoring, etc. 24 kinds of monitoring
  

## Questions about database scripts

Some reported that there is no complete SQL file. This is because the successfully compiled Jar will automatically map the Entity to a database structure after the first run. The SQL in the project only completes some database structures that Springcloud cannot complete.
The automatic database configuration is located in the application.properties configuration file:

> #jpa  
> spring.jpa.show-sql=true  
> spring.data.jpa.repositories.enabled=true  
> spring.jpa.hibernate.ddl-auto=update  

spring.jpa.hibernate.ddl-auto=update
This configuration will automatically update the database structure.

## Core function description (user side)

- [x] 1.  Registration/login/real-name authentication/audit (currently only supports mobile phones, secondary development can be added to the mail, very simple)
- [x] 2. Banner/Announcement/Help/Customized page (Banner supports separate settings for PC and APP, helps support various classification modes)  
- [x] 3. Fiat currency C2C transaction/fiat currency OTC transaction (supports two fiat currency models, the platform can undertake C2C fiat currency exchange in the early stage of the project, and OTC transactions can be opened later)  
- [x] 4. Coin trading (support for limit order, market order, and other commission modes can be added for secondary development)  
- [x] 5. Invite registered/promotion partners (support for ranking statistics on the number of invited promoters and commissions by day, week and month)  
- [x] 6. Innovation Lab (there are many supported functions in this part, and itemized explanations. In addition, the APP does not support this function for the time being)  

> - [x] 6-1. First launch panic buying activity mode (for example, when issuing a new trading pair, a certain number of currencies can be set on the trading pair for panic buying)  
> - [x] 6-2. The first distribution mode of activity (for example, before issuing the BTC/USDT trading pair, the official took out 5BTC for the activity, and divided the BTC equally according to how much USDT the user recharged mortgaged)  
> - [x] 6-3. Panic buying mode (for example, before the ZZZ/USDT trading pair is issued, the ZZZ currency price is 5USDT, and the official issuance activity price is 0.5USDT, you can use this mode)  
> - [x] 6-4. Control panel sharing mode (such as 6-3, only average distribution)  
> - [x] 6-5. Mining machine activity mode (support users to mortgage a certain amount of currency, and the official promise to return a certain amount of currency every month)  

- [x] 7. Red envelope function (support platform and official to issue a certain amount of currency red envelope, this function is suitable for user fission)  
- [x] 8. Various basic management such as user asset management, flow management, commission management, real name management  

## Core function description (management side) 

- [x] 1. Summary (view platform operating data, including transaction amount, registered number, recharge, etc.)  
- [x] 2. Member management (member information management, member real name verification, member real name management, member balance management, member recharge/freeze balance, etc.)  
- [x] 3. Invitation management (member invitation information, member invitation ranking management)  
- [x] 4. CTC management (CTC order management, flow management, acceptor management)  
- [x] 5. Content management (PC advertising management, APP advertising management, announcement management, help management)  
- [x] 6. Financial management (charge and withdrawal management, financial flow management, reconciliation management, currency wallet balance management)  
- [x] 7. Currency management (new trading pair, management trading pair, new trading robot, setting trading robot parameters, setting market engine/trading engine, revoking all orders)  
- [x] 8. Event management (new activity, mining machine subscription, panic buying/division management)  
- [x] 9. Red envelope management (platform red envelope management, user red envelope management)  
- [x] 10. System management (role management, department management, user management, authority management, currency management, RPC management, version management)  
- [x] 11. Margin management (this function is considered in design, but not used during actual operation)  
- [x] 12. OTC management (advertising management, order management, OTC currency management, surrender management, etc., this function has not been verified by actual operation)  

## About blockchain wallet RPC

This project provides two wallet docking methods, one is self-built node + blockchain browser, and the other is third-party wallet docking. If you want to use a self-built node or blockchain browser, you can directly compile the code in 00_framework. If you want to use a third-party wallet for docking, you can download the project files of the YouDun wallet in the 07_Uduncloud folder and copy them to 00_framework.

After you get the code, you can not connect the blockchain nodes during the debugging and running of this project, which will not have much impact; even if you do not connect the blockchain nodes, you can deploy one of them with matching transaction function Trading platform (just that users cannot recharge their wallet addresses).

When you are gradually familiar with the entire system, and have a certain basic reserve of blockchain operation principles, node construction, and blockchain browser, you can start to study the projects under the **01_wallet_rpc** folder. Each currency corresponds to different data access methods. Most blockchain projects have the same or very similar wallet operation methods. For example, Bitcoin derivatives such as BTC, LTC, BCH, BSV, BCD, etc., have almost the same API operations. The same; another example is ETH, when you master the operation of a contract currency, the operation of other digital currencies based on ETH is almost the same. So, basically when you spend time understanding one, you understand a bunch of currencies.

The wallet operation scheme used in this project is also different, and as far as possible to show you different usages:
- Self-built full nodes such as BTC and USDT now require almost 300G of hard disk space;
- Like ETH, self-built light nodes are used ([Reference Article](https://www.cnblogs.com/bizzan/p/11341713.html)), because the full node requires too much hard disk space;
- Such as BCH, BSV, etc., use a third-party blockchain browser to obtain data;
- Like XRP, the official has provided an interface to access block data ([Ripple API GitHub address] (https://github.com/ripple/ripple-lib/))

Generally speaking, when the amount of funds exchanged by the exchange is not large, you can explore it yourself, but when the amount of funds exchanged is large, if you are not confident about operating your wallet, you can also use a third-party wallet service Of course, this requires you to negotiate with the wallet service provider to pay an annual fee or something.

The following figure is a brief explanatory diagram of the user recharge monitoring logic, just take a look at it:

![充值逻辑](https://images.gitee.com/uploads/images/2020/0327/162223_5d418523_2182501.png "13981024-76374161aedf70d6.png")


## System display (PC front end)

![首页](https://images.gitee.com/uploads/images/2020/0327/135803_75ec9a0b_2182501.png "01_首页.png")

![币币交易](https://images.gitee.com/uploads/images/2020/0327/135834_4a5fb1c4_2182501.png "02_币币交易.png")

![法币交易](https://images.gitee.com/uploads/images/2020/0327/135902_a7286b9c_2182501.png "03_法币交易CTC.png")

![登录](https://images.gitee.com/uploads/images/2020/0322/193759_edc5dc7b_2182501.png "图片5.png")

![活动/创新实验室](https://images.gitee.com/uploads/images/2020/0327/135930_0c02d004_2182501.png "04_创新实验室.png")

![创新实验室详情](https://images.gitee.com/uploads/images/2020/0327/140037_074a81a4_2182501.png "创新实验室详情.png")

![推广合伙人](https://images.gitee.com/uploads/images/2020/0327/140003_9b962fe7_2182501.png "07_推广合伙人.png")

![公告](https://images.gitee.com/uploads/images/2020/0322/193852_3ad12a6f_2182501.png "图片8.png")

![帮助](https://images.gitee.com/uploads/images/2020/0322/193902_ef09925e_2182501.png "图片9.png")

## System operation display (APP front end)

![首页](https://images.gitee.com/uploads/images/2020/0322/193927_9940ca7c_2182501.jpeg "图片10.jpg")

![行情](https://images.gitee.com/uploads/images/2020/0322/193941_ff5a16a2_2182501.jpeg "图片11.jpg")

![K线](https://images.gitee.com/uploads/images/2020/0322/193951_abf7b5b6_2182501.jpeg "图片12.jpg")

![交易](https://images.gitee.com/uploads/images/2020/0322/194003_d14a772a_2182501.jpeg "图片13.jpg")

![个人中心](https://images.gitee.com/uploads/images/2020/0322/194021_a047d3a5_2182501.jpeg "图片14.jpg")

![个人资产管理](https://images.gitee.com/uploads/images/2020/0322/194059_faeeeb4a_2182501.jpeg "图片15.jpg")

![邀请管理](https://images.gitee.com/uploads/images/2020/0322/194112_7ae11b00_2182501.jpeg "图片16.jpg")

## Some pages on the mobile phone Web

![APP下载](https://images.gitee.com/uploads/images/2020/0325/130921_7d8dee06_2182501.jpeg "12.jpg")

![抢红包](https://images.gitee.com/uploads/images/2020/0325/130936_18809c8f_2182501.jpeg "11.jpg")

![活动/创新实验室](https://images.gitee.com/uploads/images/2020/0325/130948_e36b562d_2182501.jpeg "13.jpg")


## System operation display (back-end)

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

## APP download picture example

![输入图片说明](https://images.gitee.com/uploads/images/2020/0324/182826_f0617759_2182501.png "download1.png")
![输入图片说明](https://images.gitee.com/uploads/images/2020/0324/182834_7a82f8a8_2182501.png "download2.png")
![输入图片说明](https://images.gitee.com/uploads/images/2020/0324/182840_c3d08fb7_2182501.png "download3.png")
![输入图片说明](https://images.gitee.com/uploads/images/2020/0324/182850_8b19fe57_2182501.png "download4.png")
![输入图片说明](https://images.gitee.com/uploads/images/2020/0324/182856_9206a79a_2182501.png "download5.png")

## Attention

Anyone who uses this source code to engage in commercial activities and causes losses to others and himself is not responsible for me!
