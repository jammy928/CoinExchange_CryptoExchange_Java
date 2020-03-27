<template>
    <div>
        <Card>
            <p slot="title">
                会员邀请列表
                <Button type="primary" size="small" @click="refreshPageManual">
                    <Icon type="refresh"></Icon>
                    刷新
                </Button>
            </p>
            <Row class="functionWrapper">
                <div class="searchWrapper">
                    <div class="poptip">
                        会员名称：<Input placeholder="会员名称" v-model="realName" />
                        </Input>
                    </div>
                    <div class="poptip">
                        手机号：<Input placeholder="手机号" v-model="mobilePhone" />
                        </Input>
                    </div>
                    <div class="poptip">
                        邮箱：<Input placeholder="邮箱" v-model="email" />
                        </Input>
                    </div>
                    <div class="btns">
                        <Button type="info" @click="search">搜索</Button>
                    </div>
                </div>
            </Row>
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
import { inviteRecord } from '@/service/getData';
import expandRow from './SecondRecord';
const getParamsFun = (pageSize) => (pageNo) => (realName) => (mobilePhone) => (email) => ({
    pageNo, pageSize, realName, mobilePhone, email
});
const getParams = getParamsFun(10);
export default {
    components: { expandRow },
    data() {
        return {
            list: [],
            total: 0,
            realName: "",
            pageNo: 1,
            mobilePhone: "",
            email: "",
            currentPageIdx: 1,
            ifLoading: true,
            columns: [
                {
                    type: 'expand',
                    width: 50,
                    render: (h, params) => {
                        return h(expandRow, {
                            props: {
                                id: params.row.id
                            }
                        })
                    }
                },
                {
                    title: "编号",
                    key: "id"
                },
                {
                    title: '用户名',
                    key: 'username'
                },
                {
                    title: '邀请码',
                    key: 'promotionCode'
                },
                {
                    title: '一级邀请',
                    render: (h, params) => {
                        return h("div", {}, params.row.firstLevel);
                    }
                },
                {
                    title: '二级邀请',
                    render: (h, params) => {
                        return h("div", {}, params.row.secondLevel);
                    }
                },
                {
                    title: '三级邀请',
                    render: (h, params) => {
                        return h("div", {}, params.row.thirdLevel);
                    }
                },
                {
                    title: "获得积分",
                    key: "generalizeTotal"
                }
            ]
        }
    },
    methods: {
        init() {
            const params = getParams(this.pageNo)(this.realName)(this.mobilePhone)(this.email);
            inviteRecord(params).then(res => {
                if (!res.code) {
                    this.ifLoading = false;
                    this.list = res.data.content;
                    this.total = res.data.totalElements;
                } else {
                    this.$Message.error(res.message);
                }
            })
        },
        refreshPageManual() {
            this.realName = "";
            this.currentPageIdx = 1;
            this.pageNo = 1;
            this.mobilePhone = "";
            this.email = "";
            this.init();
        },
        search() {
            this.pageNum = this.currentPageIdx = 1;
            this.init();
        },
        changePage(index) {
            this.pageNo = this.currentPageIdx = index;
            this.init();
        }
    },
    created() {
        this.init();
    }
}
</script>

<style lang="less" scoped>
</style>
