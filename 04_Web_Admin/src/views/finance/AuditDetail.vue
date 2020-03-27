<template>
  <div>
    <Card>
      <p slot="title">
        审核详情
        <Button type="primary" size="small" @click="reset">
          <Icon type="refresh"></Icon>
          刷新
        </Button>
      </p>

      <div class="stepWrapper">
        <Steps :current="status">
          <Step title="已审核" v-if="!!status"></Step>
          <Step title="待审核" v-if="!status"></Step>
          <Step title="转账中"></Step>
          <Step title="交易失败" v-if="status===2"></Step>
          <Step title="交易成功" v-if="status!==2"></Step>
        </Steps>
      </div>
      <Row class="userDetails">

        <span>用户名：
          <i>{{ userInfo.memberUsername }}</i>
        </span>
        <span>真实姓名：
          <i>{{ userInfo.memberRealName }}</i>
        </span>
        <span>到账数量：
          <i>{{ userInfo.arrivedAmount }}</i>
        </span>
        <span>提现地址：
          <i>{{ userInfo.address }}</i>
        </span>

        <br>
        <span>提现币种：
          <i>{{ userInfo.unit }}</i>
        </span>

        <span>手续费：
          <i>{{ userInfo.fee }}</i>
        </span>
        <span>提现数量：
          <i>{{ userInfo.totalAmount }}</i>
        </span>
        <span>申请时间：
          <i>{{ userInfo.createTime }}</i>
        </span>

        <div class="passCoinWrapper" v-if="status===1&&isAuto===0">
          <Button type="error" size="large" @click=" ifPassCoin = true ">开始放币</Button>
        </div>

      </Row>

      <Modal class="passCoinModal" width="400" v-model="ifPassCoin" @on-ok="confrimPass">
        <div>请输入流水号：
          <p>
            <Input type="textarea" v-model="transactionNum"></Input>
          </p>
        </div>
      </Modal>

      <Row class="tradeRecord">
        <h3>交易记录</h3>
        <div class="tableWrapper">
          <div class="poptip" style="display:flex;flex-direction:row;margin-bottom: 10px;">
            <div style="width:100px;">交易类型：</div>
            <Select v-model="tradeType" style="width:100px;margin-right:20px;">
              <Option v-for="(item, index) in tradeTypeArr"
                    :value="index"
                    :key="item">{{ item }}</Option>
            </Select>
            <Button type="info" size="small" @click="searchByFilter" style="padding: 5px 20px;">搜索</Button>
          </div>
          <Table :columns="columns_first" :data="trade_data"></Table>
          <Page :total="totalNum" :page-size=10  @on-change="changePage"></Page>
        </div>
        <p class="line"></p>
        <div class="btnContainer" v-if="!status">
          <Button type="error" size="large" @click="ifPass(false)">不通过</Button>
          <Button type="success" size="large" @click="ifPass(true)">通过</Button>
        </div>
      </Row>

    </Card>

  </div>
</template>

<script>
import Cookies from "js-cookie";
import { setStore, getStore, removeStore } from "@/config/storage";
import {
  auditPass,
  auditNoPass,
  withdrawManage,
  perTradeAll,
  passCoinByOne
} from "@/service/getData";

export default {
  data() {
    return {
      isAuto: null,
      transactionNum: null,
      ifPassCoin: false,
      userInfo: {},
      totalNum: null,
      status: null,
      tradeType: null,
      tradeTypeArr: [ '充值', '提现', '转账', '币币交易', '法币买入', '法币卖出', '活动奖励', '推广奖励', '分红', '投票', '人工充值', '配对', '活动兑换' ],
      columns_first: [
        {
          title: "会员ID",
          key:"memberId"
        },
        {
          title: "交易方式",
          key: "type",
          render: (h, obj) => {
            let type = obj.row.type;
            let arr = [
              "充值",
              "提现",
              "转账",
              "币币交易",
              "法币买入",
              "法币卖出",
              "活动奖励",
              "推广奖励",
              "分红",
              "投票",
              "人工充值",
              "配对",
              "活动兑换"
            ];
            return h("span", {}, arr[type]);
          }
        },
        {
          title: "交易金额",
          render: (h, obj) => {
            let amount = obj.row.amount;
            let symbol = obj.row.symbol;
            return h("span", {}, amount + " " + symbol);
          }
        },
        {
          title: "交易手续费",
          key:"fee"
        },
        {
          title: "交易时间",
          key:"createTime"
        }
      ],
      trade_data: []
    };
  },
  methods: {
    confrimPass() {
      if (!this.transactionNum) {
        this.$Message.warning("请填写流水号！");
      } else {
        let obj = {
          id: this.userInfo.id,
          transactionNumber: this.transactionNum
        };

        passCoinByOne(obj).then(res => {
          if (!res.code) {
            this.$Message.success("操作成功！");
            this.$router.push("/finance/userwithdrawals");
          } else this.$Message.error(res.message);
        });
      }
    },
    reset() {},
    searchByFilter(){
      perTradeAll({pageNo: 1, pageSize: 10, memberId: this.userInfo.memberId, type: this.tradeType}).then(res => {
        this.totalNum = (res.data && res.data.totalElements) || 1;
        this.trade_data = (res.data && res.data.content)|| [];
      });
    },
    changePage(pageIndex) {
      perTradeAll({pageNo: pageIndex, pageSize: 10, memberId: this.userInfo.memberId, type: this.tradeType}).then(res => {
        this.totalNum = (res.data && res.data.totalElements) || 1;
        this.trade_data = (res.data && res.data.content)|| [];
      });
    },
    ifPass(bol) {
      let id = this.userInfo.id;
      console.log(this.userInfo);
      let fn = null;

      if (bol) fn = auditPass;
      else fn = auditNoPass;

      fn({ ids: [id] }).then(res => {
        if (!res.code) {
          this.$Message.success(res.message);
        } else {
          this.$Message.error("请求异常!");
        }

        this.$router.push({ path: "/finance/userwithdrawals" });
      });
    },
    refreshPage() {
      this.userInfo = JSON.parse(getStore("userDetails"));
      this.status = this.userInfo.status;
      this.isAuto = this.userInfo.isAuto;
      perTradeAll({pageNo: 1,pageSize: 6,memberId: this.userInfo.memberId}).then(res => {
          if (!res.code)
          this.totalNum = (res.data && res.data.totalElements) || 1;
          this.trade_data = (res.data && res.data.content) || [];
        },
        err => {
          console.log(err);
        }
      );
    }
  },
  created() {
    this.refreshPage();
  }
};
</script>

<style scoped>
.ivu-modal-body div {
  font-size: 14px;
  font-weight: 700;
  line-height: 40px;
}
.stepWrapper {
  padding: 10px 0;
  padding-left: 10%;
}
.userDetails {
  position: relative;
  font-size: 13px;
}
.passCoinWrapper {
  position: absolute;
  top: 10px;
  right: 0;
}
.userDetails span {
  padding-left: 10px;
  display: inline-block;
  min-width: 150px;
  height: 25px;
  font-weight: 700;
}
.userDetails i {
  font-style: normal;
  font-weight: normal;
}
.tradeRecord {
  padding-left: 10px;
  margin-top: 10px;
}
.tradeRecord h3 {
  color: #2b85e4;
  margin-bottom: 10px;
}
.btnContainer {
  margin-top: 15px;
  text-align: center;
}
.btnContainer button {
  margin: 10px;
}
.tableWrapper {
  text-align: right;
}
.tableWrapper .ivu-page {
  margin-top: 20px;
  margin-right: 80px;
}
.line {
  margin-top: 10px;
  border-top: 1px solid #dddee1;
}
</style>
