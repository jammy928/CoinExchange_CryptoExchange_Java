<template>
	<div>
		<Row class="btns">
			<Button type="primary"  @click="refreshPage"> 
				<Icon type="refresh"></Icon> 刷新 
			</Button>
		</Row>
		<Card>
			<div slot="title">
				基本信息
			</div>
			<div class="baseInfo">
				<Row>
					<Col span="6">
						<p>状态：<span>{{ !baseInfo.status ? '上架' : '下架' }}</span></p>
					</Col>
					<Col span="6">
						<p>交易类型：<span>{{ !baseInfo.advertiseType ? '买入' : '卖出' }}</span></p>
					</Col span="6">
					<Col span="6">
						<p>交易币种：<span>{{ !baseInfo.coin ? '' : baseInfo.coin.unit  }}</span></p>
					</Col>
					<Col span="6">
						<p>交易货币：<span>{{ !baseInfo.country ? '' : baseInfo.country.localCurrency }}</span></p>
					</Col>
				</Row>
				<Row>
					<Col span="6">
						<p>交易期限：<span>{{ baseInfo.timeLimit }}分钟</span></p>
					</Col>
					<Col span="6">
						<p>买入量：<span>{{ baseInfo.dealAmount }}</span></p>
					</Col span="6">
					<Col span="6">
						<p>国家：<span>{{ !baseInfo.country ? '' : baseInfo.country.zhName }}</span></p>
					</Col>
					<Col span="6">
						<p>付款方式：<span>{{ baseInfo.payMode }}</span></p>
					</Col>
				</Row>
				<Row>
					<Col span="6">
						<p>最小交易额：<span>{{ baseInfo.minLimit }}</span></p>
					</Col>
					<Col span="6">
						<p>最大交易额：<span>{{ baseInfo.maxLimit }}</span></p>
					</Col span="6">
					<Col span="6">
						<p>固定价格：<span>{{ baseInfo.price }}</span></p>
					</Col>
				</Row>
				<Row>
					<Col span="6">
						<p>备注信息：<span>{{ baseInfo.remark }}</span></p>
					</Col>
					<Col span="6" offset="6">
						<p>自动回复：<span>{{ !baseInfo.auto ? '是' : '否'}}</span></p>
					</Col span="6">
				</Row>
			</div>
		</Card>
			<br>
			<br>
		<Card>
			<div slot="title">
				订单明细
				<!-- <Button type="primary" size="small" @click="refreshPageManual">
					<Icon type="refresh"></Icon> 刷新
				</Button> -->
			</div>
			<Row>
				<Table
					:loading="ifLoading"
					:columns="columnsList"
					:data="userpage"
					border>
				</Table>
			</Row>
		
			<Row class="pageWrapper" >
				<Page :total="cbData.totalElements" 
							:current="currentPageIdx"   
							@on-change="changePage" 
							show-elevator>
				</Page>
			</Row>
			
		</Card>				
	</div>
</template>

<script>
import { advDetailOtc, queryOtcOrder } from '@/service/getData'
export default {
	data() {
		return {
			cbData: {},
			ifLoading: false,
			currentPageIdx: 1,
			id: '',
			memberName: '',
			baseInfo: {},
			filterSearch: {
				pageNo: 1,
				pageSize: 10,
				memberName: ''
			},
			columnsList: [
				{
					title: '订单号',
					key: 'orderSn'
				},
				{
					title: '交易人',
					key: 'customerName',
					render: (h, obj) => {
						let cName = obj.row.customerName;
						let mName = obj.row.memberName;
						return h('span',{}, `${cName}(${mName})`)
					}
				},
				{
					title: '交易时间',
					key: 'createTime'
				},
				{
					title: '订单数量',
					key: 'number'
				},
				{
					title: '订单金额',
					key: 'money'
				},
				{
					title: '手续费',
					key: 'fee'
				},
				{
					title: '支付方式',
					key: 'payMode'
				},
				{
					title: '订单状态',
					key: 'status'

				},
			],
			userpage: []
		}
	},
	methods: {
		refreshPage() {
			advDetailOtc({id: this.id})
			.then(res => {
				if(!res.code) {
					console.log(res);
					this.baseInfo = res.data;
					this.filterSearch.memberName = res.data.member.username;
					this.otcOrderTabel(this.filterSearch);
				}
				else this.$Message.error(res.message);
			})
			.catch(err => { console.log(err)})
		},
		otcOrderTabel(obj) {
			this.ifLoading = true;
			queryOtcOrder(obj)
			.then(res => {
				if(!res.code) { 
					console.log(res);
					this.cbData = res.data;
					this.userpage = res.data.content;
					this.ifLoading = false;
				}
				else this.$Message.error(res.message);
			})
		},
		changePage(pageIndex) {
			this.currentPageIdx = pageIndex;
			this.filterSearch.pageNo = pageIndex;
			this.otcOrderTabel(this.filterSearch)
		}
	},
	created () {
		this.id = this.$route.query.advID;
		this.refreshPage();
	}
}
</script>

<style lang="less" scoped>
.btns{
	line-height: 50px;
	text-align: right;
}
	.baseInfo {
		p {
			line-height: 30px;
			font-size: 15px;
			color: #adabab;
			span{
				color: #444;
			}
		}
	}
</style>


