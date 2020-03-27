<template>
  <div>
    <Card>
      <p slot="title">
        转入明细
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
          :loading="ifLoading">
				</Table>
      </Row>

      <div class="pageWrapper">
        <Page  
          :total="totalNum" 
          :current="currentPageIdx"   
          @on-change="changePage" 
          show-elevator></Page>
      </div>
    </Card>
    </div>
</template>

<script>

import { coinTransferDetail } from '@/service/getData';

export default {
	name: 'AccessLog',
  data() {
    return {
      currentPageIdx: 1,
      ifLoading: true,
      totalNum: null,
      columns_first: [
        {
					title: 'ID',
					key: 'id'
				},
				{
          title: "转入币种",
          key: "unit"
				},
        {
          title: "转入时间",
          key: "transferTime"
        },
         {
          title: "转入数量",
          key: "amount"
        },
        {
					title: "钱包余额",
					key: 'balance',
					render: (h, obj) => {
							let num = obj.row.balance;
							if(num<1) {
								num = num.toFixed(8);
							}
							return h('span', {}, num)
						}
        },
        {
          title: "最小手续费",
					key: "minerFee",
					render: (h, obj) => {
						let num = obj.row.minerFee;
						if(num<1) {
							num = num.toFixed(8);
						}
						return h('span', {}, num)
					}
        },
				{
          title: "操作人",
          key: "adminName"
				},
				{
          title: "冷钱包地址",
          key: "coldAddress"
				},
				{
          title: "转入单号",
          key: "transactionNumber"
				},
				
			],
			filterSearch: {
				pageNo: 1,
				pageSize: 10,
				unit: '',
				transactionNumber: ''
			},
      userpage: [],
    };
  },
  methods: {
    refreshPageManual() {
			this.currentPageIdx = 1;
			this.filterSearch.pageNo = 1;
      this.refreshPage(this.filterSearch);
    },
    refreshPage(obj) {
			this.ifLoading = true;
      coinTransferDetail(obj)
      .then(res => {
        if(!res.code){
          this.ifLoading = false;
          this.userpage = res.data.content;
          this.totalNum = res.data.totalElements;
				}else this.$Message,error(res.message);
      });
    },
    changePage(pageIndex) {
			this.currentPageIdx = pageIndex;
			this.filterSearch.pageNo = pageIndex;
      this.refreshPage(this.filterSearch);
    },
  },
  created() {
		this.filterSearch.unit = this.$route.query.unit;
		this.refreshPage(this.filterSearch);
  }
}
</script>

<style scoped lang='less'>
 
</style>