<style scoped>
.ivu-table td, .ivu-table th{
  height: 35px!important;
}
</style>
<template>
  <div class="nav-rights">
    <div class="nav-right">
      <div class="bill_flow_box">
        <div class="rightarea-con">
          <div class="form-group">
            <span>
              {{$t('uc.finance.record.start_end')}} ：
            </span>
            <DatePicker v-model="rangeDate" @on-change="changedate" format="yyyy-MM-dd" type="daterange" style="width: 200px;margin-right:30px;" @on-clear="clear"></DatePicker>
            <!--<DatePicker v-model="startDate" type="date"></DatePicker>-->
            <!--<span>-->
            <!--{{$t('uc.finance.record.to')}}-->
            <!--</span>-->
            <!--<DatePicker v-model="endDate" type="date"></DatePicker>-->
            <span>{{$t('uc.finance.record.symbol')}} ：</span>
            <Select v-model="coinType" style="width:100px;margin-right:30px;" @on-change="getAddrList" clearable :placeholder="$t('common.pleaseselect')">
              <Option v-for="item in coinList" :value="item.unit" :key="item.unit">{{ item.unit }}</Option>
            </Select>
            <span>
              {{$t('uc.finance.record.operatetype')}} ：
            </span>
            <Select v-model="recordValue" clearable style="width:200px" @on-change="getType" :placeholder="$t('common.pleaseselect')">
              <Option v-for="item in recordType" :value="item.value" :key="item.value">{{ item.label }}</Option>
            </Select>
            <Button type="warning" @click="queryOrder" style="padding: 6px 30px;margin-left:10px;background-color:#f0a70a;border-color:#f0a70a">{{$t('uc.finance.record.search')}}</Button>
          </div>
          <div class="order-table">
            <Table :no-data-text="$t('common.nodata')" :columns="tableColumnsRecord" :data="tableRecord" :disabled-hover="true" :loading="loading"></Table>
            <div style="margin: 10px;overflow: hidden">
              <div style="float: right;">
                <Page :total="total" :pageSize="pageSize" show-total :current="page" @on-change="changePage" id="record_pages"></Page>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  components: {},
  data() {
    return {
      loading: false,
      ordKeyword: "",
      rangeDate: "",
      startTime: "",
      endTime: "",
      recordValue: "",
      recordType: [
        {
          value: 0,
          label: this.$t("uc.finance.record.charge")
        },
        {
          value: 1,
          label: this.$t("uc.finance.record.pickup")
        },
        {
          value: 2,
          label: this.$t("uc.finance.record.transaccount")
        },
        {
          value: 3,
          label: this.$t("uc.finance.record.exchange")
        },
        {
          value: 4,
          label: this.$t("uc.finance.record.otcbuy")
        },
        {
          value: 5,
          label: this.$t("uc.finance.record.otcsell")
        },
        {
          value: 6,
          label: this.$t("uc.finance.record.activityaward")
        },
        {
          value: 7,
          label: this.$t("uc.finance.record.promotionaward")
        },
        {
          value: 8,
          label: this.$t("uc.finance.record.dividend")
        },
        {
          value: 9,
          label: this.$t("uc.finance.record.vote")
        },
        {
          value: 10,
          label: this.$t("uc.finance.record.handrecharge")
        },
        {
          value: 11,
          label: this.$t("uc.finance.record.match")
        },
        {
          value: 12,
          label: this.$t("uc.finance.record.activitybuy")
        },
        {
          value: 13,
          label: this.$t("uc.finance.record.ctcbuy")
        },
        {
          value: 14,
          label: this.$t("uc.finance.record.ctcsell")
        },
        {
          value: 15,
          label: this.$t("uc.finance.record.redout")
        },
        {
          value: 16,
          label: this.$t("uc.finance.record.redin")
        }
      ],
      coinList: [],
      coinType: "",
      pageSize: 10,
      page: 1,
      total: 0,
      tableRecord: []
    };
  },
  created: function() {
    this.getList(this.page);
    this.getAddrList();
  },
  methods: {
    changedate() {
      if (this.rangeDate[0]) {
        this.startTime = this.dateform(this.rangeDate[0]);
        this.endTime = this.dateform(this.rangeDate[1]);
      }
    },
    changePage(pageindex) {
      this.page=pageindex;
      this.getList(this.page);
    },
    queryOrder() {
      if (this.rangeDate.length == 0) {
        this.$Message.error("请选择搜索日期范围");
        return;
      } else {
        try {
          this.page=1;
          this.getList(this.page);
        } catch (ex) {
          this.$Message.error("请选择搜索日期范围");
          return;
        }
      }
    },
    getAddrList() {
      //获取地址
      this.$http.post(this.host + "/uc/withdraw/support/coin/info").then(response => {
          var resp = response.body;
          if (resp.code == 0 && resp.data.length > 0) {
            this.coinList = resp.data;
            if (this.coinType) {
              this.coinType = this.coinType;
            }
          } else {
            this.$Message.error(resp.message);
          }
        });
    },
    getType() {
      // console.log(this.recordValue);
    },
    dateform(time) {
      var date = new Date(time);
      var y = date.getFullYear();
      var m = date.getMonth() + 1;
      m = m < 10 ? "0" + m : m;
      var d = date.getDate();
      d = d < 10 ? "0" + d : d;
      var h = date.getHours();
      h = h < 10 ? "0" + h : h;
      var minute = date.getMinutes();
      var second = date.getSeconds();
      minute = minute < 10 ? "0" + minute : minute;
      second = second < 10 ? "0" + second : second;
      return y + "-" + m + "-" + d + " " + h + ":" + minute + ":" + second;
    },
    getList(pageNo) {
      //获取tableWithdraw
      let memberId = 0;
      !this.$store.getters.isLogin && this.$router.push("/login");
      this.$store.getters.isLogin && (memberId = this.$store.getters.member.id);
      let startTime = "";
      let endTime = "";
      let symbol = "";
      let type = "";
      this.startTime && (startTime = this.startTime);
      this.endTime && (endTime = this.endTime);
      this.coinType && (symbol = this.coinType);
      if(this.recordValue == 0 || this.recordValue){
        type = this.recordValue;
      }
      // this.recordValue!="" || this.recordValue!=undefined && (type = this.recordValue);
      this.loading = true;
      let params = {
        pageNo: pageNo,
        pageSize: this.pageSize,
        startTime,
        endTime,
        memberId,
        symbol,
        type
      };
      this.$http.post(this.host + "/uc/asset/transaction/all", params).then(response => {
          var resp = response.body;
          if (resp.code == 0) {
            this.loading = false;
            if (resp.data) {
              let trueData = resp.data;
              this.total = trueData.totalElements;
              this.tableRecord = trueData.content;
            }
          } else {
            this.$Message.error(resp.message);
          }
          this.loading = false;
        });
    },
    clear() {
      this.startTime = "";
      this.endTime = "";
    }
  },
  computed: {
    tableColumnsRecord() {
      let columns = [];
      var that = this;
      columns.push({
        title: this.$t("uc.finance.record.chargetime"),
        align: "center",
        width: 160,
        key:"createTime"
      });
      columns.push({
        title: this.$t("uc.finance.record.type"),
        render: function(h, params) {
          let str = "";
          let type = params.row.type;
          if (type == 0) {
            str = that.$t("uc.finance.record.charge");
          } else if (type == 1) {
            str = that.$t("uc.finance.record.pickup");
          } else if (type == 2) {
            str = that.$t("uc.finance.record.transaccount");
          } else if (type == 3) {
            str = that.$t("uc.finance.record.exchange");
          } else if (type == 4) {
            str = that.$t("uc.finance.record.otcbuy");
          } else if (type == 5) {
            str = that.$t("uc.finance.record.otcsell");
          } else if (type == 6) {
            str = that.$t("uc.finance.record.activityaward");
          } else if (type == 7) {
            str = that.$t("uc.finance.record.promotionaward");
          } else if (type == 8) {
            str = that.$t("uc.finance.record.dividend");
          } else if (type == 9) {
            str = that.$t("uc.finance.record.vote");
          } else if (type == 10) {
            str = that.$t("uc.finance.record.handrecharge");
          } else if (type == 11) {
            str = that.$t("uc.finance.record.match");
          } else if (type == 12) {
            str = that.$t("uc.finance.record.activitybuy");
          } else if (type == 13) {
            str = that.$t("uc.finance.record.ctcbuy");
          } else if (type == 14) {
            str = that.$t("uc.finance.record.ctcsell");
          } else if (type == 15) {
            str = that.$t("uc.finance.record.redout");
          } else if (type == 16) {
            str = that.$t("uc.finance.record.redin");
          }else {
            str = "充值";
          }
          return h("div", str, "");
        }
      });
      columns.push({
        title: this.$t("uc.finance.record.symbol"),
        align: "center",
        key:"symbol"
        // render: (h, param) => {
        //   return h("div", {}, param.row._source.symbol);
        // }
      });
      columns.push({
        // title: this.$t("uc.finance.record.num"),
        title: this.$t("uc.finance.record.num"), //到账数量
        align: "center",
        render(h, params) {
          return h(
            "span",
            {
              attrs: {
                title: params.row.amount
              }
            },
            that.toFloor(params.row.amount || "0")
          );
        }
      });
      columns.push({
        title: this.$t("uc.finance.record.shouldfee"), //"应付手续费"
        align: "center",
        render(h, params) {
          return h(
            "span",
            {
              attrs: {
                title: params.row.fee
              }
            },
            that.toFloor(params.row.fee || "0")
          );
        }
      });
      columns.push({
        title: this.$t("uc.finance.record.discountfee"), //"抵扣手续费"
        align: "center",
        render(h, params) {
          return h(
            "span",
            {
              attrs: {
                title: params.row.discountFee
              }
            },
            that.toFloor(params.row.discountFee || "0")
          );
        }
      });
      columns.push({
        title: this.$t("uc.finance.record.realfee"), //"实际手续费"
        align: "center",
        render(h, params) {
          return h(
            "span",
            {
              attrs: {
                title: params.row.realFee
              }
            },
            that.toFloor(params.row.realFee || "0")
          );
        }
      });
      columns.push({
        title: this.$t("uc.finance.record.status"),
        // key: "status",
        align: "center",
        render: (h, params) => {
          return h("div", that.$t("uc.finance.record.finish"), "");
        }
      });
      return columns;
    }
  }
};
</script>
<style scoped lang="scss">
.nav-rights {
  .nav-right {
    height: auto;
    overflow: hidden;
    padding: 0 15px;
    .bill_flow_box .rightarea-con {
      .form-group {
        margin-bottom: 20px;
        text-align: left;
      }
    }
  }
}
</style>
