<template>
  <div>
    <Card>
      <p slot="title">
      个人中心
        
      </p>

      <Row >
        <Table 
        :columns="column_frist" 
        :data="userpage"
        :loading="ifLoading" 
        border>
        </Table>
      </Row>

      <Modal
        v-model="showModel"
        title="修改密码"
        width=400
        @on-ok="confirmSub"
        @on-cancel="$Message.info('已取消！')">
        <Form :model="userPW" :label-width="60" label-position="right" :rules="checkPass">
          <FormItem label="旧密码：" prop="lastPassword">
            <Input type="password" v-model="userPW.lastPassword" placeholder="请输入旧密码"></Input>
          </FormItem>
           <FormItem label="新密码：" prop="newPassword">
            <Input type="password" v-model="userPW.newPassword" placeholder="请输入新密码"></Input>
          </FormItem>
        </Form>
      </Modal>

    </Card>   
  </div>
</template>

<script>
import Cookies from 'js-cookie'
import { getStore, setStore, removeStore } from '@/config/storage'
import { employeeDetail, fixPersonalPW } from '@/service/getData'
export default {
  data() {
    return {
      checkPass: {
        lastPassword:  { required: true, message: '不能为空', trigger: 'change' },
        newPassword:  { required: true, message: '不能为空', trigger: 'change' }
      },
      column_frist: [
        {
          title: '用户名',
          key: 'username'
        },
        {
          title: '所在部门',
          key: 'departmentName'
        },
        {
          title: '拥有权限',
          key: 'role'
        },
        {
          title: '电话号码',
          key: 'mobilePhone'
        },
        {
          title: '邮箱',
          key: 'email'
        },
        {
          title: 'QQ',
          key: 'qq'
        },
        {

          title: '操作',
          render : (h, obj) => {
             return h ( 'Button',{
               props: {
                 type: 'info',
                 size: 'small'
               },
               on: {
                 click: () => {
                   this.showModel = true;
                 }
               }
             }, '修改密码' )
          }
        },
      ],
      userpage: [],
      ifLoading: true,
      showModel: false,
      userPW: {
        lastPassword: '',
        newPassword: '',
        id: ''
      }
    }

  },
  methods: {
    confirmSub() {
      let pass = true;
      for (let key in this.userPW ) {
        if( !String(key).trim().length ) pass = false;
      }
      if(pass) {
        fixPersonalPW(this.userPW)
        .then( res => {
          if( !res.code ) {
            this.$Message.success(res.message);
          }else this.$Message.error(res.message);
        })
        .catch( err => console.log(err))
      }else this.$Message.warning('请完善信息！')
    }
  },
  created() {
    let personalInfo =  JSON.parse(Cookies.get('userInfo'));
    this.userPW.id = personalInfo.id;
    employeeDetail({ id: personalInfo.id })
    .then( res => {
      if(!res.code) {
        this.userpage = [res.data];
        this.ifLoading = false;
      }else this.$Message.error(res.message)
    })
    .catch( err => console.log(err))
  }
}
</script>

<style lang="less" scoped>

</style>


