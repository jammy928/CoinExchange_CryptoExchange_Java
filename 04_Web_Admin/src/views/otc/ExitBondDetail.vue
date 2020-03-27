<template>
	<div class="detail">
		<Row>
			<Card class="card">
				<p slot="title">
					<Button type="primary" size="small" @click="refreshPageManual">
						<Icon type="refresh" @click="refreshPageManual"></Icon>
						刷新
					</Button>
				</p>
				<Row>
					<h3>退保申请</h3>
					<Row>
						<Col span="6">退保金额：{{ !exitBondDetail.depositRecord ? '' : exitBondDetail.depositRecord.amount+exitBondDetail.depositRecord.coin.unit }}</Col>
						<Col span="6">状态：{{ (!exitBondDetail.businessCancelApply ? '' :exitBondDetail.businessCancelApply.status) | filterStatus }}</Col>
						<Col span="6">申请时间：{{ !exitBondDetail.businessCancelApply ? '' :exitBondDetail.businessCancelApply.cancelApplyTime }}</Col>
						<Col span="6">审核时间：{{ !exitBondDetail.businessCancelApply ? '' :exitBondDetail.businessCancelApply.handleTime }}</Col>
					</Row>
					<Row>
						<Col span="6">退保原因：{{ !exitBondDetail.businessCancelApply ? '' : exitBondDetail.businessCancelApply.reason | reasonFilter}}</Col>
					</Row>
				</Row>
				<p class="line"></p>
				<Row>
					<h3>用户信息</h3>
					<Row>
						<Col span="6">真实姓名：{{ !exitBondDetail.businessCancelApply ? '' : exitBondDetail.businessCancelApply.member.realName}}</Col>
						<Col span="6">会员昵称：{{ !exitBondDetail.businessCancelApply ? '' : exitBondDetail.businessCancelApply.member.username }}</Col>
						<Col span="6">手机号：{{ !exitBondDetail.businessCancelApply ? '' : exitBondDetail.businessCancelApply.member.mobilePhone }}</Col>
						<Col span="6">邮箱号：{{ !exitBondDetail.businessCancelApply ? '' : exitBondDetail.businessCancelApply.member.email }}</Col>
					</Row>
				</Row> 
				<p class="line"></p>
				<Row>
					<h3>交易信息</h3>
					<Row>
						<Col span="6">申诉次数：{{ exitBondDetail.complainantNum }}</Col>
						<Col span="6">被申诉次数数：{{ exitBondDetail.defendantNum }}</Col>
						<Col span="6">总成交额：{{ !exitBondDetail.money ? 0 : exitBondDetail.money }}</Col>
						<Col span="6">总手续费：{{ !exitBondDetail.fee ? 0 : exitBondDetail.fee }}</Col>
					</Row>
					<Row>
						<Col span="6">发布广告数：{{ exitBondDetail.advertiseNum }}</Col>
					</Row>
				</Row>


			</Card>
		</Row>
	</div>
</template>
<script>
import { cancelApplyDetail } from '@/service/getData'
export default {
	data() {
		return {
			exitBondDetail: {}
		}
	},
	methods: {
		refreshPageManual() {
			cancelApplyDetail({id: this.$route.query.id})
			.then(res => {
				if(!res.code) {
					this.exitBondDetail = res.data;
				}else this.$Message.error(res.message);
			})
			.catch(err => console.log(err))
		}
	},
	created() {
		this.refreshPageManual();
	},
	filters: {
		filterStatus(val) {
			if(val==='') return val;
			else{
				if(val === 6) return '失败';
				else if(val === 5) return '待审核';
				else if(val === 7) return '成功';
			}
		},
		reasonFilter(val) {
			if(!val) return '无';
			else return val;
		}
	}
}
</script>

<style lang="less" scoped>
.line{
	border-top: 1px solid #dfdfdf;
}
	.detail{
		.card{
			padding: 20px;
			.ivu-card-body{
				&>.ivu-row{
					margin: 20px 0;
					.ivu-row{
						font-size: 16px;
						line-height: 50px;
					}
				}
			}
		}
	}

</style>

