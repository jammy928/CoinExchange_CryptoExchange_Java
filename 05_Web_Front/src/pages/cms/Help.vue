<template>
  <div class="help">
    <img class="bannerimg" src="../../assets/images/help_banner.jpg">
    <div class="help_container">
      <h1>{{$t("header.helpcenter")}}</h1>
      <div class="main">
        <div class="section" v-for="section in helpData">
          <h3 v-if="langPram == 'CN'">{{section.titleCN}}</h3>
          <h3 v-if="langPram == 'EN'">{{section.titleEN}}</h3>
          <div class="list-wrap">
            <router-link v-if="langPram == 'CN'" class="item" :title="item.title" v-for="(item, index) in section.content" :to="{path:'helpdetail',query:{cate:section.cate,id:item.id,cateTitle:section.titleCN}}" :key="index">
              <span class="text">{{item.title}}</span>
            </router-link>
            <router-link v-if="langPram == 'EN'" class="item" :title="item.title" v-for="(item, index) in section.content" :to="{path:'helpdetail',query:{cate:section.cate,id:item.id,cateTitle:section.titleEN}}" :key="index">
              <span class="text">{{item.title}}</span>
            </router-link>
          </div>
          <div class="route-wrap">
            <router-link v-if="langPram == 'CN'"  :to="{path:'helplist',query:{cate:section.cate, cateTitle:section.titleCN}}">{{$t("common.more")}}>></router-link>
            <router-link v-if="langPram == 'EN'"  :to="{path:'helplist',query:{cate:section.cate, cateTitle:section.titleEN}}">{{$t("common.more")}}>></router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<style lang="scss" scoped>
.help {
  background: #FFF;
  height: 100%;
  background-size: cover;
  position: relative;
  overflow: hidden;
  padding-bottom: 50px;
  padding-top: 60px;
  color: #fff;
}
.help .bannerimg {
  display: block;
  width: 100%;
}
.help_container {
  padding: 0 12%;
  text-align: center;
  height: 100%;
  min-height: 800px;
  > h1 {
    margin-top: -170px;
    font-size: 32px;
    line-height: 1;
    padding: 50px 0;
  }
}
.help .main {
  margin-top: 80px;
  display: flex;
  justify-content: space-between;
  flex-direction: row;
  flex-wrap: wrap;
  .section {
    width: 40%;
    font-size: 16px;
    text-align: left;
    margin: 0 20px;
    position: relative;
    padding-bottom: 30px;
    margin-bottom: 50px;
    > h3 {
      font-size: 24px;
      line-height: 1;
      padding: 30px 0;
      color:#000;
    }
    .item {
      display: block;
      position: relative;
      padding: 16px 0;
      line-height: 1;
      color: #464646;
      border-bottom: 1px solid #ebebeb;
      .text {
        display: inline-block;
        max-width: calc(100% - 25px);
        white-space: nowrap;
        -o-text-overflow: ellipsis;
        text-overflow: ellipsis;
        overflow: hidden;
      }
    }
    .iconimg {
      display: inline-block;
      width: 14px;
      margin-left: 6px;
    }
  }
  .route-wrap {
    position: absolute;
    bottom: 0;
    right: 0;
    font-size: 14px;
    a {
      color: #f0a70a;
    }
  }
}
</style>

<script>
export default {
  data() {
    return {
      helpData: []
    };
  },
  created: function() {
    this.init();
    this.getData();
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
    init() {
      this.$store.commit("navigate", "nav-uc");
    },
    getData() {
      let param = {};
      param["lang"] = this.langPram;
      this.$http.post(this.host + "/uc/ancillary/more/help", param).then(res => {
        if (res.status == 200 && res.body.code == 0) {
          this.helpData = res.body.data;
        } else {
          this.$Message.error(res.body.message);
        }
      });
    }
  }
};
</script>
