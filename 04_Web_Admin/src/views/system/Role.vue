<template>
    <div >
      <Card>
        <p slot="title">
        角色管理
          <Button type="primary" size="small" @click="refreshPageManual">
            <Icon type="refresh"></Icon>
            刷新
          </Button>
        </p>

        <Row class="functionWrapper">
          <div class="btnsWrapper clearfix">
            <Button type="primary" @click="addRoleBtn">添加角色</Button>
          </div>
        </Row>

        <Modal
          v-model="delRole"
          title="删除角色"
          @on-ok="deleteRole"
          @on-cancel="$Message.info('已取消！')">
          <p>是否删除该角色?</p>
      
        </Modal>

        <Modal
          v-model="showAddRole"
          title="添加角色"
          @on-ok="addRole">
          <Form label-position="right" :model="formValidate" :label-width="100" :rules="ruleValidate"> 

            <FormItem label="角色名称：" prop="name">
              <Input v-model="formValidate.name"></Input>
            </FormItem>

            <FormItem label="角色描述：">
              <Input type="textarea" v-model="formValidate.roleDscrp"> </Input>
            </FormItem>

          </Form>
          <br>
          <Tree :data="allPermission" show-checkbox @on-check-change="addRoleTree"></Tree>

        </Modal>

        <Row >
          <Table 
          :columns="column_frist" 
          :data="userpage"
          :loading="ifLoading" 
          border>
          </Table>
        </Row>
        <Modal
            v-model="showForm"
            title="修改权限"
            @on-ok="confirmChange"
            @on-cancel="cancelChange">
            
            <Form>
              <FormItem label="角色描述：">
                <Input type="textarea" v-model="formValidate.roleDscrp"> </Input>
              </FormItem>
            </Form>
            
            <Tree :data="permissions | filterPer" show-checkbox @on-check-change="fixTreeArray"></Tree>
        </Modal>
				<Row class="pageWrapper">
          <Page :total="pageNum" 
                class="buttomPage"
                @on-change="changePage"
								:current="currentPageIdx"
                show-elevator></Page>
        </Row>
      </Card>
      <div class="permissionWrapper" v-if="permissionWrapper">
        <p>数据更新中...</p>
      </div>
    </div>
</template>

<script>

import { roleManage, queryRolePermission, addAuditRole, getAllPermission, deleteRole } from '@/service/getData'
import { setStore, getStore, removeStore } from '@/config/storage';

export default {
  data() {
    return {
			pageNum: null,
			currentPageIdx: 1,
      permissionWrapper: false,
      ifLoading: true,
      deleteRoleArr: [],
      delRole: false,
      deleteRoleID: null,

      fixedRolePermission: [],
      submitArr: [], 
      allPermission: [],
      roleId: null,
      showAddRole: false,
      showForm: false,

      formValidate: {
        name: null,
        roleDscrp: null
      },
      ruleValidate: {
        name: [
          { required: true, message: '角色名称不能为空!', trigger: 'blur' }
        ]
      },
      column_frist: [
        {
          title: 'ID',
          key: 'id',
          width: 80
        },
        {
          title: '角色名称',
          key: 'role'
        },
        {
          title: '备注',
          key: 'description'
        },
        //  {
        //   title: '权限',
        //   key: ' | filterPermission'
        // },
        {
          title: '操作',
          render: (h, obj) => {
            return h ( 'div', [
              h('Button',{
                props: {
                  type: 'info',
                  size: 'small'
                },
                style: {
                  'marginRight': '5px'
                },
                on:{
                  click: () =>{
                    this.showForm = true;
                    this.filterPermission = []; 
                    this.roleId = obj.row.id;
                    this.formValidate.name = obj.row.role;
                    this.formValidate.roleDscrp = obj.row.description;
  
                    let initTree = JSON.parse(getStore('allPermission'));
                    initTree.forEach( item => {
                      item.checked = false;
                    })
  
                    queryRolePermission({ roleId: obj.row.id }).then(res => {
                      if (!res.code) {
                        let cbIdArr = [];
                        res.data.forEach( items => {
                          items.subMenu.forEach(item => {
                            cbIdArr.push({id: item.id, parentId: item.parentId });
                          })
                        } )
                        initTree.forEach(items => {
                          (items.children).forEach(item => {
                           let judge = cbIdArr.find( (val) => {
                                return val.id === item.id;
                            });
                            if (!!judge) {
                              item.checked = true;
                            }
                          })
                        })
                    this.permissions = initTree;
                      }else {
                        this.$Message.error('数据获取异常！')
                      }
                    })  
                  }
  
                }
              }, '修改'),

              h('Button',{
                props: {
                  type: 'error',
                  size: 'small'
                },
                on:{
                  click: () =>{
                    console.log(obj.row.id);
                    this.deleteRoleID = obj.row.id;
                    this.delRoleBtn();
                  }
                }
              }, '删除'),

            ] ) 


          }
        },
      ],  
      permissions: [],
      userpage: [],
    }
  },

  methods: {
		
		changePage(pageIndex) {
      this.currentPageIdx = pageIndex;
      this.refreshPage({ pageNo: pageIndex, pageSize: 10 });
    },
    deleteRole() {
      deleteRole({ id: this.deleteRoleID })
      .then( res => {
        if(!res.code) {
          this.$Message.success('删除成功！');
          this.refreshPage();
        }else this.$Message.error(res.message);
      } )
    },
    delRoleBtn() {
      this.delRole = true;
    },
    addRoleBtn() {
			this.submitArr = [];
      this.formValidate.roleDscrp =  this.formValidate.name = null;
      this.showAddRole = true;
			this.allPermission = JSON.parse(getStore('allPermission'));
			(this.allPermission).forEach( (allItem, allIdx) =>　{
				this.submitArr.push(allItem.id);
				if(allItem.children!==undefined) {
					allItem.children.forEach( itemChild => {
						this.submitArr.push(itemChild.id);
					})
				}
			})
    },
    addRoleTree(val) { 
			let arr = [];
			this.submitArr = [];
      val.forEach( item => {
        (this.allPermission).forEach( (allItem, allIdx) =>　{
          allItem.children.forEach( itemChild => {
            if(itemChild.id === item.id) {
              arr.push(allItem.id);
            }
          })
        })
      })

      let parentAarr = [...new Set(arr)];

      let sonArr = [];
      
      val.forEach(item => {
        if (!item.children) {
          sonArr.push(item.id);
        }
      })
      this.submitArr = [ ...parentAarr, ...sonArr ];

    },
    addRole() { 
			// console.log(this.submitArr);
			// // if(!this.submitArr.length){
			// // 	this.submitArr = this.allPermission;
			// // }
			
			if(this.submitArr.indexOf(8)===-1) {
				this.submitArr.push(...[8, 11, 12, 13, 66, 90, 116])
			}
			
      if(this.formValidate.name === '' || !this.formValidate.name) {
        this.$Message.warning('角色名称不能为空！');
      }else{
				this.permissionWrapper = true;
        let obj = { 
          role: this.formValidate.name, 
          description: this.formValidate.roleDscrp, 
          permissions: this.submitArr 
        }

        addAuditRole(obj)
        .then( res => {
          if (!res.code) {
            this.$Message.success('操作成功！');
            this.refreshPage();
					}else this.$Message.error(res.message);
					this.permissionWrapper = false;
					
				})
				.catch(err => {console.log(err)})
      }

    },
    selectArrayFn (val) { 
      let fromateArr = [];
      val.forEach( item => {
        if (!!item.children) {
          fromateArr.push(item);
        }
      })

      let definedArray = [];
      fromateArr.forEach(item => {
        definedArray.push(item.id);
        item.children.forEach( it => {
           definedArray.push(it.id);
        })
      })
      
     return definedArray;
      
      
    },
    fixTreeArray(val) {
      let fixedIdArr = [];
      
      val.forEach(item => {
        fixedIdArr.push(item.id);
        fixedIdArr.push(item.parentId);
      } )
      this.fixedRolePermission = [...new Set(fixedIdArr)];
    },
		
    confirmChange () {
			this.permissionWrapper = true;
			if(this.fixedRolePermission.indexOf(8)===-1) {
				this.fixedRolePermission.push(...[8, 11, 12, 13, 66, 90, 116])
			}
       let obj = {
         id: this.roleId, 
         role: this.formValidate.name, 
         description: this.formValidate.roleDscrp, 
         permissions:  this.fixedRolePermission
			 }
			 
      addAuditRole(obj)
      .then( res =>{
        if (!res.code) {
          this.$Message.success('角色权限修改成功！');
          this.refreshPage();
        }else{
          this.$Message.error('角色权限修改失败！');
        }
      this.permissionWrapper = false;
        
      })

    },
    cancelChange() {
      this.$Message.info('已取消修改！');
    },
    getAllPermissionFn(bol = true) { 
      let allPermission = [];
     getAllPermission() 
      .then( res => {
        if( !res.code ) {
          res.data.forEach(item => {
            let firstTree = {
                title: item.title,
                id: item.id,
                checked: bol,
                children: []
              };
            (item.subMenu).forEach( it => {
              let secTree = {
                id: it.id,
                title: it.title,
                parentId: it.parentId
              };
              firstTree.children.push(secTree)
            })
            allPermission.push( firstTree );

            removeStore('allPermission');
            
            setStore('allPermission', allPermission); //处理后数据用于tree

          })
        }
      })
      
    },
    refreshPageManual() {
      this.refreshPage();
    },
    refreshPage() {
      this.ifLoading = true;
      
      roleManage()
      .then( res => {
        if(!res.code){
          this.userpage =  res.data.content;
          this.ifLoading = false;          
        }else this.$Message.error(res.message);
      }, err => {
        console.log(err);
      })
    }
  },
  created () {
		this.getAllPermissionFn();
    this.refreshPage();
  },
  filters: {
    filterPer(val) {
      val.forEach( item => {
        if(item.children.length>0){
          item.children.forEach( it => {
            if(it.id===116 || it.id===90){
              it.disableCheckbox = true;
              it.checked = true;
            }
          })
        }
      })
      return val;
      
    }
  }
}
</script>

<style lang="less" scoped>
  .permissionWrapper{
    position: absolute;
    z-index: 10;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, .2);
    p{
      position: absolute;
      top: 50%;
      left: 45%;
      transform: -50%;
      font-size: 25px;
      font-style: '黑体';
      text-align: center;
      color: #fff;

    }
  }
</style>