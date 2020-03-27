<template>
	<div>
		<Card>
			<p slot="title">
				币种管理
				<Button type="primary" size="small" @click="refreshPageManual">
					<Icon type="refresh"></Icon> 刷新
				</Button>
			</p>

			<Row class="functionWrapper">
				<div class="btnsWrapper clearfix">
					<Button type="error" @click="delCoin">删除</Button>
					<Button type="info" @click="addCoin">添加</Button>
				</div>
			</Row>

			<Row>
				<Table :loading="ifloading" 
								:columns="columnsList" 
								:data="cbData.content"
								@on-selection-change="selectionChange"
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

			<Modal v-model="showEditModal" 
						:mask-closable='false' 
						:width="450"
						@on-ok="saveEditPass"
						@on-cancel="cancelEdit"
						title="编辑币种">
				<Form :model="coinInformation" 
							:rules="ruleValidate" 
							:label-width="100" 
							label-position="right">
					<FormItem label="币种编号：" prop="id" v-show="false">
						<Input v-model="coinInformation.id" ></Input>
					</FormItem>
					<FormItem label="币种名称：" prop="nameCn">
						<Input v-model="coinInformation.nameCn"></Input>
					</FormItem>
					<FormItem label="英文名称：" prop="name">
						<Input v-model="coinInformation.name"></Input>
					</FormItem>
					<FormItem label="货币单位：" prop="unit">
						<Input v-model="coinInformation.unit"></Input>
					</FormItem>
					<FormItem label="交易手续费率（%）：" prop="jyRate">
						<Input v-model="coinInformation.jyRate"></Input>
					</FormItem>
					<FormItem label="卖出广告最低发布数量：" prop="sellMinAmount">
						<Input v-model="coinInformation.sellMinAmount"></Input>
					</FormItem>
					<FormItem label="买入广告最低发布数量：" prop="buyMinAmount">
						<Input v-model="coinInformation.buyMinAmount"></Input>
					</FormItem>
					<FormItem label="状态：" prop="status">
						<i-switch v-model="switchStatus" size="large">
						  <span slot="open">正常</span>
							<span slot="close">失效</span>
						</i-switch>
					</FormItem>
				</Form>
			</Modal>
		</Card>
	</div>
</template>
<script>

import { queryOtcCoin, addOtcCoin, updateOtcCoin, delOtcCoin } from "@/service/getData";

export default {
  name: "CoinManage",
  data() {
    return {
			selectArr: [],
			createOrUpdate: true,
			cbData: {},
			ifloading: false,
			currentPageIdx: 1,
			showEditModal: false,
			switchStatus: true,
			coinInformation: {
        id: null,
        nameCn: '',
        name: '',
        unit: '',
        jyRate: '',
        sellMinAmount: '',
        buyMinAmount: '',
        status: 0
      },
      ruleValidate: {
        nameCn: [
          { required: true, message: '币种名称不能为空', trigger: 'blur' }
        ],
        name: [
          { required: true, message: '英文名称不能为空',trigger: 'blur' }
        ],
        unit: [
          { required: true, message: '货币单位不能为空',trigger: 'blur'}
        ],
        jyRate: [
          { required: true, message: '交易手续费率不能为空',trigger: 'blur'}
        ],
        sellMinAmount: [
          { required: true, message: '卖出广告最低发布数量不能为空',trigger: 'blur' }
        ],
        buyMinAmount: [
          { required: true,message: '买入广告最低发布数量不能为空',trigger: 'blur'}
        ],
        status: [
          { required: true, message: '状态不能为空', trigger: 'blur' }
        ]
      },
      columnsList: [
        {
          type: "selection",
          width: 60,
        },
        {
          title: "币种编号",
          key: "id"
        },
        {
          title: "币种名称",
          key: "nameCn"
        },
        {
          title: "英文名称",
          key: "name"
        },
        {
          title: "货币单位",
          key: "unit"
        },
        {
          title: "交易手续费率",
          key: "jyRate",
          render(h, params) {
            const row = params.row;
            return h("span", {}, row.jyRate + "%");
          }
        },
        {
          title: "卖出广告最低发布数量",
          width: 180,
          key: "sellMinAmount"
        },
        {
          title: "买入广告最低发布数量",
          width: 180,
          key: "buyMinAmount"
        },
        {
          title: "状态",
          key: "status",
          render(h, params) {
            const row = params.row;
            return h("span", {}, !row.status ? "正常" : "失效");
          }
        },
        {
          title: "操作",
          align: "center",
          key: "handle",
          render: (h, obj) => {
            return h("div", [
              h(
                "Button",
                {
                  props: {
                    type: "primary",
                    size: "small"
                  },
                  on: {
                    click: () => {
											this.showEditModal = true;
											this.createOrUpdate = false;
											for (let val in this.coinInformation) {
												this.coinInformation[val] = obj.row[val];
											}
											!obj.row.status ? this.switchStatus = true : this.switchStatus = false;
                    }
                  }
                },
                "修改"
              )
            ]);
          }
        }
      ],
    };
  },
  methods: {
		selectionChange(selection) {
			this.selectArr = selection;
		},
		refreshPageManual() {
			this.refreshPage()
		},
    delCoin() {
			let ids = [];
			this.selectArr.forEach(item => {
				ids.push(item.id);
			})
			if(!ids.length) {
				this.$Message.warning('请选择需要删除的项！')
			}else{
				this.$Modal.confirm({
						title: '删除币种',
						content: `<p>是否删除选中的${this.selectArr.length}项</p>`,
						onOk: () => {
							delOtcCoin({ids: ids})
							.then(res => {
								if(!res.code) {
									this.$Message.success(res.message);
								}else this.$Message.error(res.message);
								this.refreshPage();
							})
							.catch(err => console.log(err))
						},
						onCancel: () => {
							this.$Message.info('已取消！');
						}
				});
			}
    },
    changePage(pageIndex) {
			this.currentPageIdx = pageIndex;
			this.refreshPage({ pageNo: pageIndex, pageSize: 10 });
    },
    addCoin() {
			this.createOrUpdate = true;
      this.showEditModal = true;
    },
    cancelEdit() {
			for(let val in this.coinInformation) {
				this.coinInformation[val] = '';
			}
			this.coinInformation.id = null;
			this.switchStatus = this.coinInformation.status = true;
    },
    saveEditPass() {
			this.switchStatus ? this.coinInformation.status = 0 : this.coinInformation.status = 1;
			let subFn = '';
			this.createOrUpdate ? subFn = addOtcCoin :  subFn = updateOtcCoin;
			subFn(this.coinInformation)
			.then(res => {
				if(!res.code) {
					this.refreshPage();
					this.$Message.success(res.message);
				}else this.$Message.error(res.message);
				this.cancelEdit();
			})
			.catch(err => {console.log(res.message)	})
    },
		refreshPage(obj = {}) {
      this.ifloading = true;
      queryOtcCoin(obj)
			.then(res => {
				if (!res.code) {
					this.cbData = res.data;
				} else this.$Message.error(res.message);
				this.ifloading = false;
			})
			.catch(err => {
				console.log(err);
			});
    }
  },
  created() {
		this.refreshPage();
  },
}
</script>