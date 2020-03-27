<template>
  <div>
    <Card>
      <p slot="title">
        订单详情 <span class="orderDetail">{{ cbData.orderSn }}</span>
      </p>
			<div class="baseInfo">
				<Row>
					<Col span="6">
						<p>状态：<span class="status">{{ !cbData.isSuccess ? '败诉' : '胜诉' }}</span></p>
					</Col>
					<Col span="6">
						<p>交易币种：<span>{{ cbData.coinName }}</span></p>
					</Col>
					<Col span="6">
						<p>交易类型：<span>{{ !cbData.advertiseType ? '买入' : '卖出' }}</span></p>
					</Col>
					<Col span="6">
						<p>支付方式：<span>{{ cbData.payMode }}</span></p>
					</Col>
				</Row>
				<Row>
					<Col span="6">
						<p>申诉人：<span>{{ cbData.initiatorName }}</span></p>
					</Col>
					<Col span="6">
						<p>广告主：<span>{{ `(${cbData.advertiseCreaterName})${cbData.advertiseCreaterUserName}` }}</span></p>
					</Col>
					<Col span="6">
						<p>承接人：<span>{{ `(${cbData.customerUserName})${cbData.customerName}`}}</span></p>
					</Col>
				</Row>
				<Row>
					<Col span="6">
						<p>订单数量 ：<span>{{ cbData.number }}</span></p>
					</Col>
					<Col span="6">
						<p>订单金额：<span>{{ cbData.money }}</span></p>
					</Col>
				</Row>
				<Row>
						<p>备注：<span>{{ cbData.remark }}</span></p>
				</Row>
			</div>
    </Card>
    </div>
</template>

<script>

import { appealManageDetail } from '@/service/getData';

export default {
	name: 'AppealDetail',
  data() {
   return {
		 cbData: {}
	 }
	},
	methods: {
		refreshPage(obj) {
			appealManageDetail(obj)
			.then(res => {
				if(!res.code) {
					this.cbData = res.data;
				}else this.$Message.error(res.message);
			})
			.catch(err => console.log(err))
		}
	},
  created() {
		this.refreshPage({ id: this.$route.query.id })
  }
}
</script>

<style scoped lang='less'>
@import '../../styles/common.less';
	.orderDetail {
		color: @primary;
	}
	.status{
		color: @light-primary !important;
	}
</style>