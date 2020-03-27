<template>
  <div class="notice">
    <Row :gutter="30">
      <Col :xs="24" :sm="24" :md="5" :lg="5" class="notice-list">
        <div class="main">
          <h2>{{$t("cms.noticecenter")}}</h2>
          <div class="list">
            <div :class="item.id==queryId?'active' : 'item1'" v-for="item in FAQList" @click="noticedeail(item.id)">
              <span class="text">{{item.title}}</span>
            </div>
          </div>
          <div class="page">
            <Page :total="totalNum" :pageSize="pageSize" :current="pageNo" @on-change="loadDataPage" size="small"></Page>
          </div>
        </div>
      </Col>
      <Col :xs="24" :sm="24" :md="5" :lg="19">
        <div class="content-wrap">
            <div v-show="hasContent">
              <div class="header">
                <h2>{{data.info.title}}</h2>
                <span>{{data.info.createTime}}</span>
              </div>
              <div class="content">
                <div class="content-text" v-html="data.info.content"></div>
                <div class="show-qrcode" style="text-align: right;padding: 15px 10px;border-radius:10px;background:#FFF;margin-top: 30px;">
                  <qriously :value="qrcode.value" :size="qrcode.size" foreground="#000" />
                  <div style="width:150px;float:right;text-align:center;">{{$t("cms.scanforshare")}}</div>
                </div>
              </div>
              <div class="nav-bottom">
                <div class="left" v-if="data.back">
                  <router-link class="link" :to="'../announcement/'+data.back.id">
                    < {{$t("cms.prevnotice")}} <p style="color:#f0a70a;">{{data.back.title}}</p>
                  </router-link>
                </div>
                <div class="right" v-if="data.next">
                  <router-link class="link" :to="'../announcement/'+data.next.id">{{$t("cms.nextnotice")}} >
                    <p style="color:#f0a70a;">{{data.next.title}}</p>
                  </router-link>
                </div>
              </div>
            </div>
            <div v-show="!hasContent">
                <p style="font-size: 30px;text-align:center;margin-top: 15%;"><Icon type="ios-cafe" /></p>
                <p style="text-align:center;font-size: 12px;margin-top: 10px;color: #828ea1;">{{$t("cms.notexist")}}</p>
            </div>
            <Spin size="large" fix v-if="spinShow"></Spin>
        </div>
      </Col>
    </Row>
    <div class="bottom-list">
      <p style="font-size: 18px;margin: 15px 0;">最新公告</p>
      <div class="notice-item" v-for="item in FAQList" @click="noticedeail(item.id)">
        <span class="text">[{{item.createTime | subTime}}] {{item.title}}</span>
      </div>
    </div>
    <div class="app_bottom">
      <div class="left_logo">
        <img style="float:left;" src="../../assets/images/applogo.png"></img>
        <div style="float:left;height: 40px;line-height:40px;color:#000;">{{$t("cms.downloadslogan")}}</div>
      </div>
      <div class="right_btn_wrap"><router-link to="/app" class="right_btn">{{$t("cms.download")}}</router-link></div>
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
      data: { info: {} },
      qrcode: {
        value: "",
        size: 150
      },
      queryId: "",
      title: "",
      time: "",
      content: "",
      initLang: this.$store.state.lang,
      hasContent: true,
      pageNo: 1,
      pageSize: 10,
      totalNum: 0,
      FAQList: [],
      spinShow: false
    };
  },
  created: function() {
    this.initialize();
    var self = this;
    self.fetchData();
    this.$store.commit("navigate", "nav-service");
    this.loadDataPage(this.pageNo);
  },
  filters: {
    subTime: function(str) {
      return str.substr(5,5);
    }
  },
  computed: {
    lang() {
      if (this.$store.state.lang != this.initLang) {
        this.$router.back();
      }
      return this.$store.state.lang;
    },
    langPram() {
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
    initialize() {

    },
    loadNoticeInfo() {
      let id = this.$route.params.id;

      if (id == null || id == "" || id == 0) {
        if(this.FAQList.length > 0){
          id = this.FAQList[0].id;
        }else{
          id = 0;
        }
      }
      this.qrcode.value = this.rootHost + "/announcement/" + id;
      this.queryId = id;

      this.spinShow = true;
      var param = {
        id: id,
        lang: this.langPram
      };
      this.$http
        .post(this.host + "/uc/announcement/more", param)
        .then(response => {
          var result = response.body;
          if (result.code == 0) {
            const data = result.data;
            this.data = data;
            this.hasContent = true;
            this.spinShow = false;

            window.document.title = (this.lang == "简体中文" ? "公告 - " : "Announcement - ") + this.data.info.title + " - 币严 | 全球比特币交易平台 | 全球数字货币交易平台";
          }else{
            this.hasContent = false;
            this.spinShow = false;
          }
        });
    },
    fetchData() {
      let id = this.$route.params.id;
      if(id == null || id == ""){
        return;
      }
      this.loadNoticeInfo();
    },
    noticedeail(id) {
      var path = { path: "/announcement/" + id};
      this.$router.push(path);
    },
    loadDataPage(pageIndex) {
      var param = {};
      (param["pageNo"] = pageIndex),
        (param["pageSize"] = this.pageSize),
        (param["lang"] = this.langPram),
        this.$http
          .post(this.host + this.api.uc.announcement, param)
          .then(response => {
            // console.log(response);
            var resp = response.body;
            if (resp.code == 0) {
              if (resp.data.content.length == 0) return;
              this.FAQList = resp.data.content;
              this.totalNum = resp.data.totalElements;
              this.loadNoticeInfo();
            } else {
              this.$Notice.error({
                title: this.$t("common.tip"),
                desc: resp.message
              });
            }
          });
    }
  },
  watch: {
    $route: "fetchData"
  }
};
</script>

<style scoped>
.main{
  margin-top: 100px;
  color: #696969;
}
.notice {
  /* background: #fff; */
  padding-bottom: 20px;
  overflow-x: hidden;
  padding: 0px 180px;
  min-height: 800px;
  background: rgba(242,246,250,1) !important;
}
.bottom-list{
  display: none;
  padding: 10px 20px 20px 20px;
  color: #696969;
  background-color: #FFF;
}
.bottom-list .notice-item span{
  display: block;
  margin: 10px 0;
  color: #f0a70a;
  font-size: 14px;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 1;
  overflow: hidden;

}
@media screen and (max-width:768px){
  .notice{
    padding: 0px 0px;position: relative;
  }
  .show-qrcode{
    display: none;
  }
  .bottom-list{
    display: block!important;
  }
  .notice .content-wrap{
    margin-top:45px;
    padding: 30px 20px 50px 20px;
    margin-bottom: 0px;
  }
  .notice-list{
    display: none;
  }
  .notice .content-wrap .header h2{
    text-align: left;
  }
  .notice .content-wrap .content{
    padding-bottom: 60px;
  }
  .notice .nav-bottom{
    padding: 30px 0px 30px 0px;
  }
  .notice .nav-bottom .link > p{
    display: none;
  }
  .show-qrcode{
    display: none;
  }
  .app_bottom{
    display: block!important;
  }

}
.content-wrap {
  width: 1200px;
  margin: 0 auto;

  /* background: #fff; */
}
.header {
  text-align: right;
  margin-bottom: 10px;
  padding: 10px 0;
  border-bottom: 1px solid #ebebeb;
}
.header h2 {
  text-align: center;
  margin-bottom: 10px;
  font-weight: normal;
  color: #696969;
}
.header span {
  padding: 0 10px;
  color: #999;
}
.ivu-spin-fix{
      background-color: rgba(255, 255, 255, 0.82)!important;
}
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
<style lang="scss">

.notice {
  .main{
    h2{
      text-align:left;
      font-size: 18px;
      font-weight:normal;
    }
    .list{
      margin-top: 20px;
      .item1{
        width: 100%;
        text-align:left;
        padding: 15px 15px;
        color: rgba(130,142,161,1);
        &:hover{
          cursor: pointer;
          color: #f0a70a;
        }
      }
      .active{
        background: #FFF;
        color: #f0a70a;
        width: 100%;
        text-align:left;
        padding: 15px 15px;
        border-radius: 3px;
        &:hover{
          cursor: pointer;
          color: #df9800;
        }
      }
    }
    .page{
      text-align:right;
      margin-top: 10px;
      .ivu-page{
        .ivu-page-prev, .ivu-page-next{
          background: transparent!important;
          color: #000;
          border: none;
        }
        .ivu-page-item{
          background-color: transparent!important;
          color: #000;
          border: none;
        }
      }
    }
  }
  .content-wrap {
    text-align:left;
    position: relative;
    width: 100%;
    min-height: 562px;
    background-color: #FFF;
    color: #000;
    // box-shadow: 0 0 2px #ccc;
    margin-top: 100px;
    border-radius: 3px;
    padding: 50px 50px;
    margin-bottom: 50px;
    padding-bottom: 110px;
    .link {
      font-size: 14px;
      color: #f0a70a;
    }
    .header {
      margin-bottom: 40px;
      padding-bottom: 10px;
      h2 {
        font-size: 20px;
      }
    }
    .content {
      color: #74777a;
      margin-bottom: 80px;
      letter-spacing: 1px;
      line-height: 25px;
      .content-text{
          p{
              text-align: justify;
              &::after {
                  width: 100%;
                  content: '';
                  height: 0;
              }
          }
      }
      span {
        .MsoNormal {
          font-weight: 100;
          line-height: 30px;
        }
        .p1 {
          .s1 {
            b {
              font-weight: 400;
              span {
                font-size: 18px !important;
              }
            }
          }
        }
        .p2 {
          span {
            font-size: 12pt !important;
            line-height: 30px;
          }
        }
        .p3 {
          span {
            font-size: 12pt !important;
            color: #fff !important;
            line-height: 30px;
          }
        }
      }
      .image-desc {
        .uploaded-img {
          width: 100%;
        }
        .image-caption {
          display: none !important;
        }
      }
    }
  }
  .nav-bottom {
    position: absolute;
    left: 0;
    bottom: 0;
    width: 90%;
    margin-left:5%;
    padding: 50px 0px 50px 0px;

    border-top: 1px solid #ebebeb;
    border-bottom: 1px solid #ebebeb;
    .link > p {
      color: #fff;
      line-height: 1;
      margin-top: 10px;
    }
    .left {
      text-align: left;
      float:left;
    }
    .right {
      text-align: right;
      float:right;
    }
  }
}
</style>
