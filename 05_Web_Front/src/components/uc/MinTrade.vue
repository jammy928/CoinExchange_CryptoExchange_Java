<template>
    <div class="nav-rights">
        <div class="nav-right col-xs-12 col-md-10 padding-right-clear">
            <div class="bill_box rightarea padding-right-clear record">
                <div class="col-xs-12 rightarea-con">
                    <div class="trade_accumulative">
                        <div class="trade_accumulative_return">{{$t('uc.finance.trade.accumulative_return')}}&nbsp;&nbsp;{{new Number(accumulative_return).toFixed(8)}}</div>
                        <div class="trade_accumulat_return">{{$t('uc.finance.trade.accumulat_return')}}&nbsp;&nbsp;{{new Number(accumulat_return).toFixed(8)}}</div>
                    </div>
                    <div class="form-group">
                        <span>
                            {{$t('uc.finance.trade.start_end')}} ：
                        </span>
                        <DatePicker v-model="rangeDate" @on-change="changedate" type="daterange" style="width: 200px;margin-right: 35px;" format="yyyy-MM-dd"></DatePicker>
                        <!--<DatePicker v-model="startDate" type="date"></DatePicker>-->
                        <!--<span>-->
                        <!--{{$t('uc.finance.record.to')}}-->
                        <!--</span>-->
                        <!--<DatePicker v-model="endDate" type="date"></DatePicker>-->
                        <!--<span>-->
                        <!--{{$t('uc.finance.record.operatetype')}} ：-->
                        <!--</span>-->
                        <!--<Select v-model="recordValue" clearable style="width:200px">-->
                        <!--<Option v-for="item in recordType" :value="item.value" :key="item.value">{{ item.label }}</Option>-->
                        <!--</Select>-->
                        <Button type="primary" @click="queryOrder" style="padding: 6px 30px;margin-left:10px;background-color:#f0a70a;border-color:#f0a70a">{{$t('uc.finance.trade.search')}}</Button>
                    </div>
                    <!-- <div class="datedaitl">
                        <span style="color: #eb6f6c">{{$t('uc.finance.trade.start_end')}} ：</span>&nbsp;&nbsp;<span>{{$t('uc.finance.trade.chargetime')}}</span>
                    </div> -->
                    <div class="order-table">
                        <Table :no-data-text="$t('common.nodata')" :columns="tableColumnsRecord" :data="tableRecord" :loading="loading"></Table>
                        <div style="margin: 10px;overflow: hidden">
                            <div style="float: right;">
                                <Page :total="total" :page-size="pageSize" show-total :current="page" @on-change="changePage" id="record_pages"></Page>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>
<script>
// import expandRow from './TradeExpand';
export default {
  components: {
    // expandRow,
  },
  data() {
    var that = this;
    return {
      loading: false,
      startTime: "",
      endTime: "",
      ordKeyword: "",
      rangeDate: "",
      startDate: "",
      endDate: "",
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
      tableColumnsRecord: [
        // {
        //     type: 'expand',
        //     width: 60,
        //     key:'data',
        //     render: (h, params) => {
        //         return h(expandRow, {
        //             props: {
        //                 row: params.row.data
        //             }
        //         })
        //     }
        // },
        {
          // 交易时间
          title: this.$t("uc.finance.trade.transactionTime"),
          // key: '_source.transaction_time',
          align: "center",
          render: (h, params) => {
            return h("div", {}, params.row._source.transaction_time);
          }
        },
        {
          // 订单ID
          title: this.$t("uc.finance.trade.exchangeOrderId"),
          align: "center",
          render: (h, params) => {
            return h("div", {}, params.row._source.exchange_order_id);
          }
        },
        {
          //交易对
          title: this.$t("uc.finance.trade.symbol"),
          align: "center",
          render: (h, params) => {
            return h("div", {}, params.row._source.symbol);
          }
        },
        {
          //交易方向
          title: this.$t("uc.finance.trade.direction"),
          // key: 'direction',
          align: "center",
          render: function(h, params) {
            let type = params.row._source.type;
            let direction = params.row._source.direction;
            let str1 = "",
              str2 = "";
            let lang = that.$store.state.lang;
            if (type == 1) {
              lang == "English" && (str1 = "market price");
              lang != "English" && (str1 = "限价");
            } else if (type == 0) {
              lang == "English" && (str1 = "Present price");
              lang != "English" && (str1 = "市价");
            }
            if (direction == 1) {
              lang == "English" && (str2 = " sell");
              lang != "English" && (str2 = "卖出");
            } else if (direction == 0) {
              lang == "English" && (str2 = " buy");
              lang != "English" && (str2 = "买入");
            }
            return h("div", str1 + str2, "");
          }
        },
        {
          //手续费
          title: this.$t("uc.finance.trade.poundageAmount"),
          // key: 'poundageAmount',
          align: "center",
          render: (h, params) => {
            let type = params.row._source.coin_id;
            let num = this.toFloor(params.row._source.poundage_amount);
            return h(
              "div",
              {
                attrs: {
                  title: params.row._source.poundage_amount
                }
              },
              num + "   " + type
            );
          }
        },
        {
          //挖币数量
          title: this.$t("uc.finance.trade.mineAmount"),
          key: "mineAmount",
          align: "center",
          // width:140,
          render: (h, params) => {
            let str = this.toFloor(params.row._source.mine_amount);
            return h(
              "span",
              {
                attrs: {
                  title: params.row._source.mine_amount
                }
              },
              str
            );
          }
        }
        // {//到账时间
        //     title: this.$t('uc.finance.trade.account_date'),
        //     key: 'transactionTime',
        //     width:160,
        //     render:function(h,params){
        //         var reg = /-/g;
        //         var a = params.row.transactionTime
        //         var b = a.split(" ")[0].replace(reg,"/")
        //         var c = a.split(" ")[1];
        //         var date = new Date(b).getTime()+86400000;
        //         date = that.dateform(date);
        //         var str = date.split(" ")[0] + " "+c;
        //         return h('div', str,'');
        //     }
        // },
      ],
      accumulative_return: 0,
      accumulat_return: 0,
      pageSize: 10,
      page: 1,
      total: 0,
      tableRecord: []
    };
  },
  created: function() {
    this.init();
  },
  methods: {
    changedate() {
      this.startTime = this.dateform(this.rangeDate[0]);
      this.endTime = this.dateform(this.rangeDate[1]);
    },
    init() {
      let memberId = 0;
      !this.$store.getters.isLogin && this.$router.push("/login");
      this.$store.getters.isLogin && (memberId = this.$store.getters.member.id);
      let startTime = "";
      let endTime = "";
      let url = this.api.uc.mylist;
      this.startTime && (startTime = this.startTime);
      this.endTime && (endTime = this.endTime);
      let params = {
        memberId: memberId,
        page: this.page,
        limit: 10,
        // inviterState:0,
        startTime,
        endTime
      };
      this.loading = true;
      this.$http.post(this.host + url, params).then(res => {
        if (res.body.code == 0) {
          let data = res.body.data;
          this.accumulative_return = data.backAmount;
          this.accumulat_return = data.preAmount;
          if (data.data) {
            let truedata = data.data.hits;
            this.total = truedata.total;
            this.tableRecord = truedata.hits;
          }
        } else {
          this.$Message.error(res.body.message);
        }
        this.loading = false;
      });
    },
    changePage(pageindex) {
      this.page=pageindex;
      this.init();
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
    queryOrder() {
      let rangedate = "";
      if (this.rangeDate.length == 0) {
        this.$Message.error("请选择搜索日期范围");
        return;
      } else {
        try {
          this.page=1;
          this.init();
        } catch (ex) {
          this.$Message.error("请选择搜索日期范围");
          return;
        }
      }
    },
    updateLangData() {
      this.tableColumnsRecord[0].title = this.$t(
        "uc.finance.trade.transactionTime"
      );
      this.tableColumnsRecord[1].title = this.$t(
        "uc.finance.trade.exchangeOrderId"
      );
      this.tableColumnsRecord[2].title = this.$t("uc.finance.trade.symbol");
      this.tableColumnsRecord[3].title = this.$t("uc.finance.trade.direction");
      this.tableColumnsRecord[4].title = this.$t(
        "uc.finance.trade.poundageAmount"
      );
      this.tableColumnsRecord[5].title = this.$t("uc.finance.trade.mineAmount");
      this.tableColumnsRecord[6].title = this.$t(
        "uc.finance.trade.account_date"
      );
    }
  },
  computed: {
    lang: function() {
      return this.$store.state.lang;
    }
  },
  watch: {
    lang: function() {
      this.updateLangData();
    }
  }
};
</script>
<style scoped>
.datedaitl {
  text-align: left;
  margin-bottom: 10px;
}
.bill_box {
  width: 100%;
  height: auto;
  overflow: hidden;
}
.form-group {
  margin-bottom: 20px;
  text-align: left;
}
.rightarea {
  background: #fff;
  padding-left: 15px !important;
  padding-right: 15px !important;
  margin-bottom: 60px !important;
}

.rightarea .rightarea-top {
  line-height: 75px;
  border-bottom: #f1f1f1 solid 1px;
}

.rightarea .rightarea-con {
  padding-top: 30px;
  padding-bottom: 20px;
}

.rightarea .trade-process {
  line-height: 30px;
  padding: 0 15px;
  background: #f1f1f1;
  display: inline-block;
  position: relative;
  margin-right: 20px;
}

.rightarea .trade-process.active {
  color: #eb6f6c;
  background: #f9f5eb;
}

.rightarea .trade-process .icon {
  background: #fff;
  border-radius: 20px;
  height: 20px;
  width: 20px;
  display: inline-block;
  line-height: 20px;
  text-align: center;
  margin-right: 10px;
}

.rightarea .trade-process .arrow {
  position: absolute;
  top: 10px;
  right: -5px;
  width: 0;
  height: 0;
  border-top: 5px solid transparent;
  border-bottom: 5px solid transparent;
  border-left: 5px solid #f1f1f1;
}

.rightarea .trade-process.active .arrow {
  border-left: 5px solid #f9f5eb;
}

.rightarea .rightarea-tabs {
  border: none;
}

.rightarea .rightarea-tabs li > a {
  width: 100%;
  height: 100%;
  padding: 0;
  margin-right: 0;
  font-size: 14px;
  color: #646464;
  border-radius: 0;
  border: none;
  display: flex;
  justify-content: center;
  align-items: center;
}

.rightarea .rightarea-tabs li > a:hover {
  background-color: #fcfbfb;
}

.rightarea .rightarea-tabs li {
  width: 125px;
  height: 40px;
  position: relative;
  margin: -1px 0 0 -1px;
  border: 1px solid #f1f1f1;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.rightarea .rightarea-tabs li.active {
  background-color: #fcfbfb;
}

.rightarea .rightarea-tabs li:last-child {
  border-right: 1px solid #f1f1f1;
}

.rightarea .rightarea-tabs li.active > a,
.rightarea .rightarea-tabs li:hover > a {
  color: #da2e22;
  border: none;
}

.rightarea .panel-tips {
  border: 3px solid #fdfaf3;
  color: #9e9e9e;
  font-size: 12px;
}

.rightarea .panel-tips .panel-header {
  background: #fdfaf3;
  line-height: 40px;
  margin-bottom: 15px;
}

.rightarea .panel-tips .panel-title {
  font-size: 16px;
}

.rightarea .recordtitle {
  cursor: pointer;
}

.nav-right {
  /* width: 1000px; */
  height: auto;
  overflow: hidden;
  padding: 0 15px;
}

.order_box {
  width: 100%;
  background: #fff;
  height: 56px;
  line-height: 56px;
  margin-bottom: 20px;
  border-bottom: 2px solid #ccf2ff;
  position: relative;
  text-align: left;
}

.order_box a {
  color: #8994a3;
  font-size: 16px;
  padding: 0 30px;
  cursor: pointer;
  text-decoration: none;
  text-align: center;
  line-height: 54px;
  display: inline-block;
}

.order_box .active {
  border-bottom: 2px solid #f0a70a;
}

.order_box .search {
  position: absolute;
  width: 300px;
  height: 32px;
  top: 12px;
  right: 0;
  display: flex;
  /* border: #c5cdd7 solid 1px; */
}

.order-table .ivu-table-body .ivu-table-cell {
  padding-right: 0 !important;
}
.trade_accumulative {
  height: auto;
  overflow: hidden;
  font-size: 18px;
  font-weight: 600;
  text-align: left;
  border-bottom: 1px solid #f5f5f5;
  padding-bottom: 20px;
  margin-bottom: 20px;
}
.trade_accumulative .trade_accumulative_return {
  width: 50%;
  float: left;
}
.trade_accumulative .trade_accumulat_return {
  width: 50%;
  float: left;
}
</style>
<style lang="scss">
th .ivu-table-cell {
  overflow: hidden;
  white-space: nowrap;
}
</style>
