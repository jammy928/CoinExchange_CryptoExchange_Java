<template>
	<div class="statistic">
		<!-- <Row type="flex" justify="space-between"> -->
		<Row style="padding-top: 40px">
			<Col span="10" offset="1">
				<div class="card">
					<div class="title">
						<p>
							<span :class="{ active: totalVolume.tardeType.active === index }"
										@click="switchType(index, totalVolume)" 
										v-for="(trade, index) in totalVolume.tardeType.type"
										:key="index">{{ trade.type }}</span>
							<span class="detail" @click="skipRouter(totalVolume)">明细</span>
						</p>
					</div>
					<div class="body">
						<Row>
							<Col span="7" offset="1">币种</Col>
							<Col span="7" offset="1">成交量</Col>
							<Col span="7" offset="1">交易额</Col>
						</Row>
						<Row v-if="!!totalVolume.tabelArr.length">  
							<Row class="info"  v-for="tabelInfo in totalVolume.tabelArr" :key="tabelInfo.unit">
								<Col span="7" offset="1">{{ tabelInfo.unit }}</Col>
								<Col span="7" offset="1">{{ tabelInfo.num | numLen }}</Col>
								<Col span="7" offset="1">{{ tabelInfo.money | numLen }}</Col>
							</Row>
						</Row>
						<Row class="noData" v-if="!totalVolume.tabelArr.length">
							<p>暂无数据！</p>
						</Row>	
					</div>
					<div class="footer">
						<p> 
							<span :class="{active: totalVolume.timeRange.active===index}" 
										v-for="(time, index) in totalVolume.timeRange.time" 
										@click="selectTimeRange(index, totalVolume)"
										:key="index">{{ time }}</span>
						</p>
					</div>
				</div>
			</Col>
			
			<Col span="10" offset="2">
				<div class="card">
					<div class="title">
						<p>
							<span :class="{ active: totalFee.tardeType.active === index }"
										@click="switchType(index, totalFee)" 
										v-for="(trade, index) in totalFee.tardeType.type"
										:key="index">{{ trade.type }}</span>
							<span class="detail" @click="skipRouter(totalFee)">明细</span>
						</p>
					</div>
					<div class="body">
						<Row>
							<Col span="12">币种</Col>
							<Col span="12">手续费</Col>
						</Row>
						<Row v-if="!!totalFee.tabelArr.length">  
							<Row class="info"  v-for="tabelInfo in totalFee.tabelArr" :key="tabelInfo.unit">
								<Col span="12">{{ tabelInfo.unit }}</Col>
								<Col span="12">{{ tabelInfo.fee | numLen }}</Col>
							</Row>
						</Row>
						<Row class="noData" v-if="!totalFee.tabelArr.length">
							<p>暂无数据！</p>
						</Row>	
					</div>
					<div class="footer">
						<p> 
							<span :class="{active: totalFee.timeRange.active===index}" 
										v-for="(time, index) in totalFee.timeRange.time" 
										@click="selectTimeRange(index, totalFee)"
										:key="index">{{ time }}</span>
						</p>
					</div>
				</div>
			</Col>

		</Row>
		<Row style="padding-top: 40px">
			<Col span="10" offset="1">
				<div class="card">
					<div class="title">
						<p>
							<span :class="{ active: totalCoin.tardeType.active === index }"
										@click="switchType(index, totalCoin)" 
										v-for="(trade, index) in totalCoin.tardeType.type"
										:key="index">{{ trade.type }}</span>
							<span class="detail" @click="skipRouter(totalCoin)">明细</span>
						</p>
					</div>
					<div class="body">
						<Row>
							<Col span="12">币种</Col>
							<Col span="12">总充币数</Col>
						</Row>
						<Row v-if="!!totalCoin.tabelArr.length">  
							<Row class="info"  v-for="tabelInfo in totalCoin.tabelArr" :key="tabelInfo.unit">
								<Col span="12">{{ tabelInfo.unit }}</Col>
								<Col span="12">{{ tabelInfo.amount | numLen }}</Col>
							</Row>
						</Row>
						<Row class="noData" v-if="!totalCoin.tabelArr.length">
							<p>暂无数据！</p>
						</Row>	
					</div>
					<div class="footer">
						<p> 
							<span :class="{active: totalCoin.timeRange.active===index}" 
										v-for="(time, index) in totalCoin.timeRange.time" 
										@click="selectTimeRange(index, totalCoin)"
										:key="index">{{ time }}</span>
						</p>
					</div>
				</div>
			</Col>
		</Row>
	</div>
</template>
<script>
import { financeTurnover, financeFee, financeRecharge } from '@/service/getData'
import { SSL_OP_COOKIE_EXCHANGE } from 'constants';
import dtime from 'time-formater';

export default {
	name: 'financeStatistic',
	data() {
		return {
			totalVolume: {
				type: 'volume',
				searchData: {
					types: ['OTC_NUM', 'OTC_MONEY'],
					startDate: dtime(Date.now()-24*60*60000).format('YYYY-MM-DD')
				},
				tabelArr: [],
				timeRange: { active: 0, time: ['昨天', '三天', '一周'] },
				tardeType: { active: 0, type: [
					{ status: ['OTC_NUM', 'OTC_MONEY'], type: '法币交易', router: '/finance/otcdetail' },
					{ status: ['EXCHANGE_BASE', 'EXCHANGE_COIN'], type: '币币交易', router: '/' }
				]}
			},
			totalFee: {
				type: 'fee',
				searchData: {
					type: 0,
					startDate: dtime(Date.now()-24*60*60000).format('YYYY-MM-DD')
				},
				tabelArr: [],
				timeRange: { active: 0, time: ['昨天', '三天', '一周'] },
				tardeType: { active: 0, type: [
					{ status: 0, type: '法币交易', router: '/finance/otcdetail'}, 
					{ status: 2, type: '币币交易', router: '/'}, 
					{ status: 6, type: '提币', router: '/finance/withdrawdetail'}
				]}
			},
			totalCoin: {
				type: 'coin',
				searchData: {
					type: 5,
					startDate: dtime(Date.now()-24*60*60000).format('YYYY-MM-DD')
				},
				tabelArr: [],
				timeRange: { active: 0, time: ['昨天', '三天', '一周'] },
				tardeType: { active: 0, type: [
					{ status: 5, type: '充币', router: '/finance/changecoindetail' }, 
					{ status: 6, type: '提币', router: '/finance/withdrawdetail' }
				]}
			},
		}
	},
	methods: {
		skipRouter(obj) {
			this.$router.push(`${obj.tardeType.type[obj.tardeType.active].router}`);
		},
		switchType(index, obj) {
			obj.tardeType.active = index;
			if(obj.type === 'volume') {
				obj.searchData.types = obj.tardeType.type[index].status;
				this.turnoverInfo();
			}else if(obj.type === 'fee') {
				obj.searchData.type = obj.tardeType.type[index].status;
				this.feeInfo();
			}else if(obj.type === 'coin') {
				obj.searchData.type = obj.tardeType.type[index].status;
				this.coinInfo();
			}
		},
		selectTimeRange(day, obj) {
			obj.timeRange.active = day;
			if(day===0) obj.searchData.startDate = this.formatTime(1);
			else if(day===1) obj.searchData.startDate = this.formatTime(3);
			else if (day===2) obj.searchData.startDate = this.formatTime(7);
			
			if(obj.type === 'volume') this.turnoverInfo();
			else if(obj.type === 'fee') this.feeInfo();
			else if(obj.type === 'coin') this.coinInfo();
		},
		formatTime(day) {
			return dtime(Date.now()-24*60*60000*day).format('YYYY-MM-DD');
		},
		turnoverInfo() {
			financeTurnover(this.totalVolume.searchData)
			.then(res => {
				if(!res.code) {
					let saved = [];
					this.totalVolume.tabelArr = [];
					res.data.forEach(item => {
						let saveIndex = saved.indexOf(item.unit);
						if(saveIndex===-1) {
							if(item.type==='OTC_MONEY') {
								this.totalVolume.tabelArr.push({ unit: item.unit, money: item.amount })
							}else if(item.type==='OTC_NUM') {
								this.totalVolume.tabelArr.push({ unit: item.unit, num: item.amount })
							}
							saved.push(item.unit);
						}else{
							if(item.type==='OTC_MONEY') {
								this.totalVolume.tabelArr[saveIndex].money = item.amount;
							}else if(item.type==='OTC_NUM') {
								this.totalVolume.tabelArr[saveIndex].num = item.amount;
							}
						}
					});
				}else this.$Message.error(res.message);
			})
			.catch(err => console.log(err))
		},
		feeInfo() {
			financeFee(this.totalFee.searchData)
			.then(res => {
				if(!res.code) {
					this.totalFee.tabelArr = res.data;
				}else this.$Message.error(res.message)
			})
			.catch(err => console.log(err))
		},
		coinInfo() {
			financeRecharge(this.totalCoin.searchData)
			.then(res => {
				if(!res.code) {
					this.totalCoin.tabelArr = res.data;
				}else this.$Message.error(res.message)
			})
			.catch(err => console.log(err))
		},
	},
	created() {
		this.turnoverInfo();
		this.feeInfo();
		this.coinInfo();
	},
	filters: {
		numLen(val) {
			if(String(val).includes('.')) {
				if(String(val).split('.')[1].length>5) {
					return val.toFixed(5);
				}else return val;
			}else return val;
		}
	}
}
</script>

<style lang="less" scoped>
@borderLine: 1px solid rgba(204, 204, 204, .4);
@fontColor: #bbb;
@activeColor: #2d8cf0;
	.statistic{
		.card{
			position: relative;
			padding: 40px 0;
			height: 350px;
			color: @fontColor;
			border-radius: 5px;
			background-color: #fff;
			cursor: pointer;
			transition: all .3s;
			.active{
				font-weight: 600;
				color: @activeColor;
			}
			&:hover{
				box-shadow: 0 0 10px 2px #ccc;
			}
			.title{
				position: absolute;
				top: 0;
				width: 100%;
				border-bottom: @borderLine;
				.active{
					position: relative;
					font-weight: 600;
					color: @activeColor;
					&:after{
						position: absolute;
						left: 0;
						bottom: -12px;
						content: ' ';
						width: 100%;
						height: 0;
						border-top: 3px solid @activeColor;
					}
				}
				p{
					position: relative;
					font-size: 15px;
					line-height: 40px;
					span{
						margin-left: 20px;
						cursor: pointer;
					}
					.detail{
						position: absolute;
						right: 20px;
						color: #2b85e4;
						cursor: pointer;
						&:hover{
							color: #5cadff;
						}
					}
				}
			}
			.body{
				padding-top: 20px;
				.ivu-col{
					font-size: 15px;
					text-align: center;
				}
				.info{
					line-height: 35px;
				}
				.noData{
					p{
						line-height: 50px;
						text-align: center;
					}
				}
			}
			.footer{
				position: absolute;
				bottom: 0;
				width: 100%;
				p{
					font-size: 15px;
					text-align: right;
					line-height: 40px;
					border-top: @borderLine;
					span{
						margin-right: 20px;
						cursor: pointer;
						&:hover{
							font-weight: 600;
						}
					}
				}
			}
		}
	}
</style>