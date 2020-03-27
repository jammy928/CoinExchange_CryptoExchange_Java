<template>
  <div>
      <Row>
          <Card>
                <p slot="title">
                活动管理
                    <Button type="primary" size="small" @click="refreshPageManual">
                        <Icon type="refresh"></Icon>刷新
                    </Button>
                </p>

                <Row class="functionWrapper">
                    <div class="btnsWrapper clearfix">
                        <Button type="primary" @click="newActivity">新建活动</Button>
                    </div>
                </Row>
                <Row>
                    <Table
                        :columns="columns"
                        :data="list"
                        border
                        :loading="ifLoading">
                    </Table>
                </Row>
                <div class="pageWrapper">
                    <Page
                        :total="pageNum"
                        :current="currentPageIdx"
                        @on-change="changePage"
                        show-elevator>
                    </Page>
                </div>
                <div style="font-size:12px;">
                  <p>注意：此页面新建活动仅仅是为了将所有活动聚合以供展示，实际活动的业务与此处无关</p>
                  <p>活动发起先行条件：</p>
                  <p>   【抢购】与【分摊】需要在【币币设置】中新建交易对时对活动进行设置</p>
                  <p>   【持仓瓜分】与【认购】需要在【活动管理】中新建新的活动</p>
                  <br>
                  <p>1、抢购：所有用户【抢购】指定数量的币，首次上线交易对可指定该类别，需要在【币币设置】中添加新交易对时确定活动内容</p>
                  <p>2、分摊：所有用户【平分】指定数量的币，首次上线交易对可指定该类别，需要在【币币设置】中添加新交易对时确定活动内容</p>
                  <p>3、持仓瓜分：任意时段可发起的活动，活动前，快照用户持仓量，根据用户持仓量瓜分指定数量的币</p>
                  <p>4、认购：一般做首发类型的活动，属于IEO认购类型，所有用户抢购指定数量的币</p>
                </div>
          </Card>

      </Row>
      <Modal
            class="auditModel"
            v-model="showProgressModel"
            title="请输入进度"
            width="550"
            @on-cancle="progress = ''"
            @on-ok="submitProgress">
            <Input v-model="progress" type="text" placeholder="请输入进度数值"></Input>
     </Modal>
     <Modal
            class="auditModel"
            v-model="showDetailModel"
            title="参与详情"
            width="750"
            @on-cancle=""
            @on-ok="">
            <Row>
                <Table
                    :columns="columnsDetail"
                    :data="orderList"
                    border
                    :loading="ifLoading">
                </Table>
            </Row>
     </Modal>
  </div>
</template>

<script>

  import { activityList, addActivity, modifyActivity, modifyActivityProgress,activityOrderList, distributeOrder, BASICURL } from '@/service/getData';
  import { getStore, removeStore, setStore } from '@/config/storage';

  export default{
    data () {
      return {
        loginPassModal: false,
        showProgressModel: false,
        showDetailModel: false,
        progress: "",
        loginPW: '',
        activityForm:{
            title: "",
            detail: "",
            activityLink: "",
            noticeLink: "",
            settings: "",
            step: "0",
            type: "1",
            status: "0",
            imageUrl: ""
        },
        id: null,
        type: 0,
        ifShowPercentCircle: false,
        percentage: 0,
        picUrl: "",
        picUrlIcon: false,
        basicUrl: BASICURL,
        filterSearch: {
            pageNo: 1,
            pageSize: 10
        },
        currentPageIdx: 1,
        ifLoading: true,
        pageNum: null,
        orderList: [],
        columnsDetail: [
          {
            title: '用户ID',
            key:"memberId",
            width: 120
          },
          {
            title: '认购数量',
            key:"amount"
          },
          {
            title: '持仓量',
            key:"freezeAmount"
          },
          {
            title: '成交额/冻结资产',
            key:"turnover",
            width: 140
          },
          {
            title: '状态',
            render: (h ,obj) => {
              let state =  obj.row.state;
              let txt = "临时";
              let color = "#000";
              if(state == 1) {txt = "未成交";color = "#FF0000";}
              if(state == 2) {txt = "已成交";color = "#19be6b";}
              if(state == 3) {txt = "已撤销";color = "#EEE";}

              return h('span',{
                style:{
                  color: color
                }
              },txt)
            }
          },
          {
              title: "操作",
              key: "xx",
              fixed: 'right',
              width: 150,
              render: (h, obj) => {
                let showProgress = "("+obj.row.progress+")";
                let disabled = true;
                if(obj.row.state == 1){
                  disabled = false;
                }
                let btnText = "派发活动币";
                if(this.type == 5) {
                  btnText = "部署矿机";
                }
                return h("div", [
                  h(
                    "Button",
                    {
                      props: {type: "success",size: "small",disabled: disabled},
                      style: {marginRight: "5px"},
                      on: {
                        click: () => {
                          let param = {};
                          param["oid"] = obj.row.id;
                          console.log(param);
                          distributeOrder(param).then( res => {
                            if(!res.code) {
                                this.$Message.success(res.message);
                                this.getDetailOrderList();
                            }else{
                                this.$Message.error(res.message);
                            }
                          });
                        }
                      }
                    },
                    btnText
                  )
                ]);
              }
           }
        ],
        list: [],
        columns: [
          {
            title: '标题',
            key:"title",
            width: 300
          },
          {
            title: '小图',
            width:70,
            render:(h, obj) => {
              let smallImageUrl = obj.row.smallImageUrl;
              return  h('img',{
                attrs: {src: smallImageUrl},
                style: {height: "45px", padding:"5px 0px"}
              },"");
            }
          },
          {
            title: '显示',
            width: 60,
            render: (h ,obj) => {
              let status =  obj.row.status;
              let txt = status == 1 ? "显示":"隐藏";
              let color = status == 1 ? "#19be6b":"#FF0000";
              return h('span',{
                style:{
                  color: color
                }
              },txt)
            }
          },
          {
            title: '状态',
            width: 75,
            render: (h ,obj) => {
              let step =  obj.row.step;
              let txt = "筹备中";
              let color = "#2db7f5";
              if(step == 1) {txt = "进行中";color="#19be6b";}
              if(step == 2) {txt = "派发中";color="#F90";}
              if(step == 3) {txt = "已结束";color="#AFAFAF";}

              return h('span',{
                style:{
                  color: color
                }
              },txt)
            }
          },
          {
            title: '一级邀请',
            key:"leveloneCount",
            width: 60
          },
          {
            title: '类型',
            width: 115,
            render: (h ,obj) => {
              let type =  obj.row.type;
              let txt = "未知活动";
              if(type == 1) txt = "首次上线(抢购)";
              if(type == 2) txt = "首次上线(平分)";
              if(type == 3) txt = "持仓瓜分";
              if(type == 4) txt = "自由认购";
              if(type == 5) txt = "矿机认购";
              return h('span',{
              },txt)
            }
          },
          {
            title: '量/价',
            width: 160,
            render: (h ,obj) => {
              return h('span',{
              },obj.row.totalSupply + " / " + obj.row.price + obj.row.acceptUnit)
            }
          },
          {
            title: '开始时间',
            key:"startTime",
            width: 140,
            render: (h ,obj) => {
              let sTime = obj.row.startTime.substr(0, 10) + " " + obj.row.startTime.substr(11, 8);
              let curTime = new Date().Format("yyyy-MM-dd HH:mm:ss");
              let text = "即将开始";
              if(sTime < curTime) {
                text = "已开始";
              }
              return h('span',{
              },sTime + text);
            }
          },
          {
            title: '结束时间',
            key:"endTime",
            width: 140,
            render: (h ,obj) => {
              let eTime = obj.row.endTime.substr(0, 10) + " " + obj.row.endTime.substr(11, 8);
              let curTime = new Date().Format("yyyy-MM-dd HH:mm:ss");
              let color = "#19be6b";
              let text = "未结束";
              if(eTime < curTime) {
                text = "已结束";
                color = "#000";
              }
              return h('span',{
                style:{
                  color: color
                }
              },eTime + text);
            }
          },
          {
            title: '公告链接',
            width: 90,
            render: (h ,obj) => {

              return h('a',{
                attrs: {
                    href: obj.row.noticeLink,
                    target: "_blank"
                }
              },"查看公告");
            }
          },
          {
            title: '活动链接',
            width: 90,
            render: (h ,obj) => {

              return h('a',{
                attrs: {
                    href: obj.row.activityLink,
                    target: "_blank"
                }
              },"活动页面");
            }
          },
          {
            title: '创建时间',
            key:"createTime",
            width: 140
          },
          {
              title: "操作",
              key: "xx",
              fixed: 'right',
              width: 220,
              render: (h, obj) => {
                let showProgress = "("+obj.row.progress+")";
                let disabled = true;
                if(obj.row.type == 3 || obj.row.type == 4 || obj.row.type == 5){
                  disabled = false;
                }
                return h("div", [
                  h(
                    "Button",
                    {
                      props: {type: "info",size: "small"},
                      style: {marginRight: "5px"},
                      on: {
                        click: () => {
                          removeStore('manageID');
                          setStore('manageID',  obj.row.id);
                          this.$router.push('/activity/activity/addActivity');
                        }
                      }
                    },
                    "修改"
                  ),
                  h(
                    "Button",
                    {
                      props: {type: "error",size: "small"},
                      style: {marginRight: "5px"},
                      on: {
                        click: () => {
                          this.showProgressModel = true;
                          this.id = obj.row.id;
                          this.progress = obj.row.progress;
                        }
                      }
                    },
                    "进度"+showProgress
                  ),
                  h(
                    "Button",
                    {
                      props: {type: "success",size: "small",disabled: disabled},
                      style: {marginRight: "5px"},
                      on: {
                        click: () => {
                          this.showDetailModel = true;
                          this.id = obj.row.id;
                          this.type = obj.row.type;
                          this.getDetailOrderList();
                        }
                      }
                    },
                    "参与详情"
                  )
                ]);
              }
           }
        ]
      }
    },
    methods: {
      getDetailOrderList(){
        activityOrderList(this.id)
        .then(res => {
          this.orderList = res.data;
        });
      },
      submitProgress(){
        let param = {};
        param["id"] = this.id;
        param["progress"] = this.progress;
        modifyActivityProgress(param).then( res => {
          if(!res.code) {
                this.showProgressModel = false;
                this.$Message.success("修改成功");
                this.refreshPage(1);
          }else{
                this.$Message.error(res.message)
          }
        })
      },
      newActivity(){
        removeStore('manageID');
        this.$router.push('/activity/activity/addActivity');
      },
      refreshPageManual() {
                this.currentPageIdx = 1;
                for(let key in this.filterSearch) {
                    this.filterSearch[key] = '';
                }
                this.filterSearch.pageNo = 1;
                this.filterSearch.pageSize = 10;
                this.refreshPage(this.filterSearch);
      },
      changePage(pageIndex) {
                this.currentPageIdx = pageIndex;
                this.filterSearch.pageNo = pageIndex;
                this.refreshPage(this.filterSearch)
      },
      refreshPage(obj) {
        this.ifLoading = true;
        activityList(this.filterSearch).then( res => {
          if(!res.code) {
                this.ifLoading = false;
                this.pageNum = (res.data && res.data.totalElements) || 1;
                this.list = (res.data && res.data.content) || [];
          }else{
                this.$Message.error(res.message)
          }
        })
      }
    },
    created () {
      this.refreshPage();
    }
  }
</script>

<style scoped>
  .ivu-upload{
    display: inline-block;
  }
</style>
