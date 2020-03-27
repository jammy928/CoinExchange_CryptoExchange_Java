<template>
  <div class="nav-rights">
    <div class="nav-right col-xs-12 col-md-10 padding-right-clear">
      <div class="bill_box rightarea padding-right-clear">
        <div class="shaow">
          <div class="money_table">
            <Table :columns="tableColumnsPromotion" :data="promotionList" :loading="loading" :disabled-hover="true"></Table>
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
import html2canvas from 'html2canvas';

export default {
  components: {},
  data() {
    return {
      loginmsg: this.$t("common.logintip"),
      total: 0,
      pageSize: 10,
      loading: true,
      pageNo: 1,
      promotionList: [],
      cardNo: "",
      promotionTitle: "",
      promotionCode: ""
    };
  },
  methods: {
    getMyPromotionList() {
      let params = {};
      params.pageNo = this.pageNo;
      params.pageSize = this.pageSize;
      this.$http.post(this.host + this.api.uc.mypromotionrecord, params).then(response => {
        var resp = response.body;
        if (resp.code == 0) {
          this.promotionList = resp.data;
        } else {
          this.$Message.error(this.loginmsg);
        }
        this.loading = false;
      });
    },
    loadDataPage(data){
      this.pageNo = data;
      this.getMyPromotionList();
    }
  },
  created() {
    this.getMyPromotionList();
  },
  computed: {
    tableColumnsPromotion() {
      let self = this;
      let columns = [];
      columns.push({
        title: this.$t("uc.promotion.my_column0"),
        key: "username",
        align: "center",
        render(h, params) {
          return h(
            "span",
            {
              style:{
                  letterSpacing: "2px"
                }
            },
            params.row.username
          );
        }
      });
      columns.push({
        title: this.$t("uc.promotion.my_column1"),
        key: "createTime",
        align: "center"
      });
      columns.push({
        title: this.$t("uc.promotion.my_column2"),
        key: "realNameStatus",
        align: "center",
        render(h, params) {
          let text = "未实名";
          if(params.row.realNameStatus == 2) {
            text = "已实名";
            return h(
              "span",{style:{
                  color: "#42b983"
                }}, text
            );
          }
          return h(
            "span",{style:{
                  color: "#FF0000"
                }}, text
          );
        }
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
