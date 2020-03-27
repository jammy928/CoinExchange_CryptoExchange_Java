<template>
    <div class="nav-right notice">
        <div class="nav-right col-xs-12 col-md-10 padding-right-clear">
            <div class="bill_box rightarea padding-right-clear">
                <section class="trade-group merchant-top">
                    <i class="merchant-icon tips"></i>
                    <span class="tips-word">平台公告</span>
                </section>
                <section class="noticeBox">
                    <Table :columns="tableColumnsAdv" :data="tableData1" :show-header="sasa"></Table>
                    <div style="margin: 10px;overflow: hidden">
                        <div style="float: right;">
                            <Page :total="100" :current="1" @on-change="changePage"></Page>
                        </div>
                    </div>
                </section>
            </div>
        </div>
    </div>
</template>
<script>
export default {
    components: {
    },
    data() {
        let self = this;
        return {
            choseBtn: 0,
            sasa: false,
            tableData1: [],
            tableColumnsAdv: [
                {
                    title: ' ',
                    key: 'name',
                    render: function(h, params) {
                        return h('p', [
                            h('a', {
                                on: {
                                    click: function() {
                                        self.$router.push('/noticeDetail?id=123');
                                    }
                                }
                            }, params.row.name)
                        ]);
                    }
                },
                {
                    title: ' ',
                    align: 'right',
                    key: 'payNums',
                },

            ],
        }
    },
    methods: {
        mockTableData1(index) {
            let data = [];
            let page = 1 || index
            this.$http.post(this.host + '/otc/advertise/page', { 'pageNo': index }).then(response => {
                var resp = response.body;
                if (resp.code == 0) {
                    for (let i = 0; i < 10; i++) {
                        data.push({
                            name: resp.data.content[i].id,
                            payNums: '2018-01-11 15:36:07',

                        })
                    }
                } else if (resp.code == 4000) {
                    this.$Message.success('请先登录');
                    this.$router.push('/login');
                } else {
                    this.$Message.error(resp.message);
                }
            })

            return data;
        },
        changePage(index) {
            // The simulated data is changed directly here, and the actual usage scenario should fetch the data from the server
            this.tableData1 = this.mockTableData1(index);
        }


    },
    computed: {

    },
    created() {
        this.mockTableData1();
    }
}
</script>
<style>
.notice .ivu-table:after {
    width: 0;
}

.notice .ivu-table:before {
    height: 0;
    background: none !important;
}

.notice .noticeBox .ivu-table-wrapper {
    border: 0;
}

.notice .trade-group {
    height: 50px;
    display: -webkit-box;
    display: -ms-flexbox;
    display: flex;
    -webkit-box-align: center;
    -ms-flex-align: center;
    align-items: center;
    background: #fff;
    padding: 0 15px;
    color: #fff;
    margin-bottom: 20px;
    font-size: 14px;
}

.notice .merchant-icon.tips {
    display: inline-block;
    margin-left: 4px;
    width: 4px;
    height: 22px;
    margin-right: 10px;
    background: #f0a70a;
}

.notice .merchant-top .tips-word {
    -webkit-box-flex: 2;
    -ms-flex-positive: 2;
    flex-grow: 2;
    text-align: left;
}

.notice .bill_box {
    width: 100%;
    height: auto;
    overflow: hidden;
}

.notice .rightarea {
    background: #fff;
    padding-left: 15px !important;
    padding-right: 15px !important;
    margin-bottom: 60px !important;
    padding-top: 20px;
}

.notice .nav-right {
    /* width: 1000px; */
    height: auto;
    overflow: hidden;
    padding: 0 15px;
}

.notice .order_box {
    width: 100%;
    background: #fff;
    height: 56px;
    line-height: 56px;
    margin-bottom: 20px;
    border-bottom: 2px solid #ccf2ff;
    position: relative;
    text-align: left;
}

.notice .order_box a {
    color: #0B0D1B;
    font-size: 16px;
    padding: 0 30px;
    cursor: pointer;
    text-decoration: none;
    text-align: center;
    line-height: 54px;
    display: inline-block;
}

.notice .order_box .active {
    border-bottom: 2px solid #00b5f6;
}

.notice .order_box .search {
    position: absolute;
    width: 300px;
    height: 32px;
    top: 12px;
    right: 0;
    display: flex;
    /* border: #c5cdd7 solid 1px; */
}
</style>
