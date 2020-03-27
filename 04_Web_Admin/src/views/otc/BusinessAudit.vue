<template>
	<div>
    <Card>
      <p slot="title">
        认证商家
        <Button type="primary" size="small" @click="refreshPageManual">
          <Icon type="refresh"></Icon>
          刷新
        </Button>
      </p>
      <Row class="functionWrapper">
        <div class="searchWrapper clearfix">
          <div class="poptip">
            <Poptip trigger="hover" content="请输入手机号、真实姓名或会员名称搜索" placement="bottom-start">
              <Input placeholder="请输入手机号、真实姓名或会员名称搜索" 
                    v-model="filterSearch.account"/> 
              </Input>      
            </Poptip>
          </div>

					<div class="poptip">
						<span>会员状态：</span>
						<Select v-model="filterSearch.status">
							<Option v-for="item in checkStatus" 
										:value="item.value" 
										:key="item.value">{{ item.name }}
							</Option>
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
					:loading="ifLoading"
					@on-sort-change="definedOrder"
					class='user_center'>
				</Table>
      </Row>

      <Row class="pageWrapper" >
        <Page :total="totalNum" 
              :current="currentPageIdx"   
              @on-change="changePage" 
              show-elevator>
				</Page>			
      </Row>

			<Modal
        v-model="ifPass"
        title="商家审核是否通过"
        @on-ok="confirmPass"
        @on-cancel="$Message.success('已取消');">
        <p>是否审核通过所选择的项</p>
      </Modal>


    </Card>
  </div>
</template>

<script>

import { setStore, getStore, removeStore } from '@/config/storage';
import { queryBusinessStatus, queryBusiness, memberManage, businessAudit, publishAdvOtc, businessDetail  } from '@/service/getData';


export default {
  data () {
		return {
			sortObj: {},
			checkStatus: [
				// { status: 0, text: '未认证' },
				// { status: 1, text: '待审核' },
				// { status: 2, text: '已认证' },
				// { status: 3, text: '未通过' },
				// { status: '', text: '全部' },
			],
			filterSearch: {
				account: '',
				status: '',
			},
      routeStatus: null,
      currentPageIdx: 1,
      status: null,
      userID: null,
      ifPass: false,
      searchInner: null,
      totalNum: null,
      ifLoading: true,
      usemmuber:'',
      loading:true,
      columns_first: [
        {
          title: '会员昵称',
					key: 'username',
					render: (h, ctx) => {
						return h('span',{},ctx.row.member.username)
					}
        },
				{
					title: '邮箱',
					key: 'email',
					render: (h, ctx) => {
						return h('span',{},ctx.row.member.email)
					}
				},
        {
          title: '真实姓名',
					key: 'realName',
					render: (h, ctx) => {
						return h('span',{},ctx.row.member.realName)
					}
        },
        {
          title: '手机号',
					key: 'mobilePhone',
					render: (h, ctx) => {
						return h('span',{},ctx.row.member.mobilePhone)
					}
        },
       
       
        {
          title: '会员等级',
          key: 'memberLevel',
          width:160,
          render: (h, obj) => {
            let memberLevel = obj.row.memberLevel;
            let memberLevelTxt = null;
            if (!memberLevel) memberLevelTxt = '普通会员'
            else if (memberLevel===1) memberLevelTxt = '实名'
            else if (memberLevel===2) memberLevelTxt = '认证'

            return h('span',{
            }, memberLevelTxt)
            
          }
        },
        {
          title: '审核时间',
					key: 'member.certifiedBusinessCheckTime',
					width: 105,
					sortable: 'custom',
					render: (h, ctx) => {
						let time = ctx.row.member.certifiedBusinessCheckTime;
						return h('span',{},!time ? '-' : time)
					}
				},
				{
					title: '申请时间',
					key: 'member.certifiedBusinessApplyTime',
					width: 105,
					sortable: 'custom',
					render: (h, ctx) => {
						let time = ctx.row.member.certifiedBusinessApplyTime;
						return h('span',{},!time ? '-' : time)
					}
				},
				{
          title: '广告状态',
					key: 'publishAdvertise',
					render: (h, obj) => {
            let adStatus = obj.row.member.publishAdvertise;
						let text = !adStatus ? '禁止发布' : '允许发布';
						return h('span', {}, text)
          }
					
        },
        {
          title: '审核状态',
          // key: 'status',
          render: (h, obj) => {
            let  userStatus = obj.row.certifiedBusinessStatus;
            let statusTxt = null;
            if (userStatus === 1)  statusTxt = '审核中'
            else if (userStatus === 2)  statusTxt = '已认证'
            else if (userStatus === 0)  statusTxt = '未认证'
            else if (userStatus === 3)  statusTxt = '未通过'
            else if (userStatus === 5)  statusTxt = '申请退保中'

            return h('span', {
            },statusTxt);
          }
				},
				// {
        //   title: '审核人',
        //   key: ''
        // },
        {
          title: '操作',
          key: 'action',
          width: 180,
          align: 'center',
          render: (h, obj) => {
						let advStatus = obj.row.member.publishAdvertise;
						let btnType = !advStatus ? 'info' : 'error';
						let btnText = !advStatus ? '允许发布' : '禁止发布';
            let userStatus = obj.row.certifiedBusinessStatus;
            let statusTxt = null;
            let type = 'text';
            let isDisabled = true;
            let color = 'unset';
            if (userStatus !== 1) {
							if (userStatus === 5) {
								return h('span', {}, '-');
							}
							return	h('div', {}, [
								h('Button', {
									props: {
										type: 'success',
										size: 'small'
									},
									style: {
										marginRight: '5px'
									},
									on: {
										click: () => {
											this.$router.push({path: '/otc/businessaudit/businessdetail', query: {id: obj.row.id}});
										}
									}
								}, '查看'),
								h('Button', {
									props: {
										type: btnType,
										size: 'small'
									},
									on: {
										click: () => {
											let status = !obj.row.member.publishAdvertise ? 1 : 0;
											publishAdvOtc({ memberId: obj.row.member.id, status: status})
											.then(res => {
												if(!res.code) {
													this.$Message.success(res.message);
													this.refreshPage({ pageNo: this.currentPageIdx, pageSize: 10 });
												}else this.$Message.error(res.message);
											})
											.catch( err => console.log(err))
										}
									}
								}, btnText)
							])
						} else {
							statusTxt = '待审核';
              type = 'info';
							isDisabled = false;
							return h('div', {}, [
								h('Button', {
									props: {
										type: type,
										size: 'small',
										disabled: isDisabled
									},
									style: {
										color: color,
										marginRight: '5px'
									},
									on: {
										click: () => {
											if(userStatus === 1) {
												this.$store.commit('switchBusinessMask', true);
												businessDetail(obj.row.member.id, {status: obj.row.member.certifiedBusinessStatus})
												.then(res => {
													if(!res.code) {
														this.$store.commit('businessCheckInfo', res.data)
													}else this.$Message.error(res.message);
												})
												// this.ifPass = true;
												this.userID = null;
												this.status = null;
												
												this.userID = obj.row.member.id;
												this.status = 2;
											}
										}
									}
								 }, statusTxt),
								 h('Button',{
										 props: {
											type: btnType,
											size: 'small'
										},
										on: {
											click: () => {
												let status = !obj.row.member.publishAdvertise ? 1 : 0;
												publishAdvOtc({ memberId: obj.row.member.id, status: status})
												.then(res => {
													if(!res.code) {
														this.$Message.success(res.message);
														this.refreshPage({ pageNo: this.currentPageIdx, pageSize: 10 });
													}else this.$Message.error(res.message);
												})
												.catch( err => console.log(err))
											}
										}
								 },btnText)
							])
						}
          }
        }
      ],
      userpage: []
    }
  },
  methods: {
		definedOrder(obj) {
			this.currentPageIdx = 1;

			let searchObj = Object.assign(this.filterSearch, { pageNo: 1	, pageSize: 10 });
			let orderNum = '';

			obj.order==='desc' ? orderNum = 1 : orderNum = 0;
			this.sortObj = { direction: [orderNum], property: [obj.key] };

			let sortSubObj = Object.assign(searchObj, this.sortObj);

			this.refreshPage(sortSubObj);
			
		},
		searchByFilter() {
			let searchObj = Object.assign(this.filterSearch, { pageNo: 1	, pageSize: 10 }, this.sortObj);
      this.refreshPage(searchObj);
		},
    confirmPass() {
      businessAudit(this.userID, { status: this.status })
      .then( res => {
        if(!res.code) {
          this.$Message.success(res.message);
        } else this.$Message.error(res.message);
        this.refreshPage({ pageNo: this.currentPageIdx, pageSize: 10 });
      })
      
    },
    refreshPageManual(){
			this.currentPageIdx = 1;
			for(let key in this.filterSearch) {
				this.filterSearch[key] = '';
			}
      this.refreshPage({ pageNo: 1, pageSize: 10 });
    },
    changePage(pageIndex) {
			this.currentPageIdx = pageIndex;
			let searchObj = Object.assign(this.filterSearch, { pageNo: pageIndex	, pageSize: 10 });
      this.refreshPage(searchObj);
    },
    refreshPage(obj = {}) {
      this.ifLoading = true;
      queryBusiness(obj)
      .then( res => {
        if (!res.code) {
          this.ifLoading = false;
          let formatArr = [];
          this.userpage = res.data.content;
          this.totalNum = res.data.totalElements;
        }
      })
    },
    judgeRouteStatus(obj) {
      if(obj.status===undefined) this.routeStatus = null;
      else  this.routeStatus = obj.status;
      this.refreshPage({status: this.routeStatus});
    }
  },
  created() {
		queryBusinessStatus()
		.then( res => {
			if (!res.code) {
			this.checkStatus = res.data;
			this.checkStatus.push({ value: '', name: '全部' },);
			}else this.$Message.error(res.message);
		})
    this.judgeRouteStatus(this.$route.query);
	},
	computed: {
		date() {
			return this.$store.state.user.date
		}
	},
  watch: {
    '$route' (to, from) {
      this.judgeRouteStatus(to.query);
		},
		date(newVal, oldVal) {
			this.searchByFilter();
		}
	}
}
</script>

<style lang="less" scoped>
  
</style>