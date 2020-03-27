<template>
    <div class="nav-rights safe">
      <div class="nav-right padding-right-clear">
        <div class="padding-right-clear padding-left-clear rightarea user account-box">
            <div class="rightarea-con">
                <div class="user-top-icon">
                    <div class="user-icons">
                        <div class="user-face user-avatar-public">
                            <span class="user-avatar-in">{{usernameS}}</span>
                        </div>
                        <div class="user-name" style="line-height:52px">
                            <span style="line-height:52px">{{user.username}}</span>
                        </div>
                    </div>
                    <Row class="user-right">
                        <Col :xs="24" :sm="24" :md="8" :lg="8">
                            <i class="m3"></i>
                            <div class="itp" v-if="user.realVerified==0&&user.phoneVerified==0&&user.fundsVerified==0">{{$t('uc.safe.safelevel_low')}}</div>
                            <div class="itp" v-else-if="user.realVerified==1&&user.phoneVerified==1&&user.fundsVerified==1">{{$t('uc.safe.safelevel_high')}}</div>
                            <div class="itp" v-else>{{$t('uc.safe.safelevel_medium')}}</div>
                        </Col>
                    </Row>
                </div>
                <section class="accountContent">
                    <div class="account-in">
                        <!-- 1 -->
                        <div class="account-item" style="display:none">
                            <div class="account-item-in">
                                <Icon type="person" style="font-size: 18px;color: #00b5f6;"></Icon>
                                <span class="card-number">{{$t('uc.safe.nickname')}}</span>
                                <p class="bankInfo" style="color: #fff;">
                                    //
                                </p>
                                <span>{{$t('uc.safe.binded')}}</span>
                            </div>
                        </div>
                        <!-- 6 -->
                        <div class="account-item">
                            <div class="account-item-in">
                                <Icon type="md-card" size="18" color="#00b5f6"/>
                                <span class="card-number">{{$t('uc.safe.verified')}}</span>
                                <p v-if="user.realVerified==1" class="bankInfo" style="color: #fff;font-size: 13px;">{{user.realName}}</p>
                                <p v-else-if="user.realVerified==0&&user.realAuditing==0&&user.realNameRejectReason!=null" class="bankInfo" style="color: #f0a70a;font-size: 13px;">
                                    审核未通过{{user.realNameRejectReason?"："+user.realNameRejectReason:""}}，请重试。
                                </p>
                                <p v-else class="bankInfo" style="color: #828ea1;font-size: 13px;">
                                    {{$t('uc.safe.verifiedtip')}}
                                </p>
                                <span v-if="user.realVerified==1">{{$t('uc.safe.verifypass')}}</span>
                                <span v-else-if="user.realAuditing==1">{{$t('uc.safe.binding')}}</span>
                                <a class="btn" @click="showItem(6)" v-else-if="user.realVerified==0&&user.realAuditing==0&&user.realNameRejectReason!=null" :title="user.realNameRejectReason">{{$t('uc.safe.bindretry')}}</a>
                                <a v-else class="btn" @click="showItem(6)">{{$t('uc.safe.notverified')}}</a>
                            </div>
                            <div class="account-detail" v-show="choseItem==6">
                                <div class="detail-list" style="width: 100%;">
                                    <Form ref="formValidate6" :model="formValidate6" :rules="ruleValidate" :label-width="85" style="text-align:center;">
                                        <!-- 真实姓名 -->
                                        <FormItem :label="$t('uc.safe.realname')" prop="realName">
                                            <Input v-model="formValidate6.realName" size="large"></Input>
                                        </FormItem>
                                        <!-- 身份证号 -->
                                        <FormItem :label="$t('uc.safe.idcard')" prop="idCard">
                                            <Input v-model="formValidate6.idCard" size="large"></Input>
                                        </FormItem>
                                        <div style="height:250px;">
                                            <Col span="8">
                                            <input type="hidden" name="imgPreview" :value="imgPreview" />
                                            <div class="idcard-title">{{$t('uc.safe.upload_positive')}}</div>
                                            <img id="frontCardImg" style="width: 180px;height: 120px;" :src="frontCardImg">
                                            <div class="acc_sc">
                                                <Upload ref="upload1" :before-upload="beforeUpload" :on-success="frontHandleSuccess" :headers="uploadHeaders" :action="uploadUrl">
                                                    <Button icon="ios-cloud-upload-outline">{{$t('uc.safe.upload')}}</Button>
                                                </Upload>
                                            </div>
                                            </Col>
                                            <Col span="8">
                                            <input type="hidden" name="imgNext" :value="imgNext" />
                                            <div class="idcard-title">{{$t('uc.safe.upload_negative')}}</div>
                                            <img id="backCardImg" style="width: 180px;height: 120px;" :src="backCardImg">
                                            <div class="acc_sc">
                                                <Upload ref="upload2" :before-upload="beforeUpload" :on-success="backHandleSuccess" :headers="uploadHeaders" :action="uploadUrl">
                                                    <Button  icon="ios-cloud-upload-outline">{{$t('uc.safe.upload')}}</Button>
                                                </Upload>
                                            </div>
                                            </Col>
                                            <Col span="8">
                                            <input type="hidden" name="imgLast" :value="imgLast" />
                                            <div class="idcard-title">{{$t('uc.safe.upload_hand')}}</div>
                                            <img id="handCardImg" style="width: 180px;height: 120px;" :src="handCardImg">
                                            <div class="acc_sc">
                                                <Upload ref="upload3" :before-upload="beforeUpload" :on-success="handHandleSuccess" :headers="uploadHeaders" :action="uploadUrl">
                                                    <Button icon="ios-cloud-upload-outline">{{$t('uc.safe.upload')}}</Button>
                                                </Upload>
                                            </div>
                                            </Col>
                                        </div>
                                        <div class="idcard-desc">
                                            <p>{{$t('uc.safe.idcard_verifymsg1')}}</p>
                                            <p>{{$t('uc.safe.idcard_verifymsg2')}}</p>
                                            <p>{{$t('uc.safe.idcard_verifymsg3')}}</p>
                                        </div>
                                        <!-- Button -->
                                        <FormItem style="text-align:center;">
                                            <Button type="warning" @click="handleSubmit('formValidate6')" style="margin-left: -85px;">{{$t('uc.safe.save')}}</Button>
                                            <Button @click="handleReset('formValidate6')" style="margin-left: 8px">{{$t('uc.safe.reset')}}</Button>
                                        </FormItem>
                                    </Form>
                                </div>
                            </div>
                        </div>
                        <!-- 2 -->
                        <div class="account-item" style="display: none;">
                            <div class="account-item-in">
                                <Icon type="ios-mail" size="20" color="#00b5f6;"/>
                                <span class="card-number">{{$t('uc.safe.email')}}</span>
                                <p v-if="user.emailVerified==1" class="bankInfo" style="color: grey;font-size: 13px;">
                                    {{user.email}}
                                </p>
                                <p v-else class="bankInfo" style="color: #828ea1;font-size: 13px;">
                                    {{$t('uc.safe.bindemail')}}
                                </p>
                                <span v-if="user.emailVerified==1">{{$t('uc.safe.binded')}}</span>
                                <a v-else class="btn" @click="showItem(2)">{{$t('uc.safe.bind')}}</a>
                            </div>
                            <div class="account-detail" v-show="choseItem==2">
                                <div class="detail-list">
                                    <Form ref="formValidate2" :model="formValidate2" :rules="ruleValidate" :label-width="110">
                                        <!-- mail -->
                                        <FormItem :label="$t('uc.safe.email')" prop="mail">
                                            <Input v-model="formValidate2.mail" size="large"></Input>
                                        </FormItem>
                                        <!-- 登录密码 -->
                                        <FormItem :label="$t('uc.safe.loginpwd')" prop="password">
                                            <Input v-model="formValidate2.password" size="large" type="password"></Input>
                                        </FormItem>
                                        <!-- 邮箱验证码 -->
                                        <FormItem :label="$t('uc.safe.emailcode')" prop="vailCode1">
                                            <Input v-model="formValidate2.vailCode1" size="large">
                                            <!-- <Button slot="append">点击获取</Button> -->
                                            <div class="timebox" slot="append">
                                                <Button @click="send(1)" :disabled="sendMsgDisabled1">
                                                    <span v-if="sendMsgDisabled1">{{time1+$t('uc.safe.second')}}</span>
                                                    <span v-if="!sendMsgDisabled1">{{$t('uc.safe.clickget')}}</span>
                                                </Button>
                                            </div>
                                            </Input>
                                        </FormItem>
                                        <!-- Button -->
                                        <FormItem>
                                            <Button type="warning" @click="handleSubmit('formValidate2')">{{$t('uc.safe.save')}}</Button>
                                            <Button @click="handleReset('formValidate2')" style="margin-left: 8px">{{$t('uc.safe.reset')}}</Button>
                                        </FormItem>
                                    </Form>
                                </div>
                            </div>
                        </div>
                        <!-- 3 -->
                        <div class="account-item">
                            <div class="account-item-in">
                                <Icon type="ios-call" color="#00b5f6" size="20"/>
                                <span class="card-number">{{$t('uc.safe.phone')}}</span>
                                <p v-if="user.phoneVerified==1" class="bankInfo" style="color: #fff;font-size: 13px;">
                                    {{user.mobilePhone}}
                                </p>
                                <p v-else class="bankInfo" style="color: #828ea1;font-size: 13px;">
                                    {{$t('uc.safe.bindphone')}}
                                </p>
                                <span v-if="user.phoneVerified==1">{{$t('uc.safe.binded')}}</span>
                                <a v-else class="btn" @click="showItem(3)">{{$t('uc.safe.bind')}}</a>
                            </div>
                            <div class="account-detail" v-show="choseItem==3">
                                <div class="detail-list">
                                    <Form ref="formValidate3" :model="formValidate3" :rules="ruleValidate" :label-width="110">
                                        <!-- 手机 -->
                                        <FormItem :label="$t('uc.safe.phone')" prop="mobile">
                                            <Input v-model="formValidate3.mobile" size="large"></Input>
                                        </FormItem>
                                        <!-- 登录密码 -->
                                        <FormItem :label="$t('uc.safe.loginpwd')" prop="password">
                                            <Input v-model="formValidate3.password" size="large" type="password"></Input>
                                        </FormItem>
                                        <!-- 手机验证码 -->
                                        <FormItem :label="$t('uc.safe.phonecode')" prop="vailCode2">
                                            <Input v-model="formValidate3.vailCode2" size="large">
                                            <!-- <Button slot="append">点击获取</Button> -->
                                            <div class="timebox" slot="append">
                                                <Button @click="send(2)" :disabled="sendMsgDisabled2">
                                                    <span v-if="sendMsgDisabled2">{{time2+$t('uc.safe.second')}}</span>
                                                    <span v-if="!sendMsgDisabled2">{{$t('uc.safe.clickget')}}</span>
                                                </Button>
                                            </div>
                                            </Input>
                                        </FormItem>
                                        <!-- Button -->
                                        <FormItem>
                                            <Button type="warning" @click="handleSubmit('formValidate3')">{{$t('uc.safe.save')}}</Button>
                                            <Button @click="handleReset('formValidate3')" style="margin-left: 8px">{{$t('uc.safe.reset')}}</Button>
                                        </FormItem>
                                    </Form>
                                </div>
                            </div>
                        </div>
                        <!-- 4 -->
                        <div class="account-item">
                            <div class="account-item-in">
                                <Icon type="ios-lock" size="20"color="#00b5f6;"></Icon>
                                <span class="card-number">{{$t('uc.safe.loginpwd')}}</span>
                                <p class="bankInfo" style="color: #828ea1;font-size: 13px;">
                                    {{$t('uc.safe.logintip')}}
                                </p>

                                <a class="btn" v-if="user.phoneVerified==0" @click="noPhone">{{$t('uc.safe.edit')}}</a>
                                <a class="btn" v-else @click="showItem(4)">{{$t('uc.safe.edit')}}</a>
                            </div>
                            <div class="account-detail" v-show="choseItem==4">
                                <div class="detail-list">
                                    <Form ref="formValidate4" :model="formValidate4" :rules="ruleValidate" :label-width="95">
                                        <!-- oldPw -->
                                        <FormItem :label="$t('uc.safe.oldpwd')" prop="oldPw">
                                            <Input v-model="formValidate4.oldPw" size="large" type="password"></Input>
                                        </FormItem>
                                        <!-- newPw -->
                                        <FormItem :label="$t('uc.safe.newpwd')" prop="newPw">
                                            <Input v-model="formValidate4.newPw" size="large" type="password"></Input>
                                        </FormItem>
                                        <!-- newPwConfirm -->
                                        <FormItem :label="$t('uc.safe.confirmnewpwd')" prop="newPwConfirm">
                                            <Input v-model="formValidate4.newPwConfirm" size="large" type="password"></Input>
                                        </FormItem>
                                        <!-- 手机验证码 -->
                                        <FormItem :label="$t('uc.safe.phonecode')" prop="vailCode3">
                                            <Input v-model="formValidate4.vailCode3" size="large">
                                            <!-- <Button slot="append">点击获取</Button> -->
                                            <div class="timebox" slot="append">
                                                <Button @click="send(3)" :disabled="sendMsgDisabled3">
                                                    <span v-if="sendMsgDisabled3">{{time3+$t('uc.safe.second')}}</span>
                                                    <span v-if="!sendMsgDisabled3">{{$t('uc.safe.clickget')}}</span>
                                                </Button>
                                            </div>
                                            </Input>
                                        </FormItem>
                                        <!-- Button -->
                                        <FormItem>
                                            <Button type="warning" @click="handleSubmit('formValidate4')">{{$t('uc.safe.save')}}</Button>
                                            <Button @click="handleReset('formValidate4')" style="margin-left: 8px">{{$t('uc.safe.reset')}}</Button>
                                        </FormItem>
                                    </Form>
                                </div>
                            </div>
                        </div>
                        <!-- 5 -->
                        <div class="account-item">
                            <div class="account-item-in">
                              <Icon type="logo-bitcoin" size="20" color="#00b5f6" />
                                <span class="card-number">{{$t('uc.safe.fundpwd')}}</span>
                                <p class="bankInfo" style="color: #828ea1;font-size: 13px;">
                                    {{$t('uc.safe.fundtip')}}
                                </p>
                                <a class="btn" v-if="user.phoneVerified==0" @click="noPhone">{{$t('uc.safe.set')}}</a>
                                <a class="btn" v-else-if="user.fundsVerified==0" @click="showItem(5)">{{$t('uc.safe.set')}}</a>
                                <a class="btn" v-else @click="showItemFundpwd()">{{$t('uc.safe.edit')}}</a>
                            </div>
                            <div class="account-detail" v-show="choseItem==5">
                                <!-- 设置 -->
                                <div class="detail-list" v-show="user.fundsVerified!=1">
                                    <Form ref="formValidate7" :model="formValidate7" :rules="ruleValidate" :label-width="85">
                                        <!-- newMPw -->
                                        <FormItem :label="$t('uc.safe.fundpwd')" prop="pw7">
                                            <Input v-model="formValidate7.pw7" size="large" type="password"></Input>
                                        </FormItem>
                                        <!-- newMPwConfirm -->
                                        <FormItem :label="$t('uc.safe.confirmpwd')" prop="pw7Confirm">
                                            <Input v-model="formValidate7.pw7Confirm" size="large" type="password"></Input>
                                        </FormItem>

                                        <!-- Button -->
                                        <FormItem>
                                            <Button type="warning" @click="handleSubmit('formValidate7')">{{$t('uc.safe.save')}}</Button>
                                            <Button @click="handleReset('formValidate7')" style="margin-left: 8px">{{$t('uc.safe.reset')}}</Button>
                                        </FormItem>
                                    </Form>
                                </div>
                                <!-- 修改 -->
                                <div class="detail-list" v-show="user.fundsVerified==1  && !fGetBackFundpwd">
                                    <Form ref="formValidate5" :model="formValidate5" :rules="ruleValidate" :label-width="95">
                                        <!-- oldPw -->
                                        <FormItem :label="$t('uc.safe.oldfundpwd')" prop="oldPw">
                                            <Input v-model="formValidate5.oldPw" size="large" type="password"></Input>
                                        </FormItem>
                                        <!-- newMPw -->
                                        <FormItem :label="$t('uc.safe.newfundpwd')" prop="newMPw">
                                            <Input v-model="formValidate5.newMPw" size="large" type="password"></Input>
                                        </FormItem>
                                        <!-- newMPwConfirm -->
                                        <FormItem :label="$t('uc.safe.confirmnewpwd')" prop="newMPwConfirm">
                                            <Input v-model="formValidate5.newMPwConfirm" size="large" type="password"></Input>
                                        </FormItem>
                                        <!-- 邮箱验证码 -->
                                        <!--<FormItem :label="$t('uc.safe.phonecode')" prop="vailCode5">-->
                                        <!--<Input v-model="formValidate5.vailCode5" size="large">-->
                                        <!--<div class="timebox" slot="append">-->
                                        <!--<Button @click="send(5)" :disabled="sendMsgDisabled5">-->
                                        <!--<span v-if="sendMsgDisabled5">{{time5+$t('uc.safe.second')}}</span>-->
                                        <!--<span v-if="!sendMsgDisabled5">{{$t('uc.safe.clickget')}}</span>-->
                                        <!--</Button>-->
                                        <!--</div>-->
                                        <!--</Input>-->
                                        <!--</FormItem>-->
                                        <p style="text-align:right;">
                                            <a @click="handleReset('formValidate8');fGetBackFundpwd=!fGetBackFundpwd" style="color:#f0ac19;">忘记密码?</a>
                                        </p>
                                        <!-- Button -->
                                        <FormItem>
                                            <Button type="warning" @click="handleSubmit('formValidate5')">{{$t('uc.safe.save')}}</Button>
                                            <Button @click="handleReset('formValidate5')" style="margin-left: 8px">{{$t('uc.safe.reset')}}</Button>
                                        </FormItem>
                                    </Form>
                                </div>
                                <!-- 找回 -->
                                <div class="detail-list" v-show="user.fundsVerified==1 && fGetBackFundpwd">
                                    <Form ref="formValidate8" :model="formValidate8" :rules="ruleValidate" :label-width="85">
                                        <!-- newMPw -->
                                        <FormItem :label="$t('uc.safe.newfundpwd')" prop="newMPw8">
                                            <Input v-model="formValidate8.newMPw8" size="large" type="password"></Input>
                                        </FormItem>
                                        <!-- newMPwConfirm -->
                                        <FormItem :label="$t('uc.safe.confirmnewpwd')" prop="newMPwConfirm8">
                                            <Input v-model="formValidate8.newMPwConfirm8" size="large" type="password"></Input>
                                        </FormItem>
                                        <!-- 邮箱验证码 -->
                                        <FormItem :label="$t('uc.safe.phonecode')" prop="vailCode5">
                                            <Input v-model="formValidate8.vailCode5" size="large">
                                            <div class="timebox" slot="append">
                                                <Button @click="send(5)" :disabled="sendMsgDisabled5">
                                                    <span v-if="sendMsgDisabled5">{{time5+$t('uc.safe.second')}}</span>
                                                    <span v-if="!sendMsgDisabled5">{{$t('uc.safe.clickget')}}</span>
                                                </Button>
                                            </div>
                                            </Input>
                                        </FormItem>
                                        <!-- Button -->
                                        <FormItem>
                                            <Button type="warning" @click="handleSubmit('formValidate8')">{{$t('uc.safe.save')}}</Button>
                                            <Button  @click="handleReset('formValidate8')" style="margin-left: 8px">{{$t('uc.safe.reset')}}</Button>
                                        </FormItem>
                                    </Form>
                                </div>
                            </div>
                        </div>

                        <!--  -->
                    </div>
                </section>
            </div>
        </div>
    </div>
    </div>
</template>
<script>
export default {
  components: {},
  data() {
    const validatePass = (rule, value, callback) => {
      if (value === "") {
        callback(new Error(this.$t("uc.safe.newpwdmsg1")));
      } else if (!/([a-zA-Z0-9]){6,18}/.test(value)) {
        callback(new Error(this.$t("uc.safe.newpwdmsg1")));
      } else {
        callback();
      }
    };
    const validatePassCheck = (rule, value, callback) => {
      if (value === "") {
        callback(new Error(this.$t("uc.safe.newpwdmsg2")));
      } else if (!/([a-zA-Z0-9]){6,18}/.test(value)) {
        callback(new Error(this.$t("uc.safe.newpwdmsg2")));
      } else if (value !== this.formValidate4.newPw) {
        callback(new Error(this.$t("uc.safe.newpwdmsg2")));
      } else {
        callback();
      }
    };
    const validateMPass = (rule, value, callback) => {
      if (value === "") {
        callback(new Error(this.$t("uc.safe.pwdmsg1")));
      } else if (!/([a-zA-Z0-9]){6,18}/.test(value)) {
        callback(new Error(this.$t("uc.safe.pwdmsg1")));
      } else {
        callback();
      }
    };
    const validateMPassCheck = (rule, value, callback) => {
      if (value === "") {
        callback(new Error(this.$t("uc.safe.pwdmsg2")));
      } else if (!/([a-zA-Z0-9]){6,18}/.test(value)) {
        callback(new Error(this.$t("uc.safe.pwdmsg2")));
      } else if (value !== this.formValidate5.newMPw) {
        callback(new Error(this.$t("uc.safe.pwdmsg2")));
      } else {
        callback();
      }
    };
    const validatepw7 = (rule, value, callback) => {
      if (value === "") {
        callback(new Error(this.$t("uc.safe.pwdmsg1")));
      } else if (!/([a-zA-Z0-9]){6,18}/.test(value)) {
        callback(new Error(this.$t("uc.safe.pwdmsg1")));
      } else {
        callback();
      }
    };
    const validatepw7check = (rule, value, callback) => {
      if (value === "") {
        callback(new Error(this.$t("uc.safe.pwdmsg1")));
      } else if (!/([a-zA-Z0-9]){6,18}/.test(value)) {
        callback(new Error(this.$t("uc.safe.pwdmsg2")));
      } else if (value !== this.formValidate7.pw7) {
        callback(new Error(this.$t("uc.safe.pwdmsg2")));
      } else {
        callback();
      }
    };
    const validateMPass8 = (rule, value, callback) => {
      if (value === "") {
        callback(new Error(this.$t("uc.safe.pwdmsg1")));
      } else if (!/([a-zA-Z0-9]){6,18}/.test(value)) {
        callback(new Error(this.$t("uc.safe.pwdmsg1")));
      } else {
        callback();
      }
    };
    const validateMPassCheck8 = (rule, value, callback) => {
      if (value === "") {
        callback(new Error(this.$t("uc.safe.pwdmsg2")));
      } else if (!/([a-zA-Z0-9]){6,18}/.test(value)) {
        callback(new Error(this.$t("uc.safe.pwdmsg2")));
      } else if (value !== this.formValidate8.newMPw8) {
        callback(new Error(this.$t("uc.safe.pwdmsg2")));
      } else {
        callback();
      }
    };
    return {
      fGetBackFundpwd: false,
      imgPreview: "",
      imgNext: "",
      imgLast: "",
      loginmsg: this.$t("common.logintip"),
      memberlevel:"",
      frontCardImg: require("../../assets/images/frontCardImg.png"),
      backCardImg: require("../../assets/images/backCardImg.png"),
      handCardImg: require("../../assets/images/HandCardImg.png"),

      uploadHeaders: { "x-auth-token": localStorage.getItem("TOKEN") },
      uploadUrl: this.host + "/uc/upload/oss/image",

      usernameS: "",
      user: {},
      choseItem: 0,
      accountValue: "1",
      formValidate2: {
        mail: "",
        vailCode1: "",
        password: ""
      },
      formValidate3: {
        mobile: "",
        vailCode2: "",
        password: ""
      },
      formValidate4: {
        oldPw: "",
        newPw: "",
        newPwConfirm: "",
        vailCode3: ""
      },
      formValidate5: {
        oldPw: "",
        newMPw: "",
        newMPwConfirm: ""
        // vailCode5: '',
      },
      formValidate6: {
        realName: "",
        idCard: ""
      },
      formValidate7: {
        pw7: "",
        pw7Confirm: ""
      },
      formValidate8: {
        newMPw8: "",
        newMPwConfirm8: "",
        vailCode5: ""
      },
      ruleValidate: {
        mail: [
          {
            required: true,
            type: "email",
            message: this.$t("uc.safe.emailtip"),
            trigger: "blur"
          }
        ],
        vailCode1: [
          {
            required: true,
            message: this.$t("uc.safe.codetip"),
            trigger: "blur"
          }
        ],
        mobile: [
          {
            required: true,
            message: this.$t("uc.safe.telnotip"),
            trigger: "blur"
          }
        ],
        vailCode2: [
          {
            required: true,
            message: this.$t("uc.safe.codetip"),
            trigger: "blur"
          }
        ],
        vailCode3: [
          {
            required: true,
            message: this.$t("uc.safe.codetip"),
            trigger: "blur"
          }
        ],
        password: [
          {
            required: true,
            type: "string",
            min: 6,
            message: this.$t("uc.safe.pwdmsg1"),
            trigger: "blur"
          }
        ],
        oldPw: [
          {
            required: true,
            type: "string",
            min: 6,
            message: this.$t("uc.safe.oldpwdtip"),
            trigger: "blur"
          }
        ],
        newPw: [
          {
            required: true,
            type: "string",
            min: 6,
            message: this.$t("uc.safe.newpwdmsg1"),
            trigger: "blur"
          },
          { validator: validatePass, trigger: "blur" }
        ],
        newPwConfirm: [
          {
            required: true,
            type: "string",
            min: 6,
            message: this.$t("uc.safe.newpwdmsg2"),
            trigger: "blur"
          },
          { validator: validatePassCheck, trigger: "blur" }
        ],
        newMPw: [
          {
            required: true,
            type: "string",
            min: 6,
            message: this.$t("uc.safe.pwdmsg1"),
            trigger: "blur"
          },
          { validator: validateMPass, trigger: "blur" }
        ],
        newMPwConfirm: [
          {
            required: true,
            type: "string",
            min: 6,
            message: this.$t("uc.safe.pwdmsg2"),
            trigger: "blur"
          },
          { validator: validateMPassCheck, trigger: "blur" }
        ],
        pw7: [
          {
            required: true,
            type: "string",
            min: 6,
            message: this.$t("uc.safe.pwdmsg1"),
            trigger: "blur"
          },
          { validator: validatepw7, trigger: "blur" }
        ],
        pw7Confirm: [
          {
            required: true,
            type: "string",
            min: 6,
            message: this.$t("uc.safe.pwdmsg2"),
            trigger: "blur"
          },
          { validator: validatepw7check, trigger: "blur" }
        ],
        vailCode5: [
          {
            required: true,
            message: this.$t("uc.safe.codetip"),
            trigger: "blur"
          }
        ],
        realName: [
          {
            required: true,
            message: this.$t("uc.safe.realnametip"),
            trigger: "blur"
          }
        ],
        idCard: [
          {
            required: true,
            message: this.$t("uc.safe.idcardtip"),
            trigger: "blur"
          }
        ],
        newMPw8: [
          {
            required: true,
            type: "string",
            min: 6,
            message: this.$t("uc.safe.pwdmsg1"),
            trigger: "blur"
          },
          { validator: validateMPass8, trigger: "blur" }
        ],
        newMPwConfirm8: [
          {
            required: true,
            type: "string",
            min: 6,
            message: this.$t("uc.safe.pwdmsg2"),
            trigger: "blur"
          },
          { validator: validateMPassCheck8, trigger: "blur" }
        ]
      },
      time1: 60, // 发送验证码倒计时
      time2: 60, // 发送验证码倒计时
      time3: 60, // 发送验证码倒计时
      time5: 60, // 发送验证码倒计时
      sendMsgDisabled1: false,
      sendMsgDisabled2: false,
      sendMsgDisabled3: false,
      sendMsgDisabled5: false
    };
  },
  methods: {
    beforeUpload(data) {
      if (data && data.size >= 1024000 * 2) {
        this.$Message.error("上传图片大小不能超过2M");
        return false;
      }
    },
    frontHandleSuccess(res, file,fileList) {
      this.$refs.upload1.fileList=[fileList[fileList.length-1]];
      if (res.code == 0) {
        this.frontCardImg = this.imgPreview = res.data;
      } else {
        this.$Message.error(res.message);
      }
    },
    backHandleSuccess(res, file,fileList) {
      this.$refs.upload2.fileList=[fileList[fileList.length-1]];
      if (res.code == 0) {
        this.backCardImg = this.imgNext = res.data;
      } else {
        this.$Message.error(res.message);
      }
    },
    handHandleSuccess(res, file,fileList) {
      this.$refs.upload3.fileList=[fileList[fileList.length-1]];
      if (res.code == 0) {
        this.handCardImg = this.imgLast = res.data;
      } else {
        this.$Message.error(res.message);
      }
    },
    noPhone() {
      this.$Message.info(this.$t("uc.safe.bindphonetip"));
      this.showItem(3);
    },
    showItemFundpwd() {
      this.fGetBackFundpwd = false;
      this.handleReset("formValidate5");
      this.showItem(5);
    },
    renderPw() {
      this.$Modal.confirm({
        title: this.$t("uc.safe.resetfundpwd"),
        onOk: () => {
          this.$Message.info("Clicked ok");
        },
        render: h => {
          return h("Input", {
            props: {
              value: this.value,
              autofocus: true
            },
            on: {
              input: val => {
                this.value = val;
              }
            }
          });
        }
      });
    },
    submit(name) {
      //实名认证
      if (name == "formValidate6") {
        if (this.imgPreview == "") {
          this.$Message.error(this.$t("uc.safe.upload_positivetip"));
          return false;
        }
        if (this.imgNext == "") {
          this.$Message.error(this.$t("uc.safe.upload_negativetip"));
          return false;
        }
        if (this.imgLast == "") {
          this.$Message.error(this.$t("uc.safe.upload_handtip"));
          return false;
        }
        let param = {};
        param["realName"] = this.formValidate6.realName;
        param["idCard"] = this.formValidate6.idCard;
        param["idCardFront"] = this.imgPreview;
        param["idCardBack"] = this.imgNext;
        param["handHeldIdCard"] = this.imgLast;
        this.$http
          .post(this.host + "/uc/approve/real/name", param)
          .then(response => {
            var resp = response.body;
            if (resp.code == 0) {
              this.member.realName = this.formValidate6.realName;
              this.$store.commit("setMember", this.member);
              this.$Message.success(this.$t("uc.safe.save_success"));
              this.getMember();
              this.choseItem = 0;
            } else {
              this.$Message.error(resp.message);
            }
          });
      }
      //邮箱认证
      if (name == "formValidate2") {
        let param = {};
        param["email"] = this.formValidate2.mail;
        param["code"] = this.formValidate2.vailCode1;
        param["password"] = this.formValidate2.password;
        this.$http
          .post(this.host + "/uc/approve/bind/email", param)
          .then(response => {
            var resp = response.body;
            if (resp.code == 0) {
              this.$Message.success(this.$t("uc.safe.save_success"));
              this.getMember();
              this.choseItem = 0;
            } else {
              this.$Message.error(resp.message);
            }
          });
      }
      //手机认证
      if (name == "formValidate3") {
        let param = {};
        param["phone"] = this.formValidate3.mobile;
        param["code"] = this.formValidate3.vailCode2;
        param["password"] = this.formValidate3.password;
        this.$http
          .post(this.host + "/uc/approve/bind/phone", param)
          .then(response => {
            var resp = response.body;
            if (resp.code == 0) {
              this.$Message.success(this.$t("uc.safe.save_success"));
              this.getMember();
              this.choseItem = 0;
            } else {
              this.$Message.error(resp.message);
            }
          });
      }
      //登录密码
      if (name == "formValidate4") {
        let param = {};
        param["oldPassword"] = this.formValidate4.oldPw;
        param["newPassword"] = this.formValidate4.newPw;
        param["code"] = this.formValidate4.vailCode3;
        this.$http
          .post(this.host + "/uc/approve/update/password", param)
          .then(response => {
            var resp = response.body;
            if (resp.code == 0) {
              this.$Message.success(this.$t("uc.safe.save_success"));
              this.getMember();
              this.choseItem = 0;
              localStorage.removeItem("MEMBER");
              localStorage.removeItem("TOKEN");
              this.$store.state.showLogout = true;
              this.$store.state.showLogin = false;
              let self = this;
              setTimeout(() => {
                self.$router.push("/login");
              }, 2000);
            } else {
              this.$Message.error(resp.message);
            }
          });
      }
      //修改资金密码
      if (name == "formValidate5") {
        let param = {};
        param["oldPassword"] = this.formValidate5.oldPw;
        param["newPassword"] = this.formValidate5.newMPw;
        // param['code'] = this.formValidate5.vailCode5
        this.$http
          .post(this.host + "/uc/approve/update/transaction/password", param)
          .then(response => {
            var resp = response.body;
            if (resp.code == 0) {
              this.$Message.success(this.$t("uc.safe.save_success"));
              this.handleReset("formValidate5");
              this.getMember();
              this.choseItem = 0;
            } else {
              this.$Message.error(resp.message);
            }
          });
      }
      //设置资金密码
      if (name == "formValidate7") {
        let param = {};
        param["jyPassword"] = this.formValidate7.pw7;
        this.$http
          .post(this.host + "/uc/approve/transaction/password", param)
          .then(response => {
            var resp = response.body;
            if (resp.code == 0) {
              this.$Message.success(this.$t("uc.safe.save_success"));
              this.getMember();
              this.choseItem = 0;
            } else {
              this.$Message.error(resp.message);
            }
          });
      }
      //找回资金密码
      if (name == "formValidate8") {
        let param = {};
        param["newPassword"] = this.formValidate8.newMPw8;
        param["code"] = this.formValidate8.vailCode5;
        this.$http
          .post(this.host + "/uc/approve/reset/transaction/password", param)
          .then(response => {
            var resp = response.body;
            if (resp.code == 0) {
              this.$Message.success(this.$t("uc.safe.save_success"));
              this.fGetBackFundpwd = false;
              this.handleReset("formValidate5");
              this.getMember();
              this.choseItem = 0;
            } else {
              this.$Message.error(resp.message);
            }
          });
      }
    },
    handleSubmit(name) {
      this.$refs[name].validate(valid => {
        if (valid) {
          this.submit(name);
        } else {
          this.$Message.error(this.$t("uc.safe.save_failure"));
        }
      });
    },
    handleReset(name) {
      this.$refs[name].resetFields();
    },
    showItem(index) {
      this.choseItem = index;
    },
    send(index) {
      let me = this;
      if (index == 1) {
        if (this.formValidate2.mail) {
          //获取邮箱code
          this.$http
            .post(this.host + "/uc/bind/email/code", {
              email: this.formValidate2.mail
            })
            .then(response => {
              var resp = response.body;
              if (resp.code == 0) {
                me.sendMsgDisabled1 = true;
                let interval = window.setInterval(function() {
                  if (me.time1-- <= 0) {
                    me.time1 = 60;
                    me.sendMsgDisabled1 = false;
                    window.clearInterval(interval);
                  }
                }, 1000);
              } else {
                this.$Message.error(resp.message);
              }
            });
        } else {
          this.$refs.formValidate2.validateField("mail");
        }
      } else if (index == 2) {
        if (this.formValidate3.mobile) {
          //获取手机code
          this.$http
            .post(this.host + "/uc/mobile/bind/code", {
              phone: this.formValidate3.mobile
            })
            .then(response => {
              var resp = response.body;
              if (resp.code == 0) {
                me.sendMsgDisabled2 = true;
                let interval = window.setInterval(function() {
                  if (me.time2-- <= 0) {
                    me.time2 = 60;
                    me.sendMsgDisabled2 = false;
                    window.clearInterval(interval);
                  }
                }, 1000);
              } else {
                this.$Message.error(resp.message);
              }
            });
        } else {
          this.$refs.formValidate3.validateField("mobile");
        }
      } else if (index == 3) {
        //登录密码获取手机code
        this.$http
          .post(this.host + "/uc/mobile/update/password/code")
          .then(response => {
            var resp = response.body;
            if (resp.code == 0) {
              me.sendMsgDisabled3 = true;
              let interval = window.setInterval(function() {
                if (me.time3-- <= 0) {
                  me.time3 = 60;
                  me.sendMsgDisabled3 = false;
                  window.clearInterval(interval);
                }
              }, 1000);
            } else {
              this.$Message.error(resp.message);
            }
          });
      } else if (index == 5) {
        //资金密码获取手机code
        this.$http
          .post(this.host + "/uc/mobile/transaction/code")
          .then(response => {
            var resp = response.body;
            if (resp.code == 0) {
              me.sendMsgDisabled5 = true;
              let interval = window.setInterval(function() {
                if (me.time5-- <= 0) {
                  me.time5 = 60;
                  me.sendMsgDisabled5 = false;
                  window.clearInterval(interval);
                }
              }, 1000);
            } else {
              this.$Message.error(resp.message);
            }
          });
      }
    },
    getMember() {
      //获取个人安全信息
      var self = this;
      this.$http
        .post(this.host + "/uc/approve/security/setting")
        .then(response => {
          var resp = response.body;
          if (resp.code == 0) {
            this.user = resp.data;
            this.usernameS = this.user.username.slice(0,1);
          } else {
            this.$Message.error(this.loginmsg);
            // this.$Message.error(this.$t('common.logintip'));
          }
        });
    }
  },
  computed: {
    member: function() {
      return this.$store.getters.member;
    },
    lang() {
      return this.$store.state.lang;
    }
  },
  created() {
    this.getMember();
    let level = this.$store.getters.member.memberRate;
    level == 0 && (this.memberlevel = "普通会员");
    level == 1 && (this.memberlevel = "超级群主");
    level == 2 && (this.memberlevel = "超级合伙人");
  }
};
</script>
<style scoped lang="scss">
button.ivu-btn{
  &:focus{
    box-shadow: 0 0 0 2px rgba(45,140,240,0);
  }
}
button.ivu-btn.ivu-btn-primary{
  box-shadow: 0 0 0 2px rgba(45,140,240,0);
}
.nav-right {
  padding-left: 15px;
  .user .user-top-icon {
    height: 80px;
    border-bottom: 1px dashed #27313e;
    position: relative;
    display: flex;
    justify-content: flex-start;
    align-items: center;
    padding: 0 50px;
  }
}
.uploadimgtip {
  position: relative;
  top: -20px;
  color: #f0a70a;
}
.account-box .account-in .account-item .account-detail {
  padding: 30px 0;
  // background: white;
  margin: 6px 0;
}

.account-box .account-in .account-item .account-detail .detail-list {
  width: 40%;
  margin: 0 auto;
}

.account-box
  .account-in
  .account-item
  .account-detail
  .detail-list
  .input-control {
  margin-bottom: 10px;
  height: 45px;
}

.detail-list .input-control .ivu-input-group-prepend {
  width: 63px;
}

.detail-list .input-control .ivu-input {
  height: 45px;
}

.account-box .account-in .account-item {
  margin-bottom: 10px;
}

.account-box .account-in .account-item .account-item-in {
  display: -webkit-box;
  display: -ms-flexbox;
  display: flex;
  -webkit-box-align: center;
  -ms-flex-align: center;
  align-items: center;
  padding: 15px 30px 15px 50px;
  // background-color: white;
  -webkit-box-shadow: 0 1px 0 0 rgba(69, 112, 128, 0.06);
  box-shadow: 0 1px 0 0 rgba(69, 112, 128, 0.06);
  font-size: 14px;
  color: #fff;
}

.account-box .account-in .account-item .account-item-in .icons {
  height: 17px;
  width: 17px;
  display: inline-block;
  margin-top: -1px;
  background-size: 100% 100%;
}

.account-box .account-in .account-item .account-item-in .yesImg {
  background-image: url(../../assets/img/overicon.png);
}

.icons.noImg {
  background-image: url(../../assets/img/noicon.png);
}

.account-box .account-in .account-item .account-item-in .card-number {
  width: 142px;
  height: 40px;
  margin-right: 15px;
  border-right: 1px dashed #27313e;
  padding: 0 15px;
  line-height: 40px;
  text-align: left;
  display: inline-block;
}

.account-box .account-in .account-item .account-item-in .bankInfo {
  width: 70%;
  text-align: left;
}

.account-box .account-in .account-item .account-item-in .btn {
  padding: 8px 10px;
  cursor: pointer;
}

.tips-g {
  color: #8994a3;
  font-size: 12px;
}

.gr {
  color: #b4b4b4;
}

.m1 {
  display: inline-block;
  width: 25px;
  height: 25px;
  background: url(../../assets/img/m1.png);
  background-size: 100% 100%;
  vertical-align: middle;
  margin-right: 5px;
}

.m2 {
  display: inline-block;
  width: 25px;
  height: 25px;
  background: url(../../assets/img/m2.png);
  background-size: 100% 100%;
  vertical-align: middle;
  margin-right: 5px;
}

.m3 {
  display: inline-block;
  width: 25px;
  height: 25px;
  background: url(../../assets/img/m3.png);
  background-size: 100% 100%;
  vertical-align: middle;
  margin-right: 5px;
}

.itp {
  display: inline-block;
}

.user-right {
  width: 80%;
}

.rightarea {
  padding-left: 15px !important;
  padding-right: 15px !important;
  margin-bottom: 60px !important;
}

.rightarea .rightarea-top {
  line-height: 75px;
  border-bottom: #f1f1f1 solid 1px;
}

.rightarea .trade-process {
  line-height: 30px;
  padding: 0 15px;
  background: #f1f1f1;
  display: inline-block;
  position: relative;
  margin-right: 20px;
}

.rightarea .trade-process.active {
  color: #eb6f6c;
  background: #f9f5eb;
}

.rightarea .trade-process .icon {
  background: #fff;
  border-radius: 20px;
  height: 20px;
  width: 20px;
  display: inline-block;
  line-height: 20px;
  text-align: center;
  margin-right: 10px;
}

.rightarea .trade-process .arrow {
  position: absolute;
  top: 10px;
  right: -5px;
  width: 0;
  height: 0;
  border-top: 5px solid transparent;
  border-bottom: 5px solid transparent;
  border-left: 5px solid #f1f1f1;
}

.rightarea .trade-process.active .arrow {
  border-left: 5px solid #f9f5eb;
}

.rightarea .rightarea-tabs {
  border: none;
}

.rightarea .rightarea-tabs li > a {
  width: 100%;
  height: 100%;
  padding: 0;
  margin-right: 0;
  font-size: 14px;
  color: #646464;
  border-radius: 0;
  border: none;
  display: flex;
  justify-content: center;
  align-items: center;
}

.rightarea .rightarea-tabs li > a:hover {
  background-color: #fcfbfb;
}

.rightarea .rightarea-tabs li {
  width: 125px;
  height: 40px;
  position: relative;
  margin: -1px 0 0 -1px;
  border: 1px solid #f1f1f1;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.rightarea .rightarea-tabs li.active {
  background-color: #fcfbfb;
}

.rightarea .rightarea-tabs li:last-child {
  border-right: 1px solid #f1f1f1;
}

.rightarea .rightarea-tabs li.active > a,
.rightarea .rightarea-tabs li:hover > a {
  color: #da2e22;
  border: none;
}

.rightarea .panel-tips {
  border: 3px solid #fdfaf3;
  color: #9e9e9e;
  font-size: 12px;
}

.rightarea .panel-tips .panel-header {
  background: #fdfaf3;
  line-height: 40px;
  margin-bottom: 15px;
}

.rightarea .panel-tips .panel-title {
  font-size: 16px;
}

.rightarea .recordtitle {
  cursor: pointer;
}

.user .top-icon {
  /* background: url("../../images/user/userplist.png") no-repeat 0 0; */
  width: 75px;
  height: 75px;
  display: inline-block;
}

.user .top-icon.intro {
  background-position: 0 -670px;
}

.user .user-info {
  padding: 0px 10px;
  background-color: #fff;
  color: #fff;
}

.user .user-info .user-info-top {
  border-bottom: 1px dashed #27313e;
  padding-bottom: 20px;
}

.user .user-info h5 {
  font-size: 18px;
}

.user .user-info h5 .iconfont {
  font-size: 20px;
  color: #e24a64;
  margin-left: 10px;
}

.user-avatar {
  display: flex;
  justify-content: space-between;
}

.user-icons {
  display: flex;
  align-self: center;
  width: 300px;
}

.user-icons .icons-in {
  height: 45px;
  width: 45px;
  border-radius: 50%;
  background-color: #00b5f6;
  display: flex;
  justify-content: center;
  align-items: center;
  align-self: center;
}

.user-icons .icons-in em {
  color: white;
  font-size: 20px;
  font-style: normal;
}

.user-icons .user-name {
  margin-left: 10px;
  display: flex;
  justify-content: flex-start;
  /* align-items: center; */
  flex-direction: column;
}

.user-icons .user-name span {
  display: flex;
  justify-content: flex-start;
  align-items: center;
  width: 225px;
  height: 52px;
  overflow: hidden;
  font-size: 14px;
  white-space: nowrap;
  text-overflow: ellipsis;
  -o-text-overflow: ellipsis;
}

.user-top-icon .trade-info {
  width: 420px;
  padding-left: 30px;
  display: flex;
}

.user-top-icon .trade-info .item {
  display: flex;
  flex-direction: column;
  width: 100%;
}

.user-top-icon .trade-info .item.capital {
  width: 60%;
}

.user-icons .user-name span.uid {
  color: #8994a3;
  font-size: 12px;
}

.circle-info {
  display: flex;
  justify-content: center;
  flex-direction: column;
  align-items: center;
  height: 80px;
  width: 80px;
  border-radius: 50%;
  border: 2px solid #ebeff5;
  margin-left: 14px;
}

.circle-info span {
  font-weight: bolder;
}

.circle-info p {
  color: #8994a3;
  margin: 0;
  padding: 0;
}

.circle-info p.count {
  color: #fff;
  font-size: 14px;
  font-weight: 600;
}

.user-avatar-public {
  background: #df9a00;
  border-radius: 50%;
  height: 52px;
  width: 52px;
  display: flex;
  justify-content: center;
  align-items: center;
  box-shadow: 0 1px 5px 0 rgba(71, 78, 114, 0.24);
  position: relative;
}

.user-avatar-public > .user-avatar-in {
  background: #f0a70a;
  border-radius: 50%;
  height: 42px;
  width: 42px;
  display: flex;
  justify-content: center;
  align-items: center;
  color: white;
}
/*新加样式*/
.router-link-active {
  color: red;
}
/* router-link-exact-active router-link-active */
.account-item-in i {
  color: #f0a70a !important;
}
.btn {
  color: #f0a70a;
}
.ivu-btn-primary {
  background-color: #f0a70a;
  border-color: #f0a70a;
}
</style>
<style lang="scss">
li.ivu-upload-list-file.ivu-upload-list-file-finish {
  &:hover {
    span {
      color: #f0a70a;
    }
  }
}

.idcard-title{
    font-size: 13px;
    margin-bottom: 10px;
}
.acc_sc{
    margin-top: 10px;
}
.idcard-desc{
    padding: 10px 150px 50px 150px;
    >p{
        font-size: 13px;
        margin-bottom: 10px;
        text-align:left;
        color: #828ea1;
    }
}

@media screen and (max-width:768px){
    .safe .nav-right .user .user-top-icon{
        padding: 0 0!important;
    }
    .safe .account-box .account-in .account-item .account-item-in{
        padding: 15px 0px 15px 0px;
    }
    .safe .account-box .account-in .account-item .account-item-in .bankInfo {
        width: 50%!important;
    }
    .safe .account-box .account-in .account-item .account-item-in .card-number{
        width: 100px!important;
    }
    .safe .user-icons .user-name span{
        width: 100px!important;
    }
}
</style>
