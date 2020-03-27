<style lang="less">
	@import './styles/common.less';
	@import "./styles/reset-iview-style.less";
</style>
<template>
    <div id="main" class="app-main">
        <router-view></router-view>
			<transition name="loadingFade">
				<div class="loadingMask" v-show="loading">
					<div class="wrapper">
						<Icon type="load-c" class="loadingIcon"></Icon>
						<p>加载中...</p>
					</div>
				</div>
			</transition>
			<!-- <transition name="loadingFade"> -->
				<div class="memberCheckMask" v-show="memberCheckMask">
					<div class="wrapper">
						<Steps :current="imgIndex">
								<Step title="" content="身份证正面"></Step>
								<Step title="" content="身份证手持"></Step>
								<Step title="" content="身份证反面"></Step>
						</Steps>
						<Row class="img">
							<Spin size="large" v-if="!memberCheckImgInfo.imgArr"></Spin>
							<img :src="!memberCheckImgInfo.imgArr ? '' : Object.values(memberCheckImgInfo.imgArr[imgIndex])" alt="">
							<span class="preStep" v-show="!!imgIndex"  @click="imgIndex--">上一步 < </span>
							<span class="nextStep" v-show="!(imgIndex===2)" @click="imgIndex++"> > 下一步</span>
						</Row>
						<Row class="btns">
							<Row v-show="imgIndex!==2">
								<Col span="4" offset="10" >
									<Button type="primary" long @click="cancelMemberCheck">取消</Button>
								</Col>
							</Row>

							<Row v-show="imgIndex===2">
								<Col span="4" offset="7" >
									<Button type="info" long @click="memberPass(true)">审核通过</Button>
								</Col>
								<Col span="4" offset="2" >
									<Button type="error" long @click="memberPass(false)">审核不通过</Button>
								</Col>
							</Row>
							<Modal
								v-model="rejectModel"
								:width="450"
								title="请备注失败原因(选填)"
								@on-ok="noPass"
								@on-cancel="cancelMemberCheck">
								<Input type="textarea" v-model="rejectReason"></Input>
							</Modal>
						</Row>
					</div>
				</div>
			<!-- </transition>	 -->
				<div class="businessCheckMask" v-show="businessCheckMask">
					<div class="wrapper">
						<!-- <Steps :current="authImgIndex">
								<Step title="" content="个人数字资产证明"></Step>
								<Step title="" content="数字资产交易证明"></Step>
						</Steps> -->
						<Row class="title">
							<Col span="12" :class="{ active: !authImgIndex}">① 个人数字资产证明</Col>
							<Col span="12" :class="{ active: !!authImgIndex}">② 数字资产交易证明</Col>
						</Row>
						<Row class="businessInfo">
							<Col span="8">手机号：{{ businessCheckInfo.telno  }}</Col>
							<Col span="8">微信号：{{ businessCheckInfo.wechat }}</Col>
							<Col span="8">QQ号：{{ businessCheckInfo.qq }}</Col>
							<Col span="8">保证金：{{ `${businessCheckInfo.amount}${businessCheckInfo.coinSymbol}` }}</Col>
							<!-- businessCheckInfo.assetData -->
							<!-- businessCheckInfo.tradeData -->
						</Row>
						<Row class="img">
							<Spin size="large" v-if="!authImg.length"></Spin>
							<img :src="authImg" >
							<span class="preStep" v-show="!authImgStep"  @click="switchAuthImg(true)">上一步 < </span>
							<span class="nextStep" v-show="authImgStep" @click="switchAuthImg(false)"> > 下一步</span>
						</Row>

						<Row class="btns">
							<Row v-show="authImgStep">
								<Col span="4" offset="10" >
									<Button type="primary" long @click="cancelBusinessCheck">取消</Button>
								</Col>
							</Row>

							<Row v-show="!authImgStep">
								<Col span="4" offset="7" >
									<Button type="info" long @click="businessPass(true)">审核通过</Button>
								</Col>
								<Col span="4" offset="2" >
									<Button type="error" long @click="businessPass(false)">审核不通过</Button>
								</Col>
							</Row>
							<Modal
								v-model="rejectBusinessModel"
								:width="450"
								title="请备注失败原因(选填)"
								@on-ok="businessNoPass"
								@on-cancel="cancelBusinessCheck">
								<Input type="textarea" v-model="rejectBusinessReason"></Input>
							</Modal>
						</Row>
					</div>
				</div>
    </div>
</template>

<script>
import store from "./store";
import axios from "axios";
import { memberCheckPass, memberCheckNotPass, businessAudit } from '@/service/getData';

export default {
  data() {
    return {
			theme: this.$store.state.app.themeColor,
			imgIndex: 0,
			rejectModel: false,
			rejectReason: '',
			authImg: '',
			authImgIndex: 0,
			authImgStep: true,
			rejectBusinessModel: false,
			rejectBusinessReason: ''
    };
  },
  created() {
    let permission = store.state.app.permissionData;
	var nes = JSON.parse(localStorage.getItem("ROLEINFO"));
	},
    methods: {
		businessNoPass() {
			let userInfo = this.$store.state.user.businessCheckInfo;
			businessAudit(userInfo.member.id, { status: 3, detail: this.rejectBusinessReason }).then(res => {
				if(!res.code) {
					this.$Message.success(res.message);
				}else {
					this.$Message.error(res.message);
				    this.$store.commit('changeDate', Date.now());
				}
			})
		},
		switchAuthImg(bol) {
			if(bol) {
				this.authImg = this.businessCheckInfo.assetData;
				this.authImgIndex = 0;
			} else {
				this.authImg = this.businessCheckInfo.tradeData;
				this.authImgIndex = 1;
			}
			this.authImgStep = !this.authImgStep;
		},
		noPass() {
			memberCheckNotPass(this.memberCheckImgInfo.id, { rejectReason: this.rejectReason })
			.then(res => {
				if(!res.code) {
					this.$Message.success(res.message);
				}else this.$Message.error(res.message);
				this.$store.commit('changeDate', Date.now());
				this.cancelMemberCheck();
			})
			.catch(err => {console.log(err)	})
				console.log(this.memberCheckImgInfo);
		},
		cancelMemberCheck() {
			this.imgIndex = 0;
			this.authImgStep = true;
			this.$store.commit('switchMemberMask', false);
			this.$store.commit('memeberCheckImg', []);
			this.rejectReason = '';
		},
		cancelBusinessCheck() {
			this.authImgIndex = 0;
			this.authImgStep = true;
			this.rejectBusinessReason = '';
			this.$store.commit('switchBusinessMask', false);
		},
		memberPass(bol) {
			if(bol) {
				memberCheckPass(this.memberCheckImgInfo.id)
				.then(res => {
					if(!res.code) {
						this.$Message.success(res.message);
					}else this.$Message.error(res.message);
					this.$store.commit('changeDate', Date.now());
					this.cancelMemberCheck();
				})
				.catch(err => {console.log(err)	})
			}else{
				this.$store.commit('switchMemberMask', false);
				this.rejectModel = true;
			}
		},
		businessPass(bol) {
			let userInfo = this.$store.state.user.businessCheckInfo;
			if(bol) {
				businessAudit(userInfo.member.id, { status: 2, detail: '' })
				.then(res => {
					if(!res.code) {
						this.$Message.success(res.message);
					}else this.$Message.error(res.message);
					this.authImgIndex = 0;
					this.authImgStep = true;
					this.$store.commit('changeDate', Date.now());
					this.$store.commit('switchBusinessMask', false);
				})
			} else {
				this.$store.commit('switchBusinessMask', false);
				this.rejectBusinessModel = true;
			}
		},
	},
  computed: {
		loading() {
			return this.$store.state.user.pageLoading;
		},
		memberCheckMask() {
			return this.$store.state.user.memberCheckMask;
		},
		businessCheckMask() {
			return this.$store.state.user.businessCheckMask;
		},
		memberCheckImgInfo() {
			return this.$store.state.user.memberCheckImgInfo;
		},
		businessCheckInfo() {
			if(!this.$store.state.user.businessCheckInfo.authInfo) return {};
			else {
				let authInfoObj = JSON.parse(this.$store.state.user.businessCheckInfo.authInfo);
				this.authImg = authInfoObj.assetData;
				return authInfoObj;
			}
		}
	}
};
</script>
<style>
	th .ivu-table-cell{
		display: inline-block;
	    word-wrap: normal;
	    vertical-align: middle;
	    font-size: 11px;
	    font-weight: bold;
	}
	.ivu-btn-small{
		padding: 2px 5px;
	    font-size: 11px;
	    border-radius: 3px;
	}
	.ivu-btn>.ivu-icon+span, .ivu-btn>span+.ivu-icon{
		margin-left: 0;
	}

	.ivu-table-cell{
		font-size: 11px;
	}
</style>
