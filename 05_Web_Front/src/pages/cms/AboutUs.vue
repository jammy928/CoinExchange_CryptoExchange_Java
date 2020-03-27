<style lang="scss" scoped>
.about_us_container {
  // background-color: #eee;
  padding: 60px 0 20px 0;
  .wrapper {
    margin: 30px 12%;
    background-color: #192330;
    padding: 20px 40px;
    h2 {
      font-weight: 400;
      height: 50px;
      font-size: 20px;
      border-bottom: 1px solid #27313e;
      line-height: 50px;
    }
    .wrapper_logo ul {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 20px 30px;
      border-bottom: 1px solid #27313e;
      height:280px;
      li {
        width: 25%;
        padding: 0 20px;
        list-style-type: none;
        border-right: 1px solid #27313e;
        text-align: center;
        min-height: 240px;
        img{
          width: 100px;
        }
        h5 {
          line-height: 50px;
          font-size: 20px;
          font-weight:400;
        }
        span {
          color: #828ea1;
          font-size: 12px;
        }
      }
    }
    .content_wrapper {
      padding: 20px 0;
      .content1 {
        border-bottom: 1px solid #27313e;
        padding-bottom: 20px;
        margin-bottom: 20px;
        h5 {
          font-size: 18px;
          line-height: 40px;
          font-weight: 500;
          margin-bottom: 15px;
        }
        p {
          font-size: 14px;
          line-height: 30px;
          color: #828ea1;
          margin-bottom: 15px;
          text-indent: 30px;
          // margin-bottom: 14px;
          text-align: justify;
        }
      }
    }
  }
}
</style>

<template>
  <div class="about_us_container">
    <div class="wrapper">
      <h2 class="title">{{$t('cms.features')}}</h2>
      <div class="wrapper_logo">
        <ul>
          <li>
            <img src="../../assets/images/feature_safe.png" alt="">
            <h5>{{$t('description.title1')}}</h5>
            <span>{{$t('description.message1')}}</span>
          </li>
          <li>
            <img src="../../assets/images/feature_fast.png" alt="">
            <h5>{{$t('description.title2')}}</h5>
            <span>{{$t('description.message2')}}</span>
          </li>
          <li>
            <img src="../../assets/images/feature_global.png" alt="">
            <h5>{{$t('description.title3')}}</h5>
            <span>{{$t('description.message3')}}</span>
          </li>
          <li style="border:none;">
            <img src="../../assets/images/feature_choose.png" alt="">
            <h5>{{$t('description.title4')}}</h5>
            <span>{{$t('description.message4')}}</span>
          </li>
        </ul>
      </div>
      <div class="content_wrapper">
        <div class="content1">
          <h5>{{$t('cms.aboutus')}}</h5>
          <p>{{$t('description.aboutdesc1')}}</p>
          <p>{{$t('description.aboutdesc2')}} </p>
          <p>{{$t('description.aboutdesc3')}}</p>
          <p>{{$t('description.aboutdesc4')}}</p>
          <p>{{$t('description.aboutdesc5')}}</p>
        </div>
        <div class="content1">
          <h5>{{$t('cms.contactus')}}</h5>
          <p>{{$t('description.service')}}：service@bizzan.com</p>
          <p>{{$t('description.support')}}：support@bizzan.com</p>
          <p>{{$t('description.apply')}}：apply@bizzan.com</p>
          <p>{{$t('description.report')}}：ceo@bizzan.com</p>
          <p>{{$t('description.coop')}}：coop@bizzan.com</p>
        </div>
        <div class="content1" style="border:none;">
          <h5>{{$t('description.community')}}</h5>
          <p>{{$t('description.wechat')}}：{{$t('description.addwechat')}}</p>
          <p>{{$t('description.weibo')}}：https://weibo.com/bizzan</p>
          <p>{{$t('description.twitter')}}：https://twitter.com/BIZZANGlobal</p>
          <p>{{$t('description.biyong')}}：https://0.plus/bizzanofficial</p>
          <p>{{$t('description.telegram')}}：https://t.me/bizzanofficial</p>
          <p>{{$t('description.medium')}}：https://medium.com/@BIZZAN</p>
          <p>{{$t('description.reddit')}}：https://www.reddit.com/u/bizzanglobal</p>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  components: {},
  created() {
    this.init();
  },
  computed: {
    lang() {
      return this.$store.state.lang;
    }
  },
  methods: {
    init() {
      this.$store.commit("navigate", "nav-about");
    }
  }
};
</script>
<style scoped>
.container {
  background: #fff;
  overflow-x: hidden;
}

.content-wrap {
  padding-top: 20px;
  position: relative;
  width: 1200px;
  margin: 0 auto;
  background: #fff;
  min-height: 600px;
}

.leftmenu {
  border: 1px solid #efefef;
}

.leftmenu .divider {
  font-size: 20px;
  padding: 5px;
  background: #efefef;
}

.leftmenu li {
  font-size: 14px;
  line-height: 30px;
  padding: 5px;
}

.leftmenu li a {
  color: #444;
}

.leftmenu li a:hover {
  color: #f0a70a;
}

.leftmenu li.cur a {
  color: #f0a70a;
}
.content {
  padding-top: 20px;
  /*padding-left: 200px;*/
  padding-bottom: 20px;
  text-align: left;
  padding-left: 20px;
}

.content .ivu-col {
  text-align: left;
}

.content p {
  text-indent: 2em;
  margin-bottom: 10px;
  font-size: 16px;
}
.content > div {
  padding-bottom: 20px;
}
.content h3 {
  font-size: 22px;
  font-weight: normal;
  margin-bottom: 20px;
}
.content h5 {
  font-size: 14px;
}
.WordSection1 span {
  color: black;
}
</style>
