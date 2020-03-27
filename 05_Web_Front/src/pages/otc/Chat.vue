<template>
    <div class="content-wrap">
        <div class="container chat-in-box" id="List">
            <p style="padding: 10px 0 10px 20px;font-size: 16px;">
              <router-link to="/uc/order" style="color:#f0a70a;">{{$t('otc.myorder')}}</router-link> ><span style="font-size:14px;">订单详情</span>
              </p>
            <Row class="chat-in">
                <Col span="4">
                <div class="leftmenu left-box chat-right">
                    <div class="chat-right-in">
                        <h6 class="h6-flex">
                            <span v-if="tradeType==0">{{$t('otc.chat.seller')}}:</span>
                            <span v-else>{{$t('otc.chat.buyer')}}:</span>
                            <router-link :to="{ path: '/checkuser', query: { 'id': msg.otherSide }}">
                            <!-- {{msg.otherSide && msg.otherSide.length>2 && strpro(msg.otherSide)}} -->
                            {{msg.otherSide}}
                            </router-link>
                        </h6>
                              <Poptip v-if="tradeType==0&&msg.memberMobile" class="pop-tel" :content="msg.memberMobile">
                                <img src="../../assets/images/icon-tel.png" alt="">
                              </Poptip>
                        <h6>
                            <span>{{$t('otc.chat.exchangeamount')}}:</span>
                            <span>{{msg.money}}&nbsp;CNY</span>
                        </h6>
                        <div class="mt20" v-if="tradeType==0">
                            <h5>{{$t('otc.chat.operatetip')}}:</h5>
                            <div>
                                <p>1、{{$t('otc.chat.operatetip_1')}}“
                                    <em>{{$t('otc.chat.finishpayment')}}</em>”。{{$t('otc.chat.operatetip_1_1')}}。</p>
                                <p>2、{{$t('otc.chat.operatetip_1_2')}}。</p>
                            </div>
                            <span>
                                <b>{{$t('otc.chat.note')}}：</b>
                            </span><br>
                            <span>{{$t('otc.chat.notetip')}}</span><br>
                        </div>
                        <div class="mt20" v-else>
                            <h5>{{$t('otc.chat.operatetip')}}:</h5>
                            <div>
                                <p>1、{{$t('otc.chat.operatetip_2_1')}}“
                                    <em>{{$t('otc.chat.confirmrelease')}}</em>”{{$t('otc.chat.paydigital')}}！</p>
                                <p>2、{{$t('otc.chat.operatetip_2_2')}}</p>
                                <p>3、{{$t('otc.chat.operatetip_2_3')}}</p>
                            </div>
                            <span>
                                <b>{{$t('otc.chat.note')}}：</b>
                            </span><br>
                            <span>{{$t('otc.chat.notetip')}}</span><br>
                        </div>
                        <div class="bottom-btn">
                            <div style="padding-top:20px;">
                                <h6 style="font-weight: 600">{{$t('otc.chat.orderstatus')}}:
                                    <span>{{statusText}}</span>
                                </h6>
                                <div v-show="statusBtn==1&&tradeType==0">
                                    <Button type="warning" @click="modal1 = true">{{$t('otc.chat.orderstatus_1')}}</Button>
                                    <Button @click="modal3 = true" type="error">{{$t('otc.chat.orderstatus_4')}}</Button>
                                </div>
                                <div v-show="statusBtn==2&&tradeType==0">
                                    <Button type="warning" @click="appearOrder">{{$t('otc.chat.orderstatus_2')}}</Button>
                                    <Button @click="modal3 = true" type="error">{{$t('otc.chat.orderstatus_4')}}</Button>
                                </div>
                                <div v-show="statusBtn==2&&tradeType==1">
                                    <Button type="warning" @click="modal5 = true">{{$t('otc.chat.orderstatus_3')}}</Button>
                                    <Button @click="appearOrder" type="error">{{$t('otc.chat.orderstatus_5')}}</Button>
                                </div>
                                <!-- <Button type="primary" v-show="statusBtn==2" @click="modal4 = true" long></Button> -->
                            </div>
                        </div>
                    </div>
                </div>
                </Col>
                <Col span="20">
                <div class="rightbox">
                    <Row class="chat-top" type="flex" justify="space-between">
                        <Col span="3" class="order-time">
                        <h5>{{statusText}}</h5>
                        <div v-show="statusBtn==1" class="reserve-time">{{reserveTime}}</div>
                        </Col>
                        <Col span="8" class="order-info">
                        <h5>
                            <label class="order-name">{{$t('otc.chat.order')}}</label>
                            <span>{{msg.orderSn}}</span>
                        </h5>
                        <p>
                            {{$t('otc.chat.and')}}
                            <router-link :to="{ path: '/checkuser', query: { 'id': msg.otherSide }}">
                            <!-- {{msg.otherSide && msg.otherSide.length>2 && strpro(msg.otherSide)}} -->
                            {{msg.otherSide}}
                            </router-link>
                            {{$t('otc.chat.transition')}}
                        </p>
                        </Col>
                        <Col span="3" class="order-info">
                        <h5>{{msg.price}}</h5>
                        <span>{{$t('otc.chat.transprice')}}(CNY)</span>
                        </Col>
                        <Col span="3" class="order-info">
                        <h5>{{msg.amount}}</h5>
                        <span>{{$t('otc.chat.transnum')}}({{msg.unit}})</span>
                        </Col>
                        <Col span="3" class="order-info">
                        <h5>{{msg.money}}</h5>
                        <span>{{$t('otc.chat.transmoney')}}(CNY)</span>
                        </Col>
                    </Row>
                    <Row class="chat-top" type="flex" justify="space-between" v-show="statusBtn!=0">
                        <Col span="8" class="order-info" v-if="bankInfo&&bankInfo!=null">
                        <i class="icons bankfor"></i>
                        <span>{{payInfo != null ? payInfo.realName : ""}} </span>
                        <p>{{bankInfo.branch}}</p>
                        <p>{{bankInfo.cardNo}}</p>
                        </Col>
                        <Col span="8" class="order-info" v-else>
                        <i class="icons bankfor"></i>
                        <pre></pre>
                        <p style="color:rgb(145, 143, 143)">{{$t('otc.chat.tip1')}}</p>
                        </Col>
                        <Col span="8" class="order-info" v-if="alipay&&alipay!=null">
                        <i class="icons alipay"></i>
                        <span>{{$t('otc.chat.zfb')}}</span>
                        <pre></pre>
                        <p>{{alipay.aliNo}}</p>
                        <p v-if="alipay&&alipay!=null&&alipay.qrCodeUrl!=null&&alipay.qrCodeUrl!=''"><a @click="showQRCode(1)">{{$t('otc.chat.qrcode')}}</a></p>
                        </Col>
                        <Col span="8" class="order-info" v-else>
                        <i class="icons alipay"></i>
                        <pre></pre>
                        <p style="color:rgb(145, 143, 143)">{{$t('otc.chat.tip2')}}</p>
                        </Col>
                        <Col span="8" class="order-info" v-if="wechatPay&&wechatPay!=null">
                        <i class="icons wechat"></i>
                        <span>{{$t('otc.chat.wx')}}</span>
                        <pre></pre>
                        <p>{{wechatPay.wechat}}</p>
                        <p v-if="wechatPay&&wechatPay!=null&&wechatPay.qrWeCodeUrl!=null&&wechatPay.qrWeCodeUrl!=''"><a @click="showQRCode(2)">{{$t('otc.chat.qrcode')}}</a></p>
                        </Col>
                        <Col span="8" class="order-info" v-else>
                        <i class="icons wechat"></i>
                        <pre></pre>
                        <p style="color:rgb(145, 143, 143)">{{$t('otc.chat.tip3')}}</p>
                        </Col>

                    </Row>
                    <chatline :msg="msg"></chatline>
                </div>
                </Col>
            </Row>
        </div>
        <Modal v-model="modal1" :title="$t('otc.chat.tip')" @on-ok="ok1">
            <p style="color:red;font-weight: bold;">{{$t('otc.chat.msg1')}}</p>
        </Modal>
        <!-- <Modal v-model="modal2" :title="$t('otc.chat.tip')" @on-ok="ok2" :loading="isloading">
            <p style="color:red;font-weight: bold;">{{$t('otc.chat.msg2')}}</p>
        </Modal> -->
        <Modal v-model="modal3" :title="$t('otc.chat.tip')" @on-ok="ok3">
            <p style="color:red;font-weight: bold;">{{$t('otc.chat.msg3')}}</p>
        </Modal>
        <Modal v-model="modal4" :title="$t('otc.chat.tip')" @on-ok="ok4">
            <Form :model="formItem" :label-width="80">
                <FormItem :label="$t('otc.chat.comptype')">
                    <Select v-model="formItem.select">
                        <Option value="1">{{$t('otc.chat.msg4')}}</Option>
                        <Option value="2">{{$t('otc.chat.msg5')}}</Option>
                    </Select>
                </FormItem>
                <FormItem :label="$t('otc.chat.compremark')">
                    <Input v-model="formItem.remark" type="textarea" :autosize="{minRows: 2,maxRows: 5}" :placeholder="$t('otc.chat.willcomp')"></Input>
                </FormItem>
            </Form>
        </Modal>
        <Modal v-model="modal5" :title="$t('otc.chat.tip')" @on-ok="ok5">
            <P style="color:red;font-weight: bold;">
              {{$t('otc.chat.msg6')}}<br/>
              <Input type="password" v-model="fundpwd" :placeholder="$t('otc.chat.msg7')"></Input>
            </p>

        </Modal>
        <Modal v-model="modal6">
            <p slot="header">
            </p>
            <div style="text-align:center">
              <img style="width: 200px;" :src="payCodeUrl">
            </div>
            <div slot="footer"></div>
        </Modal>
    </div>
</template>
<script>
var Stomp = require("stompjs");
var SockJS = require("sockjs-client");
import chatline from "../../components/otc/Chatline";
export default {
  components: {
    chatline
  },
  data() {
    return {
      watching: false,
      stompClient: null,
      reserveTime: "60",
      reserveInteval: null,
      fundpwd: "",
      statusBtn: 0,
      tradeType: 0,
      isloading: true,
      payTime: "",
      statusText: "",
      modal1: false,
      modal2: false,
      modal3: false,
      modal4: false,
      modal5: false,
      modal6: false,
      formItem: {
        select: "",
        remark: ""
      },
      msg: {},
      payInfo: {},
      bankInfo: {},
      alipay: {},
      wechatPay: {},
      payCodeUrl: ""
    };
  },
  created() {
    this.getDetail();
    this.initScok();
  },
  computed: {},
  methods: {
    //让浏览器滚动条保持在最低部
    scrollToBottom: function() {
      // window.scrollTo(0, 900000);
    },
    initScok: function() {
      var socket = new SockJS(this.host + "/chat/chat-webSocket");
      this.stompClient = Stomp.over(socket);
      this.stompClient.debug = false;
    },
    watchOrderStutusNotice: function() {
      var self = this;
      this.stompClient.connect({}, function(frame) {
        self.stompClient.subscribe(
          "/user/" +
            self.msg.myId +
            "/order-notice/" +
            self.$route.query.tradeId,
          function(response) {
            if (self.reserveInteval != null) clearInterval(self.reserveInteval);
            var confirmPayMsg = JSON.parse(response.body);
            self.$Message.success(confirmPayMsg.content);
            self.statusBtn = confirmPayMsg.status;
            if (confirmPayMsg.status == 1) {
              self.statusText = self.$t("otc.chat.result_1");
              self.setReserveTime();
            } else if (confirmPayMsg.status == 2) {
              self.statusText = self.$t("otc.chat.result_2");
            } else if (confirmPayMsg.status == 3) {
              self.statusText = self.$t("otc.chat.result_3");
            } else if (confirmPayMsg.status == 4) {
              self.statusText = self.$t("otc.chat.result_4");
            } else if (confirmPayMsg.status == 0) {
              self.statusText = self.$t("otc.chat.result_5");
            }
          }
        );
      });
    },
    sendOrderStatusNotice: function(type) {
      if (this.reserveInteval != null) clearInterval(this.reserveInteval);
      var content = "";
      if (type == 1) content = "对方已付款，请查收并确认放行!";
      else if (type == 3) content = "对方已取消订单!";
      else if (type == 4) content = "对方已申诉!";
      else if (type == 5) content = "对方已放行,请查收!";
      var jsonParam = {
        uidTo: this.msg.hisId,
        uidFrom: this.msg.myId,
        orderId: this.$route.query.tradeId,
        content: content,
        messageType: 0
      };
      this.stompClient.send("/app/message/chat", {}, JSON.stringify(jsonParam));
    },
    showQRCode: function(type) {
      if (type == 1) {
        this.payCodeUrl = this.alipay.qrCodeUrl;
      } else {
        this.payCodeUrl = this.wechatPay.qrWeCodeUrl;
      }
      this.modal6 = true;
    },
    setReserveTime: function() {
      this.getReserveTime();
      this.reserveInteval = setInterval(() => {
        this.getReserveTime();
      }, 1000);
    },
    getReserveTime: function() {
      var d1 = new Date().getTime();
      var d2 = new Date(this.msg.createTime.replace(/-/g,"/")).getTime();
      var throughSeconds = parseInt(parseInt(d1 - d2) / 1000);
      var reserveSeconds = parseInt(this.msg.timeLimit) * 60 - throughSeconds;
      this.reserveTime =
        parseInt(reserveSeconds / 60) +
        ":" +
        (parseInt(reserveSeconds % 60) >= 10
          ? parseInt(reserveSeconds % 60)
          : "0" + parseInt(reserveSeconds % 60));
      if (reserveSeconds <= 0) {
        this.resetStatus();
      }
    },
    resetStatus: function() {
      //计时时间已到，重置状态
      clearInterval(this.reserveInteval);
      this.statusBtn = 5;
      this.ok3();
    },
    appearOrder: function() {
      var nowTime = new Date().getTime();
      var payTime = new Date(this.msg.payTime).getTime();
      if (parseInt((nowTime - payTime) / 1000) < 1800) {
        //付款时间小于30分钟不允许申诉
        this.$Message.info("付款完成30分钟后才允许申诉!");
        return;
      } else {
        this.modal4 = true;
      }
    },
    ok1() {
      this.$http
        .post(this.host + "/otc/order/pay", {
          orderSn: this.$route.query.tradeId
        })
        .then(response => {
          var resp = response.body;
          if (resp.code == 0) {
            this.$Message.success(resp.message);
            this.sendOrderStatusNotice(1);
            this.getDetail();
          } else {
            this.$Message.error(resp.message);
          }
        });
    },
    ok2() {
      this.modal2 = false;
        this.modal3 = true;
        return;
      setTimeout(() => {
        this.isloading = false;
        this.modal2 = false;
        this.modal3 = true;
      }, 10000);
    },
    ok3() {
      this.$http
        .post(this.host + "/otc/order/cancel", {
          orderSn: this.$route.query.tradeId
        })
        .then(response => {
          var resp = response.body;
          if (resp.code == 0) {
            this.$Message.success(resp.message);
            this.sendOrderStatusNotice(3);
            this.getDetail();
          } else {
            this.$Message.error(resp.message);
          }
        });
    },
    ok4() {
      //订单申诉
      //时间
      if (1 == 1) {
        var params = {};
        params["orderSn"] = this.$route.query.tradeId;
        params["remark"] = this.formItem.remark;

        this.$http
          .post(this.host + "/otc/order/appeal", params)
          .then(response => {
            var resp = response.body;
            if (resp.code == 0) {
              this.$Message.success(resp.message);
              this.sendOrderStatusNotice(4);
              this.getDetail();
            } else {
              this.$Message.error(resp.message);
            }
          });
      }
    },
    ok5() {
      var params = {};
      params["orderSn"] = this.$route.query.tradeId;
      params["jyPassword"] = this.fundpwd;
      if (this.fundpwd == "") {
        this.$Message.error(this.$t("otc.chat.msg7tip"));
        return;
      }
      this.$http
        .post(this.host + "/otc/order/release", params)
        .then(response => {
          var resp = response.body;
          if (resp.code == 0) {
            this.$Message.success(resp.message);
            this.sendOrderStatusNotice(5);
            this.getDetail();
          } else {
            this.$Message.error(resp.message);
          }
        });
    },
    getDetail() {
      //获取个人广告
      this.$http
        .post(this.host + "/otc/order/detail", {
          orderSn: this.$route.query.tradeId
        })
        .then(response => {
          var resp = response.body;
          if (resp.code == 0) {
            this.msg = resp.data;
            this.payInfo = this.msg.payInfo;
            if (this.payInfo == null) {
              this.bankInfo = this.alipay = this.wechatPay == null;
            } else {
              this.bankInfo = this.msg.payInfo.bankInfo;
              this.alipay = this.msg.payInfo.alipay;
              this.wechatPay = this.msg.payInfo.wechatPay;
            }

            if (!this.watching) {
              this.watchOrderStutusNotice();
              this.watching = true;
            }

            this.statusBtn = resp.data.status;
            this.tradeType = resp.data.type;
            if (resp.data.status == 1) {
              this.statusText = this.$t("otc.chat.result_1");
              this.setReserveTime();
            } else if (resp.data.status == 2) {
              this.statusText = this.$t("otc.chat.result_2");
            } else if (resp.data.status == 3) {
              this.statusText = this.$t("otc.chat.result_3");
            } else if (resp.data.status == 4) {
              this.statusText = this.$t("otc.chat.result_4");
            } else if (resp.data.status == 0) {
              this.statusText = this.$t("otc.chat.result_5");
            }
          } else {
            this.$Message.error(resp.message);
          }
        });
    },
    strpro(str){
      let newStr = str;
      str = str.slice(1);
      var re = /[\D\d]*/g;  
      var str2 = str.replace(re,function(str){
            var result = '';
            for(var i=0;i<str.length;i++){
                result += '*';
            }              
            return result; 
        });
      return newStr.slice(0,1)+str2;
    }
  }
};
</script>

<style>
.chat-in .ivu-col.ivu-col-span-4 .ivu-poptip-popper{
  margin-top: 35px;
}
.chat-in .ivu-col.ivu-col-span-4 .ivu-poptip-title{
  display: none;
}
</style>

<style scoped>

.pop-tel{
  position: absolute;
    top: 50px;
    right: 10px;
    width: 25px;
    height: 25px;
    z-index: 100;
}
.pop-tel img{
  width: 100%;
  height: 100%;
}
.chat-in-box .chat-in .chat-right .chat-right-in h6.h6-flex{
  display: flex;
  overflow: auto;
  min-width:auto;
  white-space:normal;
}
.h6-flex>span{
  flex: 0 0 40px;
}
.h6-flex>a{
  flex: 1 1 100%;
  width: 100%;
}
/* right */
.reserve-time {
  color: #ed3f14;
  font-weight: 700;
}

.rightbox {
  background: #192330;
  margin-left: 20px;
  padding-bottom: 20px;
  margin-bottom: 40px;
}

.chat-top {
  padding: 30px 0;
  font-size: 14px;
}

.order-time h5 {
  font-size: 16px;
  line-height: 40px;
}

.order-info h5 {
  font-weight: 600;
  font-size: 14px;
}
.order-info p a{
  color: #f0a70a;
}
.icons.alipay {
  background-image: url(../../assets/img/alipay.png);
}

.icons.wechat {
  background-image: url(../../assets/img/wechat.png);
}

.icons.qrcode {
  background-image: url(../../assets/images/wechats.png);
}

.icons {
  height: 17px;
  width: 17px;
  display: inline-block;
  margin-top: -1px;
  background-size: 100% 100%;
  vertical-align: middle;
}

.bankfor {
  background-image: url(../../assets/img/bankcard.png);
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
  /* background: white; */
}
/* chat */

/* left */

.mt20 {
  margin-top: 20px;
}

.leftmenu {
  margin-bottom: 60px;
  background-color: #192330;
  position: relative;
  min-height: 1px;
  padding: 50px 15px 50px 15px;
  text-align: left;
}

.chat-in-box .chat-in .chat-right .chat-right-in {
  /* background-color: white; */
}

.chat-in-box .chat-in .chat-right .chat-right-in h6 {
  font-size: 14px;
  font-weight: 500;
  color: #fff;
  min-width: 195px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-bottom: 8px;
}

.chat-in-box .chat-in .chat-right .chat-right-in .seller {
  margin-left: 6px;
}

.chat-in-box .chat-in .chat-right .chat-right-in h6 span {
  margin-left: 6px;
}
.chat-in-box .chat-in .chat-right .chat-right-in h6 a{
  color: #f0a70a;
}
.chat-in-box .chat-in .chat-right .chat-right-in p {
  color: #ccc;
  font-size: 12px;
  margin-bottom: 8px;
  line-height: 1.5;
}

.chat-in-box .chat-in .chat-right .chat-right-in p em {
  color: #f40a0a;
  font-style: normal;
}

/* -- */

.content-wrap {
  /* background: #f5f5f5; */
  min-height: 515px;
}

.container {
  /*padding-top: 30px;*/
  margin: 0 auto;
}
</style>

