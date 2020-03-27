<template>
    <div class="content-wrap">
            <div class="container" id="List">
                <Row>
                    <Col span="4">
                    <div class="leftmenu left-box">
                        <div class="user-info">
                            <div class="avatar-box">
                                <div class="user-face user-avatar-public">
                                    <span class="user-avatar-in">{{usernameS}}</span>
                                    <!-- <span class="online-status-box user-states ">
                                                    <span class="circles"></span>
                                                </span> -->
                                </div>
                                <div class="user-name">
                                </div>
                            </div>
                            <!-- 个人姓名改为昵称 -->
                            <span class="ml10" style="width: 105px;">{{strpro(user.username)}}</span>
                        </div>
                        <div class="deal-market-info">
                            <p v-if="user.emailVerified==1">
                                <i class="iconfont icon-youxiang111"></i>
                                <span class="unmarket">{{$t('otc.tradeinfo.emaildone')}}</span>
                            </p>
                            <p v-else>
                                <i class="iconfont icon-youxiang"></i>
                                <span class="unmarket">{{$t('otc.tradeinfo.emailundo')}}</span>
                            </p>
                            <p v-if="user.phoneVerified==1">
                                <i class="iconfont icon-dianhua111"></i>
                                <span class="">{{$t('otc.tradeinfo.teldone')}}</span>
                            </p>
                            <p v-else>
                                <i class="iconfont icon-dianhua"></i>
                                <span class="">{{$t('otc.tradeinfo.telundo')}}</span>
                            </p>
                            <p v-if="user.idCardVerified==1">
                                <i class="iconfont icon-renzheng111"></i>
                                <span class="">{{$t('otc.tradeinfo.idcarddone')}}</span>
                            </p>
                            <p v-else>
                                <i class="iconfont icon-renzheng"></i>
                                <span class="unmarket">{{$t('otc.tradeinfo.idcardundo')}}</span>
                            </p>
                        </div>
                        <div class="deal-user-trade-info">
                            <p>{{$t('otc.tradeinfo.exchangetimes')}}：
                                <em class="trade-times">{{user.transactions}}</em>
                            </p>
                            <!-- <p>平均放行：
                                <em>0.21 分钟</em>
                            </p> -->
                        </div>
                    </div>
                    </Col>
                    <Col span="20">
                    <div class="right-safe">
                        <div class="trade-right-box">
                            <div class="trade-price">
                                <p>
                                    <label>{{$t('otc.tradeinfo.price')}}</label>
                                    <span>{{user.price}} CNY / {{user.unit}}</span>
                                    <!-- <span @click="update">
                                                                                                                            <a href="javascript:;">刷新</a>
                                                                                                                        </span> -->
                                </p>
                                <p>
                                    <label>{{$t('otc.tradeinfo.num')}}</label>
                                    <span>{{user.number}}&nbsp;{{user.unit}}</span>
                                </p>
                                <p>
                                    <label>{{$t('otc.tradeinfo.paymethod')}}</label>
                                    <span>{{user.payMode}}</span>
                                </p>
                                <p>
                                    <label>{{$t('otc.tradeinfo.exchangelimitamount')}}</label>
                                    <span>{{user.minLimit}} - {{user.maxLimit}} CNY</span>
                                </p>
                                <p>
                                    <label>{{$t('otc.tradeinfo.location')}}</label>
                                    <span>{{$t('otc.tradeinfo.location_text')}}</span>
                                </p>
                                <p>
                                    <label>{{$t('otc.tradeinfo.exchangeperiod')}}</label>
                                    <span>{{user.timeLimit}}{{$t('otc.tradeinfo.minute')}}</span>
                                </p>
                            </div>
                            <div class="trade-operation">
                                <div class="trade-price-input">
                                    <p class="price-input-list">
                                        <Poptip trigger="focus" :content="text1" style="width: 100%;">
                                            <Input @on-change="transform1" v-model="buyPrice" size="large" :placeholder="$t('otc.tradeinfo.amounttip')" style="width: 420px">
                                            <span slot="prepend">CNY</span>
                                            </Input>
                                        </Poptip>
                                    </p>
                                    <span class="exchange1">
                                        <Icon type="md-swap" />
                                    </span>
                                    <p class="price-input-list">
                                        <Poptip trigger="focus" :content="text2" style="width: 100%;">
                                            <Input @on-change="transform2" v-model="nuyNum" size="large" :placeholder="$t('otc.tradeinfo.numtip')" style="width: 420px">
                                            <span slot="prepend">{{user.unit}}</span>
                                            </Input>
                                        </Poptip>
                                    </p>
                                </div>
                                <div class="price-box">
                                    <p class="show-price">
                                        <em>{{type}}:</em>
                                        <span>&nbsp;&nbsp;{{buyPrice}} CNY / {{nuyNum}} {{user.unit}}</span>
                                    </p>
                                    <button class="btn-trade-in" @click="submit" :disabled="btnDisabled">{{btnType}}</button>
                                </div>
                            </div>
                            <div class="trade-remark">
                                <h5 class="titles">
                                    <span>{{$t('otc.tradeinfo.remarktitle')}}</span>
                                </h5>
                                <p class="content">
                                    {{user.remark}}
                                </p>
                                <h5 class="titles">
                                    <span>{{$t('otc.tradeinfo.exchangetitle')}}</span>
                                </h5>
                                <div class="content">
                                    <p>{{$t('otc.tradeinfo.exchange_tip1')}}</p>
                                    <p>{{$t('otc.tradeinfo.exchange_tip2')}}</p>
                                    <p>{{$t('otc.tradeinfo.exchange_tip3')}}</p>
                                    <p>{{$t('otc.tradeinfo.exchange_tip4')}}</p>
                                    <p>{{$t('otc.tradeinfo.exchange_tip5')}}</p>
                                </div>
                            </div>
                            <div class="modal">
                                <!---->
                            </div>
                        </div>
                    </div>
                    </Col>
                </Row>
            </div>
    </div>
</template>
<script>
export default {
  components: {},
  data() {
    return {
      usernameS: "",
      text1: "",
      text2: "",
      btnDisabled: false,
      submitBtn: false,
      btnType: "",
      type: "",
      user: {
          username:"aaa"
      },
      // price: '',
      buyPrice: "",
      nuyNum: 0,
      minLimit: 100,
      maxLimit: 1000,
      // number:0.6,
      advertiseType: 1
    };
  },
  methods: {
    update() {
      // this.price = '100';
      // this.user.advertiseType=1
    },
    transform1() {
      if (!Number.isNaN(Number(this.buyPrice))) {
        this.nuyNum = this.round(this.div(this.buyPrice, this.priceNow), 8);
        if (/^\d+(\.\d{1,2})?$/.test(this.buyPrice)) {
          this.submitBtn = true;
        } else {
          this.submitBtn = false;
          this.text1 = this.$t("otc.tradeinfo.warning1");
        }
      } else {
        this.text1 =
          this.$t("otc.tradeinfo.warning2") +
          this.user.minLimit +
          "~" +
          this.user.maxLimit;
        this.submitBtn = false;
        return false;
      }
    },
    transform2() {
      if (!Number.isNaN(Number(this.nuyNum))) {
        this.buyPrice = this.round(this.mul(this.nuyNum, this.priceNow), 8);
        if (this.nuyNum <= this.user.number) {
          if (/^\d+(\.\d{1,8})?$/.test(this.nuyNum)) {
            this.submitBtn = true;
          } else {
            this.submitBtn = false;
            this.text2 = this.$t("otc.tradeinfo.warning3");
          }
        } else {
          this.submitBtn = false;
          return false;
        }
      } else {
        this.text2 =
          this.$t("otc.tradeinfo.warning4") +
          this.minNum +
          "~" +
          this.user.number;
        this.submitBtn = false;
        return false;
      }
    },
    getIdAdv() {
      //获取id广告信息
      this.$http
        .post(this.host + "/otc/order/pre", { id: this.$route.query.tradeId })
        .then(response => {
          var resp = response.body;
          if (resp.code == 0) {
            this.user = resp.data;
            this.text1 =
              this.$t("otc.tradeinfo.warning2") +
              this.user.minLimit +
              "~" +
              this.user.maxLimit;
            // this.minNum = (this.user.minLimit/this.user.price).toFixed(8);
            this.text2 =
              this.$t("otc.tradeinfo.warning4") +
              this.minNum +
              "~" +
              this.user.number;
            // console.log(this.user)
            if (this.user.advertiseType == 1) {
              this.btnType = this.$t("otc.tradeinfo.confirmbuyin");
              this.type = this.$t("otc.tradeinfo.buyin");
            } else if (this.user.advertiseType == 0) {
              this.btnType = this.$t("otc.tradeinfo.confirmsellout");
              this.type = this.$t("otc.tradeinfo.sellout");
            }
            this.usernameS = (this.user.username + "")
              .replace(/^\s+|\s+$/g, "")
              .slice(0, 1);
          } else {
            this.$Message.error(resp.message);
          }
        });
    },
    submit() {
      if (this.submitBtn) {
        this.btnDisabled = true;
        if (this.user.advertiseType == 1) {
          let param = {};
          param["id"] = this.$route.query.tradeId;
          param["coinId"] = this.user.otcCoinId;
          param["price"] = this.user.price;
          param["money"] = this.buyPrice;
          param["amount"] = this.nuyNum;
          this.$http
            .post(this.host + "/otc/order/buy", param)
            .then(response => {
              this.btnDisabled = false;
              var resp = response.body;
              if (resp.code == 0) {
                this.$Message.success(resp.message);

                let self = this;
                setTimeout(() => {
                  self.$router.push("/chat?tradeId=" + resp.data);
                }, 2000);
              } else {
                this.$Message.error(resp.message);
              }
            });
        } else if (this.user.advertiseType == 0) {
          let param = {};
          param["id"] = this.$route.query.tradeId;
          param["coinId"] = this.user.otcCoinId;
          param["price"] = this.user.price;
          param["money"] = this.buyPrice;
          param["amount"] = this.nuyNum;
          this.$http
            .post(this.host + "/otc/order/sell", param)
            .then(response => {
              this.btnDisabled = false;
              var resp = response.body;
              if (resp.code == 0) {
                this.$Message.success(resp.message);
                let self = this;
                setTimeout(() => {
                  self.$router.push("/chat?tradeId=" + resp.data);
                }, 2000);
              } else {
                this.$Message.error(resp.message);
              }
            });
        }
      } else {
        this.$Message.error(this.$t("otc.tradeinfo.warning5"));
      }
    },
    sendMsg() {
      // this.$http.post(this.host + '/otc/order/sell', param).then(response => {
      //   var resp = response.body;
      //   if (resp.code == 0) {
      //     this.$Message.success(resp.message);
      //     let self = this
      //     setTimeout(() => {
      //       self.$router.push('/chat?tradeId=' + resp.data);
      //     }, 2000)
      //   } else {
      //     this.$Message.error(resp.message);
      //   }
      // })
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
    div(a, b) {
      var c,
        d,
        e = 0,
        f = 0;
      try {
        e = a.toString().split(".")[1].length;
      } catch (g) {}
      try {
        f = b.toString().split(".")[1].length;
      } catch (g) {}
      return (
        (c = Number(a.toString().replace(".", ""))),
        (d = Number(b.toString().replace(".", ""))),
        this.mul(c / d, Math.pow(10, f - e))
      );
    },
    round(v, e) {
      var t = 1;
      for (; e > 0; t *= 10, e--);
      for (; e < 0; t /= 10, e++);
      return Math.round(v * t) / t;
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
    // console.log(this.$route.query)
    // console.log(this.div(121.03, 121.03) + '--00--')
    // this.update()
    this.getIdAdv();
  },
  computed: {
    priceNow: function() {
      return (
        (this.user.price + "").replace(/,/g, "").replace(/[^\d|.]/g, "") - 0
      );
    },
    minNum: function() {
      return (this.user.minLimit / this.priceNow).toFixed(8);
    },
    maxNum: function() {
      return this.user.maxLimit / this.priceNow;
    }
  }
};
</script>

<style scoped>
/* right */

.trade-right-box {
  margin-left: 20px;
  text-align: left;
}

.trade-right-box .trade-price {
  padding: 36px;
  border: 1px solid #27313e;
  margin-bottom: 20px;
}

.trade-right-box .trade-price p {
  color: #fff;
  font-size: 14px;
  line-height: 2.8;
}

.trade-right-box .trade-price p label {
  min-width: 80px;
  display: inline-block;
}

.trade-right-box .trade-price p span {
  margin-left: 15%;
  display: inline-block;
}

.trade-right-box .trade-operation {
  padding: 20px;
  border: 1px solid #27313e;
  margin-bottom: 20px;
}

.trade-right-box .trade-operation .trade-price-input {
  display: -webkit-box;
  display: -ms-flexbox;
  display: flex;
  -webkit-box-align: center;
  -ms-flex-align: center;
  align-items: center;
  margin-bottom: 20px;
}

.boxinput .ivu-input {
  border: none;
  background-color: transparent;
  outline: none;
  padding: 10px;
  display: inline-block;
  width: 300px;
}

.trade-right-box .trade-operation .trade-price-input .exchange1 {
  width: 10%;
  text-align: center;
  font-size: 24px;
}

.trade-right-box .trade-operation .text-inputs {
  background-color: #192330;
  border: 1px solid #27313e;
  outline: none;
  display: block;
  height: 100px;
  width: 100%;
  resize: none;
  padding: 20px;
  margin-bottom: 20px;
  color: #ccc;
}

.trade-right-box .trade-operation .price-box {
  display: -webkit-box;
  display: -ms-flexbox;
  display: flex;
  -webkit-box-align: center;
  -ms-flex-align: center;
  align-items: center;
}

.trade-right-box .trade-operation .price-box .show-price {
  border: 1px solid #27313e;
  width: 80%;
  height: 58px;
  display: -webkit-box;
  display: -ms-flexbox;
  display: flex;
  -webkit-box-align: center;
  -ms-flex-align: center;
  align-items: center;
  padding-left: 10px;
}

.trade-right-box .trade-operation .price-box .show-price em {
  font-style: normal;
  font-size: 14px;
  color: #fff;
}

.trade-right-box .trade-operation .price-box .show-price span {
  font-size: 18px;
  color: #f0ac19;
  font-weight: bolder;
}

.trade-right-box .trade-operation .price-box .btn-trade-in {
  outline: medium;
  border: 0;
  color: white;
  padding: 14px 20px;
  background-color: #f0ac19;
  cursor: pointer;
  width: 20%;
  text-align: center;
  font-size: 20px;
}

.trade-right-box .trade-remark {
  /* background-color: white; */
  border: 1px solid #27313e;
  padding: 30px 36px;
  /* margin-bottom: 30px; */
}

.trade-right-box .trade-remark .titles {
  margin-bottom: 15px;
}

.trade-right-box .trade-remark .titles span {
  font-size: 16px;
  color: #fff;
  padding-right: 30px;
}

.trade-right-box .trade-remark .content {
  margin-bottom: 30px;
  font-size: 14px;
  color: #8994a3;
  line-height: 1.8;
}

/* -- */

.icon1 {
  background: url("../../assets/img/btc.png") no-repeat 0 0;
  background-size: 100% 100%;
}

.icon2 {
  background: url("../../assets/img/usdt.png") no-repeat 0 0;
  background-size: 100% 100%;
}

.content-wrap {
  /* background: #f5f5f5; */
  min-height: 600px;
  padding-top: 80px;
}

.container {
  width: 85%;
  margin: 0 auto;
  min-width: 1200px;
  background: #192330;
  color: #fff;
  margin-bottom: 20px;
}

/* left */

.leftmenu {
  margin-bottom: 60px;
  background-color: #192330;
  position: relative;
  min-height: 1px;
  padding: 50px 15px 50px 10px;
}

.left-box .user-info {
  display: -webkit-box;
  display: -ms-flexbox;
  display: flex;
  -webkit-box-align: center;
  -ms-flex-align: center;
  align-items: center;
  padding-bottom: 15px;
  border-bottom: 1px dashed #ebeff5;
}

.avatar-box {
  display: -webkit-box;
  display: -ms-flexbox;
  display: flex;
  -webkit-box-align: center;
  -ms-flex-align: center;
  align-items: center;
  -webkit-box-orient: vertical;
  -webkit-box-direction: normal;
  -ms-flex-direction: column;
  flex-direction: column;
}

.user-avatar-public {
  background: #fff;
  height: 65px;
  width: 65px;
  box-shadow: 0 1px 5px 0 rgba(71, 78, 114, 0.24);
  position: relative;
}

.user-avatar-public > .user-avatar-in,
.user-avatar-public {
  border-radius: 50%;
  display: -webkit-box;
  display: -ms-flexbox;
  display: flex;
  -webkit-box-pack: center;
  -ms-flex-pack: center;
  justify-content: center;
  -webkit-box-align: center;
  -ms-flex-align: center;
  align-items: center;
}

.user-avatar-public > .user-avatar-in {
  border-radius: 50%;
  display: -webkit-box;
  display: -ms-flexbox;
  display: flex;
  -webkit-box-pack: center;
  -ms-flex-pack: center;
  justify-content: center;
  -webkit-box-align: center;
  -ms-flex-align: center;
  align-items: center;
  background: #f0a70a;
  height: 60px;
  width: 60px;
  color: #fff;
}

.left-box span.ml10 {
  color: #fff;
  margin-left: 5px;
}

.left-box .deal-market-info {
  padding: 20px 0 20px 20px;
  border-bottom: 1px dashed #ebeff5;
}

.left-box .deal-market-info p {
  display: -webkit-box;
  display: -ms-flexbox;
  display: flex;
  -webkit-box-align: center;
  -ms-flex-align: center;
  align-items: center;
  font-size: 14px;
  color: #fff;
}

.iconfont {
  font-family: iconfont !important;
  font-size: 16px;
  font-style: normal;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

.left-box .deal-market-info p .iconfont {
  margin-right: 20px;
  font-size: 20px;
}

.left-box .deal-market-info p .iconfont:before {
  background-size: 100% 100%;
  width: 20px;
  height: 20px;
  display: inline-block;
  content: "";
}

.icon-youxiang:before {
  background-image: url(../../assets/img/t1-1.png);
}

.icon-youxiang111:before {
  background-image: url(../../assets/img/t1-2.png);
}

.icon-dianhua:before {
  background-image: url(../../assets/img/t2-1.png);
}

.icon-dianhua111:before {
  background-image: url(../../assets/img/t2-2.png);
}

.icon-renzheng:before {
  background-image: url(../../assets/img/t3-1.png);
}

.icon-renzheng111:before {
  background-image: url(../../assets/img/t3-2.png);
}

.left-box .deal-user-trade-info {
  padding-top: 20px;
  color: #8994a3;
}

.left-box .deal-user-trade-info p {
  margin-bottom: 6px;
}

.left-box .deal-user-trade-info p em {
  font-style: normal;
  color: #fff;
}
</style>


