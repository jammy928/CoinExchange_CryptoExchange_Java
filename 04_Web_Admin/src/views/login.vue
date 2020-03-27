<style lang="less">
@import "./login.less";
</style>

<template>
	<div class="login" @keydown.enter="handle">
		<div class="login-con">
			<Card :bordered="false">
				<p slot="title">
					<Icon type="log-in"></Icon> 欢迎登录
				</p>
				<div class="form-con" v-if="!phoneNum">
					<Form ref="loginForm" :model="form" :rules="rules">
						<FormItem prop="username">
							<Input v-model="form.username" :disabled="btnDisable" placeholder="请输入用户名">
								<span slot="prepend">
									<Icon :size="16" type="person"></Icon>
								</span>
							</Input>
						</FormItem>

						<FormItem prop="password">
							<Input type="password" v-model="form.password" :disabled="btnDisable" placeholder="请输入密码">
								<span slot="prepend">
									<Icon :size="14" type="locked"></Icon>
								</span>
							</Input>
						</FormItem prop="captcha">

						<Row v-show="!phoneNum">
							<Col span="12">
								<Input v-model="form.captcha" placeholder="验证码" >
								<span slot="prepend">
									<Icon :size="14" type="locked"></Icon>
								</span></Input>
							</Col>
							<Col span="10" offset="2">
								<img :src="logimg" style='vertical-align: middle;width:95%;height:95%' @click="chanloimg"/>
							</Col>
						</Row>

						<FormItem style='margin-top:10px'>
							<Button @click="handle" type="warning" long>登录</Button>
						</FormItem>

						<p style='color:red;text-align:center' v-if="messshow">{{errormessage}}</p>
					</Form>
				</div>
				<div v-if="!!phoneNum">
					<Form>
						<FormItem>
							<p class="phone-num">{{ phoneNum | hidePhoneNum }}</p>
						</FormItem>
						<FormItem>
							<Input placeholder="请输入验证码" v-model="code" :class="{appendBtn: count===0}">
							 	<Button slot="append" v-if="count>0" :disabled="count>0">{{count}}s后重新获取</Button>
							 	<Button slot="append" v-else-if="count===0" type="success" @click="getCodeTwice">获取验证码</Button>
							</Input>
						</FormItem>
						<FormItem>
							<Row>
								<Col span="11">
									<Button @click="handle" type="warning" long>登录</Button>
								</Col>
								<Col span="11" offset="2">
									<Button @click="cancelSignIn" long>取消</Button>
								</Col>
							</Row>
						</FormItem>
					</Form>

				</div>
			</Card>
		</div>
	</div>
</template>

<script>
import Cookies from "js-cookie";
import store from "../store";

import { setStore, getStore, removeStore } from "@/config/storage";
import { otherRouter, appRouter } from "@/router/router.js";
import { BASICURL, getLoginCode, signIn, getCodeAgain } from "@/service/getData";

export default {
  data() {
    return {
			timer: '',
			btnDisable: false,
			count: 0,
      form: {
        username: null,
        password: null,
        captcha: null
			},
			code: null,
			phoneNum: null,
      messshow: false,
      errormessage: null,
      logimg: `${BASICURL}/admin/captcha?cid=ADMIN_LOGIN`,
      rules: {
        username: [{ required: true, message: "不能为空", trigger: "blur" }],
        captcha: [{ required: true, message: "不能为空", trigger: "blur" }],
        password: [{ required: true, message: "不能为空", trigger: "blur" }]
      },
      permissions: {}
    };
  },
  methods: {
		cancelSignIn() {
			Cookies.remove('userPhone');
			window.location.reload();
		},
		getCodeTwice() {
			this.count = 60;
			getCodeAgain({phone: this.phoneNum})
			.then(res => {
			})
			.catch(err => {console.log(err)})
		},
    chanloimg() {
      this.logimg = `${BASICURL}admin/captcha?cid=ADMIN_LOGIN&a=${Math.random().toFixed(
        2
      )}`;
    },
    processPermission(menu) {
      if (menu.name != "") this.permissions[menu.name] = 1;
      if (menu.subMenu != null && menu.subMenu.length > 0) {
        for (var i = 0; i < menu.subMenu.length; i++) {
          this.processPermission(menu.subMenu[i]);
        }
      }
		},
    handle() {
			if(!!this.phoneNum) {
				signIn({code: this.code})
				 .then(res => {
					  if (!res.code) {
							Cookies.set('user', res.data.admin.username, { expires: 7 });
							Cookies.set('userInfo', res.data.admin, { expires: 7 });
							setStore("leftSidebarList", res.data.permissions);
							this.$router.push({ name: "home_index" });
                  	   } else this.$Message.error(res.message);
        			}).catch(err => {console.log(err)} );
			}else{
				getLoginCode(this.form)
					.then(res => {
						if (!res.code) {
							this.phoneNum = res.data;
							Cookies.set('userPhone', this.phoneNum);
						} else this.$Message.error(res.message)
					})
					.catch(err => console.log(err));
			}
    }
  },
  beforeRouteLeave(to, from, next) {
		clearInterval(this.timer)
    window.location.reload();
    next();
  },
  created() {
		this.phoneNum = Cookies.get('userPhone');
		clearInterval(this.timer)
    this.logimg = `${BASICURL}admin/captcha?cid=ADMIN_LOGIN`;
	},
	filters: {
		hidePhoneNum(val) {
			return val.split('').fill('*',3,7).join('');
		}
	},
	watch: {
		count(newVal, oldVal) {
			if(newVal>0){
				this.timer = setTimeout(()=> {
					this.count--;
				}, 1000)
			}else{
				clearInterval(this.timer)
			}
		},
		phoneNum(newVal, oldVal) {
			if(!!newVal) this.count = 0;
		}
	}
};
</script>
