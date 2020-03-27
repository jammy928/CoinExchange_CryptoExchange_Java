<template>
    <div>
        <Card>
            <Row class="tableWrapper">
                <Table :columns="columns" :data="list" border :loading="ifLoading">
                </Table>
            </Row>
            <Row class="pageWrapper">
                <Page :total="total" :current="currentPageIdx" @on-change="changePage" show-elevator></Page>
            </Row>

        </Card>
    </div>
</template>

<script>
import { inviteSecondRecord } from '@/service/getData';
const getParamsFun = (pageSize) => (pageNumber) => (id) => ({
    pageNumber, pageSize, id
});
const getParams = getParamsFun(10);
export default {
    props: {
        id: Number
    },
    data() {
        return {
            list: [],
            total: 0,
            pageNumber: 1,
            currentPageIdx: 1,
            ifLoading: true,
            columns: [
                {
                    title: "会员名称",
                    key: "username"
                },
                {
                    title: '手机号',
                    key: 'mobilePhone'
                },
                {
                    title: '邮箱',
                    key: 'email'
                },
                {
                    title: '注册时间',
                    key:"registrationTime"
                }
            ]
        }
    },
    methods: {
        init(id) {
            const params = getParams(this.pageNumber)(id);
            inviteSecondRecord(params).then(res => {
                if (!res.code) {
                    this.ifLoading = false;
                    this.list = res.data.content;
                    this.total = res.data.totalElements;
                } else {
                    this.$Message.error(res.message);
                }
            })
        },
        changePage(index) {
            this.pageNumber = this.currentPageIdx = index;
            this.init(this.id);
        }
    },
    created() {
        console.log(this.id)
        this.init(this.id);
    }
}
</script>

<style lang="less" scoped>
</style>