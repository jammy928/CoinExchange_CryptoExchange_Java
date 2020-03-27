<template>
    <div>
      <Card>
        <p slot="title">
          编辑用户
        </p>

        <Row class="mainEmployeeContent">
          <Form ref="formCustom" 
          :model="formWrapperObj" 
          :rules="ruleFrom" 
          :label-width="90"
          label-position="right">

            <FormItem label="用户名：" prop="username">
              <Input type="text" v-model="formWrapperObj.username"></Input>
            </FormItem>

            <FormItem label="密码：" prop="password" class="resetRequired" :class="{ 'resetFromErr': uerpsCheck}">
              <Input type="password" v-model="formWrapperObj.password"></Input>
              <div  v-if="uerpsCheck" class="ivu-form-item-error-tip">两次密码输入不一致！</div>
            </FormItem>

            <FormItem v-if="!employeeID" label="确认密码：" prop="passwdCheck" class="resetRequired" :class="{ 'resetFromErr': uerpsCheck}">
              <Input type="password" v-model="formWrapperObj.passwdCheck"></Input>
              <div  v-if="uerpsCheck" class="ivu-form-item-error-tip">两次密码输入不一致！</div>
            </FormItem>

            <FormItem label="真实姓名：" prop="realName">
              <Input type="text" v-model="formWrapperObj.realName"></Input>
            </FormItem>

             <FormItem label="联系电话：" prop="mobilePhone">
              <Input type="text" v-model="formWrapperObj.mobilePhone"></Input>
              <!-- <div  v-show="true" class="ivu-form-item-error-tip">请输入正确的手机号！</div> -->
            </FormItem>

            <FormItem label="角色：" prop="role">
              <Select v-model="formWrapperObj.roleId" >
                <Option v-for="role in roleArr" :key="role.id" :value="role.id">{{ role.role }}</Option>
              </Select>
							<router-link to="/system/rolemanage">添加角色</router-link>
            </FormItem>

            <FormItem label="部门：" prop="depart">
              <Select v-model="formWrapperObj.departmentId">
                <Option v-for="depart in departArr" :key="depart.id" :value="depart.id">{{ depart.name }}</Option>
              </Select>
							<router-link to="/system/departmanage">添加部门</router-link>
            </FormItem>

            <Row class="dashLine">
              <Button type="text" @click=" swtichMore = !swtichMore ">
                <span v-if="!swtichMore">更多 <Icon type="chevron-down"></Icon></span>
                <span v-else>收起 <Icon type="chevron-up"></Icon></span>
              </Button>
            </Row>  

            <Row v-show="swtichMore">
              <FormItem label="用户状态：">
                <RadioGroup v-model="formWrapperObj.enable">
                  <Radio label="0">正常</Radio>
                  <Radio label="1">禁用</Radio>
                </RadioGroup>
              </FormItem>

              <FormItem label="QQ：" prop="tencentQQ">
                <Input type="text" v-model="formWrapperObj.qq"></Input>
              </FormItem>

              <FormItem label="邮箱：" prop="email">
                <Input type="text" v-model="formWrapperObj.email" ></Input>
              </FormItem>

              <FormItem label="头像：" prop="avatar">
                <Input type="text" v-model="formWrapperObj.avatar"></Input>
              </FormItem>
            </Row>

            <FormItem>
              <Button type="success" long @click="confrimSub">确 认</Button>
            </FormItem>
          </Form>
        </Row>
      </Card>
    </div>
</template>

<script>

import { setStore, getStore, removeStore } from '@/config/storage';
import { roleManage, departmentManage, addAuditEmployee, employeeDetail } from '@/service/getData'



export default {
  
  data() {
    
    const telPass = (rule, value, callback) => {
      console.log(value);
      
    }
    return {  
      uerpsCheck: false,
      employeeID: null,
      swtichMore: false,
      roleArr: [],
      departArr: [],
      formWrapperObj: {
        id: null,//f  //11 不需要
        username: null, //11
        password: null, //隐藏
        enable: 0,//f //11
        passwdCheck: null, //隐藏
        roleId: null,// 11
        departmentId: null,//11
        realName: null,//11
        mobilePhone: null,//11
        qq: null,//f//11
        email: null,//f//11
        avatar: null,//f//11
      },
      ruleFrom: {
        username: [
          { required: true, message: '用户名不能为空！', trigger: 'blur' }
          // { required: true, validator: validatePass, trigger: 'change' }
          // { required: true, message: 'The name cannot be empty', trigger: 'change' }
          //  { type: 'email', message: 'Incorrect email format', trigger: 'change' }
        ],
        role: [
          { required: true, message: '请选择角色！', trigger: 'blur' }
        ],
        depart: [
          { required: true, message: '请选择所在部门！', trigger: 'blur' }
        ],
        realName: [
          { required: true, message: '请填写正确的格式！', trigger: 'blur' }
        ],
        mobilePhone: [
          { required: true, message: '请填写正确的手机号！', trigger: 'blur' }
        ],
        email: [
          { type: 'email', message: '请填写正确的邮箱地址！', trigger: 'blur' }
        ]
      }
    }
      },
  methods: {
    confrimSub() {
      let checkSubObj = JSON.parse(JSON.stringify(this.formWrapperObj));
      let arr = [ 'id', 'qq', 'email', 'avatar', 'enable', 'passwdCheck'];

      for(let i = 0; i < arr.length; i++) {
            delete checkSubObj[arr[i]]
      }
      
      // console.log(checkSubObj);

      for ( let x in checkSubObj ) {
        if( !checkSubObj[x] ) {
          this.$Message.warning('请完善信息！');
          return;
        }else if (!this.employeeID) {
          if(this.formWrapperObj.password!==this.formWrapperObj.passwdCheck){
            this.$Message.warning('两次密码输入不一致！');
            return;
          }
        }
      } 
    addAuditEmployee(this.formWrapperObj)
    .then( res => {
      if(!res.code) {
        console.log(res);
        this.$router.push('/system/employee');
        this.$Message.success(res.message);
      }else this.$Message.error(res.message);
    })

    }
  },
  created () {
    roleManage()
    .then( res => {
      if(!res.code){
        this.roleArr = res.data.content;
      }else this.$Message.error(res.message)
		})
		
    departmentManage()
    .then( res => {
      if(!res.code){
				
       this.departArr = res.data.content;
      }else this.$Message.error(res.message)
		})
		
		this.employeeID = getStore('employeeID');
		
    if(!!this.employeeID){
      employeeDetail({ id: this.employeeID })
      .then( res => {
        if(!res.code) {
          this.formWrapperObj = res.data;
        }else this.$Message.error(res.message);
        console.log(res);
      })
    }
    
  },
  watch: {
    formWrapperObj: {
      handler(newVal, oldVal) {
        if(!this.employeeID) {
          if(newVal.password !== newVal.passwdCheck) {
            this.uerpsCheck = true;
          }else this.uerpsCheck = false;
        }
      },
      deep: true
    }
  }
}
</script>

<style lang="less" scoped>
  .mainEmployeeContent {
    .ivu-form-item {
      margin: 25px auto;
      width: 380px;
		}
		.ivu-form-item-content {
			a{
				position: absolute;
				right: -60px;
				top: 0;
			}
		}
    .dashLine{
      text-align: center;
      border-top: 1px dashed #d2d2d2;
      Button{
        color: #b3b1b1;
        &:hover{
          color: #57a3f3;
        }

      }
    }
  }
</style>