<template>
	<div>
    <Card>
      <p slot="title">
        会员管理
        <Button type="primary" size="small" @click="refreshPageManual">
          <Icon type="refresh"></Icon>刷新
        </Button>
      </p>
      <Row class="functionWrapper">
        <div class="searchWrapper clearfix">
					<div class="poptip">
						<Poptip trigger="hover" content="请输入用户名、邮箱、手机号、姓名搜索" placement="bottom-start">
							<Input placeholder="请输入用户名、邮箱、手机号、姓名搜索"
										v-model="filterSearch.account"
										style="width: 300px"/>
							</Input>
						</Poptip>
					</div>

					<div class="poptip">
						<span>会员状态：</span>
						<Select v-model="filterSearch.commonStatus">
							<Option v-for="item in memberStatusArr"
										:value="item.status"
										:key="item.status">{{ item.text }}</Option>
						</Select>
					</div>

					<div class="btns">
						<Button type="info" size="small" @click="searchByFilter">搜索</Button>
					</div>

        </div>
        <div class="btnsWrapper clearfix">
          <Button type="success" @click="exportExcel">导出</Button>
        </div>
      </Row>

      <Row class="margin-top-10 searchable-table-con1">
          <Table
            :columns="columns_first"
            :data="userpage"
            border
            :loading="ifLoading"
            ref="tabel"
            class='user_center'>
          </Table>
      </Row>

      <Row class="pageWrapper" >
        <Page :total="totalNum" style='margin-top:8px' :current="currentPageIdx"   @on-change="changePage" show-elevator></Page>
      </Row>
    </Card>

  </div>
</template>

<script>

import { setStore, getStore, removeStore } from '@/config/storage';
import { memberManage, forbiddenMember, forbiddenMemberTrans  } from '@/service/getData';

export default {
  data () {
    return {
			currentPageIdx: 1,
			filterSearch: {
				account: '',
				commonStatus: ''
			},
			memberStatusArr: [
				{ status: 0, text: '正常' },
				{ status: 1, text: '非法' },
				{ status: '', text: '全部' },
			],
      totalNum: null,
      ifLoading: true,
      usemmuber:'',
      pageIndex:1,
      loading:true,
      columns_first: [
        {
          type: 'selection',
          width: 60
        },
        {
          title: '会员ID',
          key: 'id',
          width: 70
        },
        {
          title: '会员名称',
          key: 'username',
          width: 120
        },
        {
          title: '手机号码',
          key: 'mobilePhone',
          width: 110
        },
        {
          title: '会员等级',
          key: 'memberLevel',
          width:90,
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
          title: '真实姓名',
          key: 'realName',
          width:90
        },
        {
          title: '邀请者ID',
          key: 'inviterId',
          width:90
        },
        {
          title: '注册时间',
          width: 150,
          key: 'registrationTime'
				},
				{
          title: '交易状态',
          key: 'transactionStatus',
          width:90,
          render: (h, obj) => {
            let  userStatus = obj.row.transactionStatus;
           	let statusTxt = !userStatus ? '禁用' : '正常';
            return h('span', {
            }, statusTxt);
          }
        },

        {
          title: '状态',
          width:90,
          key: 'status',
          render: (h, obj) => {
            let  userStatus = obj.row.status;
            let statusTxt = null;
            userStatus === 0 ? statusTxt = '正常' : statusTxt = '非法';

            return h('span', {
            },statusTxt);
          }
        },
        {
          title: '会员邮箱',
          key: 'email',
          width: 150
        },
        {
         title:"合伙人",
         key:"superPartner",
         width: 100,
         render:(h, obj) =>{
           let superPartner = obj.row.superPartner;
          //  console.log(superPartner);
           let text = null;
           superPartner == 0 && (text = "普通会员");
           superPartner == 1 && (text = "俱乐部超级合伙人");
           superPartner == 2 && (text = "超级群主");
           return h('span',{},text);
         }
        },
        {
          title: '操作',
          key: 'action',
          width: 150,
          fixed: 'right',
          align: 'center',
          render: (h, obj) => {
						let memberStatus = !obj.row.status ? 1 : 0;
						let memberTxt = !obj.row.status ? '禁用' : '解禁';
						let memberStatusTrans = !obj.row.transactionStatus ? 1 : 0;
						let memberTxtTrans = !obj.row.transactionStatus ? '允许交易' : '禁止交易';
						// let
						return h('div', {
						}, [
							h('Button', {
								props: {
									type: 'info',
									size: 'small',
								},
								on: {
									click: () => {
										removeStore('memberID');
										setStore('memberID', obj.row.id);
										this.$router.push('/member/memberdetail');
									}
								}
							}, '查看'),
							h('Dropdown', {
								props: {
									transfer: true
								},
								on: {
									'on-click': (name) => {
										if(name === 'forbidden') {

											forbiddenMember({ memberId: obj.row.id, status: memberStatus })
											.then(res => {
												if(!res.code) {
													this.$Message.success(res.message)
												}else this.$Message.error(res.message)
												this.refreshPage({ pageNo: this.currentPageIdx, pageSize: 10, property: 'registrationTime', direction: 1 })
											})
											.catch( err => { console.log(err)})
										}else if(name === 'forbiddenTrans') {
											forbiddenMemberTrans({ memberId: obj.row.id, status: memberStatusTrans })
											.then(res => {
												if(!res.code) {
													this.$Message.success(res.message)
												}else this.$Message.error(res.message)
												this.refreshPage({ pageNo: this.currentPageIdx, pageSize: 10, property: 'registrationTime', direction: 1 })
											})
										}
									}
								}
							}, [
								h('DropdownMenu', {
									slot: 'list'
								}, [
										h("DropdownItem",{
											props: {
												name: 'forbidden'
											}
										}, memberTxt),
										h("DropdownItem",{
											props: {
												name: 'forbiddenTrans'
											}
										}, memberTxtTrans),
								]),
								h("Button",{
									props: {
										type: "text",
										size: "small"
									},
									style: {
										color: '#2d8cf0',
										marginRight: "5px"
									},
								},"更多")
							])
						])

          }
        }
      ],
      userpage: []
    }
  },
  methods: {
    refreshPageManual() {
    for(let val in this.filterSearch)  {
			this.filterSearch[val] = '';
		}
		this.currentPageIdx = 1;
      this.refreshPage({ pageNo: 1, pageSize: 10, property: 'registrationTime', direction: 1 });
    },
    exportExcel () {
      this.$refs.tabel.exportCsv({
        filename: 'hello'
      });
    },
    searchByFilter(){
			this.$store.commit('switchLoading', true);
      memberManage(this.filterSearch)
      .then( res => {
        if (!res.code) {
          this.ifLoading = false;
          this.userpage = res.data.content;
					this.totalNum = res.data.totalElements;
					this.$store.commit('switchLoading', false);
        }else console.log(this.$Message.error(res.message))
			})
			.catch(err => console.log(err))
    },

    changePage(pageIndex) {
      this.currentPageIdx = pageIndex;
			this.ifLoading = true;
			let obj =Object.assign({ pageNo: pageIndex, pageSize: 10, property: 'registrationTime', direction: 1 }, this.filterSearch);
      this.refreshPage(obj);
    },
    refreshPage(obj = {}) {
			this.ifLoading = true;
      memberManage(obj)
      .then( res => {
        if (!res.code) {
          this.ifLoading = false;
          this.userpage = res.data.content;
          this.totalNum = res.data.totalElements;
        }
      })
		}
  },
  created() {
    this.refreshPage({  property: 'registrationTime', direction: 1 });
  },
  // watch: {
  //   // account( newval, oldVal ) {
  //   //   if(!String(newval).length ) {
  //   //     this.refreshPage({  property: 'registrationTime', direction: 1 });
  //   //   }
  //   // }
  // }
}
</script>

<style lang="less" scoped>
  .search-mask{
    position: absolute;
    z-index: 8888;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(0, 0, 0, .2);
  }
</style>
