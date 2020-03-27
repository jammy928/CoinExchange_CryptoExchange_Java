<template>
<!-- 提币明细 -->
  <div>
    <Card>
      <p slot="title">
        提币明细
        <Button type="primary" size="small" @click="refreshPageManual">
          <Icon type="refresh"></Icon>
          刷新
        </Button>
      </p>

       <Row>
          <Table 
            :columns="columns_first" 
            :data="userpage" 
            border 
            :loading="ifLoading"
            ref="tabel"
            class='user_center'>
          </Table>
      </Row>

      <Row class="pageWrapper" >
        <Page :total="totalNum"
        :current="currentPageIdx"   
        @on-change="changePage" show-elevator></Page>
      </Row>

    </Card>
  </div>
</template>

<script>
export default {
  data() {
    return {
      columns_first: [
          {
            title: '会员ID',
            key: 'id',
          },
          {
            title: '用户名',
            key: 'username',
          },
          {
            title: '邮箱',
            key: 'email',
          },
          {
            title: '手机号',
            key: 'mobilePhone',
          },
           {
            title: '真实姓名',
            key: 'realName',
          },
           {
            title: '币种名称',
            key: 'username',
          },
          {
            title: '钱包地址',
            key: 'username',
          },
          {
            title: '提币个数',
            key: 'username',
          },
          {
            title: '实际到账个数',
            key: 'username',
          },
          {
            title: '手续费',
            key: 'username',
          },
          {
            title: '提币申请时间',
            key: 'username',
          },
           {
            title: '审核通过时间',
            key: 'username',
          },
           {
            title: '审核人',
            key: 'username',
          }
      ],
      userpage: [],
      ifLoading: true,
      totalNum: null,
      currentPageIdx: 1,
    }
  },
  mehods: {
    refreshPageManual() {

    },
    changePage() {

    }
  }
}
</script>

<style lang="less" scoped>

</style>


