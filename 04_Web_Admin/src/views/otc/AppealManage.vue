<template>
    <div>
			<Card>
				<p slot="title">
					后台申诉
					<Button type="primary" size="small" @click="refreshPageManual">
						<Icon type="refresh"></Icon> 刷新
					</Button>
				</p>

				<Row class="functionWrapper">
					<div class="searchWrapper clearfix">

					<div class="poptip">
						<Poptip trigger="hover" 
									content="请输入币种搜索" 
									placement="bottom-start">
							<Input placeholder="请输入币种搜索" 
										v-model="filterSearch.unit"/> 
							</Input>      
						</Poptip>
					</div>

					<div class="poptip">
						<Poptip trigger="hover" 
									content="请输入申诉者搜索" 
									placement="bottom-start">
						<Input placeholder="请输入申诉者搜索" 
										v-model="filterSearch.negotiant"/> 
						</Input>      
						</Poptip>
					</div>

					<div class="poptip">
						<Poptip trigger="hover" 
										content="请输入广告主搜索" 
										placement="bottom-start">
							<Input placeholder="请输入广告主搜索" 
										v-model="filterSearch.complainant"/> 
							</Input>      
						</Poptip>
					</div>

						<div class="poptip">
							<span>广告类型：</span>
							<Select v-model="filterSearch.advertiseType">
								<Option v-for="item in orderTypeArr" 
											:value="item.status" 
											:key="item.status">{{ item.text }}</Option>
							</Select>
					</div>

					<div class="poptip">
						<span>订单状态：</span>
						<Select v-model="filterSearch.success">
							<Option v-for="item in orderStatusArr" 
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
					<Table :loading="ifLoading" 
									:columns="columnsList" 
									:data="cbData.content"
									@on-sort-change="definedOrder"  
									border></Table>
				</Row>

				<Row class="pageWrapper" >
					<Page :total="cbData.totalElements" 
								:current="currentPageIdx"   
								@on-change="changePage" 
								show-elevator></Page>
				</Row>

				<Modal v-model="showEditModal" 
							 width="400"
							 class="modelInfo">
					<h3 slot="header">申诉信息</h3>
					<ul>
						<li><span>广告主：</span>{{ `${modelInner.advertiseCreaterName}(${modelInner.advertiseCreaterUserName})` }}</li>
						<li><span>承接人：</span>{{`${modelInner.customerUserName}(${modelInner.customerName})`  }}</li>
						<li><span>订单号：</span>{{ modelInner.orderSn }}</li>
						<li><span>申诉时间：</span>{{ modelInner.createTime }}</li>
						<li><span>申诉备注：</span>{{ modelInner.remark }}</li>
						<li><span>禁用违规方账号：</span><Checkbox v-model="forbidden">禁用</Checkbox></li>
					</ul>
					<div slot="footer" align="middle">
						<Button type="primary" @click="coinOperation(true)">放币</Button>
						<Button type="error" @click="coinOperation(false)">取消订单</Button>
					</div>
				</Modal>

			</Card>
    </div>
</template>
<script>
import { queryAppeal, releaseAppealCoin, cancelAppealOrder } from "@/service/getData";

import util from "@/libs/util";
import axios from "axios";
import qs from "qs";
axios.defaults.withCredentials = true;

export default {
  name: "AppealManage",
  data() {
    return {
			orderTypeArr: [
				{ status: 0, text: '买入' },
				{ status: 1, text: '卖出' },
				{ status: '', text: '全部' }
			],
			sortSearch: {
				direction: [],
				property: []
			},
      modelInner: {},
      ifLoading: false,
      cbData: {},
      currentPageIdx: 1,
      filterSearch: {
        account: "",
				success: ""
      },
      orderStatusArr: [
        { status: 0, text: "败诉" },
        { status: 1, text: "胜诉" },
        { status: "", text: "全部" }
      ],
      showEditModal: false,
      forbidden: false,
      columnsList: [
        {
          title: "订单编号",
          width: 108,
          key: "orderSn"
        },
        {
          title: "广告类型",
          key: "advertiseType",
          render(h, obj) {
            let advertiseType = obj.row.advertiseType;
            return h("span", {}, !advertiseType ? "买入" : "卖出");
          }
        },
        {
          title: "广告主",
          key: "advertiseCreaterName",
          render(h, obj) {
            let userName = obj.row.advertiseCreaterUserName;
            let realName = obj.row.advertiseCreaterName;
            return `${userName}(${realName})`;
          }
        },
        {
          title: "承接人",
          key: "customerUsername",
          render(h, obj) {
            let userName = obj.row.customerUserName;
            let realName = obj.row.customerName;
            return `${userName}(${realName})`;
          }
        },
        {
          title: "申诉者",
          key: "initiatorName"
				},
        {
          title: "币种",
          key: "coinName"
				},
				
        {
          title: "申诉时间",
          key: "createTime",
					width: 108,
					sortable: 'custom'
        },
        {
          title: "订单数",
          key: "number",
          render(h, obj) {
            return h(
              "Tooltip",
              {
                props: {
                  trigger: "hover",
                  placement: "top",
                  content: `手续费:${obj.row.fee}${obj.row.coinName}`
                },
                style: {
                  cursor: "pointer"
                }
              },
              obj.row.number
            );
          }
        },
        {
          title: "订单金额(元)",
          key: "money"
        },
        {
          title: "支付方式",
          key: "payMode"
				},
				{
          title: "订单状态",
					key: "result",
					render: (h, obj) => {
						let text = '未处理';
						if(!obj.row.dealWithTime){
							text = '未处理';
						}else{
							if(!obj.row.isSuccess) text = '申诉失败';
							else text = '申诉成功';
						}
						return h("span", {}, text);
					}	
        },
        // {
        //   title: "订单状态",
        //   key: "orderStatus",
        //   render(h, obj) {
        //     let index = obj.row.orderStatus;
        //     let orderStatus = [
        //       "已取消",
        //       "未付款",
        //       "已付款",
        //       "已完成",
        //       "申诉中"
        //     ];
        //     return h("span", {}, orderStatus[index]);
        //   }
        // },
        {
          title: "操作",
          key: "isSuccess",
          render: (h, obj) => {
            let ifSuccess = obj.row.isSuccess;
            let orderStatus = obj.row.orderStatus;
            let ifDealTime = obj.row.dealWithTime;
            let inner = "";
            let btnType = "primary";
            if (!!ifDealTime && !ifSuccess) {
              inner = "查看";
							btnType = "error";
            } else if (!!ifDealTime && ifSuccess === 1) {
              inner = "查看";
              btnType = "success";
            } else if (!ifDealTime) {
              btnType = "primary";
              inner = "处理";
            }
            return h(
              "Button",
              {
                props: {
                  type: btnType,
                  size: "small"
                },
                on: {
                  click: () => {
                    if (ifDealTime === null) {
                      this.modelInnerFn(obj);
                    }else if (inner === "查看") {
											this.$router.push({path: '/otc/appealmanage/appealdetail', query: { id: obj.row.appealId }})
										}
                  }
                }
              },
              inner
            );
          }
        }
      ]
    };
  },
  methods: {
		definedOrder(obj) {
			let direction = obj.order==='desc' ? 1 : 0;
			let propertyIndex = this.sortSearch.property.indexOf(obj.key);

			if(propertyIndex!==-1){
				this.sortSearch.direction[propertyIndex] = direction;
			}else{
				this.sortSearch.property.push(obj.key);
				this.sortSearch.direction.push(direction);
			}

			let subObj = { pageNo: 1, pageSize: 10};
      Object.assign(subObj, this.filterSearch, this.sortSearch);
			
			this.refreshPage(subObj)
		},
		coinOperation(bol) {
			let subFn = null;
			let title = '';
			let content = '';
			if(bol){
				title = '确认放币';
				content = '是否放币？'
				subFn = releaseAppealCoin;
			} else {
				title = '取消订单';
				content = '是否取消订单？'
				subFn = cancelAppealOrder;
			}
			
			this.showEditModal = false;
			let subObj = {
				orderSn: this.modelInner.orderSn,
				appealId: this.modelInner.appealId,
				banned: this.forbidden
			}
			
			this.$Modal.confirm({
				title: title,
				content: content,
				onOk: () => {
					subFn(subObj)
					.then(res => {
						if(!res.code) this.$Message.success(res.message);
						else this.$Message.error(res.message);
						this.refreshPage({ pageNo: this.currentPageIdx, pageSize: 10 })
					})
					.catch( err => console.log(err))
				},
				onCancel: () => {
					this.$Message.info('您取消了操作！');
				}
			});
		},
    modelInnerFn(obj) {
			this.modelInner = obj.row;
			this.showEditModal = true;
    },
    searchByFilter() {
			this.currentPageIdx = 1;
			let subObj = { pageNo: 1, pageSize: 10};
      Object.assign(subObj, this.filterSearch);
			this.refreshPage(subObj);
		},
    refreshPageManual() {
			this.currentPageIdx = 1;
			for(let val in this.filterSearch)  {
				this.filterSearch[val] = '';
			}
			for(let val in this.sortSearch)  {
				this.filterSearch[val] = [];
			}

      this.refreshPage();
    },
    changePage(pageIndex) {
			this.currentPageIdx = pageIndex;
			let obj = Object.assign({pageNo: pageIndex, pageSize: 10}, this.filterSearch, this.sortSearch);
      this.refreshPage(obj);
    },
    refreshPage(obj = {}) {
      this.ifLoading = true;
      queryAppeal(obj)
        .then(res => {
          if (!res.code) {
            this.cbData = res.data;
          } else this.$Message.error(res.message);

          this.ifLoading = false;
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

<style lang="less">
	.modelInfo{
		h3{
			color: #2D8CF0;
		}
		ul{
			li {
				margin: 5px 0;
				font-size: 14px;
				&>span{
					margin: 0 25px;
					font-weight: 700;
				}
			}
		}
	}
</style>