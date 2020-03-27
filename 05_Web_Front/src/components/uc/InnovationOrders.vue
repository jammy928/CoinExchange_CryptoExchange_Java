<template>
  <div class="nav-rights">
    <div class="nav-right col-xs-12 col-md-10 padding-right-clear">
      <div class="bill_box rightarea padding-right-clear">
        <div class="shaow">
          <div class="money_table">
            <Table :columns="tableColumns" :data="orderList" :loading="loading" :disabled-hover="true"></Table>
            <div class="page">
              <Page :total="total" :pageSize="pageSize" :current="pageNo" @on-change="loadDataPage"></Page>
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
      loginmsg: this.$t("common.logintip"),
      total: 0,
      pageSize: 10,
      loading: true,
      pageNo: 1,
      orderList: []
    };
  },
  methods: {
    getMyOrderList() {
      let params = {};
      params.pageNo = this.pageNo;
      params.pageSize = this.pageSize;
      this.$http.post(this.host + this.api.uc.myInnovationOrderList, params).then(response => {
        var resp = response.body;
        if (resp.code == 0) {
          this.orderList = resp.data.content;
        } else {
          this.$Message.error(this.loginmsg);
        }
        this.loading = false;
      });
    },
    loadDataPage(data){
      this.pageNo = data;
      this.getMyOrderList();
    }
  },
  created() {
    this.getMyOrderList();
  },
  computed: {
    tableColumns() {
      let self = this;
      let columns = [];
      columns.push({
        title: this.$t("uc.activity.column1"),
        key: "activityName",
        align: "center",
        width: 300,
        render: (h ,params) => {
          return h('a',{
            attrs: {
                href: this.rootHost + "/lab/detail/" + params.row.activityId,
                target: "_blank"
            }
          }, params.row.activityName);
        }
      });
      columns.push({
        title: this.$t("uc.activity.column2"),
        key: "type",
        align: "center",
        render(h, params) {
          let text = "未知";
          if(params.row.type == 1){
            text = "首发抢购";
          }
          if(params.row.type == 2){
            text = "首发分摊";
          }
          if(params.row.type == 3){
            text = "持仓瓜分";
          }
          if(params.row.type == 4){
            text = "自由认购";
          }
          if(params.row.type == 5){
            text = "云矿机认购";
          }
          return h(
            "span",{}, text
          );
        }
      });
      columns.push({
        title: this.$t("uc.activity.column3"),
        key: "amount",
        align: "center"
      });
      columns.push({
        title: this.$t("uc.activity.column4"),
        key: "baseSymbol",
        align: "center"
      });
      columns.push({
        title: this.$t("uc.activity.column5"),
        key: "coinSymbol",
        align: "center"
      });
      columns.push({
        title: this.$t("uc.activity.column6"),
        key: "state",
        align: "center",
        render(h, params) {
          let text = "临时";
          if(params.row.type == 5) {
            if(params.row.state == 1) {
              text = "未部署";
            }
            if(params.row.state == 2) {
              text = "已部署";
            }
            if(params.row.state == 3) {
              text = "已撤销";
            }
          }else{
            if(params.row.state == 1) {
              text = "待成交";
            }
            if(params.row.state == 2) {
              text = "已成交";
            }
            if(params.row.state == 3) {
              text = "已撤销";
            }
          }
          return h(
            "span",{}, text
          );
        }
      });

      columns.push({
        title: this.$t("uc.activity.column7"),
        key: "turnover",
        align: "center",
        render(h, params) {
          let text = params.row.turnover + " " + params.row.baseSymbol;
          return h(
            "span",{}, text
          );
        }
      });
      columns.push({
        title: this.$t("uc.activity.column8"),
        key: "createTime",
        align: "center",
        width: 140
      });
      return columns;
    }
  }
};
</script>

<style lang="scss">
.nav-right {
  .rightarea.bill_box {
    .shaow {
      padding: 5px;
    }
    .money_table {
      .search{
        width: 200px;
        margin-bottom: 10px;
      }
      .ivu-table-wrapper {
        .ivu-table-header{
          background: #27313e;
          th{
            color: #fff;
          }
        }
        .ivu-table-body {
          td {
            color: #fff;
            .ivu-table-cell {
              padding: 10px 10px;
              .ivu-btn {
                background: transparent;
                height: 25px;
                padding: 0 0px;
                border-radius: 0;
                span {
                  display: inline-block;
                  line-height: 20px;
                  font-size: 12px;
                  padding: 0 15px;
                  letter-spacing: 1px;
                }
              }
              .ivu-btn.ivu-btn-info {
                border: 1px solid #f0ac19;
                span {
                  color: #f0ac19;
                }
              }
              .ivu-btn.ivu-btn-error {
                border: 1px solid #f15057;
                span {
                  color: #f15057;
                }
              }
              .ivu-btn.ivu-btn-primary {
                border: 1px solid #00b275;
                border: 1px solid #00b275;
                span {
                  color: #00b275;
                }
              }
              .ivu-btn.ivu-btn-default {
                border: 1px solid #2c384f;
                background: #222c3e;
                span {
                  color: #54637a;
                }
              }
            }
          }
        }
      }
    }
  }
}
</style>

<style scoped lang="scss">
.nav-right {
  height: auto;
  overflow: hidden;
  padding: 0 0 0 15px;
  .rightarea.bill_box {
    padding-left: 15px;
    width: 100%;
    height: auto;
    overflow: hidden;
  }
}

.demo-spin-icon-load{
  animation: ani-demo-spin 1s linear infinite;
}

.header-btn{
  float:right;padding: 5px 15px;border: 1px solid #f0ac19;color: #f0ac19;
  margin-left: 20px;
  &:hover{
    background: #f0ac19;
    color: #000;
    cursor: pointer;
  }
}
</style>
