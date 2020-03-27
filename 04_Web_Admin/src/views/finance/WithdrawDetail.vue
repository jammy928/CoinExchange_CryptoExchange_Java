<template>
  <div>
    <Card>
      <p slot="title">
        提币明细
        <Button type="primary" size="small" @click="refreshPageManual">
          <Icon type="refresh"></Icon>
          刷新
        </Button>
      </p>

      <Row class="functionWrapper">
				<div class="searchWrapper clearfix">
					<!-- <div class="poptip">
						<Poptip trigger="hover"
										content="请输入用户昵称查询"
										placement="bottom-start">
							<Input placeholder="请输入用户昵称查询"
										v-model="filterSearch.memberUsername"/>
							</Input>
						</Poptip>
					</div> -->

					<div class="poptip">
						<Poptip trigger="hover"
										content="请输入真实姓名或用户昵称查询"
										placement="bottom-start">
							<Input placeholder="请输入真实姓名或用户昵称查询" style="width:200px"
										v-model="filterSearch.account"/>
							</Input>
						</Poptip>
					</div>

					<div class="poptip">
						<Poptip trigger="hover"
										content="请输入手机号查询"
										placement="bottom-start">
							<Input placeholder="请输入手机号查询"
										v-model="filterSearch.mobilePhone"/>
							</Input>
						</Poptip>
					</div>

					<div class="poptip">
						<span>币种：</span>
						<Select v-model="filterSearch.unit">
							<Option v-for="(item, index) in coinSearchArr" :value="item.unit" :key="item.unit">{{ item.unit }}</Option>
						</Select>
					</div>

					<div class="btns">
						<Button type="info" size="small" @click="searchByFilter">搜索</Button>
					</div>

				</div>
			</Row>

      <Row class="tableWrapper">
        <Table :columns="columns_first"
							@on-sort-change="definedOrder"
							:data="userpage" border
							:loading="ifLoading">
				</Table>
      </Row>

      <Row class="pageWrapper" >
        <Page :total="pageNum" :current="currentPageIdx" @on-change="changePage" show-elevator></Page>
      </Row>

    </Card>
  </div>
</template>

<script>
import { getCoinName, withdrawManage  } from '@/service/getData';
  export default{
    data () {
      return {
				coinSearchArr: [],
				filterSearch: {
					pageNo: 1,
					pageSize: 10,
					property: [],
					direction: [],
					// memberUsername: '',
					// memberRealName: '',
					account:"",
					mobilePhone: '',
					unit: ''
				},
        currentPageIdx: 1,
        ifLoading: true,
        pageNum: null,
        userpage: [],
        columns_first: [
          {
            title: 'TXID',
            key: 'transactionNumber'
          },
		  {
            title: '币种名称',
            key: 'unit'
          },
          {
            title: '提币个数',
            key: 'totalAmount'
          },
          {
						title: '实际到账数',
						key: 'arrivedAmount'
          },
          {
						title: '提币手续费',
						key: 'fee'
          },
          {
            title: '申请时间',
            key: 'createTime'
          },
          {
            title: '用户昵称',
            key: 'memberUsername'
          },
          {
            title: '邮箱',
            key: 'email'
          },
          {
            title: '手机号',
            key: 'phone'
          },
          {
            title: '真实姓名',
            key: 'memberRealName'
          },
          {
            title: '钱包地址',
            key: 'address'
          },
          {
            title: '审核时间',
            key: 'dealTime'
          },

        ]
      }
    },
    methods: {
			definedOrder(obj) {
				let direction = obj.order==='desc' ? 1 : 0;
				let propertyIndex = this.filterSearch.property.indexOf(obj.key);

				if(propertyIndex!==-1){
					this.filterSearch.direction[propertyIndex] = direction;
				}else{
					this.filterSearch.property.push(obj.key);
					this.filterSearch.direction.push(direction);
				}

				this.refreshPage(this.filterSearch);
			},
			searchByFilter() {
				let reg = /1[3456789]\d{9}/g;
				let phone = this.filterSearch.mobilePhone;
				if(phone && !reg.test(phone)){
						this.$Message.error("请输入正确的手机号")
				}else{
					this.currentPageIdx = 1;
					this.filterSearch.pageNo = 1;
					this.refreshPage(this.filterSearch)
				}
			},
      refreshPageManual() {
				this.currentPageIdx = 1;
				for(let key in this.filterSearch) {
					this.filterSearch[key] = '';
				}
				this.filterSearch.pageNo = 1;
				this.filterSearch.pageSize = 10;
				this.refreshPage(this.filterSearch);
      },
      changePage(pageIndex) {
				this.currentPageIdx = pageIndex;
				this.filterSearch.pageNo = pageIndex;
				this.refreshPage(this.filterSearch)
      },
      refreshPage(obj) {
				this.ifLoading = true;
        withdrawManage(obj).then( res => {
          if(!res.code) {
            this.ifLoading = false;
            this.pageNum = res.data.totalElements;
            this.userpage = res.data.content;
          }else this.$Message.error(res.message)
				})
      }
    },
    created () {
			getCoinName()
			.then(res => {
				if (!res.code) {
					this.coinSearchArr = res.data;
					// this.coinSearchArr.push({ name: '全部', unit: '' })
				} else this.$Message.error(res.message);
			})
			.catch(err => {
				console.log(err);
			});
      this.refreshPage();
    }
  }
</script>

<style>
/* .ivu-select-item-selected, .ivu-select-item-selected:hover{
	background: #fff;
	color: #000;
} */
</style>
