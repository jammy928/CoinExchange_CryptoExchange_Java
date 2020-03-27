<template>
  <div class="ExitBond">
    <Card>
      <p slot="title">
        退保管理
        <Button type="primary" size="small" @click="refreshPageManual">
          <Icon type="refresh"></Icon>
          刷新
        </Button>
      </p>

			<Row class="functionWrapper">
        <div class="searchWrapper clearfix">
					
					<div class="poptip">
						<Poptip trigger="hover" content="请输入会员昵称、邮箱、手机号" placement="bottom-start">
							<Input placeholder="请输入会员昵称、邮箱、手机号" v-model="filterSearch.account"/></Input>
						</Poptip>
					</div>

					<div class="poptip">
						<Select v-model="filterSearch.status" placeholder="请选择状态">
							<Option v-for="item in statusArr" :value="item.value" :key="item.value">{{item.name}}</Option>
						</Select>
					</div>
					
					<div class="btns">
						<Button type="info" size="small" @click="searchByFilter">搜索</Button>
					</div>
				</div>
			</Row>		
      <Row>
        <Table 
          :columns="columns_first" 
          :data="userpage" 
          border
          :loading="ifLoading">
				</Table>
      </Row>

      <div class="pageWrapper">
        <Page  
          :total="totalNum" 
          :current="currentPageIdx"   
          @on-change="changePage" 
          show-elevator></Page>
      </div>

			<Modal
				class="bondCheck" 
				title="退保审核"
				v-model="bondCheck"
				@on-cancel="cancelSub"
			>
				<Row>退保金额： {{ 
					!exitBondDetail.depositRecord ? '' : exitBondDetail.depositRecord.amount+exitBondDetail.depositRecord.coin.unit }}</Row>
				<Row>退保原因： {{ (!exitBondDetail.businessCancelApply ? '无' : exitBondDetail.businessCancelApply.reason) | reasonFilter }}</Row>
				<br>
				<Row>
					<Col span="8">
						发布广告数：{{ exitBondDetail.advertiseNum }}
					</Col>
					<Col span="8">
						申诉次数：{{ exitBondDetail.complainantNum }}
					</Col>
					<Col span="8">
						被申诉次数：{{ exitBondDetail.defendantNum }}
					</Col>
				</Row>
				<br>
				<Row>
					<Col span="12">
						总成交额：{{ !exitBondDetail.money ? 0 : exitBondDetail.money }}
					</Col>
					<Col span="12" >
						总手续费：{{ !exitBondDetail.fee ? 0 : exitBondDetail.fee }}
					</Col>
				</Row>
				<br>
				<br>
				<p>注：审核通过后，系统会自动将保证金转入用户账户</p>

				<div slot="footer">
					<Row>
						<Col span="8" offset="3">
							<Button long type="info" @click="confirmPass(1)">审核通过</Button>
						</Col>
						<Col span="8" offset="3">
							<Button long type="error" @click="confirmPass(0)">审核不通过</Button>
						</Col>
					</Row>
				</div>
			</Modal>

			<Modal title="拒绝原因（选填）"
				v-model="rejectModal"
				width="400"
				@on-ok="subReject"
			>
				<Input v-model="subCheck.reason" type="textarea" placeholder="请输入拒绝原因...">
				</Input>
			</Modal>
    </Card>
    </div>
</template>

<script>

import { cancelBusinessStatus, MemberRealNameList, accessLog, queryCancelApply, cancelApplyDetail, checkApply } from '@/service/getData';
import { setStore, getStore, removeStore } from '@/config/storage';

export default {
	name: 'ExitBond',
  data() {
    return {
			rejectModal: false,
			exitBondDetail: {},
			subCheck: {
				id: '',
				success: '',
				reason: '',
				
			},
			bondCheck: false,
			statusArr: [],
			filterSearch: {
				pageNo: 1,
				pageSize: 10,
				status: '',
				account: '',
				direction: [],
				property: []
			},
      currentPageIdx: 1,
      ifLoading: true,
      totalNum: null,
      columns_first: [
        {
					title: '会员昵称',
					key: 'username',
					render: (h, ctx) => {
						return h('span', {}, ctx.row.member.username)
					}
        },
        {
          title: "会员邮箱",
					key: "email",
					render: (h, ctx) => {
						return h('span', {}, ctx.row.member.email)
					}
        },
         {
          title: "会员手机号",
					key: "mobilePhone",
					render: (h, ctx) => {
						return h('span', {}, ctx.row.member.mobilePhone)
					}
        },
        {
					title: "保证金币种",
					key: 'unit',
					render: (h, ctx) => {
						return h('span', {}, ctx.row.depositRecord.coin.unit)
					}
        },
        {
          title: "保证金数量",
					key: "amount",
					render: (h, ctx) => {
						return h('span', {}, ctx.row.depositRecord.amount)
					}
        },
        {
          title: "提交审核时间",
          key: "cancelApplyTime"
				},
				{
          title: "审核时间",
					key: "handleTime",
					render: (h, ctx) => {
						return h('span', {}, !ctx.row.handleTime ? '-' : ctx.row.handleTime);
					}
				},
				{
          title: "状态",
					key: "status",
					render: (h, ctx) => {
						let status = '';
						if(ctx.row.status === 6) status = '失败';
						else if(ctx.row.status === 5) status = '待审核';
						else if(ctx.row.status === 7) status = '成功';
						return h('span', {}, status);
					}
				},
				// {
        //   title: "审核人",
        //   key: "module"
				// },
				{
          title: "操作",
					key: "module",
					render: (h, ctx) => {
						let btnText = '审核';
						let btnType = 'info';
						if(ctx.row.status === 5) {
							 btnText = '审核';
							 btnType = 'info';
						}else{
							 btnText = '查看';
						   btnType = 'success';
						}
						return h('Button', {
							props: {
								type: btnType,
								size: 'small'
							},
							on: {
								click: () => {
									if(ctx.row.status === 5) {
										cancelApplyDetail({id: ctx.row.id})
										.then(res => {
											if(!res.code) {
												this.exitBondDetail = res.data;
												this.bondCheck = true;
												this.subCheck.id = ctx.row.id;
											}else this.$Message.error(res.message);
										})
									}else{
										this.$router.push({path: '/otc/adManage/exitbonddetail', query: { id: ctx.row.id }});
									}
								}
							}
						}, btnText)
					}
				}
      ],
      userpage: [],
    };
  },
  methods: {
		cancelSub() {
			for(let key in this.subCheck)  {
				this.subCheck[key] = '';
			}
		},
		subReject() {
			checkApply(this.subCheck)
			.then(res => {
				if(!res.code) {
					this.$Message.success(res.message);
					this.refreshPage(this.filterSearch);
					this.cancelSub();
					this.rejectModal = this.bondCheck = false;
				}else this.$Message.error(res.message)
			})
		},
		confirmPass(n) {
			this.subCheck.success = n;
			if(!n) this.rejectModal = true;
			else{
				checkApply(this.subCheck)
				.then(res => {
					if(!res.code) {
						this.$Message.success(res.message);
						this.refreshPage(this.filterSearch);
						this.cancelSub();
						this.rejectModal = this.bondCheck = false;
					}else this.$Message.error(res.message)
				})
			}
		},
		searchByFilter() {
			this.filterSearch.pageNo = 1;
			this.currentPageIdx = 1;
			console.log(this.filterSearch);
			
      this.refreshPage(this.filterSearch);
		},
    refreshPageManual() {
			this.currentPageIdx = 1;
			for(let key in this.filterSearch)  {
				this.filterSearch[key] = '';
			}
			this.filterSearch.pageNo = 1;
			this.filterSearch.pageSize = 10;
			this.filterSearch.direction = [];
			this.filterSearch.property = [];
      this.refreshPage(this.filterSearch);
    },
    refreshPage(obj) {
			this.ifLoading = true;
      queryCancelApply(obj)
      .then(res => {
        if(!res.code){
          this.ifLoading = false;
          this.userpage = res.data.content;
          this.totalNum = res.data.totalElements;
				}else this.$Message.error(res.message);
      });
    },
    changePage(pageIndex) {
      this.currentPageIdx = pageIndex;
			this.filterSearch.pageNo = pageIndex;
      this.refreshPage(this.filterSearch);
    },
  },
  created() {
		cancelBusinessStatus().then(res => {
			if(!res.code){
				this.statusArr = res.data;
				this.statusArr.push({ value: '', name: '全部' });
			}else this.$Message.error(res.message);
		});
		this.refreshPage()
	},
	filters: {
		reasonFilter(val) {
			if(!val) return '无';
			else return val;
		}
	}
}
</script>

<style scoped lang='less'>
	.bondCheck{
		.ivu-row{
			font-size: 16px;
			line-height: 32px;
		}
		p{
			color: #afafaf;
			text-align: center;
		}
	}
</style>