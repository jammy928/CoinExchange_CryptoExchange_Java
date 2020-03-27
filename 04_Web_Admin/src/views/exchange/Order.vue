<template>
  <div>
    <Card>
      <div slot="title" style="min-height:30px;width: 100%;">
        <div style="display:inline-block;float:left;" class="clearfix">
          <Button :type="btnType===0 ? 'primary' : 'ghost'" @click="localEnsure">当前委托</Button>
          <Button :type="btnType===1 ? 'primary' : 'ghost'" @click="hisEnsure" style="margin-left: 20px;">历史委托</Button>
        </div>
        <Button type="primary" @click="refreshPageManual" style="float:right;">
          <Icon type="refresh"></Icon>刷新
        </Button>
      </div>
      <Row class="priceSectionWrapper clearfix" >

        <div class="priceSection">价格区间：<Input v-model="filterSearch.minPrice"></Input> ~
          <Input v-model="filterSearch.maxPrice"></Input>
        </div>

        <div class="orderStatus">
          <span>订单状态：</span>
          <Select v-model="filterSearch.status">
            <Option v-for="item in orderStatus"
                  :value="item.status"
                  :key="item.status">{{ item.text }}</Option>
          </Select>
        </div>

        <div class="orderStatus">
          <span>订单方向：</span>
          <Select v-model="filterSearch.orderDirection">
            <Option v-for="item in orderDirArr"
                  :value="item.status"
                  :key="item.status">{{ item.text }}</Option>
          </Select>
        </div>

        <div class="orderStatus">
          <span>挂单类型：</span>
          <Select v-model="filterSearch.type" style="width:80px">
            <Option v-for="item in typeArr"
                  :value="item.status"
                  :key="item.status">{{ item.text }}</Option>
          </Select>
        </div>

        <div class="orderStatus">
          <span>机器人：</span>
          <Select v-model="filterSearch.robotOrder" >
            <Option v-for="item in robotOrderArr"
                  :value="item.status"
                  :key="item.status">{{ item.text }}</Option>
          </Select>
        </div>
      </Row>
      <Row class="functionWrapper">
        <div class="searchWrapper clearfix" style="width:100%;">
          <Poptip trigger="hover" content="请输入 币单位 搜索" placement="bottom-start">
            <Input placeholder="请输入 币单位 搜索"
                  v-model="filterSearch.coinSymbol"
                  />
            </Input>
          </Poptip>

          <Poptip trigger="hover" content="请输入 订单号 搜索" placement="bottom-start">
            <Input placeholder="请输入 订单号 搜索"
                  v-model="filterSearch.orderId"
                  />
            </Input>
          </Poptip>
          <Poptip trigger="hover" content="请输入 用户ID 搜索" placement="bottom-start">
            <Input placeholder="请输入 用户ID 搜索"
                  v-model="filterSearch.memberId"
                  />
            </Input>
          </Poptip>

          <Poptip trigger="hover" content="请输入 结算单位 搜索" placement="bottom-start">
            <Input placeholder="请输入 结算单位 搜索"
                  v-model="filterSearch.baseSymbol"
                  />
            </Input>
          </Poptip>

          <div class="btns" style="float:right;">
            <Button type="info" @click="searchByFilter">搜索</Button>
          </div>
        </div>
      </Row>



			<Row>
        <Table
          :columns="columns_first"
          :data="userpage"
          :loading="ifLoading">
				</Table>
			</Row>

			<Row class="pageWrapper" >
				<Page :total="totalNum"
					style='margin-top:8px'
					:current="current"
					@on-change="changePage"
					show-elevator></Page>
			</Row>
    </Card>
  </div>

</template>

<script>

import dtime from 'time-formater'
import { queryBBOrder,cancelOrder } from '@/service/getData';
import { setStore, getStore, removeStore } from '@/config/storage';

export default {
  data() {
    return {
			btnType: 0,
      filterSearch: {
        robotOrder:'',//是否包含机器人
				coinSymbol: '',
        orderDirection: '',
        type: '',
        orderId: '',
        memberId: '',
        baseSymbol: '',
        minPrice: '',
        maxPrice: '',
        status: '',
        pageNo: 1,
				pageSize: 10,
				completed: 0 //0是委托订单1是历史订单
      },
      robotOrderArr:[
        { status: 0, text: '查看机器人' },
        { status: 1, text: '不看机器人' },
        { status: '', text: '全部' }
      ],
      typeArr: [
        { status: 0, text: '市价' },
        { status: 1, text: '限价' },
        { status: '', text: '全部' }
      ],
      orderDirArr: [
        { status: 0, text: '买入' },
        { status: 1, text: '卖出' },
        { status: '', text: '全部' }
      ],
      priceRange: '',
      orderStatusModel: null,
      orderStatus: [
        { status: 0, text: '交易中' },
        { status: 1, text: '已完成' },
        { status: 2, text: '已取消' },
        { status: 3, text: '超时' },
        { status: '', text: '全部' },
      ],
      totalNum: null,
      current:　1,
      priceSecPass: false,
      priceLow: null,
      priceHeight: null,
      coinSymbol: null,
      orderDirection: null,
      orderType: null,
      orderId: null,
      memberId: null,
      baseSymbol: null,
      currentPageIdx: 1,
      ifLoading: true,
      columns_first: [
        {
          title: "订单号",
          key: "orderId",
          width: 180
				},
        {
          title: "用户ID",
          key: "memberId",
				},
        {
          title: "交易对",
          key: "symbol"
        },
        {
          title: "委托量",
          key: "amount"
        },
        {
          title: "成交量",
          key: "tradedAmount"
        },
        {
          title: "挂单类型",
          key: "type",
          render: (h, params) => {
            const row = params.row;

            const type = row.type == "MARKET_PRICE" ? "市价" : "限价";
            return h("span", {}, type);
          }
        },
        {
          title: "订单方向",
          key: "direction",
          render: (h, params) => {
            const row = params.row;
            const direction = row.direction == "BUY" ? "买入" : "卖出";
            return h("span", {}, direction);
          }
        },
        {
          title: "挂单价格",
          key: "price"
        },
        {
          title: "挂单时间",
          width: 100,
          render: (h, obj) => {
            let formatTime = dtime(obj.row.time).format('YYYY-MM-DD HH:mm:ss')
            return h('span',{},formatTime)
          }
        },
        {
          title: "状态",
          key: "status",
          render: (h, params) => {
            const row = params.row;
            var direction = '';
            if(row.status == 'TRADING'){
                direction = '交易中';
            }
            else if(row.status == 'COMPLETED'){
                direction = '已完成'
            }
            else if(row.status == 'CANCELED'){
                direction = '已取消'
            }
            return h("span", {}, direction);
          }
        },
        {
          title: "操作",
          key: "age",
          width: 150,
          render: (h, obj) => {
            var actions = [];
            actions.push(h(
                "Button",
                {
                  props: {
                    type: "info",
                    size: "small"
                  },
                  style: {
                    marginRight: '10px'
                  },
                  on: {
                    click: () => {
                      this.$router.push('/exchange/bborder/detail');
                      removeStore('exchangeOrderId');
                      removeStore("completed")
                      setStore('exchangeOrderId',obj.row.orderId);
                      setStore('completed',obj.row.completed);
                    }
                  }
                },
                "明细"));
            if(obj.row.status == 'TRADING'){
             actions.push( h(
                "Button",
                {
                  props: {
                    type: "error",
                    size: "small"
                  },
                  on: {
                    click: () => {
                      this.cancelOrder(obj.row.orderId)
                    }
                  }
                },
                "撤销"));
            }
            return h("div", actions);
          }
        }
      ],
      userpage: []
    };
  },
  methods: {
		// switchEnsure() {

		// },
		localEnsure() {
			this.filterSearch.pageNo = 1;
			this.current = 1;
			this.btnType = 0;
			this.filterSearch.completed = 0;
			this.refreshPage(this.filterSearch);
		},
		hisEnsure () {
			this.filterSearch.pageNo = 1;
			this.current = 1;
			this.btnType = 1;
			this.filterSearch.completed = 1;
			this.refreshPage(this.filterSearch);
		},
    searchByFilter() {
			let reg = /[^0-9]/;
      // alert(this.memberId)
      this.current = 1
			let bol = reg.test(this.memberId);
			if(bol&&(!!this.memberId)) {
				this.$Message.warning('请输入正确的id！')
				return;
			}

      if(isNaN(this.priceLow*1) ||　isNaN(this.priceHeight*1)) {
        this.$Message.warning('请输入正确的价格！')
      }else if(this.priceLow*1<0 || this.priceHeight*1<0) {
        this.$Message.warning('价格应该大于等于零！')
      } else {
        if(this.priceLow*1>this.priceHeight*1) {
          this.$Message.warning('最低价格不能高于最高价格')
        }else{
					this.$store.commit('switchLoading', true);
          this.refreshPage(this.filterSearch);
        }
      }
    },
    refreshPageManual() {
			for(let key in this.filterSearch)  {
				this.filterSearch[key] = '';
			}
      this.currentPageIdx = 1;
      this.current = 1;
			this.btnType = 0;
			this.filterSearch.pageNo = 1;
			this.filterSearch.pageSize = 10;
			this.filterSearch.completed = 0;
      this.refreshPage(this.filterSearch);
    },
   changePage(pageIndex) {
			this.current= pageIndex;
			this.filterSearch.pageNo = pageIndex;
      this.refreshPage( this.filterSearch);
    },
    refreshPage(obj) {
      this.ifLoading = true;
      queryBBOrder(obj).then(res => {
        this.userpage = res.data.content;
        this.totalNum = res.data.totalElements;
				this.ifLoading = false;
				this.$store.commit('switchLoading', false);
      });
    },
    cancelOrder(orderId){
			cancelOrder({orderId:orderId}).then(res=>{
				if(res.code == 0){
					this.$Message.success('撤销成功');
					this.refreshPage(this.filterSearch);
				}else{
					this.$Message.error('撤销失败');
				}
			})
			.catch(err => console.log(err))
    }
  },
  created() {
    this.refreshPage(this.filterSearch);
  }
}
</script>

<style lang="less" scoped>
.switchTab {
	margin: 20px 0;
}
Input,.ivu-select.ivu-select-single{
  width: 150px;
}
.priceSectionWrapper{
  margin-bottom: 10px;
  .ivu-input-wrapper.ivu-input-type {
    width: 60px ;
  }
  .priceSection {
    float: left;
  }
  .orderStatus {
    margin-left: 20px;
    float: left;
  }
  .btns{
    margin-left: 10px;
    float: left;
    height: 30px;
    line-height: 30px;
  }
}
.tips{
  color: red;
}
</style>
