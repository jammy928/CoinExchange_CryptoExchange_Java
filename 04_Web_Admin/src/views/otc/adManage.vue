<template>
	<div>
		<Card>
				<p slot="title">
					后台广告
					<Button type="primary" size="small" @click="refreshPageManual">
						<Icon type="refresh"></Icon> 刷新
					</Button>
				</p>
			
				<Row class="functionWrapper">
					<div class="btnsWrapper">
						<Button type="error" @click="batchDel">批量删除</Button>
					</div>

					<div class="searchWrapper clearfix">
						<div class="poptip">
							<Poptip trigger="hover" 
											content="请输入手机号、真实姓名或会员名称搜索" 
											placement="bottom-start">
								<Input placeholder="请输入手机号、真实姓名或会员名称搜索" 
											v-model="filterSearch.account"/> 
								</Input>      
							</Poptip>
						</div>

						<div class="poptip">
								<Poptip trigger="hover" 
											content="请输入支付方式搜索" 
											placement="bottom-start">
								<Input placeholder="请输入支付方式搜索" 
											v-model="filterSearch.payModel"/> 
								</Input>      
							</Poptip>
						</div>

						<div class="poptip">
							<span>类型：</span>
							<Select v-model="filterSearch.advertiseType">
								<Option v-for="item in advertiseTypeArr" 
											:value="item.status" 
											:key="item.status">{{ item.text }}</Option>
							</Select>
						</div>

						<div class="poptip">
							<span>状态：</span>
							<Select v-model="filterSearch.status">
								<Option v-for="item in advertiseStatusArr" 
											:value="item.status" 
											:key="item.status">{{ item.text }}</Option>
							</Select>
						</div>
					
						<div class="btns">
							<Button type="info" size="small" @click="searchByFilter">搜索</Button>
						</div>
					</div>

				</Row>

				<Row>
					<Table :loading="ifloading" 
							 	 :columns="columnsList" 
							 	 :data="cbData.content"
								 @on-selection-change="selectionChange" 
								 border></Table>
				</Row>

				<Row class="pageWrapper" >
					<Page :total="cbData.totalElements" 
								:current="currentPageIdx"   
								@on-change="changePage" 
								show-elevator></Page>
				</Row>
		</Card>
	</div>
</template>
<script>
import { queryOtcAdv, upDownAdv } from "@/service/getData";

export default {
  data() {
    return {
			selection: [],
      cbData: {},
      currentPageIdx: 1,
      filterSearch: {
        account: "",
				advertiseType: "",
				status: "",
        payModel: ""
      },
      advertiseTypeArr: [
        { status: 0, text: "买入" },
        { status: 1, text: "卖出" },
        { status: "", text: "全部" }
			],
			advertiseStatusArr: [
        { status: 0, text: "上架" },
        { status: 1, text: "下架" },
        { status: "", text: "全部" }
      ],
      ifloading: false,
      columnsList: [
        {
          type: "selection",
          width: 60
        },
        {
          title: "广告编号",
          width: 108,
          key: "id"
        },
        {
          title: "创建时间",
          key: "createTime"
        },
        {
          title: "创建人",
          render: (h, obj) => {
            let member = obj.row.member;
            return `${member.username}(${member.realName})`;
          }
        },
        {
          title: "币种",
          render: (h, obj) => {
            return obj.row.coin.unit;
          }
        },
        {
          title: "广告数量",
          key: "number"
        },
        {
          title: "广告金额",
          key: "price"
        },
        {
          title: "广告限额",
          align: "center",
          render: (h, obj) => {
            return `${obj.row.minLimit}-${obj.row.maxLimit}`;
          }
        },
        {
          title: "类型",
          key: "advertiseType",
          render(h, params) {
            const row = params.row;
            return h("span", {}, row.advertiseType === 0 ? "买入" : "卖出");
          }
        },
        {
          title: "支付方式",
          key: "payMode"
        },
        {
          title: "广告状态",
          key: "status",
          render(h, params) {
            const row = params.row;
            return h("span", {}, !row.status ? "上架" : "下架");
          }
        },
        {
          title: "操作",
          align: "center",
          width: 150,
          key: "handle",
          render: (h, obj) => {
            let advStatus = obj.row.status;
            return h("div", [
              h(
                "Button",
                {
                  props: {
                    size: "small",
                    type: "primary",
                    disabled: !!advStatus ? true : false
                  },
                  style: {
                    marginRight: "5px"
                  },
                  on: {
                    click: () => {
                      this.operation({ ids: [obj.row.id], status: 1 });
                    }
                  }
                },
                "下架"
							),
							h('Button', {
								props: {
									type: 'info',
									size: 'small'
								},
								on: {
									click: () => {
										this.$router.push({name: 'otc:adManage:advertisedetail', query: {advID : obj.row.id}})
									}
								}
							}, '详情')
              // h(
              //   "Button",
              //   {
              //     props: {
              //       type: "error",
              //       size: "small",
              //       disabled: !(advStatus === 1 || !advStatus) ? true : false
              //     }
              //   },
              //   [
              //     h(
								
              //       "Poptip",
              //       {
              //         props: {
              //           confirm: true,
              //           transfer: true,
              //           title: "您确定要删除这条数据吗"
              //         },
              //         on: {
              //           "on-ok": () => {
              //             this.operation({ ids: [obj.row.id], status: 2 });
              //           },
              //           "on-cancel": () => {
              //             this.$Message.info("已取消！");
              //           }
              //         }
              //       },
              //       advStatus === 1 || advStatus === 0 ? "删除" : "已删除"
              //     )
              //   ]
              // )
            ]);
          }
        }
      ]
    };
  },
  methods: {
		selectionChange(selection) {
			this.selection = selection;
		},
		batchDel() {
			let ids = [];
      console.log(this.selection)
			this.selection.forEach(item => {
				ids.push(item.id);
			})
			this.operation({ ids: ids, status: 2 });
		},
    operation(obj) {
      upDownAdv(obj).then(res => {
          if (!res.code) {
            this.$Message.success(res.message);
            this.refreshPage({ pageNo: this.currentPageIdx, pageSize: 10 });
          } else this.$Message.error(res.message);
        }).catch(err => console.log(err));
    },
    searchByFilter() {
			this.currentPageIdx = 1;
      let obj = Object.assign(this.filterSearch, { pageNo: 1, pageSize: 10 });
      this.refreshPage(obj);
    },
    refreshPageManual() {
      for (let key in this.filterSearch) {
        this.filterSearch[key] = "";
      }
      this.refreshPage();
    },
    changePage(pageIndex) {
      this.currentPageIdx = pageIndex;
      let obj = Object.assign(this.filterSearch, {
        pageNo: pageIndex,
        pageSize: 10
      });
      this.refreshPage(obj);
    },

    refreshPage(obj = {}) {
      this.ifloading = true;
      queryOtcAdv(obj).then(res => {
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
  }
};
</script>

<style lang="less" scoped>

</style>