<template>
    <div class="nav-rights">
        <div class="nav-right col-xs-12 col-md-10 padding-right-clear">
            <div class="bill_box rightarea padding-right-clear record">
                <div class="col-xs-12 rightarea-con">
                    <div class="trade_accumulative">
                        <div class="trade_accumulative_return">{{$t('uc.finance.paydividende.money_holding')}}&nbsp;&nbsp;{{new Number(accumulative_return).toFixed(8)}}</div>
                        <!-- <div class="trade_accumulat_return">{{$t('uc.finance.paydividende.money_hold')}}&nbsp;&nbsp;{{accumulat_return}}</div> -->
                    </div>
                    <!-- 搜素时间 隐藏-->
                    <div class="form-group" style="display:none;">
                        <span>
                            {{$t('uc.finance.paydividende.start_end')}} ：
                        </span>
                        <DatePicker v-model="rangeDate" type="daterange" style="width: 200px;"></DatePicker>
                        <!--<DatePicker v-model="startDate" type="date"></DatePicker>
                        <span>
                        {{$t('uc.finance.record.to')}}
                        </span>
                        <DatePicker v-model="endDate" type="date"></DatePicker>
                        <span>
                        {{$t('uc.finance.record.operatetype')}} ：
                        </span>
                        <Select v-model="recordValue" clearable style="width:200px">
                        <Option v-for="item in recordType" :value="item.value" :key="item.value">{{ item.label }}</Option>
                        </Select>-->
                        <Button type="primary" @click="queryOrder" style="padding: 6px 50px;margin-left:10px;background-color:#f0a70a;border-color:#f0a70a">{{$t('uc.finance.paydividende.search')}}</Button>
                    </div>
                    <!-- 隐藏结尾 -->
                    <div class="datedaitl" style="display:none;">
                        <span style="color: #eb6f6c">{{$t('uc.finance.paydividende.start_end')}} ：</span>&nbsp;&nbsp;
                        <span>{{$t('uc.finance.paydividende.datehodld')}}</span>
                    </div>
                    <div class="order-table">
                        <Table :no-data-text="$t('common.nodata')" :columns="tableColumnsRecord" :data="tableRecord" :loading="loading"></Table>
                        <div style="margin: 10px;overflow: hidden">
                            <div style="float: right;">
                                <Page :total="total" :page-size="pageSize" show-total :current="page+1" @on-change="changePage" id="record_pages"></Page>
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
      const self=this;
    return {
      data: [],
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
        }
      ],
      tableColumnsRecord: [
        {
          title: this.$t("uc.finance.paydividende.datehodld"),
          key: "haveTime",
          align: "center"
        },
        {
          title: this.$t("uc.finance.paydividende.paydividends"),
          // key: 'memBouns',
          align: "center",
          render(h, obj) {
            return h(
              "span",
              {
                attrs: {
                  title: obj.row.memBouns
                }
              },
              self.toFloor(obj.row.memBouns)
            //   new Number(obj.row.memBouns).toFixed(8)
            );
          }
        },

        {
          title: this.$t("uc.finance.paydividende.account_date"),
          key: "arriveTime",
          align: "center"
        }
      ],
      accumulative_return: "0",
      accumulat_return: "0",
      pageSize: 10,
      page: 0,
      total: 0,
      tableRecord: [],
      loading: false
    };
  },
  created: function() {
    this.init();
  },
  methods: {
    init() {
      let memberId = 0;
      !this.$store.getters.isLogin && this.$router.push("/login");
      this.$store.getters.isLogin && (memberId = this.$store.getters.member.id);
      if (memberId && memberId != 0) {
        this.memberId = memberId;
        this.getTableData();
      }
    },
    getTableData(pageNo = 1, pageSize = 10) {
      this.loading = true;
      this.$http
        .post(this.host + this.api.uc.paydividends, {
          memberId: this.memberId,
          pageNo,
          pageSize
        })
        .then(res => {
          this.loading = false;
          let resp = res.body;
          if (resp.code == 0) {
            let data = (this.data = resp.data);
            this.accumulative_return = data.amount || 0;
            this.total = parseInt(resp.totalElement);
            this.tableRecord = data.bonus;
          } else {
            this.$Message.error(resp.message);
          }
        });
    },
    changePage(pageindex) {
      this.getTableData(pageindex);
    },
    queryOrder() {
      //暂时未用，如启用请加上分页并重新测试
      let rangedate = "";

      if (this.rangeDate.length == 0) {
        this.$store.state.lang == "English" &&
          this.$Message.error("Please select a search date range");
        this.$store.state.lang != "English" &&
          this.$Message.error("请选择搜索日期范围");
        return;
      } else {
        try {
          rangedate +=
            this.rangeDate[0].getFullYear() +
            "-" +
            (this.rangeDate[0].getMonth() + 1) +
            "-" +
            this.rangeDate[0].getDate();
          rangedate += "~";
          rangedate +=
            this.rangeDate[1].getFullYear() +
            "-" +
            (this.rangeDate[1].getMonth() + 1) +
            "-" +
            this.rangeDate[1].getDate();
        } catch (ex) {
          this.$store.state.lang == "English" &&
            this.$Message.error("Please select a search date range");
          this.$store.state.lang != "English" &&
            this.$Message.error("请选择搜索日期范围");
          return;
        }
      }

      let params = {};
      params["dateRange"] = rangedate;
      params["pageNo"] = 0;
      params["pageSize"] = 10;
      if (typeof this.recordValue == "number")
        params["type"] = this.recordValue;
      this.$http
        .post(this.host + "/uc/asset/transaction/all", params)
        .then(response => {
          var resp = response.body;
          if (resp.content) {
            this.tableRecord = resp.content;
            this.total = resp.totalElements;
            this.loading = false;
            this.page = resp.number;
          } else {
            this.$Message.error(resp.message);
          }
        });
    },
    updateLangData() {
      this.tableColumnsRecord[0].title = this.$t(
        "uc.finance.paydividende.datehodld"
      );
      this.tableColumnsRecord[1].title = this.$t(
        "uc.finance.paydividende.paydividends"
      );
      this.tableColumnsRecord[2].title = this.$t(
        "uc.finance.paydividende.account_date"
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

