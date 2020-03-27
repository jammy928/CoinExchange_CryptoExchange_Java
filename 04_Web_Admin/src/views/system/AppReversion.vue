<template>
    <div >
      <Card>
        <p slot="title">
        版本管理
          <Button type="primary" size="small" @click="refreshPageManual">
            <Icon type="refresh"></Icon>
            刷新
          </Button>
        </p>

        <Row >
          <Table
          :columns="column_frist"
          :data="appreversion"
          :loading="ifLoading"
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
      </Card>
    </div>
</template>

<script>

import { sysAppRevision } from '@/service/getData'
import { setStore, getStore, removeStore } from '@/config/storage';

export default {
  data() {
    return {
      pageNum: null,
      currentPageIdx: 1,
      ifLoading: true,
      showForm: false,
      column_frist: [
        {
          title: 'ID',
          key: 'id',
          width: 80
        },
        {
          title: '平台',
          key: 'platform'
        },
        {
          title: '当前版本',
          key: 'version'
        },
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

                  }

                }
              }, '修改')
            ] )
          }
        },
      ],
      appreversion: [],
    }
  },

  methods: {
    changePage(pageIndex) {
      this.currentPageIdx = pageIndex;
      this.refreshPage({ pageNo: pageIndex, pageSize: 10 });
    },
    cancelChange() {
      this.$Message.info('已取消修改！');
    },
    refreshPageManual() {
      this.refreshPage({ pageNo: this.currentPageIdx, pageSize: 10 });
    },
    refreshPage() {
      this.ifLoading = true;
      sysAppRevision()
      .then( res => {
        if(!res.code){
          this.appreversion =  res.data.content;
          this.ifLoading = false;
        }else this.$Message.error(res.message);
      }, err => {
        console.log(err);
      })
    }
  },
  created () {
    this.refreshPage();
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
