<template>
  <div>
    <Card>
      <p slot="title">
        币币委托详情
        <!-- <Button type="primary" size="small" @click="refreshPageManual">
          <Icon type="refresh"></Icon>
          刷新
        </Button> -->
      </p>

      <div class="baseInfo">
        <Row>
          <Col span="6">
          <p>委托单号：<span>{{detail[0].orderId}}</span></p>
          </Col>
          <Col span="6">
          <p>状态：<span>{{getStatus(detail[0].status)}}</span></p>
          </Col>
          <Col span="6">
          <p>委托时间：<span>{{getTime(detail[0].time)}}</span></p>
          </Col>
          <Col span="6">
          <p>委托类型：<span>{{getStyle(detail[0].type)}}</span></p>
          </Col>
        </Row>

        <Row>
          <Col span="6">
          <p>委托价格：<span>{{detail[0].price}}</span></p>
          </Col>
          <Col span="6">
          <p>委托数量：<span>{{detail[0].amount}}</span></p>
          </Col>
          <Col span="6">
          <p>成交数量：<span>{{detail[0].tradedAmount}}</span></p>
          </Col>
        </Row>
      </div>

    </Card>
    <br><br>
    <Card>
      <p slot="title">
        交易记录
      </p>
      <Row>
        <Table :columns="columnsList" :data="userpage" border :loading="ifLoading" class='user_center'>
        </Table>
      </Row>

      <Row class="pageWrapper">
        <Page :total="totalNum" style='margin-top:8px' :current="current" @on-change="changePage" show-elevator></Page>
      </Row>
    </Card>
  </div>
</template>

<script>
import dtime from "time-formater";
import { exgOrderDetail, queryBBOrder } from "@/service/getData";
import { setStore, getStore, removeStore } from "@/config/storage";
export default {
  data() {
    return {
      currentPageIdx: null,
      totalNum: null,
      ifLoading: true,
      current: 1,
      pageIndex: 1,
      columnsList: [
        {
          title: "订单号",
          key: "orderId"
        },
        {
          title: "挂单价格",
          key: "price"
        },
        {
          title: "挂单量",
          key: "amount"
        },
        {
          title: "手续费",
          key: "fee"
        },
        {
          title: "交易时间",
          key: "time",
          render: (h, obj) => {
            let formatTime = dtime(obj.row.time).format("YYYY-MM-DD HH:mm:ss");
            return h("span", {}, formatTime);
          }
        }
      ],
      userpage: [],
      detail: [
        {
          amount: null,
          baseSymbol: null,
          canceledTime: null,
          coinSymbol: null,
          completed: null,
          completedTime: null,
          detail: null,
          direction: null,
          memberId: null,
          orderId: null,
          price: null,
          status: null,
          symbol: null,
          time: null,
          tradedAmount: null,
          turnover: null,
          type: null,
          useDiscount: null
        }
      ],
      status: ["交易中", "已完成", "已取消"]
    };
  },
  methods: {
    refreshPageManual() {
      this.ifLoading = true;
      this.refreshPage({ pageNo: this.currentPageIdx, pageSize: 10 });
    },
    getTime(time){
      return dtime(time).format('YYYY-MM-DD HH:mm:ss')
    },
    getStyle(str){
      return str=="MARKET_PRICE" ? "市价" : "限价";
    },
    getStatus(str) {
      var direction = ""
      if (str == "TRADING") {
        direction = "交易中";
      } else if (str == "COMPLETED") {
        direction = "已完成";
      } else if (direction == "CANCELED") {
        direction = "已取消";
      }
      return direction;
    },
    changePage(pageIndex) {
      this.ifLoading = true;
      this.currentPageIdx = pageIndex;
      this.refreshPage({ pageNo: pageIndex, pageSize: 10 });
    },
    refreshPage() {
      let orderID = getStore("exchangeOrderId");
      let completed = JSON.parse(getStore("completed"));
      completed = completed ? 1 : 0;
      let obj = { orderId: orderID, completed: completed };
      queryBBOrder(obj).then(res => {
        if (!res.code) {
          this.detail = res.data && res.data.content;
        } else {
          this.$Message.error("查询错误");
        }
      });
      exgOrderDetail({ id: orderID }).then(res => {
        this.userpage = res.data;
        this.totalNum = res.data.length;
        this.ifLoading = false;
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