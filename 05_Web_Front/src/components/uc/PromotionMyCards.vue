<template>
  <div class="nav-rights">
    <div class="nav-right col-xs-12 col-md-10 padding-right-clear">
      <div class="bill_box rightarea padding-right-clear">
        <div class="shaow">
          <div class="money_table">
            <div style="width: 100%;height: 50px;">
              <div class="header-btn" @click="exchangeCard">{{$t('uc.promotion.exchangewithcode')}}</div>
            </div>
            <Table :columns="tableColumnsCard" :data="tableCardList" :loading="loading" :disabled-hover="true"></Table>
          </div>
        </div>
      </div>
    </div>

    <Modal
        v-model="showModal"
        :title="this.$t('uc.promotion.inputcardno')"
        width="360">
        <p slot="header" style="color:#f60;text-align:left">
            <Icon type="ios-information-circle"></Icon>
            <span>{{$t('uc.promotion.inputcardno')}}</span>
        </p>
        <div style="text-align:center">
            <Input v-model="cardNo" :placeholder="this.$t('uc.promotion.inputcardno')"/>
        </div>
        <div slot="footer">
            <Button type="error" size="large" long :loading="modal_loading" @click="exchange">{{$t('uc.promotion.exchange')}}</Button>
        </div>
    </Modal>

    <Drawer :title="promotionTitle" :closable="false" v-model="showPromotionModal" width="350" style="text-align:center;">
      <div style="position:relative;width: 318px;" id="promotionImage" ref="promotionImage">
        <img style="width:100%;display:block;" src="../../assets/images/promotion/promotionbg1.jpg"></img>
        <p style="position:absolute;top: 210px;text-align:center;width: 100%;text-align:center;font-size:26px;color:#F90;font-weight:bold;">{{promotionCode}}</p>
        <p style="position:absolute;top: 250px;text-align:center;width: 100%;text-align:center;">推广合伙人专属兑换码</p>
      </div>
      <p style="text-align:center;font-size:12px;color:#888;margin-top: 10px;">{{$t('invite.imagetips')}}</p>
      <Button type="error" size="large" :loading="saveImageLoading" long style="margin-top: 20px;" @click="saveImage">{{$t('invite.saveimage')}}</Button>
    </Drawer>
  </div>
</template>
<script>
import html2canvas from 'html2canvas';

export default {
  components: {},
  data() {
    return {
      loginmsg: this.$t("common.logintip"),
      loading: true,
      tableCardList: [],
      showModal: false,
      cardNo: "",
      modal_loading: false,
      showPromotionModal: false,
      saveImageLoading: false,
      promotionTitle: "",
      promotionCode: ""
    };
  },
  methods: {
    getMyCardList() {
      this.$http.post(this.host + this.api.uc.mycardlist).then(response => {
        var resp = response.body;
        if (resp.code == 0) {
          this.tableCardList = resp.data;
        } else {
          this.$Message.error(this.loginmsg);
        }
        this.loading = false;
      });
    },
    dataURLToBlob(dataurl) {
        let arr = dataurl.split(',');
        let mime = arr[0].match(/:(.*?);/)[1];
        let bstr = atob(arr[1]);
        let n = bstr.length;
        let u8arr = new Uint8Array(n);
        while (n--) {
            u8arr[n] = bstr.charCodeAt(n);
        }
        return new Blob([u8arr], { type: mime });
    },
    saveImage(){
      this.save("promotionImage", "推广合伙人图片");
      this.saveImageLoading = true;
    },
    save(divText, imgText) {
        let canvasID = this.$refs[divText];
        let that = this;
        let a = document.createElement('a');
        html2canvas(canvasID, {useCORS:true}).then(canvas => {
            let dom = document.body.appendChild(canvas);
            dom.style.display = 'none';
            a.style.display = 'none';
            document.body.removeChild(dom);
            let blob = that.dataURLToBlob(dom.toDataURL('image/png'));
            a.setAttribute('href', URL.createObjectURL(blob));
            //这块是保存图片操作  可以设置保存的图片的信息
            a.setAttribute('download', imgText + '.png');
            document.body.appendChild(a);
            a.click();
            URL.revokeObjectURL(blob);
            document.body.removeChild(a);
            this.saveImageLoading = false;
        });
    },
    exchangeCard() {
      this.showModal = true;
    },
    exchange() {
      if(this.cardNo == "" || this.cardNo == null) {
        this.$Message.error(this.$t('uc.promotion.inputcardno'));
        return;
      }
      let param = {};
      param["cardNo"] = this.cardNo;

      this.modal_loading = true;
      this.$http.post(this.host + this.api.uc.exchangecard, param).then(response => {
        var resp = response.body;
        if (resp.code == 0) {
          this.showModal = false;
          this.$Notice.success({
              title: this.$t("uc.promotion.exchangesuccess"),
              desc:resp.message,
              duration: 30
            });
        } else {
          this.$Message.error(resp.message);
        }
        this.modal_loading = false;
      });
    }
  },
  created() {
    this.getMyCardList();
  },
  computed: {
    tableColumnsCard() {
      let self = this;
      let columns = [];
      columns.push({
        title: this.$t("uc.promotion.card_column1"),
        key: "cardName",
        width: 200,
        align: "center"
      });
      columns.push({
        title: this.$t("uc.promotion.card_column0"),
        key: "cardNo",
        width: 150,
        align: "center"
      });
      columns.push({
        title: this.$t("uc.promotion.card_column2"),
        key: "unit",
        align: "center",
        render(h, params) {
          return h(
            "span",{},params.row.coin.unit
          );
        }
      });
      columns.push({
        title: this.$t("uc.promotion.card_column3"),
        key: "amount",
        align: "center",
        render(h, params) {
          return h(
            "span",
            {
              attrs: {
                title: params.row.amount
              }
            },
            self.toFloor(params.row.amount || "0")
          );
        }
      });
      columns.push({
        title: this.$t("uc.promotion.card_column5"),
        align: "center",
        render(h, params) {
          return h(
            "span",
            {
              attrs: {
                title: params.row.count
              }
            },
            self.toFloor(params.row.count || "0")
          );
        }
      });
      columns.push({
        title: this.$t("uc.promotion.card_column6"),
        align: "center",
        render(h, params) {
          return h(
            "span",
            {
              attrs: {
                title: params.row.exchangeCount
              }
            },
            self.toFloor(params.row.exchangeCount || "0")
          );
        }
      });
      columns.push({
        title: this.$t("uc.promotion.card_column7"),
        align: "center",
        width: 170,
        render(h, params) {
          return h(
            "span",
            {
              attrs: {
                title: params.row.createTime
              }
            },
            self.toFloor(params.row.createTime || "0")
          );
        }
      });
      columns.push({
        title: this.$t("uc.finance.money.operate"),
        key: "price1",
        align: "center",
        render: function(h, params) {
          return h(
                  "Button",
                  {
                    props: {
                      type: "info",
                      size: "small"
                    },
                    on: {
                      click: function() {
                        self.showPromotionModal = true;
                        self.promotionTitle = params.row.cardName;
                        self.promotionCode = params.row.cardNo;
                        window.pageYOffset = 0;
                        document.documentElement.scrollTop = 0;
                        document.body.scrollTop = 0;
                      }
                    }
                  },
                  self.$t("uc.promotion.gopromotion")
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
