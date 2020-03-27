# 本地开发说明

本文档适合有一定基础的人看，需要具备SpringCloud/SringBoot开发基础、Vue/Npm前端开发基础、Mysql基础、Mongodb基础、Redis基础、Linux基础。一般而言，大部分Java程序员都具备这些能力。

同时，如果需要连接区块链，对区块链钱包进行操作/数据获取，你还需要具备一些区块链相关的基础知识，包括但不限于：搭建比特币/以太坊等节点、区块链节点RPC访问、比特币钱包基础、以太坊钱包基础等。一般而言，你不需要特别精通区块链的运行过程，只需要对区块链的运行原理有一定的了解。

本项目前后端分离，如果你是一个全栈程序员，这个项目应该很快就能调试通。
有什么问题可以添加QQ：877070886，我会给予一定的技术援助（小白问题请绕过）。

#### 关于Framework开发

00_Framework文件夹下的项目是所有服务的集合，通过SpringCloud的微服务开发模式进行开发，你可以通过Eclipse打开整个工程项目，我的开发工具版本如下：

> Eclipse Java EE IDE for Web Developers.

> Version: Photon Release (4.8.0)

> Build id: 20180619-1200

首先，为开发工具安装Lombok插件。

用Eclipse打开项目后，你的开发工程目录应该如下图所示：
![开发界面](https://images.gitee.com/uploads/images/2020/0324/104050_1d27dea9_2182501.png "QQ截图20200324104038.png")

编译项目时，请先编译core以及xxx-core项目，否则会提示缺少类（本项目使用JPA QueryDsl实现表操作）。

每个项目都有独立的服务端口（配置文件中），你可以直接通过（IP：端口）调用服务，也可以通过网关（Cloud项目，7000端口）调用服务，也可以通过配置Nginx反向代理进行服务调用（生产环境使用此种方式）

#### 关于后台管理Web端开发

配置文件在src/config/api.js，你可以在这里配置你的Admin服务IP端口，因为后台管理web只会调用一个Admin服务，所以，你可以选择直接指定到Admin服务IP：端口。

同时，你还需要修改src/service/http.js文件中的访问配置。

启动方式：通过npm run dev即可热启动项目，通过npm run build可编译出部署文件。


#### 关于前台Web端开发

前台Web端（PC）因为要访问不同的服务，像个人信息需要调用Ucenter提供的服务，交易需要调用Exchange-api的服务，所以，你需要通过网关的方式为前端提供服务。
配置文件在：src/main.js，你可通过修改Vue.prototype.rootHost及Vue.prototype.host来实现对后端服务的调用。

#### 关于数据库（MySQL）
数据库文件在00_Framework/sql文件夹下，提供了db_patch，这个文件仅提供了一些基础数据（菜单树、管理员权限等），其他的数据库表会在jar包首次运行的时候自动更新到数据库，配置项在：


> #jpa

> spring.jpa.show-sql=true

> spring.data.jpa.repositories.enabled=true

> spring.jpa.hibernate.ddl-auto=update

如果你不希望数据库表与Java类实体动态更新，你可选择修改配置项：spring.jpa.hibernate.ddl-auto=update

#### 环境配置

系统运行依赖于Mongodb、Redis、MySQL、kafka、阿里云OSS，所以你需要准备好这些基础服务。

#### 常见编译问题（Error）

一、启动ucenter-api时，提示下列错误：
Error creating bean with name 'enableRedisKeyspaceNotificationsInitializer' defined in class path resource
原因：使用了注解：@EnableRedisHttpSession，这个注解是用来开启Redis来集式式管理Session。而在使用这种方式的时候，是需要Redis开启Keyspace Notifications功能的，默认是关闭的。这个功能有一个参数来控制它，notify-keyspace-events，把它的值改为Egx。
腾讯云中的redis参数notify-keyspace-events修改方法：进入实例->参数，修改即可。


2、运行jar包提示Failed to get nested archive for entry BOOT.......
提示缺少spark.xxxx.DB类，这是因为编译时未将spark-core.jar拷贝到压缩包中，这个时候需要将pom.xml文件中修改一下，追加：
				<configuration>
				    <includeSystemScope>true</includeSystemScope>
				</configuration>
追加位置：<artifactId>spring-boot-maven-plugin</artifactId>，与<executions>平级
然后删除target下的jar包，重新生成，最好run maven clean以后再maven install


3、org.apache.shiro.authz.UnauthorizedException: Subject does not have permission [system:announcement:deletes]
这种错误是操作没有权限导致，检查操作的方法与数据库中的是否一致。
第一步，通过浏览器F12查看请求的链接，一般显示404
第二步，检查数据库中admin_permission表中是否有要访问的链接

3、market.jar启动失败，提示connection refuesed之类的错误，首先用netstat -tunlp查看端口6005（也就是exchange）是否有监听，如果没有，则说明exchange未完全启动。
导致exchange启动慢的原因主要是初始化时，对未完成的订单加载慢。初始化时，需要从mangodb加载订单成交详情，这是非常巨大的数据。

#### 一些配置项说明

1、market/application.properties
	# 二级推荐人币币手续费佣金是否发放(true：开放    false：不开放)
	second.referrer.award=false

2、ucenter-api/application.properties
	# system(发送邮件使用)
	spark.system.work-id=1
	spark.system.data-center-id=1
	spark.system.host=
	spark.system.name=
        *      #推荐注册奖励，1=被推荐人必须实名认证才可获得奖励，否则无限制，要与admin模块里面的配置保持统一
	commission.need.real-name=1
	#推荐注册奖励是否开启二级奖励（1=开启，0=关闭）
	commission.promotion.second-level=0
	#个人推广链接的前缀，随着登录接口返回给客户端。客户端那边与推广码连接，组成个人推广链接。如果有推广注册功能必填
	person.promote.prefix=http://www.bizzan.com/#/register?agent=
	#转账接口地址
	transfer.url=
	transfer.key=
	transfer.smac=

#### Nginx配置

参考网址：
https://blog.csdn.net/qq_36628908/article/details/80243713

nginx文档目录：usr/local/nginx/html/、usr/local/nginx/conf/

注意事项：
1、api.xxxx.com 与 www.xxxx.com需要转发不同服务
2、需要api.xxxx.com支持websocket


===========================
依赖环境
yum install -y wget  
yum install -y vim-enhanced  
yum install -y make cmake gcc gcc-c++  
yum install -y pcre pcre-devel
yum install -y zlib zlib-devel
yum install -y openssl openssl-devel

下载nginx-1.12.2.tar.gz
wget http://nginx.org/download/nginx-1.12.2.tar.gz

编译安装 
tar -zxvf nginx-1.12.2.tar.gz 
cd nginx-1.12.2

./configure \
--prefix=/usr/local/nginx \
--pid-path=/var/run/nginx/nginx.pid \
--lock-path=/var/lock/nginx.lock \
--error-log-path=/var/log/nginx/error.log \
--http-log-path=/var/log/nginx/access.log \
--with-http_gzip_static_module \
--http-client-body-temp-path=/var/temp/nginx/client \
--http-proxy-temp-path=/var/temp/nginx/proxy \
--http-fastcgi-temp-path=/var/temp/nginx/fastcgi \
--http-uwsgi-temp-path=/var/temp/nginx/uwsgi \
--with-http_stub_status_module \
--with-http_ssl_module \
--http-scgi-temp-path=/var/temp/nginx/scgi \
--with-stream --with-stream_ssl_module

make 
make install