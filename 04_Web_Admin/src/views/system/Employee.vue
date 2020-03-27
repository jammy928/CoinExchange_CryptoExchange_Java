<template>
    <div >
      <Card>
        <p slot="title">
        用户管理
          <Button type="primary" size="small" @click="refreshPageManual">
            <Icon type="refresh"></Icon>
            刷新
          </Button>
        </p>

        <Row class="functionWrapper">
          <div class="btnsWrapper clearfix">
            <Button type="error" @click="ifDelete = true">删除用户</Button>
            <Button type="primary" @click="addUserBtn">添加用户</Button>
          </div>
        </Row>

        <Row >
          <Table 
            :columns="column_frist" 
            :data="userpage" 
            :loading="ifLoading"
            @on-selection-change="select"
            border>
          </Table>
        </Row>

        <Row class="pageWrapper">
          <Page :total="pageNum" 
                class="buttomPage"
                @on-change="changePage"
								:current="currentPageIdx"
                show-elevator></Page>
        </Row>

        <Modal
          v-model="ifDelete"
          title="删除用户"
          @on-ok="confirmDel"
          @on-cancel="$Message.info('已取消！')">
          <p>是否删除所选的{{ delArr.length }}项?</p>
       </Modal>
      </Card>
    </div>
</template>

<script>
import { setStore, getStore, removeStore } from '@/config/storage';
import { queryEmployee, addAuditEmployee, employeeDetail, delEmployee, departmentManage } from "@/service/getData";


export default {
  data() {
    return {
      currentPageIdx: 1,
      pageNum: null,
      delArr: [],

      ifLoading: true,
      ifDelete: false,

      departName: null,

      column_frist: [
        {
          type: "selection",
          width: 80,
          align: "center"
        },
        {
          title: "用户ID",
          key: "id",
          width: 80
        },
        {
          title: "用户名",
          key: "username"
        },
        {
          title: "真实姓名",
          key: "realName"
        },
        {
          title: "所在部门",
          render: (h, obj) => {
            let departName = obj.row.department.name;
            return "span", {}, departName;
          }
        },
        {
          title: "角色",
          key: "roleName"
        },
        {
          title: "手机号",
          key: "mobilePhone"
        },
        {
          title: "邮箱",
          key: "email"
        },
        {
          title: "状态",
          key: "enable",
          render: (h, obj) => {
            let enable = obj.row.enable;
            let status = "";
            let btnType = "success";
            if (!enable) {
              status = "正常";
            } else {
              status = "禁用 ";
              btnType = "error";
            }
            return h(
              "Button",
              {
                props: {
                  type: btnType,
                  size: "small"
                }
              },
              status
            );
          }
        },

        {
          title: "操作",
          render: (h, obj) => {
            return (
              "div",
              [
                h(
                  "Button",
                  {
                    props: {
                      type: "info",
                      size: "small"
                    },
                    on: {
                      click: () => {
                        removeStore('employeeID');
												setStore('employeeID', obj.row.id);
                        this.$router.push("/system/employee/auditEmployee");
                      }
                    }
                  },
                  "查看 / 编辑"
                )
              ]
            );
          }
        }
      ],
      userpage: []
    }
  },
  methods: {
    changePage(pageIndex) {
      this.currentPageIdx = pageIndex;
      this.freshPage({ pageNo: pageIndex, pageSize: 10 });
    },
    select(solutions) {
      this.delArr = [];
      solutions.forEach(item => {
        this.delArr.push(item.id);
      });
      console.log(solutions);
    },
    addUserBtn () {
      this.$router.push("/system/employee/auditEmployee");
    },
    confirmDel() {
      delEmployee({ ids: this.delArr })
      .then(res => {
        if (!res.code) {
          this.freshPage();
          this.$Message.success("删除成功！");
        } else this.$Message.error("删除失败！");
      })
      .catch( err =>{ console.log(err)})
    },
   
    refreshPageManual() {
      this.freshPage({ pageNo: this.currentPageIdx, pageSize: 10 });
    },
    freshPage(obj) {
      this.ifLoading = true;
      queryEmployee(obj)
      .then(res => {
        console.log(res);
        
        if (!res.code) {
          this.userpage = res.data.content;
          this.ifLoading = false;
          this.pageNum = res.data.totalElements;
        } else this.$Message.error(res.message);
      })
      .catch( err => console.log( err ));
    }
  },
  created() {
    removeStore('employeeID');
    this.freshPage({ pageNo: 1, pageSize: 10 });
  }
};
</script>

<style lang="less" scoped></style>