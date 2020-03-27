<template>
  <div>
      <Card>
        <p slot="title">
        权限列表
          <Button type="primary" size="small" @click="refreshPageManual">
            <Icon type="refresh"></Icon>
            刷新
          </Button>
        </p>

        <Row class="functionWrapper">
          <div class="btnsWrapper clearfix">
            <!-- <Button type="error" @click="delPermission">删除权限</Button> -->
            <Button type="error">删除权限</Button>
            
            <Button type="primary" @click="addPermission">添加权限</Button>
          </div>
        </Row>

        <Row>
          <Table 
            :columns="columns_first" 
            :data="userpage" 
            border 
            :loading="ifLoading"
            @on-selection-change="selected"
            class='user_center'>
          </Table>
      	</Row>

				<Row class="pageWrapper" >
					<Page :total="totalNum" 
						:current="current"  
						@on-change="changePage" 
						show-elevator>
					</Page>
				</Row>

       <Modal
       	class="auditPermissionModal"
        v-model="ifAudit"
        title="编辑权限"
        @on-ok="confirmAudit">
        <ul>
          <li>
            <span><i>*</i>权限标题：</span>
            <p>
              <Input v-model="title" ></Input>  
            </p>
          </li>

          <li>
            <span><i>*</i>权限名称：</span>
            <p>
              <Input v-model="name" ></Input>
            </p>
          </li>
         
          <li>
            <span><i>*</i>权限目录：</span>
            <p> 
              <Select v-model="parentID">
                <Option value="0" style="color: #ec0909" label="作为一级目录">将其作为一级目录</Option>
                <Option v-for="item in selectArr" :value="item.id" :key="item.id">{{ item.title }}</Option>
              </Select>
            </p>
          </li>
           <li>
            <span>权限描述：</span>
            <p>
              <Input type="textarea" v-model="descript" ></Input>
            </p>
          </li>
        </ul>
      </Modal>

       <Modal
       class="auditPermissionModal"
        v-model="ifDelete"
        title="删除权限"
        @on-ok="confirmDel">
        <p>是否删除选中的{{ deleteArr.length }}项</p>
       </Modal>

      </Card> 
  </div>
</template>

<script>

import { permissionManage, addAuditPermission, delPermission } from '@/service/getData'

  export default{

    data () {
      return {
        columns_first: [
        {
          type: 'selection',
          width: 60
        },
        {
          title: '序号',
          key: 'id',
          width: 80
        },
        {
          title: '父级ID',
          key: 'parentId'
        },
        {
          title: '权限标题',
          key: 'title'
        },
        {
          title: '权限名称',
          key: 'name'
        },
        {
          title: '权限描述',
          key: 'description'
        },
       
        {
          title: '操作',
          key: 'action',
          width: 150,
          align: 'center',
          render: (h, obj) => {
            return h('Button', {
              props: {
                type: 'info',
                size: 'small',
              },
              on: {
                click: () => {
                  this.ifAudit = true;

                  this.ifAdd = false;

                  this.id = obj.row.id;
                  this.name = obj.row.name;
                  this.parentID = obj.row.parentId;
                  this.title = obj.row.title;
                  this.descript = obj.row.description;
                  this.addPermissionBtn();
                }
              }
            }, '修改');
          }
        }
      ],
        userpage: [],
        title: null,
        name: null,
        descript: null,
        parentID: null,
        ifAudit: false,
        totalNum: null,
        current: 1,
        ifLoading: true,
        selectArr: [],
        ifDelete: false,
        deleteArr: [],
        ifAdd: true,
        currentPageIdx: 1

      }
    },
    methods: {
      delPermission() {
        if(!this.deleteArr.length) {
          this.$Message.warning('尚未选取！');
        }else{
          this.ifDelete = true;
        }
      },
      selected(solution) {
        // console.log(solution);
        this.deleteArr = [];
        solution.forEach(item => {
          this.deleteArr.push(item.id)
        });
        console.log(this.deleteArr);
         
      },
      confirmDel() {
        delPermission({ids: this.deleteArr})
        .then( res => {
          if(!res.code) {
            this.refreshPage({ pageNo: 1, pageSize: 10 });
            this.$Message.success('删除成功！');
          }else this.$Message.error('删除失败！');
        } )
      },
      cancelDel () {

      },
      confirmAudit() {
       if(!this.title || !this.name || !this.parentID) {
         this.$Message.warning('请完善信息！');
       } else {
         let obj = {
           name: this.name,
           parentId: this.parentID,
           title: this.title,
           description: this.descript          
         }
         if(!this.ifAdd)  obj.id = this.id;
          
         addAuditPermission(obj)
         .then( res => {
           if(!res.code) {
             this.$Message.success('操作成功！');
             this.refreshPage({ pageNo: 1, pageSize: 10 });
             this.name = this.parentID = this.title = this.descript = null;
           }else  this.$Message.error(res.message);
         })
       }
      },
     
      addPermission() {
        this.ifAdd = true;
        this.addPermissionBtn();
      },
      addPermissionBtn() {
        
        if(this.ifAdd) {
          this.name = this.parentID = this.title = this.descript = null;
        }
        this.ifAudit = true;

        permissionManage({ parentId: 0 })
        .then(res => {
          console.log(res);
          this.selectArr = res.data.content;
        })
      },
      refreshPageManual() {
        this.refreshPage({ pageNo: this.currentPageIdx, pageSize: 10 });
      },
      changePage(pageIndex) {
				this.currentPageIdx = pageIndex;
        this.refreshPage({ pageNo: pageIndex, pageSize: 10 })
      },
      refreshPage(obj) {
				
        this.ifLoading = true;
        permissionManage(obj)
        .then(res => {
          if(!res.code) {
            this.totalNum = res.data.totalElements;
            this.userpage = res.data.content;
          }else this.$Message.error('获取数据出错！')
          this.ifLoading = false;
        })

      }
    },
    created() {
      this.refreshPage({ pageNo: 1, pageSize: 10 });
    }
  }
</script>

<style lang="less" scoped>
  .auditPermissionModal{
    ul {
      li{
        position: relative;
        margin-bottom: 10px;
        span{
          position: absolute;
          top: 0;
          left: 0;
          font-size: 14px;
          line-height: 32px;

          i{
            margin-right: 5px;
            font-size: 14px;
            font-weight: 700;
            color: red;
          }

        }
        p {
          padding-left: 80px;
          width: 400px;
        }

      }
    }
  }
</style>

