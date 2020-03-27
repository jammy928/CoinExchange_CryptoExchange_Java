
<template>
  <div>
    <Card>
      <p slot="title">
        系统信息维护
        <Button type="primary" size="small" @click="refreshPageManual">
          <Icon type="refresh"></Icon>
          刷新
        </Button>
      </p>
			<Row class="functionWrapper">
				<div class="btnsWrapper clearfix ">
					<Button type="primary" @click="addNew">新增</Button>
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
				v-model="subModal"
				title="创建字典"
				@on-ok="comfirmSub"
				@on-cancel="cancelSub"
				:closable='false'
				:mask-closable='false'
				:width="400"
			>
			<Form 
				:model="formObj" 
				label-position="right" 
				:label-width="50" 
				:rules="validateForm">
				<FormItem label="键：" prop="bond">
					<Input v-model="formObj.bond"></Input>
				</FormItem>
				<FormItem label="值：" prop="value">
					<Input v-model="formObj.value"></Input>
				</FormItem>
				<FormItem label="备注：">
					<Input type="textarea" v-model="formObj.comment"></Input>
				</FormItem>
			</Form>
			</Modal>


    </Card>
    </div>
</template>

<script>

import { dictionaryQuery, createDictionary, updateDictionary } from '@/service/getData';

export default {
	name: 'AccessLog',
  data() {
		
    return {
			validateForm: {
				bond: [
					{ required: true, message: '请填写信息', type: 'string', trigger: 'blur' }
				],
				value: [
					{ required: true, message: '请填写信息', type: 'string', trigger: 'blur' }
				]
			},
			isCreateNew: true,
			subModal: false,
			formObj: {
				bond: '',
				value: '',
				comment: ''
			},
			filterSearch: {
				pageNo: 1,
				pageSize: 10,
				property: [],
				direction: [],
			},
      currentPageIdx: 1,
      ifLoading: true,
      totalNum: null,
      columns_first: [
        {
					title: 'id',
					key: 'id'
        },
        {
          title: "键",
          key: "bond"
        },
        {
          title: "值",
          key: "value"
        },
				{
          title: "备注",
          key: "comment"
        },
        {
					title: "创建时间",
					key: 'creationTime'
        },
        {
          title: "更新时间",
          key: "updateTime"
				},
				{
					title: '操作',
					key: 'opration',
					render: (h, obj) =>{
						return h('Button',{
							props: {
								size: 'small',
								type: 'info'
							},
							on: {
								click: () => {
									this.subModal = true;
									this.isCreateNew = false;
									for(let key in this.formObj)  {
										this.formObj[key] = obj.row[key];
									}
								}
							}
						},'修改')
					}
				}
      ],
      userpage: [],
    };
  },
  methods: {
		addNew() {
			this.subModal = this.isCreateNew = true;
		},
		comfirmSub() {
			if(!String(this.formObj.value).trim().length 
			|| !String(this.formObj.bond).trim().length) 
			{return this.$Message.warning('请完善信息！');}
			let fn = Object;
			if(this.isCreateNew) {
				fn = createDictionary(this.formObj);
			}else{
				let obj = { value: this.formObj.value, comment: this.formObj.comment }
				fn = updateDictionary( this.formObj.bond, obj);
			}
			fn.then(res => {
				if(!res.code) {
					this.$Message.success(res.message);
					this.cancelSub();
				}else this.$Message.error(res.message);
				this.refreshPage();
			})
			.catch(err => {
				console.log(err);
			})
		},
		cancelSub() {
			for(let key in this.formObj)  {
				this.formObj[key] = '';
			}
		},
		searchByFilter() {
			this.filterSearch.pageNo = 1;
			this.currentPageIdx = 1;
      this.refreshPage(this.filterSearch);
		},
    refreshPageManual() {
			this.currentPageIdx = 1;
			this.filterSearch.pageNo = 1;
			this.filterSearch.pageSize = 10;
			this.filterSearch.adminName = '';
			this.filterSearch.module = -1;
      this.refreshPage(this.filterSearch);
    },
    refreshPage(obj) {
			this.ifLoading = true;
      dictionaryQuery('', obj)
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
		this.refreshPage()
  }
}
</script>

<style scoped lang='less'>
 
</style>
