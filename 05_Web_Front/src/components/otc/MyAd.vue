<template>
  <div class="nav-rights">
    <div class="my_ad_box">
      <div class="add_ad">
        <Button icon="plus-round" @click="publish">{{$t('otc.myad.post')}}</Button>
      </div>
      <Alert>{{$t('otc.myad.alert')}}</Alert>
      <div class="order-table">
        <Table :columns="tableColumnsAdv" :data="tableAdv" :no-data-text="$t('common.nodata')" :loading="loading" class="tables" :disabled-hover="true"></Table>
        <div style="margin: 10px;overflow: hidden" id="pages">
          <div style="float: right;">
            <Page v-if="totalPage > 0" :pageSize="pageNumber" :total="totalPage" :current="currentPage" @on-change="changePage"></Page>
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
    let self = this;
    return {
      loginmsg: this.$t("common.logintip"),
      dataCount: 0,
      tableAdv: [],
      loading: true,
      tableColumnsAdv: [
        {
          title: self.$t("otc.myad.no"),
          key: "id",
          width: 55,
          align: "center"
        },
        {
          title: self.$t("otc.myad.type"),
          key: "advertiseType",
          width: 90,
          align: "center",
          render: (h, params) => {
            let text = "";
            if (params.row.advertiseType == 0) {
              text = self.$t("otc.myad.buy");
            } else if (params.row.advertiseType == 1) {
              text = self.$t("otc.myad.sell");
            }
            return h("div", [h("p", text)]);
          }
        },
        {
          title: self.$t("otc.myad.limit"),
          key: "limit",
          width: 100,
          align: "center",
          render: (h, params) => {
            return h("div", [
              h("p", params.row.minLimit + "~" + params.row.maxLimit)
            ]);
          }
        },
        {
          title: self.$t("otc.myad.remain"),
          key: "remainAmount",
          width: 90,
          align: "center"
        },
        {
          title: self.$t("otc.myad.coin"),
          key: "coinUnit",
          width: 100,
          align: "center"
        },
        {
          title: self.$t("otc.myad.created"),
          key: "createTime",
          width: 160,
          align: "center"
        },
        {
          title: self.$t("otc.myad.operate"),
          key: "buyBtn",
          width: 180,
          align: "center",
          render: function(h, params) {
            return h("p", [
              h(
                "a",
                {
                  on: {
                    click: function() {
                      if (params.row.status == 0) {
                        self.$Message.error(self.$t("otc.myad.errmsg"));
                      } else {
                        self.$router.push("/uc/ad/update?id=" + params.row.id);
                      }
                    }
                  }
                },
                [
                  h(
                    "Button",
                    {
                      props: {
                        size: "small"
                      },
                      style: {
                        marginRight: "8px"
                      }
                    },
                    self.$t("otc.myad.update")
                  )
                ]
              ),
              h(
                "Button",
                {
                  props: {
                    type: "primary",
                    size: "small"
                  },
                  style: {
                    marginRight: "8px"
                  },
                  on: {
                    click: () => {
                      //要上架
                      if (params.row.status == 1) {
                        let canshu = {};
                        canshu["id"] = params.row.id;
                        // canshu['status'] = params.row.status == 0 ? 1 : 0
                        self.$http
                          .post(self.host + "/otc/advertise/on/shelves", canshu)
                          .then(response => {
                            var resp = response.body;
                            if (resp.code == 0) {
                              self.$Message.success(resp.message);
                              // self.$router.go(0)
                              self.getAd();
                            } else {
                              self.$Message.error(resp.message);
                            }
                          });
                      } else if (params.row.status == 0) {
                        let canshu = {};
                        canshu["id"] = params.row.id;
                        // canshu['status'] = params.row.status == 0 ? 1 : 0
                        self.$http
                          .post(
                            self.host + "/otc/advertise/off/shelves",
                            canshu
                          )
                          .then(response => {
                            var resp = response.body;
                            if (resp.code == 0) {
                              self.$Message.success(resp.message);
                              // self.$router.go(0)
                              self.getAd();
                            } else {
                              self.$Message.error(resp.message);
                            }
                          });
                      }
                    }
                  }
                },
                params.row.status == 0
                  ? self.$t("otc.myad.dropoff")
                  : self.$t("otc.myad.shelf")
              ),
              h(
                "Button",
                {
                  props: {
                    type: "error",
                    size: "small"
                  },
                  on: {
                    click: () => {
                      let canshu = {};
                      canshu["id"] = params.row.id;

                      if (params.row.status == 1) {
                        self.$Modal.confirm({
                          title: self.$t("common.tip"),
                          content: "<p>" + self.$t("common.delete") + "</p>",
                          onOk: () => {
                            self.$http
                              .post(self.host + "/otc/advertise/delete", canshu)
                              .then(response => {
                                var resp = response.body;
                                if (resp.code == 0) {
                                  self.$Message.success(resp.message);
                                  self.remove(params.index);
                                } else {
                                  self.$Message.error(resp.message);
                                }
                              });
                          }
                        });
                      } else {
                        self.$Message.error("下架广告后才可以删除！");
                      }
                    }
                  }
                },
                self.$t("otc.myad.delete")
              )
            ]);
          }
        }
      ],
      totalPage: 0,
      pageNumber: 10,
      currentPage: 1
    };
  },
  methods: {
    updateLangData() {
      this.tableColumnsAdv[0].title = this.$t("otc.myad.no");
      this.tableColumnsAdv[1].title = this.$t("otc.myad.type");
      this.tableColumnsAdv[2].title = this.$t("otc.myad.limit");
      this.tableColumnsAdv[3].title = this.$t("otc.myad.remain");
      this.tableColumnsAdv[4].title = this.$t("otc.myad.coin");
      this.tableColumnsAdv[5].title = this.$t("otc.myad.created");
      this.tableColumnsAdv[6].title = this.$t("otc.myad.operate");
    },
    remove(index) {
      this.tableAdv.splice(index, 1);
    },
    changePage() {},
    getAd() {
      //获取个人广告
      this.$http.post(this.host + "/otc/advertise/all").then(response => {
        var resp = response.body;
        if (resp.code == 0) {
          this.tableAdv = resp.data.content;
          // console.log(this.tableAdv);
          for (var i = 0; i < this.tableAdv.length; i++) {
            this.tableAdv[i].coinUnit = this.tableAdv[i].coin.unit;
          }
          this.loading = false;
          //this.dataCount = resp.data.length
          this.totalPage = resp.data.totalElements;
        } else {
          // this.$Message.error(resp.message);
          // this.$Message.error(this.$t('common.logintip'));
          this.$Message.error(this.loginmsg);
        }
      });
    },
    publish() {
      this.$router.push(this.api.otc.createAd);
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
  },
  created() {
    this.getAd();
  }
};
</script>
<style scoped lang="scss">
.nav-rights {
  padding: 0 0 0 20px;
  .my_ad_box {
    .add_ad {
      margin-bottom: 20px;
      .ivu-btn {
        background: #f0a70a;
        color: #fff;
        &:hover {
          border-color: #f0a70a;
        }
      }
    }
    .ivu-alert.ivu-alert-info {
      border: none;
      background-color: #192330;
      text-align: center;
    }
  }
}
</style>
<style lang="scss">
.nav-rights {
  .my_ad_box {
    .order-table {
      .ivu-table {
        .ivu-table-header,
        .ivu-table-body {
          table {
            width: 100% !important;
            thead {
              .ivu-table-cell {
                padding: 0;
              }
            }
          }
        }
        .ivu-table-body {
          .ivu-table-tbody .ivu-table-row .ivu-table-cell {
            button.ivu-btn {
              border-radius: 10px;
              background: #fff;
            }
            button.ivu-btn.ivu-btn-default {
              border:1px solid #00b275;
              background-color: transparent;
              span {
                color: #00b275;
              }
            }
            button.ivu-btn.ivu-btn-primary {
              border:1px solid #f0ac19;
              background-color: transparent;
              span {
                color: #f0ac19;
              }
            }
            button.ivu-btn.ivu-btn-error {
              border:1px solid #f15057;
              background-color: transparent;
              span {
                color: #f15057;
              }
            }
          }
        }
      }
    }
  }
}
</style>
