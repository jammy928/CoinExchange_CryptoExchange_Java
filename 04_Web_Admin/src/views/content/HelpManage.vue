<template>
  <div>
    <Card>
      <p slot="title">
        帮助管理
        <Button type="primary" size="small" @click="refreshPageManual">
          <Icon type="refresh"></Icon>
          刷新
        </Button>
      </p>

      <Row class="manageRow clearfix">
        <div class="manageWrapper">
          <Button type="error" @click="delManage">删除</Button>
          <Button type="info" @click="addManage">添加</Button>
        </div>
      </Row>

      <Modal
      class="manageModal"
      v-model="manageModal"
      title="添加管理"
      width="430">
        <p slot="header" style="color:#5cadff;text-align:center">
          <Icon type="information-circled"></Icon>
          <span style="padding-left: 5px;" >添加管理</span>
        </p>

        <p> <span>标题<i>*</i>：</span>
          <Input v-model="manageTitle" ></Input>
        </p>
        <div><span>分类<i>*</i>：</span>
          <Select v-model="manageClass" style="width:200px">
            <Option v-for=" item in manageClassArr" v-model="item.status" :key="item.status">
              {{ item.klassName }}
            </Option>
          </Select>
        </div>

        <p><span>图片地址<i>*</i>：</span>
          <Input v-model="imgUrl" ></Input>
        </p>
        <p><span>排序：</span>
          <Input v-model="sort" ></Input>
        </p>
        <div>语言：
          <RadioGroup v-model="lang">
            <Radio label="CN">
              <span>中文</span>
            </Radio>
            <Radio label="EN">
              <span>English</span>
            </Radio>
          </RadioGroup>
        </div>
        <div><span>发布时间<i>*</i>：</span>
          <DatePicker v-model="createTime"
                      type="date" format="yyyy-MM-dd HH:mm:ss"
                      placeholder="请选择时间" style="width: 200px">
          </DatePicker>
        </div>

        <div><span>是否显示<i>*</i>：</span>
          <Select v-model="ifSHow" style="width:200px">
            <Option v-for=" item in ifShowArr" v-model="item.status" :key="item.status">
              {{ item.klassName }}
            </Option>
          </Select>
        </div>

        <div slot="footer" style="color:#5cadff;text-align:right">
          <div v-if="!ifUpdateBtn">
            <Button type="text" @click="cancelAdd">取消</Button>
            <Button type="info" @click="addManage">确认</Button>
          </div>

          <div v-if="ifUpdateBtn">
            <Button type="text" @click="cancelAdd">取消</Button>
            <Button type="info" @click="updateManage">更新</Button>
          </div>
        </div>

      </Modal>

      <Row class="test">
        <Table border
              :columns="columns_first"
              :data="helpManageArr"
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
import { stickTopHelp, helpManage, addHelpManage, delHelpManage, helpManageDetail, updateHelpManage,helpDown  } from '@/service/getData';
import { formatDate } from '@/caculate/caculate';
import { setStore, removeStore } from '@/config/storage';


  export default {
    data () {
      return {
        currentPageIdx: 1,
        delArr: [],
        ifDelete: false,
        ifLoading: true,
        id: null,
        ifUpdateBtn: false,
        selectedArr: [],
        helpManageArr: [],
        current: 1,
        totalNum: null,
        manageModal: false,
        manageTitle: null,
        manageClass: null,
        manageClassArr: [
          { status: 0,  klassName: '新手指南' },
          { status: 1,  klassName: '常见问题' },
          { status: 2,  klassName: '充值指南' },
          { status: 3,  klassName: '交易指南' }
        ],
        imgUrl: null,
        createTime: null,
        lang: "CN",
        sort: null,
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
            width: 60
          },
          {
           	title: '标题',
						key: 'title',
          },
           {
           	title: '分类',
            render: (h, obj) => {
              let text = null;
              let status = obj.row.sysHelpClassification*1;
              if (status === 0) text = '新手指南';
              else if (status === 1) text = '常见问题';
              else if (status === 2) text = '交易指南';
              else if (status === 3) text = '币种资料';
              else if (status === 4) text = '行情技术';
              else if (status === 5) text = '条款协议';
              else if (status === 6) text = '其他';

              return h ( 'span', {
              }, text)
            }
          },
          {
            title: '语言',
            key: 'lang',
            width: 60
          },
          {
            title: '排序',
            key: 'sort',
            width: 60
          },
           {
           	title: '显示',
            key: 'status',
            width:60,
            render: (h, obj) => {
              let text = null;
              if (obj.row.status===0) text = '是';
              else text = '否';
              return h ( 'span', {
              }, text)
            }
          },
           {
           	title: '置顶',
            // key: 'isTop',
            width:60,
            render: (h, obj) => {
              let text = null;
              if (obj.row.isTop==="0") text = '是';
              else text = '否';
              return h ( 'span', {
              }, text)
            }
          },
          {
            title: '发布时间',
            key: 'createTime',
          },
					{
           	title: '操作',
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
                          this.$router.push('/content/helpManage/addhelpmanage');
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
													stickTopHelp({id: obj.row.id})
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
													helpDown({id: obj.row.id})
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
        this.refreshPage({ pageNo: this.currentPageIdx, pageSize: 10 });
      },
      confirmDel() {
         let delArr = [];
        this.selectedArr.forEach( item => {
          delArr.push(item.id);
        })
        let formatArr =  qs.stringify({ ids: delArr }, { arrayFormat: 'repeat' });

        delHelpManage({ ids: delArr })
        .then( res => {
          this.ifDelete = false;
          if(!res.code) {
            this.refreshPage({ pageNo: 1, pageSize: 10 });
            this.$Message.success('删除成功!');
          }else{
            this.$Message.error('删除失败!');
          }
        })
      },
      cancelDel() {
        this.$Message.success('已取消!');
      },
      updateManage() {

        let updateObj = {
          id:  this.id,
          title: this.manageTitle,
          sysHelpClassification: this.manageClass,
          imgUrl: this.imgUrl,
          status: this.ifSHow,
          lang: this.lang,
          createTime: formatDate(this.createTime)
        }
        // console.log(this.createTime);
        // console.log(formatDate(this.createTime))

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
      delManage() {
        if(!!this.selectedArr.length) {
          this.ifDelete = true;
        }else this.$Message.warning("请选择要删除的内容！")
      },
      cancelAdd() {
        this.manageModal = false;
      },
      showModel() {
        this.manageModal = true;
      },
      addManage() {
        removeStore('manageID');
        this.$router.push('/content/helpManage/addhelpmanage');
      },
      select(selection) {
        this.selectedArr = selection;
      },
      changePage(pageIndex) {
        this.ifLoading = true;
        this.currentPageIdx = pageIndex;
        this.refreshPage({ pageNo: pageIndex, pageSize: 10 });
      },
      refreshPage(obj) {
        removeStore('manageID');
        helpManage(obj)
        .then( res => {
          this.ifLoading = false;
          this.helpManageArr = res.data.content;
          this.totalNum = res.data.totalElements;
        })
      }
    },
    created() {
      this.refreshPage({ pageNo: 1, pageSize: 10 });
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
<style lang="less">
.test .ivu-table-wrapper .ivu-table.ivu-table-border .ivu-table-body .ivu-table-tbody .ivu-table-row {
.ivu-table-cell{
  padding:0
}
}
</style>

