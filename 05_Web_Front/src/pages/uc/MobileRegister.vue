<template>
  <div class="login_form mob-login">
    <div class="login_right">
      <div style="color: #F90;margin-bottom: 60px;margin-top: 60px;">
        <h1 style="border-left: 5px solid #F90;padding-left: 10px;letter-spacing: 2px;line-height:30px;height:30px;">BIZZAN.COM</h1>
        <p style="padding-left:15px;letter-spacing: 6px;">严选全球优质数字资产</p>
        <div style="margin-left:5px;letter-spacing: 2px;margin-top: 10px;color: rgb(226, 226, 227);font-size:13px;padding: 5px 10px;">安全 ● 诚实 ● 公平 ● 热情 ● 开放</div>
      </div>
      <Form v-if="allowRegister" ref="formInline" :model="formInline" :rules="ruleInline" inline>
        <FormItem prop="username" style="display:none;">
          <Input type="text" v-model="formInline.username" :placeholder="$t('uc.regist.username')">
          </Input>
        </FormItem>
        <FormItem prop="user">
          <Input type="text" v-model="formInline.user" :placeholder="key">
            <Select v-model="country" slot="prepend" style="width: 65px;border-bottom: 1px solid #27313e;">
              <Option value="中国" label="+86"><span>+86</span><span style="margin-left:10px;color:#ccc">中国</span></Option>
              <Option value="新加坡" label="+65"><span>+65</span><span style="margin-left:10px;color:#ccc">新加坡</span></Option>
              <Option value="韩国" label="+82"><span>+82</span><span style="margin-left:10px;color:#ccc">韩国</span></Option>
              <Option value="日本" label="+81"><span>+81</span><span style="margin-left:10px;color:#ccc">日本</span></Option>
              <Option value="泰国" label="+66"><span>+66</span><span style="margin-left:10px;color:#ccc">泰国</span></Option>
              <Option value="俄罗斯" label="+7"><span>+7</span><span style="margin-left:10px;color:#ccc">俄罗斯</span></Option>
              <Option value="英国" label="+44"><span>+44</span><span style="margin-left:10px;color:#ccc">英国</span></Option>
              <Option value="越南" label="+84"><span>+84</span><span style="margin-left:10px;color:#ccc">越南</span></Option>
              <Option value="印度" label="+91"><span>+91</span><span style="margin-left:10px;color:#ccc">印度</span></Option>
              <Option value="意大利" label="+39"><span>+39</span><span style="margin-left:10px;color:#ccc">意大利</span></Option>
              <Option value="香港" label="+852"><span>+852</span><span style="margin-left:10px;color:#ccc">香港</span></Option>
              <Option value="马来西亚" label="+60"><span>+60</span><span style="margin-left:10px;color:#ccc">马来西亚</span></Option>
              <Option value="台湾省" label="+886"><span>+886</span><span style="margin-left:10px;color:#ccc">台湾省</span></Option>
              <Option value="土耳其" label="+90"><span>+90</span><span style="margin-left:10px;color:#ccc">土耳其</span></Option>
            </Select>
          </Input>
        </FormItem>

        <FormItem prop="code" v-show="showCode">
          <Input type="text" v-model="formInline.code" :placeholder="$t('uc.regist.smscode')">
          </Input>
          <input id="sendCode" @click="sendCode();" type="Button" shape="circle" :value="sendcodeValue" :disabled='codedisabled'>
          </input>
        </FormItem>
        <FormItem prop="password"  class="password">
          <Input type="password" v-model="formInline.password" :placeholder="$t('uc.regist.pwd')">
          </Input>
        </FormItem>
        <FormItem prop="repassword"  class="password">
          <Input type="password" v-model="formInline.repassword" :placeholder="$t('uc.regist.repwd')">
          </Input>
        </FormItem>
        <FormItem prop="promotion">
          <Input type="text" v-model="formInline.promotion">
            <span slot="prepend">{{$t('uc.regist.promotion')}} :</span>
          </Input>
        </FormItem>
        <div class="check-agree" style="">
          <label>
            <Checkbox v-model="agree">{{$t('uc.regist.agreement')}}</Checkbox>
          </label>
          <a v-if="lang=='简体中文'" href="/helpdetail?cate=1&id=5&cateTitle=常见问题" target="_blank" style="">《{{$t('uc.regist.userprotocol')}}》</a>
          <a v-if="lang=='English'" href="/helpdetail?cate=1&id=35&cateTitle=Privacy Policy" target="_blank" style="">《{{$t('uc.regist.userprotocol')}}》</a>
        </div>
        <FormItem>
          <Button class="register_btn" @click="handleSubmit('formInline')" :disabled="registing">{{$t('uc.regist.regist')}}</Button>
        </FormItem>
      </Form>
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
    <div class="app_bottom_reg">
        <div class="left_logo">
          <img style="float:left;" src="../../assets/images/applogo.png"></img>
          <div style="float:left;height: 40px;line-height:40px;color:#000;">{{$t("cms.downloadslogan")}}</div>
        </div>
        <div class="right_btn_wrap"><router-link to="/app" class="right_btn">{{$t("cms.download")}}</router-link></div>
      </div>
  </div>
</template>
<style scoped lang="scss">
#page4 {
  background: transparent;
  padding: 80px 0 80px 0;
  ul {
    width: 99%;
    margin: 0 auto;
    li {
      flex: 0 0  25%;
      display: inline-block;
      width: 100%;
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
.login_form {
  position: relative;
  .login_right {
    padding: 20px 30px;
    background: transparent;
    width: 100%;
    padding-top: 60px;
    border-radius: 5px;
    .tel-title{
      color: #fff;
      text-align: left;
      margin-bottom: 30px;
    }
    form.ivu-form.ivu-form-label-right.ivu-form-inline {
      .ivu-form-item {
        .ivu-form-item-content {
          .register_btn.ivu-btn {
            width: 100%;
            background-color: #f0ac19;
            outline: none;
            border-color: #f0ac19;
            color: #fff;
            border-radius: 5px;
            font-size: 18px;
            margin-top: 20px;
            &:focus {
              -moz-box-shadow: 2px 2px 5px transparent, -2px -2px 4px transparent;
              -webkit-box-shadow: 2px 2px 5px transparent, -2px -2px 4px transparent;
              box-shadow: 2px 2px 5px transparent, -2px -2px 4px transparent;
            }
          }
          .ivu-input-wrapper.ivu-input-type {
            .ivu-input {
              border: 1px solid red;
            }
          }
          #sendCode {
            position: absolute;
            border: 1px solid #0b1520;
            background: transparent;
            top: -10px;
            outline: none;
            right: 0;
            width: 30%;
            color: #f0ac19;
            cursor: pointer;
          }
        }
      }
      .check-agree {
        color: #979797;
        display: inline-block;
        line-height: 30px;
        font-size: 12px;
        cursor: default;
        a {
          color: #f0ac19;
          margin-left: -10px;
        }
        .ivu-checkbox-wrapper.ivu-checkbox-wrapper-checked {
          .ivu-checkbox.ivu-checkbox-checked {
            .ivu-checkbox-inner {
              border: 1px solid #f0ac19;
              background-color: #f0ac19;
            }
          }
        }
      }
    }
  }
}

.login_title {
  text-align: center;
  height: 80px;
  font-size: 25px;
}

#captcha {
  width: 100%;
  display: inline-block;
}
.show {
  display: block;
}
.hide {
  display: none;
}
#notice {
  color: red;
}
#wait {
  text-align: left;
  color: #666;
  margin: 0;
}
.tel-title {
  font-size: 25px;
}
.login_left {
  display: none;
}
</style>
<script>
//   import gtInit from '../../assets/js/gt.js';
import $ from "jquery";
export default {
  data() {
    const validateUser = (rule, value, callback) => {
      if (this.changeActive == 0) {
        var reg = /^[1][3,4,5,6,7,8,9][0-9]{9}$/;
        if (value == "") {
          callback(new Error(this.$t("uc.regist.teltip")));
        } else if (!reg.test(this.formInline.user)) {
          callback(new Error(this.$t("uc.regist.telerr")));
        } else {
          callback();
        }
      } else {
        var reg = /^[a-z0-9]+([._\\-]*[a-z0-9])*@([a-z0-9]+[-a-z0-9]*[a-z0-9]+.){1,63}[a-z0-9]+$/;
        reg = /^(\w)+(\.\w+)*@(\w)+((\.\w{2,3}){1,3})$/;
        if (value == "") {
          callback(new Error(this.$t("uc.regist.emailtip")));
        } else if (!reg.test(this.formInline.user)) {
          callback(new Error(this.$t("uc.regist.emailerr")));
        } else {
          callback();
        }
      }
    };
    const validateRepassword = (rule, value, callback) => {
      if (value === "") {
        callback(new Error(this.$t("uc.regist.confirmpwdtip")));
      } else if (value !== this.formInline.password) {
        callback(new Error(this.$t("uc.regist.confirmpwderr")));
      } else {
        callback();
      }
    };
    return {
      country: "中国",
      codedisabled:false,
      sendcodeValue: this.$t("uc.regist.sendcode"),
      isRegister: false,
      ticket: "",
      randStr: "",
      registing: false,
      captchaObj: null,
      modal1: false,
      _captchaResult: null,
      agree: true,
      allowRegister: true,
      buttonLists: [
        {
          text: this.$t("uc.regist.telregist")
        },
        {
          text: this.$t("uc.regist.emailregist")
        }
      ],
      areas: [],
      changeActive: 0,
      showCode: true,
      countdown: 60,
      formInline: {
        username: "",
        // country: "",
        user: "",
        code: "",
        areaCode: "",
        password: "",
        repassword: "",
        promotion: ""
      },
      ruleInline: {
        user: [{ validator: validateUser, trigger: "blur" }],
        code: [
          {
            required: true,
            message: this.$t("uc.regist.smscodetip"),
            trigger: "blur"
          }
        ],
        password: [
          {
            required: true,
            message: this.$t("uc.regist.pwdtip"),
            trigger: "blur"
          },
          {
            type: "string",
            min: 6,
            message: this.$t("uc.regist.pwdmsg"),
            trigger: "blur"
          }
        ],
        repassword: [{ validator: validateRepassword, trigger: "blur" }]
      },
      key: "",
      code: ""
    };
  },
  watch: {
    changeActive: function(val) {
      this.$refs["formInline"].resetFields();
      // if (val == 0) this.initGtCaptcha();
    },
    lang: function() {
      this.updateLangData();
    }
  },
  computed: {
    lang: function() {
      return this.$store.state.lang;
    },
    isLogin: function() {
      return this.$store.getters.isLogin;
    }
  },
  created: function() {
    window.scrollTo(0, 0);
    // var oDiv = document.getElementById("example-navbar-collapse");
    // oDiv && (oDiv.className = "navbar-collapse collapse");
    this.init();
    this.actives(this.changeActive);
    if(this.$route.query.code != undefined && this.$route.query.code != "" && this.$route.query.code != null){
        this.formInline.promotion = this.$route.query.code;
    }else{
        this.formInline.promotion = "";
    }
  },
  methods: {
    updateLangData() {
      this.buttonLists = [
        {
          text: this.$t("uc.regist.telregist")
        },
        {
          text: this.$t("uc.regist.emailregist")
        }
      ];

      if (this.changeActive == 0) {
        this.key = this.$t("uc.regist.telno");
      } else {
        this.key = this.$t("uc.regist.email");
      }
    },
    init() {
      this.$store.commit("navigate", "nav-other");
      this.$store.state.HeaderActiveName = "0";
      if (this.isLogin) {
        this.$router.push("/");
      }
      window.document.title = (this.lang == "简体中文" ? "新用户注册 - " : "New Register - ") + "币严 | 全球比特币交易平台 | 全球数字货币交易平台";
      // this.getAreas();
      // this.initGtCaptcha();
    },
    initGtCaptcha() {
      // 直接生成一个验证码对象
      var self = this;
      var captcha1 = new TencentCaptcha("2031827463", function(res) {
        res.ret == 0 &&
          (self.isRegister = true) &&
          (self.ticket = res.ticket) &&
          (self.randStr = res.randstr) &&
          self.success();
      });
      captcha1.show(); // 显示验证码
    },
    onAreaChange(value) {
      for (var i = 0; i < this.areas.length; i++) {
        if (this.areas[i].zhName == value) {
          this.formInline.areaCode = this.areas[i].areaCode;
        }
      }
    },
    getAreas() {
      this.$http.post(this.host + this.api.common.area).then(response => {
        var resp = response.body;
        this.areas = resp.data;
        this.formInline.country = this.areas[0].zhName;
        this.formInline.areaCode = this.areas[0].areaCode;
      });
    },
    actives: function(index) {
      this.changeActive = index;
      if (this.changeActive == 0) {
        this.showCode = true;
        this.key = this.$t("uc.regist.telno");
        this.ruleInline.code = [
          {
            required: true,
            message: this.$t("uc.regist.smscodetip"),
            trigger: "blur"
          }
        ];
      } else {
        this.showCode = false;
        this.key = this.$t("uc.regist.email");
        this.ruleInline.code = [];
      }
    },
    handleSubmit(name) {
      this.$refs[name].validate(valid => {
        if (valid) {
          if (this.agree == true) {
            if (this.changeActive == 1) {
              this.openValidateModal();
            } else {
              if (this.isRegister) {
                this.registing = true;
                var params = {};
                params["phone"] = this.formInline.user;
                params["username"] = this.formInline.username + this.formInline.user;
                params["password"] = this.formInline.password;
                params["promotion"] = this.formInline.promotion; // 邀请码
                params["code"] = this.formInline.code;
                params["country"] = this.country;//"中国";
                params["superPartner"] = "";//this.formInline.superType;
                params["ticket"] = this.ticket;
                params["randStr"] = this.randStr;

                this.$http
                  .post(this.host + "/uc/register/phone", params)
                  .then(response => {
                    this.registing = false;
                    var resp = response.body;
                    if (resp.code == 0) {
                      if (
                        this.formInline.superType == "1" ||
                        this.formInline.superType == "2"
                      ) {
                        this.$Notice.success({
                          title: this.$t("common.tip"),
                          desc: "已注册成功"
                        });
                        var that = this;
                        setTimeout(() => {
                          that.$router.push("/app");
                        }, 3000);
                      } else {
                        this.$Notice.success({
                          title: this.$t("common.tip"),
                          desc: resp.message
                        });
                        var that = this;
                        setTimeout(() => {
                          that.$router.push("/app");
                        }, 3000);
                      }
                    } else {
                      this.$Notice.error({
                        title: this.$t("common.tip"),
                        desc: resp.message
                      });
                    }
                  });
              } else {
                this.$Notice.error({
                  title: this.$t("common.tip"),
                  desc: "请输入正确的验证码"
                });
              }
            }
          } else {
            this.$Notice.error({
              title: this.$t("common.tip"),
              desc: this.$t("uc.regist.agreementtip")
            });
          }
        }else{
          console.log("999");
        }
      });
    },
    settime() {
      this.sendcodeValue = this.$t("uc.regist.resendcode") + this.countdown + ")";
      this.codedisabled = true;
      var _this = this;
      let timercode = setInterval(() => {
        _this.countdown--;
        _this.sendcodeValue = _this.$t("uc.regist.resendcode") + _this.countdown + ")";
        if (this.countdown <= 0) {
          clearInterval(timercode);
          _this.codedisabled = false;
          _this.sendcodeValue = _this.$t("uc.regist.sendcode");
          _this.countdown = 60;
        }
      }, 1000);
    },
    sendCode() {
      var mobilePhone = this.formInline.user;
      let reg = /^[1][3,4,5,6,7,8,9][0-9]{9}$/;
      if (mobilePhone == "" || !reg.test(mobilePhone)) {
        this.$Message.error(this.$t("uc.regist.teltip"));
        return;
      } else {
        this.initGtCaptcha();
      }
    },
    success() {
      var params = {};
      params["phone"] = this.formInline.user;
      params["country"] = "中国";
      var reg = /^[1][3,4,5,6,7,8,9][0-9]{9}$/;
      reg.test(params["phone"]) && this.$http.post(this.host + "/uc/mobile/code", params).then(response => {
            var resp = response.body;
            resp.code == 0 && this.$Notice.success({title: this.$t("common.tip"),desc: resp.message});
            resp.code == 0 && this.settime();
            resp.code != 0 && this.$Notice.error({title: this.$t("common.tip"),desc: resp.message});
          });
      !reg.test(params["phone"]) &&this.$Notice.error({title: this.$t("common.tip"),desc: this.$t("uc.finance.withdraw.telerr")});
    }
  }
};
</script>
<style lang="scss">
.login_form {
  .login_right {
    form.ivu-form.ivu-form-label-right.ivu-form-inline {
      text-align:center;
      .ivu-form-item {
        .ivu-form-item-content {
          .ivu-input-wrapper.ivu-input-type {
            .ivu-input {
              border: none;
              border-bottom: 1px solid #27313e;
              font-size: 14px;
              background:transparent;
              border-radius:0;
              // color:#fff;
              &:focus {
                border: none;
                border-bottom: 1px solid #27313e;
                -moz-box-shadow: 2px 2px 5px transparent, -2px -2px 4px transparent;
                -webkit-box-shadow: 2px 2px 5px transparent, -2px -2px 4px transparent;
                box-shadow: 2px 2px 5px transparent, -2px -2px 4px transparent;
              }
            }
          }
        }
      }
      .check-agree {
        .ivu-checkbox-wrapper {
          .ivu-checkbox-input {
            &:focus {
              border: none;
              outline: none;
              -moz-box-shadow: 2px 2px 5px transparent, -2px -2px 4px transparent;
              -webkit-box-shadow: 2px 2px 5px transparent, -2px -2px 4px transparent;
              box-shadow: 2px 2px 5px transparent, -2px -2px 4px transparent;
            }
          }
        }
        .ivu-checkbox-wrapper.ivu-checkbox-wrapper-checked {
          .ivu-checkbox.ivu-checkbox-checked {
            .ivu-checkbox-inner {
              border: 1px solid #f0ac19;
              background-color: #f0ac19;
            }
          }

        }
        .ivu-checkbox-wrapper.ivu-checkbox-default{
          .ivu-checkbox{
            .ivu-checkbox-inner{
              background:transparent;
            }
          }
        }
      }
    }
  }
}
</style>
<style>
  .ivu-select-single .ivu-select-selection .ivu-select-placeholder, .ivu-select-single .ivu-select-selection .ivu-select-selected-value{
    padding-right: 20px;
  }
  .ivu-select-single .ivu-select-selection {
      background-color: transparent;
  }
  .ivu-select-arrow{
    right: 4px;
  }
  .mob-login .ivu-form-item-error .ivu-input-group-append, .mob-login .ivu-form-item-error .ivu-input-group-prepend, .mob-login .ivu-input-group-append, .mob-login .ivu-input-group-prepend{
    background-color: #0b1520!important;
    border-top:none;
    border-left: none;
    border-right: none;
  }

  .ivu-select-item span:first-child{
    display: inline-block;
    width: 30px;
    text-align: left;
  }
  .ivu-input-group-append .ivu-select-selection, .ivu-input-group-prepend .ivu-select-selection{
    margin: 0;
  }
  .app_bottom_reg{
    z-index: 999;
    position: fixed;
    bottom: 0;
    left: 0;
    width: 100%;
    height: 40px;
    background: rgba(242,246,250,1) !important;
  }
  .app_bottom_reg .left_logo img{
    height: 30px;margin-top:5px;border-radius: 5px;margin-left: 5px;margin-right: 5px;
  }
  .app_bottom_reg .right_btn_wrap{
    float: right;height: 40px;line-height: 40px;margin-right: 5px;

  }
  .app_bottom_reg .right_btn{
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
