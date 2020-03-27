<template>
    <div class="activity">
        <img v-if="activityDetail.bannerImageUrl != ''" class="activity-detail-bannerimg" :src="activityDetail.bannerImageUrl">
        <div class="activity_container">
            <Row :gutter="20" style="min-height: 600px;">
                <Col :xs="24" :sm="24" :md="16" :lg="16" style="min-height: 600px;margin-bottom:10px;">
                    <div class="left-container">
                        <div class="title">
                            <img  :src="activityDetail.smallImageUrl"></img>
                            <div class="title-text">
                                <h1>{{activityDetail.title}}</h1>
                                <p>{{activityDetail.detail}}</p>
                            </div>
                        </div>
                        <div class="content-wrap" >
                            <div class="content">
                              <div class="content-text" v-html="activityDetail.content"></div>
                            </div>
                        </div>
                    </div>
                </Col>
                <Col :xs="24" :sm="24" :md="8" :lg="8">
                    <div class="right-container">
                        <p class="base">{{$t('activity.baseinfo')}}</p>
                        <div class="progress">
                            <div class="progress-text" style="margin-bottom: -3px;">
                              <span class="gray">{{$t('activity.progress')}}</span>
                              <span class="gray">{{$t('activity.totalsupply')}}</span>
                            </div>
                            <Progress :percent="activityDetail.progress" status="active" style="width: 100%;" hide-info :stroke-width="5"/>
                            <div class="progress-text">
                              <span>{{activityDetail.progress | fixedScale(2)}}%</span>
                              <span>{{activityDetail.totalSupply | fixedScale(activityDetail.amountScale)}} {{activityDetail.unit}}</span>
                            </div>
                        </div>
                        <div class="info">
                            <div class="info-item">
                                <p class="title gray">{{$t('activity.status')}}</p>
                                <p class="value" v-if="activityDetail.step == 0">{{$t('activity.prepare')}}</p>
                                <p class="value" v-if="activityDetail.step == 1">{{$t('activity.ongoing')}}</p>
                                <p class="value" v-if="activityDetail.step == 2">{{$t('activity.tokendistribute')}}</p>
                                <p class="value" v-if="activityDetail.step == 3">{{$t('activity.completed')}}</p>
                            </div>
                            <div class="info-item">
                                <p class="title gray">{{$t('activity.activitytype')}}</p>
                                <p class="value" style="color:#F90;" v-if="activityDetail.type==1">{{$t('activity.activitytype1')}} <router-link to="/helpdetail?cate=1&id=33&cateTitle=常见问题" target="_blank">{{$t('activity.ruledetail')}}</router-link></p>
                                <p class="value" style="color:#F90;" v-if="activityDetail.type==2">{{$t('activity.activitytype2')}} <router-link to="/helpdetail?cate=1&id=32&cateTitle=常见问题" target="_blank">{{$t('activity.ruledetail')}}</router-link></p>
                                <p class="value" style="color:#F90;" v-if="activityDetail.type==3">{{$t('activity.activitytype3')}} <router-link to="/helpdetail?cate=1&id=36&cateTitle=常见问题" target="_blank">{{$t('activity.ruledetail')}}</router-link></p>
                                <p class="value" style="color:#F90;" v-if="activityDetail.type==4">{{$t('activity.activitytype4')}} <router-link to="/helpdetail?cate=1&id=37&cateTitle=常见问题" target="_blank">{{$t('activity.ruledetail')}}</router-link></p>
                                <p class="value" style="color:#F90;" v-if="activityDetail.type==5">{{$t('activity.activitytype5')}}</p>
                            </div>
                            <div class="info-item">
                                <p class="title gray">{{$t('activity.totalsupply')}}</p>
                                <p class="value">{{activityDetail.totalSupply | fixedScale(activityDetail.amountScale)}}<span style="font-size:12px;margin-left:5px;">{{activityDetail.unit}}</span></p>
                            </div>
                            <div class="info-item" v-if="activityDetail.type != 3">
                                <p class="title gray">{{$t('activity.publishprice')}}</p>
                                <p class="value">{{activityDetail.price | fixedScale(activityDetail.priceScale)}}<span style="font-size:12px;margin-left:5px;">{{activityDetail.acceptUnit}}</span></p>
                            </div>
                            <div class="info-item" v-if="activityDetail.type != 5">
                                <p class="title gray">{{$t('activity.activitycoin')}}</p>
                                <p class="value">{{activityDetail.unit}}</p>
                            </div>
                            <div class="info-item">
                                <p class="title gray" v-if="activityDetail.type!=3">{{$t('activity.acceptcoin')}}</p>
                                <p class="title gray" v-if="activityDetail.type==3">{{$t('activity.holdcoin')}}</p>
                                <p class="value">{{activityDetail.acceptUnit}}</p>
                            </div>
                            <div class="info-item">
                                <p class="title gray">{{$t('activity.limittimes')}}</p>
                                <p class="value" v-if="activityDetail.limitTimes > 0">{{activityDetail.limitTimes}}次</p>
                                <p class="value" v-else>{{$t('activity.unlimited')}}</p>
                            </div>
                            <div class="info-item">
                                <p class="title gray">{{$t('activity.limitamount')}}</p>
                                <p class="value" v-if="activityDetail.minLimitAmout > 0 && activityDetail.maxLimitAmout > 0">
                                    {{activityDetail.minLimitAmout | fixedScale(activityDetail.amountScale)}} ~ {{activityDetail.maxLimitAmout | fixedScale(activityDetail.amountScale)}}
                                </p>

                                <p class="value" v-if="activityDetail.minLimitAmout > 0 && activityDetail.maxLimitAmout == 0"> ≥  {{activityDetail.minLimitAmout | fixedScale(activityDetail.amountScale)}}</p>

                                <p class="value" v-if="activityDetail.minLimitAmout == 0 && activityDetail.maxLimitAmout > 0"> ≤  {{activityDetail.maxLimitAmout | fixedScale(activityDetail.amountScale)}}</p>

                                <p class="value" v-if="activityDetail.minLimitAmout == 0 && activityDetail.maxLimitAmout == 0"> 不限 </p>
                            </div>

                            <div class="info-item" v-if="activityDetail.leveloneCount > 0">
                                <p class="title gray">{{$t('activity.leveloneCount')}}</p>
                                <p class="value" v-if="activityDetail.leveloneCount > 0">{{activityDetail.leveloneCount}}</p>
                                <p class="value" v-else>{{$t('activity.unlimited')}}</p>
                            </div>
                            <div class="info-item">
                                <p class="title gray">{{$t('activity.starttime')}}</p>
                                <p class="value">{{activityDetail.startTime | dateFormat}}</p>
                            </div>
                            <div class="info-item">
                                <p class="title gray">{{$t('activity.endtime')}}</p>
                                <p class="value">{{activityDetail.endTime | dateFormat}}</p>
                            </div>
                        </div>
                        <!-- 仅自由申购类型显示已兑/剩余数量 -->
                        <div class="tips" v-if="activityDetail.type == 4 || activityDetail.type == 5">
                            <div class="left-tip">
                                <p class="title gray">{{$t('activity.alreadyamount')}}</p>
                                <p>{{activityDetail.tradedAmount | fixedScale(activityDetail.amountScale)}} {{activityDetail.unit}}</p>
                            </div>
                            <div class="right-tip">
                                <p class="title gray">{{$t('activity.leftamount')}}</p>
                                <p>{{(activityDetail.totalSupply - activityDetail.tradedAmount) | fixedScale(activityDetail.amountScale)}} {{activityDetail.unit}}</p>
                            </div>
                        </div>
                        <!-- 持仓瓜分类型显示冻结参与者 -->
                        <div class="tips flexcolumn" v-if="activityDetail.type == 3">
                            <div class="tipsline1">
                                <div class="left-tip">
                                    <p class="title gray">{{$t('activity.myalreadyholdamount')}}</p>
                                    <p>{{myHoldAmount | fixedScale(activityDetail.amountScale)}} {{activityDetail.acceptUnit}}</p>
                                </div>
                                <div class="right-tip">
                                    <p class="title gray">{{$t('activity.alreadyholdamount')}}</p>
                                    <p>{{activityDetail.freezeAmount | fixedScale(activityDetail.amountScale)}} {{activityDetail.acceptUnit}}</p>
                                </div>
                            </div>
                            <div class="tipsline2" v-if="isLogin">
                            {{$t('activity.currentdivided')}}：{{myHoldAmount | holdPercent(activityDetail.freezeAmount, activityDetail.totalSupply)}} {{activityDetail.unit}}
                            </div>
                        </div>
                        <p v-if="activityDetail.type == 3" class="hold-tips">*{{$t('activity.holdtips')}}</p>
                        <div class="do-activity" v-if="isLogin && (activityDetail.type==3 || activityDetail.type==4 || activityDetail.type==5)">
                            <div class="do-left">
                                <p class="p-title" v-if="activityDetail.type==3">{{$t('activity.inputholdamount')}}</p>
                                <p class="p-title" v-if="activityDetail.type==4">{{$t('activity.inputamount')}}</p>
                                <p class="p-title" v-if="activityDetail.type==5">{{$t('activity.inputminingamount')}}</p>
                                <Input style="width: 90%;" placeholder="0.00000" v-model="attendAmount" type="number">
                                    <span slot="append" v-if="activityDetail.type==3">{{activityDetail.acceptUnit}}</span>
                                    <span slot="append" v-else>{{activityDetail.unit}}</span>
                                </Input>
                            </div>
                            <div class="do-right">
                                <p class="p-title">{{$t('activity.mybalance')}}</p>
                                <div class="p-value">{{mybalance | fixedScale(activityDetail.priceScale)}} <span>{{activityDetail.acceptUnit}}</span></div>
                            </div>
                        </div>
                        <div class="bottom">
                            <Button type="warning" size="large" long  v-if="activityDetail.step==1 && isActivityInDate" @click="apply">{{$t('activity.attend')}}</Button>
                            <Button type="warning" size="large" long disabled  v-else>{{$t('activity.attend')}}</Button>
                        </div>
                        <p class="mobile-tip">{{$t('activity.tipsmobile1')}}</p>
                        <img v-if="activityDetail.step==3" src="../../assets/images/completed.png" style="position: absolute;top:-10px;right:15px;width: 110px;"></img>
                    </div>
                    <div class="memo">
                        <p style="font-size:14px;margin-bottom:10px;">{{$t('activity.attention')}}：</p>
                        <p>{{$t('activity.attentiontxt1')}}</p>
                        <p>{{$t('activity.attentiontxt2')}}</p>
                        <p>{{$t('activity.attentiontxt3')}}</p><br>
                        <p>{{$t('activity.attentiontxt4')}}</p>
                    </div>

                    <router-link class="more-activity" to="/lab/">{{$t('activity.moreactivity')}}</router-link>
                    <div class="show-qrcode" style="text-align: right;padding: 25px 10px;text-align:center;background:#FFF;margin-top: 10px;">
                      <qriously :value="qrcode.value" :size="qrcode.size" foreground="#000" />
                      <div style="width:100%;text-align:center;color:#000;">{{$t("cms.scanforshare")}}</div>
                    </div>
                </Col>
            </Row>
        </div>

        <!-- model -->
        <Modal v-model="modal2" width="360">
          <p slot="header" style="color:#f60;text-align:center">
            <Icon type="ios-mail" size="20" color="#00b5f6;" />
            <span>{{$t('uc.finance.withdraw.safevalidate')}}</span>
          </p>
          <div style="text-align:center">
            <Form ref="formValidateAddr" :model="formValidateAddr" :rules="ruleValidate" :label-width="85">
              <!-- 手机号 -->
              <FormItem :label="$t('uc.finance.withdraw.telno')" prop="mobileNo" v-show="validPhone">
                <Input disabled size="large" v-model="formValidateAddr.mobileNo"></Input>
              </FormItem>
              <!-- 手机验证码 -->
              <FormItem :label="$t('uc.finance.withdraw.smscode')" prop="vailCode2" v-show="validPhone">
                <Input v-model="formValidateAddr.vailCode2" size="large">
                <!-- <Button slot="append">点击获取</Button> -->
                <div class="timebox" slot="append">
                  <Button @click="send(2)" :disabled="disbtn">
                    <!--sendMsgDisabled2为true 时间+秒    sendMsgDisabled2为false为，点击获取 -->
                    <span v-if="sendMsgDisabled2">{{time2+$t('uc.finance.withdraw.second')}}</span>
                    <span v-if="!sendMsgDisabled2">{{$t('uc.finance.withdraw.clickget')}}</span>
                  </Button>
                </div>
                </Input>
              </FormItem>
              <!-- 邮箱 -->
              <FormItem :label="$t('uc.finance.withdraw.email')" prop="email" v-show="validEmail">
                <Input disabled v-model="formValidateAddr.email" size="large"></Input>
              </FormItem>
              <!-- 邮箱验证码 -->
              <FormItem :label="$t('uc.finance.withdraw.emailcode')" prop="vailCode1" v-show="validEmail">
                <Input v-model="formValidateAddr.vailCode1" size="large">
                <!-- <Button slot="append">点击获取</Button> -->
                <div class="timebox" slot="append">
                  <Button @click="send(1)" :disabled="disbtn">
                    <span v-if="sendMsgDisabled1">{{time1+$t('uc.finance.withdraw.second')}}</span>
                    <span v-if="!sendMsgDisabled1">{{$t('uc.finance.withdraw.clickget')}}</span>
                  </Button>
                </div>
                </Input>
              </FormItem>
            </Form>
          </div>
          <div slot="footer">
            <Button type="primary" size="large" long @click="handleSubmit('formValidateAddr')">{{$t('activity.submit')}}</Button>
          </div>
        </Modal>

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

var moment = require("moment");

export default {
  data() {
    return {
        mybalance: 0, // 我的可用余额
        modal2: false,
        sendMsgDisabled1: false,
        sendMsgDisabled2: false,
        time1: 60, // 发送验证码倒计时
        time2: 60, // 发送验证码倒计时
        attendAmount: 0,
        myHoldAmount: 0,
        disbtn: false,
        validEmail: false,
        validPhone: false,
        myRecordList: [],
        isActivityInDate: false, // 活动是否可参加
        qrcode: {
            value: "",
            size: 200
        },
        activityDetail: {
            title: "-",
            detail: "-",
            status: "1",
            step: "0",
            type: 1,
            startTime: "",
            endTime: "",
            totalSupply: 0,
            price: 0,
            priceScale: 0,
            unit: "-",
            acceptUnit: "-",
            amountScale: 0,
            maxLimitAmout: 0,
            minLimitAmout: 0,
            limitTimes: 0,
            activityLink: "-",
            noticeLink: "-",
            settings: "",
            smallImageUrl: "-",
            bannerImageUrl: "-",
            tradedAmount: 0,
            progress: 0,
            leveloneCount: 0,
            content: "NONE"
        },
        queryId: 0,
        formValidateAddr: {
            mobileNo: "",
            vailCode2: "",
            email: "",
            vailCode1: ""
        },
        ruleValidate: {
            mobileNo: [
              {
                required: this.validPhone,
                message: this.$t("uc.finance.withdraw.telerr"),
                trigger: "blur"
              }
            ],
            vailCode2: [
              {
                required: this.validPhone,
                message: this.$t("uc.finance.withdraw.codeerr"),
                trigger: "change"
              }
            ],
            email: [
              {
                required: this.validEmail,
                type: "email",
                message: this.$t("uc.finance.withdraw.emailerr"),
                trigger: "blur"
              }
            ],
            vailCode1: [
              {
                required: this.validEmail,
                message: this.$t("uc.finance.withdraw.codeerr"),
                trigger: "change"
              }
            ]
        }
    };
  },
  inject: ['reload'],
  created: function() {
    this.queryId = this.$route.params.id;
    this.qrcode.value = this.rootHost + "/lab/detail/" + this.queryId;
    this.init();
  },
  filters:{
    dateFormat: function(tick) {
      return moment(tick).format("YYYY-MM-DD HH:mm:ss");
    },
    fixedScale: function(value, scale) {
      if(value != undefined && value != null && value != ""){
        return value.toFixed(scale);
      }
      return 0;
    },
    holdPercent: function(value, totalHold, totalSupply){
        if(value == 0){
            return 0;
        }else if(value > 0){
            return (value/totalHold) * totalSupply;
        }
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
    }
  },
  methods: {
    init() {
      this.$store.commit("navigate", "nav-activity");
      this.getData();
    },
    getData(){
        let param = {};
        param["id"] = this.$route.params.id;
        this.$http.post(this.host + "/uc/activity/detail", param).then(res => {
        if (res.status == 200 && res.body.code == 0) {
            this.activityDetail = res.body.data;
            window.document.title = (this.lang == "简体中文" ? "活动 - " : "Activity - ") + this.activityDetail.title + " - 币严 | 全球比特币交易平台 | 全球数字货币交易平台";
            //持仓瓜分类型进度显示处理
            if(this.activityDetail.type == 3){
                if(this.activityDetail.step == 1){
                    this.activityDetail.progress = 50;
                }else if(this.activityDetail.step == 2){
                    this.activityDetail.progress = 75;
                }else if(this.activityDetail.step == 3){
                    this.activityDetail.progress = 100;
                }else{
                    this.activityDetail.progress = 0;
                }
            }
            if(this.isLogin) {
                this.getWallet();
                this.getMyRecords();
                this.getMember();
            }
            var currentDateStr = moment(new Date().getTime()).format("YYYY-MM-DD HH:mm:ss");
            if(currentDateStr >= this.activityDetail.startTime && currentDateStr <= this.activityDetail.endTime){
                this.isActivityInDate = true;
            }
        } else {
            this.$Message.error(res.body.message);
        }
      });
    },
    apply(){
        if(this.isMobile()) {
            this.$Message.error(this.$t("activity.tipsmobile"));
            return;
        }

        var self = this;
        if(this.activityDetail.type == 1 || this.activityDetail.type == 2){
            this.$router.push("/exchange/" + this.activityDetail.unit.toLowerCase() + "_" + this.activityDetail.acceptUnit.toLowerCase());
            return;
        }
        if(!this.isLogin){
            this.$Notice.error({
              title: this.$t("common.tip"),
              desc:this.$t("common.logintip")
            });
            setTimeout(function(){
                self.$router.push("/login");
            }, 3000);
        }else{
            this.attendClick();
        }
    },
    attendClick() {
      let interval = setInterval(() => {
        if (this.time2 <= 0) {
          this.sendMsgDisabled2 = false;
          window.clearInterval(interval);
          this.disbtn = false;
        }
      }, 1000);
      // 输入检查
      if(!this.attendAmount) {
        if(this.activityDetail.type == 3)
            this.$Notice.error({
              title: this.$t("common.tip"),
              desc: this.$t("activity.pleaseinputholdamount")
            });
        if(this.activityDetail.type == 4)
            this.$Notice.error({
              title: this.$t("common.tip"),
              desc: this.$t("activity.pleaseinputamount")
            });
        return;
      }
      // 大于0
      if(parseFloat(this.attendAmount) == 0) {
        return;
      }
      // 余额检查
      if(this.activityDetail.type == 4){
        if(parseFloat(this.attendAmount) * this.activityDetail.price > this.mybalance) {
            this.$Notice.error({
              title: this.$t("common.tip"),
              desc: this.activityDetail.acceptUnit + " " +this.$t("activity.balancenotenough")
            });
            return;
        }
      }
      // 云矿机需整数购买
      if(this.activityDetail.type == 5) {
        if (!(/(^[1-9]\d*$)/.test(this.attendAmount))) {
            this.$Notice.error({
              title: this.$t("common.tip"),
              desc: this.activityDetail.acceptUnit + " " +this.$t("activity.intvalue")
            });
            return;
        }
      }
      // 限购次数、限购量检查
      var temAlreadyAttendAmount = 0;
      var temAlreadyAttendTimes = this.myRecordList.length;

      for(var i = 0; i < this.myRecordList.length; i++){
          if(this.activityDetail.type == 3) {
            temAlreadyAttendAmount += this.myRecordList[i].freezeAmount;
          } else if(this.activityDetail.type == 4) {
            temAlreadyAttendAmount += this.myRecordList[i].amount;
          }
      }

      // 最小起兑量检查
      if(this.activityDetail.minLimitAmout > 0 && this.attendAmount < this.activityDetail.minLimitAmout){
        this.$Notice.error({
          title: this.$t("common.tip"),
          desc:this.$t("activity.minlimitamountfailed")
        });
        return;
      }

      // 最大兑换量检查
      if(this.activityDetail.maxLimitAmout > 0 && ((temAlreadyAttendAmount + parseFloat(this.attendAmount)) > this.activityDetail.maxLimitAmout)){

        this.$Notice.error({
          title: this.$t("common.tip"),
          desc:this.$t("activity.maxlimitamountfailed")
        });
        return;
      }

      // 个人限购次数检查
      if(this.activityDetail.limitTimes > 0 && (temAlreadyAttendTimes+1) > this.activityDetail.limitTimes){
        this.$Notice.error({
          title: this.$t("common.tip"),
          desc:this.$t("activity.limittimesfailed")
        });
        return;
      }

      // 显示安全验证
      this.modal2 = true;
    },
    isMobile() {
        var ua = window.navigator.userAgent.toLowerCase();
        if(ua.match(/MicroMessenger/i) == 'micromessenger'){
            return true;
        }
    　　let flag = window.navigator.userAgent.match(/(phone|pad|pod|iPhone|iPod|ios|iPad|Android|Mobile|BlackBerry|IEMobile|MQQBrowser|JUC|Fennec|wOSBrowser|BrowserNG|WebOS|Symbian|Windows Phone)/i)
        return flag;
    },
    handleSubmit(name) {
      this.$refs[name].validate(valid => {
        if (valid) {
          this.submit(name);
        } else {
          this.$Message.error(this.$t("activity.commitfailed"));
        }
      });
    },
    submit(name) {
      let param = {};
      param["amount"] = this.attendAmount;
      param["activityId"] = this.$route.params.id;
      if (this.validPhone) {
        param["aims"] = this.formValidateAddr.mobileNo;
        param["code"] = this.formValidateAddr.vailCode2;
      } else {
        param["aims"] = this.formValidateAddr.email;
        param["code"] = this.formValidateAddr.vailCode1;
      }

      this.$http
        .post(this.host + this.api.uc.attendActivity, param)
        .then(response => {
          var resp = response.body;
          if (resp.code == 0) {
            this.$Notice.success({
              title: this.$t("common.tip"),
              desc:resp.message
            });
            this.modal2 = false;
            this.init();
            this.attendAmount = 0;
          } else {
            this.$Notice.error({
              title: this.$t("common.tip"),
              desc:resp.message
            });
          }
        });
    },
    getWallet() {
      this.$http
        .post(this.host + this.api.uc.wallet + this.activityDetail.acceptUnit, {})
        .then(response => {
          var resp = response.body;
          this.mybalance = resp.data.balance || "";
        });
    },
    getMyRecords(){
        let param = {};
        param["activityId"] = this.$route.params.id;
        this.$http
        .post(this.host + this.api.uc.memberactivity, param)
        .then(response => {
            var resp = response.body;
            this.myRecordList = resp.data;
            this.myHoldAmount = 0;
            if(this.activityDetail.type == 3){
                for(var i = 0; i < this.myRecordList.length; i++) {
                    this.myHoldAmount += this.myRecordList[i].freezeAmount;
                }
            }
        });
    },
    getMember() {
      //获取个人安全信息
      this.$http
        .post(this.host + "/uc/approve/security/setting")
        .then(response => {
          var resp = response.body;
          if (resp.code == 0) {
            if (resp.data.mobilePhone) {
              this.formValidateAddr.mobileNo = resp.data.mobilePhone;
              this.validPhone = true;
              this.validEmail = false;
            } else {
              this.formValidateAddr.email = resp.data.email;
              this.validPhone = false;
              this.validEmail = true;
            }
          } else {
            this.$Message.error(resp.message);
          }
        });
    },
    send(index) {
      let me = this;
      this.disbtn = true;
      if (index == 1) {
        if (this.formValidateAddr.email) {
          //获取邮箱code
          this.$http.post(this.host + "/uc/activity/code").then(response => {
            var resp = response.body;
            if (resp.code == 0) {
              this.$Message.success(resp.message);
              me.sendMsgDisabled1 = true;
              let interval = window.setInterval(function() {
                if (me.time1-- <= 0) {
                  me.time1 = 60;
                  me.sendMsgDisabled1 = false;
                  window.clearInterval(interval);
                  this.disbtn = false;
                }
              }, 1000);
            } else {
              this.$Message.error(resp.message);
              this.disbtn = false;
            }
          });
        } else {
          this.$refs.formValidateAddr.validateField("email");
          this.disbtn = false;
        }
      } else if (index == 2) {
        if (this.formValidateAddr.mobileNo) {
          //获取手机code
          this.$http
            .post(this.host + "/uc/mobile/activity/code")
            .then(response => {
              var resp = response.body;
              if (resp.code == 0) {
                this.$Message.success(resp.message);
                me.sendMsgDisabled2 = true;
                this.interval = window.setInterval(() => {
                  if (me.time2-- <= 0) {
                    me.time2 = 60;
                    me.sendMsgDisabled2 = false;
                    window.clearInterval(this.interval);
                    this.disbtn = false;
                  }
                }, 1000);
              } else {
                this.$Message.error(resp.message);
                this.disbtn = false;
              }
            });
        } else {
          this.$refs.formValidateAddr.validateField("mobileNo");
          this.disbtn = false;
        }
      }
    }
  }
};
</script>

<style>
    .ivu-progress-bg{
      border-radius: 0!important;
      background-color: #ff8100;
      max-width: 100%;
    }
    .mobile-tip{
        text-align:center;color:#FF0000;margin-top:10px;
        display: none;
        font-size:10px;
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
    @media screen and (max-width:768px){
        .activity{
            padding-top: 45px!important;
        }
        .activity_container {
            padding: 0 3%!important;
        }
        .mobile-tip{
            display: block!important;
        }
        .app_bottom{
            display: block!important;
        }
        .show-qrcode{
            display: none!important;
        }
        .activity .activity_container .left-container .title img{
            height:40px!important;
            margin-top: 10px!important;
        }
        .activity .activity_container .left-container .title .title-text{
            padding-left: 10px!important;
        }
        .activity .activity_container .left-container .title .title-text h1{
            font-size:18px!important;
        }
        .activity .activity_container .left-container{
            min-height: 50px!important;
        }
        .activity .activity_container .right-container{
            min-height: 50px!important;
        }
        .more-activity{
            display: block!important;
        }
    }
</style>

<style lang="scss">
.activity{
    background: rgba(242,246,250,1) !important;
    height: 100%;
    background-size: cover;
    position: relative;
    overflow: hidden;
    padding-bottom: 30px;
    padding-top: 60px;
    color: #fff;

    .activity-detail-bannerimg {
        display: block;
        width: 100%;
    }
    .activity_container {
        padding: 0 12%;
        text-align: center;
        height: 100%;
        min-height: 600px;
        margin-top: 20px;
        .left-container{
            width: 100%;
            background:#FFF;
            min-height: 1600px;
            padding: 20px 20px;
            box-shadow: 0 0 25px #DDD;
            .title{
                padding-bottom: 20px;
                border-bottom: 1px solid #ededed;
                display: flex;
                flex-direction: row;
                justify-content: flex-start;
                img{
                    height: 80px;
                }
                .title-text{
                    padding-left: 20px;
                    text-align:left;
                    h1{
                        margin-top: 5px;
                        font-size: 22px;
                        font-weight: normal;
                        color: #31363e !important;
                    }
                    p{
                        margin-top: 10px;
                        font-size: 12px;
                        color: #a7a7a7;
                    }
                }
            }
            .content-wrap {
                flex: 1 1 100%;
                width: 100%;
                background: #FFF;
                min-height:600px;
                .content {
                    padding: 20px 10px;
                    color:#74777a;
                    letter-spacing: 1px;
                    font-size: 14px;
                    text-align:left;
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
                }
            }
        }
        .right-container{
            width: 100%;
            background: #FFF;
            min-height: 300px;
            padding: 20px 20px;
            color: #31363e !important;
            box-shadow: 0 0 25px #DDD;
            p.base{
                text-align: left;
                font-size: 18px;
                margin-bottom: 35px;
                color: #5d5d5d;
            }
            .progress{
                .progress-text{
                  width: 100%;
                  display: flex;
                  flex-direction: row;
                  justify-content: space-between;
                  span{
                    font-size: 12px;
                  }
                }
            }
            .info{
                margin-top: 35px;
                .info-item{
                    text-align:left;
                    margin-bottom: 20px;
                    .title{
                        font-size: 13px;
                        margin-bottom: 5px;
                    }
                    .value{
                        font-size: 14px;
                        letter-spacing: 1px;
                        a{
                            float:right;
                            font-size: 12px;
                            margin-top: 5px;
                        }
                    }
                }
            }
            .flexcolumn{
                display: flex;
                flex-direction: column !important;
            }
            p.hold-tips{
                font-size: 12px;text-align:right;margin-top: 10px;color: #F90;
            }
            .tips{
                background: rgba(240,245,249,1) !important;
                padding: 15px 15px;
                border-radius: 3px;
                margin-top: 35px;
                display: flex;
                flex-direction: row;
                justify-content: space-between;
                .left-tip{
                    text-align:left;
                    p.title{
                        font-size: 12px;
                    }
                    p{
                        font-size: 13px;
                    }
                }
                .right-tip{
                    text-align: right;
                    p.title{
                        font-size: 12px;
                    }
                    p{
                        font-size: 13px;
                    }
                }
                .tipsline1{
                    display:flex;flex-direction:row;justify-content:space-between;width: 100%;
                }
                .tipsline2{
                    padding-top:10px;font-size:12px;width:100%;display:flex;flex-direction:row;justify-content:center;width: 100%;text-align:center;border-top: 1px solid #cedae3;
                    margin-top: 10px;
                }
            }
            .bottom{
                width:100%;margin-top: 30px;
                .ivu-btn-warning[disabled]{
                    color: #ffffff;
                    background-color: #8c8c8c;
                    border-color: #dcdee2;
                }
                button{
                    border-radius: 0;
                    padding: 10px 5px;
                }
            }
            .t-header{
                font-size: 12px; color: #727272;padding-bottom:3px;background:#f0f5f9 !important;padding:5px 0;margin-top:-20px;
            }
            .do-activity{
                display:flex;flex-direction:row;justify-content:space-between;margin-top: 30px;
                .do-left{
                    width: 50%;
                    text-align:left;
                    .p-title{
                        font-size: 12px;
                        color: #aeb2b9 !important;
                    }
                }
                .do-right{
                    width: 50%;
                    text-align: right;
                    .p-title{
                        font-size: 12px;
                        color: #aeb2b9 !important;
                    }
                    .p-value{
                        margin-top: 2px;
                        line-height: 30px;
                        height:30px;
                        letter-spacing: 1px;
                        color: #000;
                        padding-right: 5px;
                        background: #ffd7a6;
                        background-image: repeating-linear-gradient(60deg, hsla(0,0%,100%,.1), hsla(0,0%,100%,.1) 10px, transparent 0, transparent 20px);
                        span{
                            font-size: 12px;
                        }
                    }
                }
            }
        }
        .memo{
            text-align:left;
            font-size: 12px;
            color: #959595 !important;
            background:#fffded;
            padding: 20px 20px 30px 20px;
            border-top: 1px dashed #CCC;
            p{
                margin-top: 5px;
            }
        }
        .more-activity{
            width: 100%;
            margin-top: 20px;
            border-radius: 3px;
            background-color:#FFF;
            padding: 8px 0 8px 0;
            display: none;
        }
    }
}

.gray{
  color: #aeb2b9 !important;
}
.do-activity{
    .ivu-input,
    .ivu-input-number-input,
    .ivu-input-number {
        font-size:14px;
      background-color: #FFF;
      color: #000;
      border-color: #FFF;
      border-bottom: 1px dashed #CCC;
      border-radius: 0;
      &:hover {
        border-color: #FFF;
        border-bottom: 1px dashed #CCC;
      }
      &:focus {
        border-color: #FFF;
        border-bottom: 1px dashed #CCC;
        box-shadow: none;
      }
    }
    .ivu-input-group-append{
        background-color: #ffffff;
        border-bottom: 1px dashed #CCC;
        border-radius: 0;
        font-size: 14px;
        color: #5f5f5f;
    }
    .ivu-input[disabled]:hover,
    fieldset[disabled] .ivu-input:hover {
      border-color: #27313e;
    }
}
</style>
