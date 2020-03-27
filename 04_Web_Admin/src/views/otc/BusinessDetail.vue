<template>
	<Card class="businessDetail">
		<p slot="title">
			商家信息
			<Button type="primary" size="small" @click="refreshPageManual">
				<Icon type="refresh"></Icon>
				刷新
			</Button>
		</p>
		<Spin class="loading" v-if="ifLoading" size="large"></Spin>
		<Row>
			<Col span="6">
				审核状态：<span class="status">{{ userInfo.status | filterStatus }}</span>
			</Col>
			<Col span="6">
				真实姓名：<span>{{ userInfo.realName }}</span>
			</Col>
			<Col span="6">
				保证金：<span>{{ userInfo.amount + userInfo.info.coinSymbol }}</span>
			</Col>
		</Row>
		<Row>
			<Col span="6">
				手机号：<span>{{ userInfo.info.telno }}</span>
			</Col>
			<Col span="6">
				微信号：<span>{{ userInfo.info.wechat }}</span>
			</Col>
			<Col span="6">
				QQ号：<span>{{ userInfo.info.qq }}</span>
			</Col>
		</Row>
		<Row>
			未通过原因：<span>{{ !userInfo.detail ? '无' : userInfo.detail }}</span>
		</Row>

		<Row class="imgs" type="flex" justify="space-around">
			<Col span="11">
				<img :src="userInfo.info.assetData" alt="个人数字资产证明"><br>
				<p style="color:#333">个人数字资产证明</p>
			</Col>
			<Col span="11">
				<img :src="userInfo.info.tradeData" alt="数字资产交易证明"><br>
				<p style="color:#333">数字资产交易证明</p>
			</Col>

		</Row>
	</Card>
</template>

<script>
import { authBusinessDetail } from '@/service/getData'
export default {
	name: 'BusinessDetail',
	data() {
		return {
			ifLoading: false,
			userInfo: {
				info: {}
			}
		}
	},
	methods: {
		refreshPageManual() {
			this.refreshPage();
		},
		refreshPage() {
			this.ifLoading = true;
			authBusinessDetail({id: this.$route.query.id})
			.then(res => {
				if(!res.code){
					this.userInfo = res.data;
				}else this.$Message.error(res.message)
				this.ifLoading = false;
			})
			.catch(err => console.log(err))
		}
	},
	created() {
		this.refreshPage();
	},
	filters: {
		filterStatus(val) {
			let arr = ['未认证', '认证-待审核', '认证-审核成功', '认证-审核失败', '保证金不足'];
			return arr[val];
		}
	}
}
</script>

<style lang="less" scoped>
	.businessDetail{
		font-size: 16px;
		color: #adadad;
		.ivu-row {
			.status{
				color: #ec0909;
			}
			line-height: 60px;
		}
		.ivu-row-flex.imgs{
			margin-top: 70px;
			img{
				width: 100%;
				height: 300px;
				background-color: red;
			}
			p{
				text-align: center;
			}
		}
		.loading{
			position: absolute;
			z-index: 100;
			top: 0;
			left: 0;
			width: 100%;
			height: 100%;
			background: rgba(255, 255, 255, .8);
		}
	}
</style>