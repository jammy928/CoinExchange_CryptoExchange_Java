<template>
    <div >
      <Card>
        <p slot="title">
        部门管理
          <Button type="primary" size="small" @click="refreshPageManual">
            <Icon type="refresh"></Icon>
            刷新
          </Button>
        </p>

        <Row class="functionWrapper">
          <div class="btnsWrapper clearfix">
            <Button type="primary" @click="addDepartBtn">添加部门</Button>
          </div>
        </Row>

        <Modal
          class="addDepartModal"
          v-model="showAddDepart"
          title="添加部门"
          @on-ok="addDepart">

          <p><span>部门名称 <i>*</i>：</span><Input v-model="departName"></Input></p>
          <p><span>领导ID：</span><Input v-model="leaderID"></Input></p>
          <p><span>部门描述：</span><Input type="textarea" v-model="departDscrp"> </Input></p>

        </Modal>

        <Row >
          <!-- @on-selection-change="selected" -->
          <Table 
          :columns="column_frist" 
          :data="userpage" 
          :loading="ifLoading"
          border>
          </Table>
        </Row>

        <Modal
          class="addDepartModal departDetailModal"
          v-model="showDepartDetail"
          title="部门详情"
          @on-ok="updateDepart"
          @on-cancel="$Message.info('已取消！')">

          <p><span><em>领</em>导ID：</span><Input v-model="departmentDetail.leaderId"></Input></p>
          <p><span><em>部</em>门：</span><Input v-model="departmentDetail.name"></Input></p>
          <p><span>更新时间：</span><Input disabled v-model="departmentDetail.updateTime"></Input></p>
          <p><span>创建时间：</span><Input disabled v-model="departmentDetail.createTime"></Input></p>
          <p><span><em>备</em>注：</span><Input type="textarea" v-model="departmentDetail.remark"></Input></p>

        </Modal>

        <Modal
        v-model="ifDelete"
        title="删除部门"
        @on-ok="confirmDel"
        @on-cancel="$Message.info('已取消！')">
        <p>是否删除所选的项?</p>
       </Modal>

       <Row class="pageWrapper" >
          <Page :total="totalNum" 
            style='margin-top:8px' 
            :current="currentPageIdx"   
            @on-change="changePage" 
            show-elevator></Page>
        </Row> 
        

      </Card>
    </div>
</template>

<script>

import { departmentManage, addAuditDepart, departDetail, delDepart } from '@/service/getData'

export default {
  data() {
    return {
      totalNum: null,
      currentPageIdx: 1,
      ifLoading: true,
      ifDelete: false,
      delDepartID: null,

      deleteArr: [],
      departmentID: null,
      departmentDetail: {},
      showDepartDetail: false,
      leaderID: null,
      showAddDepart: false,
      departName: null,
      departDscrp: null,
      
      column_frist: [
        // {
        //   type: 'selection',
        //   width: 80,
        //   align: 'center'
        // },
        {
          title: '部门ID',
          key: 'id',
          width: 80
        },
        {
          title: '部门',
          key: 'name'
        },
        {
          title: '创建时间',
          key: 'createTime'
        },
        {
          title: '备注',
          key: 'remark'
        },
         {
          title: '操作',
          render: (h, obj) => {
            return ('div', [
              h('Button',{
                 props: {
                   type: 'info',
                   size: 'small'
                 },
                 style: {
                   'marginRight': '5px'
                 },
                 on: {
                   click: () => {
   
                     this.showDepartDetail = true;
   
                     this.departmentID = obj.row.id;
   
                     departDetail({ departmentId: obj.row.id })
                     .then( res => {
                       if(!res.code) {
                         console.log(res.data);
                         this.departmentDetail = res.data;
                         
                       }else this.$Message.error('数据获取出错！')
                     })
   
                   }
                 }
               },'查看 / 编辑'),
                h('Button',{
                  props: {
                    type: 'error',
                    size: 'small'
                  },
                  on: {
                    click: () => {
                      this.ifDelete = true;
                      this.delDepartID = obj.row.id;
                    }
                  }
                }, '删除')

            ])
          
          }
        },
      ],  
      userpage: []
    }
  },

  methods: {
    changePage() {
      this.freshPage({ pageNo: this.currentPageIdx, pageSize: 10 })
    },
    confirmDel() {
      delDepart({id: this.delDepartID})
        .then( res => {
        if(!res.code) {
          this.freshPage();
          this.$Message.success(res.message);
        }else this.$Message.error(res.message);
      })
    },
    addDepartBtn() {
      this.showAddDepart = true;
    },
    updateDepart() {
      if(this.departmentDetail.name === '' || !this.departmentDetail.name) {
        this.$Message.warning('部门名称不能为空！');
      }else{

        addAuditDepart(this.departmentDetail)
        .then( res => {
          if (!res.code) {
            this.$Message.success(res.message);
            this.freshPage();
          }else this.$Message.error(res.message);
            
        })
      }
    },
    addDepart() {
      if(this.departName === '' || !this.departName) {
        this.$Message.warning('部门名称不能为空！');
      }else{

        let updateObj = {
          name: this.departName, 
          leaderId: this.leaderID,
          remark: this.departDscrp
        }

        addAuditDepart(updateObj)
        .then( res => {
          if (!res.code) {
            this.departName = null;
            this.leaderID = null;
            this.departDscrp = null;

            this.$Message.success(res.message);
            this.freshPage();
          }else{
            this.$Message.error(res.message);
          }
        })
      }

    },
    refreshPageManual() {
      this.freshPage();
    },
    freshPage(obj) {
      this.ifLoading = true;
      departmentManage(obj)
      .then( res => {
        if(!res.code){
          this.userpage =  res.data.content;
          this.ifLoading = false;
          this.totalNum = res.data.totalElements;
        }else this.$Message.error(res.message)
      }, err => {
        console.log(err);
      })
    }
  },
  created () {
    this.freshPage();
  }
}
</script>

<style lang="less" scoped>

  .addDepartModal{
    p{
      position: relative;
      padding-left: 85px;
      margin-bottom: 10px;
      width: 400px;

      span{
        position: absolute;
        top: 0;
        left: 0;
        font-size: 14px;
        line-height: 32px;
        i{
          font-size: 16px;
          font-weight: 700;
          color: #ec0909;
        }
      }
    }
  }
  .departDetailModal{
    p{
      span {
        em{
          font-style: normal;
          letter-spacing: 28px;
        }
      }
    }
  }
</style>