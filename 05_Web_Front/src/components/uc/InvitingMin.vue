<template>
    <div class="nav-rights">
        <div class="nav-right col-xs-12 col-md-10 padding-right-clear">
            <div class="bill_box rightarea padding-right-clear record">
                <div class="col-xs-12 rightarea-con">
                    <div class="trade_accumulative">
                        <div class="trade_accumulative_return">{{$t('uc.finance.inviting.accumulative_return')}}&nbsp;&nbsp;{{accumulative_return}}</div>
                        <div class="trade_accumulat_return">{{$t('uc.finance.inviting.accumulat_return')}}&nbsp;&nbsp;{{accumulat_return}}</div>
                    </div>
                    <div class="form-group">
                        <span>
                            {{$t('uc.finance.inviting.start_end')}} ：
                        </span>
                        <DatePicker v-model="rangeDate" type="daterange" style="width: 200px;margin-right: 35px;"></DatePicker>
                        <!--<DatePicker v-model="startDate" type="date"></DatePicker>-->
                        <!--<span>-->
                        <!--{{$t('uc.finance.record.to')}}-->
                        <!--</span>-->
                        <!--<DatePicker v-model="endDate" type="date"></DatePicker>-->
                        <!-- <span>
                        {{$t('uc.finance.inviting.refere')}} ：
                        </span> -->
                        <!-- <Input v-model="value" :placeholder="$t('uc.finance.inviting.refereinput')" style="width: 200px"></Input> -->
                        <Button type="primary" @click="queryOrder" style="padding: 6px 50px;margin-left:10px;background-color:#f0a70a;border-color:#f0a70a">{{$t('uc.finance.record.search')}}</Button>
                    </div>
                    <div class="datedaitl">
                        <span style="color: #eb6f6c">{{$t('uc.finance.inviting.start_end')}} ：</span>&nbsp;&nbsp;<span>{{$t('uc.finance.inviting.chargetime')}}</span>
                    </div>
                    <div class="order-table">
                        <Table :no-data-text="$t('common.nodata')" :columns="tableColumnsRecord" :data="tableRecord"></Table>
                        <div style="margin: 10px;overflow: hidden" >
                            <div style="float: right;">
                                <Page :total="total" :pageSize="pageSize" show-total :current="page+1" @on-change="changePage" id="record_pages"></Page>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>
<script>
    export default {
        components: {
        },
        data() {
            let that = this;
            return {
                startTime:"",
                endTime:"",
                ordKeyword: '',
                rangeDate:'',
                startDate:'',
                endDate:'',
                recordValue: '',
                tableColumnsRecord:[
                    {//交易时间
                        title: that.$t('uc.finance.trade.transactionTime'),
                        key: 'transactionTime',
                        // width:150
                    },
                    {//被推荐人姓名
                        title: that.$t('uc.finance.inviting.refereename'),
                        key: 'inviterName',
                    },
                    {//被推荐人手机号
                        title: that.$t('uc.finance.inviting.referetel'),
                        key: 'inviterMobile',
                    },
                    {
                        title: that.$t('uc.finance.inviting.invitingawards'),
                        key:"mineAmount"
                    },
                    /*{//订单ID
                        title: this.$t('uc.finance.trade.exchangeOrderId'),
                        key: 'exchangeOrderId',
                    },
                    {//交易对
                        title: this.$t('uc.finance.trade.symbol'),
                        key: 'amount',
                    },
                    {//方向
                        title: this.$t('uc.finance.trade.direction'),
                        render:(h,params)=>{
                            let type = params.row.type;
                            let direction = params.row.direction;
                            let str1 = "", str2 = "";
                            type == 1 && (str1 = "市价");
                            type == 0 && (str1 = "现价");
                            direction == 1 && (str2="卖出");
                            direction == 0 && (str2="买入");
                            return h('div',str1+str2,'');
                        }
                    },*/
                    {//到账时间
                        title: that.$t('uc.finance.trade.account_date'),
                        key: 'transactionTime',
                        width:160,
                        render:function(h,params){
                            var reg = /-/g;
                            var a = params.row.transactionTime
                            var b = a.split(" ")[0].replace(reg,"/")
                            var c = a.split(" ")[1];
                            var date = new Date(b).getTime()+86400000;
                            date = that.dateform(date);
                            var str = date.split(" ")[0] + " "+c;
                            return h('div', str,'');
                        }
                    },
                ],
                accumulative_return:'1000',
                accumulat_return:'1000',
                pageSize:10,
                value:'',
                page:0,
                total:0,
                tableRecord: [],
            }
        },
        created:function(){
            this.init(1);
        },
        methods: {
            init(page){
                 let memberId = 0;
                !this.$store.getters.isLogin && this.$router.push('/login');
                this.$store.getters.isLogin && (memberId = this.$store.getters.member.id); 
                let startTime = "";
                let endTime = "";
                let url = this.api.uc.mylist;
                this.startTime && (startTime = this.startTime);
                this.endTime && (endTime = this.endTime);
                let params = {
                    memberId:memberId,
                    page:page,
                    limit:10,
                    inviterState:1,
                    startTime,
                    endTime
                };
                this.$http.post(this.host+url,params).then(res=>{
                    if(res.body.code == 0){
                        let data = res.body.data;
                        this.accumulative_return = data.backAmount;
                        this.accumulat_return = data.preAmount;
                        this.total = data.exchangeOrders.totalElements;
                        this.tableRecord = data.exchangeOrders.content;
                    }else{
                        this.$Message.error(res.body.message);
                    }
                });
            },
            dateform(time){
                var date = new Date(time);
                var y = date.getFullYear();  
                var m = date.getMonth() + 1;  
                m = m < 10 ? ('0' + m) : m;  
                var d = date.getDate();  
                d = d < 10 ? ('0' + d) : d;  
                var h = date.getHours();
                h = h < 10 ? ('0' + h) : h;
                var minute = date.getMinutes();
                var second = date.getSeconds();
                minute = minute < 10 ? ('0' + minute) : minute;  
                second = second < 10 ? ('0' + second) : second; 
                return y + '-' + m + '-' + d+' '+h+':'+minute+':'+second; 
            },
            changePage(pageindex) {
                this.init(pageindex);
            },
            queryOrder(){
                let rangedate = "";
                if (this.rangeDate.length == 0) {
                    this.$Message.error("请选择搜索日期范围");
                    return;
                }else {
                    try {
                        rangedate += this.rangeDate[0].getFullYear() + "-" + (this.rangeDate[0].getMonth() + 1) + "-" + this.rangeDate[0].getDate();
                        rangedate += "~";
                        rangedate += this.rangeDate[1].getFullYear() + "-" + (this.rangeDate[1].getMonth() + 1) + "-" + this.rangeDate[1].getDate();
                    }catch(ex) {
                        this.$Message.error("请选择搜索日期范围");
                        return;
                    }
                }

                let date = [];
                rangedate && (date = rangedate.split("~"));
                let memberId = 0;
                !this.$store.getters.isLogin && this.$router.push('/login');
                this.$store.getters.isLogin && (memberId = this.$store.getters.member.id); 
                let url = this.api.uc.mylist;
                let startTime = new Date(date[0]).getTime(),
                    endTime = new Date(date[1]).getTime(),
                    page = 1,
                    limit = 10,
                    inviterState = 1,
                    params = {startTime,endTime,memberId,page,limit,inviterState};
                this.startTime = startTime;
                this.endTime = endTime;
                this.$http.post(this.host + url, params).then(res => {
                    if(res.body.code == 0){
                        let data = res.body.data;
                        this.total = data.exchangeOrders.totalElements;
                        this.tableRecord = data.exchangeOrders.content;
                    }else{
                        this.$Message.error(res.body.message);
                    }
                });
               
            },
            getList(pageNo) {
                //获取tableWithdraw
                let params = {}
                params['pageNo'] = pageNo;
                params['pageSize'] = this.pageSize;
                this.$http.post(this.host + '/uc/asset/transaction/all', params).then(response => {
                    var resp = response.body;
                    if (resp.content) {
                        this.tableRecord = resp.content
                        this.total = resp.totalElements
                        this.loading = false
                        this.page = resp.number;
                    } else {
                        this.$Message.error(resp.message);
                    }
                })
            },
            updateLangData(){
                this.tableColumnsRecord[0].title = this.$t("uc.finance.trade.transactionTime");
                this.tableColumnsRecord[1].title = this.$t("uc.finance.inviting.refereename");
                this.tableColumnsRecord[2].title = this.$t("uc.finance.inviting.referetel");
                this.tableColumnsRecord[3].title = this.$t("uc.finance.inviting.invitingawards");
                this.tableColumnsRecord[4].title = this.$t("uc.finance.trade.account_date");
            }
        },
        computed: {
            'lang': function () {
                return this.$store.state.lang;
                }
        },
         watch:{
            'lang':function () {
                this.updateLangData();
            }
        }
    }
</script>
<style scoped>
    .datedaitl{
        text-align: left;
        margin-bottom: 10px;
    }
    .bill_box {
        width: 100%;
        height: auto;
        overflow: hidden;
    }
    .form-group{
        margin-bottom: 20px;
        text-align: left;
    }
    .rightarea {
        background: #fff;
        padding-left: 15px !important;
        padding-right: 15px !important;
        margin-bottom: 60px !important;
    }

    .rightarea .rightarea-top {
        line-height: 75px;
        border-bottom: #f1f1f1 solid 1px;
    }

    .rightarea .rightarea-con {
        padding-top: 30px;
        padding-bottom: 20px;
    }

    .rightarea .trade-process {
        line-height: 30px;
        padding: 0 15px;
        background: #f1f1f1;
        display: inline-block;
        position: relative;
        margin-right: 20px;
    }

    .rightarea .trade-process.active {
        color: #eb6f6c;
        background: #f9f5eb;
    }

    .rightarea .trade-process .icon {
        background: #fff;
        border-radius: 20px;
        height: 20px;
        width: 20px;
        display: inline-block;
        line-height: 20px;
        text-align: center;
        margin-right: 10px;
    }

    .rightarea .trade-process .arrow {
        position: absolute;
        top: 10px;
        right: -5px;
        width: 0;
        height: 0;
        border-top: 5px solid transparent;
        border-bottom: 5px solid transparent;
        border-left: 5px solid #f1f1f1;
    }

    .rightarea .trade-process.active .arrow {
        border-left: 5px solid #f9f5eb;
    }

    .rightarea .rightarea-tabs {
        border: none;
    }

    .rightarea .rightarea-tabs li>a {
        width: 100%;
        height: 100%;
        padding: 0;
        margin-right: 0;
        font-size: 14px;
        color: #646464;
        border-radius: 0;
        border: none;
        display: flex;
        justify-content: center;
        align-items: center;
    }

    .rightarea .rightarea-tabs li>a:hover {
        background-color: #fcfbfb;
    }

    .rightarea .rightarea-tabs li {
        width: 125px;
        height: 40px;
        position: relative;
        margin: -1px 0 0 -1px;
        border: 1px solid #f1f1f1;
        display: flex;
        align-items: center;
        justify-content: center;
        cursor: pointer;
    }

    .rightarea .rightarea-tabs li.active {
        background-color: #fcfbfb;
    }

    .rightarea .rightarea-tabs li:last-child {
        border-right: 1px solid #f1f1f1;
    }

    .rightarea .rightarea-tabs li.active>a,
    .rightarea .rightarea-tabs li:hover>a {
        color: #da2e22;
        border: none;
    }

    .rightarea .panel-tips {
        border: 3px solid #fdfaf3;
        color: #9e9e9e;
        font-size: 12px;
    }

    .rightarea .panel-tips .panel-header {
        background: #fdfaf3;
        line-height: 40px;
        margin-bottom: 15px;
    }

    .rightarea .panel-tips .panel-title {
        font-size: 16px;
    }

    .rightarea .recordtitle {
        cursor: pointer;
    }

    .nav-right {
        height: auto;
        overflow: hidden;
        padding: 0 15px;
    }

    .order_box {
        width: 100%;
        background: #fff;
        height: 56px;
        line-height: 56px;
        margin-bottom: 20px;
        border-bottom: 2px solid #ccf2ff;
        position: relative;
        text-align: left;
    }

    .order_box a {
        color: #8994A3;
        font-size: 16px;
        padding: 0 30px;
        cursor: pointer;
        text-decoration: none;
        text-align: center;
        line-height: 54px;
        display: inline-block;
    }

    .order_box .active {
        border-bottom: 2px solid #f0a70a;
    }

    .order_box .search {
        position: absolute;
        width: 300px;
        height: 32px;
        top: 12px;
        right: 0;
        display: flex;
        /* border: #c5cdd7 solid 1px; */
    }

    .order-table .ivu-table-body .ivu-table-cell{
        padding-right: 0 !important;
    }
    .trade_accumulative{
        height: auto;
        overflow: hidden;
        font-size: 18px;
        font-weight: 600;
        text-align: left;
        border-bottom: 1px solid #ccc;
        padding-bottom: 20px;
        margin-bottom: 20px;
    }
    .trade_accumulative .trade_accumulative_return{
        width: 50%;
        float: left;
    }
    .trade_accumulative .trade_accumulat_return{
        width: 50%;
        float: left;
    }
</style>
