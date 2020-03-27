<template>
  <div class="nav-right tradeCenter">
    <!-- <section class="trade-group merchant-top"> -->
    <!-- <i class="merchant-icon tips"></i>
            <span class="tips-word">{{this.coin.toUpperCase()}}</span> -->
    <!-- <a href="/user/merchants">申请为认证商家&gt;&gt;</a> -->
    <!--<a href="/#/identbusiness">{{$t("otc.applymerchant")}}&gt;&gt;</a>-->
    <!-- </section> -->
    <section class="list-content">
      <Tabs :value="tabPage" v-model="tabPage">
        <TabPane :label="$t('otc.buyin')" name="buy">
          <div class="table-responsive list-table">
            <Table :no-data-text="$t('common.nodata')" :border="showBorder" :stripe="showStripe" :show-header="showHeader" :height="fixedHeader ? 250 : ''" :size="tableSize" :data="advertiment.ask.rows" :columns="advertiment.columns" :loading="loading" :disabled-hover="true"></Table>
            <div class="page_change">
              <div style="float: right;">
                <Page v-if="advertiment.ask.totalElement > 0" :pageSize="advertiment.ask.pageNumber" :total="advertiment.ask.totalElement" :current="advertiment.ask.currentPage" @on-change="changePage"></Page>
              </div>
            </div>
          </div>
        </TabPane>
        <TabPane :label="$t('otc.sellout')" name="sell">
          <div class="table-responsive list-table">
            <Table :no-data-text="$t('common.nodata')" :border="showBorder" :stripe="showStripe" :show-header="showHeader" :height="fixedHeader ? 250 : ''" :size="tableSize" :data="advertiment.bid.rows" :columns="advertiment.columns" :loading="loading" :disabled-hover="true"></Table>
            <div class="page_change">
              <div style="float: right;">
                <Page v-if="advertiment.bid.totalElement > 0" :pageSize="advertiment.bid.pageNumber" :total="advertiment.bid.totalElement" :current="advertiment.bid.currentPage" @on-change="changePage"></Page>
              </div>
            </div>
          </div>
        </TabPane>
      </Tabs>
    </section>
  </div>
</template>


<style scoped lang="scss">
#List .nav-right {
  color: #26264c;
  padding-right: 0;
  .list-content {
    color: #fff;
  }
}
</style>
<style lang="scss">
#List .nav-right {
  color: #26264c;
  padding-right: 0;
  .list-content {
    color: #fff;
    .ivu-tabs {
      .ivu-tabs-bar {
        border-bottom:none;
        .ivu-tabs-nav-container {
          .ivu-tabs-nav-wrap {
            .ivu-tabs-nav-scroll {
              .ivu-tabs-ink-bar.ivu-tabs-ink-bar-animated {
                background: #f0ac19;
              }
              .ivu-tabs-tab {
                &:hover {
                  color: #f0ac19;
                }
              }
              .ivu-tabs-tab.ivu-tabs-tab-active.ivu-tabs-tab-focused {
                color: #f0ac19;
              }
            }
          }
        }
      }
      .ivu-tabs-content.ivu-tabs-content-animated {
        .ivu-tabs-tabpane {
          .ivu-table-wrapper {
            border: none;
            .ivu-table-body{
              .ivu-table-tbody{
                .ivu-table-row{
                  .ivu-table-cell.ivu-table-cell-ellipsis{
                    .user-face.user-avatar-public{
                      span{
                        background:#f0ac19;
                      }
                    }
                    p a{
                      color:#f0ac19;
                    }
                  }
                }
              }
            }
          }
          .page_change{
            margin: 10px;
            overflow: hidden;
          }
        }
      }
    }
  }
}
.tradeCenter button span,
.tradeCenter button a,
.tradeCenter button a:hover {
  display: block;
  color: white;
}

.tradeCenter .ivu-poptip-popper button span {
  display: block;
  color: inherit;
}

#carousel {
  margin-bottom: 40px;
}

// #List .nav-right .bread {
//   font-size: 16px;
// }

// #List .nav-right .bread a {
//   color: #e24a64;
//   display: inline-block;
//   padding-left: 1rem;
//   cursor: pointer;
// }

// #List .nav-right .list-content .list-title {
//   box-shadow: 0 4px 0 0 rgba(69, 112, 128, 0.06);
//   -webkit-box-shadow: 0 4px 0 0 rgba(69, 112, 128, 0.06);
//   z-index: 1;
//   position: relative;
// }

// #List .nav-right .list-content .list-title .search {
//   background-color: #fff;
//   height: 40px;
//   padding: 6px 12px;
// }

// #List .nav-right .list-content .list-title .search .dropdown-box {
//   display: flex;
//   flex: 1;
//   justify-content: flex-start;
//   align-items: center;
//   height: 100%;
// }

// #List .nav-right .list-content .list-title .search .dropdown-box .select-menu {
//   border: transparent;
//   outline: none;
//   background-color: transparent;
// }

// #List .nav-right .list-content .list-title .search .dropdown-box .select-items {
//   width: 25%;
//   display: flex;
//   justify-content: flex-start;
//   align-items: center;
// }

// .nav .open > a,
// .nav .open > a:hover,
// .nav .open > a:focus {
//   background: transparent;
// }

// #List .nav-right .list-content .list-title .search-btn {
//   background-color: #c5cdd7;
//   display: flex;
//   justify-content: center;
//   border-radius: 0 4px 4px 0;
// }

// #List .nav-right .list-content .list-title .search-btn span {
//   font-size: 18px;
//   height: 36px;
//   line-height: 36px;
// }

// #List .nav-right .list-content .list-title .search-btn em {
//   height: 36px;
//   line-height: 36px;
//   margin-left: 6px;
//   font-style: normal;
// }

// #List .nav-right .list-content .list-table table {
//   table-layout: fixed;
// }

// #List .nav-right .list-content .list-table tr:nth-of-type(even) {
//   background-color: #fff;
// }

// #List .nav-right .list-content .list-table tr > td {
//   vertical-align: middle;
//   line-height: normal;
//   width: 25%;
// }

// #List .nav-right .list-content .list-table .table > tbody > tr > td {
//   border-top: 1px solid transparent;
//   text-align: left;
//   height: 75px;
// }
// .ivu-menu-light.ivu-menu-vertical .ivu-menu-item-active:not(.ivu-menu-submenu) {
//   color: #f0a70a;
// }
// #List .nav-right .list-content .list-table .user-name {
//   display: flex;
//   justify-content: flex-start;
//   padding-left: 5%;
// }

// #List .nav-right .list-content .list-table .user-name .user-icon {
//   background: #00b5f6;
//   border-radius: 50%;
//   height: 42px;
//   width: 42px;
//   display: flex;
//   justify-content: center;
// }

#List .nav-right .list-content .list-table .user-name .user-icon span {
  font-size: 22px;
  color: white;
  align-self: center;
}

#List .nav-right .list-content .list-table .user-name .user-info {
  margin-left: 5%;
  width: 100px;
  word-wrap: inherit;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

#List .nav-right .list-content .list-table .user-name .user-info p {
  height: 16px;
  margin: 0 0 3px;
}

// #List .nav-right .list-content .list-table .user-name .user-info .merchant {
//   height: 17px;
//   width: 67px;
//   display: inline-block;
  /* background: url("../../images/comm/merchant-flag.png") no-repeat; */
// }

// #List .nav-right .list-content .list-table .price p {
//   font-size: 16px;
//   font-weight: bolder;
//   color: #444f71;
// }

// #List .nav-right .list-content .list-table .price h5 {
//   font-size: 12px;
//   color: #8994a3;
//   margin-top: 0;
// }

// #List .nav-right .list-content .list-table .Btn a {
//   border-radius: 6px;
//   background-color: transparent;
//   color: #e24a64;
//   display: inline-block;
//   padding: 6px;
//   width: 100px;
//   text-align: center;
//   text-decoration: none;
// }

// #List .nav-right .list-content .list-table .Btn .sell {
//   background-color: #0db124;
//   color: #fff;
// }

// #List .nav-right .list-content .list-table .Btn .buy {
//   background-color: #ed7325;
//   color: #fff;
// }

#List .nav-right .list-content .pagelist {
  display: flex;
  justify-content: flex-end;
}

#List .nav-right .list-content .pagelist ul {
  list-style: none;
}

#List .nav-right .list-content .pagelist ul li {
  display: inline-block;
  background-color: #ebeff5;
  height: 32px;
  width: 32px;
  text-align: center;
  line-height: 32px;
  border: 1px solid #c5cdd7;
  border-radius: 6px;
  cursor: pointer;
  margin: 0 2px;
}

#List .nav-right .list-content .pagelist ul li:hover {
  background-color: #c5cdd7;
}

#List .nav-right .list-content .pagelist ul li a {
  color: #26264c;
}

#List .header-search {
  width: 100%;
}

#List .select-items select {
  width: initial;
}

#List .list-payMethod {
  width: 80%;
  display: inline-block;
  word-break: keep-all;
}

.select-items .form-control {
  -webkit-box-shadow: none;
  box-shadow: none;
}

.nav-pills .dropdown a {
  color: #555555 !important;
}

.has-success .control-label {
  color: #26264c !important;
}

.trade-group {
  margin-bottom: 20px;
  font-size: 14px;
}

.merchant-icon {
  display: inline-block;
  margin-left: 4px;
  background-size: 100% 100%;
}

.merchant-icon.tips {
  width: 4px;
  height: 22px;
  margin-right: 10px;
  background: #f0a70a;
}

.merchant-icon.alipay {
  width: 17px;
  height: 17px;
  background-image: url(../../assets/img/alipay.png);
}

.merchant-icon.bankcard {
  width: 17px;
  height: 17px;
  background-image: url(../../assets/img/bankcard.png);
}

.merchant-icon.wechat {
  width: 17px;
  height: 17px;
  background-image: url(../../assets/img/wechat.png);
}

.merchant-icon.westernunion {
  width: 17px;
  height: 17px;
}

.merchant-icon.paytm {
  width: 29px;
  height: 17px;
}

.merchant-icon.m-booth {
  width: 131px;
  height: 94px;
  background-position: 0 -220px;
}

.merchant-icon.m-server {
  width: 158px;
  height: 94px;
  background-position: 0 -335px;
}

.merchant-icon.m-rate {
  width: 125px;
  height: 94px;
  background-position: 0 -110px;
}

.merchant-icon.m-ok {
  width: 23px;
  height: 9px;
  background-position: -100px 0;
}

.merchant-top {
  display: flex;
  align-items: center;
  // background: #fff;
  padding: 0 15px;
  color: #fff;
}

.merchant-top .tips-word {
  flex-grow: 2;
  text-align: left;
}

.merchant-item {
  padding: 20px 15px 20px 15px;
  background: #fff;
  width: 31%;
  float: left;
  margin: 0 1%;
}

.merchant-item.center {
  margin: 0 1.5%;
}

.merchant-item .item-hd {
  /* background: url("../../images/trade/merchant_item_split.png") left bottom no-repeat; */
  padding-bottom: 20px;
  display: flex;
  align-items: center;
}

.merchant-item .item-hd .item-face {
  width: 42px;
  height: 42px;
  text-align: center;
  line-height: 42px;
  border-radius: 42px;
  -webkit-border-radius: 42px;
  color: #fff;
  background: #00b5f6;
}

.merchant-item .item-hd .item-name {
  padding: 0 10px;
}

.merchant-item .item-hd .item-name p {
  margin-bottom: 0;
}

.merchant-item .item-hd .item-name p:first-child {
  color: #fff;
  margin-bottom: 5px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.merchant-item .item-hd .item-name p:last-child {
  color: #5c68a6;
  font-size: 12px;
}

.merchant-item .text-right {
  display: flex;
  justify-content: flex-end;
}

.merchant-item .text-right .online-status-box {
  color: #18b111;
  display: flex;
}

.merchant-item .item-hd .item-pay {
  flex-grow: 2;
  text-align: right;
}

.merchant-item .item-hd .item-pay .states {
  height: 17px;
  width: 67px;
  display: inline-block;
}

.merchant-item .item-hd .item-pay .states.merchant {
  background: url("../../assets/img/renzheng.png") no-repeat;
  background-size: 100% 100%;
}

.merchant-item .item-hd .item-pay p {
  font-size: 12px;
  color: #ed7325;
  margin-bottom: 5px;
}

.merchant-item .item-bd {
  padding-top: 10px;
}

.merchant-item .item-bd .price {
  font-size: 16px;
  color: #3e435e;
  font-weight: bold;
}

.merchant-item .item-bd .price span {
  font-size: 12px;
}

.merchant-item .item-bd .limit {
  color: #636981;
  font-size: 12px;
  padding-bottom: 15px;
}

.merchant-item .item-bd .btn {
  height: 32px;
  line-height: 32px;
  font-size: 14px;
  color: #fff;
  padding: 0 12px;
  border-radius: 6px;
  -webkit-border-radius: 6px;
}

.merchant-item .item-bd .btn-buy {
  background: #ed7325;
}

.merchant-item .item-bd .btn-sell {
  background: #0db124;
}

.merchant-items {
  margin-bottom: 40px;
}

.carousel-indicators li {
  width: 30px;
  height: 5px;
  border-radius: 5px;
  -webkit-border-radius: 3px;
  border: none;
  background: #d4d6e1;
}

.carousel-indicators .active {
  width: 30px;
  height: 5px;
  border-radius: 5px;
  -webkit-border-radius: 3px;
  border: none;
  background: #7f8bc6;
  margin: 1px;
}

.carousel-indicators {
  bottom: -30px;
}

.m-intro {
  width: 33.33%;
  float: left;
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
}

.m-intro p {
  color: #474e72;
  font-weight: bold;
  font-size: 16px;
}

.m-subtitle {
  line-height: 40px;
  padding-left: 20px;
  background: #f7f7fa;
  color: #636981;
  font-size: 12px;
}

.m-data-lf {
  width: 20%;
  float: left;
  display: flex;
  align-items: center;
}

.m-data-cn {
  width: 45%;
  float: left;
  display: flex;
  align-items: center;
}

.m-data-rf {
  width: 35%;
  float: left;
  display: flex;
  align-items: center;
}

.online-status-box {
  height: 20px;
}

.headerimg {
  color: rgb(245, 106, 0);
  background-color: rgb(253, 227, 207);
  display: inline-block;
  width: 40px;
  height: 40px;
  line-height: 40px;
  text-align: center;
  border-radius: 50%;
  margin-right: 5px;
}

.headerimg ~ p {
  display: inline-block;
}

// .price {
//   font-size: 16px;
//   font-weight: bolder;
//   color: #444f71;
// }

// .price2 {
//   font-size: 12px;
//   color: #8994a3;
//   margin-top: 0;
// }

.renzheng {
  height: 17px;
  width: 67px;
  display: inline-block;
  background: url("../../assets/img/renzheng.png") no-repeat;
  background-size: 100% 100%;
  transform: translateY(-10px);
  display: block;
}

.renzhengA {
  transform: translateY(-10px);
  display: block;
}

.tjbtn {
  width: 80%;
}

.user-avatar-public {
  background: #fff;
  border-radius: 50%;
  height: 45px;
  width: 30px;
  display: flex;
  justify-content: center;
  align-items: center;
  box-shadow: 0 1px 5px 0 rgba(71, 78, 114, 0.24);
  position: relative;
}

.user-avatar-public > .user-avatar-in {
  background: #f0a70a;
  border-radius: 50%;
  height: 35px;
  width: 35px;
  display: flex;
  justify-content: center;
  align-items: center;
  color: white;
}

.ivu-table-cell .user-avatar-public {
  width:45px;
  display: inline-block;
  margin: 10px 10px 10px 0;
  vertical-align: middle;
}

.ivu-table-cell .user-avatar-public > .user-avatar-in {
  transform: translate(5px, 5px);
}

.ivu-table-cell .user-avatar-public ~ p {
  /*width: 60%;*/
  display: inline-block;
}

/*新加的样式*/
// .list-content
//   .ivu-table-body
//   .ivu-table-tbody
//   .ivu-table-cell.ivu-table-cell-ellipsis
//   p
//   a {
//   color: #f0a70a;
// }
// .list-content {
//   .ivu-tabs-bar {
//     .ivu-tabs-nav-container {
//       .ivu-tabs-ink-bar.ivu-tabs-ink-bar-animated {
//         background: #f0a70a;
//       }
//       .ivu-tabs-tab.ivu-tabs-tab-active.ivu-tabs-tab-focused {
//         color: #f0a70a;
//       }
//       .ivu-tabs-tab {
//         &:hover {
//           color: #f0a70a;
//         }
//       }
//     }
//   }
// }
</style>



<script>
export default {
  components: {},
  data() {
    var self = this;
    return {
      showBorder: false,
      showStripe: false,
      showHeader: true,
      showIndex: false,
      showCheckbox: false,
      fixedHeader: false,
      loading: true,
      dataCount: 10,
      tableSize: "large",
      tabPage: "buy",
      advertiment: {
        //卖出的广告数据
        ask: {
          rows: [],
          currentPage: 1,
          totalPage: 1,
          pageNumber: 10,
          totalElement: 0
        },
        //买入的广告数据
        bid: {
          rows: [],
          currentPage: 1,
          totalPage: 1,
          pageNumber: 10,
          totalElement: 0
        },
        columns: [
          {
            title: self.$t("otc.merchant"),
            key: "memberName",
            // width: 160,
            ellipsis: true,
            render: function(h, params) {
              var avatar = params.row.avatar,
                haveAvatar = false;
              var innerCNT = [];
              if (avatar != null && avatar != "") {
                innerCNT[0] = h(
                  "div",
                  {
                    attrs: {
                      class: "user-face user-avatar-public"
                    }
                  },
                  [
                    h("img", {
                      attrs: {
                        src: avatar,
                        width: "45px",
                        height: "45px"
                      },
                      style: {
                        "border-radius": "50%"
                      }
                    })
                  ]
                );
              } else {
                innerCNT[0] = h(
                  "div",
                  {
                    attrs: {
                      class: "user-face user-avatar-public"
                    }
                  },
                  [
                    h(
                      "span",
                      {
                        attrs: {
                          class: "user-avatar-in"
                        }
                      },
                      params.row.memberName
                        .replace(/^\s+|\s+$/g, "")
                        .slice(0, 1)
                    )
                  ]
                );
              }
              innerCNT[1] = h("p", [
                h(
                  "a",
                  {
                    style: {
                      marginRight: "8px",
                      cursor: "pointer",
                      paddingTop: "5px"
                    },
                    class: {
                      // renzhengA: params.row.renzheng
                    },
                    on: {
                      click: function() {
                        if (self.isLogin) {
                          self.$router.push(
                            "/checkuser?id=" + params.row.memberName
                          );
                        } else {
                          self.$router.push("/login");
                        }
                      }
                    }
                  },
                  params.row.memberName
                  // self.strpro(params.row.memberName)
                ),
                h(
                  "div",
                  {
                    class: {
                      // renzheng: params.row.renzheng
                    }
                  },
                  ""
                )
              ]);
              if (params.row.level == 2)
                innerCNT[2] = h(
                  "div",
                  {
                    attrs: {
                      class: "user-business-v"
                    },
                    style: {
                      display: "inline-block",
                      "vertical-align": "text-top"
                    }
                  },
                  [
                    h("img", {
                      attrs: {
                        src: require("../../assets/images/business_v.png")
                      }
                    })
                  ]
                );
              return h("div", innerCNT);
            }
          },
          {
            title: self.$t("otc.volume"),
            key: "transactions",
            width:100,
            align:"center"
          },
          {
            title: self.$t("otc.paymethod"),
            key: "payMode",
            align:"center",
            // width:130
          },
          {
            align:"center",
            title: self.$t("otc.amount"),
            key: "remainAmount"
          },
          {
            title:"限额",
            align:'center',
            render:(h, params)=>{
              return h('div',{},params.row.minLimit + "-" + params.row.maxLimit + "CNY")
            }
          },
          {
            title:"单价",
            align:'center',
            render:(h, params)=>{
              return h('div',{},params.row.price + "CNY")
            }
          },
          // {
          //   title: self.$t("otc.price_coin"),
          //   key: "price",
          //   width: 170,
          //   render: function(h, params) {
          //     return h("div", [
          //       h(
          //         "p",
          //         {
          //           attrs: {
          //             class: "price"
          //           }
          //         },
          //         params.row.price + "CNY"
          //       ),
          //       h(
          //         "p",
          //         {
          //           attrs: {
          //             class: "price2"
          //           }
          //         },
          //         params.row.minLimit + "-" + params.row.maxLimit + "CNY"
          //       )
          //     ]);
          //   }
          // },
          {
            title: self.$t("otc.operate"),
            key: "buyBtn",
            width:70,
            align:"center",
            render: function(h, params) {
              return h("p", [
                h(
                  "a",
                  {
                    style: {
                      color: params.row.advertiseType == 0 ? "#f15057" : "#00b275",
                    },
                    on: {
                      click: () => {
                        if (!self.isLogin) {
                          self.$router.push("/login");
                        } else if (!self.member.realName) {
                          //                                            } else if (!self.member.memberLevel) {
                          self.$Message.error(self.$t("otc.validate"));
                          setTimeout(() => {
                            self.$router.push("/uc/safe");
                          }, 2000);
                        } else {
                          self.$router.push(
                            "/otc/tradeInfo?tradeId=" + params.row.advertiseId
                          );
                        }
                      }
                    }
                  },
                  // [
                  //   h(
                  //     "div",
                  //     {
                  //       // props: {
                  //       //   type:"error",
                  //       //     // params.row.advertiseType == 0 ? "error" : "success",
                  //       //   long: true
                  //       // },
                  //       style: {
                  //         marginRight: "8px",
                  //         width: "80%",
                  //       }
                  //     },
                      params.row.advertiseType == 0
                        ? self.$t("otc.sell")
                        : self.$t("otc.buy")
                    // )
                  // ]
                )
              ]);
            }
          }
        ]
      }
    };
  },
  computed: {
    isLogin: function() {
      return this.$store.getters.isLogin;
    },
    member: function() {
      return this.$store.getters.member;
    },
    coin: function() {
      return this.$route.params[0];
    },
    lang: function() {
      return this.$store.state.lang;
    }
  },
  watch: {
    coin: function() {
      this.reloadAd();
    },
    lang: function() {
      this.updateLangData();
    }
  },
  methods: {
    updateLangData() {
      this.advertiment.columns[0].title = this.$t("otc.merchant");
      this.advertiment.columns[1].title = this.$t("otc.volume");
      this.advertiment.columns[2].title = this.$t("otc.paymethod");
      this.advertiment.columns[2].title = this.$t("otc.amount");
      this.advertiment.columns[2].title = this.$t("otc.price_coin");
      this.advertiment.columns[2].title = this.$t("otc.operate");
    },
    loadAd(pageNo, advertiseType, table) {
      //获取广告
      let params = {};
      table.rows = [];
      table.totalElement = 0;
      table.currentPage = pageNo;
      params["pageNo"] = pageNo;
      params["pageSize"] = table.pageNumber;
      params["advertiseType"] = advertiseType;
      params["unit"] = this.coin;
      this.$http
        .post(this.host + this.api.otc.advertise, params)
        .then(response => {
          var resp = response.body;
          if (resp.code == 0) {
            if (resp.data.context) {
              table.rows = resp.data.context;
              table.totalElement = resp.data.totalElement;
            }
          } else {
            this.$Message.error(resp.message);
          }
          this.loading = false;
        });
    },
    changePage(page) {
      if (this.tabPage == "sell") {
        this.loadAd(page, 0, this.advertiment.bid);
      } else {
        this.loadAd(page, 1, this.advertiment.ask);
      }
    },
    reloadAd() {
      // this.tabPage = "buy";
      this.loadAd(1, 0, this.advertiment.bid);
      this.loadAd(1, 1, this.advertiment.ask);
    },
    strpro(str) {
      let newStr = str;
      str = str.slice(1);
      var re = /[\D\d]*/g;
      var str2 = str.replace(re, function(str) {
        var result = "";
        for (var i = 0; i < str.length; i++) {
          result += "*";
        }
        return result;
      });
      return newStr.slice(0, 1) + str2;
    }
  },
  created() {
    this.reloadAd();
  }
};
</script>