<template>
  <div>
    <div id="fullpage">
      <div id="pagetips" style="border-bottom:1px solid rgb(28, 39, 58);">
        <div class="topnav">
          <div class="carl">
            <div class="notice-list">
              <div class="notice-item" v-for="(item,index) in FAQList">
                <div class="cal_content">
                  <span></span>
                  <router-link target="_blank" :to="{path: '/announcement/' + item.id}">[ {{item.createTime}} ] {{strde(item.title)}}</router-link>
                </div>
              </div>
            </div>
            <div class="more">
              <router-link to="/announcement/0" target="_blank">{{$t('common.more')}}</router-link>
            </div>
          </div>
        </div>
      </div>
      <div class="section" id="page1">
        <!-- <div v-if="lang==='简体中文'"> -->

        <div class="spin-wrap banner-panel">
          <img style="height: 100%;" src="../../assets/images/bannerbg.png"></img>
          <p style="text-align:center;font-size:40px;color:#fff;position:absolute;top: 70px;width:100%;letter-spacing:5px;text-shadow: 0px 0px 10px #000000;">{{$t("common.slogan")}}</p>
          <p style="text-align:center;font-size:20px;color:#828ea1;position:absolute;top: 130px;width:100%;letter-spacing:2px;">{{$t("common.subslogan")}}</p>
          <div class="activity-list" v-show="picShow">
            <div class="swiper-container" id="swiper_container">
              <div class="swiper-wrapper">
                <div class="swiper-slide" v-for="(item,index) in picList">
                  <a v-if="item.linkUrl&&item.linkUrl!=' '&&item.linkUrl!='1'" :href="item.linkUrl" target="_blank">
                    <div class="activity-item">
                      <img :src="item.url"></img>
                    </div>
                  </a>
                  <div v-else class="activity-item">
                    <img :src="item.url"></img>
                  </div>
                </div>
              </div>
              <div class="swiper-pagination"></div>
            </div>
          </div>
        </div>
      </div>
      <div id="pagetips" style="background: #172636;">
        <div class="agent-panel">
          <div class="title">
            <div class="gettingstart">{{$t('sectionPage.gettingstart')}}</div>
            <div class="tips">{{$t('sectionPage.officialstart')}}</div>
          </div>
          <div class="agent-list">
            <div class="agent-item">
              <div class="agent-img">
                <img v-if="langPram=='EN'" src="../../assets/images/new_1usd.png"></img>
                <img v-else="langPram=='CN'" src="../../assets/images/new_1cny.png"></img>
              </div>
              <router-link to="/helpdetail?cate=0&id=20&cateTitle=新手指南" target="_blank">
                <div class="agent-detail">
                  <p  class="agent-name">{{$t('sectionPage.oneminutebuy')}}</p>
                  <p  class="agent-count">{{$t('sectionPage.oneminutebuytips')}}</p>
                </div>
              </router-link>
            </div>
            <div class="agent-item">
              <div class="agent-img">
                <img src="../../assets/images/new_3.png"></img>
              </div>
              <router-link to="/helplist?cate=2&cateTitle=交易指南" target="_blank">
                <div class="agent-detail">
                  <p  class="agent-name">{{$t('sectionPage.baseexchange')}}</p>
                  <p  class="agent-count">{{$t('sectionPage.baseexchangetips')}}</p>
                </div>
              </router-link>
            </div>
            <div class="agent-item">
              <div class="agent-img">
                <img src="../../assets/images/new_2.png"></img>
              </div>
              <router-link to="/helplist?cate=6&cateTitle=基础入门" target="_blank">
                <div class="agent-detail">
                  <p  class="agent-name">{{$t('sectionPage.baseknow')}}</p>
                  <p  class="agent-count">{{$t('sectionPage.baseknowtips')}}</p>
                </div>
              </router-link>
            </div>
            <div class="agent-item">
              <div class="agent-img">
                <img src="../../assets/images/new_4.png"></img>
              </div>
              <router-link to="/helpdetail?cate=0&id=28&cateTitle=新手指南" target="_blank">
                <div class="agent-detail">
                  <p  class="agent-name">{{$t('sectionPage.usersocial')}}</p>
                  <p  class="agent-count">{{$t('sectionPage.usersocialtips')}}</p>
                </div>
              </router-link>
            </div>
          </div>
        </div>
      </div>
      <div style="width: 100%;background: #141e2c;display:none;">
        <router-link to="/announcement/118930" target="_blank" style="width: 100%;">
          <img src="https://bizzan.oss-cn-hangzhou.aliyuncs.com/bannerimg.png" style="width: 72%;margin-left: 14%;margin-top: 20px;">
        </router-link>
      </div>
      <div class="section" id="page2">
        <div class="page2nav">
          <div class="board-title" style="display:inline-block;display: none;">{{$t('sectionPage.mainboard')}}  &nbsp; >>></div>
          <ul class="brclearfix">
            <li v-show="!(index==0&&!isLogin)" v-for="(item,index) in indexBtn" @click="addClass(index)" :class="{'active':index==choseBtn,'ivu-btn-default':index!=choseBtn}" :key="index">{{item.text}}</li>
            <li style="float:right;padding-right: 6px;"><Input search :placeholder="$t('common.searchplaceholder')" @on-change="seachInputChange" v-model="searchKey"/></li>
          </ul>
        </div>
        <div class="ptjy">
          <Table v-if="choseBtn==0" :columns="favorColumns" :data="dataIndex" class="tables" :disabled-hover="true" :loading="loading"  :no-data-text="$t('common.nodata')"></Table>
          <Table v-if="choseBtn!=0" :columns="coins.columns" :data="dataIndex" class="tables" :disabled-hover="true" :loading="loading"  :no-data-text="$t('common.nodata')"></Table>
<!--
          <p v-if="choseBtn!=0" style="height:50px;line-height:50px;padding-left:10px;border-bottom:1px solid #26303d;font-size:14px;color:rgb(97, 119, 146);">创新版</p>
          <Table  v-if="choseBtn!=0" :columns="coins.columns" :data="dataIndex2" class="tables" :disabled-hover="true" :loading="loading"  :no-data-text="$t('common.nodata')"></Table>
-->
        </div>
      </div>
      <div class="section bg-light" id="page6">
        <p class="title">{{$t('sectionPage.brandTitle')}}</p>
        <p class="subtitle">{{$t('sectionPage.brandDetail')}}</p>
        <div class="detail">{{$t('sectionPage.brandDesc1')}}</div>
        <div class="detail">{{$t('sectionPage.brandDesc2')}}</div>
      </div>
      <div class="section" id="page4">
        <ul>
          <li>
            <div><img src="../../assets/images/feature_safe.png" alt=""></div>
            <p class="title">{{$t('description.title1')}}</p>
            <p>{{$t('description.message1')}}</p>
          </li>
          <li>
            <div><img src="../../assets/images/feature_fast.png" alt=""></div>
            <p class="title">{{$t('description.title2')}}</p>
            <p>{{$t('description.message2')}}</p>
          </li>
          <li>
            <div><img src="../../assets/images/feature_global.png" alt=""></div>
            <p class="title">{{$t('description.title3')}}</p>
            <p>{{$t('description.message3')}}</p>
          </li>
          <li>
            <div><img src="../../assets/images/feature_choose.png" alt=""></div>
            <p class="title">{{$t('description.title4')}}</p>
            <p>{{$t('description.message4')}}</p>
          </li>
        </ul>
      </div>

      <div class="section" id="page5">
        <div class="phone_image"></div>
        <ul class="download">
          <li class="qrcode">{{$t('description.scanqrcode')}}</li>
          <li class="wrapper">
            <div class="download_app">
              <img src="../../assets/images/appdownload.png">
            </div>
            <div class="abstract">
              <div class="content">
                <span></span>
              </div>
            </div>
          </li>
        </ul>
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
var Stomp = require("stompjs");
var SockJS = require("sockjs-client");
var moment = require("moment");
import SvgLine from "@components/exchange/SvgLine.vue";
import $ from "@js/jquery.min.js";

import Swiper from 'swiper';

export default {
  components: { SvgLine },
  data() {
    let self = this;
    return {
      loading: false,
      percent: 0,
      pageNo: 1,
      pageSize: 6,
      totalNum: 0,
      FAQList: [],
      yesDayCashDividensBonusETH: 0,
      CNYRate: null,
      dataIndex: [],
      pageNo: 1,
      totalNum: 0,
      searchKey: "",
      favorColumns: [
        {
          title: self.$t("service.favor"),
          align: "center",
          key: "collection",
          width: 60,
          render: (h, params) => {
            let flag = this.isLogin;
            return h("Icon", {
              props: {
                color: "#f0a70a",
                size: "18",
                type: params.row.isFavor
                  ? "ios-star"
                  : "ios-star-outline"
              },
              nativeOn: {
                click: () => {
                  if (this.isLogin) {
                    event.stopPropagation(); //阻止事件冒泡
                    if (
                      event.currentTarget.className ==
                      "ivu-icon ivu-icon-ios-star"
                    ) {
                      // 解除收藏
                      this.cancelCollect(params.index, params.row);
                      event.currentTarget.className ==
                        "ivu-icon ivu-icon-ios-star-outline";
                    } else {
                      // 收藏
                      this.collect(params.index, params.row);
                      event.currentTarget.className =
                        "ivu-icon ivu-icon-ios-star";
                    }
                  } else {
                    this.$Message.warning("请先登录");
                  }
                }
              }
            });
          }
        },
        {
          title: self.$t("service.COIN"),
          align: "center",
          width: 70,
          key: "symbol"
        },
        {
          title: self.$t("service.NewPrice"),
          align: "center",
          key: "price",
          minWidth:180,
          sortable: true,
          sortMethod: function(a, b, type) {
            let a1 = parseFloat(a);
            let b1 = parseFloat(b);
            if (type == "asc") {
              return a1 - b1;
            } else {
              return b1 - a1;
            }
          },
          render: function(h, params) {
            // var rmb = self.round(self.mul(params.row.price, 6.5), 2);
            // if (self.CNYRate != null)
            //   rmb = self.round(self.mul(params.row.price, self.CNYRate), 2);
            let CNYRate = self.CNYRate || 6.5,
              rmb = self.round(self.mul(params.row.usdRate, CNYRate), 2);
            const isgreen =
              parseFloat(params.row.rose) < 0 ? "none" : "inline-block";
            const nogreen =
              parseFloat(params.row.rose) < 0 ? "inline-block" : "none";
            return h("div", {
                  attrs: {
                    class: "price-td"
                  }
            },[
              h("span", {}, params.row.price),
              h("span", {
                  attrs: {
                    class: "price-rmb"
                  }
                }, "  ≈ ￥" + rmb),
              h(
                "Icon",
                {
                  props: {
                    type: "arrow-up-c"
                  },
                  style: {
                    display: isgreen,
                    fontSize: "16px",
                    marginLeft: "5px",
                    verticalAlign: "middle"
                  },
                  class: {
                    green: true
                  }
                },
                "↑"
              ),
              h(
                "Icon",
                {
                  props: {
                    type: "arrow-down-c"
                  },
                  style: {
                    display: nogreen,
                    fontSize: "16px",
                    marginLeft: "5px",
                    verticalAlign: "middle"
                  },
                  class: {
                    red: true
                  }
                },
                "↓"
              )
            ]);
          }
        },
        {
          title: self.$t("service.Change"),
          align: "center",
          key: "rose",
          minWidth:50,
          sortable: true,
          sortMethod: function(a, b, type) {
            let a1 = a.replace(/[^\d|.|-]/g, "") - 0;
            let b1 = b.replace(/[^\d|.|-]/g, "") - 0;
            if (type == "asc") {
              return a1 - b1;
            } else {
              return b1 - a1;
            }
          },
          render: (h, params) => {
            const row = params.row;
            const className = parseFloat(row.rose) < 0 ? "red" : "green";
            return h(
              "span",
              {
                attrs: {
                  class: className
                }
              },
              row.rose
            );
          }
        },
        {
          title: self.$t("service.high"),
          align: "center",
          key: "high",
          render: (h, params) => {
            return h("div", {}, params.row.high);
          }
        },
        {
          title: self.$t("service.low"),
          align: "center",
          key: "high",
          render: (h, params) => {
            return h("div", {}, params.row.low);
          }
        },
        {
          title: self.$t("service.ExchangeNum"),
          align: "center",
          key: "volume",
          // width: 110,
          sortable: true,
          sortMethod: function(a, b, type) {
            let a1 = parseFloat(a);
            let b1 = parseFloat(b);
            if (type == "asc") {
              return a1 - b1;
            } else {
              return b1 - a1;
            }
          }
        },
        // {
        //   title: self.$t("service.OpenPrice"),
        //   align: "center",
        //   key: "open",
        //   width: 150,
        //   sortable: true,
        //   sortMethod: function(a, b, type) {
        //     let a1 = parseFloat(a);
        //     let b1 = parseFloat(b);
        //     if (type == "asc") {
        //       return a1 - b1;
        //     } else {
        //       return b1 - a1;
        //     }
        //   }
        // },

        {
          title: self.$t("service.PriceTrend"),
          align: "center",
          render: function(h, params) {
            let valus = null;
            let len = params.row.trend.length;
            valus =
              len > 0
                ? params.row.trend
                : [
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0
                  ];
            return h(SvgLine, {
              props: {
                values: valus,
                rose: params.row.rose,
                width: 100,
                height: 25
              }
            });
          }
        },
        {
            title: self.$t("service.Operate"),
            align: "center",
            key: "buyBtn",
            width: 100,
            render: function(h, params) {
              return h("div", [
                h("span", {
                  style: {
                    cursor: "pointer",
                    color: "#f0a70a",
                    display: "inline-block",
                    padding: "2px 8px"
                  },
                  on: {
                    click: function() {
                      self.$router.push({
                        name: 'ExchangePair',
                        params: {
                          pair: params.row.href
                        }
                      });
                    }
                  }
                }, self.$t("service.trading"))
              ]);
            }
          }
      ],
      //当前市场上的交易币种，按交易对分
      coins: {
        _map: [],
        USDT: [],
        USDT2: [],
        BTC: [],
        BTC2: [],
        ETH: [],
        ETH2: [],
        favor: [],
        columns: [
          {
            title: self.$t("service.favor"),
            align: "center",
            key: "collection",
            width: 60,
            // renderHeader: (h, params) => {
            // return h("Icon", {
            //   props: {
            //     color: "#f0a70a",
            //     size: "18",
            //     type: "android-star-outline"
            //   }
            // });
            // },
            render: (h, params) => {
              let flag = this.isLogin;
              return h("Icon", {
                props: {
                  color: "#f0a70a",
                  size: "18",
                  type: params.row.isFavor
                    ? "ios-star"
                    : "ios-star-outline"
                },
                nativeOn: {
                  click: (event) => {
                    if (this.isLogin) {
                      event.stopPropagation(); //阻止事件冒泡
                      if (
                        event.currentTarget.className ==
                        "ivu-icon ivu-icon-ios-star"
                      ) {
                        // 解除收藏
                        this.cancelCollect(params.index, params.row);
                        event.currentTarget.className ==
                          "ivu-icon ivu-icon-ios-star-outline";
                      } else {
                        // 收藏
                        this.collect(params.index, params.row);
                        event.currentTarget.className =
                          "ivu-icon ivu-icon-ios-star";
                      }
                    } else {
                      this.$Message.warning("请先登录");
                    }
                  }
                }
              });
            }
          },
          {
            title: self.$t("service.COIN"),
            align: "center",
            key: "coin",
            width: 90,
            render: function(h, params) {
              return h("div", [
                h("span", {}, params.row.coin+"/"+params.row.base)
              ]);
            }
          },
          {
            title: self.$t("service.NewPrice"),
            align: "center",
            key: "price",
            minWidth: 150,
            sortable: true,
            sortMethod: function(a, b, type) {
              let a1 = parseFloat(a);
              let b1 = parseFloat(b);
              if (type == "asc") {
                return a1 - b1;
              } else {
                return b1 - a1;
              }
            },
            render: function(h, params) {
              // var rmb = self.round(self.mul(params.row.price, 6.5), 2);
              // if (self.CNYRate != null)
              //   rmb = self.round(self.mul(params.row.price, self.CNYRate), 2);
              let CNYRate = self.CNYRate || 6.5,
                rmb = self.round(self.mul(params.row.usdRate, self.CNYRate), 2);
              const isgreen =
                parseFloat(params.row.rose) < 0 ? "none" : "inline-block";
              const nogreen =
                parseFloat(params.row.rose) < 0 ? "inline-block" : "none";
              return h("div", {
                attrs: {
                    class: "price-td"
                  }
              }, [
                h("span", {}, params.row.price + ""),
                h("span", {
                  attrs: {
                    class: "price-rmb"
                  }
                }, "  ≈ ￥" + rmb),
                h(
                  "Icon",
                  {
                    props: {
                      type: "arrow-up-c"
                    },
                    style: {
                      display: isgreen,
                      fontSize: "16px",
                      marginLeft: "5px",
                      verticalAlign: "middle"
                    },
                    class: {
                      green: true
                    }
                  },
                  "↑"
                ),
                h(
                  "Icon",
                  {
                    props: {
                      type: "arrow-down-c"
                    },
                    style: {
                      display: nogreen,
                      fontSize: "16px",
                      marginLeft: "5px",
                      verticalAlign: "middle"
                    },
                    class: {
                      red: true
                    }
                  },
                  "↓"
                )
              ]);
            }
          },
          {
            title: self.$t("service.Change"),
            align: "center",
            key: "rose",
            minWidth: 50,
            sortable: true,
            sortMethod: function(a, b, type) {
              let a1 = a.replace(/[^\d|.|-]/g, "") - 0;
              let b1 = b.replace(/[^\d|.|-]/g, "") - 0;
              if (type == "asc") {
                return a1 - b1;
              } else {
                return b1 - a1;
              }
            },
            render: (h, params) => {
              const row = params.row;
              const className = parseFloat(row.rose) < 0 ? "red" : "green";
              return h(
                "span",
                {
                  attrs: {
                    class: className
                  }
                },
                row.rose
              );
            }
          },
          {
            title: self.$t("service.high"),
            align: "center",
            key: "high",
            render: (h, params) => {
              return h("div", {}, params.row.high);
            }
          },
          {
            title: self.$t("service.low"),
            align: "center",
            key: "high",
            render: (h, params) => {
              return h("div", {}, params.row.low);
            }
          },
          {
            title: self.$t("service.ExchangeNum"),
            align: "center",
            key: "volume",
            // minWidth: 110,
            sortable: true,
            sortMethod: function(a, b, type) {
              let a1 = parseFloat(a);
              let b1 = parseFloat(b);
              if (type == "asc") {
                return a1 - b1;
              } else {
                return b1 - a1;
              }
            }
          },
          {
            title: self.$t("service.PriceTrend"),
            align: "center",
            render: function(h, params) {
              let valus = null;
              let len = params.row.trend.length;
              valus =
                len > 0
                  ? params.row.trend
                  : [
                      0,
                      0,
                      0,
                      0,
                      0,
                      0,
                      0,
                      0,
                      0,
                      0,
                      0,
                      0,
                      0,
                      0,
                      0,
                      0,
                      0,
                      0,
                      0,
                      0,
                      0,
                      0,
                      0,
                      0,
                      0
                    ];
              return h(SvgLine, {
                props: {
                  values: valus,
                  rose: params.row.rose,
                  width: 100,
                  height: 25
                }
              });
            }
          },
          {
            title: self.$t("service.Operate"),
            align: "center",
            key: "buyBtn",
            width: 100,
            render: function(h, params) {
              return h("div", [
                h("span", {
                  style: {
                    cursor: "pointer",
                    color: "#f0a70a",
                    display: "inline-block",
                    padding: "2px 8px"
                  },
                  on: {
                    click: function() {
                      self.$router.push({
                        name: 'ExchangePair',
                        params: {
                          pair: params.row.href
                        }
                      });
                    }
                  }
                }, self.$t("service.trading"))
              ]);
            }
          }
        ]
      },
      indexBtn: [
        {
          text: this.$t("service.CUSTOM")
        },
        {
          text: this.$t("service.USDT")
        },
        {
          text: this.$t("service.BTC")
        },
        {
          text: this.$t("service.ETH")
        }
      ],
      choseBtn: 0,
      valueCal: 0,
      showArrow: "never",
      speed: 5000,
      symbol: "",
      usdtData: [],
      usdtList: [],
      btcList: [],
      ethList: [],
      picList: [],
      picShow: false
    };
  },
  created: function() {
    this.init();
  },
  computed: {
    isLogin: function() {
      return this.$store.getters.isLogin;
    },
    lang: function() {
      return this.$store.state.lang;
    },
    langPram: function(){
      if(this.$store.state.lang == "简体中文"){
        return "CN";
      }
      if(this.$store.state.lang == "English"){
        return "EN";
      }
      return "CN";
    }
  },
  watch: {
    lang: function() {
      this.updateLangData();
    }
  },
  mounted: function() {
    this.getCNYRate();
    this.getSymbol();
    //this.initSwiper();
  },
  methods: {
    seachInputChange(){
      this.searchKey = this.searchKey.toUpperCase();
      if(this.choseBtn == 0){
          this.dataIndex = this.coins.favor.filter(item => item["symbol"].indexOf(this.searchKey) == 0);
      }else if(this.choseBtn == 1){
          this.dataIndex = this.coins.USDT.filter(item => item["symbol"].indexOf(this.searchKey) == 0);
      }else if(this.choseBtn == 2){
          this.dataIndex = this.coins.BTC.filter(item => item["symbol"].indexOf(this.searchKey) == 0);
      }else if(this.choseBtn == 3){
          this.dataIndex = this.coins.ETH.filter(item => item["symbol"].indexOf(this.searchKey) == 0);
      }
    },
    initSwiper(){
      var ss = new Swiper ('.swiper-container', {
        loop: true,
        autoplay: true,
        pagination: {
          el: '.swiper-pagination',
          clickable: true,
        },
        slidesPerView :4,
        spaceBetween : 25
      });
      var comtainer = document.getElementById('swiper_container');
      comtainer.onmouseenter = function () {
        ss.autoplay.stop();
      };
      comtainer.onmouseleave = function () {
        ss.autoplay.start();
      }
    },
    strde(str) {
      str = str.trim();
      if(this.langPram == "EN"){
        return str.length > 25 ? str.slice(0, 25) + "..." : str;
      }
      return str.length > 18 ? str.slice(0, 18) + "..." : str;
    },
    updateLangData() {
      this.indexBtn = [
        {
          text: this.$t("service.CUSTOM")
        },
        {
          text: this.$t("service.USDT")
        },
        {
          text: this.$t("service.BTC")
        },
        {
          text: this.$t("service.ETH")
        }
      ];

      this.coins.columns[0].title = this.$t("service.favor");
      this.coins.columns[1].title = this.$t("service.COIN");
      this.coins.columns[2].title = this.$t("service.NewPrice");
      this.coins.columns[3].title = this.$t("service.Change");
      this.coins.columns[4].title = this.$t("service.high");
      this.coins.columns[5].title = this.$t("service.low");
      this.coins.columns[6].title = this.$t("service.ExchangeNum");
      this.coins.columns[7].title = this.$t("service.PriceTrend");
      this.coins.columns[8].title = this.$t("service.Operate");

      this.favorColumns[0].title = this.$t("service.favor");
      this.favorColumns[1].title = this.$t("service.COIN");
      this.favorColumns[2].title = this.$t("service.NewPrice");
      this.favorColumns[3].title = this.$t("service.Change");
      this.favorColumns[4].title = this.$t("service.high");
      this.favorColumns[5].title = this.$t("service.low");
      this.favorColumns[6].title = this.$t("service.ExchangeNum");
      this.favorColumns[7].title = this.$t("service.PriceTrend");
      this.favorColumns[8].title = this.$t("service.Operate");
    },
    init() {
      this.$store.commit("navigate", "nav-index");
      this.$store.state.HeaderActiveName = "1-1";
      this.loadPicData();
      this.addClass(1);
      // this.getmoneyData();
      this.loadDataPage(this.pageNo);
    },
    getStyle(obj, attr) {
      if (obj.currentStyle) {
        return obj.currentStyle[attr];
      } else {
        return getComputedStyle(obj, false)[attr];
      }
    },
    loadDataPage(pageIndex) {
      var param = {};
      (param["pageNo"] = pageIndex),
        (param["pageSize"] = this.pageSize),
        (param["lang"] = this.langPram),
        this.$http
          .post(this.host + this.api.uc.announcement, param)
          .then(response => {
            var resp = response.body;
            if (resp.code == 0) {
              if (resp.data.content.length == 0) return;

              this.totalNum = resp.data.totalElements;

              let FAQListtem = resp.data.content;
              if(this.totalNum > 3){
                this.FAQList = FAQListtem.slice(0, 3);
              }else{
                this.FAQList = FAQListtem;
              }
              this.FAQList.forEach(function(item){
                item.createTime = item.createTime.substring(5, 10);
              });
            } else {
              this.$Notice.error({
                title: this.$t("common.tip"),
                desc: resp.message
              });
            }
          });
    },
    getCNYRate() {
      this.$http
        .post(this.host + "/market/exchange-rate/usd-cny")
        .then(response => {
          var resp = response.body;
          this.CNYRate = resp.data;
        });
    },
    donwload(type) {
      const title = this.$t("common.tip");
      const content = "<p>" + this.$t("common.expect") + "</p>";
      this.$Modal.info({
        title: title,
        content: content,
        closable: true
      });
    },
    loadPicData() {
      let param = {};
      param["sysAdvertiseLocation"] = 1;
      param["lang"] = this.langPram;
      this.$http
        .post(this.host + "/uc/ancillary/system/advertise", param)
        .then(response => {
          var result = response.body;
          if (result.code == 0 && result.data.length > 2) {
            this.picList = result.data;
            this.picShow = true;
            var _this = this;
            setTimeout(function(){
              _this.initSwiper();
            },1000);
          }else{
            this.picShow = false;
          }
        });
    },
    getCoin(symbol) {
      return this.coins._map[symbol];
    },
    startWebsock() {
      var stompClient = null;
      var that = this;
      var socket = new SockJS(that.host + that.api.market.ws);
      stompClient = Stomp.over(socket);
      stompClient.debug = false;
      stompClient.connect({}, function(frame) {
        //订阅价格变化消息
        stompClient.subscribe("/topic/market/thumb", function(msg) {
          var resp = JSON.parse(msg.body);
          var coin = that.getCoin(resp.symbol);
          if (coin != null) {
            // coin.price = resp.close.toFixed(2);
            coin.price = resp.close;
            coin.rose =
              resp.chg > 0
                ? "+" + (resp.chg * 100).toFixed(2) + "%"
                : (resp.chg * 100).toFixed(2) + "%";
            // coin.close = resp.close.toFixed(2);
            // coin.high = resp.high.toFixed(2);
            // coin.low = resp.low.toFixed(2);
            coin.close = resp.close;
            coin.high = resp.high;
            coin.low = resp.low;
            coin.turnover = parseInt(resp.volume);
          }
        });
      });
    },
    round(v, e) {
      var t = 1;
      for (; e > 0; t *= 10, e--);
      for (; e < 0; t /= 10, e++);
      return Math.round(v * t) / t;
    },
    mul(a, b) {
      var c = 0,
        d = a.toString(),
        e = b.toString();
      try {
        c += d.split(".")[1].length;
      } catch (f) {}
      try {
        c += e.split(".")[1].length;
      } catch (f) {}
      return (
        Number(d.replace(".", "")) *
        Number(e.replace(".", "")) /
        Math.pow(10, c)
      );
    },
    addClass(index) {
      this.choseBtn = index;
      if (index == 0) {
        this.dataIndex = this.coins.favor;
      } else if (index == 1) {
        this.dataIndex = this.coins.USDT;
        this.dataIndex2 = this.coins.USDT2;
      } else if (index == 2) {
        this.dataIndex = this.coins.BTC;
        this.dataIndex2 = this.coins.BTC2;
      } else if (index == 3) {
        this.dataIndex = this.coins.ETH;
        this.dataIndex2 = this.coins.ETH2;
      }
    },
    getSymbol() {
      this.loading = true;
      this.$http
        .post(this.host + this.api.market.thumbTrend, {})
        .then(response => {
          var resp = response.body;
          for (var i = 0; i < resp.length; i++) {
            var coin = resp[i];
            coin.price = resp[i].close;
            coin.rose =
              resp[i].chg > 0
                ? "+" + (resp[i].chg * 100).toFixed(2) + "%"
                : (resp[i].chg * 100).toFixed(2) + "%";
            coin.coin = resp[i].symbol.split("/")[0];
            coin.base = resp[i].symbol.split("/")[1];
            coin.href = (coin.coin + "_" + coin.base).toLowerCase();
            coin.isFavor = false;
            this.coins._map[coin.symbol] = coin;
            if(coin.zone == 0) {
              this.coins[coin.base].push(coin); // 主板
            }else{
              this.coins[coin.base+"2"].push(coin); // 创新版
            }
          }
          if (this.isLogin) {
            this.getFavor();
          }
          this.startWebsock();
          this.loading = false;
        });
    },
    // getFavor() {
    //   //查询自选
    //   this.$http
    //     .post(this.host + this.api.exchange.favorFind, {})
    //     .then(response => {
    //       var resp = response.body;
    //       for (var i = 0; i < resp.length; i++) {
    //         var coin = this.getCoin(resp[i].symbol);
    //         this.coins.favor.push(coin);
    //       }
    //     });
    // },
    getFavor() {
      //查询自选(收藏)
      this.$http
        .post(this.host + this.api.exchange.favorFind, {})
        .then(response => {
          var resp = response.body;
          this.coins.favor = [];
          for (var i = 0; i < resp.length; i++) {
            var coin = this.getCoin(resp[i].symbol);
            if (coin != null) {
              coin.isFavor = true;
              this.coins.favor.push(coin);
            }
          }
        });
    },
    collect(index, row) {
      if (!this.isLogin) {
        this.$Message.info(this.$t("common.logintip"));
        return;
      }
      var params = {};
      params["symbol"] = row.symbol;
      this.$http
        .post(this.host + this.api.exchange.favorAdd, params)
        .then(response => {
          var resp = response.body;
          if (resp.code == 0) {
            this.$Message.info(this.$t("exchange.do_favorite"));
            this.getCoin(row.symbol).isFavor = true;
            row.isFavor = true;
            this.coins.favor.push(row);
          }
        });
    },
    cancelCollect(index, row) {
      if (!this.isLogin) {
        this.$Message.info(this.$t("common.logintip"));
        return;
      }
      var params = {};
      params["symbol"] = row.symbol;
      this.$http
        .post(this.host + this.api.exchange.favorDelete, params)
        .then(response => {
          var resp = response.body;
          if (resp.code == 0) {
            this.$Message.info(this.$t("exchange.cancel_favorite"));
            this.getCoin(row.symbol).isFavor = false;
            for (var i = 0; i < this.coins.favor.length; i++) {
              var favorCoin = this.coins.favor[i];
              if (favorCoin.symbol == row.symbol) {
                this.coins.favor.splice(i, 1);
                break;
              }
            }
          }
        });
    }
  }
};
</script>
<style scoped lang="scss" >
@media screen and (max-width:768px){
  .app_bottom{
    display: block!important;
  }
  #fullpage {
    padding-top: 45px!important;
  }
}
.banner-panel{
  height:400px;background-color:#141e2b;overflow:hidden;position:relative;
  .activity-list{
    width: 100%;min-width:1200px;display:flex;flex-start:row;justify-content:center;position:absolute;bottom: 20px;
    .swiper-container {
      width: 72%;
      max-height: 150px;
      margin: 0 auto;
      .swiper-wrapper{
        margin-bottom: 15px;
        .activity-item{
          margin: 0 0;
          &:hover{
            opacity:0.9;
            cursor:pointer;
          }
          img{
            max-width:250px;
            transition: all 0.5s;
            width: 100%;
            &:hover{
              transform: scale(1.05);
            }
          }
        }
      }
    }
  }
}

#pagetips{
  .agent-panel{
    display:flex;flex-direction:row;overflow:hidden;position:relative;justify-content: center;min-width:1200px;
    .title{
      margin-right: 10px;
      float:left;width:220px;padding: 10px 0px;border-right:1px solid rgb(28, 44, 72);letter-spacing: 3px;
      .gettingstart{
        color: #FFF;
        text-align: justify;
        height: 20px;
        &:after{
          display: inline-block;
          width: 100%;
          content: '';
        }
      }
      .tips{
        font-size:10px;color: #869ec9;letter-spacing:2px;margin-top: 5px;text-align: justify;
        height: 18px;
        &:after{
          display: inline-block;
          width: 100%;
          content: '';
        }
      }
    }
    .agent-list{
      float:left;padding: 4px 0px;height:62px;display:flex;flex-direction:row;overflow:hidden;
      .agent-item{
        height:54px;background:#151f2c;width:210px;margin-left:10px;padding-right:15px;
        border: 1px solid #151f2c;
        transition: all 0.5s;
        .agent-img{
          padding-top:7px;margin-left:7px;float:left;
          img{
            height:40px;width:40px;border-radius:40px;
          }
        }
        .agent-detail{
          padding-top:10px;margin-left:10px;float:left;max-width:130px;
          .agent-name{
            font-size: 13px;color:#ffa800;
            overflow: hidden;
            text-overflow: ellipsis;
            display: -webkit-box;
            -webkit-line-clamp: 1;
            max-width: 130px;
            -webkit-box-orient: vertical;
          }
          .agent-count{
            font-size: 10px;color:rgb(103, 122, 153);margin-top:5px;
            overflow: hidden;
            text-overflow: ellipsis;
            display: -webkit-box;
            max-width: 130px;
            -webkit-line-clamp: 1;
            -webkit-box-orient: vertical;
            letter-spacing: 1px;
            white-space: nowrap;
          }
        }
      }
      .agent-item:hover{
        cursor:pointer;
        border: 1px solid rgb(240, 185, 11);
      }
    }
    .agent-all{
      height:62px;text-align:right;line-height:62px;background:transparent;position:absolute;right:12px;font-size:12px;color: #f0a70a;
    }
  }
}

#pagetips {
  background: #141e2b;
  padding: 0 10%;

  overflow: hidden;
  .topnav {
    width: 100%;
    line-height: 40px;
    height: 40px;
    // float: left;
    margin: 0 auto;
    .carl {
      width: 100%;
      height: 40px;
      position: relative;
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      position: relative;
      .notice-list{
        width: 100%;
        text-align:center;
        display:flex;
        flex-start:row;
        justify-content:center;
        height: 40px;
        .notice-item{
          max-width:25%;
          padding:0px 30px;
          text-align:center;
          position:relative;
          .cal_content{
            max-width:100%;
            a{
              color: rgba(130,142,161,1);
              font-size:12px;
            }
            a:hover{
              color: #f0a70a!important;
            }
          }
        }
        .notice-item:not(:last-child):after{
          content: "/";
          position: absolute;
          right: 0;
          top: 1px;
          color: #afafaf;
        }
      }

      .more {
        position: absolute;
        z-index: 0;
        right: 0;
        a {
          color: #f0a70a!important;
          font-size: 12px;
          padding: 3px 12px;
          border-radius:3px;
        }
      }
    }
  }
  .frinend_wakuang {
    width: 50%;
    float: right;
    text-align: right;
    height: 100%;
    line-height: 40px;
    a {
      color: #f0a70a;
      font-size: 14px;
    }
  }
}
#page6 {
  padding: 20px 14%;
  ul {
    list-style-type: none;
  }
  .page6-out {
    -moz-box-shadow: 2px 2px 5px #f5f5f5, -2px -2px 4px #f5f5f5;
    -webkit-box-shadow: 2px 2px 5px #f5f5f5, -2px -2px 4px #f5f5f5;
    box-shadow: 2px 2px 5px #f5f5f5, -2px -2px 4px #f5f5f5;
    padding: 30px 20px;
    overflow: hidden;
    .page6-list {
      width: 33.33333%;
      float: left;
      .list-op {
        .special {
          line-height: 26px;
          .num {
            color: #f0a70a;
          }
        }
        .list-opBHB {
          text-align: left;
        }
        .text {
          text-align: left;
          word-break: break-all;
          margin-right: 20px;
          .num {
            font-size: 30px;
            color: #f0a70a;
            font-weight: 500;
          }
          .type {
            font-size: 16px;
            color: #f0a70a;
            font-weight: 500;
          }
        }
        .num2 {
          color: #f0a70a;
        }
      }
    }
  }
}
#progress {
  padding: 20px 14%;
  .title {
    color: #f0a70a;
    overflow: hidden;
    line-height: 30px;
    font-size: 16px;
    .already {
      float: left;
    }
    .total {
      float: right;
      color: #f0ac19;
    }
  }
  .ivu-progress.ivu-progress-normal {
    .ivu-progress-inner {
      background: #fff;
      border-radius: 0;
      .ivu-progress-bg {
        border-radius: 0;
      }
    }
  }
}
#page2 {
  background: #141e2c;
  height: auto;
  min-height: 320px;
  padding: 40px 14%;
  .page2nav {
    line-height: 50px;
    font-size: 20px;
    background: #1e2834;
    min-width: 864px;
    display:flex;
    .board-title{
      width: 20%;
      height: 60px;
      line-height: 60px;
      text-align:center;
      background: #ffa800;
      color: #000;
    }
    .brclearfix {
      width: 100%;
      li {
        float: left;
        cursor: pointer;
        color: #fff;
        background: #1e2834;
        list-style: none;
        font-size: 16px;
        padding: 5px 40px;
        -moz-box-shadow: 2px 2px 5px transparent, -2px -2px 4px transparent;
        -webkit-box-shadow: 2px 2px 5px transparent, -2px -2px 4px transparent;
        box-shadow: 2px 2px 5px transparent, -2px -2px 4px transparent;
        &:hover {
          background: #222b38;
        }
      }
      li.active {
        background: #27313e;
        color: #f0a70a;
        position: relative;
        border-bottom: 2px solid #f0a70a;
      }
    }
  }
  .ptjy {
    height: 100%;
    min-width: 864px;
    .tables {
      border: none;
      -moz-box-shadow: 2px 2px 5px transparent, -2px -2px 4px transparent;
      -webkit-box-shadow: 2px 2px 5px transparent, -2px -2px 4px transparent;
      box-shadow: 2px 2px 5px transparent, -2px -2px 4px transparent;
      .ivu-table {
        .ivu-table-header {
          .ivu-table-column-center {
            background: none;
          }
        }

      }
    }
  }
}
.ivu-input{
  border-radius: 20px;
  border-color: transparent;
}
#page4 {
  background: #192330;
  height: auto;
  padding: 80px 0 80px 0;
  ul {
    width: 88%;
    margin: 0 auto;
    li {
      flex: 0 0  25%;
      display: inline-block;
      width: 24%;
      padding: 0 15px;
      div {
        width: 130px;
        height: 130px;
        border-radius: 50%;
        vertical-align: middle;
        text-align: center;
        margin: 0 auto;
        img {
          height: 125px;
          margin-top: 8px;
        }
      }
      p {
        font-size: 14px;
        margin: 20px 0;
        text-align: center;
        color: #828ea1;
      }
      p.title {
        color: #fff;
        font-size: 18px;
        font-weight: 400;
      }
    }
  }
}
.bg-dark{
  background: #192330;
}
.bg-light{
  background: #222e3d;
}
#page6{
  min-height: 460px;
  padding: 80px 14%;
  position: relative;
  .title{
    font-size: 30px;
    text-align:center;
    width: 100%;
    letter-spacing: 6px;
  }
  .title-left{
    font-size: 30px;
    text-align:left;
    width: 100%;
    letter-spacing: 6px;
  }
  .subtitle{
    margin-bottom: 40px;
    color: #828ea1;
    font-size: 13px;
    text-align:center;
    width: 100%;
  }
  .detail{
    line-height: 40px;
    letter-spacing: 2px;
    text-indent:45px;
    font-size: 20px;
    margin-bottom: 20px;
    color: #828ea1;
    text-align:justify;
  }
}
#page5 {
  height: 517px;
  padding: 0 14%;
  position: relative;
  background: #192330 url("../../assets/images/app-download.jpg") no-repeat 0 0;
  background-size: 100% 517px;
  overflow: hidden;
  .phone_image {
    width: 480px;
    float: left;
    height: 100%;
    background: url("../../assets/images/phone_img.png") no-repeat left 150px;
    background-size: 480px;
  }
  .download {
    float: right;
    list-style-type: none;
    .qrcode {
      color: #fff;
      font-size: 18px;
      font-weight: 500;
      margin-top: 140px;
      text-align: left;
      line-height: 40px;
      margin-bottom: 14px;
      font-weight: 900;
    }
    .wrapper {
      width: 190px;
      height: 300px;
      float: left;
      padding: 0;
      margin-right: 0px;
      .download_app {
        height: 190px;
        img {
          width: 100%;
          border-radius: 5px;
        }
      }
      .abstract {
        text-align: center;
        display: flex;
        margin-top: 14px;
        justify-content: center;
        align-items: center;
        height: 30px;
        .image {
          width: 18px;
          img {
            vertical-align: middle;
          }
        }
        .content {
          font-size: 12px;
          text-align: left;
          color: #fff;
          margin-left: 15px;
        }
      }
    }
  }
}
</style>
<style lang="scss">
#progress {
  .ivu-progress.ivu-progress-normal {
    .ivu-progress-inner {
      background: #fff;
      border-radius: 5px;
      border: 1px solid #f0a70a;
      .ivu-progress-bg {
        border-radius: 0;
        background: #f0a70a;
      }
    }
  }
}
#page2 {
  .ptjy {
    position:relative;
    min-height: 500px;
    background-color: #141e2c;
    border-bottom: 1px solid #27313e!important;
    &:after{
      background:#27313e!important;
      content: '';
      width: 1px;
      height: 100%;
      position: absolute;
      top: 0;
      right: 0;
      z-index: 3;
    }
    &:before{
      background:#27313e!important;
      content: '';
      width: 1px;
      height: 100%;
      position: absolute;
      top: 0;
      left: 0;
      z-index: 3;
    }
    .tables {
      .ivu-table {
        background-color: #141e2c;
        .ivu-table-header {
          background:#27313e;
          color:#888;
          th {
            background: none;
            border-color:#26303d;
          }
        }

        .ivu-table-header{
          background: #141e2c;
              border-bottom: 1px solid #27313e;
          .ivu-table-cell {
            padding: 10px 0;
          }
        }
        .ivu-table-body {
          .ivu-table-cell {
            padding: 5px 0;
          }
        }
        .ivu-table-body table .ivu-table-tbody {
            tr td{
              border-color:#26303d;
              color:#fff;
            }
        }
      }
    }
  }
}
</style>


<style>
.section .ivu-carousel-dots-inside {
  bottom: 20px;
}

.green {
  color: #00b275 !important;
}

.red {
  color: #f15057 !important;
}

.brclearfix:after {
  content: "";
  display: block;
  height: 0;
  overflow: hidden;
  clear: both;
}

#fullpage {
  background: #fff;
  padding-top: 60px;
}

.section {
  /* height: 574px; */
  /* text-align: center; */
  /* color: #fff; */
}

.carousel-item {
  background-repeat: no-repeat;
  background-position: center;
  height: 500px;
  background-size: cover;
}

.demo-carousel1 {
  /* background: url(../../assets/images/banner1.jpg) no-repeat center; */
  height: 575px;
  background-size: cover;
}

.demo-carousel2 {
  /* background: url(../../assets/images/banner2.jpg) no-repeat center; */
  height: 575px;
  background-size: cover;
}

.demo-carousel-btn {
  width: 100%;
  height: 100%;
  padding-top: 345px;
}

.demo-carousel1 a {
  display: inline-block;
  width: 250px;
  height: 55px;
  margin: 0 15px;
}

/*.register {
  background: url(../../assets/images/register.png) no-repeat;
}*/

/* .usdt {
  float: left;
  width: 100%;
} */

.usdt_icon {
  float: left;
  width: 18%;
  height: 290px;
  background: #1d293a;
  padding-top: 125px;
  margin: 5px;
}
.btc,
.eth {
  float: left;
  width: 100%;
  margin-top: 10px;
}

.btc_icon,
.eth_icon {
  float: left;
  width: 18%;
  height: 140px;
  background: #1d293a;
  padding-top: 50px;
  margin: 5px;
}

#nav {
  position: fixed;
  right: 10%;
  top: 50%;
  z-index: 100;
}

#nav ul li {
  display: block;
  /* width: 120px; */
  height: 25px;
  margin: 7px;
  position: relative;
  padding-right: 20px;
  text-align: right;
  color: #fff;
}

#nav ul li span {
  display: none;
}

#nav ul li a {
  top: 2px;
  right: 2px;
  width: 8px;
  height: 8px;
  background: url(../../assets/images/page.png) no-repeat;
  position: absolute;
  z-index: 1;
}

#nav ul li a:hover,
#nav ul li a.active {
  top: 0;
  right: -3px;
  width: 18px;
  height: 18px;
  background: url(../../assets/images/page_active.png) no-repeat;
  position: absolute;
  z-index: 1;
}

#page3 {
  position: relative;
  color: #979797;
  /* background: url(../../assets/images/section3.png) no-repeat center; */
}

#page3 label {
  position: absolute;
  top: 30%;
  left: 20%;
  font-size: 30px;
}

@-webkit-keyframes fadeinB {
  0% {
    top: 50%;
    opacity: 0;
  }
  100% {
    top: 30%;
    opacity: 1;
  }
}

@keyframes fadeinB {
  0% {
    top: 50%;
    opacity: 0;
  }
  100% {
    top: 30%;
    opacity: 1;
  }
}

@-webkit-keyframes fadeinA {
  0% {
    top: 60%;
    opacity: 0;
  }
  100% {
    top: 40%;
    opacity: 1;
  }
}

@keyframes fadeinA {
  0% {
    top: 60%;
    opacity: 0;
  }
  100% {
    top: 40%;
    opacity: 1;
  }
}

#page3 p {
  position: absolute;
  top: 40%;
  left: 20%;
  font-size: 15px;
}

.news_1 {
  color: #202b3c;
  font-size: 12px;
}

.news_2 {
  color: #505c6f;
  font-size: 13px;
}

.news_3 {
  color: #fff;
  font-size: 18px;
}

.news_title {
  color: #fff;
  font-size: 20px;
}

.news_date {
  color: #505c6f;
}

.news_detail {
  color: #98999f;
  margin-top: 10px;
}
.ptjy .ivu-table td, .ptjy .ivu-table th{
  height: 25px;
}
.price-rmb{
  color: #828ea1;
  font-size: 10px;
  margin-left: 3px;
}
.price-td{
  padding-left: 100px;
  text-align: left;
}
th .ivu-table-cell span{
  font-weight: normal!important;
}
.ivu-table td{
  background: transparent!important;
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


