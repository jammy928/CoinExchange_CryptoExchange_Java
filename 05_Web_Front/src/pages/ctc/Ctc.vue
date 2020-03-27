<template>
  <div class="ctc">
    <img class="bannerimg" src="../../assets/images/ctc-bg.jpg">
    <div class="ctc_container">
      <h1>{{$t('ctc.title')}}</h1>
      <p style="letter-spacing: 1px;">{{$t('ctc.desc')}}</p>
      <div class="main">
          <Tabs :animated="false" style="width:100%;">
            <TabPane :label="'USDT' + $t('ctc.trade')" name="all">
              <div class="ctc-container">
                <div class="trade_wrap">
                  <div class="trade_panel">
                    <div class="trade_bd_ctc">
                      <div class="panel panel_buy">
                        <div class="bd bd_limited">
                          <Form>
                            <FormItem class="buy-input">
                              <label class="before">{{$t('ctc.buyprice')}}</label>
                              <Input v-model="buyPrice" disabled></Input>
                              <label class="after" style="color: #45b854;">CNY</label>
                            </FormItem>
                            <FormItem class="trade-input">
                              <label class="before">{{$t('ctc.buynum')}}：</label>
                              <InputNumber style="width:70%;float:right;" v-model="buyAmount"  size="large" :max="50000" :min="50" :placeholder="$t('ctc.input50tips')"></InputNumber>
                              <label class="after">USDT</label>
                            </FormItem>
                            <p style="font-size: 12px;margin-top: -20px;text-align:right;margin-bottom: 10px;">&nbsp; </p>
                            <FormItem>
                              <label class="before">{{$t('ctc.payType')}}：</label>
                              <Select v-model="payType" style="width:70%;float:right;height:40px;" size="large">
                                  <Option v-for="item in payTypeList" :value="item.value" :key="item.value">{{ item.label }}</Option>
                              </Select>
                            </FormItem>
                            <div style="height: 30px;line-height: 30px;margin-top: -20px;margin-bottom: 5px;color: #0074eb;text-align:right;font-size:12px;">
                              <router-link to="/uc/account">{{$t("ctc.payset")}}</router-link>
                            </div>
                            <div class="total buy_total" style="min-height">
                              <div style="min-height: 40px;">
                                <div style="float:left;">{{$t('ctc.payamount')}}</div>
                                <div style="float:right;">
                                  <span style="color: #45b854;font-size:24px;font-weight: 600;">{{totalBuyMoney}}</span>
                                  <span style="font-size:14px;margin-left: 5px;color: #45b854;">CNY</span>
                                </div>
                              </div>
                              <div style="width: 100%;font-size:12px;text-align:right;">{{$t("ctc.moneyTips")}}</div>
                            </div>
                            <Button style="padding-bottom: 10px;padding-top: 10px;" class="bg-green" size="large" @click="buyClick">{{$t("ctc.buyin")}} USDT</Button>
                          </Form>
                        </div>
                      </div>
                      <div class="panel panel_sell">
                        <div class="bd bd_limited">
                          <Form ref="formValidate">
                            <FormItem class="sell-input">
                              <label class="before">{{$t('ctc.sellprice')}}</label>
                              <Input  v-model="sellPrice" disabled></Input>
                              <label class="after" style="color: #f2334f;">CNY</label>
                            </FormItem>
                            <FormItem class="trade-input">
                              <label class="before">{{$t('ctc.sellnum')}}：</label>
                              <InputNumber style="width:70%;float:right;" v-model="sellAmount" size="large" :max="50000" :min="50" :placeholder="$t('ctc.input50tips')"></InputNumber>
                              <label class="after">USDT</label>
                            </FormItem>
                            <p style="font-size: 12px;margin-top: -20px;text-align:right;margin-bottom: 10px;">
                              <span>{{$t('ctc.avabalance')}}</span>：
                              <span>21212</span><span style="margin-left: 5px;">USDT</span>
                            </p>
                            <FormItem>
                              <label class="before">{{$t('ctc.receiveType')}}：</label>
                              <Select v-model="receiveType" style="width:70%;float:right;height:40px;" size="large">
                                  <Option v-for="item in receiveTypeList" :value="item.value" :key="item.value">{{ item.label }}</Option>
                              </Select>
                            </FormItem>
                            <div style="height: 30px;line-height: 30px;margin-top: -20px;text-align:right;margin-bottom: 5px;font-size: 12px;">
                            {{$t("ctc.useselfaccount")}}
                            </div>
                            <div class="total buy_total">
                              <div style="min-height: 40px;">
                                <div style="float:left;">{{$t('ctc.getamount')}}</div>
                                <div style="float:right;color: #f2334f;">
                                  <span style="color: #f2334f;font-size:24px;font-weight: 600;">{{totalSellMoney}}</span>
                                  <span style="font-size:14px;margin-left: 5px;">CNY</span>
                                </div>
                              </div>
                              <div style="width: 100%;font-size:12px;text-align:right;">{{$t("ctc.moneyTips")}}</div>
                            </div>
                            <Button style="padding-bottom: 10px;padding-top: 10px;" class="bg-red" size="large" @click="sellClick">{{$t("ctc.sell")}} USDT</Button>
                          </Form>
                        </div>
                      </div>
                    </div>

                    <div style="float:right;width: 30%;height: 455px;font-size: 12px;color: #bcbcbc;">
                      <div style="padding: 25px 35px;width: 100%;height: 395px;overflow-y: auto; overflow-x:hidden;background-color:#192330;text-align:left;line-height: 26px;">
                        <p style="text-align:center;font-size: 18px;margin-bottom: 10px;">{{$t("ctc.notice")}}</p>
                        <p>{{$t("ctc.notice1")}}</p>
                        <p>{{$t("ctc.notice2")}}</p>
                        <p>{{$t("ctc.notice3")}}</p>
                        <p>{{$t("ctc.notice4")}}</p>
                        <p>{{$t("ctc.notice5")}}</p>
                        <router-link target="_blank" to="/helpdetail?cate=2&id=40&cateTitle=交易指南" style="float:right;">{{$t("ctc.moredetail")}}</router-link>
                      </div>
                      <div class="notice-bottom">
                        <router-link to="/uc/safe" class="notice-btn-left">{{$t("ctc.verifyset")}}</router-link>
                        <router-link to="/uc/account" class="notice-btn-right">{{$t("ctc.payset")}}</router-link>
                      </div>
                    </div>
                    <div></div>
                  </div>
                </div>
              </div>
              <div class="table">
                <Table :no-data-text="$t('common.nodata')" :columns="columns" :data="orders" :loading="loading"></Table>
                <div class="page">
                  <Page :total="total" :pageSize="pageSize" :current="pageNo" @on-change="loadDataPage"></Page>
                </div>
              </div>
            </TabPane>
          </Tabs>
      </div>
    </div>

    <Modal v-model="modal" width="450">
      <!-- <P style="color:red;font-weight: bold;">
        {{$t('uc.finance.withdraw.fundpwdtip')}}<br/>
        <Input type="password" v-model="fundpwd" :placeholder="$t('otc.chat.msg7')"></Input>
      </p> -->
      <p slot="header">
        {{$t("ctc.tip")}}
      </p>
      <Form class="withdraw-form-inline" ref="formInline" :model="formInline" inline>
        <!-- <FormItem>
          <Input type="text" v-model="user.mobilePhone" disabled></Input>
        </FormItem> -->
        <FormItem prop="code">
          <Input id="verifyCode" type="text" autocomplete="off" v-model="formInline.code" :placeholder="$t('uc.regist.smscode')">
          </Input>
          <input id="sendCode" @click="sendCode();" type="Button" :value="sendcodeValue" :disabled="codeIsSending">
          </input>
        </FormItem>
        <FormItem>
          <Input id="fundPwd" type="password" autocomplete="off" v-model="formInline.fundpwd" :placeholder="$t('otc.chat.msg7')"></Input>
        </FormItem>
      </Form>
      <div slot="footer">
        <span style="margin-right:50px" @click="cancel">取消</span>
        <span style="background:#f0ac19;color:#fff;width:80px;border-radius:30px;display:inline-block;text-align:center;height:30px;line-height: 30px;" @click="ok">确定</span>
      </div>
    </Modal>

    <Modal
        v-model="detailModal"
        title="订单详情"
        @on-ok="ok">
          <p class="ctc-order-status" v-if="detailOrder.direction == 0 && detailOrder.status == 0">订单状态：等待承兑商接单...</p>
          <p class="ctc-order-status" v-if="detailOrder.direction == 0 && detailOrder.status == 1">订单状态：承兑商已接单，等待您付款中</p>
          <p class="ctc-order-status" v-if="detailOrder.direction == 0 && detailOrder.status == 2">订单状态：已付款，等待承兑商放币</p>
          <p class="ctc-order-status" v-if="detailOrder.direction == 0 && detailOrder.status == 3">订单状态：已完成</p>
          <p class="ctc-order-status" v-if="detailOrder.direction == 0 && detailOrder.status == 4">订单状态：已取消({{detailOrder.cancelReason}})</p>
          <p class="ctc-order-status" v-if="detailOrder.direction == 1 && detailOrder.status == 0">订单状态：等待承兑商接单...</p>
          <p class="ctc-order-status" v-if="detailOrder.direction == 1 && detailOrder.status == 1">订单状态：承兑商已接单，正在付款中</p>
          <p class="ctc-order-status" v-if="detailOrder.direction == 1 && detailOrder.status == 2">订单状态：承兑商已付款，确认放币中</p>
          <p class="ctc-order-status" v-if="detailOrder.direction == 1 && detailOrder.status == 3">订单状态：已完成</p>
          <p class="ctc-order-status" v-if="detailOrder.direction == 1 && detailOrder.status == 4">订单状态：已取消({{detailOrder.cancelReason}})</p>
          <Row style="background: #27384a;padding: 10px 0px;border-radius: 5px;">
              <Col span="8">
                <p v-if="detailOrder.direction == 0" class="item-title">买入</p>
                <p v-if="detailOrder.direction == 1" class="item-title">卖出</p>
                <p class="item-desc">订单类型</p>
              </Col>
              <Col span="8">
                <p v-if="detailOrder.direction == 0" class="item-title">{{detailOrder.amount | toFixed(2)}} <span class="unit">USDT</span></p>
                <p v-if="detailOrder.direction == 1" class="item-title">{{detailOrder.amount | toFixed(2)}} <span class="unit">USDT</span></p>
                <p class="item-desc">交易数量</p>
              </Col>
              <Col span="8">
                <p v-if="detailOrder.direction == 0" class="item-title green">{{detailOrder.money | toFixed(2)}} <span class="unit">CNY</span></p>
                <p v-if="detailOrder.direction == 1" class="item-title red">{{detailOrder.money | toFixed(2)}} <span class="unit">CNY</span></p>
                <p class="item-desc">交易总额</p>
              </Col>
          </Row>

          <div style="font-size: 12px;margin-top: 15px;" v-if="detailOrder.direction == 0">
            <Icon type="md-information-circle" style="color:rgb(183, 183, 183);margin-right:5px;font-size:14px;"/>请向以下收款账户汇款/转账： <span class="green" style="font-size: 20px;font-weight:bold;">{{detailOrder.money | toFixed(2)}}</span> <span class="green">CNY</span>

            <div style="float:right;padding: 2px 10px;color:#FF0000;" v-if="orderCountdown > 0 && (detailOrder.status == 0 || detailOrder.status == 1)">
              <Icon type="ios-clock-outline" style="font-weight:bold;font-size:18px;margin-top:-5px;margin-right: 3px;"/>
              <span style="font-size:16px;">{{orderCountdown | countDownFormat}}</span>
            </div>
          </div>

          <div style="font-size: 12px;margin-top: 15px;" v-if="detailOrder.direction == 1">
            <Icon type="md-information-circle" style="color:rgb(183, 183, 183);margin-right:5px;font-size:14px;"/>你的以下账户将收到汇款/转账： <span class="red" style="font-size: 20px;font-weight:bold;">{{detailOrder.money | toFixed(2)}}</span> <span class="red">CNY</span>
          </div>

          <Row style="margin-top: 5px;background: #27384a;padding: 20px 0px;border-radius: 5px;">
              <Col span="24">
                <div style="float:left;margin-left:20px;">
                  <span style="color:rgb(190, 190, 190);font-size:12px;">账户实名：</span>
                  <span style="font-size:14px;color:#FFF;">{{detailOrder.realName}}</span>
                </div>
                <div style="float:right;margin-right: 20px;">
                  <span style="color:rgb(190, 190, 190);font-size:12px;">收款方式：</span>
                  <span style="font-size:14px;color:#FFF;" v-if="detailOrder.payMode == 'bank'">银行卡</span>
                  <span style="font-size:14px;color:#FFF;" v-if="detailOrder.payMode == 'alipay'">支付宝</span>
                  <span style="font-size:14px;color:#FFF;" v-if="detailOrder.payMode == 'wechatpay'">微信支付</span>
                </div>
              </Col>
              <Col span="24" v-if="detailOrder.payMode == 'bank'" style="margin-top: 10px;text-align:left;">
                <div style="float:left;margin-left:20px;width: 100%;">
                  <span style="color:rgb(190, 190, 190);font-size:12px;">开户银行：</span>
                  <span style="font-size:14px;color:#FFF;">{{detailOrder.bankInfo.bank}}</span>
                </div>
                <div style="float:left;margin-left:20px;width: 100%;margin-top: 10px;">
                  <span style="color:rgb(190, 190, 190);font-size:12px;">开户支行：</span>
                  <span style="font-size:14px;color:#FFF;">{{detailOrder.bankInfo.branch}}</span>
                </div>
                <div style="float:left;margin-left:20px;width: 100%;margin-top: 10px;">
                  <span style="color:rgb(190, 190, 190);font-size:12px;">银行卡号：</span>
                  <span style="font-size:16px;color:#FFF;letter-spacing: 3px;font-weight:bold;">{{detailOrder.bankInfo.cardNo}}</span>
                </div>
              </Col>

              <Col span="24" v-if="detailOrder.payMode == 'alipay'" style="margin-top: 10px;text-align:left;">
                <div style="float:left;margin-left:20px;width: 100%;">
                  <span style="color:rgb(190, 190, 190);font-size:12px;">支付宝账号：</span>
                  <span style="font-size:14px;color:#FFF;">{{detailOrder.alipay.aliNo}}</span>
                </div>
                <div style="float:left;margin-left:20px;width: 100%;margin-top: 10px;">
                  <span style="color:rgb(190, 190, 190);font-size:12px;">收款码：</span>
                </div>
                <div style="float:left;margin-left:20px;width: 100%;margin-top: 10px;text-align:center;">
                  <img :src="detailOrder.alipay.qrCodeUrl" style="width: 300px;height:400px;"></img>
                </div>
              </Col>

              <Col span="24" v-if="detailOrder.payMode == 'wechatpay'" style="margin-top: 10px;text-align:left;">
                <div style="float:left;margin-left:20px;width: 100%;">
                  <span style="color:rgb(190, 190, 190);font-size:12px;">微信账号：</span>
                  <span style="font-size:14px;color:#FFF;">{{detailOrder.wechatPay.wechat}}</span>
                </div>
                <div style="float:left;margin-left:20px;width: 100%;margin-top: 10px;">
                  <span style="color:rgb(190, 190, 190);font-size:12px;">收款码：</span>
                </div>
                <div style="float:left;margin-left:20px;width: 100%;margin-top: 10px;text-align:center;">
                  <img :src="detailOrder.wechatPay.qrWeCodeUrl" style="width: 300px;height:400px;"></img>
                </div>
              </Col>
          </Row>

          <div slot="footer">
            <Button v-if="(detailOrder.direction==1 && detailOrder.status==0) || (detailOrder.direction==0 && detailOrder.status < 2)" type="error" size="large" @click="cancelOrderClick">撤消订单</Button>
            <Button v-if="detailOrder.direction==0 && detailOrder.status == 1" type="success" size="large" @click="payOrderClick">标记已付款</Button>
            <Button type="default" size="large" @click="closeDetail">关闭</Button>
          </div>
    </Modal>
  </div>
</template>

<script>
var moment = require("moment");
import expandRow from "@components/exchange/expand.vue";

export default {
  components: { expandRow },
  data() {
    const self = this;
    return {
      payTypeList: [{
        label: this.$t("ctc.bank"),
        value: "bank"
      },{
        label: this.$t("ctc.alipay"),
        value: "alipay"
      },{
        label: this.$t("ctc.wechatpay"),
        value: "wechatpay"
      }],
      receiveTypeList: [{
        label: this.$t("ctc.bank"),
        value: "bank"
      }],
      countdown: 60,
      timer: '',
      orderTimer: "",
      orderCountdown: 0,
      direction: "buy",
      receiveType: "bank",
      payType: "bank",
      buyPrice: 7.00,
      buyAmount: null,
      sellPrice: 7.00,
      sellAmount: null,
      modal: false,
      detailModal: false,
      formInline: {
        code: "",
        fundpwd: ""
      },
      fundpwd: "",
      codeIsSending: false,
      sendcodeValue: this.$t("uc.regist.sendcode"),
      loading: false,
      pageSize: 10,
      pageNo: 1,
      total: 10,
      user: {},
      userAccount: {},
      orders: [],
      detailOrder: {},
      columns: [
        {
          type: "index",
          width: 50,
          render: (h, params) => {
            return h(expandRow, {
              props: {
                skin: params.row.skin,
                rows: params.row.detail
              }
            });
          }
        },
        {
          title: self.$t("ctc.tradetime"),
          key: "createTime",
          minWidth: 65,
          render: (h, params) => {
            return h("span", {}, self.dateFormat(params.row.createTime));
          }
        },
        {
          title: self.$t("ctc.orderSn"),
          key: "orderSn",
          minWidth: 75,
          render: (h, params) => {
            return h("span", {}, params.row.orderSn);
          }
        },
        {
          title: self.$t("ctc.direction"),
          key: "direction",
          minWidth: 55,
          render: (h, params) => {
            let dir = params.row.direction == 0 ? "买入" : "卖出";
            const txtColor = params.row.direction == 0 ? "#42b983" : "#FF0000";
            return h("div", {
                    style:{
                      color: txtColor
                    }
                  }, [
                    h("span", {}, dir)
                  ]);
          }
        },
        {
          title: self.$t("ctc.amount"),
          key: "amount",
          minWidth: 55,
          render: (h, params) => {
            return h("span", {}, params.row.amount);
          }
        },
        {
          title: self.$t("ctc.price"),
          key: "price",
          minWidth: 55,
          render: (h, params) => {
            return h("span", {}, params.row.price);
          }
        },
        {
          title: self.$t("ctc.money"),
          key: "money",
          minWidth: 55,
          render: (h, params) => {
            return h("span", {}, params.row.money.toFixed(2));
          }
        },
        {
          title: self.$t("ctc.status"),
          key: "status",
          minWidth: 95,
          render: (h, params) => {
            let sta = "未接单";
            if(params.row.status == 1 && params.row.direction == 0){
              return h("div", {}, [
                      h("span", {}, "已接单( "),
                      h("span", {
                        style:{
                          color: "#FF0000",
                          fontSize: "10px"
                        }
                      }, "请尽快完成付款"),
                      h("span", {}, " )")
                    ]);
            }
            if(params.row.status == 1 && params.row.direction == 1){
              return h("div", {}, [
                      h("span", {}, "已接单( "),
                      h("span", {
                        style:{
                          color: "#42b983",
                          fontSize: "10px"
                        }
                      }, "承兑商正在付款"),
                      h("span", {}, " )")
                    ]);
            }
            if(params.row.status == 2) sta = "已付款";
            if(params.row.status == 3) sta = "交易完成";
            if(params.row.status == 4) sta = "已取消";
            return h("span", {}, sta);
          }
        },
        {
          title: self.$t("ctc.operate"),
          key: "",
          align: 'center',
          minWidth: 95,
          render: (h, params) => {
            return h("div", [
              h(
                "Button",
                {
                  props: {type: "info",size: "small"},
                  style: {marginRight: "10px"},
                  on: {
                    click: () => {
                      //this.detailOrder = params.row;
                      let rparams = {};
                      rparams.oid = params.row.id;
                      this.$Spin.show();
                      this.$http
                        .post(this.host + "/uc/ctc/detail", rparams)
                        .then(response => {
                          var resp = response.body;
                          this.$Spin.hide();
                          if(resp.code == 0){
                            this.detailOrder = resp.data;
                            this.showDetailModal();
                          }else{
                            this.$Notice.error({
                              title: self.$t("exchange.tip"),
                              desc: resp.message
                            });
                          }
                        });
                    }
                  },
                  key: ''
                },
                "查看详情"
              )
            ]);
          }
        }
      ]
    };
  },
  created: function() {
    this.init();
  },
  filters:{
    dateFormat: function(tick) {
      return moment(tick).format("YYYY-MM-DD HH:mm:ss");
    },
    fixedScale: function(value, scale) {
      return value.toFixed(scale);
    },
    countDownFormat: function(value){
      var m = parseInt(value / 60);
      var s = value % 60;
      if(m < 10) {
        m = '0' + m;
      }
      if(s < 10) {
        s = '0' + s;
      }
      return m + ":" + s;
    }
  },
  mounted () {
    var self = this;
    this.timer = setInterval(()=>{
      self.getC2cPrice();
    },30000);
  },
  beforeDestroy: function() {
      if(this.timer) {
          clearInterval(this.timer);
      }
      if(this.orderTimer) {
          clearInterval(this.orderTimer);
      }
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
    },
    isLogin: function() {
      return this.$store.getters.isLogin;
    },
    totalBuyMoney(){
      return (this.buyPrice * this.buyAmount).toFixed(2);
    },
    totalSellMoney(){
      return (this.sellPrice * this.sellAmount).toFixed(2);
    }
  },
  methods: {
    init() {
      this.$store.commit("navigate", "nav-ctc");
      this.getC2cPrice();
      if(this.isLogin){
        this.getOrderList();
        this.getAccount();
        this.getAccountSecurity();
      }
    },
    cancelOrderClick () {
        this.$Modal.confirm({
            title: '确定取消订单吗',
            content: '<p>您确定要取消该笔订单吗？</p>',
            onOk: () => {
                this.cancelOrder();
            },
            onCancel: () => {

            }
        });
    },
    payOrderClick () {
        this.$Modal.confirm({
            title: '确定您已付款吗？',
            content: '<p>标记已付款前请确认您已付款！</p><p style="color:#FF0000;padding-top:10px;font-size:12px;">注意：对于恶意标记付款的账户，我们将对您的账户进行冻结等限制！</p>',
            onOk: () => {
                this.payOrder();
            },
            onCancel: () => {

            }
        });
    },
    closeDetail: function(){
      this.detailModal = false;
    },
    payOrder: function(){
      let params = {};
      params.oid = this.detailOrder.id;
      this.$Spin.show();
      this.$http
        .post(this.host + "/uc/ctc/pay-ctc-order", params)
        .then(response => {
          var resp = response.body;
          if (resp.code == 0) {
            this.getOrderList();
            this.detailOrder = resp.data;
          } else {
            this.$Notice.error({
              title: that.$t("exchange.tip"),
              desc: resp.message
            });
          }
          this.$Spin.hide();
        });
    },
    cancelOrder: function(){
      let params = {};
      params.oid = this.detailOrder.id;
      this.$Spin.show();
      this.$http
        .post(this.host + "/uc/ctc/cancel-ctc-order", params)
        .then(response => {
          var resp = response.body;
          if (resp.code == 0) {
            this.getOrderList();
            this.detailOrder = resp.data;
          } else {
            this.$Notice.error({
              title: that.$t("exchange.tip"),
              desc: resp.message
            });
          }
          this.$Spin.hide();
        });
    },
    dateFormat: function(tick) {
      return moment(tick).format("YYYY-MM-DD HH:mm:ss");
    },
    loadDataPage(data) {
      this.pageNo = data;
      this.getOrderList();
    },
    getAccountSecurity() {
        //获取个人账户信息
        this.$http.post(this.host + '/uc/approve/security/setting').then(response => {
            var resp = response.body;
            if (resp.code == 0) {
                this.user = resp.data;
            } else {
                this.$Notice.error({
                    title: this.$t("common.tip"),
                    desc: resp.message
                  });
            }
        });
    },
    getAccount() {
        //获取个人账户信息
        this.$http.post(this.host + '/uc/approve/account/setting').then(response => {
            var resp = response.body;
            if (resp.code == 0) {
                this.userAccount = resp.data;
            } else {
                this.$Notice.error({
                    title: this.$t("common.tip"),
                    desc: resp.message
                  });
            }
        })
    },
    getC2cPrice(){
      this.$http
        .post(this.host + "/market/ctc-usdt")
        .then(response => {
          var resp = response.body;
          this.buyPrice = resp.data.buy;
          this.sellPrice = resp.data.sell;
        });
    },
    getOrderList(){
      this.loading = true;
      let params = {};
      params.pageNo = this.pageNo;
      params.pageSize = this.pageSize;
      this.orders = [];
      this.$http
        .post(this.host + "/uc/ctc/page-query", params)
        .then(response => {
          var resp = response.body;
          let rows = [];
          if(resp.code == 0){
            if (resp.data.content.length > 0) {
              this.total = resp.data.totalElements;
              for (var i = 0; i < resp.data.content.length; i++) {
                var row = resp.data.content[i];
                rows.push(row);
              }
              this.orders = rows;
            }
          }
          this.loading = false;
        });
    },
    createOrder(){
      var that = this;
      let params = {};

      if(this.direction == "buy"){
        params.price = this.buyPrice;
        params.amount = this.buyAmount;
        params.payType = this.payType;
        params.direction = 0;
      }else{
        params.price = this.sellPrice;
        params.amount = this.sellAmount;
        params.payType = this.receiveType;
        params.direction = 1;
      }

      params.unit = "USDT";
      params.fundpwd = this.formInline.fundpwd;
      params.code = this.formInline.code;
      this.$Spin.show();
      this.$http
        .post(this.host + "/uc/ctc/new-ctc-order", params)
        .then(response => {
          var resp = response.body;
          if (resp.code == 0) {
            this.getOrderList();
            this.detailOrder = resp.data;
            this.modal = false;
            this.showDetailModal();
          } else {
            this.$Notice.error({
              title: that.$t("exchange.tip"),
              desc: resp.message
            });
          }

          this.$Spin.hide();
        });
    },
    showDetailModal(){
      var self = this;

      if(this.orderTimer != ''){
        clearInterval(this.orderTimer);
      }
      if(this.detailOrder.currentTime != null){
        let currentT = parseInt(new Date(this.detailOrder.currentTime).getTime()/1000);
        if(this.detailOrder.status == 0){
          let endT =  parseInt(new Date(this.detailOrder.createTime).getTime()/1000);
          this.orderCountdown = currentT - endT; //秒
        }else if(this.detailOrder.status == 1){
          let endT =  parseInt(new Date(this.detailOrder.confirmTime).getTime()/1000);
          this.orderCountdown = currentT - endT; //秒
        }

        if(this.orderCountdown < 30*60) {
          this.orderCountdown = 1800 - this.orderCountdown;//倒计时
          this.orderTimer = setInterval(()=>{
            self.orderCountdown--;
            if(self.orderCountdown < 1) {
              clearInterval(self.orderTimer);
            }
          },1000);
        }
      }

      this.detailModal = true;
    },
    cancel() {
      this.modal = false;
      this.formInline.code = "";
      this.formInline.fundpwd = "";
    },
    sendCode() {
      this.$http.post(this.host + "/uc/mobile/ctc/code").then(response => {
        var resp = response.body;
        if (resp.code == 0) {
          this.settime();
          this.$Notice.success({
            title: this.$t("common.tip"),
            desc: resp.message
          });
        } else {
          this.$Notice.error({
            title: this.$t("common.tip"),
            desc: resp.message
          });
        }
      });
    },
    settime() {
      this.sendcodeValue = this.countdown+"s后重新发送";
      this.codeIsSending = true;
      let timercode = setInterval(() => {
        this.countdown--;
        this.sendcodeValue = this.countdown+"s后重新发送";
        if (this.countdown <= 0) {
          clearInterval(timercode);
          this.sendcodeValue = this.$t("uc.regist.sendcode");
          this.countdown = 60;
          this.codeIsSending = false;
        }
      }, 1000);
    },
    ok() {
      if (this.formInline.code == "") {
        this.modal = true;
        this.$Message.error("请填写短信验证码");
        return;
      }
      if (this.formInline.fundpwd == "") {
        this.modal = true;
        this.$Message.error(this.$t("otc.chat.msg7tip"));
        return;
      }
      this.createOrder();
    },
    valid(type){
      if(!this.isLogin){
        this.$Message.error("请先登录！");
        return false;
      }
      if(this.user.realVerified != 1){
        this.$Message.error("请先完成实名认证！");
        return false;
      }
      if(this.user.fundsVerified != 1){
        this.$Message.error("请设置资产交易密码！");
        return false;
      }
      if(type == 0){
        if(this.buyAmount == "" || this.buyAmount == null || this.buyAmount == undefined){
          this.$Message.error("请输入买入数量");
          return false;
        }
        if(this.buyAmount < 50 || this.buyAmount > 50000) {
          this.$Message.error("请输入正确的买入数量");
          return false;
        }
        return true;
      }else{
        if(this.userAccount.bankVerified != 1){
          this.$Message.error("请先绑定银行卡");
          return false;
        }
        if(this.sellAmount == "" || this.sellAmount == null || this.sellAmount == undefined){
          this.$Message.error("请输入买入数量");
          return false;
        }
        if(this.sellAmount < 50 || this.sellAmount > 50000) {
          this.$Message.error("请输入正确的买入数量");
          return false;
        }
        return true;
      }
    },
    buyClick() {
      this.direction = "buy";
      if (this.valid(0)) {
        this.modal = true;
        let timercode = setInterval(() => {
          if (this.countdown <= 0) {
            clearInterval(timercode);
            this.sendcodeValue = this.$t("uc.regist.sendcode");
            this.codeIsSending = false;
          }
        }, 1000);
      }
    },
    sellClick(){
      this.direction = "sell";
      if (this.valid(1)) {
        this.modal = true;
        let timercode = setInterval(() => {
          if (this.countdown <= 0) {
            clearInterval(timercode);
            this.sendcodeValue = this.$t("uc.regist.sendcode");
            this.codeIsSending = false;
          }
        }, 1000);
      }
    }
  }
};
</script>

<style>
.ctc .item-title{
  font-size: 20px;
  text-align: center;
  font-weight: bold;
  color: rgb(188, 188, 188);
}
.ctc .red{
  color: #f2334f;
}
.ctc .green{
  color: #45b854;
}
.ctc .item-title .unit{
  font-size: 14px;
}
.ctc .item-desc{
  font-size: 12px;
  text-align: center;
  color: #7c7f82;
}
.ctc .notice-bottom{
  margin-top: 5px;height: 55px;background-color:#192330;padding-top: 12px;color: rgb(42, 147, 255);
}
.ctc .notice-btn-left{
  height: 30px;line-height: 30px;width: 42%;margin-left: 5%;float:left;border-radius:3px;border: 1px solid rgb(0, 116, 235);
}
.ctc .notice-btn-left:hover{
  cursor: pointer;
}
.ctc #sendCode {
  position: absolute;
  border: none;
  background: none;
  top: 6px;
  outline: none;
  right: 0;
  width: 30%;
  color: #f0ac19;
  cursor: pointer;
  height: 20px;
  line-height: 20px;
  border-left: 1px solid #dddee1;
}
.ctc .notice-btn-right{
  height: 30px;line-height: 30px;width: 42%;margin-right: 5%;float:right;border-radius:3px;border: 1px solid rgb(0, 116, 235);
}
.ctc .notice-btn-right:hover{
  cursor: pointer;
}
.ctc .ivu-tabs-bar{
    border-bottom: 1px solid #323c53;
    font-size: 18px;
}
.ctc .ivu-tabs-nav .ivu-tabs-tab:hover{
    color: #f0a70a;
}
.ctc .ivu-tabs-nav .ivu-tabs-tab:hover, .ctc .ivu-tabs-nav .ivu-tabs-tab-active{
    color: #f0a70a;
    font-size: 18px;
}
.ctc .ivu-tabs-ink-bar{
    background-color: #f0a70a;
}
.ctc .buy_total{
  border-top: 1px solid #323c53;
  padding-top: 30px;
  margin-bottom: 30px;
}
.ctc .trade_bd_ctc{
  width: 70%;
}
.ctc .trade_bd_ctc .panel {
    position: relative;
    z-index: 2;
    float: left;
    width: 49%;
    height: 455px;
    margin-top: 0;
    margin-right: 0;
    border: 0 solid transparent;
    padding-top: 35px;
}

.ctc .trade_panel{
  background: transparent!important;
}
.ctc .trade_panel .panel .hd {
    line-height: 20px;
    height: 20px;
    border-bottom: 1px solid #1F2943;
    margin-bottom: 5px;
}

.ctc .trade_panel .panel .hd span {
    padding-left: 0;
    font-size: 12px;
    margin: 0 3px;
    float:right;
}
.ctc-order-status{
  text-align:center;margin-bottom: 15px;background: #f0a70a;padding: 5px 0px;border-radius: 2px;color: #000000;
}
.ctc .trade_panel .panel .hd b {
    padding-left: 0;
    font-size: 12px;
    color: #7A98F7;
    float:right;
}

.ctc .trade_panel .panel .hd.hd_login a {
    float: right;
    text-decoration: none;
    font-size: 12px;
    margin-right: 10px;
}

.ctc .trade_panel .panel.panel_buy {
    padding-right: 35px;
    padding-left: 35px;
    background: #192330;
}

.ctc .trade_panel .panel.panel_sell {
    padding-right: 35px;
    padding-left: 35px;
    background: #192330;
    margin-left: 5px;
}
.ctc .trade_wrap .buy-input .ivu-input {
  color: #45b854;
  font-weight: bold;
  font-size: 22px;
  height: 40px;
}
.ctc .trade_wrap .sell-input .ivu-input {
  color: #f2334f;
  font-weight: bold;
  font-size: 22px;
  height: 40px;
}
.ctc .trade_wrap .trade-input .ivu-input {
    border: 1px solid #27313e;
    color: #fff;
    height: 40px;
    text-indent: 25px;
    border-radius: 0;
}

.ctc .trade_wrap .ivu-input-wrapper {
    outline: none;
}

.ctc .trade_wrap .ivu-input:focus,
.ctc .trade_wrap .ivu-input:hover {
    box-shadow: none;
    outline: none;
}
.ctc .trade_wrap .ivu-input-number-input:focus,
.ctc .trade_wrap .ivu-input-number-input:hover {
    box-shadow: none;
    border-color: #41546d;
    outline: none;
}

.ctc .trade_wrap .ivu-input:hover {
    box-shadow: none;
    outline: none;
}
.ctc .trade_wrap .ivu-input-number-input:hover {
    box-shadow: none;
    border-color: #41546d;
    outline: none;
}
.ctc .trade_wrap .ivu-form-item-content input{
    padding-left: 50px;
    text-align:right;
    padding-right: 55px;
    font-size: 20px;
}
.ctc .trade_wrap .ivu-form-item-content input::-webkit-input-placeholder {
    font-size: 12px;
    color: #515a6e;
    margin-bottom: 10px;
}
.ctc .trade_wrap .ivu-form-item-content label.before {
    position: absolute;
    top: 4px;
    left: 10px;
    color: #7c7f82;
    z-index: 2;
    font-size: 14px;
}

.ctc .trade_wrap .ivu-form-item-content label.after {
    position: absolute;
    top: 4px;
    right: 10px;
    color: #7c7f82;
    font-size: 14px;
}
.trade_bd_ctc Button {
    width: 100%;
    border: 0;
    color: #fff;
}

.trade_bd_ctc Button.bg-red {
    background-color: #f15057;
}
.trade_bd_ctc Button.bg-red:hover {
    background-color: #ff7278;
}

.trade_bd_ctc Button.bg-green {
    background-color: #00b275;
}
.trade_bd_ctc Button.bg-green:hover {
    background-color: #01ce88;
}

.trade_bd_ctc Button.bg-gray {
    background-color: #35475b;
    cursor: not-allowed;
    color: #9fabb5;
}
.trade_bd_ctc Button.bg-gray:hover{
    color: #9fabb5!important;
}
.trade_bd_ctc Button:hover {
    /* background: #54679F; */
}
.ctc .trade_wrap .ivu-btn{
  color: #FFF!important;
}
.ctc .total{
  min-height: 90px;
}
</style>
<style lang="scss" scoped>
  .ctc {
    height: 100%;
    background-size: cover;
    position: relative;
    overflow: hidden;
    padding-bottom: 50px;
    padding-top: 60px;
    color: #fff;
  }
  .ctc .bannerimg {
    display: block;
    width: 100%;
  }
  .ctc_container {
    padding: 0 5%;
    text-align: center;
    height: 100%;
    > h1 {
      margin-top: -170px;
      font-size: 32px;
      line-height: 1;
      padding: 50px 0 20px 0;
      letter-spacing: 3px;
    }
  }
  .ctc .main {
    margin-top: 55px;
    display: flex;
    justify-content: space-between;
    flex-direction: row;
    flex-wrap: wrap;
  }
  .ctc-container{
    min-height: 470px;
  }
  .bottom-panel{
      border-top: 1px solid rgb(237, 237, 237);margin-top: 15px;
      .bottom{
        display: flex;
        flex-direction: row;
        justify-content: space-between;
        span{
          font-size: 12px;
          color: #a7a7a7;
          margin-top:15px;
        }
        button, a{
          margin-top: 11px;
        }
        a.ivu-btn-primary{
          background:#0095ff;
        }
        a.ivu-btn-primary:hover{
          background: #2ba7ff;
        }
      }
  }
  .right{
    float: right;
  }
  .left{
    float: left;
  }
  .gray{
    color: #a7a7a7;
  }
</style>
