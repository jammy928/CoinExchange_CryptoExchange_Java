<template>
  <div class="identbusiness" style=" padding: 81px;padding-top: 80px;">
    <div class="content">
      <!---->
      <!--<div class="tit">{{$t('uc.identity.apply')}}</div>-->
      <!--<div class="plancon">-->
      <!--<span></span>-->

      <!--<div class="plan">-->
      <!--<div v-for="(step,i) in steps" :key="step" :class="{action:activeStepIndex>=i}">-->
      <!--{{activeStepIndex>=i?'':i+1}}-->
      <!--</div>-->
      <!--</div>-->

      <!--<div class="plans">-->
      <!--<div v-for="step in steps" :key="step">-->
      <!--{{step}}-->
      <!--</div>-->
      <!--</div>-->
      <!--</div>-->
      <div style="width: 80%;margin: 0 auto;margin-bottom: 60px;">
        <div class="ident-title" v-if="certStatus === 0">
          <!-- 申请认证商家 -->
          <h3>{{$t('uc.identity.apply')}}</h3>
          <p style="font-size: 14px;margin-top: 10px">
            <!-- {{$t('uc.identity.become')}} -->
          </p>
        </div>
        <div class="ident-title" v-else-if="certStatus == 1">
          <h3>{{$t('uc.identity.tijiao')}}</h3>
        </div>
        <div class="ident-title" v-else-if="certStatus == 2">
          <h3>{{$t('uc.identity.tijiaosuc')}}</h3>
        </div>
        <div class="ident-title" v-else-if="certStatus == 3">
          <h3>{{$t("uc.identity.tijiaofail")}}</h3>
        </div>
        <div class="ident-title" v-else-if="certStatus == 5">
          <h3>{{$t("uc.identity.zhuxiaotijiao")}}</h3>
        </div>
        <div class="ident-title" v-else-if="certStatus == 6">
          <h3>{{$t("uc.identity.shenhefail")}}</h3>
        </div>
        <div class="ident-title" v-else-if="certStatus == 7">
          <h3>{{$t("uc.identity.shenhesuc")}}</h3>
        </div>
        <!-- prepare:准备资料； review：提交审核； result:审核结果；certified：已认证 ; shenheshibai：审核失败-->
        <Steps class="apply-step" :current="certStatus == 2 ? 3 : certStatus == 3 ? 2 : certStatus" :status="certStatus == 3 ? 'error' :'finish'" v-if="certStatus != 0 && certStatus != 5 && certStatus != 6 && certStatus != 7">
          <Step :title=prepare></Step>
          <Step :title=review></Step>
          <Step :title="certStatus == 1 || certStatus == 0  ? result : certStatus == 2 ? certified : shenheshibai"></Step>
        </Steps>
        <!-- shangjiazhuxiao：商家注销  tijiaoshenqing：提交申请 shenheshibai：审核失败；passed：审核通过-->
        <Steps class="apply-step" :current="certStatus == 5 ? 1 : certStatus == 6 ? 2 : 3" :status="certStatus == 6 ? 'error':'finish'" v-if="certStatus == 5 || certStatus == 6 || certStatus == 7">
          <Step :title=shangjiazhuxiao></Step>
          <Step :title=tijiaoshenqing></Step>
          <Step :title="certStatus == 5 ? result : certStatus == 6 ? shenheshibai : passed"></Step>
        </Steps>

        <div v-if="certStatus == 6" style="width: 500px;margin: 0 auto;text-align: center;">
          <Button type="warning" style="width: 120px;background:#f0ac19;border-color:#f0ac19" @click="modal_return=true" long size="large">{{$t("uc.identity.shenagain")}}</Button>
          <div class="fail-reason" style="margin-top: 50px;font-size: 16px;">
            <Icon type="md-alert" color="red" size="16" />
            <span style="margin-left: 10px;">{{$t('uc.identity.yuanyin')}}：{{refuseReason}}</span>
          </div>
        </div>

        <div v-if="certStatus == 7" style="width: 500px;margin: 0 auto;text-align: center;">
          <Button type="warning" style="width: 120px;background:#f0ac19;border-color:#f0ac19" @click="modal_read=true" long size="large">{{$t("uc.identity.sheqinggain")}}</Button>
        </div>

        <div v-if="certStatus == 3" style="width: 500px;margin: 0 auto;text-align: center;">
          <Button type="warning" style="width: 120px;background:#f0ac19;border-color:#f0ac19" @click="modal_read=true" long size="large">{{$t("uc.identity.shenagain")}}</Button>
          <div class="fail-reason" style="margin-top: 50px;font-size: 16px;">
            <Icon type="md-alert" color="red" size="16" />
            <span style="margin-left: 10px;">{{$t("uc.identity.reason")}}：{{certReason}}</span>
          </div>
        </div>

        <div v-else-if="certStatus == 2" style="width: 500px;margin: 0 auto;text-align: center;">
          <Button type="warning" style="width: 120px;background:#f0ac19;border-color:#f0ac19" @click="publishAd" long size="large">{{$t('nav.fabu')}}</Button>
          <div style="margin-top: 30px;font-size: 16px;text-align: center;">
            <a @click="returnAdit" style="color: #aaa;">{{$t("uc.identity.shenqingtuibao")}}</a>
          </div>
        </div>
      </div>
      <!-- 认证商家第一步 -->
      <div class="ipshang" :class="certStatus != 0 ? 'applying' : '' ">
        <div class="ident-title" v-if="certStatus == 3">
          <h3 style="font-size: 20px">{{$t('uc.identity.apply')}}</h3>
          <p style="font-size: 14px;margin-top: 10px"> {{$t('uc.identity.become')}}</p>
        </div>
        <div class="ident-title" v-else-if="certStatus == 2">
          <h3>{{$t("uc.identity.getquan")}}</h3>
        </div>
        <!-- 第一步 -->
        <Row style="margin-top:40px;">
          <Col span="8">
          <div class="business-function">
            <img alt="" src="../../assets/images/business_show.png" width="300px">
            <p style="padding: 20px 0;font-weight: 600;font-size: 18px">{{$t('uc.identity.seat')}}</p>
            <span style="font-size: 14px;overflow:hidden; overflow: hidden;text-overflow:ellipsis;display: block;white-space:nowrap;color:#999;">{{$t("uc.identity.zhusnhu")}}</span>
          </div>
          </Col>
          <Col span="8">
          <div class="business-function">
            <img alt="" src="../../assets/images/business_service.png" width="300px">
            <p style="padding: 20px 0;font-weight: 600;font-size: 18px">{{$t('uc.identity.service')}}</p>
            <span style="font-size: 14px;color:#999;">{{$t("uc.identity.service")}}</span>
          </div>
          </Col>
          <Col span="8">
          <div class="business-function">
            <img alt="" src="../../assets/images/business_fee.png" width="300px">
            <p style="padding: 20px 0;font-weight: 600;font-size: 18px">{{$t('uc.identity.lowfee')}}</p>
            <span style="font-size: 14px;color:#999;">{{$t("uc.identity.lowfee")}}</span>
          </div>
          </Col>
        </Row>
        <!-- 同意第一步的协议 -->
        <div v-show="certStatus === 0" style="text-align: center;font-size: 16px;margin-top:50px">
          <Checkbox v-model="single"></Checkbox>
          <span>{{$t("uc.identity.read")}}</span>
          <router-link target="_blank" to="/helpdetail?cate=1&id=11&cateTitle=常见问题" class="cur" style="color:#f0a70a">{{$t('uc.identity.agreement')}}</router-link>
        </div>
        <!-- 同意第一步的按钮 -->
        <div v-show="certStatus === 0" class="sq">
          <Button @click="apply" style="background:#f0a70a;color:#fff;outline:none;">{{$t("uc.identity.lijishenqing")}}</Button>
        </div>
      </div>

      <!-- 商家end -->
      <!-- 发送邮件 -->
      <div class="mail" v-show="isShowMailt">
        <Input v-model="value" placeholder="Enter something..." style="width: 300px"></Input><br/>
        <Input v-model="value" placeholder="Enter something..." style="width:202px"></Input>
        <Button type="info">{{$t('uc.identity.sendcode')}}</Button><br/>
        <Button type="info" style="margin-top: 25px; width: 297px;">{{$t('uc.identity.confirm')}}</Button>
      </div>
      <!-- 邮件end -->
    </div>
    <!-- 提交审核中 -->
    <div class="submittedAudit" v-show="activeStepIndex === 1">
      <img src="../../assets/img/accomplish.png" alt="">
    </div>
    <!-- end -->
    <!-- 审核成功 -->
    <div class="auditSuccess" v-show="activeStepIndex === 2">
      <img src="../../assets/img/accomplish.png" alt="">
    </div>

    <Modal v-model="modal_read">
      <!-- 如何申请成为商家 -->
      <p slot="header">
        <span class="tit">{{$t('uc.identity.second.line')}}</span>
      </p>
      <div class="apply-note">
        <h3 style="padding-top: 10px;">{{$t('uc.identity.second.step1')}}</h3>
        <p>{{$t('uc.identity.second.step1c1')}}<br>{{$t('uc.identity.second.step1c2')}}</p>
        <h3>{{$t('uc.identity.second.step2')}}</h3>
        <p>{{$t('uc.identity.second.step2c')}}</p>
        <h3>{{$t('uc.identity.second.step3')}}</h3>
        <p>{{$t('uc.identity.second.stepc')}}</p>
        <div style="text-align: left;padding: 30px 0;">
          <Checkbox v-model="agreeFrozen"></Checkbox> {{$t('uc.identity.second.agree')}}
          <span>
            <font color="#f0a70a">{{auditText}}</font>{{$t('uc.identity.second.agreec')}}</span>
        </div>
        <Button @click="apply2" long style="font-size: 16px;background:#f0a70a;color:#fff;border:1px solid #f0a70a;">{{$t('uc.identity.second.shenqingchngweishangjia')}}</Button>
      </div>
      <p slot="footer">
        <!--<span style="text-align: left">-->
        <!--<Checkbox v-model="agreeFrozen" ></Checkbox> <span>同意冻结{{auditText}}作为商家保证金</span>-->
        <!--</span>-->
        <!--<Button type="info" @click="apply2">申请成为商家</Button>-->
      </p>
    </Modal>

    <Modal v-model="modal_apply">
      <p slot="header"></p>
      <div class="apply-content">
        <div class="apply-title">
          <h3>{{$t("uc.identity.tijiaoziliao")}}</h3>
          <p>{{$t("uc.identity.place")}}</p>
        </div>
        <Form class="apply-form" :model="apply_form" label-position="top">
          <FormItem :label="phone">
            <Input type="text" v-model="apply_form.telno" :placeholder="noEmpty"></Input>
          </FormItem>
          <FormItem :label="wechat">
            <Input type="text" v-model="apply_form.wechat" :placeholder="noEmpty"></Input>
          </FormItem>
          <FormItem :label="qq">
            <Input type="text" v-model="apply_form.qq" :placeholder="noEmpty"></Input>
          </FormItem>
          <Row>
            <Col span="8">
            <FormItem :label="bizhong">
              <Select v-model="apply_form.coinSymbol" :placeholder="select" @on-change="onCoinChange">
                <Option v-for="(item,index) in auditCurrency" :value="item.coin.unit" :key="index"></Option>
              </Select>
            </FormItem>
            </Col>
            <Col span="8">
            <span>&nbsp;</span>
            </Col>
            <Col span="8">
            <FormItem :label="shuliang">
              <Label v-model="apply_form.amount">{{apply_form.amount}}</Label>
            </FormItem>
            </Col>
          </Row>
          <Row>
            <Col span="8">
            <Upload type="drag" ref="upload1" :on-success="assetHandleSuccess" :headers="uploadHeaders" :action="uploadUrl" :on-remove="assetRemove">
              <span style="line-height: 100px;font-size: 50px;color:#ccc;">+</span>
              <img v-show="assetImg" class="previewImg" :src="assetImg">
            </Upload>
            <span>{{$t("uc.identity.gerenzichan")}}</span>
            </Col>
            <Col span="8">
            <span>&nbsp;</span>
            </Col>
            <Col span="8">
            <Upload type="drag" ref="upload2" :on-success="tradeHandleSuccess" :headers="uploadHeaders" :action="uploadUrl" :on-remove="tradeRemove">
              <span style="line-height: 100px;font-size: 50px;color:#ccc;">+</span>
              <img v-show="tradeImg" class="previewImg" :src="tradeImg">
            </Upload>
            <span>{{$t("uc.identity.shuzizichan")}}</span>
            </Col>
          </Row>
          <FormItem style="margin-top: 20px;">
            <Button style="width:100%;background:#f0a70a;color:#fff;border:1px solid #f0a70a;" type="info" @click="apply3('apply_form')" :disabled="applyBtn">{{$t("uc.identity.lijishenqing")}}</Button>
          </FormItem>
        </Form>
      </div>
      <p slot="footer"></p>
    </Modal>

    <Modal v-model="modal_return" @on-ok="returnAudit">
      <p slot="header" style="text-align: center;">{{$t("uc.identity.tips")}}</p>
      <p style="text-align: center;font-size: 14px;">{{$t("uc.identity.wufachexiao")}}</p>
      <p style="text-align: center;font-size: 14px;">{{$t("uc.identity.suredo")}}</p>
      <Input v-model="returnReason" type="textarea" :placeholder=placeholder :rows="4"></Input>
    </Modal>
  </div>

</template>
<script>
export default {
  data() {
    return {
      noEmpty: "必填",
      review: this.$t("uc.identity.review"), //准备资料
      prepare: this.$t("uc.identity.prepare"), //提交审核；
      result: this.$t("uc.identity.result"), //等待结果;
      certified: this.$t("uc.identity.certified"), //已认证
      shenheshibai: this.$t("uc.identity.shenheshibai"), //审核失败
      shangjiazhuxiao: this.$t("uc.identity.shangjiazhuxiao"), //商家注销
      tijiaoshenqing: this.$t("uc.identity.tijiaoshenqing"), //提交申请
      shenheshibai: this.$t("uc.identity.shenheshibai"), //审核失败
      passed: this.$t("uc.identity.passed"), //审核通过
      placeholder: this.$t("uc.identity.placeholder"),
      select: this.$t("uc.identity.chosen"),
      phone: this.$t("uc.identity.phone"),
      qq: this.$t("uc.identity.qq"),
      wechat: this.$t("uc.identity.wx"),
      bizhong: this.$t("uc.identity.bizhong"),
      shuliang: this.$t("uc.identity.shuliang"),
      loginmsg: this.$t("common.logintip"),
      single: false,
      value: "",
      isShowShang: true,
      isShowMailt: false,
      isShowSubmitted: false,
      isShowSuccess: false,
      activeStepIndex: 0,
      steps: [
        this.$t("uc.identity.prepare"),
        this.$t("uc.identity.review"),
        this.$t("uc.identity.passed")
      ],
      certStatus: 0, //认证申请状态，0:未申请，1：审核中，2：已认证，3：认证失败
      certReason: "",
      auditCurrency: "",
      auditText: "",
      modal_read: false,
      modal_return: false,
      agreeFrozen: false,
      modal_apply: false,
      applyBtn: false,
      apply_form: {
        telno: "",
        wechat: "",
        qq: "",
        coinSymbol: "",
        amount: "",
        assetData: "",
        tradeData: ""
      },
      assetImg: "",
      tradeImg: "",
      uploadHeaders: { "x-auth-token": localStorage.getItem("TOKEN") },
      uploadUrl: this.host + "/uc/upload/oss/image",
      returnReason: "",
      refuseReason: ""
    };
  },
  methods: {
    islogin() {
      let self = this;
      //判断是否进行实名认证；
      this.$http
        .post(this.host + "/uc/approve/security/setting", {})
        .then(response => {
          var resp = response.body;
          if (resp.code == 0) {
            if (resp.data.realName == null || resp.data.realName == "") {
              this.$Message.warning(this.$t("otc.publishad.submittip1"));
              self.$router.push("/uc/safe");
            } else if (resp.data.phoneVerified == 0) {
              this.$Message.warning(this.$t("otc.publishad.submittip2"));
              self.$router.push("/uc/safe");
            } else if (resp.data.fundsVerified == 0) {
              this.$Message.warning(this.$t("otc.publishad.submittip3"));
              self.$router.push("/uc/safe");
            }
          } else {
            this.$Message.error(resp.message);
          }
        });
    },
    timer() {
      setInterval(() => {
        this.getSetting();
      }, 10000);
    },
    publishAd() {
      this.$router.push("/uc/ad/create");
    },
    returnAdit() {
      this.modal_return = true;
    },
    returnAudit() {
      var params = {};
      params["detail"] = this.returnReason;
      this.$http
        .post(this.host + "/uc/approve/cancel/business", params)
        .then(res => {
          let resp = res.body;
          if (resp.code == 0) {
            this.$Message.success("提交成功!");
            this.modal_return = false;
            this.getSetting();
          } else {
            this.$Message.error(resp.message);
          }
        });
    },
    getAudiCoin(symbol) {
      var coin = null;
      for (var i = 0; i < this.auditCurrency.length; i++) {
        if (symbol == this.auditCurrency[i].coin.unit) {
          coin = this.auditCurrency[i];
          break;
        }
      }
      return coin;
    },
    onCoinChange(value) {
      var coin = this.getAudiCoin(value);
      if (coin != null) {
        this.apply_form.amount = coin.amount;
      }
    },
    getSetting() {
      this.$http
        .get(this.host + this.api.uc.identification)
        .then(res => {
          let certifiedBusinessStatus = res.body.data.certifiedBusinessStatus;
          this.activeStepIndex = certifiedBusinessStatus;
          this.certStatus = certifiedBusinessStatus;
          this.certReason = res.body.data.detail;
          this.refuseReason = res.body.data.reason;
        })
        .catch(function(error) {});
    },
    assetHandleSuccess(res, file, fileList) {
      // fileList = fileList[fileList.length-1]
      this.$refs.upload1.fileList = [fileList[fileList.length - 1]];
      this.apply_form.assetData = res.data;
      this.assetImg = res.data;
    },
    tradeHandleSuccess(res, file, fileList) {
      this.$refs.upload2.fileList = [fileList[fileList.length - 1]];
      this.apply_form.tradeData = res.data;
      this.tradeImg = res.data;
    },
    assetRemove(file, fileList) {
      this.apply_form.assetData = "";
      this.assetImg = "";
    },
    tradeRemove(file, fileList) {
      this.apply_form.tradeData = "";
      this.tradeImg = "";
    },
    getAuthFound() {
      this.$http
        .get(this.host + "/uc/approve/business-auth-deposit/list")
        .then(res => {
          var resp = res.body;
          if (resp.code == 0) {
            this.auditCurrency = resp.data;
            var tempText = "";
            for (var i = 0; i < resp.data.length; i++) {
              if (i == 0) {
                //BHB;
                this.apply_form.coinSymbol = resp.data[i].coin.unit;
                //10000;
                this.apply_form.amount = resp.data[i].amount;
              }
              tempText += resp.data[i].amount + "个" + resp.data[i].coin.unit;
              if (i < resp.data.length - 1) tempText += "或";
            }
            this.auditText = tempText;
          }
        });
    },
    apply() {
      let stasingle = this.single;
      if (stasingle == false) {
        this.$Message.warning(this.$t("uc.identity.approve"));
        return;
      }
      this.modal_read = true;
      return;
      this.$http
        .get(this.host + this.api.uc.apply)
        .then(res => {
          debugger;
          var resp = res.body;
          if (resp.code == 0) {
            this.$Message.success(resp.message);
            this.activeStepIndex = 1;
          } else {
            this.$Message.warning(resp.message);
          }
        })
        .catch(function(error) {
          this.$Message.error(error);
        });
    },
    apply2() {
      let agreeFrozen = this.agreeFrozen;
      if (agreeFrozen == false) {
        this.$store.state.lang != "English" &&
          this.$Message.warning("请同意冻结相应数量的币");
        this.$store.state.lang == "English" &&
          this.$Message.warning(
            "Please agree to freeze the corresponding amount of currency"
          );
        return;
      }
      this.modal_read = false;
      this.modal_apply = true;
    },
    apply3(form) {
      if (this.apply_form.telno == "") {
        this.$store.state.lang != "English" &&
          this.$Message.error("请填写手机号");
        this.$store.state.lang == "English" &&
          this.$Message.error("Please fill in your cell phone number");
        return;
      }
      if (this.apply_form.wechat == "") {
        this.$store.state.lang != "English" &&
          this.$Message.error("请填写微信号");
        this.$store.state.lang == "English" &&
          this.$Message.error("Please fill in your cell wechat number");
        return;
      }
      if (this.apply_form.qq == "") {
        this.$store.state.lang != "English" &&
          this.$Message.error("请填写qq号");
        this.$store.state.lang == "English" &&
          this.$Message.error("Please fill in your cell qq number");
        return;
      }
      if (this.apply_form.assetData == "") {
        this.$store.state.lang != "English" &&
          this.$Message.error("请上传资产证明");
        this.$store.state.lang == "English" &&
          this.$Message.error("Please upload the asset certificate");
        return;
      }
      if (this.apply_form.tradeData == "") {
        this.$store.state.lang != "English" &&
          this.$Message.error("请上传交易证明");
        this.$store.state.lang == "English" &&
          this.$Message.error("Please upload the transaction certificate");
        return;
      }
      var params = {};
      params["businessAuthDepositId"] = this.getAudiCoin(
        this.apply_form.coinSymbol
      ).id;
      params["json"] = JSON.stringify(this.apply_form);
      this.$http
        .post(this.host + "/uc/approve/certified/business/apply", params)
        .then(res => {
          var resp = res.body;
          if (resp.code == 0) {
            this.$Message.success("提交成功!");
            this.modal_apply = false;
            this.certStatus = 1;
          } else {
            this.$Message.error(resp.message);
          }
        });
    }
  },
  created() {
    //this.timer();
    this.islogin();
    this.getSetting();
    this.getAuthFound();
  },
  computed: {
    lang: function() {
      return this.$store.state.lang;
    }
  },
  watch: {
    lang: function() {
      this.prepare = this.$t("uc.identity.prepare");
      this.review = this.$t("uc.identity.review");
      this.result = this.$t("uc.identity.result");
      this.certified = this.$t("uc.identity.certified"); //已认证
      this.shenheshibai = this.$t("uc.identity.shenheshibai"); //审核失败
      this.shangjiazhuxiao = this.$t("uc.identity.shangjiazhuxiao"); //商家注销
      this.tijiaoshenqing = this.$t("uc.identity.tijiaoshenqing"); //提交申请
      this.shenheshibai = this.$t("uc.identity.shenheshibai"); //审核失败
      this.passed = this.$t("uc.identity.passed"); //审核通过

      this.phone = this.$t("uc.identity.phone");
      this.qq = this.$t("uc.identity.qq");
      this.wechat = this.$t("uc.identity.wx");
      this.bizhong = this.$t("uc.identity.bizhong");
      this.shuliang = this.$t("uc.identity.shuliang");
    }
  }
};
</script>

<style scoped>
.previewImg {
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
}
.content {
  width: 1200px;
  margin: 0 auto;
  padding-top: 30px;
  height: 100%;
  background: #192330;
}
.ip .ivu-col {
  line-height: 37px;
  text-align: left;
  padding-left: 139px;
}
.ipshang {
  overflow: hidden;
}
.ipshang.applying {
  background: #1f2833;
  padding: 40px 0;
}
.sq {
  width: 1200px;
  margin-top: 50px;
  text-align: center;
  margin-bottom: 50px;
}
.xian {
  width: 100%;
  height: 1px;
  margin-top: 105px;
  border-top: 1px #ececec dotted;
}
.sq button {
  height: 50px;
  font-size: 18px;
  width: 450px;
}
.tit {
  font-size: 16px;
  line-height: 25px;
  border-left: 5px solid #f0a70a;
  padding-left: 15px;
}
.plancon {
  width: 64%;
  margin: 49px auto;
  position: relative;
}
.plan {
  position: absolute;
  height: 36px;
  width: 100%;
  top: -13px;
}
.plans {
  position: absolute;
  height: 36px;
  width: 100%;
  top: 13px;
}
.plan div {
  z-index: 99;
  float: left;
  width: 33.33%;
  color: white;
  height: 31px;
  line-height: 31px;
  text-align: center;
  background: url("../../assets/img/2.png") center no-repeat;
  background-size: contain;
}
.action {
  z-index: 99999 !important;
  float: left;
  width: 25%;
  height: 31px;
  line-height: 31px;
  text-align: center;
  background-size: contain;
  background: url("../../assets/img/1.png") center no-repeat !important;
}
.plans div {
  z-index: 99;
  float: left;
  width: 33.333%;
  height: 53px;
  line-height: 53px;
  font-size: 14px;
  text-align: center;
  background-size: contain;
}
.plancon span {
  background: #ececec;
  height: 1px;
  width: 65%;
  display: inherit;
  margin: 0 auto;
}
.ivu-col-span-8 p {
  font-size: 19px;
}
.peakfire {
  width: 1000px;
  margin: 0 auto;
  height: 80px;
  line-height: 80px;
  border: 1px solid #eaeaea;
  margin-top: 43px;
  padding-left: 25px;
}
.peakfire span {
  color: #3faef5;
}
.mail {
  width: 1000px;
  margin: 87px auto;
  text-align: center;
  line-height: 50px;
  display: none;
}
.submittedAudit {
  width: 1000px;
  margin: 87px auto;
  text-align: center;
  display: none;
}
.auditSuccess {
  width: 1000px;
  margin: 87px auto;
  text-align: center;
  display: none;
}
.apply-note {
  font-size: 14px;
}
.apply-note h3 {
  padding: 20px 0;
  font-size: 16px;
}
.apply-note ul {
  list-style: initial;
  padding-left: 20px;
}
.apply-content {
  width: 80%;
  margin: 0 auto;
}
.apply-title {
  text-align: center;
}
.apply-title h3 {
  font-size: 20px;
}
.apply-title p {
  font-size: 14px;
  padding: 10px 0;
}
.ident-title {
  text-align: center;
  font-size: 20px;
}
.apply-step {
  padding: 50px 0;
  margin-left: 150px;
}
.apply-step .ivu-steps-title {
  display: block;
}
.business-function {
  width: 300px;
  margin: 0 auto;
  border: none;
  background-color: #27313e;
  padding-bottom: 20px;
  border-top-left-radius: 10px;
  border-top-right-radius: 10px;
}
</style>
<style>
.ivu-form-item {
  margin-bottom: 24px;
}
.ivu-steps-item.ivu-steps-status-finish .ivu-steps-head-inner {
  background-color: #313654;
  border-color: #313654;
}
.ivu-steps-item.ivu-steps-status-finish .ivu-steps-head-inner > .ivu-steps-icon,
.ivu-steps-item.ivu-steps-status-finish .ivu-steps-head-inner span {
  color: #fff !important;
}
.ivu-steps-item.ivu-steps-status-process .ivu-steps-head-inner {
  border-color: #313654;
  background-color: #313654;
}
.ivu-steps-item.ivu-steps-status-finish .ivu-steps-tail > i:after {
  background: #313654;
}
.identbusiness .ivu-steps .ivu-steps-head{
  background-color:#192330;
}
.identbusiness .ivu-steps .ivu-steps-title{
  background-color:#192330;
  color:#fff;
}
</style>
<style lang="scss">
.v-transfer-dom {
  .ivu-modal-wrap {
    .ivu-modal-content {
      .apply-content {
        form.apply-form.ivu-form.ivu-form-label-top {
          .ivu-form-item-label{
            color: #fff;
          }
          .ivu-row {
            .ivu-upload.ivu-upload-drag {
              background-color: transparent;
              &:hover {
                border-color: #f0a70a;
              }
            }
          }
          .ivu-form-item-content {
            button {
              &:focus {
               box-shadow: none;
              }
            }
          }
        }
      }
      .ivu-modal-body {
        .apply-note {
          button {
            &:focus {
              box-shadow: none;
            }
          }
        }
      }
      .ivu-modal-footer {
        border: none;
        .ivu-btn-primary {
          background-color: #f0a70a;
          color: #fff;
          border-color: #f0ac70;
        }
        .ivu-btn-text {
          &:hover,
          &:focus {
            color: #f0a70a;
          }
        }
      }
    }
  }
}
.ivu-btn-primary {
  background: #f0a70a;
  border: 1px solid #f0a70a;
  &:hover {
    background: #f0a70a;
    border: 1px solid #f0a70a;
  }
}
.ivu-btn-text {
  &:hover {
    color: #f0a70a;
  }
}
.ivu-checkbox-checked .ivu-checkbox-inner {
  background-color: #f0a70a !important;
  border: 1px solid #f0a70a !important;
}
li.ivu-upload-list-file.ivu-upload-list-file-finish {
  &:hover {
    span {
      color: #f0a70a;
    }
  }
}

.content {
  .apply-step.ivu-steps.ivu-steps-horizontal {
    .ivu-steps-item.ivu-steps-status-finish {
      .ivu-steps-tail {
        i {
          &:after {
            background: #f0a70a;
          }
        }
      }
      .ivu-steps-head {
        .ivu-steps-head-inner {
          background-color: #f0a70a;
          border-color: #f0a70a;
        }
      }
      .ivu-steps-main {
        display: inline-block;
        .ivu-steps-title {
          color: #fff;
        }
      }
    }
    .ivu-steps-item.ivu-steps-status-process {
      .ivu-steps-head {
        .ivu-steps-head-inner {
          background-color: #f0a70a;
          border-color: #f0a70a;
        }
      }
    }
  }
}
</style>


