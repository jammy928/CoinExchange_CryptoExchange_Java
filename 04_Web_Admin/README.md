#### 项目介绍
BIZZAN后台管理系统

#### 软件架构
vue+iview框架

#### Install Prerequisites
The following dependencies are required to run an instance:

1. NodeJS - 9.11.2
2. Npm - 5.6.0
#### 安装教程

1. npm install 安装包以及依赖
2. npm run dev 本地开发环境启动 【】
3. npm run build 代码打包发布

#### 参与贡献

1. Fork 本项目
2. 新建 Feat_xxx 分支
3. 提交代码
4. 新建 Pull Request

#### 项目解构简介
     -build 配置文件
        -config.js 对外暴露是开发环境[dev]还是打包后[pro]
        -env.js 环境切换
        -webpack.base.config.js -webpack基本配置
        -webpack.dev.config.js  -webpack-merge 合并基础配置和[npm run dev]开发环境
        -webpack.prod.config.js -webpack-merge 合并基础配置和[npm run build]打包需要的配置
    -src 主目录
        -caculate
            caculate.js 过滤搜索条件
        -config
            -- api.js 接口整理
            -- storage.js 缓存处理
        -images 图片
        -libs 插件
        -locale 多语言配置文件，暂不支持
        -router 前端路由配置
        -service 封装axios接口请求，以及页面请求的接口处理
        -smeditor 富文本编辑器
        -store Vuex store
        -styles 公用样式
        -template 线上index.html模版 ejs
        -vendors 开发环境需要的js引入
        -views 页面目录
        app.vue 页面初始化
        main.js 入口文件

    -index.html
    -package.json -项目配置文件
