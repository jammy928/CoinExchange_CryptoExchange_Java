<template>
  <div class="helpdetail">
    <div class="route-wrap">
      <router-link to="help">{{$t("header.helpcenter")}}</router-link>
      <span>></span>
      <router-link :to="{path:'helplist',query:{cate:cate,cateTitle:cateTitle}}">{{cateTitle}}</router-link>
    </div>
    <div class="main">
      <div class="menu">
        <p style="color: #74777a;">{{$t("cms.otherhelp")}}</p>
        <div class="titles">
          <div class="route" v-for="item in topList" @click="clickTitle(item.id)" :class="{active:item.id==article.id}">
            <span>{{item.title}}</span>
          </div>
        </div>
        <router-link :to="{path:'helplist',query:{cate:cate, cateTitle:cateTitle}}">{{$t("common.more")}}>></router-link>
      </div>
      <div class="content-wrap" >
        <h3 class="title">{{article.title}}</h3>
        <p class="time">{{article.createTime}}</p>
        <div class="content">
          <div v-html="article.content"></div>
          <div class="show-qrcode" style="text-align: right;padding: 15px 10px;border-radius:10px;background:#FFF;margin-top: 30px;">
            <qriously :value="qrcode.value" :size="qrcode.size" foreground="#000" />
            <div style="width:150px;float:right;text-align:center;">{{$t("cms.scanforshare")}}</div>
          </div>
        </div>
      </div>
    </div>
    <div class="app_bottom">
      <div class="left_logo">
        <img style="float:left;" src="../../assets/images/applogo.png"></img>
        <div style="float:left;height: 40px;line-height:40px;color:#000;">{{$t("cms.downloadslogan")}}</div>
      </div>
      <div class="right_btn_wrap"><router-link target="_blank" to="/app" class="right_btn">{{$t("cms.download")}}</router-link></div>
    </div>
  </div>
</template>
<script>
import Vue from "vue";
import VueQriously from "vue-qriously";
Vue.use(VueQriously);

export default {
  components: {
    VueQriously
  },
  data() {
    return {
      topList: [], //置顶文章列表
      article: {}, //文章详情
      qrcode: {
        value: "",
        size: 150
      }
    };
  },
  created() {
    const { cate, id, cateTitle } = this.$route.query;
    this.cate = cate;
    this.id = id;
    this.cateTitle = cateTitle;
    this.init();
    this.$store.commit("navigate", "nav-uc");

    this.qrcode.value = this.rootHost + "/helpdetail?id=" + id;
  },
  computed: {
    lang() {
      return this.$store.state.lang;
    },
    langPram(){
      if(this.$store.state.lang == "简体中文"){
        return "CN";
      }
      if(this.$store.state.lang == "English"){
        return "EN";
      }
      return "CN";
    }
  },
  methods: {
    clickTitle(id) {
      this.getData(id);
    },
    getTop() {
      return this.$http.post(this.host + "/uc/ancillary/more/help/page/top", {
        cate: this.cate,
        lang: this.langPram
      });
    },
    getArticle() {
      return this.$http.post(this.host + "/uc/ancillary/more/help/detail", {
        id: this.id
      });
    },
    init() {
      Promise.all([this.getTop(), this.getArticle()]).then(arr => {
        if (
          arr[0].status == 200 &&
          arr[0].body.code == 0 &&
          arr[1].status == 200 &&
          arr[1].body.code == 0
        ) {
          let returnTop = arr[0].body.data,
            returnArticle = arr[1].body.data,
            hsaInTop = false;
          returnTop.forEach(v => {
            if (v.id == returnArticle.id) {
              hsaInTop = true;
            }
          });
          hsaInTop ? "" : returnTop.unshift(returnArticle);
          this.topList = returnTop;
          this.article = returnArticle;
          window.document.title = (this.lang == "简体中文" ? "帮助 - " : "Help - ") + this.article.title + " - 币严 | 全球比特币交易平台 | 全球数字货币交易平台";
        } else {
          this.$message.error("网络错误");
        }
      });
    },
    getTopList() {
      this.$http
        .post(this.host + "/uc/ancillary/more/help/page/top", {
          cate: this.cate,
          lang: this.langPram
        })
        .then(res => {
          if (res.status == 200 && res.body.code == 0) {
            this.topList = res.body.data;
          } else {
            this.$Message.error(res.body.message);
          }
        });
    },
    getData(id) {
      this.$http
        .post(this.host + "/uc/ancillary/more/help/detail", {
          id
        })
        .then(res => {
          if (res.status == 200 && res.body.code == 0) {
            this.article = res.body.data;
          } else {
            this.$Message.error(res.body.message);
          }
        });
    }
  }
};
</script>
<style lang="scss" scoped>
@media screen and (max-width:768px){
  .main .content-wrap{
    margin-left: 0!important;
    padding: 50px 20px!important;
  }
  .main .menu{
    display: none;
  }
  .helpdetail{
    padding: 50px 0px 0px 0px!important;
  }
  .route-wrap{
    display: none;
  }
  .main .content-wrap .title{
    font-size:20px!important;
    line-height: 36px!important;
    font-weight:normal;
  }
  .main .content-wrap .content{
    padding: 20px 0px 30px 0px!important;
  }
  .show-qrcode{
    display: none;
  }
  .app_bottom{
    display: block!important;
  }
  .footer_content{
    padding: 80px 2% 90px 5%;
  }
}
.helpdetail {
  background: #f2f6fa!important;
  color: #fff;
  width: 100%;
  margin: 0 auto;
  padding: 80px 10%;
  font-size: 14px;
}
.main {
  display: flex;
  .menu {
    flex: 0 0 200px;
    width: 200px;
    > p {
      line-height: 1;
      font-weight: 500;
      margin-bottom: 10px;
    }
    .titles {
      margin-bottom: 20px;
    }
    > a {
      padding: 10px;
      color: #f1ab15;
    }
    .route {
      display: flex;
      align-items: center;
      font-size: 14px;
      padding: 10px;
      border-radius: 6px;
      line-height: 1.5;
      color: #fff;
      cursor: pointer;
      > span {
        overflow: hidden;
        text-overflow: ellipsis;
        display: -webkit-box;
        -webkit-box-orient: vertical;
        -webkit-line-clamp: 2;
        color: #828ea1;
      }
      &.active {
        background-color: #f1ab15;
        color: #fff;
        > span{
          color: #fff;
        }
      }
    }
  }
  .content-wrap {
    flex: 1 1 100%;
    width: 100%;
    margin-left: 35px;
    background: #FFF;
    padding: 50px 50px;
    min-height:800px;
    line-height: 25px;
    .title {
      font-size: 28px;
      line-height: 1;
      margin-bottom: 30px;
      color: #696969;
    }
    .time {
      color: #999;
      line-height: 1;
      margin-bottom: 20px;
      display: none;
    }
    .content {
      border-top: 1px solid #ebebeb;
      padding: 40px 0 10px 0;
      color:#74777a;
      letter-spacing: 1px;
      font-size: 14px;
    }
  }
}

.route-wrap {
  font-size: 14px;
  color: #f1ab15;
  margin-bottom: 40px;
  a {
    color: #f1ab15;
  }
}
</style>
<style lang="scss">
.main .content-wrap .content {
  .uploaded-img {
    width: 100% !important;
  }
}
</style>
<style>
.app_bottom{
  display: none;
  z-index: 999;
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 40px;
  background: rgba(242,246,250,1) !important;
}
.app_bottom .left_logo img{
  height: 30px;margin-top:5px;border-radius: 5px;margin-left: 5px;margin-right: 5px;
}
.app_bottom .right_btn_wrap{
  float: right;height: 40px;line-height: 40px;margin-right: 5px;
}
.app_bottom .right_btn{
  color: #FFF;
  border-radius: 3px;
  padding: 0px 10px;
  line-height: 26px;
  height: 26px;
  display: block;
  margin-top: 7px;
  background: linear-gradient(200deg, #ff9900, #ffbe2b, #ffa500);
}
</style>



