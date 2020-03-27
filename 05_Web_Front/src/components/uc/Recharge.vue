<template>
  <div class="nav-rights">
    <div class="nav-right">
      <div class="bill_box">
        <!-- <section class="trade-group merchant-top">
                    <i class="merchant-icon tips"></i>
                    <span class="tips-word">{{$t('uc.finance.recharge.recharge')}}</span>
                </section> -->
        <section>
          <div class="table-inner action-box open">
            <!-- <i class="angle" style="right: 71px;"></i> -->
            <div class="action-inner">
              <div class="inner-left">
                <p class="describe">{{$t('uc.finance.recharge.symbol')}}</p>
                <Select v-model="coinType" style="width:100px;margin-top: 23px;" @on-change="changeCoin">
                  <Option v-for="item in coinList" :value="item.coin.unit" :key="item.coin.unit">{{ item.coin.unit }}</Option>
                </Select>
              </div>
              <div class="inner-box deposit-address">
                <p class="describe">{{$t('uc.finance.recharge.address')}}</p>
                <div class="title">
                  <Input v-model="qrcode.value" readonly style="width: 400px;color:#8c979f;"></Input>
                  <a v-show="isShowGetAddress"class="link-copy" @click="resetAddress">{{$t('uc.finance.recharge.getaddress')}}</a>
                  <a v-clipboard:copy="qrcode.value" v-clipboard:success="onCopy" v-clipboard:error="onError" href="javascript:;" id="copyBtn" class="link-copy">{{$t('uc.finance.recharge.copy')}}</a>
                  <a id="showQRcode" class="link-qrcode" href="javascript:;" @click="showEwm"> {{$t('uc.finance.recharge.qrcode')}}
                    <Modal v-model="isShowEwm" width="280">
                      <!--<div v-show="isShowEwm" class="show-qrcode">-->
                      <p slot="header" style="text-align: center;">{{$t('uc.finance.recharge.qrcodeaddress')}}</p>
                      <div class="show-qrcode" style="text-align: center;padding: 15px 10px;border-radius:10px;background:#FFF;">
                        <!--<qriously :value="qrcode.coinName+':'+qrcode.value" :size="qrcode.size" />-->
                        <qriously :value="qrcode.value" :size="qrcode.size" foreground="#000" />
                      </div>
                      <div slot="footer"></div>
                    </Modal>
                  </a>
                </div>
                <p v-if="accountType != 0" style="margin-top: 10px;font-size:12px;color:#828ea1;">Memo：<span style="font-size: 20px;color: #F90;font-weight:bold;">{{memoCode}}</span></p>
                <p v-if="accountType != 0" style="margin-top: 10px;font-size:12px;color:#828ea1;">
                  {{$t('uc.finance.recharge.memotips')}}
                  <a style="color: #f0a70a;" v-clipboard:copy="memoCode" v-clipboard:success="onCopy" v-clipboard:error="onError" href="javascript:;" id="copyBtn" class="link-copy">{{$t('uc.finance.recharge.copy')}} Memo</a>
                </p>
              </div>
            </div>
            <div class="action-content">
              <div class="action-body">
                <p class="acb-p1">{{$t('common.tip')}}</p>
                <p class="acb-p2">• {{$t('uc.finance.recharge.msg3')}}{{minRechargeAmount}}{{coinType}}{{$t('uc.finance.recharge.msg3_1')}}<br>• {{$t('uc.finance.recharge.msg1')}}<br>• {{$t('uc.finance.recharge.msg2')}}<br>• {{$t('uc.finance.recharge.msg4')}}<br>• {{$t('uc.finance.recharge.msg5')}}</p>
              </div>
            </div>
            <div class="action-content">
              <div class="action-body">
                <p class="acb-p1">{{$t('uc.finance.recharge.record')}}</p>
                <div class="order-table">
                  <Table :columns="tableColumnsRecharge" :data="tableRecharge" :loading="loading" :no-data-text="$t('common.nodata')"></Table>
                  <div style="margin: 10px;overflow: hidden">
                    <div style="float: right;">
                      <Page :total="dataCount" :current="1" @on-change="changePage" class="recharge_btn"></Page>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </section>
      </div>
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
  inject: ['reload'],
  data() {
    return {
      accountType: 0,
      memoCode: "",
      minRechargeAmount:"0.001",
      isShowGetAddress: false,
      isShowEwm: false,
      dataCount: 0,
      loading: true,
      qrcode: {
        value: "",
        size: 220,
        coinName: "",
        unit: ""
      },
      coinType: "",
      coinList: [],
      tableRecharge: [],
      allTableRecharge: []
    };
  },
  methods: {
    changePage(index) {
      this.getList(index);
    },
    getCurrentCoinRecharge() {
      if (this.coinType != "") {
        var temp = [];
        for (var i = 0; i < this.allTableRecharge.length; i++) {
          if (this.allTableRecharge[i].symbol == this.coinType) {
            temp.push(this.allTableRecharge[i]);
          }
        }
        this.tableRecharge = temp;
      } else {
        this.tableRecharge = this.allTableRecharge;
      }
    },
    showEwm() {
      this.isShowEwm = !this.isShowEwm;
    },
    onCopy(e) {
      this.$Message.success(
        this.$t("uc.finance.recharge.copysuccess") + e.text
      );
    },
    onError(e) {
      this.$Message.error(this.$t("uc.finance.recharge.copysuccess"));
    },
    changeCoin(value) {
      for (var i = 0; i < this.coinList.length; i++) {
        if (this.coinList[i].coin.unit == value) {
          this.qrcode.value = this.coinList[i].address;
          this.qrcode.coinName = this.coinList[i].coin.name.toLowerCase();
          this.qrcode.unit = this.coinList[i].coin.unit;
          this.minRechargeAmount = this.coinList[i].coin.minRechargeAmount;
          if(this.coinList[i].coin.accountType == 1) {
            this.qrcode.value = this.coinList[i].coin.depositAddress;
            this.memoCode = this.coinList[i].memo;
            this.accountType = 1;
          }else{
            this.accountType = 0;
          }

          if(this.qrcode.value == "" || this.qrcode.value == null || this.qrcode.value == undefined){
            this.isShowGetAddress = true;
          }else{
            this.isShowGetAddress = false;
          }
        }
      }
      this.getCurrentCoinRecharge();
    },
    resetAddress(){
      var self = this;
      if(this.qrcode.value == "" || this.qrcode.value == null || this.qrcode.value == undefined){
        this.$Spin.show({
            render: (h) => {
                return h('div', [
                    h('Icon', {
                        'class': 'demo-spin-icon-load',
                        props: {
                            type: 'ios-loading',
                            size: 18
                        }
                    }),
                    h('div', {style:{
                      fontSize: "12px",
                      marginTop: "8px"
                    }}, this.$t('uc.finance.recharge.gettingaddress'))
                ])
            }
        });
        let params = {};
        params["unit"] = this.qrcode.unit;
        this.$http
          .post(this.host + "/uc/asset/wallet/reset-address", params)
          .then(response => {
            var resp = response.body;
            if (resp.code == 0) {
              setTimeout(function() {
                self.reload();
                self.$Spin.hide();
              }, 3000);
            } else {
              this.$Message.error(resp.message);
              self.$Spin.hide();
            }
          });
      }
    },
    getMoney() {
      //获取
      this.$http.post(this.host + this.api.uc.wallet).then(response => {
        var resp = response.body;
        if (resp.code == 0) {
          for (let i = 0; i < resp.data.length; i++) {
            var coin = resp.data[i];
            if (coin.coin.canRecharge == 1) {
              this.coinList.push(coin);
            }
          }
          this.changeCoin(this.coinType);
        } else {
          this.$Message.error(resp.message);
        }
      });
    },
    getList(pageN) {
      //获取tableRecharge
      let memberId = 0;
      !this.$store.getters.isLogin && this.$router.push("/login");
      this.$store.getters.isLogin && (memberId = this.$store.getters.member.id);
      let pageNo = pageN,
        pageSize = 10,
        type = 0,
        params = { memberId, pageNo, pageSize, type };
      this.$http.post(this.host + "/uc/asset/transaction/all", params).then(response => {
          var resp = response.body;
          if (resp.code == 0) {
            if (resp.data) {
              let trueData = resp.data;
              this.allTableRecharge = trueData.content || [];
              this.dataCount = trueData.totalElements || 0;
              this.getCurrentCoinRecharge();
            }
            this.loading = false;
          } else {
            this.$Message.error(resp.message);
          }
        });
    },
    getMember() {
      let self = this;
      this.$http.post(this.host + "/uc/approve/security/setting").then(response => {
          var resp = response.body;
          if (resp.code == 0) {
            if (resp.data.realName == null || resp.data.realName == "") {
              // 判断是否实名认证，未认证跳转到实名认证页面；
              this.$Message.success(this.$t("otc.publishad.submittip1"));
              self.$router.push("/uc/safe");
            } else if (resp.data.phoneVerified == 0) {
              // 判断是否是手机号0，1，未认证跳转到实名认证页面；
              this.$Message.success(this.$t("otc.publishad.submittip2"));
              self.$router.push("/uc/safe");
            } else if (resp.data.fundsVerified == 0) {
              // 判断是否设置交易密码，未认证跳转到实名认证页面；
              this.$Message.success(this.$t("otc.publishad.submittip3"));
              self.$router.push("/uc/safe");
            }
          } else {
            this.$Message.error(resp.message);
          }
        });
    }
  },
  created() {
    this.coinType = this.$route.query.name || "";
    //this.getMember();
    this.getMoney();
    this.getList(1);
  },
  computed: {
    tableColumnsRecharge() {
      let columns = [];
      columns.push({
        title: this.$t("uc.finance.recharge.time"),
        align: "center",
        width: 200,
        render: (h, param) => {
          let str = param.row.createTime;
          return h("div", {}, str);
        }
      });
      columns.push({
        title: this.$t("uc.finance.recharge.symbol"),
        align: "center",
        width: 120,
        render: (h, param) => {
          let str = param.row.symbol;
          return h("div", {}, str);
        }
      });
      columns.push({
        title: this.$t("uc.finance.recharge.address"),
        align: "center",
        render: (h, param) => {
          let str = param.row.address;
          return h("div", {}, str);
        }
      });
      columns.push({
        title: this.$t("uc.finance.recharge.amount"),
        align: "center",
        width: 100,
        render: (h, param) => {
          let str = param.row.amount;
          return h("div", {}, str);
        }
      });
      return columns;
    }
  }
};
</script>
<style scoped>
.table-inner {
  position: relative;
  text-align: left;
  border-radius: 3px;
}

.acb-p1 {
  font-size: 16px;
  font-weight: 400;
  line-height: 50px;
}

.acb-p2 {
  font-size: 13px;
  line-height: 24px;
  color: #8c979f;
}

.action-inner {
  width: 100%;
  display: table;
}

.action-inner .inner-box {
  display: table-cell;
  width: 100%;
}

.action-box .title .copy {
  user-select: text;
}

.action-box .title a.link-copy {
  font-size: 14px;
  margin-left: 20px;
  color: #f0a70a;
}

.hb-night a {
  text-decoration: none;
  color: #7a98f7;
  transition: all 0.2s ease-in-out;
  cursor: pointer;
}

.action-box .title a.link-qrcode {
  margin-left: 20px;
  font-size: 14px;
  position: relative;
  color: #f0a70a;
}

.hb-night a {
  text-decoration: none;
  color: #7a98f7;
  transition: all 0.2s ease-in-out;
  cursor: pointer;
}

.action-box .subtitle {
  font-size: 12px;
  margin-top: 30px;
}

.action-content {
  width: 100%;
  margin-top: 30px;
  /* overflow: hidden; */
  display: table;
  color: #ccc;
}

.action-box .title {
  margin-top: 20px;
  font-size: 20px;
  user-select: none;
}

.action-box .title .show-qrcode {
  position: absolute;
  top: -100px;
  left: 40px;
  padding: 10px;
  background: #FFF;
}

.action-inner .inner-box.deposit-address {
  width: 80%;
}

p.describe {
  font-size: 16px;
  font-weight: 600;
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

.bill_box {
  width: 100%;
  height: auto;
  /* overflow: hidden; */
}

.nav-right {
  height: auto;
  overflow: hidden;
  padding: 0 15px;
}

.order_box {
  width: 100%;
  background: #fff;
  height: 56px;
  line-height: 56px;
  margin-bottom: 20px;
  border-bottom: 2px solid #ccf2ff;
  position: relative;
  text-align: left;
}

.order_box a {
  color: #8994a3;
  font-size: 16px;
  padding: 0 30px;
  cursor: pointer;
  text-decoration: none;
  text-align: center;
  line-height: 54px;
  display: inline-block;
}

.order_box .search {
  position: absolute;
  width: 300px;
  height: 32px;
  top: 12px;
  right: 0;
  display: flex;
  /* border: #c5cdd7 solid 1px; */
}
</style>
