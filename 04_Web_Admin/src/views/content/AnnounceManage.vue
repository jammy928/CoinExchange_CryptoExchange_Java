<template>
  <div>
    <Card>
      <p slot="title">
        公告管理
        <Button type="primary" size="small" @click="refreshPageManual">
          <Icon type="refresh"></Icon>
          刷新
        </Button>
      </p>

      <Row class="manageRow clearfix">
        <div class="manageWrapper">
          <Button type="error" @click="delOneAnnounce">删除</Button>
          <Button type="info" @click="addOneAnnounce">添加</Button>
        </div>
      </Row>

      <Row>
        <Table border
              :columns="columns_first"
              :data="announcementArr"
              :loading="ifLoading"
              @on-selection-change="select">
        </Table>
      </Row>

      <Row class="pageWrapper">
        <Page :total="totalNum"
          class="buttomPage"
          :current="current"
          @on-change="changePage"
          show-elevator>
        </Page>

      </Row>
       <!-- 是否删除 -->
      <Modal
        v-model="ifDelete"
        title="删除"
        @on-ok="confirmDel"
        @on-cancel="cancelDel">
        <p>是否删除已选择的{{ selectedArr.length }}项</p>
      </Modal>


    </Card>
  </div>
</template>

<script>

import qs from 'qs';
import { stickTopAnnounce, updateHelpManage, announceManage, addAnnounce, delAnnounce,manageDown  } from '@/service/getData';
import dtime from 'time-formater'
import { setStore, removeStore } from '@/config/storage';

  export default {
    data () {
      return {
        currentPageIdx: 1,
        ifDelete: false,
        ifLoading: true,
        id: null,
        ifUpdateBtn: false,
        selectedArr: [],
        announcementArr: [],
        current: 1,
        totalNum: null,
        manageModal: false,
        manageTitle: null,
        manageClass: null,
        manageClassArr: [
          { status: 0,  klassName: '常见分类' }
        ],
        imgUrl: null,
        createTime: null,
        ifSHow: null,
        ifShowArr: [
          { status: 0,  klassName: '显示' },
          { status: 1,  klassName: '不显示' },
        ],
        columns_first: [
          {
            type: 'selection',
            width: 60
          },
          {
           	title: '编号',
            key: 'id',
            width: 80
          },
          {
           	title: '标题',
						key: 'title',
            width: 400
          },
          {
            title: '语言',
            key: 'lang',
            render:(h, obj) => {
              let lang = obj.row.lang;
              var langText = "中文";
              if(lang == "EN"){
                langText = "English";
              }
              return h('span', {
              },langText);
            }
          },
          {
           	title: '发布时间',
						key: 'createTime',
            width: 140
          },
           {
           	title: '显示',
            render: (h, obj) => {
              let text = null;
              if (obj.row.isShow) text = '是';
              else text = '否';
              return h ( 'span', {
              }, text)
            }
          },
           {
           	title: '置顶',
            render: (h, obj) => {
              let text = null;
              if (obj.row.isTop==0) text = '是';
              else text = '否';
              return h ( 'span', {
              }, text)
            }
          },

           {
           	title: '操作',
            // key: 'serialNumber',
            width:250,
            	render: (h, obj) => {
                return h ('div',[
                    h('Button', {
                      props: {
                        type: 'info',
                        size: 'small'
											},
											style: {
												'marginRight': '5px'
											},
                      on: {
                        click:() => {

                          removeStore('manageID');
                          setStore('manageID',  obj.row.id);

                          this.$router.push('/content/announceManage/addAnnounce');

                        }
                      }
										},'查看 / 编辑'),

										h('Button', {
                      props: {
                        type: 'error',
                        size: 'small'
                      },
                      on: {
                        click:() => {
													stickTopAnnounce({id: obj.row.id})
													.then(res => {
														if(!res.code) {
															this.$Message.success(res.message);
															this.refreshPage();
														}else this.$Message.error(res.message)
													})
													.catch(err => console.log(err))
                        }
                      }
                    },'置顶'),
                    h('Button', {
                      props: {
                        type: 'error',
                        size: 'small'
                      },
                      style:{
                        marginLeft:'6px',
                      },
                      on: {
                        click:() => {
													manageDown({id: obj.row.id})
													.then(res => {
														if(!res.code) {
															this.$Message.success(res.message);
															this.refreshPage();
														}else this.$Message.error(res.message)
													})
													.catch(err => console.log(err))
                        }
                      }
										},'取消置顶')
                  ])
              }
          },
			  ],
      }
    },
    methods: {
      refreshPageManual() {
        this.ifLoading = true;
        this.refreshPage({ pageNo: this.currentPageIdx, pageSize: 10 })
      },
      confirmDel () {
        let delArr = [];
        this.selectedArr.forEach( item => {
          delArr.push(item.id);
        })
        let formatArr =  qs.stringify({ ids: delArr }, { arrayFormat: 'repeat' });
        // console.log(delArr);
        delAnnounce({ ids: delArr })
        .then( res => {
          if(!res.code) {
            this.refreshPage({ pageNo: 1, pageSize: 10 });
            this.$Message.success('删除成功!');
          }else{
            this.$Message.error('删除失败!');
          }
        })
      },
      cancelDel () {
        this.$Message.success('已取消！');
      },
      delOneAnnounce() {
        if(!!this.selectedArr.length) {
          this.ifDelete = true;
        }else{
          this.$Message.warning('请选择需要删除的内容！');
        }
      },
      updateManage() {

        let updateObj = {
          id:  this.id,
          title: this.manageTitle,
          sysHelpClassification: this.manageClass,
          imgUrl: this.imgUrl,
          status: this.ifSHow,
          createTime: dtime(this.createTime).format('YYYY-MM-DD HH:mm:ss')
        }

        updateHelpManage(updateObj)
        .then(res => {
          if (!res.code) {
            this.manageModal = false;
            this.$Message.success('更新成功!');
            this.refreshPage();
          }else{
             this.$Message.error('出现异常!');
          }
        })
      },

      cancelAdd() {
        this.manageModal = false;
      },
      showModel() {
        this.manageModal = true;
      },
      addOneAnnounce() {
        this.$router.push('/content/announceManage/addAnnounce')
      },
      select(selection) {
        this.selectedArr = selection;
      },
      changePage(pageIndex) {
         this.currentPageIdx = pageIndex;
        this.refreshPage({ pageNo: pageIndex, pageSize: 10 });
      },
      refreshPage(obj) {
        removeStore('manageID');
        announceManage(obj)
        .then( res => {
          this.ifLoading = false;
          this.announcementArr = res.data.content;
          this.totalNum = res.data.totalElements;
        })
      }
    },
    created() {
      this.refreshPage();
    }
  }
</script>

<style lang="less" scoped>
  .manageRow{
    height: 45px;
  }
  .pageWrapper{
    text-align: right;
    margin: 25px;
  }
  .manageWrapper{
    text-align: right;
    Button {
      margin-right: 10px;
    }
  }

  .manageModal{
    p,div{
      margin-bottom: 10px;
    }
    p,div{
      span {
        display: inline-block;
        font-size: 15px;
        text-align: right;

        i{
          color: red;
          font-weight: 700;
          font-style: normal;
        }
      }
    }
    .ivu-input-wrapper.ivu-input-type{
      width: 70%;
    }
  }
</style>

